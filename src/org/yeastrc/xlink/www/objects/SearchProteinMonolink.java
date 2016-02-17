package org.yeastrc.xlink.www.objects;


import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searchers.NumPeptidesForProteinCriteriaSearcher;
import org.yeastrc.xlink.searchers.NumPsmsForProteinCriteriaSearcher;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;


/**
 * 
 *
 */
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

	public String getSearchName() {
		if ( search == null ) {
			
			return "";
		}
		return search.getName();
	}

	public SearcherCutoffValuesSearchLevel getSearcherCutoffValuesSearchLevel() {
		return searcherCutoffValuesSearchLevel;
	}
	public void setSearcherCutoffValuesSearchLevel(
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel) {
		this.searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel;
	}
	
	
	public int getProteinPosition() {
		return proteinPosition;
	}
	public void setProteinPosition(int proteinPosition) {
		this.proteinPosition = proteinPosition;
	}


	/**
	 * Returns the number of PSMs found for this crosslink, given its cutoffs
	 * @return
	 * @throws Exception
	 */
	public int getNumPsms() throws Exception {
		
		try {
			if( this.numPsms == null ) {
				
				this.numPsms =
						NumPsmsForProteinCriteriaSearcher.getInstance().getNumPsmsForMonolink(
								this.getSearch().getId(),
								this.getSearcherCutoffValuesSearchLevel(),
								this.getProtein().getNrProtein().getNrseqId(),
								this.getProteinPosition() );

			}

			return this.numPsms;
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPsms()";
			
			log.error( msg, e );
			
			throw e;
		}
	}

	
	public void setNumPsms(int numPsms) {
		
//		if ( true ) {
//			
//			throw new RuntimeException( "TEMP not supported" );
//		}
//		
//
//		//  TODO TEMP COMMENT OUT
		
		this.numPsms = numPsms;
	}
	
	
	
//	public int getNumUniquePsms() {
//
//		if ( true ) {
//			
//			throw new RuntimeException( "TEMP not supported" );
//		}
//
//
//		//  TODO TEMP COMMENT OUT
//		
//		return numUniquePsms;
//	}
//	
//	
//	
//	public void setNumUniquePsms(int numUniquePsms) {
//		
//		if ( true ) {
//			
//			throw new RuntimeException( "TEMP not supported" );
//		}
//		
//		
//		//  TODO TEMP COMMENT OUT
//		
////		this.numUniquePsms = numUniquePsms;
//	}
	
	
	

	public void setNumPeptides(int numPeptides) {
		
//		if ( true ) {
//			
//			throw new RuntimeException( "TEMP not supported" );
//		}
//		

		//  TODO TEMP COMMENT OUT
		
		this.numPeptides = numPeptides;
	}
	
	
	public int getNumPeptides() throws Exception {
		
		try {
			if( this.numPeptides == -1 ) {
				
				this.numPeptides = 
						NumPeptidesForProteinCriteriaSearcher.getInstance()
						.getNumPeptidesForMonolink(
								this.getSearch().getId(),
								this.getSearcherCutoffValuesSearchLevel(),
								this.getProtein().getNrProtein().getNrseqId(),
								this.getProteinPosition() );
			}

			return this.numPeptides;
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPeptides()";
			
			log.error( msg, e );
			
			throw e;
		}
	}

	public void setNumUniquePeptides(int numUniquePeptides) {
		
		
//		if ( true ) {
//			
//			throw new RuntimeException( "TEMP not supported" );
//		}
		

		//  TODO TEMP COMMENT OUT
		
		this.numUniquePeptides = numUniquePeptides;
	}
	
	public int getNumUniquePeptides() throws Exception {
		
		try {
			if( this.numUniquePeptides == -1 ) {
				
				

				this.numUniquePeptides = 
						NumPeptidesForProteinCriteriaSearcher.getInstance()
						.getNumUniquePeptidesForMonolink(
								this.getSearch().getId(),
								this.getSearcherCutoffValuesSearchLevel(),
								this.getProtein().getNrProtein().getNrseqId(),
								this.getProteinPosition(),
								YRC_NRSEQUtils.getDatabaseIdFromName( this.getSearch().getFastaFilename() ) );
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

	////////////////////////////////////////////////
	
	////   WAS
	
	
//	/**
//	 * Used by Javascript in Merged Image and Merged Structure pages
//	 * 
//	 * @return
//	 * @throws Exception
//	 */
//	public List<SearchPeptideMonolink> getPeptides() throws Exception {
//
//		try {
//			if( this.peptides == null )
//				this.peptides = SearchPeptideMonolinkSearcher.getInstance().search( this );
//
//			return this.peptides;
//
//		} catch ( Exception e ) {
//
//			String msg = "Exception in getPeptides()";
//
//			log.error( msg, e );
//
//			throw e;
//		}
//	}


	




	private SearchProtein protein;
	private SearchDTO search;
	private int proteinPosition;
	
	private Integer numPsms;
//	private int numUniquePsms;

	private int numPeptides = -1;
	private int numUniquePeptides = -1;

	
//	List<SearchPeptideMonolink> peptides;
	

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
