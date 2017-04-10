package io.github.sidf.documentreader.document;

import java.io.File;
import org.ini4j.Ini;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.InvalidObjectException;

import io.github.sidf.documentreader.util.FileUtil;

public abstract class Document implements Iterable<DocumentPage> {
	private File file;
	private String documentId;
	private String documentPath;
	private String documentName;
	private static Ini bookmarkIni;
	private DocumentBookmark bookmark;
	private static Logger logger = Logger.getLogger(Document.class.getName());
	
	public Document(File file, File bookmarkIniFilePath) throws Exception {
		this.file = file;
		
		String filePath = file.getPath();
		
		if (!file.exists()) {
			throw new FileNotFoundException(String.format("%s does not exist", filePath));
		} else if (file.isDirectory()) {
			throw new InvalidObjectException(String.format("%s is not a file", filePath));
		}
		
		if (bookmarkIni == null) {
			bookmarkIni = new Ini(bookmarkIniFilePath);
		}
		
		documentPath = file.getPath();
		documentName = file.getName();
		documentId = FileUtil.getMd5Hash(documentPath);
		
		int pageIndex = 0;
		int sentenceIndex = 0;
		if (bookmarkIni.containsKey(documentId)) {
			pageIndex = Integer.valueOf(bookmarkIni.get(documentId, "pageIndex"));
			sentenceIndex = Integer.valueOf(bookmarkIni.get(documentId, "sentenceIndex"));
		}
		
		bookmark = new DocumentBookmark(null, pageIndex, sentenceIndex, bookmarkIni, this);
	}
		
	public String getDocumentId() {
		return documentId;
	}
	
	public String getDocumentPath() {
		return documentPath;
	}
	
	public String getDocumentName() {
		return documentName;
	}
	
	public DocumentBookmark getBookmark() {
		return bookmark;
	}
	
	public void delete() {
		resetBookmark();
		file.delete();
	}
	
	public String getCurrentPageContent() {
		return bookmark.getPage().getContent();
	}
	
	public int getCurrentPageIndex() {
		return bookmark.getPageIndex();
	}
	
	public Iterator<DocumentPage> iterator() {
		return new DocumentIterator(this);
	}
	
	public DocumentPage updateBookmarkPage(int index, int sentenceIndex) throws Exception {
		DocumentPage page = fetchPage(index);

		bookmark.setPage(page);
		bookmark.setPageIndex(index);
		bookmark.setSentenceIndex(sentenceIndex);
		
		return page;
	}
	
	private void wrapBookmark() throws Exception {
		if (bookmark.endReached()) {
			updateBookmarkPage(0, 0);
		}
	}
	
	public void resetBookmark() {
		try {
			updateBookmarkPage(0, 0);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not reset bookmark", e);
		}
	}
	
	public void postReadingOperations() {
		try {
			wrapBookmark();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not perform post reading operations" , e);
		}
	}
	
	public abstract int getPageCount();
	public abstract void setPageCount(int pageCount);
	public abstract DocumentPage fetchPage(int index) throws Exception;
}
