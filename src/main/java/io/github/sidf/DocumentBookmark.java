package io.github.sidf;

public class DocumentBookmark {
	private DocumentPage page;
	private int characterIndex;
	
	public DocumentBookmark(DocumentPage page, int characterIndex) {
		this.page = page;
		this.characterIndex = characterIndex;
	}
	
	public DocumentPage getPage() {
		return page;
	}
	
	public int getCharacterIndex() {
		return characterIndex;
	}

	public void setPage(DocumentPage page) {
		this.page = page;
	}

	public void setCharacterIndex(int characterIndex) {
		this.characterIndex = characterIndex;
	}
}
