package io.github.sidf.documentreader.document;

import java.util.Iterator;

public class DocumentIterator implements Iterator<Page> {
	private Document document;
	private boolean firstIteration = true;
	
	DocumentIterator(Document document) {
		this.document = document;
	}
	
	public boolean hasNext() {
		int lastPageIndex = document.getPageCount() - 1;
		
		if (firstIteration) {
			return document.getBookmark().getPageIndex() <= lastPageIndex; 
		}
		
		return document.getBookmark().getPageIndex() < lastPageIndex;
	}

	public Page next() {
		Page page = null;
		
		int pageIndex = document.getBookmark().getPageIndex();
		int sentenceIndex = document.getBookmark().getSentenceIndex();
		
		if (!firstIteration && sentenceIndex == 0) {
			pageIndex++;
		}
		
		try {
			page = document.updateBookmarkPage(pageIndex, sentenceIndex);
		} catch (Exception e) {
			e.printStackTrace();
			new RuntimeException(String.format("Could not fetch page from %s", document.getName()));
		}
		
		firstIteration = false;
		
		return page;
	}
}