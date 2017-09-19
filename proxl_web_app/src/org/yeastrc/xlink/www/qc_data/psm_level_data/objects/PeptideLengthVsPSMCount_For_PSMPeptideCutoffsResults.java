package org.yeastrc.xlink.www.qc_data.psm_level_data.objects;

import java.util.List;

/**
 * Results from PeptideLengthVsPSMCount_For_PSMPeptideCutoffs
 *
 */
public class PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults {

	private List<PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList;

	/**
	 * 
	 *
	 */
	public static class PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType {
		
		private String linkType;
		private int peptideLengthMin;
		private int peptideLengthMax;
		private List<PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets;
		public String getLinkType() {
			return linkType;
		}
		public void setLinkType(String linkType) {
			this.linkType = linkType;
		}
		public List<PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsChartBucket> getChartBuckets() {
			return chartBuckets;
		}
		public void setChartBuckets(List<PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets) {
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
	public static class PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsChartBucket {
		
		private int peptideLength;
		private int psmCount;
		
		public int getPeptideLength() {
			return peptideLength;
		}
		public void setPeptideLength(int peptideLength) {
			this.peptideLength = peptideLength;
		}
		public int getPsmCount() {
			return psmCount;
		}
		public void setPsmCount(int psmCount) {
			this.psmCount = psmCount;
		}
		
	}

	public List<PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType> getDataForChartPerLinkTypeList() {
		return dataForChartPerLinkTypeList;
	}

	public void setDataForChartPerLinkTypeList(
			List<PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList) {
		this.dataForChartPerLinkTypeList = dataForChartPerLinkTypeList;
	}


}
