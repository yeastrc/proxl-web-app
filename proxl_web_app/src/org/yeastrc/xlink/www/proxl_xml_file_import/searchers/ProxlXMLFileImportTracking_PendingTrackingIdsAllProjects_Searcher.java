package org.yeastrc.xlink.www.proxl_xml_file_import.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;



/**
 * 
 *
 */
public class ProxlXMLFileImportTracking_PendingTrackingIdsAllProjects_Searcher {


	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTracking_PendingTrackingIdsAllProjects_Searcher.class);
	

	//  private constructor
	private ProxlXMLFileImportTracking_PendingTrackingIdsAllProjects_Searcher() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTracking_PendingTrackingIdsAllProjects_Searcher getInstance() { 
		return new ProxlXMLFileImportTracking_PendingTrackingIdsAllProjects_Searcher(); 
	}

	
	public ArrayList<Integer> getPendingTrackingIdsAllProjects( ) throws Exception {
		
		ArrayList<Integer> resultList = new ArrayList<>();
		

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql =  "SELECT id FROM proxl_xml_file_import_tracking"
				+ " WHERE "
				 + "  status_id IN ( " 
					+ ProxlXMLFileImportStatus.QUEUED.value()
					+ ", "
					+ ProxlXMLFileImportStatus.RE_QUEUED.value()
					+ ")"
					+ " AND "
					+ " marked_for_deletion != " + Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE
					+ " ORDER BY priority, id ";



//		  deleted_by_auth_user_id INT UNSIGNED NULL,
//		  deleted_date_time DATETIME NULL,
	
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				Integer result = rs.getInt( "id" );
				
				resultList.add( result );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed getPendingTrackingIdsAllProjects( ), sql: " + sql;
			
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
		
		return resultList;
		
	}
	

}
