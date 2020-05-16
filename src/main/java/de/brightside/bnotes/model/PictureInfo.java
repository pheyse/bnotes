package de.brightside.bnotes.model;

public class PictureInfo {
	private long picId;
	private String name;
	private String type;

	public PictureInfo(long picId, String name, String type) {
		super();
		this.picId = picId;
		this.name = name;
		this.type = type;
	}
	
	public long getPicId() {
		return picId;
	}
	public void setPicId(long picId) {
		this.picId = picId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "PictureInfo [picId=" + picId + ", name=" + name + ", type=" + type + "]";
	}
	
	
}
