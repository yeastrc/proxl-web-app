package org.yeastrc.xlink.www.qc_plots.psm_count_for_score;

import java.util.List;

public class PsmCountsVsScoreQCPlotDataJSONPerType {

	private List<PsmCountsVsScoreQCPlotDataJSONChartBucket> chartBuckets;
	
	private int totalCountForType;
	
	public int getTotalCountForType() {
		return totalCountForType;
	}
	public void setTotalCountForType(int totalCountForType) {
		this.totalCountForType = totalCountForType;
	}
	public List<PsmCountsVsScoreQCPlotDataJSONChartBucket> getChartBuckets() {
		return chartBuckets;
	}
	public void setChartBuckets(
			List<PsmCountsVsScoreQCPlotDataJSONChartBucket> chartBuckets) {
		this.chartBuckets = chartBuckets;
	}

}
