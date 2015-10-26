package org.yeastrc.xlink.www.objects;

import java.util.Collection;


import java.util.List;
import java.util.Map;

import org.yeastrc.xlink.dto.SearchDTO;

public class SequenceCoverageData {

	
	
	public double getPsmQValueCutoff() {
		return psmQValueCutoff;
	}
	public void setPsmQValueCutoff(double psmQValueCutoff) {
		this.psmQValueCutoff = psmQValueCutoff;
	}
	public double getPeptideQValueCutoff() {
		return peptideQValueCutoff;
	}
	public void setPeptideQValueCutoff(double peptideQValueCutoff) {
		this.peptideQValueCutoff = peptideQValueCutoff;
	}
	public Collection<Integer> getExcludeTaxonomy() {
		return excludeTaxonomy;
	}
	public void setExcludeTaxonomy(Collection<Integer> excludeTaxonomy) {
		this.excludeTaxonomy = excludeTaxonomy;
	}
	public Collection<SearchDTO> getSearches() {
		return searches;
	}
	public void setSearches(Collection<SearchDTO> searches) {
		this.searches = searches;
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


	private double psmQValueCutoff;
	private double peptideQValueCutoff;
	private Collection<Integer> excludeTaxonomy;
	private Collection<SearchDTO> searches;
	private boolean filterNonUniquePeptides;
	private Map<Integer, Double> coverages;
	private Map<Integer, List<SequenceCoverageRange>>  ranges;
	
}
