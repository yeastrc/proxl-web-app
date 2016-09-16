package org.yeastrc.xlink.dto;

/**
 * table unified_rep_pep_matched_peptide_lookup
 *
 */
public class UnifiedRepPepMatchedPeptideLookupDTO {

	private int id;
	private int unifiedReportedPeptideId;
	private int peptideId;
	/**
	 * The order the peptides are inserted into the unified reported peptide
	 */
	private int peptideOrder;

	private Integer linkPosition1;
	private Integer linkPosition2;
	
	
	/**
	 * The order the peptides are inserted into the unified reported peptide
	 * @return
	 */
	public int getPeptideOrder() {
		return peptideOrder;
	}
	/**
	 * The order the peptides are inserted into the unified reported peptide
	 * @param peptideOrder
	 */
	public void setPeptideOrder(int peptideOrder) {
		this.peptideOrder = peptideOrder;
	}
	
	public Integer getLinkPosition1() {
		return linkPosition1;
	}
	public void setLinkPosition1(Integer linkPosition1) {
		this.linkPosition1 = linkPosition1;
	}
	public Integer getLinkPosition2() {
		return linkPosition2;
	}
	public void setLinkPosition2(Integer linkPosition2) {
		this.linkPosition2 = linkPosition2;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUnifiedReportedPeptideId() {
		return unifiedReportedPeptideId;
	}
	public void setUnifiedReportedPeptideId(int unifiedReportedPeptideId) {
		this.unifiedReportedPeptideId = unifiedReportedPeptideId;
	}
	public int getPeptideId() {
		return peptideId;
	}
	public void setPeptideId(int peptideId) {
		this.peptideId = peptideId;
	}
}

