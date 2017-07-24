package org.yeastrc.xlink.dto;

/**
 * table scan_file_ms_2_per_scan_data_num_tic_rt
 *
 */
public class ScanFileMS_2_PerScanData_Num_TIC_RT_DTO {

	private int scanFileId;
	private byte[] dataJSON_Gzipped;
	
	public int getScanFileId() {
		return scanFileId;
	}
	public void setScanFileId(int scanFileId) {
		this.scanFileId = scanFileId;
	}
	public byte[] getDataJSON_Gzipped() {
		return dataJSON_Gzipped;
	}
	public void setDataJSON_Gzipped(byte[] dataJSON_Gzipped) {
		this.dataJSON_Gzipped = dataJSON_Gzipped;
	}

}
