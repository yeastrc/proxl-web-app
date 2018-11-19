package org.yeastrc.xlink.www.objects;



import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;




/**
 * 
 *
 */
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
	
	public int getNumLinkedPeptides() throws Exception {
		try {
			if( this.numLinkedPeptides == -1 ) {
				if ( this.associatedReportedPeptideIds != null ) {
					this.numLinkedPeptides = this.associatedReportedPeptideIds.size();
				} else {
					throw new ProxlWebappInternalErrorException( "numLinkedPeptides is not populated" );
				}
			}
			return this.numLinkedPeptides;
		} catch ( Exception e ) {
			String msg = "Exception in getNumLinkedPeptides()";
			log.error( msg, e );
			throw e;
		}
	}
	
	public int getNumUniqueLinkedPeptides() throws Exception {
		try {
			if( this.numUniqueLinkedPeptides == -1 ) {
				if ( this.associatedReportedPeptideIdsRelatedPeptidesUnique != null ) {
					this.numUniqueLinkedPeptides = this.associatedReportedPeptideIdsRelatedPeptidesUnique.size();
				} else {
					throw new ProxlWebappInternalErrorException( "numUniqueLinkedPeptides is not populated" );
				}
			}
			return this.numUniqueLinkedPeptides;
		} catch ( Exception e ) {
			String msg = "Exception in getNumUniqueLinkedPeptides()";
			log.error( msg, e );
			throw e;
		}
	}
	

	public boolean isRemoveNonUniquePSMsAppliedTo_numPsms() {
		return removeNonUniquePSMsAppliedTo_numPsms;
	}
	public void setRemoveNonUniquePSMsAppliedTo_numPsms(boolean removeNonUniquePSMsAppliedTo_numPsms) {
		this.removeNonUniquePSMsAppliedTo_numPsms = removeNonUniquePSMsAppliedTo_numPsms;
	}

	
	/**
	 * Only set if the PSM and Peptide Cutoffs match the defaults
	 * @param numPsms
	 */
	public void setNumPsms(Integer numPsms) {
	
		this.numPsms = numPsms;
	}
		
	/**
	 * Only set if the PSM and Peptide Cutoffs match the defaults
	 * @param numLinkedPeptides
	 */
	public void setNumLinkedPeptides(int numLinkedPeptides) {
		
		this.numLinkedPeptides = numLinkedPeptides;
	}
	
	/**
	 * Only set if the PSM and Peptide Cutoffs match the defaults
	 * @param numUniqueLinkedPeptides
	 */
	public void setNumUniqueLinkedPeptides(int numUniqueLinkedPeptides) {
		
		this.numUniqueLinkedPeptides = numUniqueLinkedPeptides;
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
		return search.getSearchId();
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

	/**
	 * @return null if not populated
	 */
	public List<ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink> getReportedPeptide_SearchReportedPeptidepeptideId_CrosslinkList() {
		return reportedPeptide_SearchReportedPeptidepeptideId_CrosslinkList;
	}
	public void setReportedPeptide_SearchReportedPeptidepeptideId_CrosslinkList(
			List<ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink> reportedPeptide_SearchReportedPeptidepeptideId_CrosslinkList) {
		this.reportedPeptide_SearchReportedPeptidepeptideId_CrosslinkList = reportedPeptide_SearchReportedPeptidepeptideId_CrosslinkList;
	}



	////////////////////////////////////////////////
	
	////   WAS
	
	
	/**
	 * Used by Javascript in Merged Image and Merged Structure pages
	 * 
	 * @return
	 * @throws Exception
	 */
//	public List<SearchPeptideCrosslink> getPeptides() throws Exception {
//
//		try {
//			if( this.peptides == null ) {
//
//				this.peptides = 
//					SearchPeptideCrosslinkSearcher.getInstance()
//					.searchOnSearchProteinCrosslink( 
//							this.getSearch().getId(),
//							this.searcherCutoffValuesSearchLevel,
//							this.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId(),
//							this.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId(),
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



	private SearchDTO search;

	private SearchProtein protein1;
	private SearchProtein protein2;

	private int protein1Position;
	private int protein2Position;
	
	private List<ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink> reportedPeptide_SearchReportedPeptidepeptideId_CrosslinkList = null;


//	private List<SearchPeptideCrosslink> peptides;
	



	private Integer numPsms;
	private boolean removeNonUniquePSMsAppliedTo_numPsms;

	private int numLinkedPeptides = -1;
	
	private int numUniqueLinkedPeptides = -1;
	
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
