package org.yeastrc.xlink.www.objects;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.MergedSearchMonolinkPeptideSearcher;
import org.yeastrc.xlink.www.searcher.MergedSearchPsmSearcher;

public class MergedSearchProteinMonolink {
	
	private static final Logger log = Logger.getLogger(MergedSearchProteinMonolink.class);
			
	public MergedSearchProtein getProtein() {
		return protein;
	}
	public void setProtein(MergedSearchProtein protein) {
		this.protein = protein;
	}
	public int getProteinPosition() {
		return proteinPosition;
	}
	public void setProteinPosition(int proteinPosition) {
		this.proteinPosition = proteinPosition;
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
			this.numPeptides = MergedSearchMonolinkPeptideSearcher.getInstance().getNumPeptides( this );
			
		return this.numPeptides;
	}
	public int getNumUniquePeptides() throws Exception {
		if( this.numUniquePeptides == -1 )
			this.numUniquePeptides = MergedSearchMonolinkPeptideSearcher.getInstance().getNumUniquePeptides( this );
			
		return this.numUniquePeptides;
	}
	
	
	public int getNumPsms() throws Exception {
		if( this.numPsms == null )
			this.numPsms = MergedSearchPsmSearcher.getInstance().getNumPsms( this );
		
		return this.numPsms;
	}
	
	public Map<SearchDTO, SearchProteinMonolink> getSearchProteinMonolinks() {
		return searchProteinMonolinks;
	}
	public void setSearchProteinMonolinks(
			Map<SearchDTO, SearchProteinMonolink> searchProteinMonolinks) {
		this.searchProteinMonolinks = searchProteinMonolinks;
	}

	public int getNumSearches() {
		if( this.searchProteinMonolinks == null ) return 0;
		return this.searchProteinMonolinks.keySet().size();
	}

	public Collection<SearchDTO> getSearches() {
		return this.searchProteinMonolinks.keySet();
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
	private int proteinPosition;
	
	private Integer numPsms;
	private int numUniquePsms;

	private int numPeptides = -1;
	private Map<SearchDTO, SearchProteinMonolink> searchProteinMonolinks;
	
	private int numUniquePeptides = -1;
	private double psmCutoff;
	private double peptideCutoff;
	private double bestPSMQValue;
	private Double bestPeptideQValue;
}
