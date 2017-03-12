package org.yeastrc.xlink.www.proxl_xml_file_import.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;

/**
 * 
 *
 */
public class ProxlXMLFileImportTracking_PendingCount_Searcher {

	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTracking_PendingCount_Searcher.class);
	//  private constructor
	private ProxlXMLFileImportTracking_PendingCount_Searcher() { }
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTracking_PendingCount_Searcher getInstance() { 
		return new ProxlXMLFileImportTracking_PendingCount_Searcher(); 
	}
	private static final String SQL = 
			"SELECT COUNT(*) AS count FROM proxl_xml_file_import_tracking"
					+ " WHERE "
			+ "  marked_for_deletion != " + Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE
			+ " AND status_id IN ( " 
			+ ProxlXMLFileImportStatus.QUEUED.value()
			+ ", "
			+ ProxlXMLFileImportStatus.RE_QUEUED.value()
			+ ", "
			+ ProxlXMLFileImportStatus.STARTED.value()
			+ " ) "
			+ " AND project_id = ? "
			;
	/**
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public int getPendingCountForProject( int projectId ) throws Exception {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql =  SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, projectId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = rs.getInt( "count" );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select count, sql: " + sql;
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
