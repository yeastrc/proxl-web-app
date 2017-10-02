package org.yeastrc.xlink.www.qc_data.scan_level_data.main;

//import org.apache.log4j.Logger;

import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;

/**
 * 
 * Manage Cached results for Scan_MS_1_IonCurrent_Histograms
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Singleton
 */
public class Scan_MS_1_IonCurrent_Histograms_CachedResultManager implements CachedDataInFileMgmtRegistrationIF {

//	private static final Logger log = Logger.getLogger( Scan_MS_1_IonCurrent_Histograms_CachedResultManager.class );

	private static final String PREFIX_FOR_CACHING = "MS1_All_Histograms_RT_MZ_";

	private static final Scan_MS_1_IonCurrent_Histograms_CachedResultManager instance = new Scan_MS_1_IonCurrent_Histograms_CachedResultManager();

	// private constructor
	private Scan_MS_1_IonCurrent_Histograms_CachedResultManager() {}

	/**
	 * @return Singleton instance
	 */
	public static Scan_MS_1_IonCurrent_Histograms_CachedResultManager getSingletonInstance() {
		return instance;
	}
	

	/**
	 * @param scanFileId
	 * @param requestedImageWidth
	 * @param imageAsBytes
	 * @throws Exception
	 */
	public byte[] retrieveDataFromCache( int scanFileId ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}
		byte[] resultJSONAsBytes = 
				CachedDataInFileMgmt.getSingletonInstance()
				.retrieveCachedDataFileContents(
						PREFIX_FOR_CACHING /* namePrefix */, 
						Scan_MS_1_IonCurrent_Histograms.VERSION_FOR_CACHING /* version */, 
						null /* fullIdentifier */, 
						Integer.toString( scanFileId ) /* id */ );
		return resultJSONAsBytes;
	}
	
	/**
	 * @param scanFileId
	 * @param imageAsBytes
	 * @throws Exception
	 */
	public void saveDataToCache( int scanFileId, byte[] resultJSONAsBytes ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return;  //  EARLY EXIT
		}
		
		CachedDataInFileMgmt.getSingletonInstance()
		.saveCachedDataFileContents(
				resultJSONAsBytes, 
				PREFIX_FOR_CACHING /* namePrefix */, 
				Scan_MS_1_IonCurrent_Histograms.VERSION_FOR_CACHING /* version */, 
				null /* fullIdentifier */, 
				Integer.toString( scanFileId ) /* id */ );
		
	}

	/* 
	 * Called from CachedDataInFileMgmtRegistration
	 * 
	 * Register all prefixes and version used with CachedDataInFileMgmt
	 */
	@Override
	public void register() throws Exception {
		
		//  Register for full width
		{
			CachedDataInFileMgmtRegistration.getSingletonInstance()
			.register( PREFIX_FOR_CACHING, Scan_MS_1_IonCurrent_Histograms.VERSION_FOR_CACHING );
		}
		
	}
	
}
