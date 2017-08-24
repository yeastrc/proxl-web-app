package org.yeastrc.xlink.www.qc_data.digestion_statistics_merged.objects;

import java.util.List;

/**
 * Results for QC_MissingCleavageReportedPeptidesCount_Merged
 *
 */
public class QC_MissingCleavageReportedPeptidesCount_Merged_Results {


	private boolean foundData;
	private List<Integer> searchIds;
	private List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> peptideCountPerLinkTypeList;
	private List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> perPeptideCountPerLinkTypeList;
	private List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> psmCountPerLinkTypeList;
	
	/**
	 * 
	 *
	 */
	public static class QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged {
		
		private String linkType;
		private List<QC_MissingCleavageReportedPeptidesCountResults_PerSearchId_Merged> countPerSearchIdList;
		public String getLinkType() {
			return linkType;
		}
		public void setLinkType(String linkType) {
			this.linkType = linkType;
		}
		public List<QC_MissingCleavageReportedPeptidesCountResults_PerSearchId_Merged> getCountPerSearchIdList() {
			return countPerSearchIdList;
		}
		public void setCountPerSearchIdList(List<QC_MissingCleavageReportedPeptidesCountResults_PerSearchId_Merged> countPerSearchIdList) {
			this.countPerSearchIdList = countPerSearchIdList;
		}
	}

	/**
	 * 
	 *
	 */
	public static class QC_MissingCleavageReportedPeptidesCountResults_PerSearchId_Merged {
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

	public List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> getPeptideCountPerLinkTypeList() {
		return peptideCountPerLinkTypeList;
	}

	public void setPeptideCountPerLinkTypeList(
			List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> peptideCountPerLinkTypeList) {
		this.peptideCountPerLinkTypeList = peptideCountPerLinkTypeList;
	}

	public List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> getPerPeptideCountPerLinkTypeList() {
		return perPeptideCountPerLinkTypeList;
	}

	public void setPerPeptideCountPerLinkTypeList(
			List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> perPeptideCountPerLinkTypeList) {
		this.perPeptideCountPerLinkTypeList = perPeptideCountPerLinkTypeList;
	}

	public List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> getPsmCountPerLinkTypeList() {
		return psmCountPerLinkTypeList;
	}

	public void setPsmCountPerLinkTypeList(
			List<QC_MissingCleavageReportedPeptidesCountResultsPerLinkType_Merged> psmCountPerLinkTypeList) {
		this.psmCountPerLinkTypeList = psmCountPerLinkTypeList;
	}

}
