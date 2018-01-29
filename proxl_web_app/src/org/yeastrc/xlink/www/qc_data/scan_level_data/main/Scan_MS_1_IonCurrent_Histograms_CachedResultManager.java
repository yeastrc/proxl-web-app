package org.yeastrc.xlink.www.qc_data.scan_level_data.main;

import java.util.ArrayList;
import java.util.List;

//import org.apache.log4j.Logger;

import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;

/**
 * 
 * Manage Cached results for Scan_MS_1_IonCurrent_Histograms
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Added to CachedDataInFileMgmtRegistration to register it
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
	public byte[] retrieveDataFromCache( int scanFileId, String requestQueryString ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}

		List<Integer> ids = new ArrayList<>( 1 );
		ids.add( scanFileId );
		
		byte[] resultJSONAsBytes = 
				CachedDataInFileMgmt.getSingletonInstance()
				.retrieveCachedDataFileContents(
						PREFIX_FOR_CACHING /* namePrefix */, 
						Scan_MS_1_IonCurrent_Histograms.VERSION_FOR_CACHING /* version */, 
						requestQueryString, 
						ids,
						IdParamType.SCAN_FILE_ID );
		return resultJSONAsBytes;
	}
	
	/**
	 * @param scanFileId
	 * @param imageAsBytes
	 * @throws Exception
	 */
	public void saveDataToCache( int scanFileId, byte[] resultJSONAsBytes, String requestQueryString ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return;  //  EARLY EXIT
		}

		List<Integer> ids = new ArrayList<>( 1 );
		ids.add( scanFileId );
		
		CachedDataInFileMgmt.getSingletonInstance()
		.saveCachedDataFileContents(
				resultJSONAsBytes, 
				ReplaceExistingValue.NO,
				PREFIX_FOR_CACHING /* namePrefix */, 
				Scan_MS_1_IonCurrent_Histograms.VERSION_FOR_CACHING /* version */, 
				requestQueryString, 
				ids,
				IdParamType.SCAN_FILE_ID );
		
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
