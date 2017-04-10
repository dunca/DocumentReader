package io.github.sidf.documentreader.document;

import java.util.Iterator;

public class Page implements Iterable<String> {
	private int length;
	private String content;
	private Bookmark sourceBookmark;
	
	Page(Bookmark sourceBookmark, String content) {
		this.sourceBookmark = sourceBookmark;
		this.length = content.length();
		this.content = content;
	}
		
	String getContent() {
		return content;
	}
	
	public Iterator<String> iterator() {
		return new PageIterator(sourceBookmark, this);
	}
}
