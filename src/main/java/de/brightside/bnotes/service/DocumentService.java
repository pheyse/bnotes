package de.brightside.bnotes.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.bright_side.brightmarkdown.BrightMarkdown;
import de.bright_side.brightmarkdown.BrightMarkdown.OutputType;
import de.brightside.bnotes.base.MainConstants;
import de.brightside.bnotes.base.ResponseConstants;
import de.brightside.bnotes.dao.ChapterRepo;
import de.brightside.bnotes.dao.DocumentAccessRepo;
import de.brightside.bnotes.dao.DocumentRepo;
import de.brightside.bnotes.dao.HistoryLogRepo;
import de.brightside.bnotes.dao.PictureRepo;
import de.brightside.bnotes.dao.UserRepo;
import de.brightside.bnotes.dto.ChapterDto;
import de.brightside.bnotes.dto.DocumentDto;
import de.brightside.bnotes.dto.ExportDataDto;
import de.brightside.bnotes.dto.IdAndName;
import de.brightside.bnotes.exceptions.WrongCredentialsException;
import de.brightside.bnotes.logic.DocumentLogic;
import de.brightside.bnotes.logic.ExportLogic;
import de.brightside.bnotes.logic.ExportLogic.ExportType;
import de.brightside.bnotes.model.Chapter;
import de.brightside.bnotes.model.Document;
import de.brightside.bnotes.model.DocumentAccess;
import de.brightside.bnotes.model.HistoryLog;
import de.brightside.bnotes.model.Picture;
import de.brightside.bnotes.model.PictureIdNameAndType;
import de.brightside.bnotes.model.PictureInfo;
import de.brightside.bnotes.model.Request;
import de.brightside.bnotes.model.Response;
import de.brightside.bnotes.util.RequestParamUtil;


@Transactional
public class DocumentService {
    Logger logger = LoggerFactory.getLogger(DocumentService.class);
	
	private static final long CHAPTER_ID_END_OF_DOCUMENT = -1;
	
    private ChapterRepo chapterRepo;
    private HistoryLogRepo historyLogRepo;
	private DocumentRepo documentsRepo;
	private UserRepo userRepo;
	private DocumentAccessRepo documentAccessRepo;
	private ExportLogic exportLogic = new ExportLogic();
	private PictureRepo pictureRepo;

	private EntityManager em;
	
	public DocumentService(EntityManager em, UserRepo userRepo, DocumentAccessRepo documentAccessRepo, DocumentRepo documentsRepo
			, ChapterRepo chapterRepo, HistoryLogRepo historyLogRepo, PictureRepo pictureRepo) {
		this.em = em;
		this.userRepo = userRepo;
		this.documentAccessRepo = documentAccessRepo;
		this.documentsRepo = documentsRepo;
		this.chapterRepo = chapterRepo;
		this.historyLogRepo = historyLogRepo;
		this.pictureRepo = pictureRepo;
	}

	private Chapter createNewChapterEntity(long documentId, int level, long orderSequence) {
		Chapter result = new Chapter();
		result.setDocumentId(documentId);
		result.setActive(1);
		result.setTitle("");
		result.setBody("");
		result.setLevel(level);
		result.setOrderSequence(orderSequence);
		return result;
	}
	
	private List<ChapterDto> toChapterDtoList(String jwt, List<Chapter> chapterEntityList) {
		return toChapterDtoList(jwt, chapterEntityList, false);
	}
	
	private List<ChapterDto> toChapterDtoList(String jwt, List<Chapter> chapterEntityList, boolean exportImageFormat) {
		List<ChapterDto> result = new ArrayList<ChapterDto>();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		DocumentLogic logic = new DocumentLogic();
		
		for (Chapter entity: chapterEntityList) {
			BrightMarkdown brightMarkdown = new BrightMarkdown();
			int level = entity.getLevel();
			logic.nextIndex(indexPerChapter, level);
			
			ChapterDto dto = new ChapterDto();
			dto.setChapterId(entity.getChapterId());
			dto.setLevel(entity.getLevel());
			dto.setTitle(entity.getTitle());
			String markdownCode = null;
			String bodyRaw = entity.getBody();
			Collection<PictureIdNameAndType> idsAndNames = pictureRepo.findByChapterId(entity.getChapterId());
			if (bodyRaw == null) {
				bodyRaw = "";
			}
			List<IdAndName> images = new ArrayList<IdAndName>();
			for (PictureIdNameAndType i: idsAndNames) {
				images.add(new IdAndName(i.getPicId(), i.getName()));
				
				String ending = "";
				if (exportImageFormat) {
					ending = "." + i.getType();
				} else {
					ending = "/" + jwt;
				}
				
				brightMarkdown.addImageNameToPathMapping(i.getName(), "image/" + i.getPicId() + ending);
			}
			dto.setImages(images);
			bodyRaw = replaceNewLineSymbol(bodyRaw);
			try {
				markdownCode = brightMarkdown.createHTML(bodyRaw, OutputType.EMBEDDABLE_HTML_CODE);
			} catch (Exception e) {
				logger.error("toChapterDtoList. Creating markdown failed from code >>" + bodyRaw + "<<", e);
				markdownCode = "Error in markdown processing: " + e + "<br><br><pre>" + bodyRaw + "</pre>";
			}
			dto.setBodyHtml(markdownCode);
			dto.setBodyRaw(bodyRaw);
			dto.setIndexLabel(logic.getIndexLabel(indexPerChapter, level));
			
			result.add(dto);
		}
		return result;
	}
	
	private List<DocumentDto> toDocumentDtoList(List<Document> documentEntityList) {
		List<DocumentDto> result = new ArrayList<DocumentDto>();
		for (Document entity: documentEntityList) {
			DocumentDto dto = new DocumentDto();
			dto.setDocumentId(entity.getDocumentId());
			dto.setTitle(entity.getTitle());
			result.add(dto);
		}
		return result;
	}
	
	private Response createChapterDisplayResponse(String userName, String jwt, long documentId, String message) {
		return createChapterDisplayResponse(userName, jwt, documentId, message, null, null);
	}

	public Response createChapterDisplayResponse(String userName, String jwt, long documentId, String message, Long chapterIdToEdit, Integer chapterIdToEditLevel) {
		Set<Long> userDocuments = documentAccessRepo.getUserDocuments(userName);

		List<ChapterDto> chapterDtoList;
		String title = "";
		if (userDocuments.contains(documentId)) {
			Document document = documentsRepo.findById(documentId).get();
			List<Chapter>  chapterEntityList = chapterRepo.getDocumentChapters(documentId);
			chapterDtoList = toChapterDtoList(jwt, chapterEntityList);
			title = document.getTitle();
		} else {
			chapterDtoList = new ArrayList<ChapterDto>();
		}

		Response response = new Response(message, ResponseConstants.RESPONSE_CODE_OK);
		response.setChapters(chapterDtoList);
		response.setChapterToEdit(chapterIdToEdit);
		response.setChapterToEditLevel(chapterIdToEditLevel);
		response.setDocumentTitle(title);
		
		return response;
	}

	private Response createDocumentListResponse(String userName, String message, Long selectedDocumentId) {
		List<Document> possibleDocumentEntities = documentsRepo.getActiveDocumentsByName();
		
		Set<Long> userDocuments = documentAccessRepo.getUserDocuments(userName);
		List<Document> documentEntities = new ArrayList<Document>();
		for (Document i: possibleDocumentEntities) {
			if (userDocuments.contains(i.getDocumentId())) {
				documentEntities.add(i);
			}
		}
		
		List<DocumentDto> documentDtoList = toDocumentDtoList(documentEntities);
		
		Response response = new Response(message, ResponseConstants.RESPONSE_CODE_OK);
		response.setDocuments(documentDtoList);
		response.setSelectedDocumentId(selectedDocumentId);
		
		return response;
	}
	
	public Response getChapters(String username, String jwt, Request request) throws Exception{
		long documentId = RequestParamUtil.getDocumentId(request);
		return createChapterDisplayResponse(username, jwt, documentId, "Response for: getChapters");
	}
	
	public Response saveChapter(String username, String jwt, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		if (!hasDocumentAccess(username, documentId)) {
			throw new WrongCredentialsException();
		}

		long chapterId = RequestParamUtil.getChapterId(request);
		boolean finishEditing = RequestParamUtil.getFinishEditing(request);
		int level = RequestParamUtil.getLevel(request);
		String title = RequestParamUtil.getTitle(request);
		String body = RequestParamUtil.getBody(request);
		
		Chapter entity = chapterRepo.findById(chapterId).get();
		writeChapterToHistory(entity, "update", username);

		entity.setLevel(level);
		entity.setTitle(title);
		entity.setBody(body);
		chapterRepo.save(entity);

		Response response = createChapterDisplayResponse(username, jwt, documentId, "Response for: saveChapter");
		response.setFinishEditing(finishEditing);
		response.setEditChapterMessage("Saved. (Server time at save: " + new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()) + ")");
		return response;
	}
	
	private void writeToHistory(Object entity, String objectType, String action, String userName) throws Exception {
		HistoryLog historyLog = new HistoryLog();
		historyLog.setAction(action);
		historyLog.setObjectType(objectType);
		historyLog.setUserName(userName);
		historyLog.setObjectValue(new ObjectMapper().writeValueAsString(entity));
		historyLogRepo.save(historyLog);
	}

	private void writeChapterToHistory(Chapter entity, String action, String userName) throws Exception {
		writeToHistory(entity, "chapter", action, userName);
	}
	
	private void writeDocumentToHistory(Document entity, String action, String userName) throws Exception {
		writeToHistory(entity, "document", action, userName);
	}
	
	public Response deleteChapter(String username, String jwt, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		if (!hasDocumentAccess(username, documentId)) {
			throw new WrongCredentialsException();
		}
		
		long chapterId = RequestParamUtil.getChapterId(request);

		Chapter entity = chapterRepo.findById(chapterId).get();
		writeChapterToHistory(entity, "delete", username);
		entity.setActive(0);
		chapterRepo.save(entity);
		
		return createChapterDisplayResponse(username, jwt, documentId, "Response for: deleteChapter");
	}

	public Response addChapter(String username, String jwt, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		if (!hasDocumentAccess(username, documentId)) {
			throw new WrongCredentialsException();
		}

		long chapterId = RequestParamUtil.getChapterId(request);
		int level = RequestParamUtil.getLevel(request);

		if (chapterId == CHAPTER_ID_END_OF_DOCUMENT) {
			logger.debug("addChapter: end of document");
			long maxSequence = getMaxOrderSequence(documentId);
			Chapter entity = createNewChapterEntity(documentId, level, maxSequence + 1);
			chapterRepo.save(entity);
			return createChapterDisplayResponse(username, jwt, documentId, "Response for: addChapter", entity.getChapterId(), entity.getLevel());
		}

		Chapter chapterEntity = chapterRepo.findById(chapterId).get();
		long newEntityOrderSequence = chapterEntity.getOrderSequence() + 1;
		chapterRepo.incrementOrderSequence(documentId, newEntityOrderSequence);
		
		Chapter entity = createNewChapterEntity(documentId, level, newEntityOrderSequence);
		chapterRepo.save(entity);
		return createChapterDisplayResponse(username, jwt, documentId, "Response for: addChapter", entity.getChapterId(), entity.getLevel());
	}

	private long getMaxOrderSequence(long documentId) {
		Long maxSequence = chapterRepo.maxOrderSequence(documentId);
		if (maxSequence == null) {
			return 0;
		}
		return maxSequence;
	}

	public Response editChapter(String username, String jwt, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		long chapterId = RequestParamUtil.getChapterId(request);
		if (!hasDocumentAccess(username, documentId)) {
			throw new WrongCredentialsException();
		}

		Chapter chapter = chapterRepo.findById(chapterId).get();
		Response response = new Response("editing chapter", ResponseConstants.RESPONSE_CODE_OK);

		response.setChapterToEdit(chapterId);
		response.setChapterToEditTitle(chapter.getTitle());
		response.setChapterToEditBody(replaceNewLineSymbol(chapter.getBody()));
		response.setChapterToEditLevel(chapter.getLevel());
		
		return response;
	}
	
	private String replaceNewLineSymbol(String text) {
		if (text == null) {
			return null;
		}
		return text.replace("{nl}", "\n");
	}
	
	public Response moveChapter(String username, String jwt, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		if (!hasDocumentAccess(username, documentId)) {
			throw new WrongCredentialsException();
		}
		
		long chapterId = RequestParamUtil.getChapterId(request);
		int direction = RequestParamUtil.getDirection(request);
		Chapter chapterEntity = chapterRepo.findById(chapterId).get();
		int level = chapterEntity.getLevel();
		
		List<Chapter> chapterEntities = chapterRepo.findAllActiveOfDocument(documentId);
		DocumentLogic logic = new DocumentLogic();
		Long idToSwitchWith = null;
		if (direction > 0) {
			idToSwitchWith = logic.getIdOfNextChapter(chapterEntities, chapterId, level);
		} else {
			idToSwitchWith = logic.getIdOfPrevChapter(chapterEntities, chapterId, level);
		}
		logger.debug("moveChapter: direction = " + direction + ", chapterId = " + chapterId + ", idToSwitchWith = " + idToSwitchWith);
		
		if (idToSwitchWith != null) {
			Chapter chapterEntityToSwitch = chapterRepo.findById(idToSwitchWith.longValue()).get();
			long orderSequence = chapterEntity.getOrderSequence();
			chapterEntity.setOrderSequence(chapterEntityToSwitch.getOrderSequence());
			chapterEntityToSwitch.setOrderSequence(orderSequence);
			chapterRepo.save(chapterEntity);
			chapterRepo.save(chapterEntityToSwitch);
			
			logger.debug("moveChapter: chapterEntity =         " + chapterEntity);
			logger.debug("moveChapter: chapterEntityToSwitch = " + chapterEntityToSwitch);
		}
		
		return createChapterDisplayResponse(username, jwt, documentId, "Response for: moveChapter");
	}

	public Response cloneChapter(String username, String jwt, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		if (!hasDocumentAccess(username, documentId)) {
			throw new WrongCredentialsException();
		}
		
		long chapterId = RequestParamUtil.getChapterId(request);
		
		Chapter chapter = chapterRepo.findById(chapterId).get();
		em.detach(chapter);
		chapter.setChapterId(0);
		chapter.setTitle("CLONE: " + chapter.getTitle());
		chapter = chapterRepo.save(chapter);

		Collection<PictureIdNameAndType> pictureInfos = pictureRepo.findByChapterId(chapterId);
		for (PictureIdNameAndType i: pictureInfos) {
			Picture picture = pictureRepo.findById(i.getPicId()).get();
			picture.setChapterId(chapter.getChapterId());
			em.detach(picture);
			picture.setPicId(0);
			pictureRepo.save(picture);
		}

		long newEntityOrderSequence = chapter.getOrderSequence() + 1;
		chapterRepo.incrementOrderSequence(documentId, newEntityOrderSequence);

		return createChapterDisplayResponse(username, jwt, documentId, "clonedChapter");
	}
	
	public Response moveChapterToOtherDocument(String username, String jwt, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		if (!hasDocumentAccess(username, documentId)) {
			throw new WrongCredentialsException();
		}
		long destDocumentId = RequestParamUtil.getDestDocumentId(request);
		if (!hasDocumentAccess(username, destDocumentId)) {
			throw new WrongCredentialsException();
		}
		
		long chapterId = RequestParamUtil.getChapterId(request);
		
		Document destDocument = documentsRepo.findById(destDocumentId).get();
		
		Chapter chapter = chapterRepo.findById(chapterId).get();
		chapter.setDocumentId(destDocumentId);
		long maxSequence = getMaxOrderSequence(destDocumentId);
		chapter.setOrderSequence(maxSequence + 1);
		chapterRepo.save(chapter);
		
		writeToHistory(chapter, "Chapter", "Moved to different document", username);

		Response response = createChapterDisplayResponse(username, jwt, destDocumentId, "moved chapter to different document");
		response.setAlertMessage("Moved chapter to document '" + destDocument.getTitle() + "'");
		response.setSelectedDocumentId(destDocumentId);
		
		
		return response;
	}
	
	public Response getDocuments(String username, Request request) throws Exception{
		return createDocumentListResponse(username, "Response for: getDocuments", null);
	}

	public Response chooseDocumentToMoveChapter(String username, Request request) throws Exception{
		Set<Long> userDocuments = documentAccessRepo.getUserDocuments(username);
		List<Document> possibleDocumentEntities = documentsRepo.getActiveDocumentsByName();
		long currentDocumentId = RequestParamUtil.getDocumentId(request);
		
		List<Document> documentEntities = new ArrayList<Document>();
		for (Document i: possibleDocumentEntities) {
			if ((userDocuments.contains(i.getDocumentId())) && (i.getDocumentId() != currentDocumentId)) {
				documentEntities.add(i);
			}
		}
		
		List<DocumentDto> documentDtoList = toDocumentDtoList(documentEntities);
		Response response = new Response("", ResponseConstants.RESPONSE_CODE_OK);
		response.setPossibleDocumentsToMoveTo(documentDtoList);
		return response;
	}
	
	public Response addDocument(String username, Request request) throws Exception {
		String title = RequestParamUtil.getTitle(request);

		Document document = new Document();
		document.setTitle(title);
		document.setActive(1);
		logger.debug("addDocument: document before save = " + document);

		Document savedDocument = documentsRepo.save(document);
		logger.debug("addDocument: document = " + document + ", savedDocument = " + savedDocument);
		
		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setDocumentId(document.getDocumentId());
		documentAccess.setUserName(username);
		documentAccessRepo.save(documentAccess);
		
		Response response = createDocumentListResponse(username, "Document created", document.getDocumentId());
		response.setChapters(new ArrayList<ChapterDto>());
		response.setDocumentTitle(document.getTitle());
		return response;
	}

	public Response editDocument(String username, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		if (!hasDocumentAccess(username, documentId)) {
			throw new WrongCredentialsException();
		}

		String title = RequestParamUtil.getTitle(request);
		Document document = documentsRepo.findById(documentId).get();
		writeDocumentToHistory(document, "rename", username);
		
		document.setTitle(title);
		documentsRepo.save(document);
		
		Response response = createDocumentListResponse(username, "Document renamed", documentId);
		response.setDocumentTitle(document.getTitle());
		return response;
	}

	public Response deleteDocument(String username, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		if (!hasDocumentAccess(username, documentId)) {
			throw new WrongCredentialsException();
		}
		
		Document document = documentsRepo.findById(documentId).get();
		document.setActive(0);
		documentsRepo.save(document);
		
		writeDocumentToHistory(document, "delete", username);
		
		Response response = createDocumentListResponse(username, "Document deleted", -1L);
		response.setDocumentTitle(document.getTitle());
		response.setChapters(new ArrayList<ChapterDto>());
		return response;
	}

	public boolean hasDocumentAccess(String userName, long documentId) {
		return documentAccessRepo.countAccess(userName, documentId) > 0;
	}

	public Response createWrongCredentialsResponse() {
		Response response = new Response("Wrong username or password", ResponseConstants.RESPONSE_CODE_WRONG_CREDENTIALS);
		return response;
	}

	public List<String> listExportItems() throws Exception, WrongCredentialsException {
    	List<String> result = new ArrayList<String>();
    	result.add(MainConstants.EXPORT_ITEM_ENTITIES);
    	
    	List<Document> documents = documentsRepo.findAll();
    	for (Document i: documents) {
    		result.add(exportLogic.getEncodedFilePath(i));
    	}

    	Collection<PictureInfo> pictureInfos = pictureRepo.getAllIdsNamesAndTypes();
    	for (PictureInfo i: pictureInfos) {
    		result.add(exportLogic.getEncodedFilePath(i));
    	}
    	
		return result;
	}

	public byte[] exportItem(String itemPath) throws Exception, WrongCredentialsException {
		ExportType exportType = exportLogic.getExportTypeFromEncodedFilePath(itemPath);

		switch (exportType) {
		case ENTITIES:
			return getExportEntitiesData();
		case HTML_FILE:
			return getExportHtmlData(itemPath);
		case IMAGE_FILE:
			return getImageData(itemPath);
		default:
			throw new Exception("Unknown type" + exportType);
		}
	}

	private byte[] getExportEntitiesData() throws Exception {
		ExportDataDto result = new ExportDataDto();
		result.setDocuments(documentsRepo.findAll());
		result.setChapters(chapterRepo.findAll());
		result.setDocumentAccess(documentAccessRepo.findAll());
		
		String resultString = new ObjectMapper().writeValueAsString(result);
		return new ExportLogic().toBytes(resultString);
	}
	
	private byte[] getExportHtmlData(String itemPath) throws Exception {
		long documentId = exportLogic.getDocumentIdFromEncodedHtmlFilePath(itemPath);		
		List<Chapter> chapterEntityList = chapterRepo.getDocumentChapters(documentId);
		Document document = documentsRepo.findById(documentId).get();
		String resultString = new HtmlExportService().createHtml(document, chapterEntityList, toChapterDtoList(null, chapterEntityList, true));
		return new ExportLogic().toBytes(resultString);
	}
	
	private byte[] getImageData(String itemPath) throws Exception {
		long pictureId = exportLogic.getPictureIdFromEncodedHtmlFilePath(itemPath);
		Picture picture = pictureRepo.findById(pictureId).get();
		return picture.getData();
	}

	public Response getPossibleUsersToGrantAccess(String username, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		if (!hasDocumentAccess(username, documentId)) {
			throw new WrongCredentialsException();
		}

		Set<String> result = getPossibleUsersNamesToGrantAccess(documentId);
		Response response = new Response("Possible users", ResponseConstants.RESPONSE_CODE_OK);
		response.setUserChoices(new ArrayList<String>(result));
		return response;
	}

	private Set<String> getPossibleUsersNamesToGrantAccess(long documentId) {
		Set<String> allUsers = userRepo.getAllUserNames();
		Set<String> usersWithAccess = documentAccessRepo.getDocumentUsers(documentId);
		Set<String> result = new TreeSet<String>(allUsers);
		result.removeAll(usersWithAccess);
		return result;
	}

	public Response grantAccess(String username, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		if (!hasDocumentAccess(username, documentId)) {
			throw new WrongCredentialsException();
		}
		String grantUserName = RequestParamUtil.getGrantUserName(request);

		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setDocumentId(documentId);
		documentAccess.setUserName(grantUserName);
		documentAccessRepo.save(documentAccess);

		Set<String> result = getPossibleUsersNamesToGrantAccess(documentId);
		Response response = new Response("Possible users", ResponseConstants.RESPONSE_CODE_OK);
		response.setUserChoices(new ArrayList<String>(result));
		return response;
	}

}
