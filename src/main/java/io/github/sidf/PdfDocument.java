package io.github.sidf;

import org.apache.pdfbox.pdmodel.PDDocument;

import io.github.sidf.abstractions.Document;

public class PdfDocument extends Document {
	private PDDocument pdfDocument;
	
	public PdfDocument(String path) throws Exception {
		super(path);
		
		pdfDocument = PDDocument.load(getFile());
		setPageCount(pdfDocument.getNumberOfPages());
	}
}
