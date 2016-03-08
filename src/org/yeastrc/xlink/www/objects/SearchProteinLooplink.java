package org.yeastrc.xlink.www.objects;





import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.number_peptides_psms.NumPeptidesPSMsForProteinCriteria;
import org.yeastrc.xlink.number_peptides_psms.NumPeptidesPSMsForProteinCriteriaResult;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;




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

				populateNumPsmNumPeptideNumUniquePeptide();
				
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

				populateNumPsmNumPeptideNumUniquePeptide();

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
				
				populateNumPsmNumPeptideNumUniquePeptide();

			}

			return this.numUniquePeptides;
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumUniquePeptides()";
			
			log.error( msg, e );
			
			throw e;
		}
	}

	

	private void populateNumPsmNumPeptideNumUniquePeptide() throws Exception {
		
		try {

			NumPeptidesPSMsForProteinCriteriaResult numPeptidesPSMsForProteinCriteriaResult =
					NumPeptidesPSMsForProteinCriteria.getInstance()
					.getNumPeptidesPSMsForLooplink(
							this.getSearch().getId(),
							this.getSearcherCutoffValuesSearchLevel(),
							this.getProtein().getNrProtein().getNrseqId(),
							this.getProteinPosition1(),
							this.getProteinPosition2(),
							YRC_NRSEQUtils.getDatabaseIdFromName( this.getSearch().getFastaFilename() ) );
			
			this.numPeptides = numPeptidesPSMsForProteinCriteriaResult.getNumPeptides();
			this.numUniquePeptides = numPeptidesPSMsForProteinCriteriaResult.getNumUniquePeptides();
			
			this.numPsms = numPeptidesPSMsForProteinCriteriaResult.getNumPSMs();

		} catch ( Exception e ) {

			String msg = "Exception in populateNumPsmNumPeptideNumUniquePeptide()";

			log.error( msg, e );

			throw e;
		}
		
		
	}

	
	//  WAS
	
	/**
	 * WAS
	 * Used by Javascript in Merged Image and Merged Structure pages
	 * 
	 * @return
	 * @throws Exception
	 */
//	public List<SearchPeptideLooplink> getPeptides() throws Exception {
//		
//		try {
//			if( this.peptides == null ) {
//
//				this.peptides = 
//				SearchPeptideLooplinkSearcher.getInstance()
//				.searchOnSearchProteinLooplink(
//						this.getSearch().getId(),
//						searcherCutoffValuesSearchLevel,
//						this.getProtein().getNrProtein().getNrseqId(),
//						this.getProteinPosition1(),
//						this.getProteinPosition2() );
//
//			}
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
