package de.brightside.bnotes.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
public class Document {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long documentId;

	@Size(min = 0, max = 100)
	private String title;
	
	@UpdateTimestamp
	private Timestamp lastChangeTime;
	
	@CreationTimestamp
	private Timestamp creationTime;
	
	private int active;

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Timestamp getLastChangeTime() {
		return lastChangeTime;
	}

	public void setLastChangeTime(Timestamp lastChangeTime) {
		this.lastChangeTime = lastChangeTime;
	}

	public Timestamp getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Timestamp creationTime) {
		this.creationTime = creationTime;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "Document [documentId=" + documentId + ", title=" + title + ", lastChangeTime=" + lastChangeTime
				+ ", creationTime=" + creationTime + ", active=" + active + "]";
	}
	
}
