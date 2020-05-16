package de.brightside.bnotes.model;

import java.util.Map;

public class Request {
	private String action;
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
	@Override
	public String toString() {
		return "Request [action=" + action + ", parameters=" + parameters + "]";
	}

}
