package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchLinkerDTO;

/**
 * Table search_linker_tbl
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
		ResultSet rsGenKeys = null;

		final String sql = "INSERT INTO search_linker_tbl (search_id, linker_abbr, linker_name) VALUES ( ?, ?, ? )";
		
		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setString( counter, item.getLinkerAbbr() );
			counter++;
			pstmt.setString( counter, item.getLinkerName() );
			
			pstmt.executeUpdate();

			rsGenKeys = pstmt.getGeneratedKeys();
			if ( rsGenKeys.next() ) {
				item.setId( rsGenKeys.getInt( 1 ) );
			}

			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rsGenKeys != null ) {
				try { rsGenKeys.close(); } catch( Throwable t ) { ; }
				rsGenKeys = null;
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
