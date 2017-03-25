package io.github.sidf.documentreader.document;

import java.util.Iterator;
import java.text.BreakIterator;

public class DocumentPageSentenceIterator implements Iterator<String> {
	private DocumentPage page;
	private int endBoundaryIndex;
	private int startBoundaryIndex;
	private DocumentBookmark sourceDocumentBookmark;
	private BreakIterator breakIterator = BreakIterator.getSentenceInstance();
	
	public DocumentPageSentenceIterator(DocumentBookmark sourceDocumentBookmark, DocumentPage page) {
		this.sourceDocumentBookmark = sourceDocumentBookmark;
		breakIterator.setText(page.getContent());
		startBoundaryIndex = breakIterator.first();
		this.page = page;
	}
	
	public boolean hasNext() {
		endBoundaryIndex = breakIterator.next();
		return endBoundaryIndex != BreakIterator.DONE;
	}

	public String next() {
		String sentence = page.getContent().substring(startBoundaryIndex, endBoundaryIndex);
		sourceDocumentBookmark.setCharacterIndex(startBoundaryIndex);
		startBoundaryIndex = endBoundaryIndex;
		return sentence;
	}
}
