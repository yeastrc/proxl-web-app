package org.yeastrc.xlink.www.dto;

/**
 * table protein_sequence
 *
 * equals(...) based on sequence
 */
public class ProteinSequenceDTO {
	

	private int id;
	private String sequence;

	/**
	 * Constructor
	 */
	public ProteinSequenceDTO() { }
	
	/**
	 * Constructor
	 * 
	 * @param sequence
	 */
	public ProteinSequenceDTO( String sequence ) {
		
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
		ProteinSequenceDTO other = (ProteinSequenceDTO) obj;
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
