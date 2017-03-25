package io.github.sidf.abstractions;

import java.io.File;
import java.util.Iterator;
import java.io.IOException;
import java.io.FileNotFoundException;

import io.github.sidf.DocumentPage;
import io.github.sidf.DocumentBookmark;
import io.github.sidf.DocumentIterator;
import io.github.sidf.exceptions.InvalidDocumentException;

public abstract class Document implements AutoCloseable, Iterable<DocumentPage> {
	private File file;
	private int pageCount;
	private DocumentBookmark bookmark;
	private String documentPath;
	private String documentName;
	
	public Document(String filePath) throws Exception {
		file = new File(filePath);
		
		if (!file.exists()) {
			throw new FileNotFoundException(String.format("%s does not exist", filePath));
		} else if (file.isDirectory()) {
			throw new InvalidDocumentException(String.format("%s is not a file", filePath));
		}
		
		documentPath = filePath;
		documentName = file.getName();
		bookmark = new DocumentBookmark(getNextPage(), 0);
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
	
	public DocumentBookmark getBookmark() {
		return bookmark;
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
	
	public abstract DocumentPage getNextPage() throws IOException;
	public abstract void close() throws Exception;
}
