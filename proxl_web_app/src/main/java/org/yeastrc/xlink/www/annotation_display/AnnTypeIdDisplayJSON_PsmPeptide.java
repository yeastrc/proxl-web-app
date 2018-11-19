package org.yeastrc.xlink.www.annotation_display;

/**
 * The PSM or Peptide object for annotation type ids for what annotations to display
 * 
 * This is what is passed when retrieving PSMs or Peptides from web services
 */
public class AnnTypeIdDisplayJSON_PsmPeptide {

	private int[] addAnnTypeId;
	private int[] inclAnnTypeId;
	private int[] exclAnnTypeId;
	
	
	public int[] getInclAnnTypeId() {
		return inclAnnTypeId;
	}
	public void setInclAnnTypeId(int[] inclAnnTypeId) {
		this.inclAnnTypeId = inclAnnTypeId;
	}
	public int[] getExclAnnTypeId() {
		return exclAnnTypeId;
	}
	public void setExclAnnTypeId(int[] exclAnnTypeId) {
		this.exclAnnTypeId = exclAnnTypeId;
	}
	public int[] getAddAnnTypeId() {
		return addAnnTypeId;
	}
	public void setAddAnnTypeId(int[] addAnnTypeId) {
		this.addAnnTypeId = addAnnTypeId;
	}
}

