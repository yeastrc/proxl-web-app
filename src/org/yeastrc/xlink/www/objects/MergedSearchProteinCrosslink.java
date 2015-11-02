package org.yeastrc.xlink.www.objects;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.MergedSearchCrosslinkPeptideSearcher;
import org.yeastrc.xlink.www.searcher.MergedSearchPsmSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinCrosslinkSearcher;

public class MergedSearchProteinCrosslink implements IProteinCrosslink, IMergedSearchLink {

	private static final Logger log = Logger.getLogger(MergedSearchProteinCrosslink.class);
	
			
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

	

	public Map<SearchDTO, SearchProteinCrosslink> getSearchProteinCrosslinks() throws Exception {
		
		if ( searchProteinCrosslinks == null ) {
			
			searchProteinCrosslinks = new TreeMap<SearchDTO, SearchProteinCrosslink>();
			for( SearchDTO search : searches ) {
				SearchProteinCrosslink tlink = SearchProteinCrosslinkSearcher.getInstance().search(search, 
																									 psmCutoff, 
																									 peptideCutoff, 
																									 this.getProtein1().getNrProtein(),
																									 this.getProtein2().getNrProtein(),
																									 this.getProtein1Position(),
																									 this.getProtein2Position()
																									);

				if( tlink != null )
					searchProteinCrosslinks.put( search, tlink );
			}
			
		}
		
		return searchProteinCrosslinks;
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

		for ( Map.Entry<SearchDTO, SearchProteinCrosslink> searchProteinCrosslinksEntry : getSearchProteinCrosslinks().entrySet() ) {

			SearchProteinCrosslink searchProteinCrosslinkEntry = searchProteinCrosslinksEntry.getValue();

			if ( searchProteinCrosslinkEntry.getBestPeptideQValue() != null ) {

				anyLinksHaveBestPeptideQValue = true;
				break;
			}
		}
		
		anyLinksHaveBestPeptideQValueSet = true;
		
		return anyLinksHaveBestPeptideQValue;
	}

	
	
	
	public MergedSearchProtein getProtein1() {
		return protein1;
	}
	public void setProtein1(MergedSearchProtein protein1) {
		this.protein1 = protein1;
	}
	public MergedSearchProtein getProtein2() {
		return protein2;
	}
	public void setProtein2(MergedSearchProtein protein2) {
		this.protein2 = protein2;
	}
	public int getProtein1Position() {
		return protein1Position;
	}
	public void setProtein1Position(int protein1Position) {
		this.protein1Position = protein1Position;
	}
	public int getProtein2Position() {
		return protein2Position;
	}
	public void setProtein2Position(int protein2Position) {
		this.protein2Position = protein2Position;
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
	public void setPsmCutoff(double cutoff) {
		this.psmCutoff = cutoff;
	}
	public int getNumLinkedPeptides() throws Exception {
		if( this.numLinkedPeptides == -1 )
			this.numLinkedPeptides = MergedSearchCrosslinkPeptideSearcher.getInstance().getNumLinkedPeptides( this );
			
		return this.numLinkedPeptides;
	}
	public int getNumUniqueLinkedPeptides() throws Exception {
		
		try {
			if( this.numUniqueLinkedPeptides == -1 )
				this.numUniqueLinkedPeptides = MergedSearchCrosslinkPeptideSearcher.getInstance().getNumUniqueLinkedPeptides( this );

			return this.numUniqueLinkedPeptides;
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumUniqueLinkedPeptides( MergedSearchProteinCrosslink crosslink ): " 
					+ " this.getProtein1().getNrProtein().getNrseqId(): " + this.getProtein1().getNrProtein().getNrseqId()
					+ " this.getProtein2().getNrProtein().getNrseqId(): " + this.getProtein2().getNrProtein().getNrseqId();
			
			log.error( msg, e );
			
			throw e;
		}

	}
	

	public double getPeptideCutoff() {
		return peptideCutoff;
	}
	public void setPeptideCutoff(double peptideCutoff) {
		this.peptideCutoff = peptideCutoff;
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



	private MergedSearchProtein protein1;
	private MergedSearchProtein protein2;
	private int protein1Position;
	private int protein2Position;
	
	private Integer numPsms;
	private int numUniquePsms;

	private int numLinkedPeptides = -1;
	
	private List<SearchDTO> searches;
	
	private Map<SearchDTO, SearchProteinCrosslink> searchProteinCrosslinks;
	




	private int numUniqueLinkedPeptides = -1;
	private double psmCutoff;
	private double peptideCutoff;
	private double bestPSMQValue;
	private Double bestPeptideQValue;
	

	private boolean anyLinksHaveBestPeptideQValue;
	private boolean anyLinksHaveBestPeptideQValueSet;	
}
