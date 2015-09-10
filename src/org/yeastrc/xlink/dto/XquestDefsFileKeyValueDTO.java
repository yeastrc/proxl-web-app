package org.yeastrc.xlink.dto;

/**
 * Table xquest_defs_file_key_value
 *
 */
public class XquestDefsFileKeyValueDTO {

	private int id;

	private int xquestDefsFileId;
	
	private String key;
	private String value;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getXquestDefsFileId() {
		return xquestDefsFileId;
	}
	public void setXquestDefsFileId(int xquestDefsFileId) {
		this.xquestDefsFileId = xquestDefsFileId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}

//CREATE TABLE IF NOT EXISTS proxl.xquest_defs_file_key_value (
//		  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//		  xquest_defs_file_id INT(10) UNSIGNED NOT NULL,
//		  xquest_defs_file_key VARCHAR(255) NOT NULL,
//		  value VARCHAR(2000) NULL DEFAULT NULL,
//		  