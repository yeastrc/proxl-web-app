package org.yeastrc.xlink.www.objects;

import java.util.List;

/**
 *  Web Service Returned Result
 *
 */
public class ReportedPeptidesPerSearchForMergedPeptidePageResult {

	private List<ReportedPeptidesPerSearchForMergedPeptidePageResultEntry> reportedPeptidesPerProjectSearchIdList;

	public List<ReportedPeptidesPerSearchForMergedPeptidePageResultEntry> getReportedPeptidesPerProjectSearchIdList() {
		return reportedPeptidesPerProjectSearchIdList;
	}
	public void setReportedPeptidesPerProjectSearchIdList(
			List<ReportedPeptidesPerSearchForMergedPeptidePageResultEntry> reportedPeptidesPerProjectSearchIdList) {
		this.reportedPeptidesPerProjectSearchIdList = reportedPeptidesPerProjectSearchIdList;
	}
}
