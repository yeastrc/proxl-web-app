package org.yeastrc.xlink.www.file_import_proxl_xml_scans.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.populate_dto_from_result.ProxlXMLFileImportTracking_PopulateDTO;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.objects.ProxlXMLFileTrackingIdStatusId;

/**
 * 
 *
 */
public class ProxlXMLFileImportTracking_All_Searcher {

	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTracking_All_Searcher.class);
	//  private constructor
	private ProxlXMLFileImportTracking_All_Searcher() { }
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTracking_All_Searcher getInstance() { 
		return new ProxlXMLFileImportTracking_All_Searcher(); 
	}
	private static final String SELECT_WHERE_CLAUSE = 
			 "  status_id IN ( " 
			+ ProxlXMLFileImportStatus.QUEUED.value()
			+ ", "
			+ ProxlXMLFileImportStatus.RE_QUEUED.value()
			+ ", "
			+ ProxlXMLFileImportStatus.STARTED.value()
			+ ", "
			+ ProxlXMLFileImportStatus.FAILED.value()
			+ ", "
			+ ProxlXMLFileImportStatus.COMPLETE.value()
			+ " ) "
			+ " AND "
			+ " marked_for_deletion != " + Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE
			+ " AND project_id = ? ORDER BY priority, id DESC"
			;
	/**
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<ProxlXMLFileImportTrackingDTO> getAllForWebDisplayForProject( int projectId ) throws Exception {
		List<ProxlXMLFileImportTrackingDTO> resultList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql =  "SELECT * FROM proxl_xml_file_import_tracking"
				+ " WHERE "
				+ SELECT_WHERE_CLAUSE;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, projectId );
			rs = pstmt.executeQuery();
			ProxlXMLFileImportTracking_PopulateDTO proxlXMLFileImportTracking_PopulateDTO =
					ProxlXMLFileImportTracking_PopulateDTO.getInstance();
			while( rs.next() ) {
				ProxlXMLFileImportTrackingDTO returnItem = proxlXMLFileImportTracking_PopulateDTO.populateResultObject( rs );
				resultList.add(returnItem);
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return resultList;
	}
	
	/**
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<ProxlXMLFileTrackingIdStatusId> getAllStatusExceptInitInsertForProject( int projectId ) throws Exception {
		List<ProxlXMLFileTrackingIdStatusId> resultList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql =  "SELECT id, status_id FROM proxl_xml_file_import_tracking"
				+ " WHERE "
				+ SELECT_WHERE_CLAUSE;
				;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, projectId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				ProxlXMLFileTrackingIdStatusId item = new ProxlXMLFileTrackingIdStatusId();
				item.setTrackingId( rs.getInt( "id" ) );
				item.setStatusId( rs.getInt( "status_id" ) );
				resultList.add(item);
			}
		} catch ( Exception e ) {
			String msg = "Failed to select ProxlXMLFIleTrackingIdStatusId, sql: " + sql;
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
