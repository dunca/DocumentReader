package io.github.sidf;

public class Bookmark {
	private Page page;
	private int characterIndex;
	
	public Bookmark(Page page, int characterIndex) {
		this.page = page;
		this.characterIndex = characterIndex;
	}
	
	public Page getPage() {
		return page;
	}
	
	public int getCharacterIndex() {
		return characterIndex;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public void setCharacterIndex(int characterIndex) {
		this.characterIndex = characterIndex;
	}
}
