package org.yeastrc.xlink.www.cached_data_in_file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.www.async_action_via_executor_service.AsyncActionViaExecutorService;
import org.yeastrc.xlink.www.async_action_via_executor_service.AsyncItemToRun;
import org.yeastrc.xlink.www.async_action_via_executor_service.AsyncItemToRunFactory;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.exceptions.ProxlWebappConfigException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;

/**
 * For Cached data that is written to a file under the Cached Data Directory.
 * 
 * This is a single point for retrieving and saving files containing cached data
 * (JSON,PNG image, etc.)
 *
 * Singleton
 */
public class CachedDataInFileMgmt {

	private static final Logger log = LoggerFactory.getLogger( CachedDataInFileMgmt.class);
	
	/**
	 * If YES, always save even if already exists
	 *
	 */
	public enum ReplaceExistingValue { YES, NO }
	
	/**
	 * Accepted types for the ids
	 *
	 */
	public enum IdParamType { SEARCH_ID, PROJECT_SEARCH_ID, SCAN_FILE_ID }
	
	//  Filename prefix for the assoc id type
	private static final String CACHE_FILENAME_PREFIX_FOR_SEARCH_ID = "se_";
	private static final String CACHE_FILENAME_PREFIX_FOR_PROJECT_SEARCH_ID = "ps_";
	private static final String CACHE_FILENAME_PREFIX_FOR_SCAN_FILE_ID = "sc_";
	

	private static final String SHA_384_ALGORITHM = "SHA-384";
//	private static final String SHA_512_ALGORITHM = "SHA-512";
//	private static final String SHA_1_ALGORITHM = "SHA1";

	private static final String CACHE_FILES_ROOT_DIRECTORY = ConfigSystemsKeysSharedConstants.CACHE_FILES_ROOT_DIRECTORY;

	private static final String subdirPrefixWhenOneChar = "_";

	private static final String REQUEST_URL_PARTIAL_FILE_SUFFIX = "_rurlp";
	
	private static final String DONE_FILE_SUFFIX = "_done";
	
	private enum CalledFrom { FROM_retrieveCachedDataFileContents, FROM_saveCachedDataFileContentsFor_NamePrefix_Id_Version }

	private static final CachedDataInFileMgmt instance = new CachedDataInFileMgmt();

	// private constructor
	private CachedDataInFileMgmt() {}

	/**
	 * @return Singleton instance
	 */
	public static CachedDataInFileMgmt getSingletonInstance() {
		return instance;
	}
	
	//  For Version as String
    private final DecimalFormat decimalFormatZeroFill = new DecimalFormat("00000");
    
    /**
     * Used to stop removal of cached directories
     */
    private volatile boolean shutdownNowReceived = false;
    
	/**
	 * Called on web app shutdown
	 */
	public void shutdownNow(){
		log.warn( "INFO: shutdownNow() called." );
		shutdownNowReceived = true;
	}
	
    /**
     * @return
     * @throws Exception
     */
    private String getCacheFilesRootDirString() throws Exception {

		String cacheFilesRootDirString = ConfigSystemCaching.getInstance()
				.getConfigValueForConfigKey( CACHE_FILES_ROOT_DIRECTORY );
		return cacheFilesRootDirString;
    }
	
	/**
	 * @return
	 * @throws Exception
	 */
	public boolean isCachedDataFilesDirConfigured() throws Exception {
		
		String cacheFilesRootDirString = getCacheFilesRootDirString();
		if ( StringUtils.isEmpty( cacheFilesRootDirString ) ) {
			//  CACHE_FILES_ROOT_DIRECTORY is not configured so return false
			return false; //  Early Exit
		}
		return true;
	}
	

	public void removeCachedDataDirectory( String namePrefix, int version ) throws Exception {

		if ( shutdownNowReceived ) {
			return; // EARLY EXIT
		}
		
		String cacheFilesRootDirString = getCacheFilesRootDirString();
		if ( StringUtils.isEmpty( cacheFilesRootDirString ) ) {
			//  CACHE_FILES_ROOT_DIRECTORY is not configured so return null always
			return; //  Early Exit
		}

		File cacheFilesRootDir = new File( cacheFilesRootDirString );
		if ( ! cacheFilesRootDir.exists() ) {
			//  CACHE_FILES_ROOT_DIRECTORY does not exist
//			log.warn( "Cached Data Files Directory does not exist.  Config Key: '" + CACHE_FILES_ROOT_DIRECTORY
//					+ "', Config Value: " + cacheFilesRootDirString );
			return; //  Early Exit
		}
		
		
		String versionAsString = getVersionAsString( version );

		String rootDirForPrefixAndVersionString =  getRootDirForPrefixAndVersionString( namePrefix, versionAsString );
		
		File rootDirForPrefixAndVersion = new File( cacheFilesRootDir, rootDirForPrefixAndVersionString );
		
		if ( ! rootDirForPrefixAndVersion.exists() ) {
			log.info( "Cached file directory to delete does not exist: " + rootDirForPrefixAndVersion.getAbsolutePath() );
			return; //  Early Exit
		}
		
		log.warn( "INFO: Starting: Deleting cached file directory (and it's contents): " + rootDirForPrefixAndVersion.getAbsolutePath() );
		//  Delete cacheFilesRootDir and everything under it.  Always delete 'done' file first
		deleteCacheFilesDir( rootDirForPrefixAndVersion );

		if ( shutdownNowReceived ) {
			log.warn( "Interrupted by shutdown received: Deleting cached file directory (and it's contents): " + rootDirForPrefixAndVersion.getAbsolutePath() );
			return; // EARLY EXIT
		}
		log.warn( "INFO: Finished: Deleting cached file directory (and it's contents): " + rootDirForPrefixAndVersion.getAbsolutePath() );
	}
	
	/**
	 * Calls itself recursively to delete subdirectories.
	 * returns immediately if shutdownNowReceived is true
	 * @param dir
	 */
	private void deleteCacheFilesDir( File dir ) {

		if ( shutdownNowReceived ) {
			return; // EARLY EXIT
		}
		//  Delete 'done' files first
		for ( File dirEntry : dir.listFiles() ) {
			if ( dirEntry.isFile() && dirEntry.getName().endsWith( DONE_FILE_SUFFIX ) ) {
				if ( ! dirEntry.delete() ) {
					String msg = "Failed to delete file: " + dirEntry.getAbsolutePath();
					log.error( msg );
				}
			}
		}
		
		//  Delete other files and subdirectories
		for ( File dirEntry : dir.listFiles() ) {
			if ( shutdownNowReceived ) {
				break;  // Exit loop immediately
			}
			if ( dirEntry.isFile() ) {
				if ( ! dirEntry.delete() ) {
					String msg = "Failed to delete file: " + dirEntry.getAbsolutePath();
					log.error( msg );
				}
			} else if ( dirEntry.isDirectory() ) {
				deleteCacheFilesDir( dirEntry ); // recursively delete subdirectories
			} else {
				String msg = "dirEntry is not file or directory: " + dirEntry.getAbsolutePath();
				log.error( msg );
			}
		}
		if ( ! dir.delete() ) {
			String msg = "Failed to delete dir: " + dir.getAbsolutePath();
			log.error( msg );
		}

	}

	/**
	 * Get the byte[] of the cached data, if it exists.
	 * 
	 * namePrefix and version are used for grouping cached data and deleting cached data that is no longer used.
	 *   (Currently that amounts to putting them under the same subdirectory)
	 *   
	 * requestURLPartial: normally the query string.  May be the path if write true REST services 
	 * 
	 * ids are the ids associated for this cached data - Search Ids, Project Search Ids, or Scan File Ids
	 *   
	 * @param namePrefix
	 * @param version
	 * @param requestURLPartial
	 * @param ids - Search Ids, Project Search Ids, or Scan File Ids
	 * @param idParamType - type for param ids
	 * @return
	 * @throws Exception
	 */
	public byte[] retrieveCachedDataFileContents( 
			String namePrefix,
			int version,
			String requestIdentifierString, 
			List<Integer> ids,
			IdParamType idParamType
			 ) throws Exception {
		
		if ( StringUtils.isEmpty( requestIdentifierString ) ) {
			String msg = "requestIdentifierString cannot be null or empty";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		byte[] requestIdentifierBytes = null;
		try {
			requestIdentifierBytes = requestIdentifierString.getBytes( StandardCharsets.UTF_8 );
		} catch ( RuntimeException e ) {
			String msg = "Failed to convert requestIdentifierString to bytes using UTF-8.  requestIdentifierString: " + requestIdentifierString;
			log.error( msg, e );
			throw new ProxlWebappInternalErrorException( msg, e );
		}
		
		return retrieveCachedDataFileContents( 
				namePrefix, 
				version, 
				requestIdentifierBytes, 
				ids, 
				idParamType );
	}
	
	/**
	 * Get the byte[] of the cached data, if it exists.
	 * 
	 * namePrefix and version are used for grouping cached data and deleting cached data that is no longer used.
	 *   (Currently that amounts to putting them under the same subdirectory)
	 *   
	 * requestURLPartial: normally the query string.  May be the path if write true REST services 
	 * 
	 * ids are the ids associated for this cached data - Search Ids, Project Search Ids, or Scan File Ids
	 *   
	 * @param namePrefix
	 * @param version
	 * @param requestIdentifierBytes
	 * @param ids - Search Ids, Project Search Ids, or Scan File Ids
	 * @param idParamType - type for param ids
	 * @return
	 * @throws Exception
	 */
	public byte[] retrieveCachedDataFileContents( 
			String namePrefix,
			int version,
			byte[] requestIdentifierBytes, 
			List<Integer> ids,
			IdParamType idParamType
			 ) throws Exception {

		GetCachedDataFileAndDoneFileResult getCachedDataFileAndDoneFileResult =
				getCachedDataFileAndDoneFile( namePrefix, version, requestIdentifierBytes, ids, idParamType, CalledFrom.FROM_retrieveCachedDataFileContents );

		if ( getCachedDataFileAndDoneFileResult == null ) {
			//  Not configured or the subdirectory does not exist
			return null; //  Early Exit
		}
		
		File cachedDataFile = getCachedDataFileAndDoneFileResult.cachedDataFile;
		File cachedDataFileRequestURLPartialFile = getCachedDataFileAndDoneFileResult.cachedDataFileRequestURLPartialFile;
		File cachedDataFileDoneFile = getCachedDataFileAndDoneFileResult.cachedDataFileDoneFile;

		if ( ! cachedDataFileDoneFile.exists() ) {
			//  The 'done' file does not exist
			return null; //  Early Exit
		}
		if ( ! cachedDataFileRequestURLPartialFile.exists() ) {
			//  The file does not exist
			return null; //  Early Exit
		}
		if ( ! cachedDataFile.exists() ) {
			//  The file does not exist
			return null; //  Early Exit
		}
		
		{
			//		Read cachedDataFileRequestURLPartialFile and compare to requestURLPartial
			//		return null if not match and log warn

			long cachedDataFileRequestURLPartialFileLength = cachedDataFileRequestURLPartialFile.length();
			if ( cachedDataFileRequestURLPartialFileLength == 0 ) {
				String msg = "cachedDataFileRequestURLPartialFile file is empty. length: " + cachedDataFileRequestURLPartialFileLength 
						+ ", file: " + cachedDataFileRequestURLPartialFile.getCanonicalPath();
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			if ( cachedDataFileRequestURLPartialFileLength > Integer.MAX_VALUE ) {
				String msg = "Cached file Request URL Partial length >  so unable to read into byte[]. length: " + cachedDataFileRequestURLPartialFileLength 
						+ ", file: " + cachedDataFileRequestURLPartialFile.getCanonicalPath();
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}

			byte[] cachedDataFileRequestURLPartialContents = new byte[ (int)cachedDataFileRequestURLPartialFileLength ];
			FileInputStream cachedDataFileRequestURLPartialInputStream = null;
			try {
				cachedDataFileRequestURLPartialInputStream = new FileInputStream( cachedDataFileRequestURLPartialFile );
				int bytesRead = cachedDataFileRequestURLPartialInputStream.read( cachedDataFileRequestURLPartialContents );
				if ( bytesRead != cachedDataFileRequestURLPartialFileLength ) {
					String msg = "bytesRead != cachedDataFileRequestURLPartialFileLength. length: " + cachedDataFileRequestURLPartialFileLength 
							+ ", file: " + cachedDataFile.getCanonicalPath();
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
			} finally {
				if ( cachedDataFileRequestURLPartialInputStream != null ) {
					cachedDataFileRequestURLPartialInputStream.close();
				}
			}
			
			if ( ! Arrays.equals( cachedDataFileRequestURLPartialContents, requestIdentifierBytes ) ) {
				
				String requestIdentifierString = "Unable to convert to String UTF-8";
				String cachedDataFileRequestURLPartial_String = "Unable to convert to String UTF-8";

				try {
					requestIdentifierString = new String( requestIdentifierBytes, StandardCharsets.UTF_8 );
				} catch ( Throwable t ) {
					log.warn( "In Prep to create 'warn' message: Failing to convert requestIdentifierBytes to String UTF-8", t );
				}
				try {
					cachedDataFileRequestURLPartial_String = new String( cachedDataFileRequestURLPartialContents, StandardCharsets.UTF_8 );
				} catch ( Throwable t ) {
					log.warn( "In Prep to create 'warn' message: Failing to convert cachedDataFileRequestURLPartialContents to String UTF-8", t );
				}
				
				String msg = "The cached Request URL Partial does not match the Request URL Partial in the request.  "
						+ "Cached Request URL Partial: " + cachedDataFileRequestURLPartial_String
						+ ".  Request Identifier in the request: " + requestIdentifierString
						+ ", file: " + cachedDataFile.getCanonicalPath();
				log.warn( msg );
				return null;  // EARLY EXIT
			}
		}
		
		long cachedDataFileLength = cachedDataFile.length();
		if ( cachedDataFileLength > Integer.MAX_VALUE ) {
			String msg = "Cached file length >  so unable to read into byte[]. length: " + cachedDataFileLength 
					+ ", file: " + cachedDataFile.getCanonicalPath();
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
		
		byte[] cachedDataFileContents = new byte[ (int)cachedDataFileLength ];
		FileInputStream cachedDataFileInputStream = null;
		try {
			cachedDataFileInputStream = new FileInputStream( cachedDataFile );
			int bytesRead = cachedDataFileInputStream.read( cachedDataFileContents );
			if ( bytesRead != cachedDataFileLength ) {
				String msg = "bytesRead != cachedDataFileLength. length: " + cachedDataFileLength 
						+ ", file: " + cachedDataFile.getCanonicalPath();
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
		} finally {
			if ( cachedDataFileInputStream != null ) {
				cachedDataFileInputStream.close();
			}
		}
		
		return cachedDataFileContents;
	}

	/**
	 * Save the byte[] of the cached data in dataToSave.
	 * 
	 * namePrefix and version are used for grouping cached data and deleting cached data that is no longer used.
	 *   (Currently that amounts to putting them under the same subdirectory)
	 *   
	 * requestURLPartial: normally the query string.  May be the path if write true REST services 
	 * 
	 * ids are the ids associated for this cached data - Search Ids, Project Search Ids, or Scan File Ids
	 * 
	 * @param dataToSave
	 * @param namePrefix
	 * @param version
	 * @param requestIdentifierString
	 * @param ids - Search Ids, Project Search Ids, or Scan File Ids
	 * @param idParamType - type for param ids
	 * @return
	 * @throws Exception
	 */
	public boolean saveCachedDataFileContents( 
			byte[] dataToSave,
			ReplaceExistingValue replaceExistingValue,
			String namePrefix, 
			int version,
			String requestIdentifierString, 
			List<Integer> ids,
			IdParamType idParamType
			) throws Exception {

		if ( StringUtils.isEmpty( requestIdentifierString ) ) {
			String msg = "requestIdentifierString cannot be null or empty";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		byte[] requestIdentifierBytes = null;
		try {
			requestIdentifierBytes = requestIdentifierString.getBytes( StandardCharsets.UTF_8 );
		} catch ( RuntimeException e ) {
			String msg = "Failed to convert requestIdentifierString to bytes using UTF-8.  requestIdentifierString: " + requestIdentifierString;
			log.error( msg, e );
			throw new ProxlWebappInternalErrorException( msg, e );
		}
		
		return saveCachedDataFileContents( 
				dataToSave, 
				replaceExistingValue, 
				namePrefix, 
				version, 
				requestIdentifierBytes, 
				ids, 
				idParamType );
	}
	
	/**
	 * Save the byte[] of the cached data in dataToSave.
	 * 
	 * namePrefix and version are used for grouping cached data and deleting cached data that is no longer used.
	 *   (Currently that amounts to putting them under the same subdirectory)
	 *   
	 * requestURLPartial: normally the query string.  May be the path if write true REST services 
	 * 
	 * ids are the ids associated for this cached data - Search Ids, Project Search Ids, or Scan File Ids
	 * 
	 * @param dataToSave
	 * @param namePrefix
	 * @param version
	 * @param requestURLPartial
	 * @param ids - Search Ids, Project Search Ids, or Scan File Ids
	 * @param idParamType - type for param ids
	 * @return
	 * @throws Exception
	 */
	public boolean saveCachedDataFileContents( 
			byte[] dataToSave,
			ReplaceExistingValue replaceExistingValue,
			String namePrefix, 
			int version,
			byte[] requestIdentifierBytes, 
			List<Integer> ids,
			IdParamType idParamType
			) throws Exception {

		GetCachedDataFileAndDoneFileResult getCachedDataFileAndDoneFileResult =
				getCachedDataFileAndDoneFile( namePrefix, version, requestIdentifierBytes, ids, idParamType, CalledFrom.FROM_saveCachedDataFileContentsFor_NamePrefix_Id_Version );

		if ( getCachedDataFileAndDoneFileResult == null ) {
			//  Not configured or the configured subdirectory does not exist
			return false; //  Early Exit
		}
		
		File cachedDataFileDoneFile = getCachedDataFileAndDoneFileResult.cachedDataFileDoneFile;

		//  If already exists, assume it is the same value and exit
		if ( ( replaceExistingValue == null || replaceExistingValue ==  ReplaceExistingValue.NO ) 
				&& cachedDataFileDoneFile.exists() ) {
			return true; //  Early Exit
		}

		if ( ( replaceExistingValue != null && replaceExistingValue ==  ReplaceExistingValue.YES ) 
				&& cachedDataFileDoneFile.exists() ) {
			cachedDataFileDoneFile.delete();
		}
		
		SaveCachedDataFileContentsActualParameters saveCachedDataFileContentsActualParameters = null;
		synchronized ( this ) {
			saveCachedDataFileContentsActualParameters = new SaveCachedDataFileContentsActualParameters();
			saveCachedDataFileContentsActualParameters.dataToSave = dataToSave;
			saveCachedDataFileContentsActualParameters.cachedDataFile = getCachedDataFileAndDoneFileResult.cachedDataFile;
			saveCachedDataFileContentsActualParameters.cachedDataFileRequestURLPartialFile = getCachedDataFileAndDoneFileResult.cachedDataFileRequestURLPartialFile;
			saveCachedDataFileContentsActualParameters.requestUrlPartialData = getCachedDataFileAndDoneFileResult.requestUrlPartialData;
			saveCachedDataFileContentsActualParameters.cachedDataFileDoneFile = getCachedDataFileAndDoneFileResult.cachedDataFileDoneFile;
			saveCachedDataFileContentsActualParameters.subdir_1 = getCachedDataFileAndDoneFileResult.subdir_1;
			saveCachedDataFileContentsActualParameters.subdir_2 = getCachedDataFileAndDoneFileResult.subdir_2;
		}
		
		//  Save cached file sync
//		saveCachedDataFileContentsActual( saveCachedDataFileContentsActualParameters );
		
		// Save cached file async
		
		CachedDataInFileMgmtAsyncWriteFile cachedDataInFileMgmtAsyncWriteFile = CachedDataInFileMgmtAsyncWriteFile.getNewInstance();
		cachedDataInFileMgmtAsyncWriteFile.setSaveCachedDataFileContentsActualParameters( saveCachedDataFileContentsActualParameters );
		
		AsyncItemToRun asyncItemToRun = AsyncItemToRunFactory.createAsyncItemToRun( cachedDataInFileMgmtAsyncWriteFile );
		AsyncActionViaExecutorService.getInstance().addAsyncItemToRunToQueue( asyncItemToRun );
		
		return true;  // If async, true only means it has been queued to be saved
	}
	
	/**
	 * parameters for method saveCachedDataFileContentsActual
	 *
	 */
	static class SaveCachedDataFileContentsActualParameters {
		private byte[] dataToSave;
		private File cachedDataFile;
		private File cachedDataFileRequestURLPartialFile;
		private byte[] requestUrlPartialData;
		private File cachedDataFileDoneFile;
		private File subdir_1;
		private File subdir_2;
		
		public byte[] getDataToSave() {
			return dataToSave;
		}
		public void setDataToSave(byte[] dataToSave) {
			this.dataToSave = dataToSave;
		}
		public File getCachedDataFile() {
			return cachedDataFile;
		}
		public void setCachedDataFile(File cachedDataFile) {
			this.cachedDataFile = cachedDataFile;
		}
		public File getCachedDataFileDoneFile() {
			return cachedDataFileDoneFile;
		}
		public void setCachedDataFileDoneFile(File cachedDataFileDoneFile) {
			this.cachedDataFileDoneFile = cachedDataFileDoneFile;
		}
		public File getSubdir_1() {
			return subdir_1;
		}
		public void setSubdir_1(File subdir_1) {
			this.subdir_1 = subdir_1;
		}
		public File getSubdir_2() {
			return subdir_2;
		}
		public void setSubdir_2(File subdir_2) {
			this.subdir_2 = subdir_2;
		}
		public File getCachedDataFileRequestURLPartialFile() {
			return cachedDataFileRequestURLPartialFile;
		}
		public void setCachedDataFileRequestURLPartialFile(File cachedDataFileRequestURLPartialFile) {
			this.cachedDataFileRequestURLPartialFile = cachedDataFileRequestURLPartialFile;
		}
		public byte[] getRequestUrlPartialData() {
			return requestUrlPartialData;
		}
		public void setRequestUrlPartialData(byte[] requestUrlPartialData) {
			this.requestUrlPartialData = requestUrlPartialData;
		}
	}

	/**
	 * Package Private, called from CachedDataInFileMgmtAsyncWriteFile
	 * 
	 * @param dataToSave
	 * @param getCachedDataFileAndDoneFileResult
	 * @throws Exception
	 */
	void saveCachedDataFileContentsActual( 
			SaveCachedDataFileContentsActualParameters saveCachedDataFileContentsActualParameters ) throws Exception {

		File cachedDataFile = saveCachedDataFileContentsActualParameters.cachedDataFile;
		File cachedDataFileRequestURLPartialFile = saveCachedDataFileContentsActualParameters.cachedDataFileRequestURLPartialFile;
		File cachedDataFileDoneFile = saveCachedDataFileContentsActualParameters.cachedDataFileDoneFile;

		File subdir_1 = saveCachedDataFileContentsActualParameters.subdir_1;
		File subdir_2 = saveCachedDataFileContentsActualParameters.subdir_2;
		
		if ( ! subdir_1.exists() ) {
			if ( ! subdir_1.mkdir() ) {
				String msg = "Failed to make subdir: " + subdir_1.getAbsolutePath();
				log.error( msg );
				throw new ProxlWebappConfigException( msg );
			}
		}

		if ( ! subdir_2.exists() ) {
			if ( ! subdir_2.mkdir() ) {
				String msg = "Failed to make subdir: " + subdir_2.getAbsolutePath();
				log.error( msg );
				throw new ProxlWebappConfigException( msg );
			}
		}
		
		//  Create data file
		{
			FileOutputStream cachedDataFileOutputStream = null;
			try {
				cachedDataFileOutputStream = new FileOutputStream( cachedDataFile );
				cachedDataFileOutputStream.write( saveCachedDataFileContentsActualParameters.dataToSave );
			} catch ( Exception e ) {
				String msg = "Failed to write to Cached Data file: " + cachedDataFile.getCanonicalPath();
				log.error( msg, e );
				throw new ProxlWebappInternalErrorException( msg );
			} finally {
				if ( cachedDataFileOutputStream != null ) {
					cachedDataFileOutputStream.close();
				}
			}
		}
		//  Create file containing the Partial Request URL
		{
			FileOutputStream cachedDataFileOutputStream = null;
			try {
				cachedDataFileOutputStream = new FileOutputStream( cachedDataFileRequestURLPartialFile );
				cachedDataFileOutputStream.write( saveCachedDataFileContentsActualParameters.requestUrlPartialData );
			} catch ( Exception e ) {
				String msg = "Failed to write to Cached Data 'Partial Request URL' file: " + cachedDataFileRequestURLPartialFile.getCanonicalPath();
				log.error( msg, e );
				throw new ProxlWebappInternalErrorException( msg );
			} finally {
				if ( cachedDataFileOutputStream != null ) {
					cachedDataFileOutputStream.close();
				}
			}
		}
		//  Create 'done' file
		{
			FileOutputStream cachedDataFileOutputStream = null;
			try {
				cachedDataFileOutputStream = new FileOutputStream( cachedDataFileDoneFile );
				cachedDataFileOutputStream.write( "done".getBytes() );
			} catch ( Exception e ) {
				String msg = "Failed to write to Cached Data 'done' file: " + cachedDataFileDoneFile.getCanonicalPath();
				log.error( msg, e );
				throw new ProxlWebappInternalErrorException( msg );
			} finally {
				if ( cachedDataFileOutputStream != null ) {
					cachedDataFileOutputStream.close();
				}
			}
		}
	}
	
	/**
	 * Internal to this class, returned from getCachedDataFileAndDoneFile
	 *
	 */
	private static class GetCachedDataFileAndDoneFileResult {
		File cachedDataFile;
		File cachedDataFileRequestURLPartialFile;
		byte[] requestUrlPartialData;
		File cachedDataFileDoneFile;
		File subdir_1;
		File subdir_2;
	}
	
	/**
	 * return null if:
	 *    CACHE_FILES_ROOT_DIRECTORY is not configured 
	 *    createSubdirsIfNotExist == YES and base directory does not exist
	 *    createSubdirsIfNotExist == NO and ( base directory or subdirectories do not exist )
	 * 
	 * namePrefix and version are used for grouping cached data and deleting cached data that is no longer used.
	 *   (Currently that amounts to putting them under the same subdirectory)
	 *   
	 * requestURLPartial: normally the query string.  May be the path if write true REST services 
	 * 
	 * ids are the ids associated for this cached data - Search Ids, Project Search Ids, or Scan File Ids
	 *   
	 * @param namePrefix
	 * @param version
	 * @param requestURLPartial
	 * @param ids - Search Ids, Project Search Ids, or Scan File Ids
	 * @param idParamType - type for param ids
	 * @param createSubdirsIfNotExist
	 * @return
	 * @throws Exception
	 */
	private GetCachedDataFileAndDoneFileResult getCachedDataFileAndDoneFile( 
			String namePrefix,
			int version,
			byte[] requestIdentifierBytes, 
			List<Integer> ids,
			IdParamType idParamType,
			CalledFrom calledFrom ) throws Exception {
		
		if ( StringUtils.isEmpty( namePrefix ) ) {
			String msg = "namePrefix cannot be null or empty";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}

		if ( requestIdentifierBytes == null || requestIdentifierBytes.length == 0 ) {
			String msg = "requestIdentifierBytes cannot be null or empty";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}

		if ( ids == null || ids.isEmpty() ) {
			String msg = "ids cannot be null or empty";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}

		if ( idParamType == null ) {
			String msg = "idParamType cannot be null";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		String cacheFilesRootDirString = getCacheFilesRootDirString();
		if ( StringUtils.isEmpty( cacheFilesRootDirString ) ) {
			//  CACHE_FILES_ROOT_DIRECTORY is not configured so return null always
			return null; //  Early Exit
		}

		File cacheFilesRootDir = new File( cacheFilesRootDirString );
		if ( ! cacheFilesRootDir.exists() ) {
			//  CACHE_FILES_ROOT_DIRECTORY does not exist
			log.warn( "Cached Data Files Directory does not exist.  Config Key: '" + CACHE_FILES_ROOT_DIRECTORY
					+ "', Config Value: " + cacheFilesRootDirString );
			return null; //  Early Exit
		}

		Integer firstId = ids.get( 0 );
		
		//  The namePrefix and version are used for the root subdirectory for this particular file.
		//  The last 2 characters of the first id as string are used for a subdirectory path to limit the number of files in a subdirectory.
		
		String versionAsString = getVersionAsString( version );

		String rootDirForPrefixAndVersionString =  getRootDirForPrefixAndVersionString( namePrefix, versionAsString );

		String filename = getCacheFilename( requestIdentifierBytes, ids, idParamType );
		String stringForSubdirs = String.valueOf( firstId );
		if ( stringForSubdirs.length() == 1 ) {
			stringForSubdirs += subdirPrefixWhenOneChar;
		}
		
		String filenameRequestURLPartialFileString = filename + REQUEST_URL_PARTIAL_FILE_SUFFIX;
		
		String filenameDoneFileString = filename + DONE_FILE_SUFFIX;
		
		int stringForSubdirsLength = stringForSubdirs.length();

		//  sub dirs to split files to limit size of directories
		
		//  Next to last character in id string
		String subdir_1_String = stringForSubdirs.substring( stringForSubdirsLength - 2, stringForSubdirsLength - 1 );
		//  Last character in id string
		String subdir_2_String = stringForSubdirs.substring( stringForSubdirsLength - 1, stringForSubdirsLength );

		File rootDirForPrefixAndVersion = new File( cacheFilesRootDir, rootDirForPrefixAndVersionString );
		if ( ! rootDirForPrefixAndVersion.exists() ) {
			if ( calledFrom == CalledFrom.FROM_retrieveCachedDataFileContents ) {
				return null;  //  EARLY EXIT
			}
			//  For Save Contents, create this directory here to ensure this user account can create this directory
			if ( ! rootDirForPrefixAndVersion.mkdir() ) {
				String msg = "Failed to make subdir: " + rootDirForPrefixAndVersion.getAbsolutePath();
				log.error( msg );
				throw new ProxlWebappConfigException( msg );
			}
		}

		File subdir_1 = new File( rootDirForPrefixAndVersion, subdir_1_String );
		File subdir_2 = new File( subdir_1, subdir_2_String );

		File cachedDataFile = new File( subdir_2, filename );
		File cachedDataFileRequestURLPartialFile = new File( subdir_2, filenameRequestURLPartialFileString );
		File cachedDataFileDoneFile = new File( subdir_2, filenameDoneFileString );

		GetCachedDataFileAndDoneFileResult result = new GetCachedDataFileAndDoneFileResult();
		
		result.cachedDataFile = cachedDataFile;
		result.cachedDataFileRequestURLPartialFile = cachedDataFileRequestURLPartialFile;
		result.requestUrlPartialData = requestIdentifierBytes;
		result.cachedDataFileDoneFile = cachedDataFileDoneFile;
		result.subdir_1 = subdir_1;
		result.subdir_2 = subdir_2;
		
		return result;
	}
	
	/**
	 * @param requestURLPartial
	 * @param ids
	 * @param idParamType
	 * @return
	 * @throws Exception 
	 */
	private String getCacheFilename( byte[] requestIdentifierBytes, List<Integer> ids, IdParamType idParamType ) throws Exception {
		
		StringBuilder filenameSB = new StringBuilder( 1000 );
		
		if ( idParamType == IdParamType.SEARCH_ID ) {
			filenameSB.append( CACHE_FILENAME_PREFIX_FOR_SEARCH_ID );
		} else if ( idParamType == IdParamType.PROJECT_SEARCH_ID ) {
			filenameSB.append( CACHE_FILENAME_PREFIX_FOR_PROJECT_SEARCH_ID );
		} else if ( idParamType == IdParamType.SCAN_FILE_ID ) {
			filenameSB.append( CACHE_FILENAME_PREFIX_FOR_SCAN_FILE_ID );
		} else {
			String msg = "Unknown value for idParamType: " + idParamType.toString();
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
		
		for ( Integer id : ids ) {
			filenameSB.append( String.valueOf( id ) );
			filenameSB.append( "_" );
		}
		
		// get 
		
		MessageDigest md_SHA_384 = MessageDigest.getInstance( SHA_384_ALGORITHM );
		md_SHA_384.update( requestIdentifierBytes );
		byte[] requestURLPartialDigest = md_SHA_384.digest();

		String requestURLPartialDigestHexString = hashBytesToHexString( requestURLPartialDigest );
		
		filenameSB.append( requestURLPartialDigestHexString );
		
		String filename = filenameSB.toString();
		
		return filename;
	}

	/**
	 * @param hashBytes
	 * @return
	 */
	private String hashBytesToHexString( byte[] hashBytes ) {

		StringBuilder hashBytesAsHexSB = new StringBuilder( hashBytes.length * 2 + 2 );

		for ( int i = 0; i < hashBytes.length; i++ ) {
			String byteAsHex = Integer.toHexString( Byte.toUnsignedInt( hashBytes[ i ] ) );
			if ( byteAsHex.length() == 1 ) {
				hashBytesAsHexSB.append( "0" ); //  Leading zero dropped by 'toHexString' so add here
			}
			hashBytesAsHexSB.append( byteAsHex );
		}

		String result = hashBytesAsHexSB.toString();

		return result;
		
		//  WAS - which is equivalent, except for the added "0" when a hex pair starts with "0"
		
		//convert the byte to hex format
//		StringBuffer sb = new StringBuffer("");
//		for (int i = 0; i < hashBytes.length; i++) {
//			sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
//		}
//		
//		String result = sb.toString();
//		
//		return result;
	}
	
	
	
	/**
	 * The namePrefix and version are used for the root subdirectory for this particular file.
	 * 
	 * @param namePrefix
	 * @param versionAsString
	 * @return
	 */
	private String getRootDirForPrefixAndVersionString( String namePrefix, String versionAsString ) {

		String rootDirForPrefixAndVersionString = namePrefix + "_v_" + versionAsString;
		return rootDirForPrefixAndVersionString;
	}
	
	/**
	 * @param version
	 * @return
	 */
	private String getVersionAsString( int version ) {
		String versionAsString = decimalFormatZeroFill.format( version );
		return versionAsString;
	}


}
