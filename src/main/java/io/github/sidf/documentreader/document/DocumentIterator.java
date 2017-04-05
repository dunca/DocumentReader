package io.github.sidf.documentreader.document;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileNotFoundException;

import io.github.sidf.documentreader.util.FileUtil;

public class DocumentIterator implements Iterator<DocumentPage> {
	private Document document;
	private boolean firstIteration = true;
	private static Logger logger = Logger.getLogger(DocumentIterator.class.getName());
	
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
			page = document.updateBookmarkPage(pageIndex, sentenceIndex);
		} catch (Exception e) {
			e.printStackTrace();
			new RuntimeException(String.format("Could not fetch page from %s", document.getDocumentName()));
		}
		
		try {
			FileUtil.stringToFile(page.getContent(), document.getCurrentPagePath().getPath());
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Could not write the page's content", e);
		}
		
		firstIteration = false;
		
		return page;
	}
}