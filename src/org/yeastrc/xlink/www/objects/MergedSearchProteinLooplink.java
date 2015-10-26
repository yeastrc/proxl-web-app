package org.yeastrc.xlink.www.objects;

import java.util.Collection;
import java.util.Map;

import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.MergedSearchLooplinkPeptideSearcher;
import org.yeastrc.xlink.www.searcher.MergedSearchPsmSearcher;

public class MergedSearchProteinLooplink implements IProteinLooplink, IMergedSearchLink {
	

	@Override
	public Collection<SearchDTO> getSearches() {
		
		if ( searchProteinLooplinks == null ) {
			
			return null;
		}
		return searchProteinLooplinks.keySet();
	}
	

	/**
	 * @return true if any child level link "Best Peptide Q-Value" is not null
	 */
	public boolean isAnyLinksHaveBestPeptideQValue() {

		if ( anyLinksHaveBestPeptideQValueSet ) {
			
			return anyLinksHaveBestPeptideQValue;
		}
	
		/// Check if any child level link "Best Peptide Q-Value" is not null

		for ( Map.Entry<SearchDTO, SearchProteinLooplink> searchProteinLooplinksEntry : searchProteinLooplinks.entrySet() ) {

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
		if( this.numPsms == null )
			this.numPsms = MergedSearchPsmSearcher.getInstance().getNumPsms( this );
		
		return this.numPsms;
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
	
	
	
	
	public Map<SearchDTO, SearchProteinLooplink> getSearchProteinLooplinks() {
		return searchProteinLooplinks;
	}
	public void setSearchProteinLooplinks(
			Map<SearchDTO, SearchProteinLooplink> searchProteinLooplinks) {
		this.searchProteinLooplinks = searchProteinLooplinks;
	}

	public int getNumSearches() {
		if( this.searchProteinLooplinks == null ) return 0;
		return this.searchProteinLooplinks.keySet().size();
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



	private MergedSearchProtein protein;
	private int proteinPosition1;
	private int proteinPosition2;
	
	private Integer numPsms;
	private int numUniquePsms;

	private int numPeptides = -1;
	private Map<SearchDTO, SearchProteinLooplink> searchProteinLooplinks;
	
	private int numUniquePeptides = -1;
	private double psmCutoff;
	private double peptideCutoff;
	private double bestPSMQValue;
	private Double bestPeptideQValue;
	
	private boolean anyLinksHaveBestPeptideQValue;
	private boolean anyLinksHaveBestPeptideQValueSet;
}
