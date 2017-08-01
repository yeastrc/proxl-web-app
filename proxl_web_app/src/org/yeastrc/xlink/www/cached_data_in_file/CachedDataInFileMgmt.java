package org.yeastrc.xlink.www.cached_data_in_file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.www.async_action_via_executor_service.AsyncActionViaExecutorService;
import org.yeastrc.xlink.www.async_action_via_executor_service.AsyncItemToRun;
import org.yeastrc.xlink.www.async_action_via_executor_service.AsyncItemToRunFactory;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.exceptions.ProxlWebappConfigException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
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

	private static final Logger log = Logger.getLogger(CachedDataInFileMgmt.class);

	private static final String CACHE_FILES_ROOT_DIRECTORY = ConfigSystemsKeysSharedConstants.CACHE_FILES_ROOT_DIRECTORY;

	private static final String idPrefixWhenOneChar = "_";
	
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
	
    private final DecimalFormat decimalFormatZeroFill = new DecimalFormat("00000");
    
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
	

	/**
	 * Get the byte[] of the cached data, if it exists.
	 * 
	 * namePrefix and version are used for grouping cached data and deleting cached data that is no longer used.
	 *   (Currently that amounts to putting them under the same subdirectory)
	 * 
	 * For specifying a specific cached data item, either namePrefix and id or fullIdentifier is used.
	 * 
	 * namePrefix and version are required.  fullIdentifier or id is required.
	 * If fullIdentifier and id are provided, id is used for splitting files into subdirectories.
	 * If id is not provided, the last 2 characters of fullIdentifier are used for splitting files into subdirectories.
	 * 
	 * @param namePrefix
	 * @param version
	 * @param fullIdentifier
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public byte[] retrieveCachedDataFileContents( 
			String namePrefix,
			int version,
			String fullIdentifier,
			String id 
			 ) throws Exception {

		GetCachedDataFileAndDoneFileResult getCachedDataFileAndDoneFileResult =
				getCachedDataFileAndDoneFile( namePrefix, version, fullIdentifier, id, CalledFrom.FROM_retrieveCachedDataFileContents );

		if ( getCachedDataFileAndDoneFileResult == null ) {
			//  Not configured or the subdirectory does not exist
			return null; //  Early Exit
		}
		
		File cachedDataFile = getCachedDataFileAndDoneFileResult.cachedDataFile;
		File cachedDataFileDoneFile = getCachedDataFileAndDoneFileResult.cachedDataFileDoneFile;

		if ( ! cachedDataFileDoneFile.exists() ) {
			//  The 'done' file does not exist
			return null; //  Early Exit
		}
		if ( ! cachedDataFile.exists() ) {
			//  The file does not exist
			return null; //  Early Exit
		}
		
		long cachedDataFileLength = cachedDataFile.length();
		if ( cachedDataFileLength > Integer.MAX_VALUE ) {
			String msg = "Cached file length >  so unable to read into byte[]. length: " + cachedDataFileLength 
					+ ", file: " + cachedDataFile.getCanonicalPath();
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}
		
		byte[] cachedDataFileContents = new byte[ (int)cachedDataFileLength ];
		FileInputStream cachedDataFileInputStream = null;
		try {
			cachedDataFileInputStream = new FileInputStream(cachedDataFile);
			int bytesRead = cachedDataFileInputStream.read( cachedDataFileContents );
			if ( bytesRead != cachedDataFileLength ) {
				String msg = "bytesRead != cachedDataFileLength. length: " + cachedDataFileLength 
						+ ", file: " + cachedDataFile.getCanonicalPath();
				log.error( msg );
				throw new ProxlWebappDataException(msg);
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
	 * For specifying a specific cached data item, either namePrefix and id or fullIdentifier is used.
	 * 
	 * namePrefix and version are required.  fullIdentifier or id is required.
	 * If fullIdentifier and id are provided, id is used for splitting files into subdirectories.
	 * If id is not provided, the last 2 characters of fullIdentifier are used for splitting files into subdirectories.
	 * 
	 * @param dataToSave
	 * @param namePrefix
	 * @param version
	 * @param fullIdentifier
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean saveCachedDataFileContents( 
			byte[] dataToSave,
			String namePrefix, 
			int version,
			String fullIdentifier,
			String id
			) throws Exception {

		GetCachedDataFileAndDoneFileResult getCachedDataFileAndDoneFileResult =
				getCachedDataFileAndDoneFile( namePrefix, version, fullIdentifier, id, CalledFrom.FROM_saveCachedDataFileContentsFor_NamePrefix_Id_Version );

		if ( getCachedDataFileAndDoneFileResult == null ) {
			//  Not configured or the configured subdirectory does not exist
			return false; //  Early Exit
		}
		
		File cachedDataFileDoneFile = getCachedDataFileAndDoneFileResult.cachedDataFileDoneFile;

		//  If already exists, assume it is the same value and exit
		if ( cachedDataFileDoneFile.exists() ) {
			return true; //  Early Exit
		}
		
		SaveCachedDataFileContentsActualParameters saveCachedDataFileContentsActualParameters = null;
		synchronized ( this ) {
			saveCachedDataFileContentsActualParameters = new SaveCachedDataFileContentsActualParameters();
			saveCachedDataFileContentsActualParameters.dataToSave = dataToSave;
			saveCachedDataFileContentsActualParameters.cachedDataFile = getCachedDataFileAndDoneFileResult.cachedDataFile;
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
	 * @param namePrefix
	 * @param version
	 * @param fullIdentifier
	 * @param id
	 * @param createSubdirsIfNotExist
	 * @return 
	 * @throws Exception 
	 */
	private GetCachedDataFileAndDoneFileResult getCachedDataFileAndDoneFile( 
			String namePrefix,
			int version,
			String fullIdentifier,
			String id,
			CalledFrom createSubdirsIfNotExist ) throws Exception {
		
		if ( StringUtils.isEmpty( namePrefix ) ) {
			String msg = "namePrefix cannot be null or empty";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}

		if ( StringUtils.isEmpty( fullIdentifier )
				&& ( StringUtils.isEmpty( id ) ) ) {
			String msg = "fullIdentifier and id cannot both be null or empty";
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
		
		//  The namePrefix and version are used for the root subdirectory for this particular file.
		//  The last 2 characters of the id as string are used for a subdirectory path to limit the number of files in a subdirectory.
		
		String versionAsString = decimalFormatZeroFill.format( version );

		String rootDirForPrefixAndVersionString = namePrefix + "_v_" + versionAsString;

		String filename = null;
		String stringForSubdirs = null;
		
		if ( StringUtils.isNotEmpty( fullIdentifier ) ) {

			filename = fullIdentifier;
			stringForSubdirs = fullIdentifier;
		} else {
			filename =  namePrefix + "_id_" + id + "_v_" + versionAsString;
			stringForSubdirs = id;

			//  prefix id if only 1 character long
			if ( stringForSubdirs.length() == 1 ) {
				stringForSubdirs = idPrefixWhenOneChar + stringForSubdirs;
			}
		}
		
		String filenameDoneFileString = filename + DONE_FILE_SUFFIX;
		
		int stringForSubdirsLength = stringForSubdirs.length();

		//  sub dirs to split files to limit size of directories
		
		//  Next to last character in id string
		String subdir_1_String = stringForSubdirs.substring( stringForSubdirsLength - 2, stringForSubdirsLength - 1 );
		//  Last character in id string
		String subdir_2_String = stringForSubdirs.substring( stringForSubdirsLength - 1, stringForSubdirsLength );

		File rootDirForPrefixAndVersion = new File( cacheFilesRootDir, rootDirForPrefixAndVersionString );
		if ( ! rootDirForPrefixAndVersion.exists() ) {
			if ( createSubdirsIfNotExist == CalledFrom.FROM_retrieveCachedDataFileContents ) {
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
		File cachedDataFileDoneFile = new File( subdir_2, filenameDoneFileString );

		GetCachedDataFileAndDoneFileResult result = new GetCachedDataFileAndDoneFileResult();
		
		result.cachedDataFile = cachedDataFile;
		result.cachedDataFileDoneFile = cachedDataFileDoneFile;
		result.subdir_1 = subdir_1;
		result.subdir_2 = subdir_2;
		
		return result;
	}
	
	
}
