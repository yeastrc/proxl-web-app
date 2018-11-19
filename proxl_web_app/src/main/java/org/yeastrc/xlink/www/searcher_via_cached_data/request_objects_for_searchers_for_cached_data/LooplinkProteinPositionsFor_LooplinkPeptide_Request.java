package org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data;

/**
 * hashCode and equals used for cache
 *
 */
public class LooplinkProteinPositionsFor_LooplinkPeptide_Request {

	private int searchId; 
	private int reportedPeptideId; 
	private int peptideId;
	private int position_1;
	private int position_2;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + peptideId;
		result = prime * result + position_1;
		result = prime * result + position_2;
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
		LooplinkProteinPositionsFor_LooplinkPeptide_Request other = (LooplinkProteinPositionsFor_LooplinkPeptide_Request) obj;
		if (peptideId != other.peptideId)
			return false;
		if (position_1 != other.position_1)
			return false;
		if (position_2 != other.position_2)
			return false;
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
	public int getPeptideId() {
		return peptideId;
	}
	public void setPeptideId(int peptideId) {
		this.peptideId = peptideId;
	}
	public int getPosition_1() {
		return position_1;
	}
	public void setPosition_1(int position_1) {
		this.position_1 = position_1;
	}
	public int getPosition_2() {
		return position_2;
	}
	public void setPosition_2(int position_2) {
		this.position_2 = position_2;
	}
	
}
