package org.yeastrc.xlink.www.objects;

import java.security.InvalidParameterException;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base_searcher.PsmCountForSearchIdReportedPeptideIdSearcher;
import org.yeastrc.xlink.base_searcher.PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ReportedPeptideDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Reported Peptides retrieved when expanding a Protein entry
 * 
 * Common Base Class for SearchPeptideCrosslink, SearchPeptideLooplink, SearchPeptideUnlink, SearchPeptideDimer
 *
 */
public class SearchPeptide_BaseCommon {

	private static final Logger log = Logger.getLogger( SearchPeptide_BaseCommon.class );

	/**
	 * Called to update numPsms to remove the number of non-unique PSMs.
	 * @throws Exception 
	 */
	public void updateNumPsmsToNotInclude_NonUniquePSMs() throws Exception {
		if ( updateNumPsmsToNotInclude_NonUniquePSMs_Called ) {
			//  already called
			return;  //  EARLY EXIT
//			throw new IllegalStateException( "updateNumPsmsToNotInclude_NonUniquePSMs(...) cannot be called more than once" );
		}
		updateNumPsmsToNotInclude_NonUniquePSMs_Called = true;
		SearchDTO search = getSearch();
		if ( search.isHasScanData() ) {
			//  Only update if search has scan data
			int numPsms = getNumPsms();
			int numNonUniquePsms= getNumNonUniquePsms();
			this.numPsms = numPsms - numNonUniquePsms;
		}
	}

	/**
	 * Do not allow calling updateNumPsmsToNotInclude_NonUniquePSMs() more than once
	 */
	private boolean updateNumPsmsToNotInclude_NonUniquePSMs_Called;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public int getNumPsms() throws Exception {
		if ( numPsmsSet ) {
			return numPsms;
		}
		//		num psms is always based on searching psm table for: search id, reported peptide id, and psm cutoff values.
		numPsms = 
				PsmCountForSearchIdReportedPeptideIdSearcher.getInstance()
				.getPsmCountForSearchIdReportedPeptideId( reportedPeptideId, search.getSearchId(), searcherCutoffValuesSearchLevel );
		numPsmsSet = true;
		return numPsms;
	}
	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
		numPsmsSet = true;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public int getNumNonUniquePsms() throws Exception {
		try {
			int numPsms = this.getNumPsms();
			int numUniquePsms = this.getNumUniquePsms();
			int nonUniquePSMs = numPsms - numUniquePsms;
			return nonUniquePSMs;
			
		} catch ( Exception e ) {
			log.error( "getNumNonUniquePsms() Exception: " + e.toString(), e );
			throw e;
		}
	}
	
	/**
	 * @return null when no scan data for search
	 * @throws Exception
	 */
	public Integer getNumUniquePsms() throws Exception {
		try {
			if ( numUniquePsmsSet ) {
				return numUniquePsms;
			}
			if ( ! this.search.isHasScanData() ) {
				numUniquePsms = null;
				numUniquePsmsSet = true;
				return numUniquePsms;
			}
			numUniquePsms = 
					PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher.getInstance()
					.getPsmCountForUniquePSM_SearchIdReportedPeptideId( this.getReportedPeptideId(), this.search.getSearchId(), searcherCutoffValuesSearchLevel );
			numUniquePsmsSet = true;
			return numUniquePsms;
		} catch ( Exception e ) {
			log.error( "getNumUniquePsms() Exception: " + e.toString(), e );
			throw e;
		}
	}

	
	
	
	@JsonIgnore // Don't serialize to JSON
	public SearchDTO getSearch() {
		return search;
	}
	public void setSearch(SearchDTO search) {
		if ( search == null ) {
			throw new InvalidParameterException( "search cannot be assigned to null");
		}
		this.search = search;
	}
	public int getSearchId() {
		return search.getSearchId();
	}

	public int getReportedPeptideId() {
		return reportedPeptideId;
	}
	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}
	public ReportedPeptideDTO getReportedPeptide() throws Exception {
		try {
			if ( reportedPeptide == null ) {
				reportedPeptide = 
						Cached_ReportedPeptideDTO.getInstance().getReportedPeptideDTO( reportedPeptideId );
			}
			return reportedPeptide;
		} catch ( Exception e ) {
			log.error( "getReportedPeptide() Exception: " + e.toString(), e );
			throw e;
		}
	}
	public void setReportedPeptide(ReportedPeptideDTO reportedPeptide) {
		this.reportedPeptide = reportedPeptide;
		if ( reportedPeptide != null ) {
			this.reportedPeptideId = reportedPeptide.getId();
		}
	}
	
	public SearcherCutoffValuesSearchLevel getSearcherCutoffValuesSearchLevel() {
		return searcherCutoffValuesSearchLevel;
	}
	public void setSearcherCutoffValuesSearchLevel(
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel) {
		this.searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel;
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
	

	private int reportedPeptideId = -999;
	private ReportedPeptideDTO reportedPeptide;
	
	private SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel;
	private SearchDTO search;
	private int numPsms;
	/**
	 * true when SetNumPsms has been called
	 */
	private boolean numPsmsSet;
	private Integer numUniquePsms;
	private boolean numUniquePsmsSet;
	/**
	 * Used for display on web page
	 */
	private List<String> psmAnnotationValueList;
	/**
	 * Used for display on web page
	 */
	private List<String> peptideAnnotationValueList;
}
