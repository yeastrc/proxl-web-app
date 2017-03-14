package org.yeastrc.xlink.base.file_import_proxl_xml_scans.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.populate_dto_from_result.ProxlXMLFileImportTracking_PopulateDTO;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 * table file_import_proxl_xml_scans_tracking
 */
public class ProxlXMLFileImportTracking_Base_DAO {

	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTracking_Base_DAO.class);

	//  private constructor
	private ProxlXMLFileImportTracking_Base_DAO() { }
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTracking_Base_DAO getInstance() { 
		return new ProxlXMLFileImportTracking_Base_DAO(); 
	}

	/**
	 * Get the given file_import_proxl_xml_scans_tracking from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProxlXMLFileImportTrackingDTO getItem( int id ) throws Exception {
		ProxlXMLFileImportTrackingDTO item = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM file_import_proxl_xml_scans_tracking WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				item = ProxlXMLFileImportTracking_PopulateDTO.getInstance().populateResultObject( rs );
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
	

		
}
