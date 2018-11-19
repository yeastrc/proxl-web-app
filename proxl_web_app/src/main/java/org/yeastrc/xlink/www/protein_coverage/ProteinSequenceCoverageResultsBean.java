package org.yeastrc.xlink.www.protein_coverage;

import java.util.Set;

public class ProteinSequenceCoverageResultsBean {

	private int proteinId;
	private Set<CoverageRangeBean> ranges;
	
	
	public int getProteinId() {
		return proteinId;
	}
	public void setProteinId(int proteinId) {
		this.proteinId = proteinId;
	}
	public Set<CoverageRangeBean> getRanges() {
		return ranges;
	}
	public void setRanges( Set<CoverageRangeBean> ranges) {
		this.ranges = ranges;
	}
	
	
	
}
