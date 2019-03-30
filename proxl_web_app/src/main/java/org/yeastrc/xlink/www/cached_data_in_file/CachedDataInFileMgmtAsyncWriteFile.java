package org.yeastrc.xlink.www.cached_data_in_file;

import java.security.InvalidParameterException;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.SaveCachedDataFileContentsActualParameters;

/**
 * Write the cached data file async
 *
 */
public class CachedDataInFileMgmtAsyncWriteFile implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(  CachedDataInFileMgmtAsyncWriteFile.class);

	// private constructor
	private CachedDataInFileMgmtAsyncWriteFile() {}

	/**
	 * @return New instance
	 */
	public static CachedDataInFileMgmtAsyncWriteFile getNewInstance() {
		return new CachedDataInFileMgmtAsyncWriteFile();
	}
	
	private volatile SaveCachedDataFileContentsActualParameters saveCachedDataFileContentsActualParameters;

	@Override
	public void run() {
		if ( saveCachedDataFileContentsActualParameters == null ) {
			String msg = "saveCachedDataFileContentsActualParameters == null";
			log.error( msg );
			throw new InvalidParameterException( msg );
		}
		try {
			CachedDataInFileMgmt.getSingletonInstance().saveCachedDataFileContentsActual( saveCachedDataFileContentsActualParameters );
		} catch ( Exception e ) {
			String msg = "Exception calling CachedDataInFileMgmt.getSingletonInstance().saveCachedDataFileContentsActual(...)";
			log.error( msg, e );
			throw new RuntimeException( msg, e );
		}
	}

	public SaveCachedDataFileContentsActualParameters getSaveCachedDataFileContentsActualParameters() {
		return saveCachedDataFileContentsActualParameters;
	}

	public void setSaveCachedDataFileContentsActualParameters(
			SaveCachedDataFileContentsActualParameters saveCachedDataFileContentsActualParameters) {
		this.saveCachedDataFileContentsActualParameters = saveCachedDataFileContentsActualParameters;
	}

}
