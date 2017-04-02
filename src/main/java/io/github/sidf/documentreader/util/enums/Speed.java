package io.github.sidf.documentreader.util.enums;

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
	
	public Speed fromString(String displayName) {
		for (Speed speed : Speed.values()) {
			if (speed.displayName.equals(displayName)) {
				return speed;
			}
		}
		return null;
	}
}
