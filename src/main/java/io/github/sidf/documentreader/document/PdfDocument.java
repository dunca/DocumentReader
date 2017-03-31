package io.github.sidf.documentreader.document;

import java.io.File;
import java.io.IOException;

import io.github.sidf.documentreader.util.FileUtil;
import io.github.sidf.documentreader.util.CommandUtil;
import io.github.sidf.documentreader.util.CommandResult;

public class PdfDocument extends Document {
	public PdfDocument(File file, File bookmarkIniFilePath) throws Exception {
		super(file, bookmarkIniFilePath);
		
		setPageCount(fetchPageCount());
	}
	
	@Override
	public void close() throws IOException {
		
	}

	@Override
	public DocumentPage fetchPage(int index) throws Exception {
		File tempFile = File.createTempFile("tempContent", null);
		CommandUtil.launchNonBlockingCommand(String.format("pdftotext -layout -nopgbrk -f %d -l %d \"%s\" %s", index + 1, index + 1,
														   getDocumentPath(), tempFile.getPath()));
		
		DocumentPage page = new DocumentPage(getBookmark(), FileUtil.fileToString(tempFile));
		tempFile.delete();
		return page;
	}
	
	private int fetchPageCount() throws IOException, InterruptedException {
		CommandResult commandResult = CommandUtil.launchNonBlockingCommand(String.format("pdfinfo \"%s\" | grep Pages", getDocumentPath()));
		int pageCount = Integer.valueOf(commandResult.getStdout().split("\\s+")[1]);
		return pageCount;
	}
}
