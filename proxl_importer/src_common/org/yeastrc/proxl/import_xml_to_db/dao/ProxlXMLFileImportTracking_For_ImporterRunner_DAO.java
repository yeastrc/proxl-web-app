package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dao.ProxlXMLFileImportTrackingHistoryDAO;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.populate_dto_from_result.ProxlXMLFileImportTracking_PopulateDTO;

/**
 * 
 * table proxl_xml_file_import_tracking
 */
public class ProxlXMLFileImportTracking_For_ImporterRunner_DAO {


	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTracking_For_ImporterRunner_DAO.class);
	

	//  private constructor
	private ProxlXMLFileImportTracking_For_ImporterRunner_DAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTracking_For_ImporterRunner_DAO getInstance() { 
		return new ProxlXMLFileImportTracking_For_ImporterRunner_DAO(); 
	}
	
	
	private static final String GET_NEXT_QUEUED_SQL = 
			
			"SELECT * FROM proxl_xml_file_import_tracking "
			+ " WHERE status_id IN ( " 
			+ 		ProxlXMLFileImportStatus.QUEUED.value()
			+	  "," + ProxlXMLFileImportStatus.RE_QUEUED.value()
			+ 	") AND marked_for_deletion != " + Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE
			+   " AND priority <= ? "
			+ " ORDER BY priority, ID LIMIT 1 FOR UPDATE";

	/**
	 * Get next import tracking item that is queued or re-queued
	 * @return
	 * @throws Exception
	 */
	public ProxlXMLFileImportTrackingDTO getNextQueued( int maxPriority, Connection dbConnection ) throws Exception {


		ProxlXMLFileImportTrackingDTO result = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = GET_NEXT_QUEUED_SQL;
		
		
		try {
			
			pstmt = dbConnection.prepareStatement( sql );
			
			pstmt.setInt( 1, maxPriority );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				
				result = ProxlXMLFileImportTracking_PopulateDTO.getInstance().populateResultObject( rs );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select ProxlXMLFileImportTrackingDTO, sql: " + sql;
			
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
		
		return result;
	}
	

	/**
	 * @param id
	 * @throws Exception
	 */
	public void updateStatusStarted( int id, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE proxl_xml_file_import_tracking SET status_id = "
				+ ProxlXMLFileImportStatus.STARTED.value() 
				+ ", import_start_date_time = NOW(), last_updated_date_time = NOW() WHERE id = ?";

		
		try {
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update status, sql: " + sql;
			
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
		
		ProxlXMLFileImportTrackingHistoryDAO.getInstance().save( ProxlXMLFileImportStatus.STARTED, id /* ProxlXMLFileImportTrackingId */, dbConnection );		
	}
	
	


	/**
	 * @param status
	 * @param id
	 * @throws Exception
	 */
//	public void updateStatusAtImportEnd( ProxlXMLFileImportStatus status, int id ) throws Exception {
//		
//		
//		Connection dbConnection = null;
//
//		try {
//			
//			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//
//			updateStatusAtImportEnd( status, id, dbConnection );
//
//		} finally {
//			
//			if( dbConnection != null ) {
//				try { dbConnection.close(); } catch( Throwable t ) { ; }
//				dbConnection = null;
//			}
//			
//		}
//		
//	}
//	
	
	/**
	 * @param status
	 * @param id
	 * @throws Exception
	 */
	public void updateStatusAtImportEnd( ProxlXMLFileImportStatus status, int id, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE proxl_xml_file_import_tracking SET status_id = ?"
				+ ", import_end_date_time = NOW(), last_updated_date_time = NOW() WHERE id = ?";

		
		try {
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;

			counter++;
			pstmt.setInt( counter, status.value() );
			
			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update status, sql: " + sql;
			
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
		
		ProxlXMLFileImportTrackingHistoryDAO.getInstance().save( ProxlXMLFileImportStatus.STARTED, id /* ProxlXMLFileImportTrackingId */, dbConnection );		
	}
	


}
