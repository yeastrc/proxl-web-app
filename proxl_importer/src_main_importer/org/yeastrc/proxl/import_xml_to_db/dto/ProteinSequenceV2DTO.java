package org.yeastrc.proxl.import_xml_to_db.dto;

import org.yeastrc.proxl.import_xml_to_db.constants.DatabaseAutoIncIdFieldForRecordNotInsertedYetConstants;

/**
 * table protein_sequence_v2
 *
 * equals(...) based on sequence
 */
public class ProteinSequenceV2DTO {
	

	private int id = DatabaseAutoIncIdFieldForRecordNotInsertedYetConstants.DB_AUTO_INC_FIELD_INITIAL_VALUE_FOR_NOT_INSERTED_YET;
	private String sequence;

	/**
	 * Constructor
	 */
	public ProteinSequenceV2DTO() { }
	
	/**
	 * Constructor
	 * 
	 * @param sequence
	 */
	public ProteinSequenceV2DTO( String sequence ) {
		
		this.sequence = sequence;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sequence == null) ? 0 : sequence.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProteinSequenceV2DTO other = (ProteinSequenceV2DTO) obj;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		return true;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
}
