package de.brightside.bnotes.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.Size;

@Entity
public class Picture {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long picId;
	private long chapterId;
	@Size(min = 0, max = 100)
	private String name;
	@Size(min = 1, max = 10)
	private String type;
	@Lob
	byte[] data;
	
	public long getPicId() {
		return picId;
	}
	public void setPicId(long picId) {
		this.picId = picId;
	}
	public long getChapterId() {
		return chapterId;
	}
	public void setChapterId(long chapterId) {
		this.chapterId = chapterId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Picture [picId=" + picId + ", chapterId=" + chapterId + ", name=" + name + ", type=" + type + "]";
	}
	
}
