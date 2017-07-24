package org.yeastrc.xlink.dto;

/**
 * table scan_file_ms1_intensity_binned_summed_summary_data
 *
 */
public class ScanFileMS_1_IntensityBinnedSummedSummaryDataDTO {

	private int scanFileId;
	private byte[] summaryDataJSON;
	
	public int getScanFileId() {
		return scanFileId;
	}
	public void setScanFileId(int scanFileId) {
		this.scanFileId = scanFileId;
	}
	public byte[] getSummaryDataJSON() {
		return summaryDataJSON;
	}
	public void setSummaryDataJSON(byte[] summaryDataJSON) {
		this.summaryDataJSON = summaryDataJSON;
	}

}
