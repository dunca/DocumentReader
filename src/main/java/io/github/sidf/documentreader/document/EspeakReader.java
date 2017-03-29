package io.github.sidf.documentreader.document;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import io.github.sidf.documentreader.system.Device;
import io.github.sidf.documentreader.util.Language;
import io.github.sidf.documentreader.system.enums.OperatingSystem;

public class EspeakReader extends Reader {
	private String espeakPath;
	private Map<Language, String> languages = new HashMap<>();
	
	public EspeakReader(DocumentPage page) throws Exception {
		super(page);
		
		if (Device.getOperatingSystem() == OperatingSystem.LINUX) {
			espeakPath = "/usr/bin/espeak";
		} else {
			String programFilesPath = System.getenv("ProgramFiles");
			String programFiles86Path = programFilesPath += " (x86)";
			espeakPath =  new File(programFiles86Path).exists() ? programFiles86Path : programFilesPath + "/eSpeak/command_line/espeak.exe";
		}
		
		languages.put(Language.HUNGARIAN, "hu");
		languages.put(Language.ROMANIAN, "en");
		languages.put(Language.ENGLISH, "en");
		languages.put(Language.SPANISH, "es");
		languages.put(Language.FRENCH, "fr");
	}

	@Override
	public void read(String text) throws IOException {
		Runtime.getRuntime().exec(String.format("%s -v %s \"%s\"", espeakPath, languages.get(getLanguage()), text));
	}

	@Override
	public Language[] getSupportedLanguages() {
		return (Language[]) languages.keySet().toArray();
	}
}
