package org.yeastrc.xlink.dto;

/**
 * table: scan_file_statistics
 *
 */
public class ScanFileStatisticsDTO {

	private int scanFileId;
	private long ms_1_ScanCount;
	private double ms_1_ScanIntensitiesSummed;
	private long ms_2_ScanCount;
	private double ms_2_ScanIntensitiesSummed;
	
	@Override
	public String toString() {
		return "ScanFileStatisticsDTO [scanFileId=" + scanFileId + ", ms_1_ScanCount=" + ms_1_ScanCount
				+ ", ms_1_ScanIntensitiesSummed=" + ms_1_ScanIntensitiesSummed + ", ms_2_ScanCount=" + ms_2_ScanCount
				+ ", ms_2_ScanIntensitiesSummed=" + ms_2_ScanIntensitiesSummed + "]";
	}

	public int getScanFileId() {
		return scanFileId;
	}
	public void setScanFileId(int scanFileId) {
		this.scanFileId = scanFileId;
	}
	public long getMs_1_ScanCount() {
		return ms_1_ScanCount;
	}
	public void setMs_1_ScanCount(long ms_1_ScanCount) {
		this.ms_1_ScanCount = ms_1_ScanCount;
	}
	public double getMs_1_ScanIntensitiesSummed() {
		return ms_1_ScanIntensitiesSummed;
	}
	public void setMs_1_ScanIntensitiesSummed(double ms_1_ScanIntensitiesSummed) {
		this.ms_1_ScanIntensitiesSummed = ms_1_ScanIntensitiesSummed;
	}
	public long getMs_2_ScanCount() {
		return ms_2_ScanCount;
	}
	public void setMs_2_ScanCount(long ms_2_ScanCount) {
		this.ms_2_ScanCount = ms_2_ScanCount;
	}
	public double getMs_2_ScanIntensitiesSummed() {
		return ms_2_ScanIntensitiesSummed;
	}
	public void setMs_2_ScanIntensitiesSummed(double ms_2_ScanIntensitiesSummed) {
		this.ms_2_ScanIntensitiesSummed = ms_2_ScanIntensitiesSummed;
	}

}
