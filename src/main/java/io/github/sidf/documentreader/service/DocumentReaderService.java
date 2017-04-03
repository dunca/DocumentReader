package io.github.sidf.documentreader.service;

import java.io.File;
import java.util.Map;
import java.io.IOException;
import java.io.FileNotFoundException;

import io.github.sidf.documentreader.system.Device;
import io.github.sidf.documentreader.system.Lighting;
import io.github.sidf.documentreader.document.Reader;
import io.github.sidf.documentreader.util.enums.Speed;
import io.github.sidf.documentreader.document.Document;
import io.github.sidf.documentreader.util.enums.Language;
import io.github.sidf.documentreader.document.ReaderFactory;
import io.github.sidf.documentreader.document.DocumentLibrary;
import io.github.sidf.documentreader.featuredetection.FeatureDetector;

public class DocumentReaderService {
	private static Reader readerInstance;
	private static Lighting lightingInstance;
	private static FeatureDetector featureDetectorInstance;
	
	private static Document document;
	private static DocumentLibrary documentLibrary;
	
	private static Thread readerThread;
	private static Thread lightingThread;
	private static Thread featureDetectionThread;
	
	
	public DocumentReaderService(File libraryPath, File bookmarkFilePath) throws FileNotFoundException {
		documentLibrary = new DocumentLibrary(libraryPath, bookmarkFilePath);
	}
	
	public void setDocument(String documentId) throws Exception {
		document = documentLibrary.getDocumentById(documentId);
	}
	
	public Map<String, String> getDocumentMap() {
		return documentLibrary.getDocumentMap();
	}
	
	public void setCurrentReader(String readerName) throws Exception {
		readerInstance = ReaderFactory.getInstance(readerName, document);
	}
	
	public void startReading(boolean featureDetectionEnabled) throws IOException {
		readerThread = new Thread(readerInstance);
		readerThread.start();
		
		if (featureDetectionEnabled) {
			featureDetectorInstance = new FeatureDetector();
			featureDetectionThread = new Thread(featureDetectorInstance);
			featureDetectionThread.start();
			
			lightingInstance = new Lighting();
			lightingThread = new Thread(lightingInstance);
			lightingThread.start();
		}
	}
	
	public void stopReading() {
		if (readerInstance != null) {
			readerInstance.stop();
		}
		
		if (featureDetectorInstance != null) {
			featureDetectorInstance.stop();
			lightingInstance.stop();
		}
	}
	
	public void setAudioVolume(int level) throws Exception {
		Device.setVolume(level);
	}
	
	public int getAudioVolume() throws Exception {
		return Device.getVolume();
	}
	
	public void setCurrentReaderSpeed(String speed) throws IOException {
		readerInstance.setSpeed(Speed.fromString(speed));
	}
	
	public void setCurrentReaderLanguage(String language) throws IOException {
		readerInstance.setLanguage(Language.fromString(language));
	}
	
	public String[] getReaderProviders() {
		return ReaderFactory.getReaderProviders();
	}

	public String[] getCurrentSupportedLanguages() {
		return readerInstance.getSupportedLanguages();
	}
	
	public String[] getCurrentSupportedSpeed() {
		return readerInstance.getSupportedSpeed();
	}

	public void updateDocumentLibrary() {
		documentLibrary.update();
	}
	
	public void shutDownDevice() throws IOException {
		Device.shutDown();
	}
	
	public void rebootDevice() throws IOException {
		Device.reboot();
	}
	
	public void deleteDocument(String documentId) {
		boolean changed = documentLibrary.deleteDocument(documentId);
		
		if (changed) {
			documentLibrary.update();
		}
	}
}