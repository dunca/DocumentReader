package io.github.sidf.documentreader.document;

import org.ini4j.Ini;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.logging.Logger;

public class Bookmark {
	private Page page;
	private int pageIndex;
	private Ini bookmarkIni;
	private int sentenceIndex;
	private Document sourceDocument;
	
	private BreakIterator breakIterator = BreakIterator.getSentenceInstance();
	
	private static Logger logger = Logger.getLogger(Bookmark.class.getName());
	
	Bookmark(Ini ini, Document sourceDocument) throws Exception {
		this.bookmarkIni = ini;
		this.sourceDocument = sourceDocument;
		
		String documentId = sourceDocument.getId();
		
		// try to get the actual progress made on the given document
		if (bookmarkIni.containsKey(documentId)) {
			int storedPageIndex = Integer.valueOf(bookmarkIni.get(documentId, "pageIndex"));
			int storedSentenceIndex = Integer.valueOf(bookmarkIni.get(documentId, "sentenceIndex"));
			updatePosition(storedPageIndex, storedSentenceIndex);
		} else {
			reset();
		}
	}
	
	Page getPage() {
		return page;
	}

	int getPageIndex() {
		return pageIndex;
	}
	
	int getSentenceIndex() {
		return sentenceIndex;
	}
	
	/**
	 * @return 'true' if the bookmark points to last page and sentence
	 */
	private boolean isEndReached() {
		return islastPage() && breakIterator.preceding(breakIterator.last()) == sentenceIndex;
	}
	
	/**
	 * @return 'true' if the bookmark points to the last page of the document
	 */
	private boolean islastPage() {
		return pageIndex == sourceDocument.getPageCount() - 1;
	}
	
	/**
	 * Syncs the underlying bookmark file with the current position
	 * @throws IOException if an I/O error occurs
	 */
	private void updateBookmarkFile() throws IOException {
		bookmarkIni.put(sourceDocument.getId(), "pageIndex", pageIndex);
		bookmarkIni.put(sourceDocument.getId(), "sentenceIndex", sentenceIndex);
		bookmarkIni.store();
		
		logger.info(String.format("Updated bookmark history file with pageIndex: %d and sentenceIndex: %d", pageIndex, sentenceIndex));
	}
	
	/**
	 * Resets the bookmark information and deletes the entry from the bookmark file
	 * @throws IOException
	 */
	void delete() throws Exception {
		reset();
		bookmarkIni.remove(sourceDocument.getId());
		bookmarkIni.store();
	}
	
	/**
	 * Updates the bookmark's position. It also updates the {@link Bookmark#page} property
	 * @param index the zero-based index of the page the bookmark should point to
	 * @param sentenceIndex the zero-based index of the sentence the bookmark should point to
	 * @throws Exception if an error occurs
	 */
	private void updatePosition(int pageIndex, int sentenceIndex) throws Exception {
		// we always want this when pageIndex is 0, since it's called from the constructor
		if (this.pageIndex == 0 || this.pageIndex != pageIndex) {
			this.page = sourceDocument.fetchPage(pageIndex);
			breakIterator.setText(page.getContent());
			this.pageIndex = pageIndex;
		}
		
		this.sentenceIndex = sentenceIndex;
		updateBookmarkFile();
	}
	
	/**
	 * Advances the bookmark to the page determined by the index parameter
	 * @param pageIndex the index of the page
	 * @throws Exception if an error occurs
	 */
	void setPageIndex(int pageIndex) throws Exception {
		updatePosition(pageIndex, 0);
	}
	
	/**
	 * Advances the bookmark to the sentence determined by the index parameter, while leaving the page index unchanged
	 * @param sentenceIndex the index of the sentence
	 * @throws Exception if an error occurs
	 */
	void setSentenceIndex(int sentenceIndex) throws Exception {
		updatePosition(pageIndex, sentenceIndex);
	}
	
	/**
	 * Points the bookmark to the start of the document
	 * @throws Exception if an error occurs
	 */
	void reset() throws Exception {
		updatePosition(0, 0);
	}
	
	/**
	 * If the end of the document has been reached it resets the bookmark
	 * @throws Exception
	 */
	void wrapAround() throws Exception {
		if (isEndReached()) {
			reset();
		}
	}
}
