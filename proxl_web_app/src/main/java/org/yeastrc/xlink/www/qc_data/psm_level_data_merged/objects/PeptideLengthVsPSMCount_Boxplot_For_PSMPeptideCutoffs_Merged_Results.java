package org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects;

import java.util.List;
import java.util.Map;

/**
 * Results for PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged
 *
 */
public class PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_Results {


	private List<PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType> dataForChartPerLinkTypeList;

	/**
	 * 
	 * For Single link type, data for one chart
	 */
	public static class PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType {

		private String linkType;
		private boolean dataFound;
		private Map<Integer, PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId> dataForChartPerSearchIdMap_KeyProjectSearchId;
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
		public Map<Integer, PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId> getDataForChartPerSearchIdMap_KeyProjectSearchId() {
			return dataForChartPerSearchIdMap_KeyProjectSearchId;
		}
		public void setDataForChartPerSearchIdMap_KeyProjectSearchId(
				Map<Integer, PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId> dataForChartPerSearchIdMap_KeyProjectSearchId) {
			this.dataForChartPerSearchIdMap_KeyProjectSearchId = dataForChartPerSearchIdMap_KeyProjectSearchId;
		}
		
	}

	/**
	 * 
	 * For Single search id, part of a chart
	 */
	public static class PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForSearchId {

		private int searchId;
		private boolean dataFound;

		//  Box chart values
		private double chartIntervalMax;
		private double chartIntervalMin;
		private double firstQuartile;
		private double median;
		private double thirdQuartile;
		
		private List<Integer> peptideLengths_outliers;
		
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
		public List<Integer> getPeptideLengths_outliers() {
			return peptideLengths_outliers;
		}
		public void setPeptideLengths_outliers(List<Integer> peptideLengths_outliers) {
			this.peptideLengths_outliers = peptideLengths_outliers;
		}
		
	}

	public List<PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType> getDataForChartPerLinkTypeList() {
		return dataForChartPerLinkTypeList;
	}

	public void setDataForChartPerLinkTypeList(
			List<PeptideLengthVsPSMCount_Boxplot_For_PSMPeptideCutoffs_Merged_ResultsForLinkType> dataForChartPerLinkTypeList) {
		this.dataForChartPerLinkTypeList = dataForChartPerLinkTypeList;
	}
}
