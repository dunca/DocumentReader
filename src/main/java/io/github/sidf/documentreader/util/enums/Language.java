package io.github.sidf.documentreader.util.enums;

public enum Language {
	HUNGARIAN("Magyar"),
	ROMANIAN("Română"),
	ENGLISH("English"),
	SPANISH("Español"),
	FRENCH("Français");
	
	private String displayName;

	private Language(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public static Language fromString(String displayName) {
		for (Language language : Language.values()) {
			if (language.displayName.equals(displayName)) {
				return language;
			}
		}
		return null;
	}
}
