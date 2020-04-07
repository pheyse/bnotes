package de.brightside.bnotes.model;

import java.util.Map;

public class Request {
	private String action;
	private long userId;
	private Map<String, String> parameters;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "Request [action=" + action + ", userId=" + userId + ", parameters=" + parameters + "]";
	}

	
}
