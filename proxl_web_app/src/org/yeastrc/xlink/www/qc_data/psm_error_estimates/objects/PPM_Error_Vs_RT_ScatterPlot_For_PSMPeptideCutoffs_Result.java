package org.yeastrc.xlink.www.qc_data.psm_error_estimates.objects;

import java.util.List;


/**
 * Results from PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs
 *
 */
public class PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_Result {

	private List<PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultForLinkType> dataForChartPerLinkTypeList;

	public static class PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultForLinkType {
		
		private String linkType;
		private int numScans;
		private double ppmErrorBinMin;
		private double ppmErrorBinMax;
		private double ppmErrorPossibleMax;
		private int retentionTimeBinMin;
		private int retentionTimeBinMax;
		private int retentionTimePossibleMax;
		
		private List<Double> ppmErrorBinStartDistinctSorted;

		private List<PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket> retentionTimeBuckets;
		
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
		public double getPpmErrorBinMin() {
			return ppmErrorBinMin;
		}
		public void setPpmErrorBinMin(double ppmErrorBinMin) {
			this.ppmErrorBinMin = ppmErrorBinMin;
		}
		public double getPpmErrorBinMax() {
			return ppmErrorBinMax;
		}
		public void setPpmErrorBinMax(double ppmErrorBinMax) {
			this.ppmErrorBinMax = ppmErrorBinMax;
		}
		public double getPpmErrorPossibleMax() {
			return ppmErrorPossibleMax;
		}
		public void setPpmErrorPossibleMax(double ppmErrorPossibleMax) {
			this.ppmErrorPossibleMax = ppmErrorPossibleMax;
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
		public List<PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket> getRetentionTimeBuckets() {
			return retentionTimeBuckets;
		}
		public void setRetentionTimeBuckets(
				List<PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket> retentionTimeBuckets) {
			this.retentionTimeBuckets = retentionTimeBuckets;
		}
		public List<Double> getPpmErrorBinStartDistinctSorted() {
			return ppmErrorBinStartDistinctSorted;
		}
		public void setPpmErrorBinStartDistinctSorted(List<Double> ppmErrorBinStartDistinctSorted) {
			this.ppmErrorBinStartDistinctSorted = ppmErrorBinStartDistinctSorted;
		}

	}
	
	/**
	 * Per RetentionTime binned
	 *
	 */
	public static class PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultRetentionTimeBucket {

		private int retentionTimeStart;
		private int retentionTimeEnd;
		private List<PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket> chartBuckets;
		
		public List<PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket> getChartBuckets() {
			return chartBuckets;
		}
		public void setChartBuckets(List<PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket> chartBuckets) {
			this.chartBuckets = chartBuckets;
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
	}
	
	/**
	 * 
	 *
	 */
	public static class PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket {
		
		private int retentionTimeStart;
		private int retentionTimeEnd;
		
		private double ppmErrorStart;
		private double ppmErrorEnd;
		
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
		public double getPpmErrorStart() {
			return ppmErrorStart;
		}
		public void setPpmErrorStart(double ppmErrorStart) {
			this.ppmErrorStart = ppmErrorStart;
		}
		public double getPpmErrorEnd() {
			return ppmErrorEnd;
		}
		public void setPpmErrorEnd(double ppmErrorEnd) {
			this.ppmErrorEnd = ppmErrorEnd;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		
	}

	public List<PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultForLinkType> getDataForChartPerLinkTypeList() {
		return dataForChartPerLinkTypeList;
	}

	public void setDataForChartPerLinkTypeList(
			List<PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_ResultForLinkType> dataForChartPerLinkTypeList) {
		this.dataForChartPerLinkTypeList = dataForChartPerLinkTypeList;
	}
}
