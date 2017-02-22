package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.URLShortenerAssociatedProjectSearchIdDTO;

/**
 * DAO for url_shortener_associated_project_search_id table
 *
 */
public class URLShortenerAssociatedProjectSearchIdDAO {
	
	private static final Logger log = Logger.getLogger(URLShortenerAssociatedProjectSearchIdDAO.class);
	
	//  private constructor
	private URLShortenerAssociatedProjectSearchIdDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static URLShortenerAssociatedProjectSearchIdDAO getInstance() { 
		return new URLShortenerAssociatedProjectSearchIdDAO(); 
	}
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( URLShortenerAssociatedProjectSearchIdDTO item ) throws Exception {
		
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			save( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}


	private final String INSERT_SQL = "INSERT IGNORE INTO url_shortener_associated_project_search_id "
			+ " (url_shortener_id, project_search_id ) "
			+ " VALUES ( ?, ? ) ";
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( URLShortenerAssociatedProjectSearchIdDTO item, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
//		ResultSet rs = null;

		final String sql = INSERT_SQL;

		try {
			
			pstmt = dbConnection.prepareStatement( sql );
//			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getUrlShortenerId() );
			counter++;
			pstmt.setInt( counter, item.getProjectSearchId() );

			pstmt.executeUpdate();
			
		} catch ( SQLException sqlException ) {

			String msg = "Failed to insert URLShortenerAssociatedSearchIdDTO, sql: " + sql;
			log.error( msg, sqlException );
			throw sqlException;
			
		} catch ( Exception e ) {
			String msg = "Failed to insert URLShortenerAssociatedSearchIdDTO, sql: " + sql;
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
		}
	}


	/**
	 * @param id
	 * @param url
	 * @throws Exception
	 */
	public void delete( int id ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "DELETE FROM url_shortener WHERE id = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;

			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to delete, sql: " + sql;
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
		
	}
	



}
