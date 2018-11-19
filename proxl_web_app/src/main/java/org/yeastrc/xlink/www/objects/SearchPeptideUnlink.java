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
 * Unlinked Reported Peptides retrieved when expanding a Protein entry
 *
 */
public class SearchPeptideUnlink extends SearchPeptide_BaseCommon {
	
	private static final Logger log = Logger.getLogger(SearchPeptideUnlink.class);

	private void populatePeptides() throws Exception {

		if ( populatePeptidesCalled ) {
			return;
		}
		
		populatePeptidesCalled = true;
		
		try {
			SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams = new SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams();
			srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setSearchId( this.getSearch().getSearchId() );
			srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setReportedPeptideId( this.getReportedPeptideId() );
			SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result =
					Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId.getInstance()
					.getSrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result( srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams );
			List<SrchRepPeptPeptideDTO> results = srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result.getSrchRepPeptPeptideDTOList();

			if ( results.size() != 1 ) {
				String msg = "List<SrchRepPeptPeptideDTO> results.size() != 1. SearchId: " +this.getSearch().getSearchId()
						+ ", ReportedPeptideId: " + this.getReportedPeptideId() ;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			SrchRepPeptPeptideDTO result = results.get( 0 );

			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( result.getPeptideId() );

			this.setPeptide( peptideDTO );
			
		} catch ( Exception e ) {

			String msg = "Exception in populatePeptides()";

			log.error( msg, e );

			throw e;
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

	public List<SearchProteinPosition> getPeptideProteinPositions() throws Exception {
		try {
			if( this.peptideProteinPositions == null ) {

				populatePeptides();

				this.peptideProteinPositions = 
						SearchProteinSearcher.getInstance().getProteinForUnlinked( this.getSearch(), this.getReportedPeptideId(), this.getPeptide().getId() );
			}

			return peptideProteinPositions;

		} catch ( Exception e ) {
			String msg = "Exception in getPeptideProteinPositions()";
			log.error( msg, e );
			throw e;
		}
	}

	
	private PeptideDTO peptide;

	private boolean populatePeptidesCalled = false;

	private List<SearchProteinPosition> peptideProteinPositions;
	
}
