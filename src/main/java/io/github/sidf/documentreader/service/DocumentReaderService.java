package io.github.sidf.documentreader.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.io.FileNotFoundException;

import io.github.sidf.documentreader.system.Device;
import io.github.sidf.documentreader.document.Reader;
import io.github.sidf.documentreader.util.enums.Speed;
import io.github.sidf.documentreader.document.Document;
import io.github.sidf.documentreader.util.enums.Language;
import io.github.sidf.documentreader.document.ReaderFactory;
import io.github.sidf.documentreader.document.DocumentLibrary;

public class DocumentReaderService implements Runnable {
	private static Reader reader;
	private static Document document;
	private static DocumentReaderService instance;
	private static boolean featureDetectionEnabled;
	private static DocumentLibrary documentLibrary;
	
	
	private DocumentReaderService(File libraryPath) throws FileNotFoundException {
		documentLibrary = new DocumentLibrary(libraryPath);
	}
	
	public DocumentReaderService getInstance(File libraryPath) throws FileNotFoundException {
		if (instance == null) {
			instance = new DocumentReaderService(libraryPath);
		} else if (documentLibrary.getLibraryPath() != libraryPath) {
			throw new RuntimeException("Cannot re-instantiate the class with a new library path");
		}
		
		return instance;
	}
	
	public void setDocument(String documentId) throws IOException {
		document = documentLibrary.getDocumentById(documentId);
	}
	
	public Map<String, String> getDocumentNameMap() {
		return documentLibrary.getDocumentNameMap();
	}
	
	public void setReader(String readerName) throws Exception {
		reader = ReaderFactory.getInstance(readerName, document.getBookmark().getPage());
	}
	
	public void startReading() {

	}
	
	public void stopReading() {
		
	}
	
	public void setAudioVolume(int level) throws Exception {
		Device.setVolume(level);
	}
	
	public int getAudioVolume() throws Exception {
		return Device.getVolume();
	}
	
	public void setReaderSpeed(Speed speed) throws IOException {
		reader.setSpeed(speed);
	}
	
	public Speed getReaderSpeed() {
		return reader.getSpeed();
	}
	
	public void setReaderLanguage(Language language) throws IOException {
		reader.setLanguage(language);
	}
	
	public Language getReaderLanguage() {
		return reader.getLanguage();
	}
	
	public void setFeatureDetection(boolean enabled) {
		featureDetectionEnabled = enabled;
	}
	
	public boolean getFeatureDetection() {
		return featureDetectionEnabled;
	}
	
	public List<String> getReaderProviders() {
		return ReaderFactory.getReaderProviders();
	}

	public Language[] getSupportedLanguages() {
		return reader.getSupportedLanguages();
	}
	
	public Speed[] getSupportedSpeed() {
		return reader.getSupportedSpeed();
	}

	public void updateLibrary() {
		documentLibrary.update();
	}
	
	public void shutDown() throws IOException {
		Device.shutDown();
	}
	
	@Override
	public void run() {
		startReading();
	}
}