package org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs;

import java.util.List;
import java.util.Map;

public class DropPeptidePSMCutoffValues {
	
	

	private Map<String,Map<String,DropPeptidePSMCutoffValue>> dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName;

	private Map<String,Map<String,DropPeptidePSMCutoffValue>> dropPSMCutoffValueKeyedOnSearchPgmNameAnnName;
	
	

	private List<DropPeptidePSMCutoffValue> dropPeptideCutoffValuesCommandLineList;

	private List<DropPeptidePSMCutoffValue> dropPSMCutoffValuesCommandLineList;
	
	
	

	public Map<String, Map<String, DropPeptidePSMCutoffValue>> getDropPeptideCutoffValueKeyedOnSearchPgmNameAnnName() {
		return dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName;
	}

	public void setDropPeptideCutoffValueKeyedOnSearchPgmNameAnnName(
			Map<String, Map<String, DropPeptidePSMCutoffValue>> dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName) {
		this.dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName = dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName;
	}

	public Map<String, Map<String, DropPeptidePSMCutoffValue>> getDropPSMCutoffValueKeyedOnSearchPgmNameAnnName() {
		return dropPSMCutoffValueKeyedOnSearchPgmNameAnnName;
	}

	public void setDropPSMCutoffValueKeyedOnSearchPgmNameAnnName(
			Map<String, Map<String, DropPeptidePSMCutoffValue>> dropPSMCutoffValueKeyedOnSearchPgmNameAnnName) {
		this.dropPSMCutoffValueKeyedOnSearchPgmNameAnnName = dropPSMCutoffValueKeyedOnSearchPgmNameAnnName;
	}

	public List<DropPeptidePSMCutoffValue> getDropPeptideCutoffValuesCommandLineList() {
		return dropPeptideCutoffValuesCommandLineList;
	}

	public void setDropPeptideCutoffValuesCommandLineList(
			List<DropPeptidePSMCutoffValue> dropPeptideCutoffValuesCommandLineList) {
		this.dropPeptideCutoffValuesCommandLineList = dropPeptideCutoffValuesCommandLineList;
	}

	public List<DropPeptidePSMCutoffValue> getDropPSMCutoffValuesCommandLineList() {
		return dropPSMCutoffValuesCommandLineList;
	}

	public void setDropPSMCutoffValuesCommandLineList(
			List<DropPeptidePSMCutoffValue> dropPSMCutoffValuesCommandLineList) {
		this.dropPSMCutoffValuesCommandLineList = dropPSMCutoffValuesCommandLineList;
	}
	


}
