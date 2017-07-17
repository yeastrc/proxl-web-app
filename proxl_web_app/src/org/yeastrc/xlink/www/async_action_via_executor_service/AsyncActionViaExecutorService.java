package org.yeastrc.xlink.www.async_action_via_executor_service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * Class that uses an ExecutorService for running things asynchronously.
 * Asynchronously is that it is executed after the response is returned to the browser/client
 * 
 * This is used for anything that is not critical to be executed.
 * There is a small but real risk that it won't be executed before the web app is shut down.
 * Another reason it may be not executed is if the queue of things to do is full.
 *
 * Singleton Instance
 *
 */
public class AsyncActionViaExecutorService {

	private static final Logger log = Logger.getLogger( AsyncActionViaExecutorService.class);
	
	private static final int DEFAULT_THREAD_POOL_SIZE = 1;
	
	private AsyncActionViaExecutorService( int executorThreadPoolSize ) {
		executorPool = Executors.newFixedThreadPool( executorThreadPoolSize );
	}
	private static AsyncActionViaExecutorService instance = null;
	public static AsyncActionViaExecutorService getInstance() { 
		return instance;
	}
	
	public static void initInstance( int executorThreadPoolSize ) { 
		instance = new AsyncActionViaExecutorService( executorThreadPoolSize );
	}

	public static void initInstance() { 
		instance = new AsyncActionViaExecutorService( DEFAULT_THREAD_POOL_SIZE );
	}

	
	private final ExecutorService executorPool;

//	private static final int 
	
	/**
	 * @param item
	 * @return true if added to queue
	 */
	public boolean addAsyncItemToRunToQueue( AsyncItemToRun item ) {
		if ( executorPool == null ) {
			String msg = "initInstance(...) has not been called";
			log.error( msg );
			throw new IllegalStateException( msg );
		}
		try {
			executorPool.execute( item.getRunnable() );
			return true; // Currently using unbounded queue
			
		} catch ( Exception e ) {
			String msg = "Exception in addAsyncItemToRunToQueue(...)";
			log.error( msg, e );
			throw e;
		}
	}
	
	public void shutdownNow() {
		if ( executorPool == null ) {
			String msg = "initInstance(...) has not been called";
			log.error( msg );
			throw new IllegalStateException( msg );
		}
		try {
			executorPool.shutdownNow();
		} catch ( Exception e ) {
			String msg = "Exception in shutdownNow(...)";
			log.error( msg, e );
			throw e;
		}
	}

}
