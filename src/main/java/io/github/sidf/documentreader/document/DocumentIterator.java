package io.github.sidf.documentreader.document;

import java.util.Iterator;

public class DocumentIterator implements Iterator<DocumentPage> {
	private Document document;
	private boolean firstIteration = true;
	
	public DocumentIterator(Document document) {
		this.document = document;
	}
	
	public boolean hasNext() {
		int lastPageIndex = document.getPageCount() - 1;
		
		if (firstIteration) {
			return document.getBookmark().getPageIndex() <= lastPageIndex; 
		}
		
		return document.getBookmark().getPageIndex() < lastPageIndex;
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