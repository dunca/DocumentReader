package io.github.sidf.documentreader.document;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class DocumentBookmark {
	private int pageIndex;
	private Ini bookmarkIni;
	private int sentenceIndex;
	private String documentId;
	private DocumentPage page;
	private static Logger logger = Logger.getLogger(DocumentBookmark.class.getName());
	
	public DocumentBookmark(DocumentPage page, int pageIndex, int sentenceIndex, File bookmarkIniFilePath, String documentId) throws InvalidFileFormatException, IOException {
		this.page = page;
		this.pageIndex = pageIndex;
		this.documentId = documentId;
		this.sentenceIndex = sentenceIndex;
		this.bookmarkIni = new Ini(bookmarkIniFilePath);
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
		this.sentenceIndex = sentenceIndex;
		updateBookmarkIni();
	}
	
	private void updateBookmarkIni() throws IOException {
		bookmarkIni.put(documentId, "pageIndex", pageIndex);
		bookmarkIni.put(documentId, "sentenceIndex", sentenceIndex);
		bookmarkIni.store();
		
		logger.info(String.format("Updated bookmark history file with pageIndex: %d and sentenceIndex: %d", pageIndex, sentenceIndex));
	}
}
