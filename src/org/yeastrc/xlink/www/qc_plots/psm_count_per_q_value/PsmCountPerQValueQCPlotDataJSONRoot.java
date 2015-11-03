package org.yeastrc.xlink.www.qc_plots.psm_count_per_q_value;

import java.util.List;


/**
 * Root of JSON returned for Psm Count per Q value Chart
 *
 */
public class PsmCountPerQValueQCPlotDataJSONRoot {

	private List<PsmCountPerQValueQCPlotDataJSONChartBucket> chartBuckets;
	private int totalQValueCount;
	
	private int qvalueZeroCount;
	private int qvalueOneCount;

	public int getQvalueZeroCount() {
		return qvalueZeroCount;
	}
	public void setQvalueZeroCount(int qvalueZeroCount) {
		this.qvalueZeroCount = qvalueZeroCount;
	}
	public int getQvalueOneCount() {
		return qvalueOneCount;
	}
	public void setQvalueOneCount(int qvalueOneCount) {
		this.qvalueOneCount = qvalueOneCount;
	}
	public int getTotalQValueCount() {
		return totalQValueCount;
	}
	public void setTotalQValueCount(int totalQValueCount) {
		this.totalQValueCount = totalQValueCount;
	}
	public List<PsmCountPerQValueQCPlotDataJSONChartBucket> getChartBuckets() {
		return chartBuckets;
	}
	public void setChartBuckets(
			List<PsmCountPerQValueQCPlotDataJSONChartBucket> chartBuckets) {
		this.chartBuckets = chartBuckets;
	}

	
}
