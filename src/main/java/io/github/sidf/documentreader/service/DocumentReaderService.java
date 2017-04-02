package io.github.sidf.documentreader.service;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;

import io.github.sidf.documentreader.system.Device;
import io.github.sidf.documentreader.system.Lighting;
import io.github.sidf.documentreader.document.Reader;
import io.github.sidf.documentreader.util.enums.Speed;
import io.github.sidf.documentreader.document.Document;
import io.github.sidf.documentreader.system.AccessPoint;
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
	
	public void setReader(String readerName) throws Exception {
		readerInstance = ReaderFactory.getInstance(readerName, document);
	}
	
	public void startReading(boolean featureDetectionEnabled) throws IOException {
		readerThread = new Thread(readerInstance);
		readerThread.start();
		
		if (featureDetectionEnabled) {
			featureDetectorInstance = FeatureDetector.getInstance();
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
	
	public void setReaderSpeed(Speed speed) throws IOException {
		readerInstance.setSpeed(speed);
	}
	
	public Speed getCurrentReaderSpeed() {
		return readerInstance.getSpeed();
	}
	
	public void setCurrentReaderLanguage(Language language) throws IOException {
		readerInstance.setLanguage(language);
	}
	
	public Language getCurrentReaderLanguage() {
		return readerInstance.getLanguage();
	}
	
	public String[] getReaderProviders() {
		return ReaderFactory.getReaderProviders();
	}

	public Language[] getCurrentSupportedLanguages() {
		return readerInstance.getSupportedLanguages();
	}
	
	public Speed[] getCurrentSupportedSpeed() {
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
}