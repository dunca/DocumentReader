package io.github.sidf.documentreader.document;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.util.log.Log;

import java.util.function.Consumer;
import java.io.FileNotFoundException;

public class DocumentLibrary implements AutoCloseable {
	private File libraryPath;
	private File currentPagePath;
	private File bookmarkIniFilePath;
	private List<Document> documents = new ArrayList<Document>();
	private static Logger logger = Logger.getLogger(DocumentLibrary.class.getName());
	
	public DocumentLibrary(File libraryPath, File bookmarkFilePath, File currentPagePath) throws FileNotFoundException {
		if (!libraryPath.exists() || !libraryPath.isDirectory()) {
			throw new FileNotFoundException(String.format("%s does not exist as a directory", libraryPath));
		} else if (!bookmarkFilePath.exists()) {
			throw new FileNotFoundException(String.format("%s does not exist as a file", bookmarkFilePath));
		}
		
		this.bookmarkIniFilePath = bookmarkFilePath;
		this.currentPagePath = currentPagePath;
		this.libraryPath = libraryPath;
		update();
	}
	
	public void clear() {
		documents.clear();
	}
	
	public void update() {
		clear();
		Set<File> brokenDocuments = new HashSet<>();
		
		for (File file : libraryPath.listFiles()) {
			for (String documentProvider : DocumentFactory.getDocumentProviders()) {
				Document document = null;
				try {
					document = DocumentFactory.getInstance(documentProvider, file, bookmarkIniFilePath, currentPagePath);
					if (brokenDocuments.contains(file)) {
						brokenDocuments.remove(file);
					}
				} catch (Exception e) {
					brokenDocuments.add(file);
					continue;
				}
				
				documents.add(document);
				logger.info(String.format("Document %s with id %s was added to the library", document.getDocumentName(), 
										  document.getDocumentId()));
			}
		}
		
		if (!brokenDocuments.isEmpty()) {
			for (File file : brokenDocuments) {
				logger.warning(String.format("No document provider was able to initialize %s, deleting", file.getPath()));
				file.delete();
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
	
	public Map<String, String> getDocumentMap() {
		Map<String, String> map = new HashMap<>();
		
		documents.forEach(new Consumer<Document>() {
			@Override
			public void accept(Document document) {
				map.put(document.getDocumentId(), document.getDocumentName());
			}
		});
		
		return map;
	}
	
	@Override
	public void close() throws Exception {
		for (Document document : documents) {
			document.close();
		}
	}

	public boolean deleteDocument(String documentId) {
		for (Document document : documents) {
			if (document.getDocumentId().equals(documentId)) {
				document.delete();
				return true;
			}
		}
		
		return false;
	}
}
