package io.github.sidf.documentreader.document;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.sidf.documentreader.util.Language;

public abstract class Reader implements Runnable {
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
	
	public abstract Language[] getSupportedLanguages();
	public abstract void read(String text) throws Exception;
}
