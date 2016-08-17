package org.yeastrc.proxl.import_xml_to_db.dto;

public class PeptideDTO {
	
	@Override
	public String toString() {
		return "PeptideDTO [id=" + id + ", sequence=" + sequence + "]";
	}

	public int hashCode() {
		return id;
	}
	
	public boolean equals( Object o ) {
		if( !(o instanceof PeptideDTO )) return false;
		if( ((PeptideDTO)o).getId() == this.getId() ) return true;
		
		return false;
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
	
	private int id;
	private String sequence;
	
}
