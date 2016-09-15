package org.yeastrc.xlink.www.dto;


/**
 * table srch_rep_pept__peptide
 *
 */
public class SrchRepPeptPeptideDTO {

	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int peptideId;
	private Integer peptidePosition_1;
	private Integer peptidePosition_2;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public Integer getPeptidePosition_1() {
		return peptidePosition_1;
	}
	public void setPeptidePosition_1(Integer peptidePosition_1) {
		this.peptidePosition_1 = peptidePosition_1;
	}
	public Integer getPeptidePosition_2() {
		return peptidePosition_2;
	}
	public void setPeptidePosition_2(Integer peptidePosition_2) {
		this.peptidePosition_2 = peptidePosition_2;
	}

}
