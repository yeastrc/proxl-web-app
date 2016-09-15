package org.yeastrc.proxl.import_xml_to_db.unified_reported_peptide.objects;

import java.util.List;


public class UnifiedReportedPeptideObj {

	private List<UnifiedRpSinglePeptideObj> singlePeptides;
	private int linkType;

	public List<UnifiedRpSinglePeptideObj> getSinglePeptides() {
		return singlePeptides;
	}
	public void setSinglePeptides(List<UnifiedRpSinglePeptideObj> singlePeptides) {
		this.singlePeptides = singlePeptides;
	}
	public int getLinkType() {
		return linkType;
	}
	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}
	
}
