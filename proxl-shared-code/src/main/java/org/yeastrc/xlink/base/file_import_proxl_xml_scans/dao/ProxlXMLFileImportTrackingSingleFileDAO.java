package org.yeastrc.xlink.base.file_import_proxl_xml_scans.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingSingleFileDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportFileType;
import org.yeastrc.xlink.db.DBConnectionFactory;
/**
 * 
 * table proxl_xml_file_import_tracking_single_file
 */
public class ProxlXMLFileImportTrackingSingleFileDAO {

	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTrackingSingleFileDAO.class);
	//  private constructor
	private ProxlXMLFileImportTrackingSingleFileDAO() { }
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTrackingSingleFileDAO getInstance() { 
		return new ProxlXMLFileImportTrackingSingleFileDAO(); 
	}
	
	/**
	 * @param id
	 * @return 
	 * @throws Exception
	 */
	public ProxlXMLFileImportTrackingSingleFileDTO getForId( int id ) throws Exception {
		ProxlXMLFileImportTrackingSingleFileDTO result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM proxl_xml_file_import_tracking_single_file WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if ( rs.next() ) {
				result = populateResultObject( rs );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select ProxlXMLFileImportTrackingSingleFileDTO, id: " + id + ", sql: " + sql;
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
	
	/**
	 * @param trackingId
	 * @return 
	 * @throws Exception
	 */
	public List<ProxlXMLFileImportTrackingSingleFileDTO> getForTrackingId( int trackingId ) throws Exception {
		List<ProxlXMLFileImportTrackingSingleFileDTO> resultList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM proxl_xml_file_import_tracking_single_file WHERE proxl_xml_file_import_tracking_id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, trackingId );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {
				ProxlXMLFileImportTrackingSingleFileDTO result = populateResultObject( rs );
				resultList.add(result);
			}
		} catch ( Exception e ) {
			String msg = "Failed to select ProxlXMLFileImportTrackingSingleFileDTO, id: " + trackingId + ", sql: " + sql;
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
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public ProxlXMLFileImportTrackingSingleFileDTO populateResultObject( ResultSet rs ) throws SQLException {
		ProxlXMLFileImportTrackingSingleFileDTO returnItem = new ProxlXMLFileImportTrackingSingleFileDTO();
		returnItem.setId( rs.getInt( "id" ) );
		returnItem.setProxlXmlFileImportTrackingId( rs.getInt( "proxl_xml_file_import_tracking_id" ) );
		returnItem.setFileType( ProxlXMLFileImportFileType.fromValue( rs.getInt( "file_type_id" ) ) );
		returnItem.setFilenameInUpload( rs.getString( "filename_in_upload" ) );
		returnItem.setFilenameOnDisk( rs.getString( "filename_on_disk" ) );
		returnItem.setFilenameOnDiskWithPathSubSameMachine( rs.getString( "filename_on_disk_with_path_sub_same_machine" ) );
		returnItem.setCanonicalFilename_W_Path_OnSubmitMachine( rs.getString( "canonical_filename_w_path_on_submit_machine" ) );
		returnItem.setAbsoluteFilename_W_Path_OnSubmitMachine( rs.getString( "absolute_filename_w_path_on_submit_machine" ) );
		
		return returnItem;
	}
}
