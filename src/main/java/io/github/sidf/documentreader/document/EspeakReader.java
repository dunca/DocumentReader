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
	private Map<Language, String> languageMap = new HashMap<>();
	
	public EspeakReader(Document document) throws Exception {
		super(document);
		
		if (Device.getOperatingSystem() == OperatingSystem.LINUX) {
			espeakPath = "/usr/bin/espeak";
		} else {
			String programFilesPath = System.getenv("ProgramFiles");
			String programFiles86Path = programFilesPath += " (x86)";
			espeakPath =  new File(programFiles86Path).exists() ? programFiles86Path + "/eSpeak/command_line/espeak.exe" : programFilesPath + "/eSpeak/command_line/espeak.exe";
		}
		
		speedMap.put(Speed.FAST, "200");
		speedMap.put(Speed.MEDIUM, "150");
		speedMap.put(Speed.SLOW, "100");
		
		languageMap.put(Language.HUNGARIAN, "hu");
		languageMap.put(Language.ROMANIAN, "ro");
		languageMap.put(Language.ENGLISH, "en");
		languageMap.put(Language.SPANISH, "es");
		languageMap.put(Language.FRENCH, "fr");
	}

	@Override
	// todo only add the shell if running on linux; run espeak's output though aplay to avoid rpi lag
	public void read(String text) throws Exception {
		Process process = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", String.format("%s -s %s -v %s \"%s\"", espeakPath, speedMap.get(getSpeed()), languageMap.get(getLanguage()), text)});
		process.waitFor();
	}

	@Override
	public Language[] getSupportedLanguages() {
		return languageMap.keySet().toArray(new Language[0]);
	}
	
	@Override
	public Speed[] getSupportedSpeed() {
		return speedMap.keySet().toArray(new Speed[0]);
	}
}
