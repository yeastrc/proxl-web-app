package org.yeastrc.xlink.www.objects;


import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.CrosslinkDAO;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dto.CrosslinkDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searchers.PsmCountForSearchIdReportedPeptideIdSearcher;
import org.yeastrc.xlink.searchers.PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;



public class SearchPeptideCrosslink {
	
	private static final Logger log = Logger.getLogger(SearchPeptideCrosslink.class);


	private void populatePeptides() throws Exception {
		
		Integer psmId = getSinglePsmId();
		
		if ( psmId == null ) {
			
			log.warn( "No PSMs for search.id : " + this.search.getId() 
					+ ", this.getReportedPeptideId(): " + this.getReportedPeptideId() );
			
			return;
		}
		
		try {
			
			//  Get crosslink table entry for a psm.  assume the peptide position is the same for all.


			CrosslinkDTO crosslinkDTO = CrosslinkDAO.getInstance().getARandomCrosslinkDTOForPsmId( psmId );

			if ( crosslinkDTO == null ) {


				String msg = "crosslinkDTO == null for psmId: " + psmId;

				log.error( msg );

				throw new Exception( msg );

			}

			if( crosslinkDTO.getPeptide1Id() == crosslinkDTO.getPeptide2Id() ) {

				//  Same peptide

				int position1 = crosslinkDTO.getPeptide1Position();
				int position2 = crosslinkDTO.getPeptide2Position();

				PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( crosslinkDTO.getPeptide1Id() );

				this.setPeptide1( peptideDTO );
				this.setPeptide2( peptideDTO );

				if( position1 <= position2 ) {
					this.setPeptide1Position( position1 );
					this.setPeptide2Position( position2 );
				} else {
					this.setPeptide1Position( position2 );
					this.setPeptide2Position( position1 );
				}

			} else {

				//  different peptides

				PeptideDTO peptideDTO1 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( crosslinkDTO.getPeptide1Id() );
				PeptideDTO peptideDTO2 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( crosslinkDTO.getPeptide2Id() );

				int position1 = crosslinkDTO.getPeptide1Position();
				int position2 = crosslinkDTO.getPeptide2Position();



				if( peptideDTO1.getId() <= peptideDTO2.getId() ) {
					this.setPeptide1( peptideDTO1 );
					this.setPeptide1Position( position1 );

					this.setPeptide2( peptideDTO2 );
					this.setPeptide2Position( position2 );

				} else {
					this.setPeptide1( peptideDTO2 );
					this.setPeptide1Position( position2 );

					this.setPeptide2( peptideDTO1 );
					this.setPeptide2Position( position1 );
				}
			}

			this.peptide1ProteinPositions = 
					SearchProteinSearcher.getInstance()
					.getProteinPositions( psmId, this.getPeptide1().getId(), this.getPeptide1Position(), search );

			this.peptide2ProteinPositions = 
					SearchProteinSearcher.getInstance()
					.getProteinPositions( psmId, this.getPeptide2().getId(), this.getPeptide2Position(), search );

		} catch ( Exception e ) {

			String msg = "Exception in populatePeptides()";

			log.error( msg, e );

			throw e;
		}
	}
	
//	@JsonIgnore // Don't serialize to JSON
//	public SearchDTO getSearch() {
//		return search;
//	}
	public void setSearch(SearchDTO search) {
		this.search = search;
	}
	

	public int getSearchId() {
		
		return search.getId();
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


	
	
	
	public PeptideDTO getPeptide1() throws Exception {
		
		try {

			if( this.peptide1 == null )
				this.populatePeptides();

			return this.peptide1;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide1(PeptideDTO peptide1) {
		this.peptide1 = peptide1;
	}
	public PeptideDTO getPeptide2() throws Exception {
		
		try {

			if( this.peptide1 == null )
				this.populatePeptides();

			return peptide2;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide2(PeptideDTO peptide2) {
		this.peptide2 = peptide2;
	}
	public int getPeptide1Position() throws Exception {
		
		try {

			if( this.peptide1Position == -1 )
				this.populatePeptides();

			return peptide1Position;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1Position()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide1Position(int peptide1Position) {
		this.peptide1Position = peptide1Position;
	}
	public int getPeptide2Position() throws Exception {
		
		try {

			if( this.peptide2Position == -1 )
				this.populatePeptides();

			return peptide2Position;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2Position()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide2Position(int peptide2Position) {
		this.peptide2Position = peptide2Position;
	}

	
	public String getPeptide1ProteinPositionsString() throws Exception {
		return StringUtils.join( this.getPeptide1ProteinPositions(), ", " );
	}
	public List<SearchProteinPosition> getPeptide1ProteinPositions() throws Exception {
		
		try {

			if( this.peptide1ProteinPositions == null ) {
				
				populatePeptides();
			}

			return peptide1ProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1ProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide1ProteinPositions(
			List<SearchProteinPosition> peptide1ProteinPositions) {
		this.peptide1ProteinPositions = peptide1ProteinPositions;
	}

	public String getPeptide2ProteinPositionsString() throws Exception {
		
		try {

			return StringUtils.join( this.getPeptide2ProteinPositions(), ", " );

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2ProteinPositionsString()";

			log.error( msg, e );

			throw e;
		}
	}
	public List<SearchProteinPosition> getPeptide2ProteinPositions() throws Exception {
		
		try {

			if( this.peptide2ProteinPositions == null ) {
				
				populatePeptides();
			}

			return peptide2ProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2ProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide2ProteinPositions(
			List<SearchProteinPosition> peptide2ProteinPositions) {
		this.peptide2ProteinPositions = peptide2ProteinPositions;
	}
	

	
	public void setNumUniquePsms(int numUniquePsms) {
		this.numUniquePsms = numUniquePsms;
		numUniquePsmsSet = true;
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
				.getPsmCountForSearchIdReportedPeptideId( this.reportedPeptideId, search.getId(), searcherCutoffValuesSearchLevel );

		numPsmsSet = true;

		return numPsms;
	}

	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
		numPsmsSet = true;
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




	
	/**
	 * @return the psmId for a random psm record associated with this Peptide, null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId() throws Exception {

		Integer psmId = SearchPsmSearcher.getInstance().getSinglePsmId( this.getSearchId(), this.getReportedPeptideId() );
		
		return psmId;
	}
	
	
	
	private int reportedPeptideId = -999;

	private ReportedPeptideDTO reportedPeptide;
	
	
	private PeptideDTO peptide1;
	private PeptideDTO peptide2;
	private int peptide1Position = -1;
	private int peptide2Position = -1;



	private SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel;
	


	private SearchDTO search;
	private List<SearchProteinPosition> peptide1ProteinPositions;
	private List<SearchProteinPosition> peptide2ProteinPositions;
	
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
