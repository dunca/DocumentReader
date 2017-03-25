package io.github.sidf;

import java.util.Iterator;
import java.io.IOException;

import io.github.sidf.abstractions.Document;

public class DocumentIterator implements Iterator<Page> {
	private Document document;
	
	public DocumentIterator(Document document) {
		this.document = document;
	}
	
	public boolean hasNext() {
		return document.getCurrentPage().getIndex() <= document.getPageCount() - 1;
	}

	public Page next() {
		try {
			return document.getNextPage();
		} catch (IOException e) {
			// do something about it
		}
		
		return null;
	}
}