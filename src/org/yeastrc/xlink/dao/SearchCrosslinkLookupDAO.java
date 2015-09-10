package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchCrosslinkLookupDTO;

public class SearchCrosslinkLookupDAO {

	private static final Logger log = Logger.getLogger(SearchCrosslinkLookupDAO.class);
			
	private SearchCrosslinkLookupDAO() { }
	public static SearchCrosslinkLookupDAO getInstance() { return new SearchCrosslinkLookupDAO(); }

	/**
	 * For the given search id, will populate the search_protein_lookup table
	 * @param searchId
	 * @throws Exception
	 */
	public void createEntriesForSearch( int searchId ) throws Exception {
		

		Connection conn = null;
		PreparedStatement pstmt = null;
		Statement st = null;
		ResultSet rs = null;

		String disableKeysSQL = "ALTER TABLE search_crosslink_lookup DISABLE KEYS";
		
		String sql = "SELECT a.nrseq_id_1, a.nrseq_id_2, a.protein_1_position, a.protein_2_position, min( b.q_value ), min( c.q_value ) FROM "
				+ "crosslink AS a INNER JOIN psm AS b ON a.psm_id = b.id INNER JOIN search_reported_peptide AS c ON ( b.search_id = c.search_id AND b.reported_peptide_id = c.reported_peptide_id ) "
				+ "WHERE b.search_id = ? GROUP BY a.nrseq_id_1, a.nrseq_id_2, a.protein_1_position, a.protein_2_position";

		String enableKeysSQL = "ALTER TABLE search_crosslink_lookup ENABLE KEYS";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			st = conn.createStatement();
			st.execute( disableKeysSQL );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );

			
			rs = pstmt.executeQuery();
			

			while( rs.next() ) {
				
				SearchCrosslinkLookupDTO prcl = new SearchCrosslinkLookupDTO();
				
				prcl.setSearchId( searchId );
				prcl.setNrseqId1( rs.getInt( 1 ) );
				prcl.setNrseqId2( rs.getInt( 2 ) );
				prcl.setProtein1Position( rs.getInt( 3 ) );
				prcl.setProtein2Position( rs.getInt( 4 ) );
				prcl.setBestPSMQValue( rs.getDouble( 5 ) );
				prcl.setBestPeptideQValue( rs.getDouble( 6 ) );
				
				save( prcl );
				
			}
			
			st.execute( enableKeysSQL );
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' \n sql: " + sql
					+ "\n disableKeysSQL: " + disableKeysSQL
					+ "\n enableKeysSQL: " + enableKeysSQL
					, e );
			
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
			
			if( st != null ) {
				try { st.close(); } catch( Throwable t ) { ; }
				st = null;
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
	public void save( SearchCrosslinkLookupDTO prcl ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "INSERT INTO search_crosslink_lookup (search_id, nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position, "
				+ "bestPSMQValue, bestPeptideQValue) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1,  prcl.getSearchId() );
			pstmt.setInt( 2,  prcl.getNrseqId1() );
			pstmt.setInt( 3,  prcl.getNrseqId2() );
			pstmt.setInt( 4,  prcl.getProtein1Position() );
			pstmt.setInt( 5,  prcl.getProtein2Position() );
			pstmt.setDouble( 6, prcl.getBestPSMQValue() );
			pstmt.setDouble( 7, prcl.getBestPeptideQValue() );
			
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