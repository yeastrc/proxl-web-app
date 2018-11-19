package org.yeastrc.xlink.www.objects;

import java.util.Map;

/**
 *  Web Service Returned Result
 *
 */
public class ReportedPeptidesPerSearchForMergedPeptidePageResult {

	/**
	 * Key is project search id
	 */
	private Map<Integer, ReportedPeptidesPerSearchForMergedPeptidePageResultEntry> reportedPeptidesPerProjectSearchIdMap;

	/**
	 * Key is project search id
	 * @return
	 */
	public Map<Integer, ReportedPeptidesPerSearchForMergedPeptidePageResultEntry> getReportedPeptidesPerProjectSearchIdMap() {
		return reportedPeptidesPerProjectSearchIdMap;
	}

	/**
	 * Key is Project search id
	 * @param reportedPeptidesPerProjectSearchIdMap
	 */
	public void setReportedPeptidesPerProjectSearchIdMap(
			Map<Integer, ReportedPeptidesPerSearchForMergedPeptidePageResultEntry> reportedPeptidesPerProjectSearchIdMap) {
		this.reportedPeptidesPerProjectSearchIdMap = reportedPeptidesPerProjectSearchIdMap;
	}

}
