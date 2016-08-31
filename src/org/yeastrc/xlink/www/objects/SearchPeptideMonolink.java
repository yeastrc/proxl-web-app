package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.searcher.PsmCountForSearchIdReportedPeptideIdSearcher;
import org.yeastrc.xlink.www.searcher.PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;

import com.fasterxml.jackson.annotation.JsonIgnore;



public class SearchPeptideMonolink {
	

	private static final Logger log = Logger.getLogger(SearchPeptideMonolink.class);


	/**
	 * Constructor
	 */
	public SearchPeptideMonolink() {}

//	private void populatePeptides() throws Exception {
//
//		if ( populatePeptidesCalled ) {
//			
//			return;
//		}
//		
//		populatePeptidesCalled = true;
//		
//		
//		try {
//			List<SrchRepPeptPeptideDTO> results = 
//					SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher.getInstance()
//					.getForSearchIdReportedPeptideId( this.getSearch().getId(), this.getReportedPeptideId() );
//
//			if ( results.size() != 1 ) {
//
//
//				String msg = "List<SrchRepPeptPeptideDTO> results.size() != 1. SearchId: " +this.getSearch().getId()
//						+ ", ReportedPeptideId: " + this.getReportedPeptideId() ;
//
//				log.error( msg );
//
//				throw new ProxlWebappDataException( msg );
//			}
//			
//			SrchRepPeptPeptideDTO result = results.get( 0 );
//
//			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( result.getPeptideId() );
//
//			this.setPeptide( peptideDTO );
//
////			this.setPeptidePosition(  );  //  Peptide Position already set from the srch_rep_pept__prot_seq_id_pos_monolink record
//
//		} catch ( Exception e ) {
//
//			String msg = "Exception in populatePeptides()";
//
//			log.error( msg, e );
//
//			throw e;
//		}
//	}
	
	@JsonIgnore // Don't serialize to JSON
	public SearchDTO getSearch() {
		return search;
	}
	public void setSearch(SearchDTO search) {
		this.search = search;
	}
	

	public ReportedPeptideDTO getReportedPeptide() throws Exception {
		
		try {
			if ( reportedPeptide == null ) {

				reportedPeptide = 
						ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( reportedPeptideId );
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
	
	
	public PeptideDTO getPeptide() throws Exception {
		
		try {

			if( this.peptide == null ) {
				
				PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( this.getPeptideId() );

				this.setPeptide( peptideDTO );
			}
			
			return peptide;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide(PeptideDTO peptide) {
		this.peptide = peptide;
	}
	public int getPeptidePosition() throws Exception {
		
		try {

			if ( peptidePosition == -1 ) {
				
				String msg = "Peptide Position Not Set.  Search Id: " + search.getId()
						+ ", reported peptide id: " + reportedPeptideId;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}

			return peptidePosition;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptidePosition()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptidePosition(int peptidePosition) {
		this.peptidePosition = peptidePosition;
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
					.getPsmCountForUniquePSM_SearchIdReportedPeptideId( this.getReportedPeptide().getId(), this.search.getId(), searcherCutoffValuesSearchLevel );

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
				.getPsmCountForSearchIdReportedPeptideId( reportedPeptideId, search.getId(), searcherCutoffValuesSearchLevel );

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
	public Integer getSinglePsmId() throws Exception {

		try {
			
			Integer psmId = SearchPsmSearcher.getInstance().getSinglePsmId( this.getSearch().getId(), this.getReportedPeptide().getId() );

			return psmId;

		} catch ( Exception e ) {

			String msg = "Exception in getSinglePsmId()";

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


	public int getReportedPeptideId() {
		return reportedPeptideId;
	}

	public void setReportedPeptideId(int reportedPeptideId) {
		this.reportedPeptideId = reportedPeptideId;
	}

	public int getPeptideId() {
		return peptideId;
	}

	public void setPeptideId(int peptideId) {
		this.peptideId = peptideId;
	}


//	private boolean populatePeptidesCalled;
	

	private int reportedPeptideId = -999;
	private int peptideId = -999;
	
	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide;
	private int peptidePosition = -1;

	
	

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
