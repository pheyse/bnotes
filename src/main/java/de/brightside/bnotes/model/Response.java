package de.brightside.bnotes.model;

import java.util.List;

import de.brightside.bnotes.dto.ChapterDto;
import de.brightside.bnotes.dto.DocumentDto;

public class Response {
	private String info;
	private int status;
	private List<ChapterDto> chapters;
	private List<DocumentDto> documents;
	private Long chapterToEdit;
	private Integer chapterToEditLevel;
	private String documentTitle;
	private Long selectedDocumentId;
	private boolean finishEditing;
	private String editChapterMessage;
	private List<String> userChoices;
	
	public Response(String info, int status) {
		this.info = info;
		this.status = status;
	}
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public List<ChapterDto> getChapters() {
		return chapters;
	}

	public void setChapters(List<ChapterDto> chapters) {
		this.chapters = chapters;
	}

	public Long getChapterToEdit() {
		return chapterToEdit;
	}

	public void setChapterToEdit(Long chapterToEdit) {
		this.chapterToEdit = chapterToEdit;
	}

	public Integer getChapterToEditLevel() {
		return chapterToEditLevel;
	}

	public void setChapterToEditLevel(Integer chapterToEditLevel) {
		this.chapterToEditLevel = chapterToEditLevel;
	}

	public List<DocumentDto> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentDto> documents) {
		this.documents = documents;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public Long getSelectedDocumentId() {
		return selectedDocumentId;
	}

	public void setSelectedDocumentId(Long selectedDocumentId) {
		this.selectedDocumentId = selectedDocumentId;
	}

	public boolean isFinishEditing() {
		return finishEditing;
	}

	public void setFinishEditing(boolean finishEditing) {
		this.finishEditing = finishEditing;
	}

	public String getEditChapterMessage() {
		return editChapterMessage;
	}

	public void setEditChapterMessage(String editChapterMessage) {
		this.editChapterMessage = editChapterMessage;
	}

	public List<String> getUserChoices() {
		return userChoices;
	}

	public void setUserChoices(List<String> userChoices) {
		this.userChoices = userChoices;
	}
	
}
