package org.yeastrc.proxl.import_xml_to_db.database_update_with_transaction_services;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO;
import org.yeastrc.proxl.import_xml_to_db.dao.ProxlXMLFileImportTracking_For_ImporterRunner_DAO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.objects.TrackingDTOTrackingRunDTOPair;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Service for getting next Tracking to process, updating Tracking and updating and inserting Tracking Run records as a single DB transaction
 *
 */
public class GetNextTrackingToProcessDBTransaction {

	private static final Logger log = Logger.getLogger(GetNextTrackingToProcessDBTransaction.class);
	
	private GetNextTrackingToProcessDBTransaction() { }
	public static GetNextTrackingToProcessDBTransaction getInstance() { 
		return new GetNextTrackingToProcessDBTransaction(); 
	}
	

	/**
	 * @param maxPriority - max priority value to consider
	 * @return - null when no next record to process is found
	 * @throws Exception
	 */
	public TrackingDTOTrackingRunDTOPair getNextTrackingToProcess( int maxPriority ) throws Exception {

		TrackingDTOTrackingRunDTOPair trackingDTOTrackingRunDTOPair = null;
		
		ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO = null;
		ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO = null;
		
		Connection dbConnection = null;

		try {

			dbConnection = getConnectionWithAutocommitTurnedOff();
			
			proxlXMLFileImportTrackingDTO =
					ProxlXMLFileImportTracking_For_ImporterRunner_DAO.getInstance().getNextQueued( maxPriority, dbConnection );

			if ( proxlXMLFileImportTrackingDTO != null ) {
			
				proxlXMLFileImportTrackingDTO.setStatus( ProxlXMLFileImportStatus.STARTED );

				ProxlXMLFileImportTracking_For_ImporterRunner_DAO.getInstance()
				.updateStatusStarted( proxlXMLFileImportTrackingDTO.getId(), dbConnection );
				
				//  Clear "current_run" on previous runs for tracking id
				
				ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO.getInstance()
				.updateClearCurrentRunForTrackingId( proxlXMLFileImportTrackingDTO.getId(), dbConnection );

				//  Create a "run" db record

				proxlXMLFileImportTrackingRunDTO = new ProxlXMLFileImportTrackingRunDTO();
				
				proxlXMLFileImportTrackingRunDTO.setProxlXmlFileImportTrackingId( proxlXMLFileImportTrackingDTO.getId() );
				proxlXMLFileImportTrackingRunDTO.setRunStatus( ProxlXMLFileImportStatus.STARTED );
				
				ProxlXMLFileImportTrackingRun_For_ImporterRunner_DAO.getInstance().save( proxlXMLFileImportTrackingRunDTO, dbConnection );

				trackingDTOTrackingRunDTOPair = new TrackingDTOTrackingRunDTOPair();

				trackingDTOTrackingRunDTOPair.setProxlXMLFileImportTrackingDTO( proxlXMLFileImportTrackingDTO );
				trackingDTOTrackingRunDTOPair.setProxlXMLFileImportTrackingRunDTO( proxlXMLFileImportTrackingRunDTO );
			}

			dbConnection.commit();
						
		} catch ( Exception e ) {
			
			String msg = "Failed getNextTrackingToProcess(...)";

			log.error( msg , e);

			if ( dbConnection != null ) {
				
				try {
					dbConnection.rollback();
					
				} catch (Exception ex) {
					
					String msgRollback = "Rollback Exception:  getNextTrackingToProcess(...) Exception:  See Syserr or Sysout for original exception: Rollback Exception, tables 'scan' and 'scan_spectrum_data' are in an inconsistent state. '" + ex.toString();

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
					String msg = "Failed dbConnection.setAutoCommit(true) in getNextTrackingToProcess(...)";

					System.out.println( msg );
					System.err.println( msg );

					throw new Exception(msg);
				}

				try { dbConnection.close(); } 
				catch(Throwable t ) { ; }
				dbConnection = null;
			}
		}
		
		return trackingDTOTrackingRunDTOPair;
	}
	

	
	/**
	 * @return
	 * @throws Exception
	 */
	private Connection getConnectionWithAutocommitTurnedOff(  ) throws Exception {
		
		Connection dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
		
		dbConnection.setAutoCommit(false);
		
		return dbConnection;
	}
	
	

}
