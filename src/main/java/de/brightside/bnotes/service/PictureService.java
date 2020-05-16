package de.brightside.bnotes.service;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.brightside.bnotes.base.MainConstants;
import de.brightside.bnotes.base.ResponseConstants;
import de.brightside.bnotes.dao.HistoryLogRepo;
import de.brightside.bnotes.dao.PictureRepo;
import de.brightside.bnotes.model.HistoryLog;
import de.brightside.bnotes.model.Picture;
import de.brightside.bnotes.model.Request;
import de.brightside.bnotes.model.Response;
import de.brightside.bnotes.util.BnotesUtil;
import de.brightside.bnotes.util.RequestParamUtil;

@Transactional
public class PictureService {
    private static final String IMAGE_ENDING_JPEG = "jpeg";
    private static final String IMAGE_ENDING_JPG = "jpg";
    private static final String IMAGE_ENDING_GIF = "gif";
    private static final String IMAGE_ENDING_PNG = "png";

	Logger logger = LoggerFactory.getLogger(PictureService.class);
	
	private PictureRepo pictureRepo;
	private DocumentService documentService;
	private HistoryLogRepo historyLogRepo;

    public PictureService(DocumentService documentService, PictureRepo pictureRepo, HistoryLogRepo historyLogRepo) {
		this.documentService = documentService;
		this.pictureRepo = pictureRepo;
		this.historyLogRepo = historyLogRepo;
	}

	public Picture getPicture(long imageId) {
		Picture result = pictureRepo.findById(imageId).get();
		if (result.getData() == null) {
			result.setData(new byte[0]);
		}
		return result;
	}

	public Response addPicture(MultipartFile file, String userName, String jwt, long documentId, long chapterId, String name) throws IOException {
		if (!documentService.hasDocumentAccess(userName, documentId)) {
    		Response response = new Response("image uploaded", ResponseConstants.RESPONSE_CODE_OK);
    		response.setAlertMessage("Error: no document access");
		}
		
    	Picture picture = new Picture();
    	picture.setChapterId(chapterId);
    	
    	byte[] data = null;
    	if (file != null) {
    		data = file.getBytes();
    	}
    	if ((data == null) || (data.length == 0)) {
    		Response response = new Response("image uploaded", ResponseConstants.RESPONSE_CODE_OK);
    		response.setAlertMessage("Error: you did not choose and image file");
    		return response;
    	}
    	
    	picture.setData(data);
    	String fileType = determineTypeFromFilename(file.getOriginalFilename());
    	if (fileType == null) {
    		Response response = new Response("image uploaded", ResponseConstants.RESPONSE_CODE_OK);
    		response.setAlertMessage("Error: File '" + file.getOriginalFilename() + "' doesn't have an image file ending (.jpg, .jpeg, .gif, .png)");
    		return response;
    	}
    	
    	picture.setType(fileType);

    	String useImageName = name;
    	if ((useImageName == null) || (useImageName.length() == 0)){
    		String filename = file.getOriginalFilename();
    		String ending = BnotesUtil.getEndingOfFilename(filename);
    		useImageName = filename.substring(0, filename.length() - ending.length());
    	}
    	
    	useImageName = transformToImageName(useImageName);
    	
    	picture.setName(useImageName);
    	Picture existingPicuture = pictureRepo.findByChapterIdAndName(chapterId, useImageName); 
    	if (existingPicuture != null) {
    		logger.info("Found existing image with chapter '" + chapterId + "' and name '" + useImageName + "': " + existingPicuture);
    		Response response = new Response("image uploaded", ResponseConstants.RESPONSE_CODE_OK);
    		response.setAlertMessage("Error: There already is an image with name '" + useImageName + "' for this chapter.");
    		return response;
    	}

    	pictureRepo.save(picture);

    	
    	Response response = documentService.createChapterDisplayResponse(userName, jwt, documentId, "image uploaded", null, null);
		response.setAlertMessage("Image uploaded as name '" + useImageName + "'.");
		
		return response;
	}

	private String transformToImageName(String text) {
		StringBuilder result = new StringBuilder();

		for (char i: text.toLowerCase().replace(" ", "_").toCharArray()) {
			boolean lowerCaseChar = (i >= 'a') && (i <= 'z');
			boolean number = (i >= '0') && (i <= '9');
			boolean underscore = i == '_';
			
			if (lowerCaseChar || number || underscore) {
				result.append(i);
			}
		}
		
		return result.toString();
	}
	
	private String determineTypeFromFilename(String filename) {
		String ending = BnotesUtil.getEndingOfFilename(filename);
		if (ending.length() == 0) {
			return null;
		}
		
		switch (ending) {
		case IMAGE_ENDING_JPEG:
		case IMAGE_ENDING_JPG:
			return MainConstants.PICTURE_TYPE_JPEG;
		case IMAGE_ENDING_GIF:
			return MainConstants.PICTURE_TYPE_GIF;
		case IMAGE_ENDING_PNG:
			return MainConstants.PICTURE_TYPE_PNG;
		default:
			return null;
		}
	}

	public Response deletePicture(String username, String jwt, Request request) throws Exception {
		long documentId = RequestParamUtil.getDocumentId(request);
		if (!documentService.hasDocumentAccess(username, documentId)) {
			return documentService.createWrongCredentialsResponse();
		}
		long imageId = RequestParamUtil.getImageId(request);

		Optional<Picture> optionalPicture = pictureRepo.findById(imageId);
		if (optionalPicture.isEmpty()) {
    		Response response = new Response("error", ResponseConstants.RESPONSE_CODE_OK);
    		response.setAlertMessage("Error: This image doesn't exist");
    		return response;
		}
		Picture picture = optionalPicture.get();
		HistoryLog historyEntry = new HistoryLog();
		historyEntry.setAction("delete");
		historyEntry.setUserName(username);
		historyEntry.setObjectType("picture");
		historyEntry.setObjectBinaryData(picture.getData());
		picture.setData(null); //: the picture data should not be stored as JSON
		historyEntry.setObjectValue(new ObjectMapper().writeValueAsString(picture));
		
		historyLogRepo.save(historyEntry);
		
		pictureRepo.deleteById(imageId);
		
		Response response = documentService.createChapterDisplayResponse(username, jwt, documentId, "image uploaded", null, null);
		response.setAlertMessage("Image deleted");
		
		return response;
	}
    
}
