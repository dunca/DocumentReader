package io.github.sidf;

public class DocumentBookmark {
	private int pageIndex;
	private DocumentPage page;
	private int characterIndex;
	
	public DocumentBookmark(DocumentPage page, int pageIndex, int characterIndex) {
		this.page = page;
		this.pageIndex = pageIndex;
		this.characterIndex = characterIndex;
	}
	
	public DocumentPage getPage() {
		return page;
	}
	
	public void setPage(DocumentPage page) {
		this.page = page;
	}
	
	public int getPageIndex() {
		return pageIndex;
	}
	
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	
	public int getCharacterIndex() {
		return characterIndex;
	}
	
	public void setCharacterIndex(int characterIndex) {
		this.characterIndex = characterIndex;
	}
}
