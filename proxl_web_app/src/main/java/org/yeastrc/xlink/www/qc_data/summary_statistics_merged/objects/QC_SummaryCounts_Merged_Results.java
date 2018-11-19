package org.yeastrc.xlink.www.qc_data.summary_statistics_merged.objects;

import java.util.List;
import java.util.Map;


/**
 * Results from QC_SummaryCounts_Merged
 *
 */
public class QC_SummaryCounts_Merged_Results {

	private boolean foundData;
	private List<Integer> searchIds;
	private List<QC_SummaryCountsResultsPerLinkType_Merged> psmCountPerLinkTypeList;
	private List<QC_SummaryCountsResultsPerLinkType_Merged> reportedPeptideCountPerLinkTypeList;
	private List<QC_SummaryCountsResultsPerLinkType_Merged> proteinCountPerLinkTypeList;
	
	/**
	 * 
	 *
	 */
	public static class QC_SummaryCountsResultsPerLinkType_Merged {
		
		private String linkType;
		private boolean combinedEntry;
		private Map<Integer,QC_SummaryCountsResults_PerSearchId_Merged> countPerSearchIdMap_KeyProjectSearchId;
		public String getLinkType() {
			return linkType;
		}
		public void setLinkType(String linkType) {
			this.linkType = linkType;
		}
		public boolean isCombinedEntry() {
			return combinedEntry;
		}
		public void setCombinedEntry(boolean combinedEntry) {
			this.combinedEntry = combinedEntry;
		}
		public Map<Integer, QC_SummaryCountsResults_PerSearchId_Merged> getCountPerSearchIdMap_KeyProjectSearchId() {
			return countPerSearchIdMap_KeyProjectSearchId;
		}
		public void setCountPerSearchIdMap_KeyProjectSearchId(
				Map<Integer, QC_SummaryCountsResults_PerSearchId_Merged> countPerSearchIdMap_KeyProjectSearchId) {
			this.countPerSearchIdMap_KeyProjectSearchId = countPerSearchIdMap_KeyProjectSearchId;
		}

	}

	/**
	 * 
	 *
	 */
	public static class QC_SummaryCountsResults_PerSearchId_Merged {
		private int searchId;
		private int count;
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
		
	}

	public List<QC_SummaryCountsResultsPerLinkType_Merged> getPsmCountPerLinkTypeList() {
		return psmCountPerLinkTypeList;
	}

	public void setPsmCountPerLinkTypeList(List<QC_SummaryCountsResultsPerLinkType_Merged> psmCountPerLinkTypeList) {
		this.psmCountPerLinkTypeList = psmCountPerLinkTypeList;
	}

	public List<QC_SummaryCountsResultsPerLinkType_Merged> getReportedPeptideCountPerLinkTypeList() {
		return reportedPeptideCountPerLinkTypeList;
	}

	public void setReportedPeptideCountPerLinkTypeList(
			List<QC_SummaryCountsResultsPerLinkType_Merged> reportedPeptideCountPerLinkTypeList) {
		this.reportedPeptideCountPerLinkTypeList = reportedPeptideCountPerLinkTypeList;
	}

	public List<QC_SummaryCountsResultsPerLinkType_Merged> getProteinCountPerLinkTypeList() {
		return proteinCountPerLinkTypeList;
	}

	public void setProteinCountPerLinkTypeList(List<QC_SummaryCountsResultsPerLinkType_Merged> proteinCountPerLinkTypeList) {
		this.proteinCountPerLinkTypeList = proteinCountPerLinkTypeList;
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
}
