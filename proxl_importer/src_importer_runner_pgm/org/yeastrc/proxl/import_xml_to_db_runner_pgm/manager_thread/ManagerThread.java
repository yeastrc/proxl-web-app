package org.yeastrc.proxl.import_xml_to_db_runner_pgm.manager_thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.constants.RunControlFileConstants;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.get_import_and_process_thread.GetImportAndProcessThread;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.main.ImporterRunnerMain;

public class ManagerThread extends Thread {


	private static final String className = ManagerThread.class.getSimpleName();

	private static Logger log = Logger.getLogger(ManagerThread.class);



	private static final int WAIT_TIME_FOR_MANAGER_THREAD_TO_EXIT_IN_SECONDS = 10;

//	private static final int WAIT_TIME_FOR_CLIENT_STATUS_UPDATE_THREAD_TO_EXIT_IN_SECONDS = 10;

	private static final int WAIT_TIME_FOR_GET_IMPORT_THREAD_TO_EXIT_IN_SECONDS = 10;


	

	private static final int WAIT_TIME_FOR_CHECK_RUN_CONTROL_FILE_IN_SECONDS = 10;

	

	private static final String GET_IMPORT_AND_PROCESS_THREAD = "GetImportAndProcessThread";

	private volatile boolean keepRunning = true;


	private volatile boolean stopProcessingNextImport = false;


	private volatile boolean stopProxlRunImporterProgram = false;;
	
	

	private int maxTrackingRecordPriorityToRetrieve;

	
	private ImporterRunnerMain importerRunnerMain;


	private volatile GetImportAndProcessThread getImportAndProcessThread;

	private int getImportAndProcessThreadCounter = 2;  // used if need to replace the thread




//	private volatile ClientStatusUpdateThread clientStatusUpdateThread;
//
//	private int clientStatusUpdateThreadCounter = 2;  // used if need to replace the thread



	/**
	 * default Constructor
	 */
	public ManagerThread() {


		//  Set a name for the thread

		String threadName = className;

		setName( threadName );

		init();
	}


	/**
	 * Constructor
	 * @param s
	 */
	public ManagerThread( String s ) {

		super(s);

		init();
	}

	/**
	 *
	 */
	private void init() {


	}


	/**
	 * awaken thread to process request
	 */
	public void awaken() {

		synchronized (this) {

			notify();
		}

	}




	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {


		log.debug( "run() called " );

		//		ClassLoader thisClassLoader = this.getClass().getClassLoader();

		//		this.setContextClassLoader( thisClassLoader );


		try {

			createClientControlFile();


//			clientStatusUpdateThread = new ClientStatusUpdateThread();
//
//			clientStatusUpdateThread.start();


			getImportAndProcessThread = new GetImportAndProcessThread( GET_IMPORT_AND_PROCESS_THREAD /* name */ );

			getImportAndProcessThread.setMaxTrackingRecordPriorityToRetrieve( maxTrackingRecordPriorityToRetrieve );
			
			getImportAndProcessThread.start();

			runProcessLoop( );  // Call main processing loop that will run while keepRunning == true




			if ( stopProcessingNextImport ) {

				File clientControlFile = null;

				FileWriter clientControlFileWriter = null;

				try {

					clientControlFile = new File( RunControlFileConstants.CLIENT_RUN_CONTROL_FILENAME );

					log.info( "ClientControlFile: filename = '" + RunControlFileConstants.CLIENT_RUN_CONTROL_FILENAME + "' filepath is = '" + clientControlFile.getAbsolutePath() + "'." );

					clientControlFileWriter = new FileWriter( clientControlFile );

					if ( stopProxlRunImporterProgram ) {

						log.info( "ClientControlFile: Changing file contents to: \n" + RunControlFileConstants.CLIENT_RUN_CONTROL_CURRENT_IMPORT_COMPLETE_SHUTDOWN_PROCEEDING );

						clientControlFileWriter.write( RunControlFileConstants.CLIENT_RUN_CONTROL_CURRENT_IMPORT_COMPLETE_SHUTDOWN_PROCEEDING );

					} else {

						log.info( "ClientControlFile: Changing file contents to: \n" + RunControlFileConstants.CLIENT_RUN_CONTROL_CURRENT_IMPORT_COMPLETE_READY_FOR_SHUTDOWN );

						clientControlFileWriter.write( RunControlFileConstants.CLIENT_RUN_CONTROL_CURRENT_IMPORT_COMPLETE_READY_FOR_SHUTDOWN );
					}

				} catch (Throwable e) {

					log.error( "Exception in createClientControlFile(): ", e );

				} finally {

					if ( clientControlFileWriter != null ) {

						try {

							clientControlFileWriter.close();

						} catch (Throwable e) {

							log.error( "Exception in createClientControlFile(): calling clientControlFileWriter.close(); ", e );
						}
					}
				}

				if ( stopProxlRunImporterProgram ) {

					importerRunnerMain.stopMainThread();
				}

			}


		} catch (Throwable e) {

			log.error( "Exception in run(): ", e );
		}


		log.debug( "About to exit run()" );

//		LogOpenFiles.logOpenFiles( LogOpenFiles.LIST_FILES_TRUE );




		log.info( "exitting run()" );


	}


	/**
	 * Main Processing loop
	 */
	private void runProcessLoop() {

		while ( keepRunning ) {

//			if ( log.isDebugEnabled() ) {
//
//				log.debug( "Top of loop in 'runProcessLoop()', waitTime in microseconds = " + waitTime );
//
//			}


			try {

				replaceWorkerThreadsIfDead();

				if ( keepRunning && ! stopProcessingNextImport ) {
					checkForStopProcessingJobsRequest();
				}

				if ( keepRunning ) {

					synchronized (this) {

						try {

							int waitTimeInSeconds = WAIT_TIME_FOR_CHECK_RUN_CONTROL_FILE_IN_SECONDS; // ClientConfigDTO.getSingletonInstance().getSleepTimeCheckingControlFile();

							wait( waitTimeInSeconds * 1000 ); //  wait for notify() call or timeout, in milliseconds

						} catch (InterruptedException e) {

							log.warn( "wait( waitTime ) was interrupted." );

						}
					}
				}

				this.getId();

			} catch (Throwable e) {

				log.error( "Exception in runProcessLoop(): ", e );
			}
		}

	}



	/**
	 *
	 */
	private void replaceWorkerThreadsIfDead() {

		if ( keepRunning ) {  //  only do if keep running is true




			//  check health of heartbeatThread, replace thread if dead

//			if ( ! clientStatusUpdateThread.isAlive() ) {
//
//				ClientStatusUpdateThread oldHeartbeatThread = clientStatusUpdateThread;
//
//				clientStatusUpdateThread = new ClientStatusUpdateThread(  );
//
//				clientStatusUpdateThread.setName( ClientStatusUpdateThread.className + "_" + clientStatusUpdateThreadCounter );
//
//				clientStatusUpdateThreadCounter++;
//
//				log.error( "HeartbeatThread thread '" + oldHeartbeatThread.getName() + "' is dead.  Replacing it with HeartbeatThread thread '" + clientStatusUpdateThread.getName() + "'."  );
//
//				clientStatusUpdateThread.start();
//			}



			//  check health of getImportAndProcessThread, replace thread if dead

			if ( ! getImportAndProcessThread.isAlive() ) {

				GetImportAndProcessThread oldGetImportAndProcessThread = getImportAndProcessThread;

				getImportAndProcessThread = new GetImportAndProcessThread(  GET_IMPORT_AND_PROCESS_THREAD + "_" + getImportAndProcessThreadCounter /* name */  );

				getImportAndProcessThreadCounter += 1;

//				ThreadsHolderSingleton.getInstance().setGetJobThread( getJobThread );

				log.error( "GetImportAndProcessThread thread '" + oldGetImportAndProcessThread.getName() + "' is dead.  Replacing it with GetImportAndProcessThread thread '" + getImportAndProcessThread.getName() + "'."  );

				getImportAndProcessThread.start();
			}



		}
	}


	/**
	 *
	 */
	private void createClientControlFile() {

		File clientControlFile = null;

		BufferedWriter clientControlFileWriter = null;

		try {


			clientControlFile = new File( RunControlFileConstants.CLIENT_RUN_CONTROL_FILENAME );
			
			String clientControlFileWithPathString = clientControlFile.getCanonicalPath();

			log.info( "ClientControlFile: filename = '" + RunControlFileConstants.CLIENT_RUN_CONTROL_FILENAME + "' filepath is = '" + clientControlFile.getAbsolutePath() + "'." );

			clientControlFileWriter = new BufferedWriter( new FileWriter( clientControlFile ) );

			if ( log.isDebugEnabled() ) {

				StringBuilder contentsSB = new StringBuilder( 2000);

				for ( String line : RunControlFileConstants.CLIENT_RUN_CONTROL_INITIAL_CONTENTS ) {

					contentsSB.append( line  );

					contentsSB.append( "\n" );
				}


				log.debug( "ClientControlFile: Changing file contents to: \n" + contentsSB );
			}

			for ( String line : RunControlFileConstants.CLIENT_RUN_CONTROL_INITIAL_CONTENTS ) {

				clientControlFileWriter.append( line  );

				clientControlFileWriter.newLine();
			}

		} catch (Throwable e) {

			log.error( "Exception in createClientControlFile(): ", e );

		} finally {

			if ( clientControlFileWriter != null ) {

				try {

					clientControlFileWriter.close();

				} catch (Throwable e) {

					log.error( "Exception in createClientControlFile(): calling clientControlFileWriter.close(); ", e );
				}
			}
		}

	}


	/**
	 * Check if the control file has been updated to indicate that a "stop" has been requested
	 */
	private void checkForStopProcessingJobsRequest() {


		File clientControlFile = null;

		BufferedReader clientControlFileReader = null;

		BufferedWriter clientControlFileWriter = null;

		try {

			clientControlFile = new File( RunControlFileConstants.CLIENT_RUN_CONTROL_FILENAME );

			clientControlFileReader = new BufferedReader( new FileReader( clientControlFile ) );

			String inputLine = clientControlFileReader.readLine();

			if ( inputLine != null ) {

				boolean stopRequestedLocal = false;

				String stopRequestType = null;

				if ( inputLine.startsWith(  RunControlFileConstants.CLIENT_RUN_CONTROL_STOP_JOBS_TEXT ) ) {

					stopRequestedLocal = true;

					stopRequestType = RunControlFileConstants.CLIENT_RUN_CONTROL_STOP_JOBS_TEXT;

					log.info(  "File '" + RunControlFileConstants.CLIENT_RUN_CONTROL_FILENAME
							+ "' has been changed to specify to stop processing new imports and keep running when the current import is complete."
							+ "  All threads except for the main thread will be dead when the current import is complete." );

				} else if ( inputLine.startsWith(  RunControlFileConstants.CLIENT_RUN_CONTROL_STOP_RUN_TEXT ) ) {

					stopProxlRunImporterProgram = true;
					stopRequestedLocal = true;

					stopRequestType = RunControlFileConstants.CLIENT_RUN_CONTROL_STOP_RUN_TEXT;


					log.info(  "File '" + RunControlFileConstants.CLIENT_RUN_CONTROL_FILENAME
							+ "' has been changed to specify to stop processing new imports and exit when the current import is complete." );
				}

				if ( stopRequestedLocal ) {

					stopProcessingNextImport = true;

					processStopProcessingNewImportsRequest( stopRequestType );


					log.info( "ClientControlFile: Adding to file contents : \n" + RunControlFileConstants.CLIENT_RUN_CONTROL_STOP_REQUEST_ACCEPTED );

					log.info( "ClientControlFile: filename = '" + RunControlFileConstants.CLIENT_RUN_CONTROL_FILENAME + "' filepath is = '" + clientControlFile.getAbsolutePath() + "'." );

					clientControlFileWriter = new BufferedWriter( new FileWriter( clientControlFile, true /* append */ ) );

					for ( String line : RunControlFileConstants.CLIENT_RUN_CONTROL_STOP_REQUEST_ACCEPTED ) {

						clientControlFileWriter.append( line  );

						clientControlFileWriter.newLine();
					}

				}

			}

		} catch (Throwable e) {

			log.error( "Exception in checkForStopProcessingJobsRequest(): ", e );

		} finally {

			if ( clientControlFileReader != null ) {

				try {

					clientControlFileReader.close();

				} catch (Throwable e) {

					log.error( "Exception in checkForStopProcessingJobsRequest(): calling clientControlFileReader.close(); ", e );
				}
			}

			if ( clientControlFileWriter != null ) {

				try {

					clientControlFileWriter.close();

				} catch (Throwable e) {

					log.error( "Exception in checkForStopProcessingJobsRequest(): calling clientControlFileWriter.close(); ", e );
				}
			}
		}

	}


	/**
	 * Process the "stop" request from the control file.
	 */
	private void processStopProcessingNewImportsRequest( String stopRequestType ) {

		keepRunning = false;  // Set thread of the current object to exit main processing loop.




		//  call getImportAndProcessThread.shutdown();

		if ( getImportAndProcessThread != null ) {

			try {
				getImportAndProcessThread.shutdown();
			} catch (Throwable e) {

				log.error( "In processStopProcessingNewImportsRequest(): call to getImportAndProcessThread.shutdown() threw Throwable " + e.toString(), e );
			}

		} else {

			log.info( "In processStopProcessingNewImportsRequest(): getImportAndProcessThread == null" );
		}

		waitForGetImportAndProcessThread();

		//  call clientStatusUpdateThread


//		if ( clientStatusUpdateThread != null ) {
//
//			clientStatusUpdateThread.setStopRequestType( stopRequestType );
//
//			clientStatusUpdateThread.shutdown();
//		}
//
//
//		waitForClientStatusUpdateThreadToComplete();



		//  notify server attempting to shut down

//		ClientStatusUpdateTypeEnum updateType = ClientStatusUpdateTypeEnum.CLIENT_STOP_RETRIEVING_JOBS_AND_PAUSE_REQUESTED;
//
//		if ( RunControlFileConstants.CLIENT_RUN_CONTROL_STOP_JOBS_TEXT.equals( stopRequestType ) ) {
//
//			updateType = ClientStatusUpdateTypeEnum.CLIENT_STOP_RETRIEVING_JOBS_AND_PAUSE_COMPLETED;
//
//		} else if ( RunControlFileConstants.CLIENT_RUN_CONTROL_STOP_RUN_TEXT.equals( stopRequestType ) ) {
//
//			updateType = ClientStatusUpdateTypeEnum.CLIENT_STOP_RETRIEVING_JOBS_AND_SHUTDOWN_COMPLETED;
//
//		}
//
//		try {
//
//			SendClientStatusUpdateToServer.sendClientStatusUpdateToServer( updateType, PassJobsToServer.PASS_JOBS_TO_SERVER_NO );
//
//		} catch (Throwable t) {
//
//			log.info( "In processStopRetrievingJobsRequest(): call to SendClientStatusUpdateToServer.sendClientStatusUpdateToServer( ClientStatusUpdateTypeEnum.CLIENT_ABOUT_TO_EXIT ) threw Throwable " + t.toString(), t );
//		}

	}


	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {


		log.debug( "shutdown() called " );


		//  Update DB that process has received shut down request.


//		if ( clientStatusUpdateThread != null ) {
//
//			clientStatusUpdateThread.shutdown();
//		}
		


		//  call getImportAndProcessThread.shutdown();

		if ( getImportAndProcessThread != null ) {

			try {
				getImportAndProcessThread.shutdown();
			} catch (Throwable e) {

				log.error( "In processStopProcessingNewImportsRequest(): call to getImportAndProcessThread.shutdown() threw Throwable " + e.toString(), e );
			}

		} else {

			log.info( "In processStopProcessingNewImportsRequest(): getImportAndProcessThread == null" );
		}

		

		keepRunning = false;  // Set thread of the current object to exit main processing loop.

		awaken();  // send notify() to to the thread of the current object to start it so it will exit.


		boolean managerThreadExited = false;

		while ( ! managerThreadExited ) {


			try {  // wait for thread of the current object to die, so it won't start any threads to replace the threads that will be setup to die next.
				this.join( WAIT_TIME_FOR_MANAGER_THREAD_TO_EXIT_IN_SECONDS * 1000 );

			} catch (Throwable e) {

				log.error( "In processStopRetrievingJobsRequest(): call to this.shutdown() threw Throwable " + e.toString(), e );
			}

			if ( this.isAlive() ) {

//				log.warn( "The thread 'managerThread' has not exited in the allocated time of "
//						+ WAIT_TIME_FOR_MANAGER_THREAD_TO_EXIT_IN_SECONDS
//						+ " seconds.  The wait for 'managerThread' to exit will be repeated with the same wait time." );

			} else {

				managerThreadExited = true;
			}

		}


		waitForGetImportAndProcessThread();
		
		

		waitForClientStatusUpdateThreadToComplete();  //  Currently does nothing since no status update thread

		
		//  TODO   Update process status in DB to about to shut down or shut down
		
	}



	/**
	 * wait For GetImportAndProcessThread To Complete
	 */
	private void waitForGetImportAndProcessThread () {

		log.info( "waitForGetImportAndProcessThread(): wait for getImportAndProcessThread to complete, call getImportAndProcessThread.join() " );

		// wait for getImportAndProcessThread to complete

		if ( getImportAndProcessThread != null ) {

//			boolean getJobThreadExited = false;

//			while ( ! getJobThreadExited ) {
				try {
					getImportAndProcessThread.join( WAIT_TIME_FOR_GET_IMPORT_THREAD_TO_EXIT_IN_SECONDS * 1000 );
				} catch (InterruptedException e) {

					log.info( "In waitForGetJobThreadToComplete(): call to getJobThread.join() threw InterruptedException " + e.toString(), e );
				}

				if ( getImportAndProcessThread.isAlive() ) {

					log.error( "The thread 'getImportAndProcessThread' has not exited in the allocated time of "
							+ WAIT_TIME_FOR_GET_IMPORT_THREAD_TO_EXIT_IN_SECONDS
							+ " seconds.  The thread 'getJobThread' will not be waited for any further." );

//					log.error( "The thread 'getJobThread' has not exited in the allocated time of "
//							+ WAIT_TIME_FOR_GET_JOB_THREAD_TO_EXIT_IN_SECONDS
//							+ " seconds.  The wait for 'getJobThread' to exit will be repeated with the same wait time." );

				} else {

//					getJobThreadExited = true;
				}
//			}

		} else {

			log.info( "In waitForGetImportAndProcessThread(): getImportAndProcessThread == null" );
		}

		log.info( "waitForGetImportAndProcessThread():  getImportAndProcessThread IS complete, called getImportAndProcessThread.join() " );

	}


	/**
	 * wait For ClientStatusUpdateThread To Complete
	 */
	private void waitForClientStatusUpdateThreadToComplete () {

//		log.info( "waitForClientStatusUpdateThreadToComplete(): wait for ClientStatusUpdateThread to complete, call clientStatusUpdateThread.join() " );
//
//		// wait for clientStatusUpdateThread to complete
//
//		if ( clientStatusUpdateThread != null ) {
//
//
//			boolean clientStatusUpdateThreadExited = false;
//
//			while ( ! clientStatusUpdateThreadExited ) {
//
//
//				try {
//					clientStatusUpdateThread.join( WAIT_TIME_FOR_CLIENT_STATUS_UPDATE_THREAD_TO_EXIT_IN_SECONDS * 1000 );
//				} catch (InterruptedException e) {
//
//					log.info( "In waitForClientStatusUpdateThreadToComplete(): call to clientStatusUpdateThread.join() threw InterruptedException " + e.toString(), e );
//				}
//
//
//				if ( clientStatusUpdateThread.isAlive() ) {
//
//					log.info( "The thread 'clientStatusUpdateThread' has not exited in the allocated time of "
//							+ WAIT_TIME_FOR_CLIENT_STATUS_UPDATE_THREAD_TO_EXIT_IN_SECONDS
//							+ " seconds.  The wait for 'clientStatusUpdateThread' to exit will be repeated with the same wait time." );
//
//				} else {
//
//					clientStatusUpdateThreadExited = true;
//				}
//
//			}
//
//		} else {
//
//			log.info( "In waitForClientStatusUpdateThreadToComplete(): clientStatusUpdateThread == null" );
//		}
//
//		log.info( "waitForClientStatusUpdateThreadToComplete():  clientStatusUpdateThread IS complete, called clientStatusUpdateThread.join() " );

	}


	public ImporterRunnerMain getImporterRunnerMain() {
		return importerRunnerMain;
	}


	public void setImporterRunnerMain(ImporterRunnerMain importerRunnerMain) {
		this.importerRunnerMain = importerRunnerMain;
	}


	public int getMaxTrackingRecordPriorityToRetrieve() {
		return maxTrackingRecordPriorityToRetrieve;
	}


	public void setMaxTrackingRecordPriorityToRetrieve(
			int maxTrackingRecordPriorityToRetrieve) {
		this.maxTrackingRecordPriorityToRetrieve = maxTrackingRecordPriorityToRetrieve;
	}



}
