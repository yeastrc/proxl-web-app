package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingRunDTO;

/**
 * 
 * table proxl_xml_file_import_tracking_run
 */
public class ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO {


	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO.class);
	

	//  private constructor
	private ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO getInstance() { 
		return new ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO(); 
	}
	


	/**
	 * @param item
	 * @throws Exception
	 */
//	public void save( ProxlXMLFileImportTrackingRunDTO item ) throws Exception {
//		
//		
//		Connection dbConnection = null;
//
//		try {
//			
//			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//
//			save( item, dbConnection );
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

	
	private static final String SAVE_SQL = "INSERT INTO proxl_xml_file_import_tracking_run ( "
			+ "proxl_xml_file_import_tracking_id, status_id,"
			+ " current_run, "
			+ " last_updated_date_time )"
			+ " VALUES ( ?, ?, "
			+  /* current_run */	Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE
			+  /* last_updated_date_time */ " , NOW() )";
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ProxlXMLFileImportTrackingRunDTO item, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		final String sql = SAVE_SQL;

		try {
			
			
			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
//			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getProxlXmlFileImportTrackingId() );
			counter++;
			pstmt.setInt( counter, item.getRunStatus().value() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				
				String msg = "Failed to insert ProxlXMLFileImportTrackingRunDTO, generated key not found.";
				
				log.error( msg );
				
				throw new Exception( msg );
			}
			
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert ProxlXMLFileImportTrackingRunDTO: " + item + ", sql: " + sql;
			
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
	 * @param trackingId
	 * @throws Exception
	 */
	public void updateClearCurrentRunForTrackingId( int trackingId, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE proxl_xml_file_import_tracking_run "
				+ "SET current_run = " + Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE
				+ " WHERE proxl_xml_file_import_tracking_id = ?";

		
		try {
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, trackingId );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed updateClearCurrentRunForTrackingId(id), trackingId: " + trackingId + ", sql: " + sql;
			
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
	 * @param status
	 * @param id
	 * @throws Exception
	 */
//	public void updateStatusResultTexts( ProxlXMLFileImportTrackingRunDTO item ) throws Exception {
//		
//		
//		Connection dbConnection = null;
//
//		try {
//			
//			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//
//			updateStatusResultTexts( item, dbConnection );
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
	
	
	/**
	 * @param status
	 * @param id
	 * @throws Exception
	 */
	public void updateStatusResultTexts( ProxlXMLFileImportTrackingRunDTO item, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE proxl_xml_file_import_tracking_run "
				+ "SET status_id = ?, importer_sub_status_id = ?, import_result_text = ?, data_error_text = ?, "
				+ " last_updated_date_time = NOW() WHERE id = ?";

		
		try {
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getRunStatus().value() );

			counter++;
			
			if ( item.getRunSubStatus() != null ) {

				pstmt.setInt( counter, item.getRunSubStatus().value() );
				
			} else {
				
				pstmt.setNull(counter, java.sql.Types.INTEGER );
			}
						
			counter++;
			pstmt.setString( counter, item.getImportResultText() );
			
			counter++;
			pstmt.setString( counter, item.getDataErrorText() );
			
			counter++;
			pstmt.setInt( counter, item.getId() );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed updateStatusResultTexts(item), item: " + item + ", sql: " + sql;
			
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
	 * @param item
	 * @throws Exception
	 */
	public void updateInsertedSearchId( ProxlXMLFileImportTrackingRunDTO item ) throws Exception {
		
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			updateInsertedSearchId( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
	
	

	/**
	 * @param item
	 * @param dbConnection
	 * @throws Exception
	 */
	public void updateInsertedSearchId( ProxlXMLFileImportTrackingRunDTO item, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE proxl_xml_file_import_tracking_run "
				+ "SET inserted_search_id  = ? WHERE id = ?";

		
		try {
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getInsertedSearchId() );
			
			counter++;
			pstmt.setInt( counter, item.getId() );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update inserted_search_id, sql: " + sql;
			
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
	

}
