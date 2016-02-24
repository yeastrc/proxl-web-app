package org.yeastrc.xlink.www.qc_plots.psm_count_per_q_value;

public class PsmCountPerQValueQCPlotDataJSONChartBucket {

	private double binStart;
	private double binEnd;
	private int totalCount;
	
	
	public double getBinStart() {
		return binStart;
	}
	public void setBinStart(double binStart) {
		this.binStart = binStart;
	}
	public double getBinEnd() {
		return binEnd;
	}
	public void setBinEnd(double binEnd) {
		this.binEnd = binEnd;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	
}
