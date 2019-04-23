package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.DataPageSavedViewDTO;
/**
 * DAO for data_page_saved_view_tbl table
 *
 */
public class DataPageSavedViewDAO {
	
	private static final Logger log = LoggerFactory.getLogger( DataPageSavedViewDAO.class);
	//  private constructor
	private DataPageSavedViewDAO() { }
	/**
	 * @return newly created instance
	 */
	public static DataPageSavedViewDAO getInstance() { 
		return new DataPageSavedViewDAO(); 
	}
	
	/**
	 * @param projectSearchId
	 * @param pageName
	 * @return null if not found
	 * @throws Exception
	 */
	public DataPageSavedViewDTO getForProjectSearchIdPageName( int projectSearchId, String pageName ) throws Exception {
		DataPageSavedViewDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM data_page_saved_view_tbl WHERE single_project_search_id__default_view = ? AND page_name = ?";
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
			String msg = "Failed to select DataPageSavedViewDTO, projectSearchId: " + projectSearchId + ", sql: " + sql;
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
//	/**
//	 * @param projectSearchId
//	 * @return 
//	 * @throws Exception
//	 */
//	public List<DataPageSavedViewDTO> getForProjectSearchId( int projectSearchId ) throws Exception {
//		 List<DataPageSavedViewDTO> results = new ArrayList<>();
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		final String sql = "SELECT * FROM project WHERE project_search_id = ? ";
//		try {
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//			pstmt = conn.prepareStatement( sql );
//			pstmt.setInt( 1, projectSearchId );
//			rs = pstmt.executeQuery();
//			while( rs.next() ) {
//				DataPageSavedViewDTO item = populateResultObject( rs );
//				results.add( item );
//			}
//		} catch ( Exception e ) {
//			String msg = "Failed to select DataPageSavedViewDTO, projectSearchId: " + projectSearchId + ", sql: " + sql;
//			log.error( msg, e );
//			throw e;
//		} finally {
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//		}
//		return results;
//	}
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private DataPageSavedViewDTO populateResultObject(ResultSet rs) throws SQLException {
		DataPageSavedViewDTO returnItem = new DataPageSavedViewDTO();
		returnItem.setProjectId( rs.getInt( "project_id" ) );
		returnItem.setPageName( rs.getString( "page_name" ) );
		returnItem.setLabel( rs.getString( "label" ) );
		returnItem.setUrlStartAtPageName( rs.getString( "url_start_at_page_name" ) );
		returnItem.setPageQueryJSONString( rs.getString( "page_query_json_string" ) );
		returnItem.setAuthUserIdCreated( rs.getInt( "auth_user_id_created_record" ) );
		returnItem.setAuthUserIdLastUpdated( rs.getInt( "auth_user_id_last_updated_record" ) );
		returnItem.setDateCreated( rs.getDate( "date_record_created" ) );
		returnItem.setDateLastUpdated( rs.getDate( "date_record_last_updated" ) );
		return returnItem;
	}

	/**
	 * Return the numeric fields for id
	 * 
	 * @param id
	 * @return null if not found, only the numeric fields
	 * @throws SQLException
	 */
	public DataPageSavedViewDTO getNumericFieldsById( int id ) throws Exception {
		
		DataPageSavedViewDTO result = null;
		
		final String querySQL = "SELECT project_id, auth_user_id_created_record, auth_user_id_last_updated_record FROM data_page_saved_view_tbl WHERE id = ? ";
		
		try ( Connection dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			     PreparedStatement preparedStatement = dbConnection.prepareStatement( querySQL ) ) {
			
			preparedStatement.setInt( 1, id );
			
			try ( ResultSet rs = preparedStatement.executeQuery() ) {
				if ( rs.next() ) {
					result = new DataPageSavedViewDTO();
					result.setId( id );
					result.setProjectId( rs.getInt( "project_id" ) );
					result.setAuthUserIdCreated( rs.getInt( "auth_user_id_created_record" ) );
					result.setAuthUserIdLastUpdated( rs.getInt( "auth_user_id_last_updated_record" ) );
				}
			}
		} catch ( RuntimeException e ) {
			String msg = "SQL: " + querySQL;
			log.error( msg, e );
			throw e;
		} catch ( SQLException e ) {
			String msg = "SQL: " + querySQL;
			log.error( msg, e );
			throw e;
		}
		
		return result;
	}
	
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( DataPageSavedViewDTO item ) throws Exception {
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

	///////
	
	private static final String INSERT_SQL = 
			"INSERT INTO data_page_saved_view_tbl "
			+ " ( project_id, page_name, "
			+ " label, url_start_at_page_name, page_query_json_string,"
			+ " auth_user_id_created_record, auth_user_id_last_updated_record ) "
			+ " VALUES ( ?, ?, ?, ?, ?, ?, ? )";

	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( DataPageSavedViewDTO item, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = INSERT_SQL;
		try {
//			pstmt = dbConnection.prepareStatement( sql );
			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getProjectId() );
			counter++;
			pstmt.setString( counter, item.getPageName() );
			counter++;
			pstmt.setString( counter, item.getLabel() );
			counter++;
			pstmt.setString( counter, item.getUrlStartAtPageName() );
			counter++;
			pstmt.setString( counter, item.getPageQueryJSONString() );
			counter++;
			pstmt.setInt( counter, item.getAuthUserIdCreated() );
			counter++;
			pstmt.setInt( counter, item.getAuthUserIdLastUpdated() );

			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				String msg = "Failed to insert DataPageSavedViewDTO, generated key not found.";
				log.error( msg );
				throw new Exception( msg );
			}
		} catch ( Exception e ) {
			String msg = "Failed to insert DataPageSavedViewDTO, sql: " + sql;
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
		}
	}

	/**
	 * @param label
	 * @param id
	 */
	public void updateLabel( String label, int userId, int id ) throws Exception {
		
		final String UPDATE_SQL = "UPDATE data_page_saved_view_tbl SET label = ?, auth_user_id_last_updated_record = ? WHERE id = ?";

		try ( Connection dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
				PreparedStatement preparedStatement = dbConnection.prepareStatement( UPDATE_SQL ) ) {

			int counter = 0;
			counter++;
			preparedStatement.setString( counter, label );
			counter++;
			preparedStatement.setInt( counter, userId );
			counter++;
			preparedStatement.setInt( counter, id );
			preparedStatement.executeUpdate();
			
		} catch ( Exception e ) {
			String msg = "label: " + label + ", id: " + id + ", SQL: " + UPDATE_SQL;
			log.error( msg, e );
			throw e;
		}
	}

	/**
	 * @param id
	 */
	public void delete( int id ) throws Exception {

		final String DELETE_SQL = "DELETE FROM data_page_saved_view_tbl WHERE id = ?";
		
		try ( Connection dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
				PreparedStatement preparedStatement = dbConnection.prepareStatement( DELETE_SQL ) ) {

			int counter = 0;
			counter++;
			preparedStatement.setInt( counter, id );
			preparedStatement.executeUpdate();
			
		} catch ( Exception e ) {
			String msg = "id: " + id + ", SQL: " + DELETE_SQL;
			log.error( msg, e );
			throw e;
		}
	}
}
