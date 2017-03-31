package io.github.sidf.documentreader.document;

import java.io.IOException;

import org.ini4j.Ini;

public class DocumentBookmark {
	private int pageIndex;
	private Ini bookmarkIni;
	private int sentenceIndex;
	private String documentId;
	
	private DocumentPage page;
	
	public DocumentBookmark(DocumentPage page, int pageIndex, int sentenceIndex, Ini bookmarkIni, String documentId) {
		this.page = page;
		this.pageIndex = pageIndex;
		this.documentId = documentId;
		this.bookmarkIni = bookmarkIni;
		this.sentenceIndex = sentenceIndex;
	}
	
	public DocumentPage getPage() {
		return page;
	}

	public void setPage(DocumentPage page) throws IOException {
		updateBookmarkIni();
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
		updateBookmarkIni();
		this.sentenceIndex = sentenceIndex;
	}
	
	private void updateBookmarkIni() throws IOException {
		bookmarkIni.put(documentId, "pageIndex", pageIndex);
		bookmarkIni.put(documentId, "sentenceIndex", sentenceIndex);
		// todo fix storing
//		bookmarkIni.store();
	}
}
