package org.yeastrc.xlink.www.qc_data.scan_level_data.objects;

public class MS2_Counts_Where_PSMs_MeetsCutoffsResults {

	private long crosslinkCount;
	private long looplinkCount;
	/**
	 * includes dimers
	 */
	private long unlinkedCount;
	
	public long getCrosslinkCount() {
		return crosslinkCount;
	}
	public void setCrosslinkCount(long crosslinkCount) {
		this.crosslinkCount = crosslinkCount;
	}
	public long getLooplinkCount() {
		return looplinkCount;
	}
	public void setLooplinkCount(long looplinkCount) {
		this.looplinkCount = looplinkCount;
	}
	public long getUnlinkedCount() {
		return unlinkedCount;
	}
	public void setUnlinkedCount(long unlinkedCount) {
		this.unlinkedCount = unlinkedCount;
	}
}
