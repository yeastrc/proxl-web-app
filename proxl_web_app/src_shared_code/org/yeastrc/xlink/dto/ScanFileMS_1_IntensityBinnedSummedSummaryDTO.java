package org.yeastrc.xlink.dto;

/**
 * table scan_file_ms1_intensity_binned_summed_summary
 *
 */
public class ScanFileMS_1_IntensityBinnedSummedSummaryDTO {

	private int scanFileId;
	private long retentionTimeMaxBinMinusMinBinPlusOne;
	private long mzMaxBinMinusMinBinPlusOne;
	private double intensityMaxBinMinusMinBin;
	
	public int getScanFileId() {
		return scanFileId;
	}
	public void setScanFileId(int scanFileId) {
		this.scanFileId = scanFileId;
	}
	public long getRetentionTimeMaxBinMinusMinBinPlusOne() {
		return retentionTimeMaxBinMinusMinBinPlusOne;
	}
	public void setRetentionTimeMaxBinMinusMinBinPlusOne(long retentionTimeMaxBinMinusMinBinPlusOne) {
		this.retentionTimeMaxBinMinusMinBinPlusOne = retentionTimeMaxBinMinusMinBinPlusOne;
	}
	public long getMzMaxBinMinusMinBinPlusOne() {
		return mzMaxBinMinusMinBinPlusOne;
	}
	public void setMzMaxBinMinusMinBinPlusOne(long mzMaxBinMinusMinBinPlusOne) {
		this.mzMaxBinMinusMinBinPlusOne = mzMaxBinMinusMinBinPlusOne;
	}
	public double getIntensityMaxBinMinusMinBin() {
		return intensityMaxBinMinusMinBin;
	}
	public void setIntensityMaxBinMinusMinBin(double intensityMaxBinMinusMinBin) {
		this.intensityMaxBinMinusMinBin = intensityMaxBinMinusMinBin;
	}
}
