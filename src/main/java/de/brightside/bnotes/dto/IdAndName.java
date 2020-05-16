package de.brightside.bnotes.dto;

public class IdAndName {
	private long id;
	private String name;
	
	public IdAndName(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "IdAndName [id=" + id + ", name=" + name + "]";
	}
	
	
}
