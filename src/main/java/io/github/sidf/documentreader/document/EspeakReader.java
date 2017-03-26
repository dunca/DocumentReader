package io.github.sidf.documentreader.document;

import java.io.File;
import java.io.IOException;

import io.github.sidf.documentreader.system.Device;
import io.github.sidf.documentreader.system.OperatingSystem;

public class EspeakReader extends Reader {
	private String espeakPath;
	
	public EspeakReader(DocumentPage page) throws Exception {
		super(page);
		
		if (Device.getOperatingSystem() == OperatingSystem.LINUX) {
			espeakPath = "/usr/bin/espeak";
		} else {
			String programFilesPath = System.getenv("ProgramFiles");
			String programFiles86Path = programFilesPath += " (x86)";
			espeakPath =  new File(programFiles86Path).exists() ? programFiles86Path : programFilesPath + "/eSpeak/command_line/espeak.exe";
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
