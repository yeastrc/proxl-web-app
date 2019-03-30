package org.yeastrc.xlink.www.objects;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.SearchCrosslinkProteinsFromPeptide;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result;

/**
 * Crosslink Reported Peptides retrieved when expanding a Protein entry
 *
 */
public class SearchPeptideCrosslink extends SearchPeptide_BaseCommon {

	private static final Logger log = LoggerFactory.getLogger( SearchPeptideCrosslink.class);
	
	private void populatePeptides() throws Exception {
		if ( populatePeptidesCalled ) {
			return;
		}
		populatePeptidesCalled = true;
		try {
			SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams = new SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams();
			srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setSearchId( this.getSearchId() );
			srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setReportedPeptideId( this.getReportedPeptideId() );
			SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result =
					Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId.getInstance()
					.getSrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result( srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams );
			List<SrchRepPeptPeptideDTO> results = srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result.getSrchRepPeptPeptideDTOList();

			if ( results.size() != 2 ) {
				String msg = "List<SrchRepPeptPeptideDTO> results.size() != 2. SearchId: " + this.getSearchId()
						+ ", ReportedPeptideId: " + this.getReportedPeptideId() ;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			SrchRepPeptPeptideDTO result_1 = results.get( 0 );
			SrchRepPeptPeptideDTO result_2 = results.get( 1 );
			if( result_1.getPeptideId() == result_2.getPeptideId() ) {
				//  Same peptide
				int position1 = result_1.getPeptidePosition_1();
				int position2 = result_2.getPeptidePosition_1();
				PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( result_1.getPeptideId() );
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
				PeptideDTO peptideDTO1 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( result_1.getPeptideId() );
				PeptideDTO peptideDTO2 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( result_2.getPeptideId() );
				int position1 = result_1.getPeptidePosition_1();
				int position2 = result_2.getPeptidePosition_1();
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
		} catch ( Exception e ) {
			String msg = "Exception in populatePeptides()";
			log.error( msg, e );
			throw e;
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
				this.peptide1ProteinPositions = 
						SearchCrosslinkProteinsFromPeptide.getInstance()
						.getProteinPositions( this.getSearch(), this.getReportedPeptideId(), this.getPeptide1().getId(), this.getPeptide1Position() );
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
				this.peptide2ProteinPositions = 
						SearchCrosslinkProteinsFromPeptide.getInstance()
						.getProteinPositions( this.getSearch(), this.getReportedPeptideId(), this.getPeptide2().getId(), this.getPeptide2Position() );
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
	
	private boolean populatePeptidesCalled = false;
	private PeptideDTO peptide1;
	private PeptideDTO peptide2;
	private int peptide1Position = -1;
	private int peptide2Position = -1;

	private List<SearchProteinPosition> peptide1ProteinPositions;
	private List<SearchProteinPosition> peptide2ProteinPositions;
}
