package org.yeastrc.xlink.www.qc_data.psm_level_data.objects;

import java.util.List;

/**
 * 
 *
 */
public class ChargeStateCountsResults {

	private List<ChargeStateCountsResultsPerLinkType> resultsPerLinkTypeList;
	
	/**
	 * 
	 *
	 */
	public static class ChargeStateCountsResultsPerLinkType {
		
		private String linkType;
		private List<ChargeStateCountsResultsPerChargeValue> resultsPerChargeValueList;

		public List<ChargeStateCountsResultsPerChargeValue> getResultsPerChargeValueList() {
			return resultsPerChargeValueList;
		}
		public void setResultsPerChargeValueList(List<ChargeStateCountsResultsPerChargeValue> resultsPerChargeValueList) {
			this.resultsPerChargeValueList = resultsPerChargeValueList;
		}
		public String getLinkType() {
			return linkType;
		}
		public void setLinkType(String linkType) {
			this.linkType = linkType;
		}
	}
	
	/**
	 * 
	 *
	 */
	public static class ChargeStateCountsResultsPerChargeValue {
		
		private int chargeValue;
		private long chargeCount;
		
		public int getChargeValue() {
			return chargeValue;
		}
		public void setChargeValue(int chargeValue) {
			this.chargeValue = chargeValue;
		}
		public long getChargeCount() {
			return chargeCount;
		}
		public void setChargeCount(long chargeCount) {
			this.chargeCount = chargeCount;
		}
	}
	public List<ChargeStateCountsResultsPerLinkType> getResultsPerLinkTypeList() {
		return resultsPerLinkTypeList;
	}
	public void setResultsPerLinkTypeList(List<ChargeStateCountsResultsPerLinkType> resultsPerLinkTypeList) {
		this.resultsPerLinkTypeList = resultsPerLinkTypeList;
	}

}
