package de.brightside.bnotes.dto;

public class ChapterDto {
	private long chapterId;
	private int level;
	private String title;
	private String indexLabel;
	private String body;
	public long getChapterId() {
		return chapterId;
	}
	public void setChapterId(long chapterId) {
		this.chapterId = chapterId;
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
	public String getIndexLabel() {
		return indexLabel;
	}
	public void setIndexLabel(String indexLabel) {
		this.indexLabel = indexLabel;
	}
	@Override
	public String toString() {
		return "ChapterDto [chapterId=" + chapterId + ", level=" + level + ", title=" + title + ", indexLabel="
				+ indexLabel + ", body=" + body + "]";
	}
	
}
