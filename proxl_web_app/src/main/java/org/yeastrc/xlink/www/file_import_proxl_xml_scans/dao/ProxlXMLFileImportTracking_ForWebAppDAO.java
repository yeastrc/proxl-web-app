package org.yeastrc.xlink.www.file_import_proxl_xml_scans.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dao.ProxlXMLFileImportTrackingHistoryDAO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus;

/**
 * 
 * table proxl_xml_file_import_tracking
 */
public class ProxlXMLFileImportTracking_ForWebAppDAO {

	private static final Logger log = LoggerFactory.getLogger( ProxlXMLFileImportTracking_ForWebAppDAO.class);
	

	//  private constructor
	private ProxlXMLFileImportTracking_ForWebAppDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTracking_ForWebAppDAO getInstance() { 
		return new ProxlXMLFileImportTracking_ForWebAppDAO(); 
	}
	
	



	/**
	 * @param id
	 * @return 
	 * @throws Exception
	 */
	public ProxlXMLFileImportStatus getStatusForId( int id ) throws Exception {


		ProxlXMLFileImportStatus result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT status_id FROM proxl_xml_file_import_tracking WHERE id = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				
				result = ProxlXMLFileImportStatus.fromValue( rs.getInt( "status_id" ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select status, id: " + id + ", sql: " + sql;
			
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
		
		return result;
	}
	
	




	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ProxlXMLFileImportTrackingDTO item ) throws Exception {
		
		
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

	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ProxlXMLFileImportTrackingDTO item, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		//  Insert field "id" since not autoincrement

		final String sql = "INSERT INTO proxl_xml_file_import_tracking ( "
				+ " id, project_id, priority, status_id, marked_for_deletion, insert_request_url, "
				+ " search_name, search_path, auth_user_id,  "
				+ " remote_user_ip_address, last_updated_date_time )"
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW() )";

		try {
			
			
//			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getId() );
			counter++;
			pstmt.setInt( counter, item.getProjectId() );
			counter++;
			pstmt.setInt( counter, item.getPriority() );

			counter++;
			pstmt.setInt( counter, item.getStatus().value() );
			

			counter++;
			
			if ( item.isMarkedForDeletion() ) {

				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			
			counter++;
			pstmt.setString( counter, item.getInsertRequestURL() );
			counter++;
			pstmt.setString( counter, item.getSearchName() );
			counter++;
			pstmt.setString( counter, item.getSearchPath() );
			
			counter++;
			pstmt.setInt( counter, item.getAuthUserId() );
			
			counter++;
			pstmt.setString( counter, item.getRemoteUserIpAddress() );
			
			pstmt.executeUpdate();
			
//			rs = pstmt.getGeneratedKeys();
//
//			if( rs.next() ) {
//				item.setId( rs.getInt( 1 ) );
//			} else {
//				
//				String msg = "Failed to insert ProxlXMLFileImportTrackingDTO, generated key not found.";
//				
//				log.error( msg );
//				
//				throw new Exception( msg );
//			}
			
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert ProxlXMLFileImportTrackingDTO: " + item + ", sql: " + sql;
			
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
		

		ProxlXMLFileImportTrackingHistoryDAO.getInstance().save( item.getStatus(), item.getId() /* ProxlXMLFileImportTrackingId */, dbConnection );		
		
	}

	

	/**
	 * @param markedForDeletion
	 * @param status
	 * @param id
	 * @return true if record updated, false otherwise
	 * @throws Exception
	 */
	public boolean updateMarkedForDeletionForIdStatus( 
			
			boolean markedForDeletion, ProxlXMLFileImportStatus status, int id,
			Integer deletedByAuthUserId ) throws Exception {


		if ( markedForDeletion ) {
			
			if ( deletedByAuthUserId == null ) {
				
				throw new IllegalArgumentException( "deletedByAuthUserId == null invalid when markedForDeletion is true" );
			}
			
		} else {
			
			if ( deletedByAuthUserId != null ) {
				
				throw new IllegalArgumentException( "deletedByAuthUserId != null invalid when markedForDeletion is false" );
			}
			
		}

		boolean recordUpdated = false;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = null;
		
		
		
		if ( markedForDeletion ) {

			sql = "UPDATE proxl_xml_file_import_tracking "
					+ " SET marked_for_deletion = " + Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE
					+ " , last_updated_date_time = NOW(),"
					+ " deleted_by_auth_user_id = ?, deleted_date_time = NOW() "
					+ " WHERE id = ? AND status_id = ?";
		} else {
			
			sql = "UPDATE proxl_xml_file_import_tracking "
					+ " SET marked_for_deletion = " + Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE
					+ " , last_updated_date_time = NOW(),"
					+ " deleted_by_auth_user_id = NULL, deleted_date_time = NULL "
					+ " WHERE id = ? AND status_id = ?";
		}
		

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );

			int counter = 0;
			

			
			if ( markedForDeletion ) {

				counter++;
				pstmt.setInt( counter, deletedByAuthUserId );
			}

			counter++;
			pstmt.setInt( counter, id );
			counter++;
			pstmt.setInt( counter, status.value() );
			
			
			int rowsUpdated = pstmt.executeUpdate();
			
			if ( rowsUpdated > 0 ) {
				
				recordUpdated = true;
			}
			
		} catch ( Exception e ) {
			
			String msg = "updateMarkedForDeletionForIdStatus(...)  id: " + id + ", sql: " + sql;
			
			log.error( msg, e );
			
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
		
		return recordUpdated;
	}
	

}
