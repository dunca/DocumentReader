package io.github.sidf.documentreader.document;

import java.io.File;
import org.ini4j.Ini;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.InvalidObjectException;

import io.github.sidf.documentreader.util.FileUtil;

public abstract class Document implements Iterable<Page> {
	private File file;
	private String id;
	private String path;
	private String name;
	private Bookmark bookmark;
	private static Ini bookmarkIni;
	
	private static Logger logger = Logger.getLogger(Document.class.getName());
	
	Document(File file, File bookmarkIniFile) throws Exception {
		this.file = file;
		
		String filePath = file.getPath();
		
		if (!file.exists()) {
			throw new FileNotFoundException(String.format("%s does not exist", filePath));
		} else if (file.isDirectory()) {
			throw new InvalidObjectException(String.format("%s is not a file", filePath));
		}
		
		if (bookmarkIni == null) {
			bookmarkIni = new Ini(bookmarkIniFile);
		}
		
		path = file.getPath();
		name = file.getName();
		id = FileUtil.getMd5Hash(path);
		
		int pageIndex = 0;
		int sentenceIndex = 0;
		if (bookmarkIni.containsKey(id)) {
			pageIndex = Integer.valueOf(bookmarkIni.get(id, "pageIndex"));
			sentenceIndex = Integer.valueOf(bookmarkIni.get(id, "sentenceIndex"));
		}
		
		bookmark = new Bookmark(null, pageIndex, sentenceIndex, bookmarkIni, this);
	}
		
	public String getId() {
		return id;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return name;
	}
	
	Bookmark getBookmark() {
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
	
	public Iterator<Page> iterator() {
		return new DocumentIterator(this);
	}
	
	Page updateBookmarkPage(int index, int sentenceIndex) throws Exception {
		Page page = fetchPage(index);

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
	abstract Page fetchPage(int index) throws Exception;
}
