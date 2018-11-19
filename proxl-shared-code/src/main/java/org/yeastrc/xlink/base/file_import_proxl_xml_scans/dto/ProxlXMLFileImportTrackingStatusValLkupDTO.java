package org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto;


/**
 * 
 * table proxl_xml_file_import_tracking_status_values_lookup
 * 
 */
public class ProxlXMLFileImportTrackingStatusValLkupDTO {


	private int id;
	private String statusDisplayText;
	
	
	@Override
	public String toString() {
		return "ProxlXMLFileImportTrackingStatusValuesLookupDTO [id=" + id
				+ ", statusDisplayText=" + statusDisplayText + "]";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStatusDisplayText() {
		return statusDisplayText;
	}
	public void setStatusDisplayText(String statusDisplayText) {
		this.statusDisplayText = statusDisplayText;
	}
	
}
