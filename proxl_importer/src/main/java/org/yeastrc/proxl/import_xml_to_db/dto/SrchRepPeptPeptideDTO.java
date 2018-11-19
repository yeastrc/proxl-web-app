package org.yeastrc.proxl.import_xml_to_db.dto;


/**
 * table srch_rep_pept__peptide
 *
 */
public class SrchRepPeptPeptideDTO {

	private int id;
	private int searchId;
	private int reportedPeptideId;
	private int peptideId;
	private int peptidePosition_1 = -1;  //  -1 if no position, which is default value for object 
	private int peptidePosition_2 = -1;  //  -1 if no position, which is default value for object 
	
	@Override
	public String toString() {
		return "SrchRepPeptPeptideDTO [id=" + id + ", searchId=" + searchId
				+ ", reportedPeptideId=" + reportedPeptideId + ", peptideId="
				+ peptideId + ", peptidePosition_1=" + peptidePosition_1
				+ ", peptidePosition_2=" + peptidePosition_2 + "]";
	}

	/**
	 * -1 if no position, which is default value for object property
	 * @return
	 */
	public int getPeptidePosition_1() {
		return peptidePosition_1;
	}
	/**
	 * -1 if no position, which is default value for object 
	 * @param peptidePosition_1
	 */
	public void setPeptidePosition_1(int peptidePosition_1) {
		this.peptidePosition_1 = peptidePosition_1;
	}
	/**
	 * -1 if no position, which is default value for object 
	 * @return
	 */
	public int getPeptidePosition_2() {
		return peptidePosition_2;
	}
	/**
	 * -1 if no position, which is default value for object 
	 * @param peptidePosition_2
	 */
	public void setPeptidePosition_2(int peptidePosition_2) {
		this.peptidePosition_2 = peptidePosition_2;
	}
	
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


}
