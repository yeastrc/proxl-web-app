package org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data;

/**
 * request for Cached_SrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId
 * 
 * hashCode and equals used for cache
 */
public class SrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_ReqParams {

	private int srchRepPeptPeptideId;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + srchRepPeptPeptideId;
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
		SrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_ReqParams other = (SrchRepPeptPeptideIsotopeLabelDTO_For_SrchRepPeptPeptideId_ReqParams) obj;
		if (srchRepPeptPeptideId != other.srchRepPeptPeptideId)
			return false;
		return true;
	}

	public int getSrchRepPeptPeptideId() {
		return srchRepPeptPeptideId;
	}

	public void setSrchRepPeptPeptideId(int srchRepPeptPeptideId) {
		this.srchRepPeptPeptideId = srchRepPeptPeptideId;
	} 
	
}
