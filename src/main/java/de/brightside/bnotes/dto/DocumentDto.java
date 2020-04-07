package de.brightside.bnotes.dto;

public class DocumentDto {
	private long documentId;
	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}
	@Override
	public String toString() {
		return "DocumentDto [documentId=" + documentId + ", title=" + title + "]";
	}

	
}
