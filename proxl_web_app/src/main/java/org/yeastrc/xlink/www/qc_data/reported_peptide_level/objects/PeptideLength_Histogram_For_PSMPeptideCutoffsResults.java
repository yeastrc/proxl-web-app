package org.yeastrc.xlink.www.qc_data.reported_peptide_level.objects;

import java.util.List;

/**
 * Result from PeptideLength_Histogram_For_PSMPeptideCutoffs
 *
 */
public class PeptideLength_Histogram_For_PSMPeptideCutoffsResults {

	private List<PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList;

	/**
	 * 
	 *
	 */
	public static class PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType {
		
		private String linkType;
		private int numReportedPeptides;
		private int peptideLengthMin;
		private int peptideLengthMax;
		private List<PeptideLength_Histogram_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets;
		public String getLinkType() {
			return linkType;
		}
		public void setLinkType(String linkType) {
			this.linkType = linkType;
		}
		public int getNumReportedPeptides() {
			return numReportedPeptides;
		}
		public void setNumReportedPeptides(int numReportedPeptides) {
			this.numReportedPeptides = numReportedPeptides;
		}
		public List<PeptideLength_Histogram_For_PSMPeptideCutoffsResultsChartBucket> getChartBuckets() {
			return chartBuckets;
		}
		public void setChartBuckets(List<PeptideLength_Histogram_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets) {
			this.chartBuckets = chartBuckets;
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
	}
	
	/**
	 * 
	 *
	 */
	public static class PeptideLength_Histogram_For_PSMPeptideCutoffsResultsChartBucket {
		
		private int binStart;
		private int binEnd;
		private double binCenter;
		private int count;
		
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
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		
	}

	public List<PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType> getDataForChartPerLinkTypeList() {
		return dataForChartPerLinkTypeList;
	}

	public void setDataForChartPerLinkTypeList(
			List<PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList) {
		this.dataForChartPerLinkTypeList = dataForChartPerLinkTypeList;
	}
}
