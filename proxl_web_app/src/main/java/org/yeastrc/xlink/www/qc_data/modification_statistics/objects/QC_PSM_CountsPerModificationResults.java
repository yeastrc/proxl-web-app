package org.yeastrc.xlink.www.qc_data.modification_statistics.objects;

import java.util.List;

/**
 * Results for QC_PSM_CountsPerModification
 *
 */
public class QC_PSM_CountsPerModificationResults {
	
	private List<QC_PSM_CountsPerModificationResultsPerLinkType> resultsPerLinkTypeList;
	
	/**
	 * 
	 *
	 */
	public static class QC_PSM_CountsPerModificationResultsPerLinkType {
		
		private String linkType;
		private int totalPSMCountForLinkType;
		private Integer psmCountNoMods;
		private List<QC_PSM_CountsPerModificationResultsPerModification> countPerModMassList;
		
		public String getLinkType() {
			return linkType;
		}
		public void setLinkType(String linkType) {
			this.linkType = linkType;
		}
		public int getTotalPSMCountForLinkType() {
			return totalPSMCountForLinkType;
		}
		public void setTotalPSMCountForLinkType(int totalPSMCountForLinkType) {
			this.totalPSMCountForLinkType = totalPSMCountForLinkType;
		}
		public List<QC_PSM_CountsPerModificationResultsPerModification> getCountPerModMassList() {
			return countPerModMassList;
		}
		public void setCountPerModMassList(List<QC_PSM_CountsPerModificationResultsPerModification> countPerModMassList) {
			this.countPerModMassList = countPerModMassList;
		}
		public Integer getPsmCountNoMods() {
			return psmCountNoMods;
		}
		public void setPsmCountNoMods(Integer psmCountNoMods) {
			this.psmCountNoMods = psmCountNoMods;
		}
	}

	/**
	 * 
	 *
	 */
	public static class QC_PSM_CountsPerModificationResultsPerModification {
		private String label;
		private int count;
		
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
	}
	
	public List<QC_PSM_CountsPerModificationResultsPerLinkType> getResultsPerLinkTypeList() {
		return resultsPerLinkTypeList;
	}

	public void setResultsPerLinkTypeList(List<QC_PSM_CountsPerModificationResultsPerLinkType> resultsPerLinkTypeList) {
		this.resultsPerLinkTypeList = resultsPerLinkTypeList;
	}

}
