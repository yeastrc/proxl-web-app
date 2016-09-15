package org.yeastrc.proxl.import_xml_to_db.dto;

/**
 * table search_scan_filename
 *
 */
public class SearchScanFilenameDTO {
		
	private int id;
	private int searchId;
	private String filename;
	
	@Override
	public String toString() {
		return "SearchScanFilenameDTO [id=" + id + ", searchId=" + searchId
				+ ", filename=" + filename + "]";
	}
	
 	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}

	
}
