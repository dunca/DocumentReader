package io.github.sidf;

public class Page {
	private int index;
	private String content;
	
	public Page(String content, int index) {
		this.content = content;
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
