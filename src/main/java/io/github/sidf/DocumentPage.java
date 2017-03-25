package io.github.sidf;

import java.util.Iterator;

public class DocumentPage implements Iterable<String> {
	private int index;
	private int length;
	private String content;
	private DocumentBookmark sourceDocumentBookmark;
	
	public DocumentPage(DocumentBookmark sourceDocumentBookmark, String content, int index) {
		this.sourceDocumentBookmark = sourceDocumentBookmark;
		this.length = content.length();
		this.content = content;
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getLength() {
		return length;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
		this.length = content.length();
	}

	public Iterator<String> iterator() {
		return new DocumentPageSentenceIterator(sourceDocumentBookmark, this);
	}
}
