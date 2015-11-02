package org.yeastrc.xlink.www.objects;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.MergedSearchLooplinkPeptideSearcher;
import org.yeastrc.xlink.www.searcher.MergedSearchPsmSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinLooplinkSearcher;

public class MergedSearchProteinLooplink implements IProteinLooplink, IMergedSearchLink {
	
	

	/* 
	 * The searches for the records that were found for this specific set of query keys
	 */
	@Override
	public Collection<SearchDTO> getSearches() {
		
		return searches;
	}
	
	public void setSearches(List<SearchDTO> searches) {
		this.searches = searches;
	}

	


	public Map<SearchDTO, SearchProteinLooplink> getSearchProteinLooplinks() throws Exception {
		
		if ( searchProteinLooplinks == null ) {

			// add search-level info for the protein looplinks:
			searchProteinLooplinks = new TreeMap<SearchDTO, SearchProteinLooplink>();
			for( SearchDTO search : searches ) {
				SearchProteinLooplink tlink = SearchProteinLooplinkSearcher.getInstance().search(search, 
						psmCutoff, 
						peptideCutoff, 
						this.getProtein().getNrProtein(),
						this.getProteinPosition1(),
						this.getProteinPosition2()
						);

				if( tlink != null ) {
					searchProteinLooplinks.put( search, tlink );
				}
			}
		}
		
		return searchProteinLooplinks;
	}


	/**
	 * @return true if any child level link "Best Peptide Q-Value" is not null
	 * @throws Exception 
	 */
	public boolean isAnyLinksHaveBestPeptideQValue() throws Exception {

		if ( anyLinksHaveBestPeptideQValueSet ) {
			
			return anyLinksHaveBestPeptideQValue;
		}
	
		/// Check if any child level link "Best Peptide Q-Value" is not null

		for ( Map.Entry<SearchDTO, SearchProteinLooplink> searchProteinLooplinksEntry : this.getSearchProteinLooplinks().entrySet() ) { 

			SearchProteinLooplink searchProteinLooplinkEntry = searchProteinLooplinksEntry.getValue();

			if ( searchProteinLooplinkEntry.getBestPeptideQValue() != null ) {

				anyLinksHaveBestPeptideQValue = true;
				break;
			}
		}
		
		anyLinksHaveBestPeptideQValueSet = true;
		
		return anyLinksHaveBestPeptideQValue;
	}


	
	public MergedSearchProtein getProtein() {
		return protein;
	}
	public void setProtein(MergedSearchProtein protein) {
		this.protein = protein;
	}
	public int getProteinPosition1() {
		return proteinPosition1;
	}
	public void setProteinPosition1(int proteinPosition1) {
		this.proteinPosition1 = proteinPosition1;
	}
	public int getProteinPosition2() {
		return proteinPosition2;
	}
	public void setProteinPosition2(int proteinPosition2) {
		this.proteinPosition2 = proteinPosition2;
	}

	public int getNumPsms() throws Exception {
		
		if( this.numPsms == null ) {
		
			this.numPsms = MergedSearchPsmSearcher.getInstance().getNumPsms( this );
		}
		
		return this.numPsms;
	}
	
	public void setNumPsms(Integer numPsms) {
		this.numPsms = numPsms;
	}

	
	public int getNumUniquePsms() {
		return numUniquePsms;
	}
	public void setNumUniquePsms(int numUniquePsms) {
		this.numUniquePsms = numUniquePsms;
	}
	public double getPsmCutoff() {
		return psmCutoff;
	}
	public void setPsmCutoff(double psmCutoff) {
		this.psmCutoff = psmCutoff;
	}
	public double getPeptideCutoff() {
		return peptideCutoff;
	}
	public void setPeptideCutoff(double peptideCutoff) {
		this.peptideCutoff = peptideCutoff;
	}
	
	
	public int getNumPeptides() throws Exception {
		if( this.numPeptides == -1 )
			this.numPeptides = MergedSearchLooplinkPeptideSearcher.getInstance().getNumPeptides( this );
			
		return this.numPeptides;
	}
	public int getNumUniquePeptides() throws Exception {
		if( this.numUniquePeptides == -1 )
			this.numUniquePeptides = MergedSearchLooplinkPeptideSearcher.getInstance().getNumUniquePeptides( this );
			
		return this.numUniquePeptides;
	}
	
	
	
	

	public int getNumSearches() {
		if( this.searches == null ) return 0;
		return this.searches.size();
	}


	
	public double getBestPSMQValue() {
		return bestPSMQValue;
	}
	public void setBestPSMQValue(double bestPSMQValue) {
		this.bestPSMQValue = bestPSMQValue;
	}
	public Double getBestPeptideQValue() {
		return bestPeptideQValue;
	}
	public void setBestPeptideQValue(Double bestPeptideQValue) {
		this.bestPeptideQValue = bestPeptideQValue;
	}


	private List<SearchDTO> searches;
	

	private Map<SearchDTO, SearchProteinLooplink> searchProteinLooplinks;


	private MergedSearchProtein protein;
	private int proteinPosition1;
	private int proteinPosition2;
	
	private Integer numPsms;
	private int numUniquePsms;

	private int numPeptides = -1;
	

		private int numUniquePeptides = -1;
	private double psmCutoff;
	private double peptideCutoff;
	private double bestPSMQValue;
	private Double bestPeptideQValue;
	
	private boolean anyLinksHaveBestPeptideQValue;
	private boolean anyLinksHaveBestPeptideQValueSet;
}
