package de.brightside.bnotes.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
public class Chapter {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long chapterId;
	private long documentId;
	private long orderSequence;
	private int level;
	@Size(min = 0, max = 100)
	private String title;
	@Size(min = 0, max = 10000)
	private String body;
	private int active;
	
	@Override
	public String toString() {
		return "Chapter [chapterId=" + chapterId + ", documentId=" + documentId + ", orderSequence=" + orderSequence
				+ ", level=" + level + ", title=" + title + ", body=" + body + ", active=" + active + "]";
	}

	public long getChapterId() {
		return chapterId;
	}

	public void setChapterId(long chapterId) {
		this.chapterId = chapterId;
	}

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public long getOrderSequence() {
		return orderSequence;
	}

	public void setOrderSequence(long orderSequence) {
		this.orderSequence = orderSequence;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

}
