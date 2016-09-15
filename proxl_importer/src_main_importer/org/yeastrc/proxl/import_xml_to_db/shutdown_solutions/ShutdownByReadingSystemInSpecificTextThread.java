package org.yeastrc.proxl.import_xml_to_db.shutdown_solutions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Not TESTED
 * 
 * Not currently used.  
 * 
 * It was found that calling Process.destroy() in the Run Importer Program
 * did trigger the Shutdown hook on Linux.
 * It appears that the Shutdown hook is not triggered on Windows. 
 * 
 * 
 * 
 * Shut down the importer when the string in the variable systemInStringForShutdown
 * is read from System.in
 * 
 * Kind of chaotically shuts the importer down by closing the DB connections.
 * 
 * TODO  Improve the shutdown
 * 
 */
@Deprecated
public class ShutdownByReadingSystemInSpecificTextThread extends Thread {
	
	private static final Logger log = Logger.getLogger(ShutdownByReadingSystemInSpecificTextThread.class);

	private byte[] systemInStringForShutdown;

	/*
	 * method that will run when thread is started
	 */
	public void run() {
		
		if ( log.isDebugEnabled() ) {

			log.debug( "ShutdownByReadingSystemInSpecificTextThread::run() started now(): " + new Date());
		}

		Thread thisThread = Thread.currentThread();

		thisThread.setName( "Thread-Proc-Shutdn-Req-Sysin" );
		
		
		if ( systemInStringForShutdown == null ) {
			
			String msg = "systemInStringForShutdown is not set to a value";
			log.error( msg );
			return;   //  EARLY EXIT
		}
		

		try {
				
			final int CHAR_ARRAY_SIZE = systemInStringForShutdown.length + 1000;
			
			byte[] sysinByteArray = new byte[ CHAR_ARRAY_SIZE ];
			
			int startIndexForReading = 0;
			int lengthForReading = CHAR_ARRAY_SIZE;
						
			while (true) {
				
				int readByteCount = System.in.read( sysinByteArray, startIndexForReading, lengthForReading );
				
				if ( readByteCount == -1 ) {
					
					//  End of input reached
					
					break;
				}
				
				startIndexForReading += readByteCount;
				
				lengthForReading = CHAR_ARRAY_SIZE - startIndexForReading;
				
			}
			
			int bytesReadTotal = startIndexForReading;
			
			byte[] sysinBytesReadArray = Arrays.copyOf( sysinByteArray, bytesReadTotal );
			
			
			log.debug( "String read from System.in: " + sysinBytesReadArray );
			
			if ( ! Arrays.equals( sysinBytesReadArray, systemInStringForShutdown ) ) {
				
				//  The shutdown string was not passed so exit
				
				return; //  EARLY EXIT
			}

		} catch (IOException e) {
			
			String msg = "IOException reading system.in";
			log.error( msg, e );
			
			
			return; //  EARLY EXIT
			
		} finally {
			
		}


		if ( log.isDebugEnabled() ) {

			log.debug( "Calling DBConnectionFactory.closeAllConnections(); on shutdown from sysin thread to ensure connections closed.");
		}

		//  Ensure database connections get closed before program dies.

		try {
			// free up our db resources
			DBConnectionFactory.closeAllConnections();

			if ( log.isDebugEnabled() ) {

				log.debug( "COMPLETE:  Calling DBConnectionFactory.closeAllConnections(); on shutdown from sysin thread to ensure connections closed.");
			}

		} catch ( Exception e ) {

			System.out.println( "----------------------------------------");
			System.out.println( "----");

			System.err.println( "----------------------------------------");
			System.err.println( "----");


			System.out.println( "Shutdown On Sysin Thread: Exception in closing database connections  on shutdown from sysin thread" );
			System.err.println( "Shutdown On Sysin Thread: Exception in closing database connections  on shutdown from sysin thread" );

			e.printStackTrace( System.out );
			e.printStackTrace( System.err );

			System.out.println( "----");
			System.out.println( "----------------------------------------");

			System.err.println( "----");
			System.err.println( "----------------------------------------");
		}


		if ( log.isDebugEnabled() ) {

			log.debug( "ShutdownByReadingSystemInSpecificTextThread::run() exiting now(): " + new Date() );
		}

	}

	public byte[] getSystemInStringForShutdown() {
		return systemInStringForShutdown;
	}

	public void setSystemInStringForShutdown(byte[] systemInStringForShutdown) {
		this.systemInStringForShutdown = systemInStringForShutdown;
	}


}
