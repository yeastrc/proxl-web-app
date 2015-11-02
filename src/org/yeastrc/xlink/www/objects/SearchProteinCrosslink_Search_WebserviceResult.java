package org.yeastrc.xlink.www.objects;


public class SearchProteinCrosslink_Search_WebserviceResult {

	private SearchProteinCrosslink searchProteinCrosslink;
	

	public double getPeptideQValueCutoff() {
		
		return searchProteinCrosslink.getPeptideCutoff();
	}
	public double getPsmQValueCutoff() {
		
		return searchProteinCrosslink.getPeptideCutoff();
	}

	public int getProteinId_1() {
		
		return searchProteinCrosslink.getProtein1().getNrProtein().getNrseqId();
	}
	
	public int getProteinId_2() {
		
		return searchProteinCrosslink.getProtein2().getNrProtein().getNrseqId();
	}
	
	public int getProtein1Position() {

		return searchProteinCrosslink.getProtein1Position();
	}

	public int getProtein2Position() {

		return searchProteinCrosslink.getProtein2Position();
	}

	public String getSearchName() {
		return searchProteinCrosslink.getSearch().getName();
	}

	public int getSearchId() {
		return searchProteinCrosslink.getSearch().getId();
	}

	
	
	public SearchProteinCrosslink getSearchProteinCrosslink() {
		return searchProteinCrosslink;
	}
	public void setSearchProteinCrosslink(
			SearchProteinCrosslink searchProteinCrosslink) {
		this.searchProteinCrosslink = searchProteinCrosslink;
	}

}
