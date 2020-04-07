package de.brightside.bnotes.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DocumentAccess {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long documentAccessId;
	
	private String userName;
	
	private long documentId;

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public long getDocumentAccessId() {
		return documentAccessId;
	}

	public void setDocumentAccessId(long documentAccessId) {
		this.documentAccessId = documentAccessId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "DocumentAccess [documentAccessId=" + documentAccessId + ", userName=" + userName + ", documentId="
				+ documentId + "]";
	}

}
