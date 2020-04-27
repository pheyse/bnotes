package de.brightside.bnotes.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.bright_side.brightmarkdown.BrightMarkdown;
import de.brightside.bnotes.base.RequestActionConstants;
import de.brightside.bnotes.base.ResponseConstants;
import de.brightside.bnotes.dao.ChapterRepo;
import de.brightside.bnotes.dao.DocumentAccessRepo;
import de.brightside.bnotes.dao.DocumentRepo;
import de.brightside.bnotes.dao.HistoryLogRepo;
import de.brightside.bnotes.dao.UserRepo;
import de.brightside.bnotes.exceptions.InternalErrorException;
import de.brightside.bnotes.exceptions.WrongCredentialsException;
import de.brightside.bnotes.model.Request;
import de.brightside.bnotes.model.Response;
import de.brightside.bnotes.service.DocumentService;

@Controller
public class MainController{
    Logger logger = LoggerFactory.getLogger(MainController.class);
	
    @Autowired
    ChapterRepo chapterRepo;
    @Autowired
    DocumentRepo documentsRepo;
    @Autowired
    HistoryLogRepo historyLogRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    DocumentAccessRepo documentAccessRepo;
    
    @RequestMapping("/")
    public String main(){
    	return "main.jsp";
    }
    
    private DocumentService createDocumentService() {
    	return new DocumentService(userRepo, documentAccessRepo, documentsRepo, chapterRepo, historyLogRepo);
    }

    @GetMapping("/BrightMarkdownHelp")
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
    @ResponseBody
    public List<String> listExportItems(@RequestParam String userName, @RequestParam String password) {
    	try {
    		List<String> result = createDocumentService().listExportItems(userName, password);
    		return result;
    	} catch (WrongCredentialsException e) {
    		throw e;
    	} catch (Exception e) {
    		logger.error("error", e);
    		throw new InternalErrorException();
    	}
    }
    
    @PostMapping("/exportItem")
    @ResponseBody
    public String exportItem(@RequestParam String userName, @RequestParam String password, @RequestParam String item) {
    	try {
    		String result = createDocumentService().exportItem(userName, password, item);
    		return result;
    	} catch (WrongCredentialsException e) {
    		throw e;
    	} catch (Exception e) {
    		logger.error("Could not export item with name '" + item + "'", e);
    		throw new InternalErrorException();
    	}
    }

    @PostMapping("/exportHtml")
    @ResponseBody
    public String exportHtml(@RequestParam String userName, @RequestParam String password, @RequestParam long documentId) {
    	
    	try {
    		String result = createDocumentService().exportDocumentAsHtml(userName, password, documentId);
    		return result;
    	} catch (Exception e) {
    		logger.error("Could not export document with id " + documentId, e);
    		return "Error. see log for details";
    	}
    }

    
    @PostMapping("/request")
    @ResponseBody
    public Response mainPageRequest(@RequestBody Request request) {
    	logger.debug("Request: " + request);
    	try {
    		Response response = null;
    		String action = "";
    		if (request.getAction() != null) {
    			action = request.getAction();
    		}
    		switch (action) {
			case RequestActionConstants.LOGIN:
				response = createDocumentService().login(request);
				break;
			case RequestActionConstants.ADD_DOCUMENT:
				response = createDocumentService().addDocument(request);
				break;
			case RequestActionConstants.RENAME_DOCUMENT:
				response = createDocumentService().editDocument(request);
				break;
			case RequestActionConstants.DELETE_DOCUMENT:
				response = createDocumentService().deleteDocument(request);
				break;
			case RequestActionConstants.ADD_CHAPTER:
				response = createDocumentService().addChapter(request);
				break;
			case RequestActionConstants.SAVE_CHAPTER:
				response = createDocumentService().saveChapter(request);
				break;
			case RequestActionConstants.MOVE_CHAPTER:
				response = createDocumentService().moveChapter(request);
				break;
			case RequestActionConstants.DELETE_CHAPTER:
				response = createDocumentService().deleteChapter(request);
				break;
			case RequestActionConstants.GET_DOCUMENTS:
				response = createDocumentService().getDocuments(request);
				break;
			case RequestActionConstants.GET_CHAPTERS:
			case RequestActionConstants.TEST_BUTTON_CLICKED:
				response = createDocumentService().getChapters(request);
				break;
			case RequestActionConstants.GET_POSSIBLE_USERS_TO_GRANT_ACCESS:
				response = createDocumentService().getPossibleUsersToGrantAccess(request);
				break;
			case RequestActionConstants.GRANT_ACCESS:
				response = createDocumentService().grantAccess(request);
				break;
			default:
				response = new Response("Unknown action: '" + action + "'", ResponseConstants.RESPONSE_CODE_ERROR);
				break;
			}
    		logger.debug("response: " + response);
    		return response;
    	} catch (Exception e) {
    		logger.error("error occured: " + e, e);
//    		Response response = new Response("Error: " + e, 400);
    		throw new InternalErrorException("Internal error");

    	}
    }
}