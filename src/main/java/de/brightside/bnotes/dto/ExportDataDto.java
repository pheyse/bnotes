package de.brightside.bnotes.dto;

import java.util.List;

import de.brightside.bnotes.model.Chapter;
import de.brightside.bnotes.model.Document;
import de.brightside.bnotes.model.DocumentAccess;

public class ExportDataDto {
	private List<Document> documents;
	private List<Chapter> chapters;
	private List<DocumentAccess> documentAccess;
	public List<Document> getDocuments() {
		return documents;
	}
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	public List<Chapter> getChapters() {
		return chapters;
	}
	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}
	public List<DocumentAccess> getDocumentAccess() {
		return documentAccess;
	}
	public void setDocumentAccess(List<DocumentAccess> documentAccess) {
		this.documentAccess = documentAccess;
	}
	@Override
	public String toString() {
		return "ExportDataDto [documents=" + documents + ", chapters=" + chapters + ", documentAccess=" + documentAccess
				+ "]";
	}
	
}
