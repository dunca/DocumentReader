package io.github.sidf.documentreader.service;

import java.awt.print.Pageable;

import io.github.sidf.documentreader.document.Document;
import io.github.sidf.documentreader.document.EspeakReader;
import io.github.sidf.documentreader.document.Reader;

public class DocumentReaderService {
	private static Reader reader;
	private static Document document;
	private static String documentId;

	private static DocumentReaderService instance;
	
	private DocumentReaderService() {
		
	}
	
	public DocumentReaderService getInstance() {
		if (instance == null) {
			instance = new DocumentReaderService();
		}
		
		return instance;
	}
	
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	public void startReading() {

	}
	
	public void stopReading() {
		
	}
	
	public void setVolume(int level) {
		
	}
	
	public int getVolume() {
		return 1;
	}
	
	public void setReaderSpeed(int level) {
		
	}
	
	public int getReaderSpeed() {
		return 1;
	}
	
	public void setReaderLanguage(String language) {
		
	}
	
	public String getReaderLanguage() {
		return "";
	}
}
