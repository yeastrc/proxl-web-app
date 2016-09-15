package org.yeastrc.xlink.www.objects;

import java.util.List;
import java.util.Map;


public class SequenceCoverageData {
	
	public Map<Integer, Double> getCoverages() {
		return coverages;
	}
	public void setCoverages(Map<Integer, Double> coverages) {
		this.coverages = coverages;
	}
	public Map<Integer, List<SequenceCoverageRange>> getRanges() {
		return ranges;
	}
	public void setRanges(Map<Integer, List<SequenceCoverageRange>> ranges) {
		this.ranges = ranges;
	}
	
	private Map<Integer, Double> coverages;
	private Map<Integer, List<SequenceCoverageRange>>  ranges;
	
}
