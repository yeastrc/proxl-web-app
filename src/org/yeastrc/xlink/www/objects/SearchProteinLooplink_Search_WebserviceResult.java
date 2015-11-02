package org.yeastrc.xlink.www.objects;

public class SearchProteinLooplink_Search_WebserviceResult {

	private SearchProteinLooplink searchProteinLooplink;

	

	public double getPeptideQValueCutoff() {
		
		return searchProteinLooplink.getPeptideCutoff();
	}
	public double getPsmQValueCutoff() {
		
		return searchProteinLooplink.getPeptideCutoff();
	}

	public int getProteinId() {
		
		return searchProteinLooplink.getProtein().getNrProtein().getNrseqId();
	}
	
	public int getProteinPosition1() {

		return searchProteinLooplink.getProteinPosition1();
	}

	public int getProteinPosition2() {

		return searchProteinLooplink.getProteinPosition2();
	}

	public String getSearchName() {
		return searchProteinLooplink.getSearch().getName();
	}

	public int getSearchId() {
		return searchProteinLooplink.getSearch().getId();
	}

	

	public SearchProteinLooplink getSearchProteinLooplink() {
		return searchProteinLooplink;
	}
	public void setSearchProteinLooplink(SearchProteinLooplink searchProteinLooplink) {
		this.searchProteinLooplink = searchProteinLooplink;
	}

}
