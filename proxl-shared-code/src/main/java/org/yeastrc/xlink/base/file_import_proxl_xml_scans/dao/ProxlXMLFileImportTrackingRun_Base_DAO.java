package org.yeastrc.xlink.base.file_import_proxl_xml_scans.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.populate_dto_from_result.ProxlXMLFileImportTrackingRun_PopulateDTO;
import org.yeastrc.xlink.db.DBConnectionFactory;
/**
 * 
 * table proxl_xml_file_import_tracking_run
 */
public class ProxlXMLFileImportTrackingRun_Base_DAO {
	
	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTrackingRun_Base_DAO.class);
	//  private constructor
	private ProxlXMLFileImportTrackingRun_Base_DAO() { }
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTrackingRun_Base_DAO getInstance() { 
		return new ProxlXMLFileImportTrackingRun_Base_DAO(); 
	}
	
	/**
	 * Get the given proxl_xml_file_import_tracking_run from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProxlXMLFileImportTrackingRunDTO getItem( int id ) throws Exception {
		ProxlXMLFileImportTrackingRunDTO item = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM proxl_xml_file_import_tracking_run WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				item = ProxlXMLFileImportTrackingRun_PopulateDTO.getInstance().populateResultObject( rs );
			}
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
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
		return item;
	}
	
	/**
	 * @param status
	 * @param id
	 * @throws Exception
	 */
	public void updateStatus( ProxlXMLFileImportStatus status, int id ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			updateStatus( status, id, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	/**
	 * @param status
	 * @param id
	 * @throws Exception
	 */
	public void updateStatus( ProxlXMLFileImportStatus status, int id, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE proxl_xml_file_import_tracking_run SET status = ?, last_updated_date_time = NOW() WHERE id = ?";
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
	}
}
