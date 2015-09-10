package org.yeastrc.xlink.dto;

/**
 * Table xquest_xproph_defs_file
 *
 */
public class XquestXprophDefsFileDTO {

	private int id;

	private int xquestFileId;

	private String filename;
	private String path;


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getXquestFileId() {
		return xquestFileId;
	}
	public void setXquestFileId(int xquestFileId) {
		this.xquestFileId = xquestFileId;
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

//	CREATE TABLE xquest_xproph_defs_file (
//			  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//			  xquest_file_id INT(10) UNSIGNED NOT NULL,
//			  filename VARCHAR(255) NOT NULL,
//			  path VARCHAR(2000) NOT NULL,

}
