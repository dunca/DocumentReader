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
		
		while ((startBoundaryIndex = breakIterator.first()) < sourceDocumentBookmark.getSentenceIndex()) {
			startBoundaryIndex = breakIterator.next();
		}
		
		this.page = page;
	}
	
	public boolean hasNext() {
		endBoundaryIndex = breakIterator.next();
		return endBoundaryIndex != BreakIterator.DONE;
	}

	public String next() {
		String sentence = page.getContent().substring(startBoundaryIndex, endBoundaryIndex);
		try {
			sourceDocumentBookmark.setSentenceIndex(startBoundaryIndex);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Something went wrong when setting the sentence index", e);
		}
		startBoundaryIndex = endBoundaryIndex;
		return sentence;
	}
}
