package org.yeastrc.xlink.www.objects;


import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.SearchCrosslinkPeptideSearcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideCrosslinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;

public class SearchProteinCrosslink implements IProteinCrosslink {	
	
	private static final Logger log = Logger.getLogger(SearchProteinCrosslink.class);
	
	public SearchProtein getProtein1() {
		return protein1;
	}
	public void setProtein1(SearchProtein protein1) {
		this.protein1 = protein1;
	}
	public SearchProtein getProtein2() {
		return protein2;
	}
	public void setProtein2(SearchProtein protein2) {
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

		try {
			if( this.numLinkedPeptides == -1 )
				this.numLinkedPeptides = SearchCrosslinkPeptideSearcher.getInstance().getNumLinkedPeptides( this );

			return this.numLinkedPeptides;

		} catch ( Exception e ) {

			String msg = "Exception in getNumLinkedPeptides()";

			log.error( msg, e );

			throw e;
		}
	}
	public int getNumUniqueLinkedPeptides() throws Exception {

		try {
			if( this.numUniqueLinkedPeptides == -1 )
				this.numUniqueLinkedPeptides = SearchCrosslinkPeptideSearcher.getInstance().getNumUniqueLinkedPeptides( this );

			return this.numUniqueLinkedPeptides;

		} catch ( Exception e ) {

			String msg = "Exception in getNumUniqueLinkedPeptides()";

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
	public SearchDTO getSearch() {
		return search;
	}
	public void setSearch(SearchDTO search) {
		this.search = search;
	}

	public List<SearchPeptideCrosslink> getPeptides() throws Exception {

		try {
			if( this.peptides == null )
				this.peptides = SearchPeptideCrosslinkSearcher.getInstance().searchOnSearchProteinCrosslink( this );

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



	private SearchProtein protein1;
	private SearchProtein protein2;
	private SearchDTO search;
	private int protein1Position;
	private int protein2Position;
	
	private Integer numPsms;
	private int numUniquePsms;

	private int numLinkedPeptides = -1;
	List<SearchPeptideCrosslink> peptides;
	
	private int numUniqueLinkedPeptides = -1;
	private double psmCutoff;
	private double peptideCutoff;
	private double bestPSMQValue;
	private Double bestPeptideQValue;
	
}
