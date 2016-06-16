package org.yeastrc.xlink.www.objects;

import org.yeastrc.xlink.www.dto.SearchDTO;

public class SearchBooleanWrapper {

	public SearchBooleanWrapper( SearchDTO search, boolean contained ) {
		this.search = search;
		this.contained = contained;
	}

	
	
	public Boolean getContained() {
		return contained;
	}
	public void setContained(Boolean contained) {
		this.contained = contained;
	}
	public SearchDTO getSearch() {
		return search;
	}
	public void setSearch(SearchDTO search) {
		this.search = search;
	}



	private Boolean contained;
	private SearchDTO search;
	
}
