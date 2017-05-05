package io.github.sidf.documentreader.reader;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.sidf.documentreader.document.Page;
import io.github.sidf.documentreader.util.enums.Speed;
import io.github.sidf.documentreader.document.Document;
import io.github.sidf.documentreader.util.enums.Language;

public abstract class Reader implements Runnable {
	private Speed speed;
	private boolean reading;
	private Language language;
	private Document document;
	private static Logger logger = Logger.getLogger(Reader.class.getName());
	
	public void setLanguage(Language language) throws IOException {
		if (getSupportedLanguages().contains(language.getDisplayName())) {
			String message = String.format("The reader does not support %s", language.getDisplayName());
			throw new IOException(message);
		}
		
		this.language = language;
	}
	
	public void setSpeed(Speed speed) throws IOException {
		if (getSupportedSpeed().contains(speed.getDisplayName())) {
			String message = String.format("The reader does not support %s speed", speed.getDisplayName());
			throw new IOException(message);
		}
		
		this.speed = speed;
	}
	
	public void setDocument(Document document) {
		this.document = document;
	}
	
	public Document getDocument() {
		return document;
	}

	public Language getLanguage() {
		return language;
	}
	
	public Speed getSpeed() {
		return speed;
	}

	@Override
	public void run() {
		readerLoop();
	}
	
	private void readerLoop() {
		reading = true;
		
		logger.info("Entered reader loop");
		
		int pageIndex = 0;
		
		outerLoop:
		for (Page page : document) {
			logger.info(String.format("Reading page with the session index of %d", pageIndex));
			for (String sentence : page) {
				try {
					read(sentence);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Couldn't read sentence", e);
					throw new RuntimeException(e.getMessage());
				}
				
				if (!reading) {
					break outerLoop;
				}
			}
			pageIndex++;
		}
		
		document.postReadingOperations();
		stop();
	}
	
	public void stop() {
		reading = false;
		
		try {
			stopInternal();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not stop fully stop the reader", e);
		}
	}
	
	public boolean isReading() {
		return reading;
	}
	
	public abstract List<String> getSupportedSpeed();
	public abstract List<String> getSupportedLanguages();
	abstract void stopInternal() throws Exception;
	abstract void read(String text) throws Exception;
}
