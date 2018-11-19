package org.yeastrc.proxl.import_xml_to_db.dto;

/**
 * 
 * table annotation
 */
public class AnnotationDTO {

	private int id;
	private int taxonomy;
	private String name;
	private String description;
	
	@Override
	public String toString() {
		return "AnnotationDTO [id=" + id + ", taxonomy=" + taxonomy + ", name="
				+ name + ", description=" + description + "]";
	}
	
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


}
