package io.github.sidf.documentreader.document;

import java.io.File;
import java.util.Iterator;

import io.github.sidf.documentreader.util.FileUtil;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.FileNotFoundException;

public abstract class Document implements AutoCloseable, Iterable<DocumentPage> {
	private File file;
	private int pageCount;
	private String documentId;
	private String documentPath;
	private String documentName;
	private DocumentBookmark bookmark;
	
	public Document(File file) throws Exception {
		this.file = file;
		
		String filePath = file.getPath();
		
		if (!file.exists()) {
			throw new FileNotFoundException(String.format("%s does not exist", filePath));
		} else if (file.isDirectory()) {
			throw new InvalidObjectException(String.format("%s is not a file", filePath));
		} 
		
		documentPath = file.getPath();
		documentName = file.getName();
		documentId = FileUtil.getMd5Hash(documentPath);
		bookmark = new DocumentBookmark(fetchNextPage(), -1, 0);
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
	
	public File getFile() {
		return file;
	}
	
	public void delete() {
		file.delete();
	}
	
	public Iterator<DocumentPage> iterator() {
		return new DocumentIterator(this);
	}
	
	public DocumentPage nextPage() throws IOException {
		DocumentPage page = fetchNextPage();
		
		getBookmark().setPage(page);
		bookmark.setCharacterIndex(0);
		bookmark.setPageIndex(bookmark.getPageIndex() + 1);
		
		return page;
	}
	
	public abstract void close() throws Exception;
	public abstract DocumentPage fetchNextPage() throws IOException;
}
