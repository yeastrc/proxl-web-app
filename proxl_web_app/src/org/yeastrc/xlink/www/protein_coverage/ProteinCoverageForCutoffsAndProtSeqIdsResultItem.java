package org.yeastrc.xlink.www.protein_coverage;

/**
 * One entry in result from PeptideProteinPositionsForCutoffsAndProtSeqIdsSearcher
 *
 */
public class ProteinCoverageForCutoffsAndProtSeqIdsResultItem {

	private int searchId;
	private int reportedPeptideId;
//	private int peptideIdInfoOnly; //  Not to be used for peptide to protein mapping
	private int proteinSequenceVersionId;
	private int proteinStartPosition;
	private int proteinEndPosition;
	
	
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public int getReportedPeptideId() {
		return reportedPeptideId;
	}
	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}
	public int getProteinSequenceVersionId() {
		return proteinSequenceVersionId;
	}
	public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
		this.proteinSequenceVersionId = proteinSequenceVersionId;
	}
	public int getProteinStartPosition() {
		return proteinStartPosition;
	}
	public void setProteinStartPosition(int proteinStartPosition) {
		this.proteinStartPosition = proteinStartPosition;
	}
	public int getProteinEndPosition() {
		return proteinEndPosition;
	}
	public void setProteinEndPosition(int proteinEndPosition) {
		this.proteinEndPosition = proteinEndPosition;
	}


}
