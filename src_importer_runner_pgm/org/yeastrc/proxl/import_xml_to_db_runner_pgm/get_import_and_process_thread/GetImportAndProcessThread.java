package org.yeastrc.proxl.import_xml_to_db_runner_pgm.get_import_and_process_thread;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.dao.ProxlXMLFileImportTracking_For_ImporterRunner_DAO;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.process_import.ProcessProxlXMLImport;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingDTO;

/**
 * Get the next import and process it thread
 *
 */
public class GetImportAndProcessThread extends Thread {

	private static final String className = GetImportAndProcessThread.class.getSimpleName();

	private static final int WAIT_TIME_TO_GET_SOMETHING_TO_PROCESS = 5; // in seconds

	private static Logger log = Logger.getLogger(GetImportAndProcessThread.class);

	private volatile boolean keepRunning = true;
	
	private volatile ProcessProxlXMLImport processProxlXMLImport;


	/**
	 * default Constructor
	 */
	public GetImportAndProcessThread() {


		//  Set a name for the thread

		String threadName = className;

		setName( threadName );

		init();
	}


	/**
	 * Constructor
	 * @param s
	 */
	public GetImportAndProcessThread( String s ) {

		super(s);

		init();
	}

	/**
	 *
	 */
	private void init() {

	}

	/**
	 * awaken thread to get next import or to complete
	 */
	public void awaken() {

		synchronized (this) {

			notify();
		}

	}


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		log.info( "run() entered" );


		while ( keepRunning ) {

			try {
				
				ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO =
						ProxlXMLFileImportTracking_For_ImporterRunner_DAO.getInstance().getNextQueued();

				if ( proxlXMLFileImportTrackingDTO != null ) {

					synchronized (this) {

						processProxlXMLImport = ProcessProxlXMLImport.getInstance();
					}
					
					processProxlXMLImport.processProxlXMLImport( proxlXMLFileImportTrackingDTO );
					
				} else {

					int waitTimeInSeconds = WAIT_TIME_TO_GET_SOMETHING_TO_PROCESS;

					synchronized (this) {

						try {

							wait( ( (long) waitTimeInSeconds ) * 1000 ); //  wait for notify() call or timeout, in milliseconds

						} catch (InterruptedException e) {

							log.info("waitForSleepTime():  wait() interrupted with InterruptedException");

						}
					}
					
				}


			} catch ( Throwable t ) {

				log.error( "Exception in run(): ", t );


				//  TODO   handle and/or log exception


			} finally {
				

				synchronized (this) {

					if ( processProxlXMLImport != null ) {

						processProxlXMLImport = null;
					}
				}
			}
		}


		log.info( "Exiting run()" );
	}


	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {


		log.warn( "shutdown() called, setting keepRunning = false, calling awaken() " );

		keepRunning = false;
		
		
		try {
		
			if ( processProxlXMLImport != null ) {

				processProxlXMLImport.shutdown();
			}
			
		} catch ( NullPointerException e ) {
			
			//  Eat the NullPointerException since that meant that nothing had to be done.
		}


		awaken();

		log.warn( "Exiting shutdown()" );
	}


	
	

}
