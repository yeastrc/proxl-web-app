package org.yeastrc.xlink.www.cached_data_in_file;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

/**
 * Run on webapp startup
 *
 * Started in CachedDataInFileMgmtRegistration.init()
 */
public class CachedDataInFileMgmtCleanupOnWebAppStartRunnable implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(  CachedDataInFileMgmtCleanupOnWebAppStartRunnable.class);

	private CachedDataInFileMgmtCleanupOnWebAppStartRunnable() {}
	public static CachedDataInFileMgmtCleanupOnWebAppStartRunnable getNewInstance() {
		return new CachedDataInFileMgmtCleanupOnWebAppStartRunnable();
	}

	@Override
	public void run() {
		log.warn( "run() called" );
		try {
			CachedDataInFileMgmtRegistration.getSingletonInstance()
			.cleanupUnusedCachedFilesDirectories();
		} catch ( Exception e ) {
			String msg = "Exception calling CachedDataInFileMgmtRegistration.getSingletonInstance().cleanupUnusedCachedFilesDirectories();";
			log.error( msg, e );
		}
		log.warn( "run() exiting" );
	}

}
