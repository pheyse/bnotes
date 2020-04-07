package de.brightside.bnotes.util;

import de.brightside.bnotes.base.RequestParamConstants;
import de.brightside.bnotes.model.Request;

public class RequestParamUtil {
	public static long getLongValue(Request request, String keyName) throws Exception{
		String value = request.getParameters().get(keyName);
		long result = Long.valueOf(value);
		return result;
	}

	public static int getIntValue(Request request, String keyName) throws Exception{
		String value = request.getParameters().get(keyName);
		int result = Integer.valueOf(value);
		return result;
	}

	public static String getStringValue(Request request, String keyName) throws Exception{
		String value = request.getParameters().get(keyName);
		return value;
	}
	
	public static long getDocumentId(Request request) throws Exception{
		return getLongValue(request, RequestParamConstants.DOC_ID);
	}

	public static long getChapterId(Request request) throws Exception {
		return getLongValue(request, RequestParamConstants.CHAPTER_ID);
	}

	public static int getLevel(Request request) throws Exception {
		return getIntValue(request, RequestParamConstants.LEVEL);
	}

	public static int getDirection(Request request) throws Exception {
		return getIntValue(request, RequestParamConstants.DIRECTION);
	}
	
	public static String getTitle(Request request) throws Exception {
		return getStringValue(request, RequestParamConstants.TITLE);
	}

	public static String getBody(Request request) throws Exception {
		return getStringValue(request, RequestParamConstants.BODY);
	}

	public static String getUserName(Request request) throws Exception {
		return getStringValue(request, RequestParamConstants.USER_NAME);
	}

	public static String getPassword(Request request) throws Exception {
		return getStringValue(request, RequestParamConstants.PASSWORD);
	}
}
