package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.LooplinkDAO;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dto.LooplinkDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searchers.PsmCountForSearchIdReportedPeptideIdSearcher;
import org.yeastrc.xlink.searchers.PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;

import com.fasterxml.jackson.annotation.JsonIgnore;




public class SearchPeptideLooplink {
	
	private static final Logger log = Logger.getLogger(SearchPeptideLooplink.class);

	private void populatePeptides() throws Exception {
		
		Integer psmId = getSinglePsmId();
		
		
		if ( psmId == null ) {
			
			log.warn( "No PSMs for search.id : " + search.getId() 
					+ ", this.getReportedPeptideId(): " + this.getReportedPeptideId() );
			
			return;
		}
		
		try {

			//  Get looplink table entry for first psm.  assume the peptide position is the same for all.
			
			LooplinkDTO looplinkDTO = LooplinkDAO.getInstance().getARandomLooplinkDTOForPsmId( psmId );

			if ( looplinkDTO == null ) {
				

				String msg = "looplinkDTO == null for psmId: " + psmId;

				log.error( msg );

				throw new Exception( msg );
				
			}

			int position1 = looplinkDTO.getPeptidePosition1();
			int position2 = looplinkDTO.getPeptidePosition2();

			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( looplinkDTO.getPeptideId() );

			this.setPeptide( peptideDTO );

			if( position1 <= position2 ) {
				this.setPeptidePosition1( position1 );
				this.setPeptidePosition2( position2 );
			} else {
				this.setPeptidePosition1( position2 );
				this.setPeptidePosition2( position1 );
			}

		} catch ( Exception e ) {

			String msg = "Exception in populatePeptides()";

			log.error( msg, e );

			throw e;
		}
	}
	
	@JsonIgnore // Don't serialize to JSON
	public SearchDTO getSearch() {
		return search;
	}
	public void setSearch(SearchDTO search) {
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

			if( this.peptide == null )
				populatePeptides();

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
	public int getPeptidePosition1() throws Exception {
		
		try {

			if( this.peptide == null )
				populatePeptides();

			return peptidePosition1;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptidePosition1()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptidePosition1(int peptidePosition1) {
		this.peptidePosition1 = peptidePosition1;
	}
	public int getPeptidePosition2() throws Exception {
		
		try {

			if( this.peptide == null )
				populatePeptides();

			return peptidePosition2;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptidePosition2()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptidePosition2(int peptidePosition2) {
		this.peptidePosition2 = peptidePosition2;
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
			
			
			if ( this.search.isNoScanData() ) {
				
				numUniquePsms = null;
				
				numUniquePsmsSet = true;
				
				return numUniquePsms;
			}




			numUniquePsms = 
					PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher.getInstance()
					.getPsmCountForUniquePSM_SearchIdReportedPeptideId( this.getReportedPeptideId(), this.search.getId(), searcherCutoffValuesSearchLevel );

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
			
			Integer psmId = SearchPsmSearcher.getInstance().getSinglePsmId( this.getSearch().getId(), this.getReportedPeptideId() );

			return psmId;

		} catch ( Exception e ) {

			String msg = "Exception in getSinglePsmId()";

			log.error( msg, e );

			throw e;
		}
	}
	
	
	public List<SearchProteinDoublePosition> getPeptideProteinPositions() throws Exception {
		
		try {

			if( this.peptideProteinPositions == null )
				this.peptideProteinPositions = SearchProteinSearcher.getInstance().getProteinDoublePositions( this.search, this.getPeptide(), this.getPeptidePosition1(), this.getPeptidePosition2() );

			return peptideProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptideProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	
	public String getProteinPositionsString() throws Exception {
		
		try {

			return StringUtils.join( this.getPeptideProteinPositions(), ", " );

		} catch ( Exception e ) {

			String msg = "Exception in getProteinPositionsString()";

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


	private int reportedPeptideId = -999;

	private ReportedPeptideDTO reportedPeptide;
	
	
	private PeptideDTO peptide;
	private int peptidePosition1 = -1;
	private int peptidePosition2 = -1;

	
	

	private SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel;


	private SearchDTO search;
	private List<SearchProteinDoublePosition> peptideProteinPositions;
	
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
