package org.yeastrc.xlink.dto;


public class XquestPsmDTO {
	
	private int id;
	private int psmId;
	private int xquestFileId;

	private String type;
	
	private String scanNumber;


	private String xquestId;
	private String fdr;
	
	private String charge;
	private String seq1;
	private String seq2;
	
	private String xlinkposition;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPsmId() {
		return psmId;
	}

	public void setPsmId(int psmId) {
		this.psmId = psmId;
	}

	public int getXquestFileId() {
		return xquestFileId;
	}

	public void setXquestFileId(int xquestFileId) {
		this.xquestFileId = xquestFileId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getScanNumber() {
		return scanNumber;
	}

	public void setScanNumber(String scanNumber) {
		this.scanNumber = scanNumber;
	}

	public String getXquestId() {
		return xquestId;
	}

	public void setXquestId(String xquestId) {
		this.xquestId = xquestId;
	}

	public String getFdr() {
		return fdr;
	}

	public void setFdr(String fdr) {
		this.fdr = fdr;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getSeq1() {
		return seq1;
	}

	public void setSeq1(String seq1) {
		this.seq1 = seq1;
	}

	public String getSeq2() {
		return seq2;
	}

	public void setSeq2(String seq2) {
		this.seq2 = seq2;
	}

	public String getXlinkposition() {
		return xlinkposition;
	}

	public void setXlinkposition(String xlinkposition) {
		this.xlinkposition = xlinkposition;
	}

}


//	CREATE TABLE xquest_psm (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  psm_id INT UNSIGNED NOT NULL,
//			  xquest_file_id INT UNSIGNED NOT NULL,
//			  type VARCHAR(200) NULL,
//			  scan_number VARCHAR(45) NULL,
//			  xquest_id VARCHAR(2000) NULL,
//			  fdr VARCHAR(200) NULL,
//			  charge VARCHAR(200) NULL,
//			  seq1 VARCHAR(2000) NULL,
//			  seq2 VARCHAR(2000) NULL,
//			  xlinkposition VARCHAR(200) NULL,
