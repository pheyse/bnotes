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
	private String chapterToEditTitle;
	private String chapterToEditBody;
	private String documentTitle;
	private Long selectedDocumentId;
	private boolean finishEditing;
	private String editChapterMessage;
	private List<String> userChoices;
	private String alertMessage;
	private List<DocumentDto> possibleDocumentsToMoveTo;
	
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

	public String getAlertMessage() {
		return alertMessage;
	}

	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}

	public String getChapterToEditTitle() {
		return chapterToEditTitle;
	}

	public void setChapterToEditTitle(String chapterToEditTitle) {
		this.chapterToEditTitle = chapterToEditTitle;
	}

	public String getChapterToEditBody() {
		return chapterToEditBody;
	}

	public void setChapterToEditBody(String chapterToEditBody) {
		this.chapterToEditBody = chapterToEditBody;
	}

	public List<DocumentDto> getPossibleDocumentsToMoveTo() {
		return possibleDocumentsToMoveTo;
	}

	public void setPossibleDocumentsToMoveTo(List<DocumentDto> possibleDocumentsToMoveTo) {
		this.possibleDocumentsToMoveTo = possibleDocumentsToMoveTo;
	}


}
