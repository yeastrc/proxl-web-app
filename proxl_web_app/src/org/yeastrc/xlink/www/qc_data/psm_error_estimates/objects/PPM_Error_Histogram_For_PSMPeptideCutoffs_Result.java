package org.yeastrc.xlink.www.qc_data.psm_error_estimates.objects;

import java.util.List;


/**
 * Results from PPM_Error_Histogram_For_PSMPeptideCutoffs
 *
 */
public class PPM_Error_Histogram_For_PSMPeptideCutoffs_Result {

	private List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList;

	public static class PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType {
		
		private String linkType;
		private int numScans;
		private double ppmErrorMin;
		private double ppmErrorMax;
		private List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets;
		
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
		public double getPpmErrorMin() {
			return ppmErrorMin;
		}
		public void setPpmErrorMin(double ppmErrorMin) {
			this.ppmErrorMin = ppmErrorMin;
		}
		public double getPpmErrorMax() {
			return ppmErrorMax;
		}
		public void setPpmErrorMax(double ppmErrorMax) {
			this.ppmErrorMax = ppmErrorMax;
		}
		public List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket> getChartBuckets() {
			return chartBuckets;
		}
		public void setChartBuckets(List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets) {
			this.chartBuckets = chartBuckets;
		}

	}
	
	public static class PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket {
		
		private double binStart;
		private double binEnd;
		private double binCenter;
		private int count;
		
		public double getBinCenter() {
			return binCenter;
		}
		public void setBinCenter(double binCenter) {
			this.binCenter = binCenter;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
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
		
	}

	public List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> getDataForChartPerLinkTypeList() {
		return dataForChartPerLinkTypeList;
	}

	public void setDataForChartPerLinkTypeList(
			List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList) {
		this.dataForChartPerLinkTypeList = dataForChartPerLinkTypeList;
	}
}
