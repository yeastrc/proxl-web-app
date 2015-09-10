package org.yeastrc.xlink.dto;

/**
 * Table kojak_file
 *
 */
public class KojakFileDTO {

	private int id;

	private String filename;
//	private String filenameForPercolator;
	private String path;
	private String sha1sum;
	private String kojakProgramVersion;


	public String getKojakProgramVersion() {
		return kojakProgramVersion;
	}
	public void setKojakProgramVersion(String kojakProgramVersion) {
		this.kojakProgramVersion = kojakProgramVersion;
	}
//	public String getFilenameForPercolator() {
//		return filenameForPercolator;
//	}
//	public void setFilenameForPercolator(String filenameForPercolator) {
//		this.filenameForPercolator = filenameForPercolator;
//	}
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
		return "KojakFileDTO [filename=" + filename + ", id=" + id + ", path="
				+ path + "]";
	}

//	CREATE TABLE scan_file (
//	  id int(10) unsigned NOT NULL AUTO_INCREMENT,
//	  filename varchar(255) NOT NULL,
//	  filename_for_percolator VARCHAR(255) NULL,
//	  path varchar(2000) DEFAULT NULL,
//	  sha1sum varchar(255) DEFAULT NULL,
//	  kojak_program_version VARCHAR(255) NOT NULL

}
