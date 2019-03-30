package org.yeastrc.xlink.www.file_import_proxl_xml_scans.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.populate_dto_from_result.ProxlXMLFileImportTrackingRun_PopulateDTO;

/**
 * 
 *
 */
public class ProxlXMLFileImportTrackingRun_LatestForParent_Searcher {

	private static final Logger log = LoggerFactory.getLogger( ProxlXMLFileImportTrackingRun_LatestForParent_Searcher.class);
	//  private constructor
	private ProxlXMLFileImportTrackingRun_LatestForParent_Searcher() { }
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTrackingRun_LatestForParent_Searcher getInstance() { 
		return new ProxlXMLFileImportTrackingRun_LatestForParent_Searcher(); 
	}
	
	/**
	 * @param proxlXMLFileImportTrackingId
	 * @return
	 * @throws Exception
	 */
	public ProxlXMLFileImportTrackingRunDTO getLatestRunForProxlXMLFileImportTrackingDTO( int proxlXMLFileImportTrackingId ) throws Exception {
		ProxlXMLFileImportTrackingRunDTO result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql =  "SELECT * FROM proxl_xml_file_import_tracking_run"
				+ " WHERE id = "
				+ 		"( SELECT MAX(id) from proxl_xml_file_import_tracking_run "
				+ 		" WHERE proxl_xml_file_import_tracking_id = ? )";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, proxlXMLFileImportTrackingId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = ProxlXMLFileImportTrackingRun_PopulateDTO.getInstance().populateResultObject( rs );
			}
		} catch ( Exception e ) {
			String msg = "Failed getLatestRunForProxlXMLFileImportTrackingDTO(...), sql: " + sql;
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
}
