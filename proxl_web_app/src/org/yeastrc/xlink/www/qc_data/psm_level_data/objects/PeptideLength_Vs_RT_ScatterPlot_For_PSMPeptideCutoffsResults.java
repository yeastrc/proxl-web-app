package org.yeastrc.xlink.www.qc_data.psm_level_data.objects;

import java.util.List;


/**
 * Results from PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs
 *
 */
public class PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffsResults {

	private String linkType;
	private int numScans;
	private int peptideLengthMin;
	private int peptideLengthMax;
	private int retentionTimeBinMin;
	private int retentionTimeBinMax;
	private int retentionTimePossibleMax;
	
	private int countValuePercentile25;
	private int countValuePercentile50;
	private int countValuePercentile75;
	
	private List<PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket> retentionTimeBuckets;
	
	/**
	 * Per RetentionTime binned
	 *
	 */
	public static class PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket {

		private int retentionTimeStart;
		private int retentionTimeEnd;
		private List<PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket> chartBuckets;
		
		public int getRetentionTimeStart() {
			return retentionTimeStart;
		}
		public void setRetentionTimeStart(int retentionTimeStart) {
			this.retentionTimeStart = retentionTimeStart;
		}
		public int getRetentionTimeEnd() {
			return retentionTimeEnd;
		}
		public void setRetentionTimeEnd(int retentionTimeEnd) {
			this.retentionTimeEnd = retentionTimeEnd;
		}
		public List<PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket> getChartBuckets() {
			return chartBuckets;
		}
		public void setChartBuckets(
				List<PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket> chartBuckets) {
			this.chartBuckets = chartBuckets;
		}
	}

	/**
	 * 
	 *
	 */
	public static class PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket {
		
		private int retentionTimeStart;
		private int retentionTimeEnd;
		
		private int peptideLength;
		
		private int count;
		
		public void incrementCount() {
			count++;
		}

		public int getRetentionTimeStart() {
			return retentionTimeStart;
		}

		public void setRetentionTimeStart(int retentionTimeStart) {
			this.retentionTimeStart = retentionTimeStart;
		}

		public int getRetentionTimeEnd() {
			return retentionTimeEnd;
		}

		public void setRetentionTimeEnd(int retentionTimeEnd) {
			this.retentionTimeEnd = retentionTimeEnd;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public int getPeptideLength() {
			return peptideLength;
		}

		public void setPeptideLength(int peptideLength) {
			this.peptideLength = peptideLength;
		}
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public int getNumScans() {
		return numScans;
	}

	public void setNumScans(int numScans) {
		this.numScans = numScans;
	}

	public int getPeptideLengthMin() {
		return peptideLengthMin;
	}

	public void setPeptideLengthMin(int peptideLengthMin) {
		this.peptideLengthMin = peptideLengthMin;
	}

	public int getPeptideLengthMax() {
		return peptideLengthMax;
	}

	public void setPeptideLengthMax(int peptideLengthMax) {
		this.peptideLengthMax = peptideLengthMax;
	}

	public int getRetentionTimeBinMin() {
		return retentionTimeBinMin;
	}

	public void setRetentionTimeBinMin(int retentionTimeBinMin) {
		this.retentionTimeBinMin = retentionTimeBinMin;
	}

	public int getRetentionTimeBinMax() {
		return retentionTimeBinMax;
	}

	public void setRetentionTimeBinMax(int retentionTimeBinMax) {
		this.retentionTimeBinMax = retentionTimeBinMax;
	}

	public int getRetentionTimePossibleMax() {
		return retentionTimePossibleMax;
	}

	public void setRetentionTimePossibleMax(int retentionTimePossibleMax) {
		this.retentionTimePossibleMax = retentionTimePossibleMax;
	}

	public List<PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket> getRetentionTimeBuckets() {
		return retentionTimeBuckets;
	}

	public void setRetentionTimeBuckets(
			List<PeptideLength_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket> retentionTimeBuckets) {
		this.retentionTimeBuckets = retentionTimeBuckets;
	}

	public int getCountValuePercentile25() {
		return countValuePercentile25;
	}

	public void setCountValuePercentile25(int countValuePercentile25) {
		this.countValuePercentile25 = countValuePercentile25;
	}

	public int getCountValuePercentile50() {
		return countValuePercentile50;
	}

	public void setCountValuePercentile50(int countValuePercentile50) {
		this.countValuePercentile50 = countValuePercentile50;
	}

	public int getCountValuePercentile75() {
		return countValuePercentile75;
	}

	public void setCountValuePercentile75(int countValuePercentile75) {
		this.countValuePercentile75 = countValuePercentile75;
	}
}
