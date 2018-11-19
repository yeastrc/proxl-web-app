package org.yeastrc.xlink.www.objects;

import java.util.List;

public class PsmScoreVsScoreSearcherResults {
	private List<PsmScoreVsScoreEntry> crosslinkEntries;
	private List<PsmScoreVsScoreEntry> looplinkEntries;
	private List<PsmScoreVsScoreEntry> unlinkedEntries;
	public List<PsmScoreVsScoreEntry> getCrosslinkEntries() {
		return crosslinkEntries;
	}
	public void setCrosslinkEntries(List<PsmScoreVsScoreEntry> crosslinkEntries) {
		this.crosslinkEntries = crosslinkEntries;
	}
	public List<PsmScoreVsScoreEntry> getLooplinkEntries() {
		return looplinkEntries;
	}
	public void setLooplinkEntries(List<PsmScoreVsScoreEntry> looplinkEntries) {
		this.looplinkEntries = looplinkEntries;
	}
	public List<PsmScoreVsScoreEntry> getUnlinkedEntries() {
		return unlinkedEntries;
	}
	public void setUnlinkedEntries(List<PsmScoreVsScoreEntry> unlinkedEntries) {
		this.unlinkedEntries = unlinkedEntries;
	}
}
