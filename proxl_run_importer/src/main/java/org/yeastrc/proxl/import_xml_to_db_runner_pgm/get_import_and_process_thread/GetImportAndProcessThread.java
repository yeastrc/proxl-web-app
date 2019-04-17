package org.yeastrc.proxl.import_xml_to_db_runner_pgm.get_import_and_process_thread;

import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.database_update_with_transaction_services.GetNextTrackingToProcessDBTransaction;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.config.ImporterRunnerConfigData;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.constants.GetImportStatus_FileConstants;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.process_import.ProcessProxlXMLImport;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.objects.TrackingDTOTrackingRunDTOPair;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Get the next import and process it thread
 *
 */
public class GetImportAndProcessThread extends Thread {

	private static final String className = GetImportAndProcessThread.class.getSimpleName();
	private static final int WAIT_TIME_TO_GET_SOMETHING_TO_PROCESS_DEFAULT = 5; // in seconds
	private static final int WAIT_TIME_WHEN_GET_EXCEPTION = 5 * 60; // in seconds
	
	private static final Logger log = LoggerFactory.getLogger( GetImportAndProcessThread.class);
	
	private volatile boolean keepRunning = true;
	private volatile ProcessProxlXMLImport processProxlXMLImport;
	
	private int waitTimeForNextCheckForImportToProcess_InSeconds = WAIT_TIME_TO_GET_SOMETHING_TO_PROCESS_DEFAULT;
	
	private int maxTrackingRecordPriorityToRetrieve;
	
	public static GetImportAndProcessThread getInstance( String s ) {
		
		GetImportAndProcessThread instance = new GetImportAndProcessThread();
		instance.init();
		return instance;
	}
	
	/**
	 * default Constructor
	 */
	public GetImportAndProcessThread() {
		//  Set a name for the thread
		String threadName = className;
		setName( threadName );
	}
	/**
	 * Constructor
	 * @param s
	 */
	public GetImportAndProcessThread( String s ) {
		super(s);
	}
	
	/**
	 *
	 */
	private void init() {
		Integer waitTimeForNextCheckForImportToProcess_InSeconds_InConfig = ImporterRunnerConfigData.getWaitTimeForNextCheckForImportToProcess_InSeconds();
		if ( waitTimeForNextCheckForImportToProcess_InSeconds_InConfig != null ) {
			waitTimeForNextCheckForImportToProcess_InSeconds = waitTimeForNextCheckForImportToProcess_InSeconds_InConfig;
		}
	}
	
	/**
	 * awaken thread to get next import or to complete
	 */
	public void awaken() {
		synchronized (this) {
			notify();
		}
	}

	/**
	 * Called on a different thread.
	 * The ManagerThread instance has detected that the user has requested that the Run Importer client stop after current import.
	 */
	public void stopRunningAfterProcessingImport() {

		log.warn("INFO: stopRunningAfterProcessingJob() called:  GetImportAndProcessThread.getId() = " + this.getId() + ", GetImportAndProcessThread.getName() = " + this.getName() );
		synchronized (this) {
			this.keepRunning = false;
		}
		this.awaken();
	}
	
	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {
		log.warn( "INFO: shutdown() called, setting keepRunning = false, calling awaken() " );
		keepRunning = false;
		try {
			if ( processProxlXMLImport != null ) {
				processProxlXMLImport.shutdown();
			}
		} catch ( NullPointerException e ) {
			//  Eat the NullPointerException since that meant that nothing had to be done.
		}
		awaken();
		log.warn( "INFO: Exiting shutdown()" );
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		log.info( "run() entered" );
		while ( keepRunning ) {
			TrackingDTOTrackingRunDTOPair trackingDTOTrackingRunDTOPair = null;
			try {
				try {
					trackingDTOTrackingRunDTOPair =
							GetNextTrackingToProcessDBTransaction.getInstance()
							.getNextTrackingToProcess( maxTrackingRecordPriorityToRetrieve );
				} catch ( Throwable t ) {
					updateGetImportStatus_File_ERROR_GettingImportToProcess( t );
					throw t;
				}
				if ( trackingDTOTrackingRunDTOPair != null ) {
					updateGetImportStatus_File_YES_ImportToProcess( trackingDTOTrackingRunDTOPair );
					synchronized (this) {
						processProxlXMLImport = ProcessProxlXMLImport.getInstance();
					}
					processProxlXMLImport.processProxlXMLImport( trackingDTOTrackingRunDTOPair );
				} else {
					updateGetImportStatus_File_NO_ImportToProcess();
					int waitTimeInSeconds = waitTimeForNextCheckForImportToProcess_InSeconds;
					synchronized (this) {
						try {
							wait( ( (long) waitTimeInSeconds ) * 1000 ); //  wait for notify() call or timeout, in milliseconds
						} catch (InterruptedException e) {
							log.info("waitForSleepTime():  wait() interrupted with InterruptedException");
						}
					}
				}
			} catch ( Throwable t ) {
				
				if ( trackingDTOTrackingRunDTOPair != null ) {
					updateGetImportStatus_File_ERROR_ProcessingImportToProcess( t, trackingDTOTrackingRunDTOPair );
				}
				
				if ( keepRunning ) {
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					log.error( "!!!! \n\n Exception in run(): Will next wait " + WAIT_TIME_WHEN_GET_EXCEPTION
							+ " seconds before doing any more processing.  Exception: \n\n", t );

					log.error( "!!!! \n\n " );
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

					int waitTimeInSeconds = WAIT_TIME_WHEN_GET_EXCEPTION;
					synchronized (this) {
						try {
							wait( ( (long) waitTimeInSeconds ) * 1000 ); //  wait for notify() call or timeout, in milliseconds
						} catch (InterruptedException e) {
							log.info("waiting on Throwable exception caught:  wait() interrupted with InterruptedException");
						}
					}
				} else {
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					log.error( "!!!! \n\n Exception in run(): keepRunning is false so will not wait but exit."
							+ "  Exception: \n\n", t );

					log.error( "!!!! \n\n " );
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					
				}
				
			} finally {
				synchronized (this) {
					if ( processProxlXMLImport != null ) {
						processProxlXMLImport = null;
					}
				}
				
				try {
					DBConnectionFactory.closeAllConnections();

				} catch ( Throwable t ) {
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					log.error( "!!!! \n\n "
							+ "DBConnectionFactory.closeAllConnections(); failed in .getNextTrackingToProcess(...)"
							+ "  Exception: \n\n", t );
					log.error( "!!!! \n\n " );
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}
				
			}
		}
		log.info( "Exiting run()" );
	}
	
	/**
	 * @param trackingDTOTrackingRunDTOPair
	 * @throws IOException
	 */
	private void updateGetImportStatus_File_YES_ImportToProcess( TrackingDTOTrackingRunDTOPair trackingDTOTrackingRunDTOPair ) {
	
		try {
			int id = trackingDTOTrackingRunDTOPair.getProxlXMLFileImportTrackingDTO().getId();
	
			try ( BufferedWriter writer = new BufferedWriter( new FileWriter( GetImportStatus_FileConstants.GET_IMPORT_STATUS_FILENAME ) ) ) {
				
				writer.write( GetImportStatus_FileConstants.GET_IMPORT_STATUS_YES_FOUND_REQUEST_TO_PROCESS_TEXT );
				writer.write( String.valueOf( id ) );
				writer.newLine();
			}
		} catch ( Throwable t ) {
			
			log.error( "Failed to update file " + GetImportStatus_FileConstants.GET_IMPORT_STATUS_FILENAME );
			//  Eat Exception
		}
	}

	/**
	 * @throws IOException
	 */
	private void updateGetImportStatus_File_NO_ImportToProcess( ) {

		try {
			try ( BufferedWriter writer = new BufferedWriter( new FileWriter( GetImportStatus_FileConstants.GET_IMPORT_STATUS_FILENAME ) ) ) {
				
				writer.write( GetImportStatus_FileConstants.GET_IMPORT_STATUS_NONE_FOUND_REQUEST_TO_PROCESS_TEXT );
				writer.newLine();
			}
	} catch ( Throwable t ) {
			
			log.error( "Failed to update file " + GetImportStatus_FileConstants.GET_IMPORT_STATUS_FILENAME );
			//  Eat Exception
		}
	}

	/**
	 * @param throwable
	 */
	private void updateGetImportStatus_File_ERROR_GettingImportToProcess( Throwable throwable ) {
		try {
			try ( PrintWriter writer = new PrintWriter( new FileWriter( GetImportStatus_FileConstants.GET_IMPORT_STATUS_FILENAME ) ) ) {
				
				writer.write( GetImportStatus_FileConstants.GET_IMPORT_STATUS_YES_ERROR_CHECKING_FOR_REQUEST_TEXT );
				writer.write( "\n" );
				throwable.printStackTrace( writer );
				writer.write( "\n" );
			}
		} catch ( Throwable t ) {
			
			log.error( "Failed to update file " + GetImportStatus_FileConstants.GET_IMPORT_STATUS_FILENAME );
			//  Eat Exception
		}
	}

	/**
	 * @param throwable
	 * @param trackingDTOTrackingRunDTOPair
	 */
	private void updateGetImportStatus_File_ERROR_ProcessingImportToProcess( Throwable throwable, TrackingDTOTrackingRunDTOPair trackingDTOTrackingRunDTOPair ) {

		String trackingId = null;
		try {
			if ( trackingDTOTrackingRunDTOPair != null ) {
				trackingId = ": Import Tracking Id: " + trackingDTOTrackingRunDTOPair.getProxlXMLFileImportTrackingDTO().getId();
			}
		} catch ( Throwable t ) {
			
		}
		
		try {
			try ( PrintWriter writer = new PrintWriter( new FileWriter( GetImportStatus_FileConstants.GET_IMPORT_STATUS_FILENAME ) ) ) {
				
				writer.write( GetImportStatus_FileConstants.GET_IMPORT_STATUS_YES_ERROR_PROCESSING_REQUEST_TEXT );
				if ( trackingId != null ) {
					writer.write( trackingId );					
				}
				writer.write( "\n" );
				throwable.printStackTrace( writer );
				writer.write( "\n" );
			}
		} catch ( Throwable t ) {
			
			log.error( "Failed to update file " + GetImportStatus_FileConstants.GET_IMPORT_STATUS_FILENAME );
			//  Eat Exception
		}
	}


	public int getMaxTrackingRecordPriorityToRetrieve() {
		return maxTrackingRecordPriorityToRetrieve;
	}
	public void setMaxTrackingRecordPriorityToRetrieve(
			int maxTrackingRecordPriorityToRetrieve) {
		this.maxTrackingRecordPriorityToRetrieve = maxTrackingRecordPriorityToRetrieve;
	}
}
