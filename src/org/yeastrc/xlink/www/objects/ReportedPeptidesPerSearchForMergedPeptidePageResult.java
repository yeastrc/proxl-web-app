package org.yeastrc.xlink.www.objects;

import java.util.Map;

/**
 *  Web Service Returned Result
 *
 */
public class ReportedPeptidesPerSearchForMergedPeptidePageResult {

	/**
	 * Key is search id
	 */
	private Map<Integer, ReportedPeptidesPerSearchForMergedPeptidePageResultEntry> reportedPeptidesPerSearchIdMap;

	/**
	 * Key is search id
	 * @return
	 */
	public Map<Integer, ReportedPeptidesPerSearchForMergedPeptidePageResultEntry> getReportedPeptidesPerSearchIdMap() {
		return reportedPeptidesPerSearchIdMap;
	}

	/**
	 * Key is search id
	 * @param reportedPeptidesPerSearchIdMap
	 */
	public void setReportedPeptidesPerSearchIdMap(
			Map<Integer, ReportedPeptidesPerSearchForMergedPeptidePageResultEntry> reportedPeptidesPerSearchIdMap) {
		this.reportedPeptidesPerSearchIdMap = reportedPeptidesPerSearchIdMap;
	}

}
