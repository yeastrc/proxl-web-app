package org.yeastrc.xlink.base.proxl_xml_file_import.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.Statement;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;

public class ProxlXMLFileImportTrackingHistoryDAO {

	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTrackingHistoryDAO.class);
	//  private constructor
	private ProxlXMLFileImportTrackingHistoryDAO() { }
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTrackingHistoryDAO getInstance() { 
		return new ProxlXMLFileImportTrackingHistoryDAO(); 
	}
	
	/**
	 * @param status
	 * @param fastaImportTrackingId
	 * @throws Exception
	 */
	public void save( ProxlXMLFileImportStatus status, int fastaImportTrackingId  ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			save( status, fastaImportTrackingId, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	private static final String SAVE_SQL = "INSERT INTO proxl_xml_file_import_tracking_status_history "
			+ "( proxl_xml_file_import_tracking_id, status_id )"
			+ " VALUES ( ?, ? )";

	/**
	 * @param status
	 * @param fastaImportTrackingId
	 * @param dbConnection
	 * @throws Exception
	 */
	public void save( ProxlXMLFileImportStatus status, int fastaImportTrackingId, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SAVE_SQL;
		try {
//			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt = dbConnection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, fastaImportTrackingId );
			counter++;
			pstmt.setInt( counter, status.value() );
			pstmt.executeUpdate();
//			rs = pstmt.getGeneratedKeys();
//			if( rs.next() ) {
//				int id = rs.getInt( 1 );
//			} else {
//				String msg = "Failed to insert fasta_import_tracking_status_history, generated key not found.";
//				log.error( msg );
//				throw new Exception( msg );
//			}
		} catch ( Exception e ) {
			String msg = "Failed to insert proxl_xml_file_import_tracking_status_history, sql: " + sql;
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
