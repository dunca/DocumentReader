package io.github.sidf.documentreader.document;

import java.io.File;
import java.util.List;
//import java.io.FileFilter;
import java.util.ArrayList;

import java.io.FileNotFoundException;

public class DocumentLibrary {
	private File libraryPath;
	private List<Document> storage = new ArrayList<Document>();
	
	public DocumentLibrary(File libraryPath) throws FileNotFoundException {
		if (!libraryPath.exists() || !libraryPath.isDirectory()) {
			throw new FileNotFoundException(String.format("%s is not a directory", libraryPath));
		}
		
		this.libraryPath = libraryPath;
	}
	
	public void clear() {
		if (storage != null) {
			storage.clear();
		}
	}
	
//	public void update() {
//		for (File file : libraryPath.listFiles(new FileFilter() {
//			public boolean accept(File pathname) {
//				return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".pdf");
//			}
//		})) { 
//			try {
//				storage.add(new PdfDocument(file));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				// ignore document for now
//			}
//		}
//	}
}
