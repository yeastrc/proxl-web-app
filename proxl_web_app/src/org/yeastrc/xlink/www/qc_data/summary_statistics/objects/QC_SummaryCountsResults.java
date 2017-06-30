package org.yeastrc.xlink.www.qc_data.summary_statistics.objects;

import java.util.List;


/**
 * Results for QC_SummaryCounts
 *
 */
public class QC_SummaryCountsResults {

	private List<QC_SummaryCountsResultsPerLinkType> resultsPerLinkTypeList;
	
	/**
	 * 
	 *
	 */
	public static class QC_SummaryCountsResultsPerLinkType {
		
		private String linkType;
		private int psmCount;
		/**
		 * Unique in case of more than one search id
		 */
		private int uniqueReportedPeptideCount;
		private int uniqueProteinSequenceIdCount;
		
		public int getUniqueProteinSequenceIdCount() {
			return uniqueProteinSequenceIdCount;
		}
		public void setUniqueProteinSequenceIdCount(int uniqueProteinSequenceIdCount) {
			this.uniqueProteinSequenceIdCount = uniqueProteinSequenceIdCount;
		}
		public String getLinkType() {
			return linkType;
		}
		public void setLinkType(String linkType) {
			this.linkType = linkType;
		}
		public int getPsmCount() {
			return psmCount;
		}
		public void setPsmCount(int psmCount) {
			this.psmCount = psmCount;
		}
		public int getUniqueReportedPeptideCount() {
			return uniqueReportedPeptideCount;
		}
		public void setUniqueReportedPeptideCount(int uniqueReportedPeptideCount) {
			this.uniqueReportedPeptideCount = uniqueReportedPeptideCount;
		}
	}

	public List<QC_SummaryCountsResultsPerLinkType> getResultsPerLinkTypeList() {
		return resultsPerLinkTypeList;
	}

	public void setResultsPerLinkTypeList(List<QC_SummaryCountsResultsPerLinkType> resultsPerLinkTypeList) {
		this.resultsPerLinkTypeList = resultsPerLinkTypeList;
	}

}
