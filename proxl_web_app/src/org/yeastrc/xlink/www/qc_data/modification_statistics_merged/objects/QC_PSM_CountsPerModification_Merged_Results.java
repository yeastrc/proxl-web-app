package org.yeastrc.xlink.www.qc_data.modification_statistics_merged.objects;

import java.util.List;


/**
 * Results from QC_PSM_CountsPerModification_Merged
 *
 */
public class QC_PSM_CountsPerModification_Merged_Results {

	private boolean foundData;
	private List<Integer> searchIds;
	private List<QC_PSM_CountsPerModificationResultsPerLinkType_Merged> resultsPerLinkTypeList;
	
	/**
	 * 
	 *
	 */
	public static class QC_PSM_CountsPerModificationResultsPerLinkType_Merged {
		
		private String linkType;
		private boolean foundDataForLinkType;
		private List<QC_PSM_CountsPerModificationResultsPerModMass_Merged> resultsPerModMassList;
		public String getLinkType() {
			return linkType;
		}
		public void setLinkType(String linkType) {
			this.linkType = linkType;
		}
		public List<QC_PSM_CountsPerModificationResultsPerModMass_Merged> getResultsPerModMassList() {
			return resultsPerModMassList;
		}
		public void setResultsPerModMassList(List<QC_PSM_CountsPerModificationResultsPerModMass_Merged> resultsPerModMassList) {
			this.resultsPerModMassList = resultsPerModMassList;
		}
		public boolean isFoundDataForLinkType() {
			return foundDataForLinkType;
		}
		public void setFoundDataForLinkType(boolean foundDataForLinkType) {
			this.foundDataForLinkType = foundDataForLinkType;
		}

	}

	/**
	 * 
	 *
	 */
	public static class QC_PSM_CountsPerModificationResultsPerModMass_Merged {
		
		private String modMassLabel;
		private boolean noModMass;
		private List<QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged> countPerSearchIdList;
		public String getModMassLabel() {
			return modMassLabel;
		}
		public void setModMassLabel(String modMassLabel) {
			this.modMassLabel = modMassLabel;
		}
		public boolean isNoModMass() {
			return noModMass;
		}
		public void setNoModMass(boolean noModMass) {
			this.noModMass = noModMass;
		}
		public List<QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged> getCountPerSearchIdList() {
			return countPerSearchIdList;
		}
		public void setCountPerSearchIdList(
				List<QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged> countPerSearchIdList) {
			this.countPerSearchIdList = countPerSearchIdList;
		}

	}
	
	/**
	 * 
	 *
	 */
	public static class QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged {
		private int searchId;
		private int count;
		private int totalCount;
		public int getSearchId() {
			return searchId;
		}
		public void setSearchId(int searchId) {
			this.searchId = searchId;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public int getTotalCount() {
			return totalCount;
		}
		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
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

	public List<QC_PSM_CountsPerModificationResultsPerLinkType_Merged> getResultsPerLinkTypeList() {
		return resultsPerLinkTypeList;
	}

	public void setResultsPerLinkTypeList(
			List<QC_PSM_CountsPerModificationResultsPerLinkType_Merged> resultsPerLinkTypeList) {
		this.resultsPerLinkTypeList = resultsPerLinkTypeList;
	}




}
