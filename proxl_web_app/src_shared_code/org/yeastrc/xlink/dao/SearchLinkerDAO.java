package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchLinkerDTO;

/**
 * Table search_linker
 *
 */
public class SearchLinkerDAO {
	
	private static final Logger log = Logger.getLogger(SearchLinkerDAO.class);

	private SearchLinkerDAO() { }
	public static SearchLinkerDAO getInstance() { return new SearchLinkerDAO(); }
	

	
	/**
	 * This will INSERT the given SearchDTO into the database... 
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchLinkerDTO item ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			saveToDatabase( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
		
		
	/**
	 * This will INSERT the given SearchLinkerDTO into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchLinkerDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO search_linker (search_id, linker_id) VALUES (?, ?)";
		
		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setInt( counter, item.getLinkerId() );
			
			pstmt.executeUpdate();
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
		
		
	}
	
}
