package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.MatchedPeptideDAO;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dto.MatchedPeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SearchPeptideUnlink {
	
	private static final Logger log = Logger.getLogger(SearchPeptideUnlink.class);

	private void populatePeptides() throws Exception {
		
		Integer psmId = getSinglePsmId();
		
		if ( psmId == null ) {
			
			log.warn( "No PSMs for search.id : " + search.getId() 
					+ ", this.getReportedPeptideId(): " + this.getReportedPeptideId() );
			
			return;
		}
		
		try {
			//  Get MatchedPeptide table entries for a psm.
			List<MatchedPeptideDTO> results = MatchedPeptideDAO.getInstance().getMatchedPeptideDTOForPsmId(  psmId );

			if ( results.size() < 1 ) {

				String msg = "results.size() < 1 for psmId: " + psmId;

				log.error( msg );

				throw new Exception( msg );
				
			}

			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( results.get(0).getPeptide_id() );

			this.setPeptide( peptideDTO );
			
			this.peptideProteinPositions = SearchProteinSearcher.getInstance().getProteinForUnlinked( psmId, this.peptide.getId(), this.search);

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




	
	
	public int getNumPsms() {
		
		throw new RuntimeException( "Unsuppported, No Logic to retrieve value");
//		return numPsms;
	}
	public void setNumPsms(int numPsms) {
		
		throw new RuntimeException( "Unsuppported, No Logic to retrieve value");

//		this.numPsms = numPsms;
	}

	
	/**
	 * @return the psmId for a random psm record associated with this Peptide, null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId() throws Exception {

		Integer psmId = SearchPsmSearcher.getInstance().getSinglePsmId( this.getSearch().getId(), this.getReportedPeptide().getId() );
		
		return psmId;
	}
	
	public List<SearchProteinPosition> getPeptideProteinPositions() throws Exception {
		
		try {

			if( this.peptideProteinPositions == null )
				populatePeptides();

			return peptideProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptideProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}


	private int reportedPeptideId = -999;

	

	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide;

	
	
	private SearchDTO search;
	private List<SearchProteinPosition> peptideProteinPositions;
	
//	private int numPsms = -999;
	
}
