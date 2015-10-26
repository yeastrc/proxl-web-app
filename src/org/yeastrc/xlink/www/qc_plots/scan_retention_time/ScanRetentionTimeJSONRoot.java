package org.yeastrc.xlink.www.qc_plots.scan_retention_time;

import java.util.List;

/**
 * Root of JSON returned for Scan Retention Time Chart 
 *
 */
public class ScanRetentionTimeJSONRoot {

	private int scanFileId;
	private int numScans;
	private double retentionTimeMin;
	private double retentionTimeMax;
	private List<ScanRetentionTimeJSONChartBucket> chartBuckets;
	
	
	public int getScanFileId() {
		return scanFileId;
	}
	public void setScanFileId(int scanFileId) {
		this.scanFileId = scanFileId;
	}
	public int getNumScans() {
		return numScans;
	}
	public void setNumScans(int numScans) {
		this.numScans = numScans;
	}
	public double getRetentionTimeMin() {
		return retentionTimeMin;
	}
	public void setRetentionTimeMin(double retentionTimeMin) {
		this.retentionTimeMin = retentionTimeMin;
	}
	public double getRetentionTimeMax() {
		return retentionTimeMax;
	}
	public void setRetentionTimeMax(double retentionTimeMax) {
		this.retentionTimeMax = retentionTimeMax;
	}
	public List<ScanRetentionTimeJSONChartBucket> getChartBuckets() {
		return chartBuckets;
	}
	public void setChartBuckets(List<ScanRetentionTimeJSONChartBucket> chartBuckets) {
		this.chartBuckets = chartBuckets;
	}

}
