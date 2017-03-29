package io.github.sidf.documentreader.document;

import java.util.Iterator;
import java.io.IOException;

public class DocumentIterator implements Iterator<DocumentPage> {
	private Document document;
	
	public DocumentIterator(Document document) {
		this.document = document;
	}
	
	public boolean hasNext() {
		return document.getBookmark().getPageIndex() <= document.getPageCount() - 1;
	}

	public DocumentPage next() {
		DocumentPage page = null;
		
		try {
			page = document.nextPage();
		} catch (IOException e) {
			new RuntimeException(String.format("Could not fetch page from %s", document.getDocumentName()));
		}
		
		return page;
	}
}