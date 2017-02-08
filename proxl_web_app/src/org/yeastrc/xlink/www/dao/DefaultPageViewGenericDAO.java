package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.DefaultPageViewGenericDTO;

/**
 * DAO for default_page_view_generic table
 *
 */
public class DefaultPageViewGenericDAO {
	
	private static final Logger log = Logger.getLogger(DefaultPageViewGenericDAO.class);

	//  private constructor
	private DefaultPageViewGenericDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static DefaultPageViewGenericDAO getInstance() { 
		return new DefaultPageViewGenericDAO(); 
	}
	
	/**
	 * @param projectSearchId
	 * @param pageName
	 * @return null if not found
	 * @throws Exception
	 */
	public DefaultPageViewGenericDTO getForProjectSearchIdPageName( int projectSearchId, String pageName ) throws Exception {

		DefaultPageViewGenericDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM default_page_view_generic WHERE project_search_id = ? AND page_name = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectSearchId );
			pstmt.setString( 2, pageName );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				returnItem = populateResultObject( rs );

			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select DefaultPageViewDTO, projectSearchId: " + projectSearchId + ", sql: " + sql;
			
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
	
	
	//  Not currently used
	
	/**
	 * @param projectSearchId
	 * @return 
	 * @throws Exception
	 */
	public List<DefaultPageViewGenericDTO> getForProjectSearchId( int projectSearchId ) throws Exception {


		 List<DefaultPageViewGenericDTO> results = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM project WHERE project_search_id = ? ";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectSearchId );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				DefaultPageViewGenericDTO item = populateResultObject( rs );
				results.add( item );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select DefaultPageViewDTO, projectSearchId: " + projectSearchId + ", sql: " + sql;
			
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
	private DefaultPageViewGenericDTO populateResultObject(ResultSet rs) throws SQLException {
		
		DefaultPageViewGenericDTO returnItem = new DefaultPageViewGenericDTO();

		returnItem.setProjectSearchId( rs.getInt( "project_search_id" ) );
		returnItem.setPageName( rs.getString( "page_name" ) );
		returnItem.setAuthUserIdCreated( rs.getInt( "auth_user_id_created_record" ) );
		returnItem.setAuthUserIdLastUpdated( rs.getInt( "auth_user_id_last_updated_record" ) );
		returnItem.setDateCreated( rs.getDate( "date_record_created" ) );
		returnItem.setDateLastUpdated( rs.getDate( "date_record_last_updated" ) );
		returnItem.setUrl( rs.getString( "url" ) );
		returnItem.setQueryJSON( rs.getString( "query_json" ) );
		
		return returnItem;
	}


	/**
	 * @param item
	 * @throws Exception
	 */
	public void saveOrUpdate( DefaultPageViewGenericDTO item ) throws Exception {
		
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			saveOrUpdate( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}


	private final String INSERT_SQL = "INSERT INTO default_page_view_generic "
			+ " (project_search_id, page_name, "
			+ 	" auth_user_id_created_record, auth_user_id_last_updated_record, "
			+ 	" date_record_created, date_record_last_updated, "
			+ 	" url, query_json ) " 
			+ " VALUES ( ?, ?, ?, ?, NOW(), NOW(), ?, ? ) "
			+ " ON DUPLICATE KEY UPDATE auth_user_id_last_updated_record = ?, url = ?, query_json = ?, "
			+ 		" date_record_last_updated = NOW() ";
	
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void saveOrUpdate( DefaultPageViewGenericDTO item, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;



		final String sql = INSERT_SQL;

		try {
			
			
			pstmt = dbConnection.prepareStatement( sql );
			
//			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getProjectSearchId() );
			counter++;
			pstmt.setString( counter, item.getPageName() );
			counter++;
			pstmt.setInt( counter, item.getAuthUserIdCreated() );
			counter++;
			pstmt.setInt( counter, item.getAuthUserIdLastUpdated() );
			counter++;
			pstmt.setString( counter, item.getUrl() );
			counter++;
			pstmt.setString( counter, item.getQueryJSON() );

			counter++;
			pstmt.setInt( counter, item.getAuthUserIdLastUpdated() );
			counter++;
			pstmt.setString( counter, item.getUrl() );
			counter++;
			pstmt.setString( counter, item.getQueryJSON() );

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
	 * UNUSED
	 * 
	 * 
	 * Update url = ?, query_json = ?, auth_user_id = ?
	 * @param id
	 * @param url
	 * @throws Exception
	 */
//	public void updateUrlQueryJSONAuthUserId( DefaultPageViewGenericDTO item ) throws Exception {
//		
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//
//		final String sql = "UPDATE default_page_view SET url = ?, query_json = ?, auth_user_id = ? WHERE project_search_id = ? AND page_name = ?";
//
//		
//		try {
//			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//
//
//
//
//			
//			pstmt = conn.prepareStatement( sql );
//			
//			int counter = 0;
//
//			counter++;
//			pstmt.setString( counter, item.getUrl() );
//			
//			counter++;
//			pstmt.setString( counter, item.getQueryJSON() );
//			
//			counter++;
//			pstmt.setInt( counter, item.getAuthUserId() );
//
//			
//			counter++;
//			pstmt.setInt( counter, item.getSearchId() );
//
//			counter++;
//			pstmt.setString( counter, item.getPageName() );
//			
//			
//			pstmt.executeUpdate();
//			
//		} catch ( Exception e ) {
//			
//			String msg = "Failed to update url = ?, auth_user_id = ?, sql: " + sql;
//			
//			log.error( msg, e );
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//	}
	



}
