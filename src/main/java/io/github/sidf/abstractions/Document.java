package io.github.sidf.abstractions;

import java.io.File;
import java.util.Iterator;
import java.io.IOException;
import java.io.FileNotFoundException;

import io.github.sidf.Page;
import io.github.sidf.Bookmark;
import io.github.sidf.DocumentIterator;
import io.github.sidf.exceptions.InvalidDocumentException;

public abstract class Document implements AutoCloseable, Iterable<Page> {
	private File file;
	private int pageCount;
	private Bookmark bookmark;
	private String documentPath;
	private String documentName;
	private Page currentPage;
	
	public Document(String filePath) throws Exception {
		file = new File(filePath);
		
		if (!file.exists()) {
			throw new FileNotFoundException(String.format("%s does not exist", filePath));
		} else if (file.isDirectory()) {
			throw new InvalidDocumentException(String.format("%s is not a file", filePath));
		}
		
		bookmark = new Bookmark();
		
		documentPath = filePath;
		documentName = file.getName();
	}
	
	public int getPageCount() {
		return pageCount;
	}
	
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	
	public String getDocumentPath() {
		return documentPath;
	}
	
	public String getDocumentName() {
		return documentName;
	}
	
	public Bookmark getBookmark() {
		return bookmark;
	}
	
	public File getFile() {
		return file;
	}
	
	public Page getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(Page page) {
		this.currentPage = page;
	}
	
	public void delete() {
		file.delete();
	}
	
	public Iterator<Page> iterator() {
		return new DocumentIterator(this);
	}
	
	public abstract Page getNextPage() throws IOException;
	public abstract void close() throws Exception;
}
