package io.github.sidf.documentreader.service;

import java.io.File;
import java.util.Map;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.github.sidf.documentreader.reader.Reader;
import io.github.sidf.documentreader.system.Device;
import io.github.sidf.documentreader.system.Lighting;
import io.github.sidf.documentreader.util.enums.Speed;
import io.github.sidf.documentreader.document.Document;
import io.github.sidf.documentreader.util.enums.Language;
import io.github.sidf.documentreader.reader.ReaderFactory;
import io.github.sidf.documentreader.document.DocumentLibrary;
import io.github.sidf.documentreader.featuredetection.FeatureDetector;

public class DocumentReaderService {
	private static String readerName;
	private static Speed readerSpeed;
	private static Language readerLanguage;
	
	private static Document document;
	private static DocumentLibrary documentLibrary;
	
	private static Thread readerThread;
	private static Thread lightingThread;
	private static Thread featureDetectionThread;
	
	private static Reader readerInstance;
	private static Lighting lightingInstance;
	private static FeatureDetector featureDetectorInstance;
	
	public DocumentReaderService(File libraryPath, File bookmarkFilePath) throws FileNotFoundException {
		documentLibrary = new DocumentLibrary(libraryPath, bookmarkFilePath);
	}
	
	public void setDocument(String documentId) throws IOException {
		document = documentLibrary.getDocumentById(documentId);
	}
	
	public Map<String, String> getDocumentMap() {
		return documentLibrary.getDocumentMap();
	}
	
	public void setCurrentReader(String readerName) {
		DocumentReaderService.readerName = readerName;
	}
	
	public void startReading(boolean featureDetectionEnabled) throws Exception {
		readerInstance = ReaderFactory.getInstance(readerName, document, readerLanguage, readerSpeed);
		readerThread = new Thread(readerInstance);
		readerThread.start();
		
		if (featureDetectionEnabled) {
			featureDetectorInstance = new FeatureDetector();
			featureDetectionThread = new Thread(featureDetectorInstance);
			featureDetectionThread.start();
			
			lightingInstance = new Lighting();
			lightingThread = new Thread(lightingInstance);
			lightingThread.start();
			
			ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					if (!readerThread.isAlive()) {
						featureDetectorInstance.stop();
						commonExecutorServiceTasks(scheduledExecutorService);
					} else if (!featureDetectionThread.isAlive()) {
						readerInstance.stop();
						commonExecutorServiceTasks(scheduledExecutorService);
					}
				}
			}, 1000, 500, TimeUnit.MILLISECONDS);
		}
	}
	
	private void commonExecutorServiceTasks(ScheduledExecutorService scheduledExecutorService) {
		lightingInstance.stop();
		scheduledExecutorService.shutdown();
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
	
	public void setCurrentReaderSpeed(String speed) {
		readerSpeed = Speed.fromString(speed);
	}
	
	public void setCurrentReaderLanguage(String language) {
		readerLanguage = Language.fromString(language);
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
	
	public Integer getCurrentDocumentPageCount() {
		if (document == null) {
			return null;
		}
		return document.getPageCount();
	}
	
	public String getCurrentPageContent() {
		return document.getCurrentPageContent();
	}
	
	public int getCurrentPageNumber() {
		return document.getCurrentPageIndex() + 1;
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
		documentLibrary.deleteDocumentById(documentId);
	}
	
	public void resetCurrentDocumentBookmark() {
		document.resetBookmark();
	}
	
	public boolean isReading() {
		return readerInstance.isReading();
	}
}