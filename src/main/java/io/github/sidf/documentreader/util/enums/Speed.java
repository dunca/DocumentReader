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
}
