package io.github.sidf.documentreader.document;

import java.io.File;
import java.util.List;
import java.io.FileFilter;
import java.util.ArrayList;
import java.io.FileNotFoundException;

public class DocumentLibrary implements AutoCloseable {
	private File libraryPath;
	private List<Document> documentStorage = new ArrayList<Document>();
	
	public DocumentLibrary(File libraryPath) throws FileNotFoundException {
		if (!libraryPath.exists() || !libraryPath.isDirectory()) {
			throw new FileNotFoundException(String.format("%s is not a directory", libraryPath));
		}
		
		this.libraryPath = libraryPath;
		update();
	}
	
	public void clear() {
		if (documentStorage != null) {
			documentStorage.clear();
		}
	}
	
	public void update() {
		// TODO make the implementation a bit more dynamic
		for (File file : libraryPath.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".pdf");
			}
		})) { 
			try {
				documentStorage.add(new PdfDocument(file));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// ignore document for now
			}
		}
	}
	
	public void delete() {
		for (Document document : documentStorage) {
			document.delete();
		}
	}

	public void close() throws Exception {
		for (Document document : documentStorage) {
			document.close();
		}
	}
}
