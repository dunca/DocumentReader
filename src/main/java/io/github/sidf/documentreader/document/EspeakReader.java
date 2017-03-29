package io.github.sidf.documentreader.document;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import io.github.sidf.documentreader.system.Device;
import io.github.sidf.documentreader.util.enums.Speed;
import io.github.sidf.documentreader.util.enums.Language;
import io.github.sidf.documentreader.system.enums.OperatingSystem;

public class EspeakReader extends Reader {
	private String espeakPath;
	private Map<Speed, String> speedMap = new HashMap<>();
	private Map<Language, String> languagMap = new HashMap<>();
	
	public EspeakReader(DocumentPage page) throws Exception {
		super(page);
		
		if (Device.getOperatingSystem() == OperatingSystem.LINUX) {
			espeakPath = "/usr/bin/espeak";
		} else {
			String programFilesPath = System.getenv("ProgramFiles");
			String programFiles86Path = programFilesPath += " (x86)";
			espeakPath =  new File(programFiles86Path).exists() ? programFiles86Path : programFilesPath + "/eSpeak/command_line/espeak.exe";
		}
		
		speedMap.put(Speed.FAST, "200");
		speedMap.put(Speed.MEDIUM, "150");
		speedMap.put(Speed.SLOW, "100");
		
		languagMap.put(Language.HUNGARIAN, "hu");
		languagMap.put(Language.ROMANIAN, "en");
		languagMap.put(Language.ENGLISH, "en");
		languagMap.put(Language.SPANISH, "es");
		languagMap.put(Language.FRENCH, "fr");
	}

	@Override
	public void read(String text) throws IOException {
		Runtime.getRuntime().exec(String.format("%s -s %s -v %s \"%s\"", espeakPath, languagMap.get(getLanguage()), speedMap.get(getSpeed()), text));
	}

	@Override
	public Language[] getSupportedLanguages() {
		return (Language[]) languagMap.keySet().toArray();
	}
	
	@Override
	public Speed[] getSupportedSpeed() {
		return (Speed[]) speedMap.keySet().toArray();
	}
}
