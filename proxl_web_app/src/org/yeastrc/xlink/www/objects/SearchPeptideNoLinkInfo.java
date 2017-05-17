package org.yeastrc.xlink.www.objects;

import java.security.InvalidParameterException;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base_searcher.PsmCountForSearchIdReportedPeptideIdSearcher;
import org.yeastrc.xlink.base_searcher.PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ReportedPeptideDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * No Link info (crosslink, etc) so display Reported Peptide for Protein All Page
 * 
 */
public class SearchPeptideNoLinkInfo {

	private static final Logger log = Logger.getLogger(SearchPeptideNoLinkInfo.class);
	
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
	 * @return the psmId for a random psm record associated with this Peptide, null if none found
	 * @throws Exception
	 */
//	public Integer getSinglePsmId() throws Exception {
//		try {
//			Integer psmId = SearchPsmSearcher.getInstance().getSinglePsmId( this.getSearch().getSearchId(), this.getReportedPeptideId() );
//			return psmId;
//		} catch ( Exception e ) {
//			String msg = "Exception in getSinglePsmId()";
//			log.error( msg, e );
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

	public String getLinkType() {
		return linkType;
	}
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
	
	private String linkType;
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
