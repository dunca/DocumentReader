package io.github.sidf.documentreader.document;

import java.io.File;
import org.ini4j.Ini;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileNotFoundException;

import io.github.sidf.documentreader.util.IoUtil;

/**
 * Abstract class that represents a document. This is the class that should be extended in order to
 * provide support for different document types.
 * @author sidf
 */
public abstract class Document implements Iterable<Page> {
	/**
	 * The MD5 hash of the document
	 */
	private String id;
	private File file;
	
	/**
	 * Path to the underlying file
	 */
	private String path;
	
	/**
	 * The name of the file, including the extension
	 */
	private String name;
	private Bookmark bookmark;
	private static Ini bookmarkIni;
	
	private static Logger logger = Logger.getLogger(Document.class.getName());
	
	Document(String filePath, File bookmarkFile) throws Exception {	
		file = new File(filePath);
		
		if (!file.isFile()) {
			throw new FileNotFoundException(String.format("%s does not exist as a file", filePath));
		} 
		
		// share the same object between all instances
		if (bookmarkIni == null) {
			bookmarkIni = new Ini(bookmarkFile);
		}
		
		path = filePath;
		name = file.getName();
		id = IoUtil.getMd5Hash(path);
		bookmark = new Bookmark(bookmarkIni, this);
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
	
	/**
	 * Deletes a document and it's bookmark information
	 */
	public void delete() {
		try {
			bookmark.delete();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Couldn't delete bookmark information", e);
		}
		file.delete();
	}
	
	/**
	 * Points the bookmark to the start of the document
	 */
	public void resetBookmark() {
		try {
			bookmark.reset();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Couldn't reset bookmark information", e);
		}
	}
	
	/**
	 * @return the content of the page marked by the bookmark
	 */
	public String getCurrentPageContent() {
		return bookmark.getPage().getContent();
	}
	
	/**
	 * @return the zero-based index of the page marked by the bookmark
	 */
	public int getCurrentPageIndex() {
		return bookmark.getPageIndex();
	}
	
	/**
	 * @return a {@link DocumentIterator} which permits iteration over the document's pages
	 */
	@Override
	public Iterator<Page> iterator() {
		return new DocumentIterator(this);
	}
	
	public void postReadingOperations() {
		try {
			bookmark.wrapAround();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not perform post reading operations" , e);
		}
	}
	
	/**
	 * @return when implemented in a subclass, it should return the total number of pages in the document
	 */
	public abstract int getPageCount();
	
	/**
	 * @param index the 0 based index of a page in the document
	 * @return when implemented in a subclass, it should return a {@link Page} instance determined by the supplied parameter
	 * @throws Exception if an error occurs
	 */
	abstract Page fetchPage(int index) throws Exception;
}
