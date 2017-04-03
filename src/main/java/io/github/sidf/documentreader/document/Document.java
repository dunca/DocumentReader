package io.github.sidf.documentreader.document;

import java.io.File;
import java.util.Map;
import org.ini4j.Ini;
import java.util.HashMap;
import java.util.Iterator;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.InvalidObjectException;

import io.github.sidf.documentreader.util.FileUtil;

public abstract class Document implements AutoCloseable, Iterable<DocumentPage> {
	private File file;
	private int pageCount;
	private String documentId;
	private String documentPath;
	private String documentName;
	private static Ini bookmarkIni;
	private DocumentBookmark bookmark;
	private static Map<String, String[]> bookmarkIniMap;
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
			bookmarkIni = new Ini(new FileReader(bookmarkIniFilePath));
		}
		
		if (bookmarkIniMap == null) {
			bookmarkIniMap = parseBookmarkIniFile(bookmarkIniFilePath);
		}
		
		documentPath = file.getPath();
		documentName = file.getName();
		documentId = FileUtil.getMd5Hash(documentPath);
		
		int pageIndex = 0;
		int sentenceIndex = 0;
		if (bookmarkIniMap.containsKey(documentId)) {
			String[] info = bookmarkIniMap.get(documentId);
			pageIndex = Integer.valueOf(info[0]);
			sentenceIndex = Integer.valueOf(info[1]);
		}
		
		bookmark = new DocumentBookmark(null, pageIndex, sentenceIndex, bookmarkIniFilePath, this);
	}
	
	public int getPageCount() {
		return pageCount;
	}
	
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
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
	
	public void setBookmark(DocumentBookmark bookmark) {
		this.bookmark = bookmark;
	}
	
	public File getFile() {
		return file;
	}
	
	public void delete() {
		file.delete();
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
	
	private static Map<String, String[]> parseBookmarkIniFile(File bookmarkIniFile) {
		Map<String, String[]> map = new HashMap<>();
		
		for (String hash : bookmarkIni.keySet()) {
			map.put(hash, new String[] { bookmarkIni.get(hash, "pageIndex"), bookmarkIni.get(hash, "sentenceIndex") });
		}
		
		return map;
	}
	
	private void wrapBookmark() throws Exception {
		if (bookmark.endReached()) {
			updateBookmarkPage(0, 0);
		}
	}
	
	public void postReadingOperations() {
		try {
			wrapBookmark();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not perform post reading operations" , e);
		}
	}
	
	public abstract void close() throws Exception;
	public abstract DocumentPage fetchPage(int index) throws Exception;
}
