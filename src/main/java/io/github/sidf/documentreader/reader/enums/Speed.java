package io.github.sidf.documentreader.reader.enums;

/**
 * Enum that represents the reading speed levels supported by {@link io.github.sidf.documentreader.reader.Reader} subclasses
 * @author sidf
 */
public enum Speed {
	FAST("fast"),
	MEDIUM("medium"),
	SLOW("slow");
	
	private String displayName;
	
	private Speed(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public static Speed fromString(String displayName) {
		for (Speed speed : Speed.values()) {
			if (speed.displayName.equals(displayName)) {
				return speed;
			}
		}
		return null;
	}
}
