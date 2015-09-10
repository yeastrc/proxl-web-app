package org.yeastrc.xlink.dto;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * search_file table,  all but file_contents field
 */
public class SearchFileDTO {

	private int id;
	private int searchId;

	private String displayFilename;

	private String filename;
	private String path;
	
	private long fileSize;
	private String mimeType;
	private String description;
	private Date uploadDate;
	

	
	
	@Override
	public String toString() {
		return "SearchFileDTO [id=" + id + ", searchId=" + searchId
				+ ", displayFilename=" + displayFilename + ", filename="
				+ filename + ", path=" + path + ", fileSize=" + fileSize
				+ ", mimeType=" + mimeType + ", description=" + description
				+ ", uploadDate=" + uploadDate + "]";
	}
	
	//  special getter
	
	public String getDisplayFilename() {
		
		if ( StringUtils.isNotEmpty( displayFilename ) ) {
			return displayFilename;
		}
		
		return filename;
	}
	
	public void setDisplayFilename(String displayFilename) {
		this.displayFilename = displayFilename;
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
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	
}



//		CREATE TABLE search_file (
//		id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//		search_id INT(10) UNSIGNED NOT NULL,
//		filename VARCHAR(255) NOT NULL,
//		path VARCHAR(2000) NULL DEFAULT NULL,
//		filesize INT(11) NOT NULL,
//		mime_type VARCHAR(500) NULL DEFAULT NULL,
//		description VARCHAR(2500) NULL DEFAULT NULL,
//		upload_date DATETIME NOT NULL,
//		file_contents LONGBLOB NULL DEFAULT NULL,

