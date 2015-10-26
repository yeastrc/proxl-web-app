package org.yeastrc.xlink.www.qc_plots.scan_retention_time;

/**
 * 
 *
 */
public class ScanRetentionTimeJSONChartBucket {

	private int binStart;
	private int binEnd;
	private double binCenter;
	private int totalCount;
	private int countForPsmsThatMeetCriteria;
	
	
	public int getBinStart() {
		return binStart;
	}
	public void setBinStart(int binStart) {
		this.binStart = binStart;
	}
	public int getBinEnd() {
		return binEnd;
	}
	public void setBinEnd(int binEnd) {
		this.binEnd = binEnd;
	}
	public double getBinCenter() {
		return binCenter;
	}
	public void setBinCenter(double binCenter) {
		this.binCenter = binCenter;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getCountForPsmsThatMeetCriteria() {
		return countForPsmsThatMeetCriteria;
	}
	public void setCountForPsmsThatMeetCriteria(int countForPsmsThatMeetCriteria) {
		this.countForPsmsThatMeetCriteria = countForPsmsThatMeetCriteria;
	}
}
