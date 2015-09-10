package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchLooplinkLookupDTO;

public class SearchLooplinkLookupDAO {
	
	private static final Logger log = Logger.getLogger(SearchLooplinkLookupDAO.class);

	private SearchLooplinkLookupDAO() { }
	public static SearchLooplinkLookupDAO getInstance() { return new SearchLooplinkLookupDAO(); }

	/**
	 * For the given search id, will populate the search_protein_lookup table
	 * @param searchId
	 * @throws Exception
	 */
	public void createEntriesForSearch( int searchId ) throws Exception {
		

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT a.nrseq_id, a.protein_position_1, a.protein_position_2, min( b.q_value ), min( c.q_value ) FROM "
				+ "looplink AS a INNER JOIN psm AS b ON a.psm_id = b.id INNER JOIN search_reported_peptide AS c ON ( b.search_id = c.search_id AND b.reported_peptide_id = c.reported_peptide_id ) "
				+ "WHERE b.search_id = ? GROUP BY a.nrseq_id, a.protein_position_1, a.protein_position_2";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );

			
			rs = pstmt.executeQuery();
			

			while( rs.next() ) {
				
				SearchLooplinkLookupDTO prll = new SearchLooplinkLookupDTO();
				
				prll.setSearchId( searchId );
				prll.setNrseqId( rs.getInt( 1 ) );
				prll.setProteinPosition1( rs.getInt( 2 ) );
				prll.setProteinPosition2( rs.getInt( 3 ) );
				prll.setBestPSMQValue( rs.getDouble( 4 ) );
				prll.setBestPeptideQValue( rs.getDouble( 5 ) );
				
				save( prll );
				
			}
						
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
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
		
		

		
		
	}
	
	/**
	 * Save the associated data to the database
	 * @param prpl
	 * @throws Exception
	 */
	public void save( SearchLooplinkLookupDTO prll ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "INSERT INTO search_looplink_lookup (search_id, nrseq_id, protein_position_1, protein_position_2, "
				+ "bestPSMQValue, bestPeptideQValue) VALUES (?, ?, ?, ?, ?, ?)";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1,  prll.getSearchId() );
			pstmt.setInt( 2,  prll.getNrseqId() );
			pstmt.setInt( 3,  prll.getProteinPosition1() );
			pstmt.setInt( 4,  prll.getProteinPosition2() );
			pstmt.setDouble( 5, prll.getBestPSMQValue() );
			pstmt.setDouble( 6, prll.getBestPeptideQValue() );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
	}
	
}