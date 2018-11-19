package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result;

/**
 * Dimer Reported Peptides retrieved when expanding a Protein entry
 *
 */
public class SearchPeptideDimer extends SearchPeptide_BaseCommon {
	
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
					.getProteinForDimer( this.getSearch(), this.getReportedPeptideId(), this.getPeptide1().getId() );

			this.peptide2ProteinPositions = 
					SearchProteinSearcher.getInstance()
					.getProteinForDimer( this.getSearch(), this.getReportedPeptideId(), this.getPeptide2().getId() );

		} catch ( Exception e ) {
			String msg = "Exception in populatePeptides()";
			log.error( msg, e );
			throw e;
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

	private boolean populatePeptidesCalled;
	
	private PeptideDTO peptide1;
	private PeptideDTO peptide2;

	
}
