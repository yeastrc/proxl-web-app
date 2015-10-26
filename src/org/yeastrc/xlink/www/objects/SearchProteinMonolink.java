package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.SearchMonolinkPeptideSearcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideMonolinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;


public class SearchProteinMonolink {

	private static final Logger log = Logger.getLogger(SearchProteinMonolink.class);
	
	public SearchProtein getProtein() {
		return protein;
	}
	public void setProtein(SearchProtein protein) {
		this.protein = protein;
	}
	public SearchDTO getSearch() {
		return search;
	}
	public void setSearch(SearchDTO search) {
		this.search = search;
	}
	public int getProteinPosition() {
		return proteinPosition;
	}
	public void setProteinPosition(int proteinPosition) {
		this.proteinPosition = proteinPosition;
	}

//	public int getNumPsms() {
//		return numPsms;
//	}
	

	/**
	 * Returns the number of PSMs found for this crosslink, given its cutoffs
	 * @return
	 * @throws Exception
	 */
	public int getNumPsms() throws Exception {
		
		try {
			if( this.numPsms == null )
				this.numPsms = SearchPsmSearcher.getInstance().getNumPsms( this );

			return this.numPsms;
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPsms()";
			
			log.error( msg, e );
			
			throw e;
		}
	}
	
	
	public void setNumPsms(int numPsms) {
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
		
		try {
			if( this.numPeptides == -1 )
				this.numPeptides = SearchMonolinkPeptideSearcher.getInstance().getNumPeptides( this );

			return this.numPeptides;
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPeptides()";
			
			log.error( msg, e );
			
			throw e;
		}
	}
	public int getNumUniquePeptides() throws Exception {
		
		try {
			if( this.numUniquePeptides == -1 )
				this.numUniquePeptides = SearchMonolinkPeptideSearcher.getInstance().getNumUniquePeptides( this );

			return this.numUniquePeptides;

		} catch ( Exception e ) {

			String msg = "Exception in getNumUniquePeptides()";

			log.error( msg, e );

			throw e;
		}
	}

	public List<SearchPeptideMonolink> getPeptides() throws Exception {

		try {
			if( this.peptides == null )
				this.peptides = SearchPeptideMonolinkSearcher.getInstance().search( this );

			return this.peptides;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptides()";

			log.error( msg, e );

			throw e;
		}
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




	private SearchProtein protein;
	private SearchDTO search;
	private int proteinPosition;
	
	private Integer numPsms;
	private int numUniquePsms;

	private int numPeptides = -1;
	List<SearchPeptideMonolink> peptides;
	
	private int numUniquePeptides = -1;
	private double psmCutoff;
	private double peptideCutoff;
	private double bestPSMQValue;
	private Double bestPeptideQValue;
}
