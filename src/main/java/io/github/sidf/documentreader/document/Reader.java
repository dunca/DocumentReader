package io.github.sidf.documentreader.document;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.sidf.documentreader.util.ArrayUtil;
import io.github.sidf.documentreader.util.enums.Language;
import io.github.sidf.documentreader.util.enums.Speed;

public abstract class Reader implements Runnable {
	private Speed speed;
	private Language language;
	private DocumentPage page;
	private boolean isStillRunning;
	private static Logger logger = Logger.getLogger(Reader.class.getName());
	
	public Reader(DocumentPage page) throws Exception {
		this.page = page;
	}

	public DocumentPage getPage() {
		return page;
	}

	public void setPage(DocumentPage page) {
		this.page = page;
	}
	
	public Language getLanguage() {
		return language;
	}
	
	public void setLanguage(Language language) throws IOException {
		if (!ArrayUtil.arrayContains(getSupportedLanguages(), language)) {
			String message = String.format("The reader does not support %s", language.getDisplayName());
			throw new IOException(message);
		}
		
		this.language = language;
	}
	
	public Speed getSpeed() {
		return speed;
	}
	
	public void setSpeed(Speed speed) throws IOException {
		if (!ArrayUtil.arrayContains(getSupportedSpeed(), speed)) {
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
		
		for (String sentence : page) {
			try {
				read(sentence);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Couldn't read sentence", e);
				throw new RuntimeException(e.getMessage());
			}
			
			if (isStillRunning) {
				break;
			}
		}
		
		isStillRunning = false;
	}
	
	public void stop() {
		isStillRunning = false;
	}
	
	public abstract Speed[] getSupportedSpeed();
	public abstract Language[] getSupportedLanguages();
	public abstract void read(String text) throws Exception;
}
