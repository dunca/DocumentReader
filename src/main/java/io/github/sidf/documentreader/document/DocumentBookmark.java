package io.github.sidf.documentreader.document;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.logging.Logger;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class DocumentBookmark {
	private int pageIndex;
	private Ini bookmarkIni;
	private int sentenceIndex;
	private Document document;
	private DocumentPage page;
	private static Logger logger = Logger.getLogger(DocumentBookmark.class.getName());
	
	public DocumentBookmark(DocumentPage page, int pageIndex, int sentenceIndex, Ini ini, Document document) throws InvalidFileFormatException, IOException {
		this.page = page;
		this.bookmarkIni = ini;
		this.document = document;
		this.pageIndex = pageIndex;
		this.sentenceIndex = sentenceIndex;
	}
	
	public DocumentPage getPage() {
		return page;
	}

	public void setPage(DocumentPage page) throws IOException {
		this.page = page;
	}

	public int getPageIndex() {
		return pageIndex;
	}
	
	public void setPageIndex(int pageIndex) throws IOException {
		this.pageIndex = pageIndex;
		setSentenceIndex(0);
	}
	
	public int getSentenceIndex() {
		return sentenceIndex;
	}
	
	public void setSentenceIndex(int sentenceIndex) throws IOException {
		if (this.sentenceIndex == sentenceIndex && 
			(bookmarkIni.get(document.getDocumentId(), "pageIndex") == String.valueOf(pageIndex))) {
			return;
		}
		
		this.sentenceIndex = sentenceIndex;
		updateBookmarkIni();
	}
	
	public boolean endReached() {
		BreakIterator iterator = BreakIterator.getSentenceInstance();
		iterator.setText(page.getContent());
		return onLastPage() && iterator.preceding(iterator.last()) == sentenceIndex;
	}
	
	public boolean onLastPage() {
		return pageIndex == document.getPageCount() - 1;
	}
	
	private void updateBookmarkIni() throws IOException {
		bookmarkIni.put(document.getDocumentId(), "pageIndex", pageIndex);
		bookmarkIni.put(document.getDocumentId(), "sentenceIndex", sentenceIndex);
		bookmarkIni.store();
		
		logger.info(String.format("Updated bookmark history file with pageIndex: %d and sentenceIndex: %d", pageIndex, sentenceIndex));
	}
}
