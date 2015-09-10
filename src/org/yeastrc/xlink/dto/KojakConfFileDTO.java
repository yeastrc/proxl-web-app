package org.yeastrc.xlink.dto;

/**
 * Table kojak_conf_file
 *
 */
public class KojakConfFileDTO {

	private int id;

	private int kojakFileId;

	private String filename;
	private String path;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getKojakFileId() {
		return kojakFileId;
	}
	public void setKojakFileId(int kojakFileId) {
		this.kojakFileId = kojakFileId;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}


}

//CREATE TABLE kojak_conf_file (
//id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//kojak_file_id INT(10) UNSIGNED NOT NULL,
//filename VARCHAR(255) NOT NULL,
//path VARCHAR(2000) NOT NULL,
