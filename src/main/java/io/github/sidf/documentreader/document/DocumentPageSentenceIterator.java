package io.github.sidf.documentreader.document;

import java.util.Iterator;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentPageSentenceIterator implements Iterator<String> {
	private DocumentPage page;
	private int endBoundaryIndex;
	private int startBoundaryIndex;
	private DocumentBookmark sourceDocumentBookmark;
	private BreakIterator breakIterator = BreakIterator.getSentenceInstance();
	private static Logger logger = Logger.getLogger(DocumentPageSentenceIterator.class.getName());
	
	public DocumentPageSentenceIterator(DocumentBookmark sourceDocumentBookmark, DocumentPage page) {
		this.sourceDocumentBookmark = sourceDocumentBookmark;
		breakIterator.setText(page.getContent());
		startBoundaryIndex = breakIterator.first();
		
		while (startBoundaryIndex < sourceDocumentBookmark.getSentenceIndex()) {
			startBoundaryIndex = breakIterator.next();
		}
		
		this.page = page;
	}
	
	public boolean hasNext() {
		endBoundaryIndex = breakIterator.next();
		boolean hasNext = endBoundaryIndex != BreakIterator.DONE;
		
		if (!hasNext && !sourceDocumentBookmark.onLastPage()) {
			setSentenceIndex(0);
		}
		
		return hasNext;
	}

	public String next() {
		String sentence = page.getContent().substring(startBoundaryIndex, endBoundaryIndex);
		setSentenceIndex(startBoundaryIndex);
		startBoundaryIndex = endBoundaryIndex;
		return sentence;
	}
	
	private void setSentenceIndex(int index) {
		try {
			sourceDocumentBookmark.setSentenceIndex(index);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Something went wrong when setting the sentence index", e);
		}
	}
}
