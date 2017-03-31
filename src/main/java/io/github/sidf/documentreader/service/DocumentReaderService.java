package io.github.sidf.documentreader.service;

import java.awt.image.AreaAveragingScaleFilter;
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
	private static AccessPoint accessPointInstance;
	private static FeatureDetector featureDetectorInstance;
	
	private static Document document;
	private static DocumentReaderService instance;
	private static boolean featureDetectionEnabled;
	private static DocumentLibrary documentLibrary;
	
	private static Thread readerThread;
	private static Thread lightingThread;
	private static Thread featureDetectionThread;
	
	
	public DocumentReaderService(File libraryPath, File bookmarkFilePath) throws FileNotFoundException {
		documentLibrary = new DocumentLibrary(libraryPath, bookmarkFilePath);
	}
	
	public void setDocument(String documentId) throws Exception {
		document = documentLibrary.getDocumentById(documentId);
//		setReader(Reader.class.getName());
	}
	
	public Map<String, String> getDocumentNameMap() {
		return documentLibrary.getDocumentNameMap();
	}
	
	public void setReader(String readerName) throws Exception {
		readerInstance = ReaderFactory.getInstance(readerName, document);
	}
	
	public void startReading() throws IOException {
		readerThread = new Thread(readerInstance);
		
//		lightingInstance = new Lighting();
//		lightingThread = new Thread(lightingInstance);
//		
//		featureDetectorInstance = FeatureDetector.getInstance();
//		featureDetectionThread = new Thread(featureDetectorInstance);
		
		readerThread.start();
//		lightingThread.start();
//		featureDetectionThread.start();
	}
	
	public void stopReading() {
//		featureDetectorInstance.stop();
//		lightingInstance.stop();
		readerInstance.stop();
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
	
	public Speed getReaderSpeed() {
		return readerInstance.getSpeed();
	}
	
	public void setReaderLanguage(Language language) throws IOException {
		readerInstance.setLanguage(language);
	}
	
	public Language getReaderLanguage() {
		return readerInstance.getLanguage();
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
		return readerInstance.getSupportedLanguages();
	}
	
	public Speed[] getSupportedSpeed() {
		return readerInstance.getSupportedSpeed();
	}

	public void updateLibrary() {
		documentLibrary.update();
	}
	
	public void shutDown() throws IOException {
		Device.shutDown();
	}
	
	public void reboot() throws IOException {
		Device.reboot();
	}
	
	public void startAccessPoint(String ipAddress, String hostapdConfigPath) throws Exception {
		if (accessPointInstance == null) {
			accessPointInstance = new AccessPoint(ipAddress, hostapdConfigPath);
		}
		accessPointInstance.start();
	}
	
	public void stopAccessPoint() throws Exception {
		accessPointInstance.close();
	}
	
	public void getSupportedReaders() {
		
	}
	
	public static void main(String[] array) throws Exception {
		File lib = new File(array[0]);
		File bk = new File(array[1]);
		String ip = "192.168.13.37";
		String hostapdConfigPath = array[2];
		
		DocumentReaderService drService = new DocumentReaderService(lib, bk);
//		drService.setDocument("[79, 16, -62, 41, -72, 91, -17, -111, 2, 122, -18, -92, -75, 56, 106, -107]");
//		drService.setDocument("[109, 89, -6, 13, -48, 29, -57, 125, -63, -72, 56, 28, -7, -18, 121, -117]");
		drService.setDocument("[-77, 0, 49, -126, -28, 52, 66, 39, 85, -54, -93, -13, 81, 102, 27, 62]");
		
		drService.setReader("io.github.sidf.documentreader.document.EspeakReader");
		System.out.println("done setting reader");

		drService.setReaderLanguage(Language.ROMANIAN);
		System.out.println("done setting language");
		
		drService.setReaderSpeed(Speed.FAST);
		System.out.println("done setting speed");
		
		drService.startReading();
		System.out.println("started reading");
//		Thread.sleep(20000);
//		drService.stopReading();
//		drService.startAccessPoint(ip, hostapdConfigPath);
	}
}