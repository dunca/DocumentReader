package io.github.sidf.documentreader.document;

public abstract class Reader implements Runnable {
	private DocumentPage page;
	private boolean isStillRunning;
	
	public Reader(DocumentPage page) throws Exception {
		this.page = page;
	}

	public DocumentPage getPage() {
		return page;
	}

	public void setPage(DocumentPage page) {
		this.page = page;
	}

	public void run() {
		readerLoop();
	}
	
	private void readerLoop() {
		isStillRunning = true;
		
		for (String sentence : page) {
			try {
				read(sentence);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
	public abstract void read(String text) throws Exception;
}
