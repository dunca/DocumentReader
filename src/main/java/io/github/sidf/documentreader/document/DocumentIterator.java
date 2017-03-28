package io.github.sidf.documentreader.document;

import java.util.Iterator;
import java.io.IOException;
import java.util.logging.Logger;

public class DocumentIterator implements Iterator<DocumentPage> {
	private static Logger logger = Logger.getLogger(DocumentIterator.class.getName());
	
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
			new RuntimeException(String.format("Could not fetch page from %s", document.getDocumentName()));
		}
		
		return null;
	}
}