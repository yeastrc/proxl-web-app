package org.yeastrc.xlink.www.file_import_proxl_xml_scans.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingSingleFileDTO;

/**
 * 
 * table file_import_proxl_xml_scans_tracking_single_file
 */
public class ProxlXMLFileImportTrackingSingleFile_ForWebAppDAO {

	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTrackingSingleFile_ForWebAppDAO.class);
	

	//  private constructor
	private ProxlXMLFileImportTrackingSingleFile_ForWebAppDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTrackingSingleFile_ForWebAppDAO getInstance() { 
		return new ProxlXMLFileImportTrackingSingleFile_ForWebAppDAO(); 
	}
	
	


	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ProxlXMLFileImportTrackingSingleFileDTO item ) throws Exception {
		
		
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
	public void save( ProxlXMLFileImportTrackingSingleFileDTO item, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		final String sql = "INSERT INTO file_import_proxl_xml_scans_tracking_single_file "
				+ "( file_import_proxl_xml_scans_tracking_id, file_type_id, file_upload_status_id, "
				+ " filename_in_upload, filename_on_disk, filename_on_disk_with_path_sub_same_machine, "
				+ " file_size )"
				+ " VALUES ( ?, ?, ?, ?, ?, ?, ? )";


		try {
			
			
			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getProxlXmlFileImportTrackingId() );
			counter++;
			pstmt.setInt( counter, item.getFileType().value() );
			counter++;
			pstmt.setInt( counter, item.getFileUploadStatus().value() );

			counter++;
			pstmt.setString( counter, item.getFilenameInUpload() );
			counter++;
			pstmt.setString( counter, item.getFilenameOnDisk() );
			counter++;
			pstmt.setString( counter, item.getFilenameOnDiskWithPathSubSameMachine() );

			counter++;
			pstmt.setLong( counter, item.getFileSize() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				
				String msg = "Failed to insert ProxlXMLFileImportTrackingSingleFileDTO, generated key not found.";
				
				log.error( msg );
				
				throw new Exception( msg );
			}
			
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert ProxlXMLFileImportTrackingSingleFileDTO: " + item + ", sql: " + sql;
			
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
