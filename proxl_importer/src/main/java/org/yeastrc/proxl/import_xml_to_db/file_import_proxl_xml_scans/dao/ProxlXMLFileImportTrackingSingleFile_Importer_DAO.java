package org.yeastrc.proxl.import_xml_to_db.file_import_proxl_xml_scans.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 * table proxl_xml_file_import_tracking_single_file
 */
public class ProxlXMLFileImportTrackingSingleFile_Importer_DAO {
	
	private static final Logger log = LoggerFactory.getLogger( ProxlXMLFileImportTrackingSingleFile_Importer_DAO.class);
	//  private constructor
	private ProxlXMLFileImportTrackingSingleFile_Importer_DAO() { }
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTrackingSingleFile_Importer_DAO getInstance() { 
		return new ProxlXMLFileImportTrackingSingleFile_Importer_DAO(); 
	}
	
	/**
	 * @param fileSize
	 * @param sha1Sum
	 * @param id
	 * @throws Exception
	 */
	public void updateFileSizeSHA1Sum( long fileSize, String sha1Sum, int id ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			updateFileSizeSHA1Sum( fileSize, sha1Sum, id, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * @param fileSize
	 * @param sha1Sum
	 * @param id
	 * @param dbConnection
	 * @throws Exception
	 */
	public void updateFileSizeSHA1Sum( long fileSize, String sha1Sum, int id, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE proxl_xml_file_import_tracking_single_file SET file_size = ?, sha1_sum = ? WHERE id = ?";
		try {
			pstmt = dbConnection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setLong( counter, fileSize );
			counter++;
			pstmt.setString( counter, sha1Sum );
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update fileSize: " + fileSize 
					+ ", sha1Sum: " + sha1Sum
					+ ", sql: " + sql;
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
