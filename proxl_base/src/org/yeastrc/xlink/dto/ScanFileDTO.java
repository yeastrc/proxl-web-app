package org.yeastrc.xlink.dto;

/**
 * Table scan_file
 *
 */
public class ScanFileDTO {

	private int id;

	private String filename;
	private String path;
	private String sha1sum;

	public String getSha1sum() {
		return sha1sum;
	}
	public void setSha1sum(String sha1sum) {
		this.sha1sum = sha1sum;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	@Override
	public String toString() {
		return "ScanFileDTO [filename=" + filename + ", id=" + id + ", path="
				+ path + "]";
	}

//	CREATE TABLE scan_file (
//			  id int(10) unsigned NOT NULL AUTO_INCREMENT,
//			  filename varchar(255) NOT NULL,
//			  path varchar(2000) DEFAULT NULL,
//			  sha1sum varchar(255) DEFAULT NULL,

}
