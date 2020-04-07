package de.brightside.bnotes.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

@Entity
public class HistoryLog {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long historyLogId;
	@CreationTimestamp
	private Timestamp time;
	private long userId;
	private String objectType;
	@Size(min = 0, max = 20000)
	private String objectValue;
	private String action;

	public long getHistoryLogId() {
		return historyLogId;
	}
	public void setHistoryLogId(long historyLogId) {
		this.historyLogId = historyLogId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getObjectValue() {
		return objectValue;
	}
	public void setObjectValue(String objectValue) {
		this.objectValue = objectValue;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "HistoryLog [historyLogId=" + historyLogId + ", time=" + time + ", userId=" + userId + ", objectType="
				+ objectType + ", objectValue=" + objectValue + ", action=" + action + "]";
	}

	
	
}
