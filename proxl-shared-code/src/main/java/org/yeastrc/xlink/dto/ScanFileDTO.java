package org.yeastrc.xlink.dto;

/**
 * Table scan_file
 *
 */
public class ScanFileDTO {
	
	public static final long FILE_SIZE_NOT_SET = -1;

	private int id;

	private String filename;
	private String path;
	private String sha1sum;
	private long fileSize = FILE_SIZE_NOT_SET;
	
	@Override
	public String toString() {
		return "ScanFileDTO [id=" + id + ", filename=" + filename + ", path=" + path + ", sha1sum=" + sha1sum
				+ ", fileSize=" + fileSize + "]";
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
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

}
