package org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data;

/**
 * request for Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId
 * 
 * hashCode and equals used for cache
 */
public class SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams {

	private int searchId; 
	private int reportedPeptideId;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + reportedPeptideId;
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
		SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams other = (SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams) obj;
		if (reportedPeptideId != other.reportedPeptideId)
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
	public int getReportedPeptideId() {
		return reportedPeptideId;
	}
	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}
}
