package org.yeastrc.xlink.www.qc_data.psm_level_data_merged.objects;

import java.util.List;

/**
 * results from ChargeStateCounts_Merged
 *
 */
public class ChargeStateCounts_Merged_Results {

	private boolean foundData;
	private List<Integer> searchIds;
	private List<ChargeStateCountsResultsForLinkType> resultsPerLinkTypeList;

	/**
	 * 
	 * For Single link type, data for one chart
	 */
	public static class ChargeStateCountsResultsForLinkType {

		private String linkType;
		private boolean dataFound;
		private List<ChargeStateCountsResultsForChargeValue> dataForChartPerChargeValueList;
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
		public List<ChargeStateCountsResultsForChargeValue> getDataForChartPerChargeValueList() {
			return dataForChartPerChargeValueList;
		}
		public void setDataForChartPerChargeValueList(
				List<ChargeStateCountsResultsForChargeValue> dataForChartPerChargeValueList) {
			this.dataForChartPerChargeValueList = dataForChartPerChargeValueList;
		}
	}

	/**
	 * For Single charge value, part of a chart
	 *
	 */
	public static class ChargeStateCountsResultsForChargeValue {
		
		private int charge;
		private List<ChargeStateCountsResultsForSearchId> countPerSearchIdList;
		
		public List<ChargeStateCountsResultsForSearchId> getCountPerSearchIdList() {
			return countPerSearchIdList;
		}
		public void setCountPerSearchIdList(List<ChargeStateCountsResultsForSearchId> countPerSearchIdList) {
			this.countPerSearchIdList = countPerSearchIdList;
		}
		public int getCharge() {
			return charge;
		}
		public void setCharge(int charge) {
			this.charge = charge;
		}
		
	}
	
	/**
	 * 
	 * For Single search id, part of a chart
	 */
	public static class ChargeStateCountsResultsForSearchId {

		private int searchId;
		private long count;
		
		public long getCount() {
			return count;
		}
		public void setCount(long count) {
			this.count = count;
		}
		public int getSearchId() {
			return searchId;
		}
		public void setSearchId(int searchId) {
			this.searchId = searchId;
		}
	}

	public boolean isFoundData() {
		return foundData;
	}

	public void setFoundData(boolean foundData) {
		this.foundData = foundData;
	}

	public List<Integer> getSearchIds() {
		return searchIds;
	}

	public void setSearchIds(List<Integer> searchIds) {
		this.searchIds = searchIds;
	}

	public List<ChargeStateCountsResultsForLinkType> getResultsPerLinkTypeList() {
		return resultsPerLinkTypeList;
	}

	public void setResultsPerLinkTypeList(List<ChargeStateCountsResultsForLinkType> resultsPerLinkTypeList) {
		this.resultsPerLinkTypeList = resultsPerLinkTypeList;
	}
		
}
