package io.github.sidf.documentreader.document;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.sidf.documentreader.util.ArrayUtil;
import io.github.sidf.documentreader.util.enums.Speed;
import io.github.sidf.documentreader.util.enums.Language;

public abstract class Reader implements Runnable {
	private Speed speed;
	private Language language;
	private Document document;
	private File isReadingPath;
	private boolean isStillRunning;
	private static Logger logger = Logger.getLogger(Reader.class.getName());
	
	public Reader(Document document, File isReadingPath) throws Exception {
		this.document = document;
		this.isReadingPath = isReadingPath;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
	public Language getLanguage() {
		return language;
	}
	
	public void setLanguage(Language language) throws IOException {
		if (!ArrayUtil.arrayContains(getSupportedLanguages(), language.getDisplayName())) {
			String message = String.format("The reader does not support %s", language.getDisplayName());
			throw new IOException(message);
		}
		
		this.language = language;
	}
	
	public Speed getSpeed() {
		return speed;
	}
	
	public void setSpeed(Speed speed) throws IOException {
		if (!ArrayUtil.arrayContains(getSupportedSpeed(), speed.getDisplayName())) {
			String message = String.format("The reader does not support %s speed", speed.getDisplayName());
			throw new IOException(message);
		}
		
		this.speed = speed;
	}

	@Override
	public void run() {
		readerLoop();
	}
	
	private void readerLoop() {
		isStillRunning = true;
		
		logger.info("Entered reader loop");
		
		int pageIndex = 0;
		
		try {
			isReadingPath.createNewFile();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Could not create the file that represents the reading status", e);
		}
		
		outerLoop:
		for (DocumentPage page : document) {
			logger.info(String.format("Reading page with the session index of %d", pageIndex));
			for (String sentence : page) {
				try {
					read(sentence);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Couldn't read sentence", e);
					throw new RuntimeException(e.getMessage());
				}
				
				if (!isStillRunning) {
					break outerLoop;
				}
			}
			pageIndex++;
		}
		
		document.postReadingOperations();
		stop();
	}
	
	public void stop() {
		isStillRunning = false;
		
		try {
			stopInternal();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not stop fully stop the reader", e);
		}
		
		isReadingPath.delete();
	}
	
	public abstract String[] getSupportedSpeed();
	public abstract String[] getSupportedLanguages();
	public abstract void stopInternal() throws Exception;
	public abstract void read(String text) throws Exception;
}
