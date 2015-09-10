package org.yeastrc.xlink.dto;

/**
 * table xl_user_access_level_label_description
 *
 */
public class XLUserAccessLevelLabelDescriptionDTO {

	private int accessLevelNumericValue;
	
	private String label;
	private String description;
	
	
	public int getAccessLevelNumericValue() {
		return accessLevelNumericValue;
	}
	public void setAccessLevelNumericValue(int accessLevelNumericValue) {
		this.accessLevelNumericValue = accessLevelNumericValue;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}


//CREATE TABLE xl_user_access_level_label_description (
//		  xl_user_access_level_numeric_value INT UNSIGNED NOT NULL,
//		  label VARCHAR(255) NOT NULL,
//		  description VARCHAR(255) NULL,
