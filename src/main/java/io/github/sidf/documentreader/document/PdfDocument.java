package io.github.sidf.documentreader.document;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfDocument extends Document {
	private PDDocument pdfDocument;
	
	public PdfDocument(File file) throws Exception {
		super(file);
		
		pdfDocument = PDDocument.load(getFile());
		setPageCount(pdfDocument.getNumberOfPages());
	}
	
	@Override
	public void close() throws IOException {
		pdfDocument.close();
	}

	@Override
	public DocumentPage getNextPage() throws IOException {
		int currentIndex = getBookmark().getPageIndex();
		int newIndex = currentIndex + 1;
		
		PDFTextStripper textStripper = new PDFTextStripper();
		textStripper.setStartPage(newIndex + 1);
		textStripper.setEndPage(newIndex + 1);
		
		DocumentPage page = new DocumentPage(getBookmark(), textStripper.getText(pdfDocument), newIndex + 1);

		currentIndex = newIndex;
		return page;
	}
}
