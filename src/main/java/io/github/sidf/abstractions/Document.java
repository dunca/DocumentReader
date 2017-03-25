package io.github.sidf.abstractions;

import java.io.File;
import java.io.FileNotFoundException;

import io.github.sidf.Bookmark;
import io.github.sidf.exceptions.InvalidDocumentException;

public abstract class Document implements AutoCloseable {
	private File file;
	private int pageCount;
	private Bookmark bookmark;
	private String documentPath;
	private String documentName;
	
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
	
	public void delete() {
		file.delete();
	}

	public abstract void close() throws Exception;
}
