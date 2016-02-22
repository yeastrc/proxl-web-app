package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.DefaultPageViewDTO;

/**
 * DAO for default_page_view table
 *
 */
public class DefaultPageViewDAO {
	
	private static final Logger log = Logger.getLogger(DefaultPageViewDAO.class);

	//  private constructor
	private DefaultPageViewDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static DefaultPageViewDAO getInstance() { 
		return new DefaultPageViewDAO(); 
	}
	
	

//	CREATE TABLE default_page_view (
//	  search_id INT UNSIGNED NOT NULL,
//	  page_name VARCHAR(80) NOT NULL,
//	  auth_user_id INT UNSIGNED NOT NULL,
//	  url VARCHAR(6000) NOT NULL,


	
	
	


	/**
	 * @param searchId
	 * @param pageName
	 * @return null if not found
	 * @throws Exception
	 */
	public DefaultPageViewDTO getForSearchIdPageName( int searchId, String pageName ) throws Exception {


		DefaultPageViewDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM default_page_view WHERE search_id = ? AND page_name = ?";

//		CREATE TABLE default_page_view (
//		  search_id INT UNSIGNED NOT NULL,
//		  page_name VARCHAR(80) NOT NULL,
//		  auth_user_id INT UNSIGNED NOT NULL,
//		  url VARCHAR(6000) NOT NULL,


		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setString( 2, pageName );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				returnItem = populateResultObject( rs );

			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select DefaultPageViewDTO, id: " + searchId + ", sql: " + sql;
			
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
		
		return returnItem;
	}
	
	

	/**
	 * @param searchId
	 * @return 
	 * @throws Exception
	 */
	public List<DefaultPageViewDTO> getForSearchId( int searchId ) throws Exception {


		 List<DefaultPageViewDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM project WHERE search_id = ? ";

//		CREATE TABLE default_page_view (
//		  search_id INT UNSIGNED NOT NULL,
//		  page_name VARCHAR(80) NOT NULL,
//		  auth_user_id INT UNSIGNED NOT NULL,
//		  url VARCHAR(6000) NOT NULL,


		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				DefaultPageViewDTO item = populateResultObject( rs );
				results.add( item );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select DefaultPageViewDTO, id: " + searchId + ", sql: " + sql;
			
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
	
	


	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private DefaultPageViewDTO populateResultObject(ResultSet rs) throws SQLException {
		
		DefaultPageViewDTO returnItem = new DefaultPageViewDTO();

		returnItem.setSearchId( rs.getInt( "search_id" ) );
		returnItem.setPageName( rs.getString( "page_name" ) );
		returnItem.setAuthUserId( rs.getInt( "auth_user_id" ) );
		returnItem.setUrl( rs.getString( "url" ) );
		
		return returnItem;
	}
//	CREATE TABLE default_page_view (
//	  search_id INT UNSIGNED NOT NULL,
//	  page_name VARCHAR(80) NOT NULL,
//	  auth_user_id INT UNSIGNED NOT NULL,
//	  url VARCHAR(6000) NOT NULL,




	/**
	 * @param item
	 * @throws Exception
	 */
	public void saveOrUpdate( DefaultPageViewDTO item ) throws Exception {
		
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			saveOrUpdate( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}

	/**
	 * @param item
	 * @throws Exception
	 */
	public void saveOrUpdate( DefaultPageViewDTO item, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;


//		CREATE TABLE default_page_view (
//		  search_id INT UNSIGNED NOT NULL,
//		  page_name VARCHAR(80) NOT NULL,
//		  auth_user_id INT UNSIGNED NOT NULL,
//		  url VARCHAR(6000) NOT NULL,




		final String sql = "INSERT INTO default_page_view (search_id, page_name, auth_user_id, url ) " 
				+ " VALUES ( ?, ?, ?, ? ) "
				+ " ON DUPLICATE KEY UPDATE auth_user_id = ?, url = ?;";
		
		try {
			
			
			pstmt = dbConnection.prepareStatement( sql );
			
//			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setString( counter, item.getPageName() );
			counter++;
			pstmt.setInt( counter, item.getAuthUserId() );
			counter++;
			pstmt.setString( counter, item.getUrl() );

			counter++;
			pstmt.setInt( counter, item.getAuthUserId() );
			counter++;
			pstmt.setString( counter, item.getUrl() );

			pstmt.executeUpdate();
			
//			rs = pstmt.getGeneratedKeys();
//
//			if( rs.next() ) {
//				item.setId( rs.getInt( 1 ) );
//			} else {
//				
//				String msg = "Failed to insert DefaultPageViewDTO, generated key not found.";
//				
//				log.error( msg );
//				
//				throw new Exception( msg );
//			}
			
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert DefaultPageViewDTO, sql: " + sql;
			
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
	 * Update url = ?, auth_user_id = ? 
	 * @param id
	 * @param url
	 * @throws Exception
	 */
	public void updateUrlAuthUserId( DefaultPageViewDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE default_page_view SET url = ?, auth_user_id = ? WHERE search_id = ? AND page_name = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );


//			CREATE TABLE default_page_view (
//			  search_id INT UNSIGNED NOT NULL,
//			  page_name VARCHAR(80) NOT NULL,
//			  auth_user_id INT UNSIGNED NOT NULL,
//			  url VARCHAR(6000) NOT NULL,


			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setString( counter, item.getUrl() );
			
			counter++;
			pstmt.setInt( counter, item.getAuthUserId() );

			
			counter++;
			pstmt.setInt( counter, item.getSearchId() );

			counter++;
			pstmt.setString( counter, item.getPageName() );
			
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update url = ?, auth_user_id = ?, sql: " + sql;
			
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
