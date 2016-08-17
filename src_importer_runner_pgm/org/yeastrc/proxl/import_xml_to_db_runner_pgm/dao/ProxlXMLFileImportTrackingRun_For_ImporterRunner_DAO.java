package org.yeastrc.proxl.import_xml_to_db_runner_pgm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingRunDTO;

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
	public void save( ProxlXMLFileImportTrackingRunDTO item ) throws Exception {
		
		
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
	public void save( ProxlXMLFileImportTrackingRunDTO item, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		


//CREATE TABLE IF NOT EXISTS proxl_xml_file_import_tracking_run (
//  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//  proxl_xml_file_import_tracking_id INT UNSIGNED NOT NULL,
//  status_id TINYINT UNSIGNED NOT NULL,
//  importer_sub_status_id TINYINT NULL,
//  importer_percent_psms_processed TINYINT NULL,
//  inserted_search_id INT UNSIGNED NULL,
//  import_result_text MEDIUMTEXT NULL,
//  data_error_text MEDIUMTEXT NULL,
//  start_date_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
//  last_updated_date_time TIMESTAMP NULL,



		final String sql = "INSERT INTO proxl_xml_file_import_tracking_run ( "
				+ "proxl_xml_file_import_tracking_id, status_id, last_updated_date_time )"
				+ " VALUES ( ?, ?, NOW() )";

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


		

}
