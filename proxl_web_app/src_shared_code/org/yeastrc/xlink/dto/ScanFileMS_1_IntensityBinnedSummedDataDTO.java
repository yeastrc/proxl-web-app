package org.yeastrc.xlink.dto;

/**
 * table scan_file_ms1_intensity_binned_summed_data
 *
 */
public class ScanFileMS_1_IntensityBinnedSummedDataDTO {

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
