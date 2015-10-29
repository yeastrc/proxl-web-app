package org.yeastrc.xlink.dto;

/**
 * Table xquest_file
 *
 */
public class XquestFileDTO {

	private int id;
	private int searchId;
	private String filename;
	private String path;
	private String sha1sum;



	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
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

//	CREATE TABLE xquest_file (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  filename VARCHAR(255) NOT NULL,
//			  path VARCHAR(2000) NOT NULL,
//			  sha1sum VARCHAR(255) NOT NULL,

}
