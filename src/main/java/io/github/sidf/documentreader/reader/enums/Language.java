package io.github.sidf.documentreader.reader.enums;

/**
 * Enum that represents the languages supported by {@link io.github.sidf.documentreader.reader.Reader} subclasses
 * @author sidf
 */
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
