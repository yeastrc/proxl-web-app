package org.yeastrc.xlink.www.objects;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;

public class ImageViewerData {

	public Set<Integer> getProteins() {
		return proteins;
	}

	public void setProteins(Set<Integer> proteins) {
		this.proteins = proteins;
	}

	public Map<Integer, Integer> getProteinLengths() {
		return proteinLengths;
	}

	public void setProteinLengths(Map<Integer, Integer> proteinLengths) {
		this.proteinLengths = proteinLengths;
	}

	public Map<Integer, String> getProteinNames() {
		return proteinNames;
	}

	public void setProteinNames(Map<Integer, String> proteinNames) {
		this.proteinNames = proteinNames;
	}





	

	public Map<Integer, Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>>> getProteinLinkPositions() {
		return proteinLinkPositions;
	}

	public void setProteinLinkPositions(
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>>> proteinLinkPositions) {
		this.proteinLinkPositions = proteinLinkPositions;
	}

	public Map<Integer, Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>>> getProteinLoopLinkPositions() {
		return proteinLoopLinkPositions;
	}

	public void setProteinLoopLinkPositions(
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>>> proteinLoopLinkPositions) {
		this.proteinLoopLinkPositions = proteinLoopLinkPositions;
	}

	public Collection<SearchDTO> getSearches() {
		return searches;
	}

	public void setSearches(Collection<SearchDTO> searches) {
		this.searches = searches;
	}
	


	public Map<Integer, String> getTaxonomies() {
		return taxonomies;
	}

	public void setTaxonomies(Map<Integer, String> taxonomies) {
		this.taxonomies = taxonomies;
	}


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


	public Map<Integer, Map<Integer, Set<Integer>>> getProteinMonoLinkPositions() {
		return proteinMonoLinkPositions;
	}

	public void setProteinMonoLinkPositions(
			Map<Integer, Map<Integer, Set<Integer>>> proteinMonoLinkPositions) {
		this.proteinMonoLinkPositions = proteinMonoLinkPositions;
	}



	public Collection<Integer> getExcludeType() {
		return excludeType;
	}

	public void setExcludeType(Collection<Integer> excludeType) {
		this.excludeType = excludeType;
	}

	public Map<Integer, Collection<Integer>> getLinkablePositions() {
		return linkablePositions;
	}

	public void setLinkablePositions(
			Map<Integer, Collection<Integer>> linkablePositions) {
		this.linkablePositions = linkablePositions;
	}

	public boolean isFilterOnlyOnePSM() {
		return filterOnlyOnePSM;
	}

	public void setFilterOnlyOnePSM(boolean filterOnlyOnePSM) {
		this.filterOnlyOnePSM = filterOnlyOnePSM;
	}

	public boolean isFilterOnlyOnePeptide() {
		return filterOnlyOnePeptide;
	}

	public void setFilterOnlyOnePeptide(boolean filterOnlyOnePeptide) {
		this.filterOnlyOnePeptide = filterOnlyOnePeptide;
	}

	
	
	
	public Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> getCrosslinkPSMCounts() {
		return crosslinkPSMCounts;
	}

	public void setCrosslinkPSMCounts(
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> crosslinkPSMCounts) {
		this.crosslinkPSMCounts = crosslinkPSMCounts;
	}

	public Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> getLooplinkPSMCounts() {
		return looplinkPSMCounts;
	}

	public void setLooplinkPSMCounts(
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> looplinkPSMCounts) {
		this.looplinkPSMCounts = looplinkPSMCounts;
	}

	public Map<Integer, Map<Integer, Integer>> getMonolinkPSMCounts() {
		return monolinkPSMCounts;
	}

	public void setMonolinkPSMCounts(
			Map<Integer, Map<Integer, Integer>> monolinkPSMCounts) {
		this.monolinkPSMCounts = monolinkPSMCounts;
	}
	
	public CutoffValuesRootLevel getCutoffs() {
		return cutoffs;
	}

	public void setCutoffs(CutoffValuesRootLevel cutoffs) {
		this.cutoffs = cutoffs;
	}




	private CutoffValuesRootLevel cutoffs;


	private Collection<Integer> excludeTaxonomy;
	private Collection<Integer> excludeType;
	private Set<Integer> proteins;
	private Collection<SearchDTO> searches;
	private Map<Integer, Integer> proteinLengths;
	private Map<Integer, String> proteinNames;
	private Map<Integer, Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>>> proteinLinkPositions;
	private Map<Integer, Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>>> proteinLoopLinkPositions;
	private Map<Integer, Map<Integer, Set<Integer>>> proteinMonoLinkPositions;
	private Map<Integer, String> taxonomies;
	private boolean filterNonUniquePeptides;
	private boolean filterOnlyOnePSM;
	private boolean filterOnlyOnePeptide;
	
	private Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> crosslinkPSMCounts;
	private Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> looplinkPSMCounts;
	private Map<Integer, Map<Integer, Integer>> monolinkPSMCounts;


	private Map<Integer, Collection<Integer>> linkablePositions;

}
