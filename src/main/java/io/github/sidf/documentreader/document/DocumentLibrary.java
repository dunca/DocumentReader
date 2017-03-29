package io.github.sidf.documentreader.document;

import java.io.File;
import java.util.List;
import java.io.FileFilter;
import java.util.ArrayList;
import java.io.IOException;
import java.util.logging.Logger;
import java.io.FileNotFoundException;

public class DocumentLibrary implements AutoCloseable {
	private File libraryPath;
	private List<Document> documents = new ArrayList<Document>();
	private static Logger logger = Logger.getLogger(DocumentLibrary.class.getName());
	
	public DocumentLibrary(File libraryPath) throws FileNotFoundException {
		if (!libraryPath.exists() || !libraryPath.isDirectory()) {
			throw new FileNotFoundException(String.format("%s is not a directory", libraryPath));
		}
		
		this.libraryPath = libraryPath;
		update();
	}
	
	public void clear() {
		documents.clear();
	}
	
	public void update() {
		// TODO make the implementation a bit more dynamic
		for (File file : libraryPath.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".pdf");
			}
		})) { 
			try {
				documents.add(new PdfDocument(file));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// ignore document for now
			}
		}
	}
	
	public void delete() {
		for (Document document : documents) {
			document.delete();
		}
	}

	public Document getDocumentById(String id) throws IOException {
		for (Document document : documents) {
			if (document.getDocumentId().equals(id)) {
				return document;
			}
		}
		
		String message = String.format("No document with id %s found in the library", id);
		logger.severe(message);
		
		throw new IOException(message);
	}
	
	public File getLibraryPath() {
		return libraryPath;
	}
	
	@Override
	public void close() throws Exception {
		for (Document document : documents) {
			document.close();
		}
	}
}
