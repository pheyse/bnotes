package de.brightside.bnotes.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import de.bright_side.brightmarkdown.BrightMarkdown;
import de.brightside.bnotes.base.MainConstants;
import de.brightside.bnotes.base.RequestActionConstants;
import de.brightside.bnotes.base.ResponseConstants;
import de.brightside.bnotes.dao.ChapterRepo;
import de.brightside.bnotes.dao.DocumentAccessRepo;
import de.brightside.bnotes.dao.DocumentRepo;
import de.brightside.bnotes.dao.HistoryLogRepo;
import de.brightside.bnotes.dao.PictureRepo;
import de.brightside.bnotes.dao.UserRepo;
import de.brightside.bnotes.exceptions.InternalErrorException;
import de.brightside.bnotes.exceptions.WrongCredentialsException;
import de.brightside.bnotes.model.Picture;
import de.brightside.bnotes.model.Request;
import de.brightside.bnotes.model.Response;
import de.brightside.bnotes.model.User;
import de.brightside.bnotes.security.JwtProvider;
import de.brightside.bnotes.security.JwtTokenFilter;
import de.brightside.bnotes.security.UserDetailsImplService;
import de.brightside.bnotes.service.DocumentService;
import de.brightside.bnotes.service.PictureService;
import de.brightside.bnotes.util.BnotesUtil;

@Controller
public class MainController{
    Logger logger = LoggerFactory.getLogger(MainController.class);
	
    private ChapterRepo chapterRepo;
    private DocumentRepo documentsRepo;
    private HistoryLogRepo historyLogRepo;
    private UserRepo userRepo;
    private DocumentAccessRepo documentAccessRepo;
    private PictureRepo pictureRepo;
	private JwtProvider jwtProvider;
	private UserDetailsImplService userDetailsService;

    @PersistenceContext
    private EntityManager em;
	
    @Autowired
	public MainController(ChapterRepo chapterRepo, DocumentRepo documentsRepo, HistoryLogRepo historyLogRepo,
			UserRepo userRepo, DocumentAccessRepo documentAccessRepo, PictureRepo pictureRepo, JwtProvider jwtProvider, UserDetailsImplService userDetailsService) {
		super();
		this.chapterRepo = chapterRepo;
		this.documentsRepo = documentsRepo;
		this.historyLogRepo = historyLogRepo;
		this.userRepo = userRepo;
		this.documentAccessRepo = documentAccessRepo;
		this.pictureRepo = pictureRepo;
		this.jwtProvider = jwtProvider;
		this.userDetailsService = userDetailsService;
	}

	@RequestMapping("/")
    public String main(){
    	return "main.jsp";
    }
    
    private DocumentService createDocumentService() {
    	return new DocumentService(em, userRepo, documentAccessRepo, documentsRepo, chapterRepo, historyLogRepo, pictureRepo);
    }
    
	private PictureService createPictureService() {
		DocumentService documentService = createDocumentService();
		return new PictureService(documentService, pictureRepo, historyLogRepo);
	}
	
    @GetMapping("/BrightMarkdownHelp")
    @PreAuthorize("hasRole('ROLE_EDIT')")
    @ResponseBody
    public String createBrightMarkdownHelpPage() {
    	try {
    		return new BrightMarkdown().getDocumentationAsHTML();
    	} catch (Exception e) {
    		logger.error("error", e);
    		throw new InternalErrorException();
    	}
    }

    @PostMapping("/listExportItems")
    @PreAuthorize("hasRole('ROLE_EXPORT')")
    @ResponseBody
	public List<String> listExportItems() {
    	try {
    		List<String> result = createDocumentService().listExportItems();
    		return result;
    	} catch (WrongCredentialsException e) {
    		throw e;
    	} catch (Exception e) {
    		logger.error("error", e);
    		throw new InternalErrorException();
    	}
    }
    
    @PostMapping("/exportItem")
    @PreAuthorize("hasRole('ROLE_EXPORT')")
    @ResponseBody
	public byte[] exportItem(@RequestParam String item) {
    	try {
    		byte[] result = createDocumentService().exportItem(item);
    		return result;
    	} catch (WrongCredentialsException e) {
    		throw e;
    	} catch (Exception e) {
    		logger.error("Could not export item with name '" + item + "'", e);
    		throw new InternalErrorException();
    	}
    }

    private String mapContentType(String type) {
    	if (type == null) {
    		return MediaType.IMAGE_JPEG_VALUE; 
    	}
    	switch (type) {
		case MainConstants.PICTURE_TYPE_GIF:
			return MediaType.IMAGE_JPEG_VALUE;
		case MainConstants.PICTURE_TYPE_PNG:
			return MediaType.IMAGE_JPEG_VALUE;
		default:
			return MediaType.IMAGE_JPEG_VALUE;
		}
    }

    @GetMapping("/image/{imageId}/{jwt}")
    public void getImage(@PathVariable long imageId, @PathVariable String jwt, HttpServletResponse response) throws IOException {
    	logger.debug("called getImage: imageId = " + imageId + ", jwt = " + jwt);
    	
    	String useJwt = jwt;
    	if (useJwt.startsWith(JwtTokenFilter.BEARER)) {
    		useJwt = useJwt.substring(JwtTokenFilter.BEARER.length()).trim();
    	}
    	logger.debug("called getImage: useJWT = >>" + useJwt + "<<");
    	
    	if (!userDetailsService.loadUserByJwtToken(useJwt).isPresent()) {
    		response.setStatus(MainConstants.HTTP_STATUS_FORBIDDEN_403);
    		return;
    	}
    	

    	String username = jwtProvider.getUsername(useJwt);
    	logger.debug("called getImage: username = >>" + username + "<<");

    	User user = userRepo.findByUsername(username).get();
    	
    	if (!BnotesUtil.hasRole(user, MainConstants.USER_ROLE_EDIT)) {
    		response.setStatus(MainConstants.HTTP_STATUS_FORBIDDEN_403);
    		return;
    	}
    	
    	PictureService service = createPictureService();
    	Picture picture = service.getPicture(imageId);
        response.setContentType(mapContentType(picture.getType()));
    	IOUtils.copy(new ByteArrayInputStream(picture.getData()), response.getOutputStream());
    }
    

	@PostMapping("/uploadImage")
    @PreAuthorize("hasRole('ROLE_EDIT')")
	@ResponseBody
	public Response uploadImage(@RequestParam("file") MultipartFile file
			, @RequestParam("jwt") String jwt
			, @RequestParam("documentId") long documentId
			, @RequestParam("chapterId") long chapterId
			, @RequestParam("imageName") String imageName
			) throws IOException {
		
		String username = jwtProvider.getUsername(jwt);
		logger.debug("called upload image: file = " + file);
		logger.debug("called upload image: chapterId = " + chapterId);
		logger.debug("called upload image: name = '" + imageName + "'");
		PictureService service = createPictureService();
		
		Response response = service.addPicture(file, username, jwt, documentId, chapterId, imageName);
		logger.debug("response: " + response);
		return response;
	}

    @PostMapping("/request")
    @PreAuthorize("hasRole('ROLE_EDIT')")
    @ResponseBody
    public Response mainPageRequest(Principal principal, @RequestHeader (name="Authorization") String jwt, @RequestBody Request request) {
    	logger.debug("Request: principal = " + principal + "jwt = >>" + jwt + "<<, request body = " + request);
    	
    	if (principal != null) {
        	logger.debug("Request: username = '" + principal.getName() + "'");
    	}
    	
    	try {
    		String username = null;
    		username = principal.getName();
    		
    		Response response = null;
    		String action = "";
    		if (request.getAction() != null) {
    			action = request.getAction();
    		}
    		switch (action) {
			case RequestActionConstants.ADD_DOCUMENT:
				response = createDocumentService().addDocument(username, request);
				break;
			case RequestActionConstants.RENAME_DOCUMENT:
				response = createDocumentService().editDocument(username, request);
				break;
			case RequestActionConstants.DELETE_DOCUMENT:
				response = createDocumentService().deleteDocument(username, request);
				break;
			case RequestActionConstants.EDIT_CHAPTER:
				response = createDocumentService().editChapter(username, jwt, request);
				break;
			case RequestActionConstants.ADD_CHAPTER:
				response = createDocumentService().addChapter(username, jwt, request);
				break;
			case RequestActionConstants.SAVE_CHAPTER:
				response = createDocumentService().saveChapter(username, jwt, request);
				break;
			case RequestActionConstants.MOVE_CHAPTER:
				response = createDocumentService().moveChapter(username, jwt, request);
				break;
			case RequestActionConstants.CHOOSE_DOCUMENT_TO_MOVE_CHAPTER:
				response = createDocumentService().chooseDocumentToMoveChapter(username, request);
				break;
			case RequestActionConstants.MOVE_CHAPTER_TO_OTHER_DOCUMENT:
				response = createDocumentService().moveChapterToOtherDocument(username, jwt, request);
				break;
			case RequestActionConstants.CLONE_CHAPTER:
				response = createDocumentService().cloneChapter(username, jwt, request);
				break;
			case RequestActionConstants.DELETE_CHAPTER:
				response = createDocumentService().deleteChapter(username, jwt, request);
				break;
			case RequestActionConstants.DELETE_IMAGE:
				response = createPictureService().deletePicture(username, jwt, request);
				break;
			case RequestActionConstants.GET_DOCUMENTS:
				response = createDocumentService().getDocuments(username, request);
				break;
			case RequestActionConstants.GET_CHAPTERS:
			case RequestActionConstants.TEST_BUTTON_CLICKED:
				response = createDocumentService().getChapters(username, jwt, request);
				break;
			case RequestActionConstants.GET_POSSIBLE_USERS_TO_GRANT_ACCESS:
				response = createDocumentService().getPossibleUsersToGrantAccess(username, request);
				break;
			case RequestActionConstants.GRANT_ACCESS:
				response = createDocumentService().grantAccess(username, request);
				break;
			default:
				response = new Response("Unknown action: '" + action + "'", ResponseConstants.RESPONSE_CODE_ERROR);
				break;
			}
    		logger.debug("response: " + response);
    		return response;
    	} catch (Exception e) {
    		logger.error("error occured: " + e, e);
    		throw new InternalErrorException("Internal error");

    	}
    }
}