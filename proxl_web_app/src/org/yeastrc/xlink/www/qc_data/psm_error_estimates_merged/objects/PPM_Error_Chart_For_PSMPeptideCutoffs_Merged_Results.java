package org.yeastrc.xlink.www.qc_data.psm_error_estimates_merged.objects;

import java.util.List;


/**
 * Results from PPM_Error_Chart_For_PSMPeptideCutoffs_Merged
 *
 */
public class PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Results {

	private List<PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList;

	/**
	 * 
	 * For Single link type, data for one chart
	 */
	public static class PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType {

		private String linkType;
		private boolean dataFound;
		private List<PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId> dataForChartPerSearchIdList;
		
		public String getLinkType() {
			return linkType;
		}
		public void setLinkType(String linkType) {
			this.linkType = linkType;
		}
		public boolean isDataFound() {
			return dataFound;
		}
		public void setDataFound(boolean dataFound) {
			this.dataFound = dataFound;
		}
		public List<PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId> getDataForChartPerSearchIdList() {
			return dataForChartPerSearchIdList;
		}
		public void setDataForChartPerSearchIdList(
				List<PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId> dataForChartPerSearchIdList) {
			this.dataForChartPerSearchIdList = dataForChartPerSearchIdList;
		}
		
	}

	/**
	 * 
	 * For Single search id, part of a chart
	 */
	public static class PPM_Error_Chart_For_PSMPeptideCutoffsResultsForSearchId {

		private int searchId;
		private boolean dataFound;

		//  Box chart values
		private double chartIntervalMax;
		private double chartIntervalMin;
		private double firstQuartile;
		private double median;
		private double thirdQuartile;
		
		public int getSearchId() {
			return searchId;
		}
		public void setSearchId(int searchId) {
			this.searchId = searchId;
		}
		public boolean isDataFound() {
			return dataFound;
		}
		public void setDataFound(boolean dataFound) {
			this.dataFound = dataFound;
		}
		public double getChartIntervalMax() {
			return chartIntervalMax;
		}
		public void setChartIntervalMax(double chartIntervalMax) {
			this.chartIntervalMax = chartIntervalMax;
		}
		public double getChartIntervalMin() {
			return chartIntervalMin;
		}
		public void setChartIntervalMin(double chartIntervalMin) {
			this.chartIntervalMin = chartIntervalMin;
		}
		public double getFirstQuartile() {
			return firstQuartile;
		}
		public void setFirstQuartile(double firstQuartile) {
			this.firstQuartile = firstQuartile;
		}
		public double getMedian() {
			return median;
		}
		public void setMedian(double median) {
			this.median = median;
		}
		public double getThirdQuartile() {
			return thirdQuartile;
		}
		public void setThirdQuartile(double thirdQuartile) {
			this.thirdQuartile = thirdQuartile;
		}
	}

	public List<PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType> getDataForChartPerLinkTypeList() {
		return dataForChartPerLinkTypeList;
	}

	public void setDataForChartPerLinkTypeList(
			List<PPM_Error_Chart_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList) {
		this.dataForChartPerLinkTypeList = dataForChartPerLinkTypeList;
	}

	
	
}
