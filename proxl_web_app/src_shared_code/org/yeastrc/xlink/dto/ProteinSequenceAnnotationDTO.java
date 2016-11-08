package org.yeastrc.xlink.dto;

/**
 * Represents a name, taxonomy id, and description associated with a protein sequence id in
 * a given search. Data comes from the "annotation" table.
 * 
 * @author mriffle
 *
 */
public class ProteinSequenceAnnotationDTO {
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTaxonomy() {
		return taxonomy;
	}
	public void setTaxonomy(int taxonomy) {
		this.taxonomy = taxonomy;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private int id;
	private int taxonomy;
	private String name;
	private String description;
	
}
