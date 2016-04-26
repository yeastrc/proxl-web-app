package org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs;

import java.util.List;

public class DropPeptidePSMCutoffValues {

	private List<DropPeptidePSMCutoffValue> dropPeptideCutoffValueList;

	private List<DropPeptidePSMCutoffValue> dropPSMCutoffValueList;
	
	
	public List<DropPeptidePSMCutoffValue> getDropPeptideCutoffValueList() {
		return dropPeptideCutoffValueList;
	}
	public void setDropPeptideCutoffValueList(
			List<DropPeptidePSMCutoffValue> dropPeptideCutoffValueList) {
		this.dropPeptideCutoffValueList = dropPeptideCutoffValueList;
	}
	public List<DropPeptidePSMCutoffValue> getDropPSMCutoffValueList() {
		return dropPSMCutoffValueList;
	}
	public void setDropPSMCutoffValueList(
			List<DropPeptidePSMCutoffValue> dropPSMCutoffValueList) {
		this.dropPSMCutoffValueList = dropPSMCutoffValueList;
	}

}
