package io.github.sidf;

import java.util.Iterator;
import java.io.IOException;

import io.github.sidf.abstractions.Document;

public class DocumentIterator implements Iterator<DocumentPage> {
	private Document document;
	
	public DocumentIterator(Document document) {
		this.document = document;
	}
	
	public boolean hasNext() {
		return document.getBookmark().getPageIndex() <= document.getPageCount() - 1;
	}

	public DocumentPage next() {
		try {
			DocumentPage page = document.getNextPage();
			document.getBookmark().setPage(page);
			return page;
		} catch (IOException e) {
			// do something about it
		}
		
		return null;
	}
}