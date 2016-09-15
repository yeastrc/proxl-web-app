package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;

public class SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher {

	private static final Logger log = Logger.getLogger( SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher.class );

	private SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher() { }
	public static SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher getInstance() { return new SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher(); }
	
	
	private static final String SEARCH_SQL =
			"SELECT  * "
			+ " FROM srch_rep_pept__peptide"
			+ " WHERE search_id = ? AND reported_peptide_id = ? ";
	
	

	/**
	 * get records from srch_rep_pept__peptide for search id and reported peptide id
	 * @param searchId
	 * @param reportedPeptideId
	 * @return
	 * @throws Exception
	 */
	public List<SrchRepPeptPeptideDTO> getForSearchIdReportedPeptideId( int searchId, int reportedPeptideId ) throws Exception {
		
		List<SrchRepPeptPeptideDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = SEARCH_SQL;
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, searchId );

			counter++;
			pstmt.setInt( counter, reportedPeptideId );
			
			
			rs = pstmt.executeQuery();


			while( rs.next() ) {
				
				SrchRepPeptPeptideDTO result = new SrchRepPeptPeptideDTO();
				
				result.setId( rs.getInt( "id" ) );
				result.setSearchId( rs.getInt( "search_id" ) );
				result.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				
				result.setPeptideId( rs.getInt( "peptide_id" ) );
				result.setPeptidePosition_1( rs.getInt( "peptide_position_1" ) );
				result.setPeptidePosition_2( rs.getInt( "peptide_position_2" ) );
				
				results.add( result );
			}

		} catch ( Exception e ) {
			
			String msg = "getForSearchIdReportedPeptideId(), sql: " + sql;
			
			log.error( msg, e );
			
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
		
		
		return results;
		
	}
}
