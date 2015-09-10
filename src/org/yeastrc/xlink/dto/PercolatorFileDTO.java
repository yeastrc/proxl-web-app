package org.yeastrc.xlink.dto;



/**
 * Table percolator_file
 *
 */
public class PercolatorFileDTO {

	private int id;

	private int searchId;
	private String filename;
	private String path;
	private String sha1sum;
	
	
	
	@Override
	public String toString() {
		return "PercolatorFileDTO [id=" + id + ", searchId=" + searchId
				+ ", filename=" + filename + ", path=" + path + ", sha1sum="
				+ sha1sum + "]";
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getSha1sum() {
		return sha1sum;
	}
	public void setSha1sum(String sha1sum) {
		this.sha1sum = sha1sum;
	}
	
}

//CREATE TABLE percolator_file (
//		  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//		  search_id INT UNSIGNED NOT NULL,
//		  filename VARCHAR(255) NOT NULL,
//		  path VARCHAR(2000) NOT NULL,
//		  sha1sum VARCHAR(40) NOT NULL,
