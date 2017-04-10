package io.github.sidf.documentreader.document;

import java.util.Iterator;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageIterator implements Iterator<String> {
	private Page page;
	private int endBoundaryIndex;
	private int startBoundaryIndex;
	private Bookmark sourceBookmark;
	private BreakIterator breakIterator = BreakIterator.getSentenceInstance();
	private static Logger logger = Logger.getLogger(PageIterator.class.getName());
	
	PageIterator(Bookmark sourceBookmark, Page page) {
		this.sourceBookmark = sourceBookmark;
		breakIterator.setText(page.getContent());
		startBoundaryIndex = breakIterator.first();
		
		while (startBoundaryIndex < sourceBookmark.getSentenceIndex()) {
			startBoundaryIndex = breakIterator.next();
		}
		
		this.page = page;
	}
	
	public boolean hasNext() {
		endBoundaryIndex = breakIterator.next();
		boolean hasNext = endBoundaryIndex != BreakIterator.DONE;
		
		if (!hasNext && !sourceBookmark.onLastPage()) {
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
			sourceBookmark.setSentenceIndex(index);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Something went wrong when setting the sentence index", e);
		}
	}
}
