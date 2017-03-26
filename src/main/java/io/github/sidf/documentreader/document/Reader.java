package io.github.sidf.documentreader.document;


public class Reader implements Runnable {
	public DocumentPage page;
	
	public Reader(DocumentPage page) {
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
		// iterate over page and read
	}
	
	public void stop() {
		// stop reading
	}
}
