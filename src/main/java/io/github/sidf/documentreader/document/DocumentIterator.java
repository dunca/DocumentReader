package io.github.sidf.documentreader.document;

import java.util.Iterator;

public class DocumentIterator implements Iterator<DocumentPage> {
	private Document document;
	private boolean firstIteration = true;
	
	public DocumentIterator(Document document) {
		this.document = document;
	}
	
	public boolean hasNext() {
		return document.getBookmark().getPageIndex() < document.getPageCount() - 1;
	}

	public DocumentPage next() {
		DocumentPage page = null;
		
		int pageIndex = document.getBookmark().getPageIndex();
		int sentenceIndex = document.getBookmark().getSentenceIndex();
		
		if (!firstIteration && sentenceIndex == 0) {
			pageIndex++;
		}
		
		try {
			page = document.setPage(pageIndex, sentenceIndex);
		} catch (Exception e) {
			e.printStackTrace();
			new RuntimeException(String.format("Could not fetch page from %s", document.getDocumentName()));
		}
		
		firstIteration = false;
		
		return page;
	}
}