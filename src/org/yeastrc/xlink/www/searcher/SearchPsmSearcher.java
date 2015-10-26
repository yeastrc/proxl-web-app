package org.yeastrc.xlink.www.searcher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.PsmDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.www.objects.PsmWebDisplay;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchPeptideDimer;
import org.yeastrc.xlink.www.objects.SearchPeptideLooplink;
import org.yeastrc.xlink.www.objects.SearchPeptideMonolink;
import org.yeastrc.xlink.www.objects.SearchPeptideUnlink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;
import org.yeastrc.xlink.www.web_utils.RetentionTimeScalingAndRounding;

public class SearchPsmSearcher {

	private static final Logger log = Logger.getLogger(SearchPsmSearcher.class);
	
	private SearchPsmSearcher() { }
	private static final SearchPsmSearcher _INSTANCE = new SearchPsmSearcher();
	public static SearchPsmSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * Get the number of PSMs in the database corresponding to the given crosslink with its given cutoffs
	 * @param crosslink
	 * @return
	 * @throws Exception
	 */
	public int getNumPsms( SearchProteinCrosslink crosslink ) throws Exception {
		
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = 
				"SELECT COUNT(*) FROM psm AS a INNER JOIN search_reported_peptide AS b ON ( a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id ) "
				+ "INNER JOIN crosslink AS c ON a.id = c.psm_id WHERE a.search_id = ? AND a.q_value <= ? AND ( b.q_value <= ? OR b.q_value IS NULL )   AND "
				+ "c.nrseq_id_1 = ? AND c.nrseq_id_2 = ? AND c.protein_1_position = ? AND c.protein_2_position = ? ";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
	
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1,  crosslink.getSearch().getId() );
			pstmt.setDouble( 2,  crosslink.getPsmCutoff() );
			pstmt.setDouble( 3,  crosslink.getPeptideCutoff() );
			pstmt.setInt( 4,  crosslink.getProtein1().getNrProtein().getNrseqId() );
			pstmt.setInt( 5,  crosslink.getProtein2().getNrProtein().getNrseqId() );
			pstmt.setInt( 6,  crosslink.getProtein1Position() );
			pstmt.setInt( 7,  crosslink.getProtein2Position() );

			rs = pstmt.executeQuery();
			
			rs.next();
			count = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPsms( SearchProteinCrosslink crosslink ): sql: " + sql;
			
			log.error( msg );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		
		return count;
	}
	
	/**
	 * Get the number of PSMs in the database corresponding to the given looplink with its given cutoffs
	 * @param crosslink
	 * @return
	 * @throws Exception
	 */
	public int getNumPsms( SearchProteinLooplink looplink ) throws Exception {
		
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = 
				"SELECT COUNT(*) FROM psm AS a INNER JOIN search_reported_peptide AS b ON ( a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id ) "
				+ "INNER JOIN looplink AS c ON a.id = c.psm_id WHERE a.search_id = ? AND a.q_value <= ? AND ( b.q_value <= ? OR b.q_value IS NULL )   AND "
				+ "c.nrseq_id = ? AND c.protein_position_1 = ? AND c.protein_position_2 = ? ";
	
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1,  looplink.getSearch().getId() );
			pstmt.setDouble( 2,  looplink.getPsmCutoff() );
			pstmt.setDouble( 3,  looplink.getPeptideCutoff() );
			pstmt.setInt( 4,  looplink.getProtein().getNrProtein().getNrseqId() );
			pstmt.setInt( 5,  looplink.getProteinPosition1() );
			pstmt.setInt( 6,  looplink.getProteinPosition2() );

			rs = pstmt.executeQuery();
			
			rs.next();
			count = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPsms( SearchProteinLooplink looplink ): sql: " + sql;
			
			log.error( msg );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		
		return count;
	}
	
	
	private static final String GET_NUM_PSMS_FOR_MONOLINK_SQL =
			"SELECT COUNT(DISTINCT(a.id)) FROM psm AS a INNER JOIN search_reported_peptide AS b ON ( a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id ) "
					+ "INNER JOIN monolink AS c ON a.id = c.psm_id WHERE a.search_id = ? AND a.q_value <= ? AND ( b.q_value <= ? OR b.q_value IS NULL )   AND "
					+ "c.nrseq_id = ? AND c.protein_position = ?";
	
	/**
	 * Get the number of PSMs in the database corresponding to the given monolink with its given cutoffs
	 * @param crosslink
	 * @return
	 * @throws Exception
	 */
	public int getNumPsms( SearchProteinMonolink monolink ) throws Exception {
		
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = GET_NUM_PSMS_FOR_MONOLINK_SQL;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
					

			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1,  monolink.getSearch().getId() );
			pstmt.setDouble( 2,  monolink.getPsmCutoff() );
			pstmt.setDouble( 3,  monolink.getPeptideCutoff() );
			pstmt.setInt( 4,  monolink.getProtein().getNrProtein().getNrseqId() );
			pstmt.setInt( 5,  monolink.getProteinPosition() );

			rs = pstmt.executeQuery();
			
			rs.next();
			count = rs.getInt( 1 );
			
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPsms( SearchProteinMonolink monolink ): sql: " + sql;
			
			log.error( msg );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		
		return count;
	}
	

	/**
	 * @param crosslink
	 * @return
	 * @throws Exception
	 */
	public List<PsmDTO> getPsms( SearchPeptideCrosslink crosslink ) throws Exception {

		return getPsms( crosslink.getSearch().getId(), crosslink.getReportedPeptide().getId(), crosslink.getPsmQValueCutoff() );
	}
	

	/**
	 * @param looplink
	 * @return
	 * @throws Exception
	 */
	public List<PsmDTO> getPsms( SearchPeptideLooplink looplink ) throws Exception {

		return getPsms( looplink.getSearch().getId(), looplink.getReportedPeptide().getId(), looplink.getPsmQValueCutoff() );
	}
	
	
	
	/**
	 * @param monolink
	 * @return
	 * @throws Exception
	 */
	public List<PsmDTO> getPsms( SearchPeptideMonolink monolink ) throws Exception {

		return getPsms( monolink.getSearch().getId(), monolink.getReportedPeptide().getId(), monolink.getPsmQValueCutoff() );
	}
	
	


	/**
	 * @param link
	 * @return
	 * @throws Exception
	 */
	public List<PsmDTO> getPsms( SearchPeptideUnlink link ) throws Exception {

		return getPsms( link.getSearch().getId(), link.getReportedPeptide().getId(), link.getPsmQValueCutoff() );
	}
	
	
	

	/**
	 * @param link
	 * @return
	 * @throws Exception
	 */
	public List<PsmDTO> getPsms( SearchPeptideDimer link ) throws Exception {

		return getPsms( link.getSearch().getId(), link.getReportedPeptide().getId(), link.getPsmQValueCutoff() );
	}
	


	/**
	 * @param searchId
	 * @param reportedPeptideId
	 * @param psmQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public List<PsmDTO> getPsms( int searchId, int reportedPeptideId, double psmQValueCutoff  ) throws Exception {
		
		List<PsmDTO> psms = new ArrayList<PsmDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = 
				"SELECT id "
				+ " FROM psm  "
				+ " LEFT OUTER JOIN percolator_psm ON psm.id = percolator_psm.psm_id"
				
				+ " WHERE search_id = ? AND reported_peptide_id = ? AND psm.q_value <= ? "
				
				+ " ORDER BY psm.q_value , percolator_psm.pep, percolator_psm.svm_score DESC, psm.id ";

		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, reportedPeptideId );
			pstmt.setDouble( 3, psmQValueCutoff );

			rs = pstmt.executeQuery();
			
			while( rs.next() )
				psms.add( PsmDAO.getInstance().getPsmDTO( rs.getInt( 1 ) ) );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getPsmsInternal( ... ): sql: " + sql;
			
			log.error( msg );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		return psms;
	}
	

	/**
	 * @param searchId
	 * @param reportedPeptideId
	 * @param psmQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public List<PsmWebDisplay> getPsmsWebDisplay( int searchId, int reportedPeptideId, double psmQValueCutoff  ) throws Exception {
		
		List<PsmWebDisplay> psms = new ArrayList<PsmWebDisplay>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = 
				"SELECT psm.id AS psm_id, psm.charge, scan.start_scan_number AS scan_number, scan.retention_time, "
				+        "  scan.preMZ, scan_file.filename AS scan_filename "
						
				+ " FROM psm  "
				+ " LEFT OUTER JOIN percolator_psm ON psm.id = percolator_psm.psm_id"
				+ " INNER JOIN scan ON psm.scan_id = scan.id "
				+ " INNER JOIN scan_file ON scan.scan_file_id = scan_file.id "
				
				+ " WHERE search_id = ? AND reported_peptide_id = ? AND psm.q_value <= ? "
				
				+ " ORDER BY psm.q_value , percolator_psm.pep, percolator_psm.svm_score DESC, psm.id ";

		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, reportedPeptideId );
			pstmt.setDouble( 3, psmQValueCutoff );

			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				PsmWebDisplay psmWebDisplay = new PsmWebDisplay();
				
				PsmDTO psmDTO = PsmDAO.getInstance().getPsmDTO( rs.getInt( "psm_id" ) );
				psmWebDisplay.setPsmDTO( psmDTO );

				psmWebDisplay.setCharge( rs.getInt( "charge" ) );

				psmWebDisplay.setScanNumber( rs.getInt( "scan_number" ) );
				psmWebDisplay.setScanFilename( rs.getString( "scan_filename" ) );
				
				psmWebDisplay.setRetentionTime( rs.getBigDecimal( "retention_time" ) );
				
				//  Get the retention time in minutes
				
//				int retentionInMinutesRoundedInt = RetentionTimeScalingAndRounding.retentionTimeToMinutesRounded( psmWebDisplay.getRetentionTime() );
//				
//				psmWebDisplay.setRetentionTimeMinutesRounded( retentionInMinutesRoundedInt );

				BigDecimal retentionInMinutesRounded = RetentionTimeScalingAndRounding.retentionTimeToMinutesRounded( psmWebDisplay.getRetentionTime() );
				
				psmWebDisplay.setRetentionTimeMinutesRounded( retentionInMinutesRounded );

				
				BigDecimal preMZ = rs.getBigDecimal( "preMZ" );
				
				psmWebDisplay.setPreMZ( preMZ );

				//  Round the preMZ
				
				String preMZRoundedString = null;
				
				if ( preMZ != null ) {
					
					// first param to setScale is the number of decimal places to keep  
					BigDecimal preMZRounded = preMZ.setScale( 5, RoundingMode.HALF_UP );
					
					preMZRoundedString = preMZRounded.toString();  // convert to string so trailing zeros are preserved
				}
				
				psmWebDisplay.setPreMZRounded( preMZRoundedString );
				
				
				psms.add( psmWebDisplay );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getPsmsInternal( ... ): sql: " + sql;
			
			log.error( msg );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		return psms;
	}
	
	

	/**
	 * Get the psm id for any psm that matches the search criteria
	 * 
	 * @param crosslink
	 * @return null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId( SearchPeptideCrosslink crosslink ) throws Exception {

		return getSinglePsmId( crosslink.getSearch().getId(), crosslink.getReportedPeptide().getId() );
	}
	

	/**
	 * Get the psm id for any psm that matches the search criteria
	 * 
	 * @param looplink
	 * @return null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId( SearchPeptideLooplink looplink ) throws Exception {

		return getSinglePsmId( looplink.getSearch().getId(), looplink.getReportedPeptide().getId() );
	}
	
	
	
	/**
	 * Get the psm id for any psm that matches the search criteria
	 * 
	 * @param monolink
	 * @return null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId( SearchPeptideMonolink monolink ) throws Exception {

		return getSinglePsmId( monolink.getSearch().getId(), monolink.getReportedPeptide().getId() );
	}
	
	


	/**
	 * Get the psm id for any psm that matches the search criteria
	 * 
	 * @param link
	 * @return null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId( SearchPeptideUnlink link ) throws Exception {

		return getSinglePsmId( link.getSearch().getId(), link.getReportedPeptide().getId() );
	}
	
	
	

	/**
	 * Get the psm id for any psm that matches the search criteria
	 * 
	 * @param link
	 * @return null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId( SearchPeptideDimer link ) throws Exception {

		return getSinglePsmId( link.getSearch().getId(), link.getReportedPeptide().getId() );
	}
	


	/**
	 * Get the psm id for any psm that matches the search criteria
	 * 
	 * @param searchId
	 * @param reportedPeptideId
	 * @return null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId( int searchId, int reportedPeptideId  ) throws Exception {
		
		Integer singlePsmId = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = 
				"SELECT id "
				+ " FROM psm  "
				
				+ " WHERE search_id = ? AND reported_peptide_id = ? LIMIT 1 ";

		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, reportedPeptideId );

			rs = pstmt.executeQuery();
			
			if( rs.next() )
				singlePsmId = rs.getInt( "id" );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getSinglePsmIdInternal( ... ): sql: " + sql;
			
			log.error( msg );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		return singlePsmId;
	}
}
