package io.github.sidf.documentreader.document;

import java.io.File;
import java.io.IOException;

public class EspeakReader extends Reader {
	private String espeakPath;
	
	public EspeakReader(DocumentPage page) throws Exception {
		super(page);
		
		if (System.getProperty("os.name").contains("Linux")) {
			espeakPath = "/usr/bin/espeak";
		} else {
			espeakPath = System.getenv("ProgramFiles") + " (x86)/eSpeak/command_line/espeak.exe";
		}
		
		File espeakFile = new File(espeakPath);
		
		if (!espeakFile.exists()) {
			throw new IOException(String.format("Path %s does not exist", espeakPath));
		}
	}

	@Override
	public void read(String text) throws IOException {
		Runtime.getRuntime().exec(String.format("%s \"%s\"", espeakPath, text));
	}
}
