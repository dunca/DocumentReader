package io.github.sidf.documentreader.document;

import java.util.Map;
import java.util.HashMap;

import io.github.sidf.documentreader.util.enums.Speed;
import io.github.sidf.documentreader.util.enums.Language;

public class EspeakReader extends Reader {
	private static final String shellPath = "/bin/sh";
	private Map<Speed, String> speedMap = new HashMap<>();
	private Map<Language, String> languageMap = new HashMap<>();
	private static final String espeakCommandTemplate =  "/usr/bin/espeak -s %s -v %s \"%s\" --stdout | aplay";
	
	public EspeakReader(Document document) throws Exception {
		super(document);
		
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
	public void read(String text) throws Exception {
		String espeakCommand = String.format(espeakCommandTemplate, speedMap.get(getSpeed()), languageMap.get(getLanguage()), text);
		Process process = Runtime.getRuntime().exec(new String[] {shellPath, "-c", espeakCommand});
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
