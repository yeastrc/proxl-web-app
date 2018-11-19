package org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data;

import java.util.Arrays;

import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.searcher.ReportedPeptideBasicObjectsSearcher.ReturnOnlyReportedPeptidesWithMonolinks;

/**
 * Request for ReportedPeptideBasicObjectsSearcher
 *
 * hashCode and equals used for cache
 */
public class ReportedPeptideBasicObjectsSearcherRequestParameters {

	private int searchId;
	private SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel; 
	private String[] linkTypes;
	private String[] modMassSelections; 
	private ReturnOnlyReportedPeptidesWithMonolinks returnOnlyReportedPeptidesWithMonolinks;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(linkTypes);
		result = prime * result + Arrays.hashCode(modMassSelections);
		result = prime * result + ((returnOnlyReportedPeptidesWithMonolinks == null) ? 0
				: returnOnlyReportedPeptidesWithMonolinks.hashCode());
		result = prime * result + searchId;
		result = prime * result
				+ ((searcherCutoffValuesSearchLevel == null) ? 0 : searcherCutoffValuesSearchLevel.hashCode());
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
		ReportedPeptideBasicObjectsSearcherRequestParameters other = (ReportedPeptideBasicObjectsSearcherRequestParameters) obj;
		if (!Arrays.equals(linkTypes, other.linkTypes))
			return false;
		if (!Arrays.equals(modMassSelections, other.modMassSelections))
			return false;
		if (returnOnlyReportedPeptidesWithMonolinks != other.returnOnlyReportedPeptidesWithMonolinks)
			return false;
		if (searchId != other.searchId)
			return false;
		if (searcherCutoffValuesSearchLevel == null) {
			if (other.searcherCutoffValuesSearchLevel != null)
				return false;
		} else if (!searcherCutoffValuesSearchLevel.equals(other.searcherCutoffValuesSearchLevel))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "PeptideBasicObjectsSearcherRequestParameters [searchId=" + searchId
				+ ", searcherCutoffValuesSearchLevel=" + searcherCutoffValuesSearchLevel + ", linkTypes="
				+ Arrays.toString(linkTypes) + ", modMassSelections=" + Arrays.toString(modMassSelections)
				+ ", returnOnlyReportedPeptidesWithMonolinks=" + returnOnlyReportedPeptidesWithMonolinks + "]";
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public SearcherCutoffValuesSearchLevel getSearcherCutoffValuesSearchLevel() {
		return searcherCutoffValuesSearchLevel;
	}
	public void setSearcherCutoffValuesSearchLevel(SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel) {
		this.searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel;
	}
	public String[] getLinkTypes() {
		return linkTypes;
	}
	public void setLinkTypes(String[] linkTypes) {
		this.linkTypes = linkTypes;
	}
	public String[] getModMassSelections() {
		return modMassSelections;
	}
	public void setModMassSelections(String[] modMassSelections) {
		this.modMassSelections = modMassSelections;
	}
	public ReturnOnlyReportedPeptidesWithMonolinks getReturnOnlyReportedPeptidesWithMonolinks() {
		return returnOnlyReportedPeptidesWithMonolinks;
	}
	public void setReturnOnlyReportedPeptidesWithMonolinks(
			ReturnOnlyReportedPeptidesWithMonolinks returnOnlyReportedPeptidesWithMonolinks) {
		this.returnOnlyReportedPeptidesWithMonolinks = returnOnlyReportedPeptidesWithMonolinks;
	}
}
