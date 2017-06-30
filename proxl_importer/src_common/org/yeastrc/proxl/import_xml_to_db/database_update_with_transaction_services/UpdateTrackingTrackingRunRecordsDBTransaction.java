package org.yeastrc.proxl.import_xml_to_db.database_update_with_transaction_services;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO;
import org.yeastrc.proxl.import_xml_to_db.dao.ProxlXMLFileImportTracking_For_ImporterRunner_DAO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Service for updating Tracking and Tracking Run records as a single DB transaction
 *
 */
public class UpdateTrackingTrackingRunRecordsDBTransaction {

	private static final Logger log = Logger.getLogger(UpdateTrackingTrackingRunRecordsDBTransaction.class);
	
	private UpdateTrackingTrackingRunRecordsDBTransaction() { }
	public static UpdateTrackingTrackingRunRecordsDBTransaction getInstance() { 
		return new UpdateTrackingTrackingRunRecordsDBTransaction(); 
	}
	

	/**
	 * @param status - for tracking item
	 * @param id - for tracking item
	 * @param runItem - tracking run item
	 * @throws Exception
	 */
	public void updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(
			
			ProxlXMLFileImportStatus status, 
			int id,
			
			ProxlXMLFileImportTrackingRunDTO runItem
			) throws Exception {

		Connection dbConnection = null;

		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			dbConnection.setAutoCommit(false);
			
			ProxlXMLFileImportTracking_For_ImporterRunner_DAO.getInstance()
			.updateStatusAtImportEnd( status, id, dbConnection );
			
			ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO.getInstance()
			.updateStatusResultTexts( runItem, dbConnection );
			
			dbConnection.commit();
			
		} catch ( Exception e ) {
			
			String msg = "Failed updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(...)";

			log.error( msg , e);

			if ( dbConnection != null ) {
				
				try {
					dbConnection.rollback();
					
				} catch (Exception ex) {
					
					String msgRollback = "Rollback Exception:  updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(...) Exception:  See Syserr or Sysout for original exception: Rollback Exception, tables 'scan' and 'scan_spectrum_data' are in an inconsistent state. '" + ex.toString();

					System.out.println( msgRollback );
					System.err.println( msgRollback );
					
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
					String msg = "Failed dbConnection.setAutoCommit(true) in updateTrackingStatusAtImportEndupdateTrackingRunStatusResultTexts(...)";

					System.out.println( msg );
					System.err.println( msg );

					throw new Exception(msg);
				}

				try { dbConnection.close(); } 
				catch(Throwable t ) { ; }
				dbConnection = null;
			}
		}
		
	}
	
}
