package io.github.sidf.documentreader.document;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfDocument extends Document {
	private PDDocument pdfDocument;
	
	public PdfDocument(File file, File bookmarkIniFilePath) throws Exception {
		super(file, bookmarkIniFilePath);
		
		pdfDocument = PDDocument.load(getFile());
		setPageCount(pdfDocument.getNumberOfPages());
	}
	
	@Override
	public void close() throws IOException {
		pdfDocument.close();
	}

	@Override
	public DocumentPage fetchPage(int index) throws IOException {
		PDFTextStripper textStripper = new PDFTextStripper();
		textStripper.setStartPage(index + 1);
		textStripper.setEndPage(index + 1);
		
		DocumentPage page = new DocumentPage(getBookmark(), textStripper.getText(pdfDocument));
		return page;
	}
}
