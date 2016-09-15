package org.yeastrc.xlink.www.objects;





import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;




public class SearchProteinLooplink implements IProteinLooplink {

	
	private static final Logger log = Logger.getLogger(SearchProteinLooplink.class);
	
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
	
	public String getSearchName() {
		if ( search == null ) {
			
			return "";
		}
		return search.getName();
	}
	

	public int getSearchId() {
		if ( search == null ) {
			
			return -1;
		}
		return search.getId();
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
	
	

	/**
	 * Only set if the PSM and Peptide Cutoffs match the cutoffs for the source field
	 * @param numPsms
	 */
	public void setNumPsms(Integer numPsms) {
		this.numPsms = numPsms;
	}
	
	

	/**
	 * Returns the number of PSMs found for this looplink, given its cutoffs
	 * @return
	 * @throws Exception
	 */
	public int getNumPsms() throws Exception {
		
		try {
			if( this.numPsms == null ) {
				throw new ProxlWebappInternalErrorException( "numPsms is not populated" );
			}

			return this.numPsms;
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPsms()";
			
			log.error( msg, e );
			
			throw e;
		}
	}
	
	/**
	 * Only set if the PSM and Peptide Cutoffs match the cutoffs for the source field
	 * @param numPeptides
	 */
	public void setNumPeptides(int numPeptides) {
		this.numPeptides = numPeptides;
	}

	
	public int getNumPeptides() throws Exception {
		
		try {
			if( this.numPeptides == -1 ) {

				if ( this.associatedReportedPeptideIds != null ) {
					
					this.numPeptides = this.associatedReportedPeptideIds.size();

				} else {
					throw new ProxlWebappInternalErrorException( "numPeptides is not populated" );
				}

			}

			return this.numPeptides;
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPeptides()";
			
			log.error( msg, e );
			
			throw e;
		}
	}
	

	/**
	 * Only set if the PSM and Peptide Cutoffs match the cutoffs for the source field
	 * @param numUniquePeptides
	 */
	public void setNumUniquePeptides(int numUniquePeptides) {
		this.numUniquePeptides = numUniquePeptides;
	}
	
	
	public int getNumUniquePeptides() throws Exception {
		
		try {
			if( this.numUniquePeptides == -1 ) {

				if ( this.associatedReportedPeptideIdsRelatedPeptidesUnique != null ) {
					
					this.numUniquePeptides = this.associatedReportedPeptideIdsRelatedPeptidesUnique.size();
				
				} else {
					throw new ProxlWebappInternalErrorException( "numUniquePeptides is not populated" );
				}

			}

			return this.numUniquePeptides;
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumUniquePeptides()";
			
			log.error( msg, e );
			
			throw e;
		}
	}

	

	public List<String> getPsmAnnotationValueList() {
		return psmAnnotationValueList;
	}
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		this.psmAnnotationValueList = psmAnnotationValueList;
	}
	public List<String> getPeptideAnnotationValueList() {
		return peptideAnnotationValueList;
	}
	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		this.peptideAnnotationValueList = peptideAnnotationValueList;
	}


	public Set<Integer> getAssociatedReportedPeptideIds() {
		return associatedReportedPeptideIds;
	}
	public void setAssociatedReportedPeptideIds(
			Set<Integer> associatedReportedPeptideIds) {
		this.associatedReportedPeptideIds = associatedReportedPeptideIds;
	}
	public Set<Integer> getAssociatedReportedPeptideIdsRelatedPeptidesUnique() {
		return associatedReportedPeptideIdsRelatedPeptidesUnique;
	}
	public void setAssociatedReportedPeptideIdsRelatedPeptidesUnique(
			Set<Integer> associatedReportedPeptideIdsRelatedPeptidesUnique) {
		this.associatedReportedPeptideIdsRelatedPeptidesUnique = associatedReportedPeptideIdsRelatedPeptidesUnique;
	}

	public SearcherCutoffValuesSearchLevel getSearcherCutoffValuesSearchLevel() {
		return searcherCutoffValuesSearchLevel;
	}
	public void setSearcherCutoffValuesSearchLevel(
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel) {
		this.searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel;
	}



	private SearchProtein protein;
	private SearchDTO search;
	private int proteinPosition1;
	private int proteinPosition2;
	

//	private List<SearchPeptideLooplink> peptides;
	
	
	private Integer numPsms;
//	private int numUniquePsms = -999;



	private int numPeptides = -1;
	
	private int numUniquePeptides = -1;

	private Set<Integer> associatedReportedPeptideIds;
	private Set<Integer> associatedReportedPeptideIdsRelatedPeptidesUnique;


	private SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel;



	/**
	 * Used for display on web page
	 */
	private List<String> psmAnnotationValueList;
	

	/**
	 * Used for display on web page
	 */
	private List<String> peptideAnnotationValueList;

}
