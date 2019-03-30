package org.yeastrc.xlink.www.objects;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.SearchLooplinkProteinsFromPeptide;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result;


/**
 * Looplink Reported Peptides retrieved when expanding a Protein entry
 *
 */
public class SearchPeptideLooplink extends SearchPeptide_BaseCommon {

	private static final Logger log = LoggerFactory.getLogger( SearchPeptideLooplink.class);
	
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
			int position1 = result.getPeptidePosition_1();
			int position2 = result.getPeptidePosition_2();
			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( result.getPeptideId() );
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
			if ( peptidePosition1 == -1 ) {
				populatePeptides();
			}
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
			if ( peptidePosition2 == -1 ) {
				populatePeptides();
			}
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

	public List<SearchProteinDoublePosition> getPeptideProteinPositions() throws Exception {
		try {
			if( this.peptideProteinPositions == null ) {
				populatePeptides();
				this.peptideProteinPositions = 
						SearchLooplinkProteinsFromPeptide.getInstance()
						.getLooplinkProteinPositions( this.getSearch(), this.getReportedPeptideId(), this.getPeptide().getId(), this.getPeptidePosition1(), this.getPeptidePosition2() );
			}
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

	
	private boolean populatePeptidesCalled = false;
	private PeptideDTO peptide;
	private int peptidePosition1 = -1;
	private int peptidePosition2 = -1;
	private List<SearchProteinDoublePosition> peptideProteinPositions;

}
