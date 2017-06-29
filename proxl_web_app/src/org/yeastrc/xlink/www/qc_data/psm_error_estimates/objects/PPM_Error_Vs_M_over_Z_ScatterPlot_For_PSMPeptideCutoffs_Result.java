package org.yeastrc.xlink.www.qc_data.psm_error_estimates.objects;

import java.util.List;


/**
 * Results from PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs
 *
 */
public class PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_Result {

	private List<PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultForLinkType> dataForChartPerLinkTypeList;

	public static class PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultForLinkType {
		
		private String linkType;
		private int numScans;
		private double ppmErrorBinMin;
		private double ppmErrorBinMax;
		private double ppmErrorPossibleMax;
		private int retentionTimeBinMin;
		private int retentionTimeBinMax;
		private int retentionTimePossibleMax;
		
		private List<Double> ppmErrorBinStartDistinctSorted;

		private List<PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultM_over_ZBucket> m_over_ZBuckets;
		
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
		public int getM_over_ZBinMin() {
			return retentionTimeBinMin;
		}
		public void setM_over_ZBinMin(int retentionTimeBinMin) {
			this.retentionTimeBinMin = retentionTimeBinMin;
		}
		public int getM_over_ZBinMax() {
			return retentionTimeBinMax;
		}
		public void setM_over_ZBinMax(int retentionTimeBinMax) {
			this.retentionTimeBinMax = retentionTimeBinMax;
		}
		public int getM_over_ZPossibleMax() {
			return retentionTimePossibleMax;
		}
		public void setM_over_ZPossibleMax(int retentionTimePossibleMax) {
			this.retentionTimePossibleMax = retentionTimePossibleMax;
		}
		public List<PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultM_over_ZBucket> getM_over_ZBuckets() {
			return m_over_ZBuckets;
		}
		public void setM_over_ZBuckets(
				List<PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultM_over_ZBucket> m_over_ZBuckets) {
			this.m_over_ZBuckets = m_over_ZBuckets;
		}
		public List<Double> getPpmErrorBinStartDistinctSorted() {
			return ppmErrorBinStartDistinctSorted;
		}
		public void setPpmErrorBinStartDistinctSorted(List<Double> ppmErrorBinStartDistinctSorted) {
			this.ppmErrorBinStartDistinctSorted = ppmErrorBinStartDistinctSorted;
		}

	}
	
	/**
	 * Per M_over_Z binned
	 *
	 */
	public static class PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultM_over_ZBucket {

		private int m_over_ZStart;
		private int m_over_ZEnd;
		private List<PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket> chartBuckets;
		
		public List<PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket> getChartBuckets() {
			return chartBuckets;
		}
		public void setChartBuckets(List<PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket> chartBuckets) {
			this.chartBuckets = chartBuckets;
		}
		public int getM_over_ZStart() {
			return m_over_ZStart;
		}
		public void setM_over_ZStart(int m_over_ZStart) {
			this.m_over_ZStart = m_over_ZStart;
		}
		public int getM_over_ZEnd() {
			return m_over_ZEnd;
		}
		public void setM_over_ZEnd(int m_over_ZEnd) {
			this.m_over_ZEnd = m_over_ZEnd;
		}
	}
	
	/**
	 * 
	 *
	 */
	public static class PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultChartBucket {
		
		private int m_over_ZStart;
		private int m_over_ZEnd;
		
		private double ppmErrorStart;
		private double ppmErrorEnd;
		
		private int count;
		
		public void incrementCount() {
			count++;
		}
		
		public int getM_over_ZStart() {
			return m_over_ZStart;
		}
		public void setM_over_ZStart(int m_over_ZStart) {
			this.m_over_ZStart = m_over_ZStart;
		}
		public int getM_over_ZEnd() {
			return m_over_ZEnd;
		}
		public void setM_over_ZEnd(int m_over_ZEnd) {
			this.m_over_ZEnd = m_over_ZEnd;
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

	public List<PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultForLinkType> getDataForChartPerLinkTypeList() {
		return dataForChartPerLinkTypeList;
	}

	public void setDataForChartPerLinkTypeList(
			List<PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_ResultForLinkType> dataForChartPerLinkTypeList) {
		this.dataForChartPerLinkTypeList = dataForChartPerLinkTypeList;
	}
}
