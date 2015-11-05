package org.yeastrc.xlink.www.qc_plots.psm_count_per_q_value;

import java.util.List;

public class PsmCountPerQValueQCPlotDataJSONPerType {

	private List<PsmCountPerQValueQCPlotDataJSONChartBucket> chartBuckets;
//	private int zeroQvalueCount = 0;
	
	public List<PsmCountPerQValueQCPlotDataJSONChartBucket> getChartBuckets() {
		return chartBuckets;
	}
	public void setChartBuckets(
			List<PsmCountPerQValueQCPlotDataJSONChartBucket> chartBuckets) {
		this.chartBuckets = chartBuckets;
	}

}
