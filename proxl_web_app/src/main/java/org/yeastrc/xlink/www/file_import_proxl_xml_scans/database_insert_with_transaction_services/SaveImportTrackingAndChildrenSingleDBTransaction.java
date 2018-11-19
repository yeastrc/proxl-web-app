package org.yeastrc.xlink.www.file_import_proxl_xml_scans.database_insert_with_transaction_services;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingSingleFileDTO;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.dao.ProxlXMLFileImportTrackingSingleFile_ForWebAppDAO;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.dao.ProxlXMLFileImportTracking_ForWebAppDAO;

/**
 * 
 *
 */
public class SaveImportTrackingAndChildrenSingleDBTransaction {

	private static final Logger log = Logger.getLogger(SaveImportTrackingAndChildrenSingleDBTransaction.class);
	

	//  private constructor
	private SaveImportTrackingAndChildrenSingleDBTransaction() { }
	
	/**
	 * @return newly created instance
	 */
	public static SaveImportTrackingAndChildrenSingleDBTransaction getInstance() { 
		return new SaveImportTrackingAndChildrenSingleDBTransaction(); 
	}
	
	
	/**
	 * @param trackingItem
	 * @param singleFileDTOList
	 * @throws Exception 
	 */
	public void saveImportTrackingAndChildrenInSingleDBTransaction( 
			
			ProxlXMLFileImportTrackingDTO trackingItem,
			List<ProxlXMLFileImportTrackingSingleFileDTO> singleFileDTOList
			) throws Exception {
		

		Connection dbConnection = null;

		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			dbConnection.setAutoCommit(false);
			
			ProxlXMLFileImportTracking_ForWebAppDAO.getInstance().save(trackingItem, dbConnection);
		 	
			for ( ProxlXMLFileImportTrackingSingleFileDTO singleFileItem : singleFileDTOList ) {
				
				singleFileItem.setProxlXmlFileImportTrackingId( trackingItem.getId() );
				
				ProxlXMLFileImportTrackingSingleFile_ForWebAppDAO.getInstance()
				.save( singleFileItem, dbConnection );
			}
			
			
			dbConnection.commit();
			
		} catch ( Exception e ) {
			
			String msg = "Failed saveImportTrackingAndChildrenInSingleDBTransaction(...)";
			
			log.error( msg , e);

			if ( dbConnection != null ) {
				
				try {
					dbConnection.rollback();
					
				} catch (Exception ex) {
					
					String msgRollback = "Rollback Exception:  saveImportTrackingAndChildrenInSingleDBTransaction(...) Exception:  See Syserr or Sysout for original exception: Rollback Exception, tables 'scan' is in an inconsistent state. '" + ex.toString();
					
					log.error( msgRollback, ex );

					throw new Exception( msgRollback, ex );
				}
			}
			
			throw e;
			
		} finally {

			if( dbConnection != null ) {

				try {
					dbConnection.setAutoCommit(true);  /// reset for next user of connection
				} catch (Exception ex) {
					String msg = "Failed dbConnection.setAutoCommit(true) in saveImportTrackingAndChildrenInSingleDBTransaction(...)";

					log.error( msg );

					throw new Exception(msg);
				}

				try { dbConnection.close(); } 
				catch(Throwable t ) { ; }
				dbConnection = null;
			}

		}
		
		
		
		
	}
	
}
