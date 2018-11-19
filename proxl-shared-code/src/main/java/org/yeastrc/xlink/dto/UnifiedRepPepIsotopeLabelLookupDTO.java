package org.yeastrc.xlink.dto;

/**
 * table unified_rep_pep_isotope_label_lookup
 *
 */
public class UnifiedRepPepIsotopeLabelLookupDTO {
	
	private int id;
	private int rpMatchedPeptideId;

	private int isotopeLabelId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRpMatchedPeptideId() {
		return rpMatchedPeptideId;
	}

	public void setRpMatchedPeptideId(int rpMatchedPeptideId) {
		this.rpMatchedPeptideId = rpMatchedPeptideId;
	}

	public int getIsotopeLabelId() {
		return isotopeLabelId;
	}

	public void setIsotopeLabelId(int labelId) {
		this.isotopeLabelId = labelId;
	}
}
