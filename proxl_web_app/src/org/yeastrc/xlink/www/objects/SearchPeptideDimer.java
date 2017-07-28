package org.yeastrc.xlink.www.objects;

import java.security.InvalidParameterException;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.SearchPsmSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ReportedPeptideDTO;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SearchPeptideDimer {
	
	private static final Logger log = Logger.getLogger(SearchPeptideDimer.class);

	private void populatePeptides() throws Exception {

		if ( populatePeptidesCalled ) {
			
			return;
		}
		
		populatePeptidesCalled = true;
		
		try {
			SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams = new SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams();
			srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setSearchId(  this.getSearch().getSearchId() );
			srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setReportedPeptideId( this.getReportedPeptideId() );
			SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result =
					Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId.getInstance()
					.getSrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result( srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams );
			List<SrchRepPeptPeptideDTO> results = srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result.getSrchRepPeptPeptideDTOList();

			if ( results.size() != 2 ) {


				String msg = "List<SrchRepPeptPeptideDTO> results.size() != 2. SearchId: " + this.getSearch().getSearchId()
						+ ", ReportedPeptideId: " + this.getReportedPeptideId() ;

				log.error( msg );

				throw new ProxlWebappDataException( msg );
			}
			
			SrchRepPeptPeptideDTO result_1 = results.get( 0 );
			SrchRepPeptPeptideDTO result_2 = results.get( 1 );


			if( result_1.getPeptideId() == result_2.getPeptideId() ) {

				PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( result_1.getPeptideId() );

				this.setPeptide1( peptideDTO );
				this.setPeptide2( peptideDTO );
				
			} else {

				PeptideDTO peptideDTO1 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( result_1.getPeptideId() );
				this.setPeptide1( peptideDTO1 );
			
				PeptideDTO peptideDTO2 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( result_2.getPeptideId() );
				this.setPeptide2( peptideDTO2 );
			}
			

			this.peptide1ProteinPositions = 
					SearchProteinSearcher.getInstance()
					.getProteinForDimer( search, this.getReportedPeptideId(), this.getPeptide1().getId() );

			this.peptide2ProteinPositions = 
					SearchProteinSearcher.getInstance()
					.getProteinForDimer( search, this.getReportedPeptideId(), this.getPeptide2().getId() );



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


	public PeptideDTO getPeptide1() throws Exception {
		
		try {

			if( this.peptide1 == null )
				populatePeptides();

			return peptide1;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide1(PeptideDTO peptide) {
		this.peptide1 = peptide;
	}
	
	public PeptideDTO getPeptide2() throws Exception {
		
		try {

			if( this.peptide2 == null )
				populatePeptides();

			return peptide2;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2()";

			log.error( msg, e );

			throw e;
		}
	}
	public void setPeptide2(PeptideDTO peptide) {
		this.peptide2 = peptide;
	}


	
	
	public int getNumPsms() {
		
		throw new RuntimeException( "Unsuppported, No Logic to retrieve value");
//		return numPsms;
	}
	public void setNumPsms(int numPsms) {

		throw new RuntimeException( "Unsuppported, No Logic to retrieve value");
//		return numPsms;

//		this.numPsms = numPsms;
	}
	
	
	/**
	 * @return the psmId for a random psm record associated with this Peptide, null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId() throws Exception {

		Integer psmId = SearchPsmSearcher.getInstance().getSinglePsmId( this.getSearch().getSearchId(), this.getReportedPeptide().getId() );
		
		return psmId;
	}
	
	
	
	
	public List<SearchProteinPosition> getPeptide1ProteinPositions() throws Exception {
		
		try {

			if( this.peptide1ProteinPositions == null )
				populatePeptides();

			return peptide1ProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide1ProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	
	
	public List<SearchProteinPosition> getPeptide2ProteinPositions() throws Exception {
		
		try {

			if( this.peptide2ProteinPositions == null )
				populatePeptides();

			return peptide2ProteinPositions;

		} catch ( Exception e ) {

			String msg = "Exception in getPeptide2ProteinPositions()";

			log.error( msg, e );

			throw e;
		}
	}
	
	private List<SearchProteinPosition> peptide1ProteinPositions;
	private List<SearchProteinPosition> peptide2ProteinPositions;
	

	private int reportedPeptideId = -999;
	
	private boolean populatePeptidesCalled;


	private ReportedPeptideDTO reportedPeptide;
	
	
	private PeptideDTO peptide1;
	private PeptideDTO peptide2;

	
	
	private SearchDTO search;
//	private List<SearchProteinDoublePosition> peptideProteinPositions;
	
//	private int numPsms = -999;
	
}
