package de.brightside.bnotes.logic;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import de.brightside.bnotes.base.MainConstants;
import de.brightside.bnotes.model.Document;

public class ExportLogic {
	private static final int MAX_FILENAME_LENGTH = 30;
	private static final String HTML_FILE_ENDING = ".html";
	private static final String ID_SEPARATOR_IN_FILENAME = "_";
	
	public enum ExportType{ENTITIES, HTML_FILE}
	
	public String getEncodedFilePath(Document document) throws UnsupportedEncodingException {
		String path = MainConstants.EXPORT_DIR_PATH_HTML + getFilename(document);
		return URLEncoder.encode(path, StandardCharsets.UTF_8.toString());
	}
	
	public String getFilePath(Document document) {
		return MainConstants.EXPORT_DIR_PATH_HTML + getFilename(document);
	}
	
	public String getFilename(Document document) {
		String useTitle = filterLetters(document.getTitle());
		if (useTitle.length() > MAX_FILENAME_LENGTH) {
			useTitle = useTitle.substring(0, MAX_FILENAME_LENGTH);
		}
		
		return document.getDocumentId() + ID_SEPARATOR_IN_FILENAME + useTitle + HTML_FILE_ENDING;
	}

	private String filterLetters(String text) {
		StringBuilder result = new StringBuilder();

		for (char i: text.replace(" ", "_").toCharArray()) {
			boolean lowerCaseChar = (i >= 'a') && (i <= 'z');
			boolean upperCaseChar = (i >= 'A') && (i <= 'Z');
			boolean number = (i >= '0') && (i <= '9');
			boolean underscore = i == '_';
			
			if (lowerCaseChar || upperCaseChar || number || underscore) {
				result.append(i);
			}
		}
		
		return result.toString();
	}
	
	public ExportType getExportTypeFromEncodedFilePath(String path) {
		if (MainConstants.EXPORT_ITEM_ENTITIES.equals(path)) {
			return ExportType.ENTITIES;
		} else if (path.startsWith(MainConstants.EXPORT_DIR_PATH_HTML)) {
			return ExportType.HTML_FILE;
		}
		return null;
	}

	public long getDocumentIdFromEncodedHtmlFilePath(String itemPath) throws Exception {
		String path = URLDecoder.decode(itemPath, StandardCharsets.UTF_8.toString());

		path = path.substring(MainConstants.EXPORT_DIR_PATH_HTML.length());
		
		int pos = path.indexOf(ID_SEPARATOR_IN_FILENAME);
		if (pos < 0) {
			throw new Exception("Could not find ID in item path '" + itemPath + "'");
		}
		
		String idText = path.substring(0, pos);
		
		try {
			return Long.valueOf(idText);
		} catch (Exception e) {
			throw new Exception("Could not read ID in item path '" + itemPath + "'");
		}
	}
	
}
