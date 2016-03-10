package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchSearchProgramDTO;

/**
 * search__search_program table
 */
public class SearchSearchProgramDAO {
	
	private static final Logger log = Logger.getLogger(SearchSearchProgramDAO.class);

	private SearchSearchProgramDAO() { }
	public static SearchSearchProgramDAO getInstance() { return new SearchSearchProgramDAO(); }
	

	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( SearchSearchProgramDTO item ) throws Exception {
		
		Connection conn = null;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			save( item, conn );

		} catch ( Exception e ) {
			
			String msg = "ERROR inserting item. Error getting database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n item: " + item;
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
	}
	
	

	private final String INSERT_SQL = 
			"INSERT INTO search__search_program ( search_id, search_program_id, version ) "
			+ "VALUES ( ?, ?, ? )";
	
	/**
	 * @param item
	 * @param conn
	 * @throws Exception
	 */
	public void save( SearchSearchProgramDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
//		ResultSet rs = null;
		
		final String sql = INSERT_SQL;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;

			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setInt( counter, item.getSearchProgramId() );
			counter++;
			pstmt.setString( counter, item.getVersion() );
			
			pstmt.executeUpdate();
			
//			rs = pstmt.getGeneratedKeys();
//
//			if( rs.next() ) {
//				item.setId( rs.getInt( 1 ) );
//			} else
//				throw new Exception( "Failed to insert item" );
			
		} catch ( Exception e ) {
			
			String msg = "ERROR inserting item. database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n item: " + item
					+ "\nsql: " + sql;
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
			
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

