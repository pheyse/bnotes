package de.brightside.bnotes.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.brightside.bnotes.base.MainConstants;
import de.brightside.bnotes.base.ResponseConstants;
import de.brightside.bnotes.dao.ChapterRepo;
import de.brightside.bnotes.dao.DocumentAccessRepo;
import de.brightside.bnotes.dao.DocumentRepo;
import de.brightside.bnotes.dao.HistoryLogRepo;
import de.brightside.bnotes.dao.UserRepo;
import de.brightside.bnotes.dto.ChapterDto;
import de.brightside.bnotes.dto.DocumentDto;
import de.brightside.bnotes.dto.ExportDataDto;
import de.brightside.bnotes.logic.DocumentLogic;
import de.brightside.bnotes.model.Chapter;
import de.brightside.bnotes.model.Document;
import de.brightside.bnotes.model.DocumentAccess;
import de.brightside.bnotes.model.HistoryLog;
import de.brightside.bnotes.model.Request;
import de.brightside.bnotes.model.Response;
import de.brightside.bnotes.model.User;
import de.brightside.bnotes.util.RequestParamUtil;

public class DocumentService {
    Logger logger = LoggerFactory.getLogger(DocumentService.class);
	
	private static final long CHAPTER_ID_END_OF_DOCUMENT = -1;
	
    private ChapterRepo chapterRepo;
    private HistoryLogRepo historyLogRepo;
	private DocumentRepo documentsRepo;
	private UserRepo userRepo;
	private DocumentAccessRepo documentAccessRepo;
	
	public DocumentService(UserRepo userRepo, DocumentAccessRepo documentAccessRepo, DocumentRepo documentsRepo, ChapterRepo chapterRepo, HistoryLogRepo historyLogRepo) {
		this.userRepo = userRepo;
		this.documentAccessRepo = documentAccessRepo;
		this.documentsRepo = documentsRepo;
		this.chapterRepo = chapterRepo;
		this.historyLogRepo = historyLogRepo;
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
	
	private List<ChapterDto> toChapterDtoList(List<Chapter> chapterEntityList) {
		List<ChapterDto> result = new ArrayList<ChapterDto>();
		Map<Integer, Integer> indexPerChapter = new TreeMap<Integer, Integer>();
		DocumentLogic logic = new DocumentLogic();
		
		
		for (Chapter entity: chapterEntityList) {
			int level = entity.getLevel();
			logic.nextIndex(indexPerChapter, level);
			
			ChapterDto dto = new ChapterDto();
			dto.setChapterId(entity.getChapterId());
			dto.setLevel(entity.getLevel());
			dto.setTitle(entity.getTitle());
			dto.setBody(entity.getBody());
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
	
	private Response createChapterDisplayResponse(String userName, long documentId, String message) {
		return createChapterDisplayResponse(userName, documentId, message, null, null);
	}

	private Response createChapterDisplayResponse(String userName, long documentId, String message, Long chapterIdToEdit, Integer chapterIdToEditLevel) {
		Set<Long> userDocuments = documentAccessRepo.getUserDocuments(userName);

		List<ChapterDto> chapterDtoList;
		String title = "";
		if (userDocuments.contains(documentId)) {
			Document document = documentsRepo.findById(documentId).get();
			List<Chapter>  chapterEntityList = chapterRepo.getDocumentChapters(documentId);
			chapterDtoList = toChapterDtoList(chapterEntityList);
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
	
	public Response getChapters(Request request) throws Exception{
		if (!credentialsOk(request)) {
			return createWrongCredentialsResponse();
		}
		
		long documentId = RequestParamUtil.getDocumentId(request);
		String userName = RequestParamUtil.getUserName(request);
		return createChapterDisplayResponse(userName, documentId, "Response for: getChapters");
	}
	
	public Response saveChapter(Request request) throws Exception {
		if (!credentialsAndDocumentAccessOk(request)) {
			return createWrongCredentialsResponse(); 
		}

		String userName = RequestParamUtil.getUserName(request);
		long documentId = RequestParamUtil.getDocumentId(request);
		long chapterId = RequestParamUtil.getChapterId(request);
		int level = RequestParamUtil.getLevel(request);
		String title = RequestParamUtil.getTitle(request);
		String body = RequestParamUtil.getBody(request);
		
		Chapter entity = chapterRepo.findById(chapterId).get();
		writeChapterToHistory(entity, "update", request.getUserId());

		entity.setLevel(level);
		entity.setTitle(title);
		entity.setBody(body);
		chapterRepo.save(entity);

		return createChapterDisplayResponse(userName, documentId, "Response for: saveChapter");
	}
	
	private void writeToHistory(Object entity, String objectType, String action, long userId) throws Exception {
		HistoryLog historyLog = new HistoryLog();
		historyLog.setAction(action);
		historyLog.setObjectType(objectType);
		historyLog.setUserId(userId);
		historyLog.setObjectValue(new ObjectMapper().writeValueAsString(entity));
		historyLogRepo.save(historyLog);
	}

	private void writeChapterToHistory(Chapter entity, String action, long userId) throws Exception {
		writeToHistory(entity, "chapter", action, userId);
	}
	
	private void writeDocumentToHistory(Document entity, String action, long userId) throws Exception {
		writeToHistory(entity, "document", action, userId);
	}
	
	public Response deleteChapter(Request request) throws Exception {
		if (!credentialsAndDocumentAccessOk(request)) {
			return createWrongCredentialsResponse(); 
		}
		String userName = RequestParamUtil.getUserName(request);
		long documentId = RequestParamUtil.getDocumentId(request);
		long chapterId = RequestParamUtil.getChapterId(request);

		Chapter entity = chapterRepo.findById(chapterId).get();
		writeChapterToHistory(entity, "delete", request.getUserId());
		entity.setActive(0);
		chapterRepo.save(entity);
		
		return createChapterDisplayResponse(userName, documentId, "Response for: deleteChapter");
	}

	public Response addChapter(Request request) throws Exception {
		if (!credentialsAndDocumentAccessOk(request)) {
			return createWrongCredentialsResponse(); 
		}
		long documentId = RequestParamUtil.getDocumentId(request);
		String userName = RequestParamUtil.getUserName(request);
		long chapterId = RequestParamUtil.getChapterId(request);
		int level = RequestParamUtil.getLevel(request);

		if (chapterId == CHAPTER_ID_END_OF_DOCUMENT) {
			logger.debug("addChapter: end of document");
			Long maxSequence = chapterRepo.maxOrderSequence(documentId);
			if (maxSequence == null) {
				maxSequence = 0L;
			}
			Chapter entity = createNewChapterEntity(documentId, level, maxSequence + 1);
			chapterRepo.save(entity);
			return createChapterDisplayResponse(userName, documentId, "Response for: addChapter", entity.getChapterId(), entity.getLevel());
		}

		Chapter chapterEntity = chapterRepo.findById(chapterId).get();
		long newEntityOrderSequence = chapterEntity.getOrderSequence() + 1;
		chapterRepo.incrementOrderSequence(documentId, newEntityOrderSequence);
		
		Chapter entity = createNewChapterEntity(documentId, level, newEntityOrderSequence);
		chapterRepo.save(entity);
		return createChapterDisplayResponse(userName, documentId, "Response for: addChapter", entity.getChapterId(), entity.getLevel());
	}

	public Response moveChapter(Request request) throws Exception {
		if (!credentialsAndDocumentAccessOk(request)) {
			return createWrongCredentialsResponse(); 
		}

		String userName = RequestParamUtil.getUserName(request);
		long documentId = RequestParamUtil.getDocumentId(request);
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
		
		return createChapterDisplayResponse(userName, documentId, "Response for: moveChapter");
	}

	public Response getDocuments(Request request) throws Exception{
		if (!credentialsAndDocumentAccessOk(request)) {
			return createWrongCredentialsResponse(); 
		}
		String userName = RequestParamUtil.getUserName(request);
		return createDocumentListResponse(userName, "Response for: getDocuments", null);
	}

	public Response addDocument(Request request) throws Exception {
		if (!credentialsOk(request)) {
			return createWrongCredentialsResponse();
		}
		String userName = RequestParamUtil.getUserName(request);

		String title = RequestParamUtil.getTitle(request);

		Document document = new Document();
		document.setTitle(title);
		document.setActive(1);
		logger.debug("addDocument: document before save = " + document);

		Document savedDocument = documentsRepo.save(document);
		logger.debug("addDocument: document = " + document + ", savedDocument = " + savedDocument);
		
		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setDocumentId(document.getDocumentId());
		documentAccess.setUserName(userName);
		documentAccessRepo.save(documentAccess);
		
		Response response = createDocumentListResponse(userName, "Document created", document.getDocumentId());
		response.setChapters(new ArrayList<ChapterDto>());
		response.setDocumentTitle(document.getTitle());
		return response;
	}

	public Response editDocument(Request request) throws Exception {
		if (!credentialsAndDocumentAccessOk(request)) {
			return createWrongCredentialsResponse(); 
		}
		String userName = RequestParamUtil.getUserName(request);
		
		String title = RequestParamUtil.getTitle(request);
		long documentId = RequestParamUtil.getDocumentId(request);
		Document document = documentsRepo.findById(documentId).get();
		writeDocumentToHistory(document, "rename", request.getUserId());
		
		document.setTitle(title);
		documentsRepo.save(document);
		
		Response response = createDocumentListResponse(userName, "Document renamed", documentId);
		response.setDocumentTitle(document.getTitle());
		return response;
	}

	public Response deleteDocument(Request request) throws Exception {
		if (!credentialsAndDocumentAccessOk(request)) {
			return createWrongCredentialsResponse(); 
		}
		String userName = RequestParamUtil.getUserName(request);
		
		long documentId = RequestParamUtil.getDocumentId(request);
		
		Document document = documentsRepo.findById(documentId).get();
		document.setActive(0);
		documentsRepo.save(document);
		
		Response response = createDocumentListResponse(userName, "Document renamed", -1L);
		response.setDocumentTitle(document.getTitle());
		response.setChapters(new ArrayList<ChapterDto>());
		return response;
	}

	public Response login(Request request) throws Exception{
		String userName = RequestParamUtil.getUserName(request);
		String password = RequestParamUtil.getPassword(request);

		User user = userRepo.findByUserName(userName);
		if ((user != null) && (user.getPassword().equals(password))){
			Response response = createDocumentListResponse(user.getUserName(), "login complete", -1L);
			response.setDocumentTitle("");
			response.setChapters(new ArrayList<ChapterDto>());
			return response;
		}
		
		return createWrongCredentialsResponse();
	}

	private boolean credentialsOk(Request request) throws Exception {
		String userName = RequestParamUtil.getUserName(request);
		String password = RequestParamUtil.getPassword(request);

		return userRepo.countUserNamePassword(userName, password) > 0;
	}
	
	private boolean checkCredentialsAndExportRight(String userName, String password) throws Exception {
		if (userRepo.countUserNamePassword(userName, password) < 1) {
			return false;
		}
		
		User user = userRepo.findById(userName).get();
		return user.getRole() == MainConstants.USER_ROLE_ADMIN;
	}
	
	private boolean credentialsAndDocumentAccessOk(Request request) throws Exception {
		if (!credentialsOk(request)) {
			return false;
		}
		String userName = RequestParamUtil.getUserName(request);
		long documentId = RequestParamUtil.getDocumentId(request);
		return documentAccessRepo.countAccess(userName, documentId) > 0;
	}

	private Response createWrongCredentialsResponse() {
		Response response = new Response("Wrong username or password", ResponseConstants.RESPONSE_CODE_WRONG_CREDENTIALS);
		return response;
	}

	public List<String> listExportItems(String userName, String password) throws Exception {
		if (!checkCredentialsAndExportRight(userName, password)) {
			return null;
		}
		
    	List<String> result = new ArrayList<String>();
    	result.add(MainConstants.EXPORT_ITEM_ENTITIES);

		return result;
	}

	public String exportItem(String userName, String password, String itemName) throws Exception {
		if (!checkCredentialsAndExportRight(userName, password)) {
			return null;
		}
		
		if (!MainConstants.EXPORT_ITEM_ENTITIES.equals(itemName)) {
			return "unkonwn item: '" + itemName + "'";
		}
		
		ExportDataDto result = new ExportDataDto();
		result.setDocuments(documentsRepo.findAll());
		result.setChapters(chapterRepo.findAll());
		result.setDocumentAccess(documentAccessRepo.findAll());
		
		String resultString = new ObjectMapper().writeValueAsString(result);		
		return resultString;
	}
	
}
