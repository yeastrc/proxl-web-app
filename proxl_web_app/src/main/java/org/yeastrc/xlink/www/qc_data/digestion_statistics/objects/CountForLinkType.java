package org.yeastrc.xlink.www.qc_data.digestion_statistics.objects;

/**
 * 
 *
 */
public class CountForLinkType {

	//  Per Reported peptide counts
	private int totalReportedPeptideCount;
	private int missedCleavageReportedPeptideCount;

	//  Per PSM counts
	private int totalPSMCount;
	private int missedCleavagePSMCount;

	//  Total Cleavage points counts
	private int missedCleavageCount;

	
	private String linkType;
	
	//  Per Reported peptide counts
	public void incrementTotalReportedPeptideCount() {
		totalReportedPeptideCount++;
	}
	public void incrementMissedCleavageReportedPeptideCount() {
		missedCleavageReportedPeptideCount++;
	}

	//  Per PSM counts
	public void addToTotalPSMCount( int countToAdd ) {
		totalPSMCount += countToAdd;
	}
	public void addToMissedCleavagePSMCount( int countToAdd ) {
		missedCleavagePSMCount += countToAdd;
	}

	//  Total Cleavage points counts
	public void addToMissedCleavageCount( int countToAdd ) {
		missedCleavageCount += countToAdd;
	}
	
	public String getLinkType() {
		return linkType;
	}
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
	public int getTotalReportedPeptideCount() {
		return totalReportedPeptideCount;
	}
	public void setTotalReportedPeptideCount(int totalReportedPeptideCount) {
		this.totalReportedPeptideCount = totalReportedPeptideCount;
	}
	public int getMissedCleavageReportedPeptideCount() {
		return missedCleavageReportedPeptideCount;
	}
	public void setMissedCleavageReportedPeptideCount(int missedCleavageReportedPeptideCount) {
		this.missedCleavageReportedPeptideCount = missedCleavageReportedPeptideCount;
	}
	public int getMissedCleavageCount() {
		return missedCleavageCount;
	}
	public void setMissedCleavageCount(int missedCleavageCount) {
		this.missedCleavageCount = missedCleavageCount;
	}
	public int getTotalPSMCount() {
		return totalPSMCount;
	}
	public void setTotalPSMCount(int totalPSMCount) {
		this.totalPSMCount = totalPSMCount;
	}
	public int getMissedCleavagePSMCount() {
		return missedCleavagePSMCount;
	}
	public void setMissedCleavagePSMCount(int missedCleavagePSMCount) {
		this.missedCleavagePSMCount = missedCleavagePSMCount;
	}

}
