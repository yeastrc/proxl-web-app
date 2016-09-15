package org.yeastrc.xlink.dto;


import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * table unified_reported_peptide_lookup
 *
 */
public class UnifiedReportedPeptideLookupDTO {
	
	private int id;
	private String unifiedSequence;
	
	private String linkTypeString;
	private int linkTypeNumber;
	
	private boolean hasMods;
	

	public void setLinkTypeString(String linkTypeString) {
		this.linkTypeString = linkTypeString;
		
		this.linkTypeNumber = XLinkUtils.getTypeNumber(linkTypeString);
	}
	
	public void setLinkTypeNumber(int linkTypeNumber) {
		this.linkTypeNumber = linkTypeNumber;
		
		this.linkTypeString = XLinkUtils.getTypeString(linkTypeNumber);
	}


	
	
	public String getLinkTypeString() {
		return linkTypeString;
	}
	public int getLinkTypeNumber() {
		return linkTypeNumber;
	}
	public boolean isHasMods() {
		return hasMods;
	}
	public void setHasMods(boolean hasMods) {
		this.hasMods = hasMods;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUnifiedSequence() {
		return unifiedSequence;
	}
	public void setUnifiedSequence(String unifiedSequence) {
		this.unifiedSequence = unifiedSequence;
	}

	
}
