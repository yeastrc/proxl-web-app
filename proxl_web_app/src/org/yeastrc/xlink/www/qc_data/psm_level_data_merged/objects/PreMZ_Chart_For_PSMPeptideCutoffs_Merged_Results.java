package org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects;

import java.util.List;
import java.util.Map;

/**
 * results from  PreMZ_Chart_For_PSMPeptideCutoffs_Merged
 *
 */
public class PreMZ_Chart_For_PSMPeptideCutoffs_Merged_Results {

	private List<PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList;

	/**
	 * 
	 * For Single link type, data for one chart
	 */
	public static class PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType {

		private String linkType;
		private boolean dataFound;
		private Map<Integer, PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId> dataForChartPerSearchIdMap_KeyProjectSearchId;
		
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
		public Map<Integer, PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId> getDataForChartPerSearchIdMap_KeyProjectSearchId() {
			return dataForChartPerSearchIdMap_KeyProjectSearchId;
		}
		public void setDataForChartPerSearchIdMap_KeyProjectSearchId(
				Map<Integer, PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId> dataForChartPerSearchIdMap_KeyProjectSearchId) {
			this.dataForChartPerSearchIdMap_KeyProjectSearchId = dataForChartPerSearchIdMap_KeyProjectSearchId;
		}
		
	}

	/**
	 * 
	 * For Single search id, part of a chart
	 */
	public static class PreMZ_Chart_For_PSMPeptideCutoffsResultsForSearchId {

		private int searchId;
		private boolean dataFound;

		//  Box chart values
		private double chartIntervalMax;
		private double chartIntervalMin;
		private double firstQuartile;
		private double median;
		private double thirdQuartile;
		
		private List<Double> preMZ_outliers;
		
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
		public List<Double> getPreMZ_outliers() {
			return preMZ_outliers;
		}
		public void setPreMZ_outliers(List<Double> preMZ_outliers) {
			this.preMZ_outliers = preMZ_outliers;
		}
		
	}

	public List<PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType> getDataForChartPerLinkTypeList() {
		return dataForChartPerLinkTypeList;
	}

	public void setDataForChartPerLinkTypeList(
			List<PreMZ_Chart_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList) {
		this.dataForChartPerLinkTypeList = dataForChartPerLinkTypeList;
	}
}
