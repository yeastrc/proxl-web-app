package org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data;

import java.util.List;


/**
 * Result from ReportedPeptideBasicObjectsSearcher
 *
 */
public class ReportedPeptideBasicObjectsSearcherResult {

	private int searchId;
	private List<ReportedPeptideBasicObjectsSearcherResultEntry> entryList;

	public List<ReportedPeptideBasicObjectsSearcherResultEntry> getEntryList() {
		return entryList;
	}

	public void setEntryList(List<ReportedPeptideBasicObjectsSearcherResultEntry> entryList) {
		this.entryList = entryList;
	}

	public int getSearchId() {
		return searchId;
	}

	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
}
