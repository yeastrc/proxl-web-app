package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchMonolinkLookupDTO;

public class SearchMonolinkLookupDAO {

	private static final Logger log = Logger.getLogger(SearchMonolinkLookupDAO.class);
			
	private SearchMonolinkLookupDAO() { }
	public static SearchMonolinkLookupDAO getInstance() { return new SearchMonolinkLookupDAO(); }

	/**
	 * For the given search id, will populate the search_protein_lookup table
	 * @param searchId
	 * @throws Exception
	 */
	public void createEntriesForSearch( int searchId ) throws Exception {
		

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT a.nrseq_id, a.protein_position, min( b.q_value ), min( c.q_value ) FROM "
				+ "monolink AS a INNER JOIN psm AS b ON a.psm_id = b.id INNER JOIN search_reported_peptide AS c ON ( b.search_id = c.search_id AND b.reported_peptide_id = c.reported_peptide_id ) "
				+ "WHERE b.search_id = ? GROUP BY a.nrseq_id, a.protein_position";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );

			
			rs = pstmt.executeQuery();
			

			while( rs.next() ) {
				
				SearchMonolinkLookupDTO prml = new SearchMonolinkLookupDTO();
				
				prml.setSearchId( searchId );
				prml.setNrseqId( rs.getInt( 1 ) );
				prml.setProteinPosition( rs.getInt( 2 ) );
				prml.setBestPSMQValue( rs.getDouble( 3 ) );
				prml.setBestPeptideQValue( rs.getDouble( 4 ) );
				
				save( prml );
				
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
	public void save( SearchMonolinkLookupDTO prml ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "INSERT INTO search_monolink_lookup (search_id, nrseq_id, protein_position, "
				+ "bestPSMQValue, bestPeptideQValue) VALUES (?, ?, ?, ?, ?)";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1,  prml.getSearchId() );
			pstmt.setInt( 2,  prml.getNrseqId() );
			pstmt.setInt( 3,  prml.getProteinPosition() );
			pstmt.setDouble( 4, prml.getBestPSMQValue() );
			pstmt.setDouble( 5, prml.getBestPeptideQValue() );
			
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