package org.yeastrc.proxl.import_xml_to_db.objects;

import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosMonolinkDTO;


/**
 * A SrchRepPeptProtSeqIdPosMonolinkDTO and it's associated data
 *
 */
public class MonolinkContainer {

	private SrchRepPeptProtSeqIdPosMonolinkDTO srchRepPeptProtSeqIdPosMonolinkDTO;

	private ProteinImporterContainer proteinImporterContainer;
	
	

	public SrchRepPeptProtSeqIdPosMonolinkDTO getSrchRepPeptProtSeqIdPosMonolinkDTO() {
		return srchRepPeptProtSeqIdPosMonolinkDTO;
	}

	public void setSrchRepPeptProtSeqIdPosMonolinkDTO(
			SrchRepPeptProtSeqIdPosMonolinkDTO srchRepPeptProtSeqIdPosMonolinkDTO) {
		this.srchRepPeptProtSeqIdPosMonolinkDTO = srchRepPeptProtSeqIdPosMonolinkDTO;
	}

	public ProteinImporterContainer getProteinImporterContainer() {
		return proteinImporterContainer;
	}

	public void setProteinImporterContainer(
			ProteinImporterContainer proteinImporterContainer) {
		this.proteinImporterContainer = proteinImporterContainer;
	}
	
}
