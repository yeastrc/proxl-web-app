package org.yeastrc.xlink.www.objects;

import java.util.Collection;


import java.util.List;
import java.util.Map;


public class SequenceCoverageData {

	
	public Collection<Integer> getExcludeTaxonomy() {
		return excludeTaxonomy;
	}
	public void setExcludeTaxonomy(Collection<Integer> excludeTaxonomy) {
		this.excludeTaxonomy = excludeTaxonomy;
	}
	public boolean isFilterNonUniquePeptides() {
		return filterNonUniquePeptides;
	}
	public void setFilterNonUniquePeptides(boolean filterNonUniquePeptides) {
		this.filterNonUniquePeptides = filterNonUniquePeptides;
	}
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


	private Collection<Integer> excludeTaxonomy;
//	private Collection<SearchDTO> searches;
	private boolean filterNonUniquePeptides;
	
	private Map<Integer, Double> coverages;
	private Map<Integer, List<SequenceCoverageRange>>  ranges;
	
}
