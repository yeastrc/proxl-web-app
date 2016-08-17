package org.yeastrc.proxl.import_xml_to_db_runner_pgm.main;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.manager_thread.ManagerThread;



/**
 * 
 *
 */
public class ImporterRunnerMain {

	private static final Logger log = Logger.getLogger( ImporterRunnerMain.class );

	/**
	 * private constructor
	 */
	private ImporterRunnerMain() { }


	/**
	 * Static singleton instance
	 */
	private static final ImporterRunnerMain _instance = new ImporterRunnerMain();

	/**
	 * Static get singleton instance
	 * @return
	 */
	public static ImporterRunnerMain getInstance() {
		return _instance; 
	}


	private volatile boolean keepRunning = true;

//	private volatile Thread currentThread;

	private ManagerThread managerThread;


	public void importerRunnerMain() {


		try {

			log.warn( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!   importerRunnerMain start" );


//			currentThread = Thread.currentThread();

			managerThread = new ManagerThread( );

			managerThread.setImporterRunnerMain( this );

			managerThread.start();



			while ( keepRunning ) {

				//   TODO  TEMP code

				synchronized (this) {

					try {

						wait();

					} catch (InterruptedException e) {

						log.warn("wait() interrupted with InterruptedException");

					}
				}


			}


		} catch (Throwable e1) {

			String msg = "ImporterRunnerMain: Exception: " + e1.toString();

			log.error( msg, e1);


			System.out.println( msg );
			e1.printStackTrace(System.out);

			System.err.println( msg );
			e1.printStackTrace();

		}


	}




	/**
	 * shutdown was received from the operating system on a different thread
	 */
	public void shutdown() {

		log.debug( "shutdown() Called" );


		try {


			if ( managerThread != null ) {

				managerThread.shutdown();

				try {

					//  wait for managerThread to exit the run() method

					managerThread.join();

				} catch (InterruptedException e) {

					log.warn( "In shutdown(): call to managerThread.join() threw InterruptedException " + e.toString(), e );
				}
			}


		} catch (Throwable e1) {

			log.error( "ImporterRunnerMain: shutdown:  managerThread.shutdown();:  Exception: ", e1);
		}

		keepRunning = false;

		awaken();


		log.info( "Exiting shutdown()" );


	}

	/**
	 * Stop the main thread
	 */
	public void stopMainThread() {


		log.info( "stopMainThread() Called" );

		//  awaken and let main thread die

		keepRunning = false;

		awaken();


		log.info( "Exiting stopMainThread()" );


	}


	/**
	 * awaken thread to process request
	 */
	private void awaken() {

		synchronized (this) {

			notify();
		}

	}

}
