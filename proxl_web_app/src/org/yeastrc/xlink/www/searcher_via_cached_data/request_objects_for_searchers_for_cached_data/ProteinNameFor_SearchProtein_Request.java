package org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data;

/**
 * hashCode and equals used for cache
 *
 */
public class ProteinNameFor_SearchProtein_Request {

	int searchId;
	int proteinSequenceId;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + proteinSequenceId;
		result = prime * result + searchId;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProteinNameFor_SearchProtein_Request other = (ProteinNameFor_SearchProtein_Request) obj;
		if (proteinSequenceId != other.proteinSequenceId)
			return false;
		if (searchId != other.searchId)
			return false;
		return true;
	}

	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public int getProteinSequenceId() {
		return proteinSequenceId;
	}
	public void setProteinSequenceId(int proteinSequenceId) {
		this.proteinSequenceId = proteinSequenceId;
	}
}
