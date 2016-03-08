package org.yeastrc.xlink.www.objects;



import java.util.List;


//import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
//import org.yeastrc.xlink.searchers.NumPeptidesForProteinCriteriaSearcher;
//import org.yeastrc.xlink.searchers.NumPsmsForProteinCriteriaSearcher;
//import org.yeastrc.xlink.utils.YRC_NRSEQUtils;
//import org.yeastrc.xlink.searcher_result_objects.NumPeptidesPSMsForProteinCriteriaResult;
//import org.yeastrc.xlink.searchers.NumPeptidesPSMsForProteinCriteriaSearcher;




/**
 * 
 *
 */
public class SearchProteinDimer  {	
	
//	private static final Logger log = Logger.getLogger(SearchProteinDimer.class);
	
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

	//  Not used.  The fields are not set for default annotation cutoffs
//	
//	/**
//	 * Returns the number of PSMs found for this dimer, given its cutoffs
//	 * @return
//	 * @throws Exception
//	 */
//	public int getNumPsms() throws Exception {
//
//		try {
//			if( this.numPsms == null ) {
//								
//	populateNumPsmNumPeptideNumUniquePeptide();
//	
////				this.numPsms =
////						NumPsmsForProteinCriteriaSearcher.getInstance().getNumPsmsForDimer(
////								this.getSearch().getId(),
////								this.getSearcherCutoffValuesSearchLevel(),
////								this.getProtein1().getNrProtein().getNrseqId(),
////								this.getProtein2().getNrProtein().getNrseqId() );
//
//			}
//
//			return this.numPsms;
//
//		} catch ( Exception e ) {
//
//			String msg = "Exception in getNumPsms()";
//
//			log.error( msg, e );
//
//			throw e;
//		}
//	}
//	
//	public int getNumLinkedPeptides() throws Exception {
//
//		try {
//			if( this.numLinkedPeptides == -1 ) {
//				
//	populateNumPsmNumPeptideNumUniquePeptide();
//	
////				this.numLinkedPeptides = 
////						NumPeptidesForProteinCriteriaSearcher.getInstance()
////						.getNumLinkedPeptidesForDimer( 
////								this.getSearch().getId(),
////								this.getSearcherCutoffValuesSearchLevel(),
////								this.getProtein1().getNrProtein().getNrseqId(),
////								this.getProtein2().getNrProtein().getNrseqId() );
//			}
//
//			return this.numLinkedPeptides;
//
//		} catch ( Exception e ) {
//
//			String msg = "Exception in getNumLinkedPeptides()";
//
//			log.error( msg, e );
//
//			throw e;
//		}
//	}
//	
//	
//	public int getNumUniqueLinkedPeptides() throws Exception {
//
//		try {
//			if( this.numUniqueLinkedPeptides == -1 ) {
//				
//				populateNumPsmNumPeptideNumUniquePeptide();
//	
////				this.numUniqueLinkedPeptides = 
////						NumPeptidesForProteinCriteriaSearcher.getInstance()
////						.getNumUniqueLinkedPeptidesForDimer(
////								this.getSearch().getId(),
////								this.getSearcherCutoffValuesSearchLevel(),
////								this.getProtein1().getNrProtein().getNrseqId(),
////								this.getProtein2().getNrProtein().getNrseqId(),
////								YRC_NRSEQUtils.getDatabaseIdFromName( this.getSearch().getFastaFilename() ) );
//			}
//
//			return this.numUniqueLinkedPeptides;
//
//		} catch ( Exception e ) {
//
//			String msg = "Exception in getNumUniqueLinkedPeptides()";
//
//			log.error( msg, e );
//
//			throw e;
//		}
//	}
//
//	
//	
//	/**
//	 * Only set if the PSM and Peptide Cutoffs match
//	 * @param numPsms
//	 */
//	public void setNumPsms(Integer numPsms) {
//		this.numPsms = numPsms;
//	}
//	
//	/**
//	 * Only set if the PSM and Peptide Cutoffs match
//	 * @param numLinkedPeptides
//	 */
//	public void setNumLinkedPeptides(int numLinkedPeptides) {
//		this.numLinkedPeptides = numLinkedPeptides;
//	}
//	
//	/**
//	 * Only set if the PSM and Peptide Cutoffs match
//	 * @param numUniqueLinkedPeptides
//	 */
//	public void setNumUniqueLinkedPeptides(int numUniqueLinkedPeptides) {
//		this.numUniqueLinkedPeptides = numUniqueLinkedPeptides;
//	}
//	
//
//
//	private void populateNumPsmNumPeptideNumUniquePeptide() throws Exception {
//		
//		try {
//
//			NumPeptidesPSMsForProteinCriteriaResult numPeptidesPSMsForProteinCriteriaResult =
//					NumPeptidesPSMsForProteinCriteriaSearcher.getInstance()
//					.getNumPeptidesPSMsForLooplink(
//							this.getSearch().getId(),
//							this.getSearcherCutoffValuesSearchLevel(),
//							this.getProtein().getNrProtein().getNrseqId(),
//							this.getProteinPosition1(),
//							this.getProteinPosition2(),
//							YRC_NRSEQUtils.getDatabaseIdFromName( this.getSearch().getFastaFilename() ) );
//			
//			this.numPeptides = numPeptidesPSMsForProteinCriteriaResult.getNumPeptides();
//			this.numUniquePeptides = numPeptidesPSMsForProteinCriteriaResult.getNumUniquePeptides();
//			
//			this.numPsms = numPeptidesPSMsForProteinCriteriaResult.getNumPSMs();
//
//		} catch ( Exception e ) {
//
//			String msg = "Exception in populateNumPsmNumPeptideNumUniquePeptide()";
//
//			log.error( msg, e );
//
//			throw e;
//		}
//		
//		
//	}
	


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


	////////////////////////////////////////////////
	
	////   WAS
	
	
	/**
	 * Used by Javascript in Merged Image and Merged Structure pages
	 * 
	 * @return
	 * @throws Exception
	 */
//	public List<SearchPeptideDimer> getPeptides() throws Exception {
//
//		try {
//			if( this.peptides == null ) {
//
//				this.peptides = 
//					SearchPeptideDimerSearcher.getInstance()
//					.searchOnSearchProteinDimer( 
//							this.getSearch().getId(),
//							this.searcherCutoffValuesSearchLevel,
//							this.getProtein1().getNrProtein().getNrseqId(),
//							this.getProtein2().getNrProtein().getNrseqId(),
//							this.getProtein1Position(),
//							this.getProtein2Position() );
//			}
//
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




	private SearchProtein protein1;
	private SearchProtein protein2;
	private SearchDTO search;
	

//	private List<SearchPeptideDimer> peptides;
	
//	private Integer numPsms;
//
//	private int numLinkedPeptides = -1;
//	
//	private int numUniqueLinkedPeptides = -1;

	
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
