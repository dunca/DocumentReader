package io.github.sidf;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import io.github.sidf.abstractions.Document;

public class PdfDocument extends Document {
	private PDDocument pdfDocument;
	
	public PdfDocument(String path) throws Exception {
		super(path);
		
		pdfDocument = PDDocument.load(getFile());
		setPageCount(pdfDocument.getNumberOfPages());
	}
	
	@Override
	public void close() throws IOException {
		pdfDocument.close();
	}

	@Override
	public Page getNextPage() throws IOException {
		int currentIndex = getCurrentPage().getIndex();
		int newIndex = currentIndex + 1;
		
		PDFTextStripper textStripper = new PDFTextStripper();
		textStripper.setStartPage(newIndex + 1);
		textStripper.setEndPage(newIndex + 1);
		
		Page page = new Page(textStripper.getText(pdfDocument), newIndex + 1);
		setCurrentPage(page);
		
		currentIndex = newIndex;
		return page;
	}
}
