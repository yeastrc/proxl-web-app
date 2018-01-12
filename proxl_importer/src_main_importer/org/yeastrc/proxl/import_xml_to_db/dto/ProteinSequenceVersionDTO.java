package org.yeastrc.proxl.import_xml_to_db.dto;

import org.yeastrc.proxl.import_xml_to_db.constants.DatabaseAutoIncIdFieldForRecordNotInsertedYetConstants;

/**
 * table protein_sequence_version
 *
 */
public class ProteinSequenceVersionDTO {
	

	private int id = DatabaseAutoIncIdFieldForRecordNotInsertedYetConstants.DB_AUTO_INC_FIELD_INITIAL_VALUE_FOR_NOT_INSERTED_YET;
	private int proteinSequenceId;
	private int isotopeLabelId;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProteinSequenceId() {
		return proteinSequenceId;
	}
	public void setProteinSequenceId(int proteinSequenceId) {
		this.proteinSequenceId = proteinSequenceId;
	}
	public int getIsotopeLabelId() {
		return isotopeLabelId;
	}
	public void setIsotopeLabelId(int isotopeLabelId) {
		this.isotopeLabelId = isotopeLabelId;
	}
	
}
