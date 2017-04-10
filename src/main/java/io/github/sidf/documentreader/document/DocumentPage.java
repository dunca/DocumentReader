package io.github.sidf.documentreader.document;

import java.util.Iterator;

public class DocumentPage implements Iterable<String> {
	private int length;
	private String content;
	private DocumentBookmark sourceDocumentBookmark;
	
	public DocumentPage(DocumentBookmark sourceDocumentBookmark, String content) {
		this.sourceDocumentBookmark = sourceDocumentBookmark;
		this.length = content.length();
		this.content = content;
	}
	
	public int getLength() {
		return length;
	}
	
	public String getContent() {
		return content;
	}
	
	public Iterator<String> iterator() {
		return new DocumentPageSentenceIterator(sourceDocumentBookmark, this);
	}
}
