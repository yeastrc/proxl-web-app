package org.yeastrc.xlink.www.file_import_proxl_xml_scans.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;



/**
 * generates the "id" for the next insert to fasta_import_tracking.
 *
 */
public class ProxlXMLFIleImportTrackingFileIdCreatorDAO {


	private static final Logger log = Logger.getLogger(ProxlXMLFIleImportTrackingFileIdCreatorDAO.class);

	
	//  private constructor
	private ProxlXMLFIleImportTrackingFileIdCreatorDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFIleImportTrackingFileIdCreatorDAO getInstance() { 
		return new ProxlXMLFIleImportTrackingFileIdCreatorDAO(); 
	}
	
	
	public int getNextId(  ) throws Exception {

		int nextId = -1;
		
		Connection dbConnection = null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		PreparedStatement pstmtDelete = null;

		final String sql = "INSERT INTO file_import_proxl_xml_scans_tracking_id_creator () VALUES ( )";


				
//		CREATE TABLE file_import_proxl_xml_scans_tracking_id_creator (
//				  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
				  
		try {

			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			try {
			
				pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
				
				pstmt.executeUpdate();
				
				rs = pstmt.getGeneratedKeys();
	
				if( rs.next() ) {
					nextId = rs.getInt( 1 );
					
				} else {
					
					String msg = "Failed to insert file_import_proxl_xml_scans_tracking_id_creator, generated key not found.";
					
					log.error( msg );
					
					throw new Exception( msg );
				}
			
				
			} catch ( Exception e ) {
				
				String msg = "Failed to insert file_import_proxl_xml_scans_tracking_id_creator, sql: " + sql;
				
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
			

			
			final String deleteSQL = "DELETE FROM file_import_proxl_xml_scans_tracking_id_creator WHERE id < ?";
			
			try {
				

				pstmtDelete = dbConnection.prepareStatement( deleteSQL );
				
				pstmtDelete.setInt( 1, nextId );
				
				pstmtDelete.executeUpdate();
				
			} catch ( Exception e ) {
				
				String msg = "Failed to delete from file_import_proxl_xml_scans_tracking_id_creator, sql: " + deleteSQL;
				
				log.error( msg, e );
				
				throw e;
				

			} finally {
				
				// be sure database handles are closed
				if( pstmtDelete != null ) {
					try { pstmtDelete.close(); } catch( Throwable t ) { ; }
					pstmtDelete = null;
				}
				
				
			}
			
		} finally {
			
			// be sure database handles are closed
			
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
		
		return nextId;
	}
	


}
