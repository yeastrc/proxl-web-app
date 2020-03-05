package org.yeastrc.xlink.www.qc_data.scan_level_data.main;

import java.util.ArrayList;
import java.util.List;

//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;


//*  !!!  Skip Caching for now since result will change when mzML files are reprocessed to latest data file format version 
//*         and data needed for these charts will be available.


/**
 * 
 * Manage Cached results for Scan_MS_AllScans_AllButPeaks
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Added to CachedDataInFileMgmtRegistration to register it
 * 
 * Singleton
 */
public class Scan_MS_AllScans_AllButPeaks_CachedResultManager { //  implements CachedDataInFileMgmtRegistrationIF {

//	private static final Logger log = LoggerFactory.getLogger(  XXX.class );

//	private static final String PREFIX_FOR_CACHING = "Scan_MS_AllScans_AllButPeaks_";
//
//	static final int VERSION_FOR_CACHING = Scan_MS_AllScans_AllButPeaks.VERSION_FOR_CACHING;
//	
//	
//	private static final Scan_MS_AllScans_AllButPeaks_CachedResultManager instance = new Scan_MS_AllScans_AllButPeaks_CachedResultManager();
//
//	// private constructor
//	private Scan_MS_AllScans_AllButPeaks_CachedResultManager() {}
//
//	/**
//	 * @return Singleton instance
//	 */
//	public static Scan_MS_AllScans_AllButPeaks_CachedResultManager getSingletonInstance() {
//		return instance;
//	}
//	
//
//	/**
//	 * @param scanFileId
//	 * @param requestedImageWidth
//	 * @param imageAsBytes
//	 * @throws Exception
//	 */
//	public byte[] retrieveDataFromCache( int scanFileId, byte[] requestJSONBytes ) throws Exception {
//		
//		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
//			return null;  //  EARLY EXIT
//		}
//
//		List<Integer> ids = new ArrayList<>( 1 );
//		ids.add( scanFileId );
//		
//		byte[] resultJSONAsBytes = 
//				CachedDataInFileMgmt.getSingletonInstance()
//				.retrieveCachedDataFileContents(
//						PREFIX_FOR_CACHING /* namePrefix */, 
//						VERSION_FOR_CACHING /* version */, 
//						requestJSONBytes, 
//						ids,
//						IdParamType.SCAN_FILE_ID );
//		return resultJSONAsBytes;
//	}
//	
//	/**
//	 * @param scanFileId
//	 * @param imageAsBytes
//	 * @throws Exception
//	 */
//	public void saveDataToCache( int scanFileId, byte[] resultJSONAsBytes, byte[] requestJSONBytes ) throws Exception {
//		
//		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
//			return;  //  EARLY EXIT
//		}
//
//		List<Integer> ids = new ArrayList<>( 1 );
//		ids.add( scanFileId );
//		
//		CachedDataInFileMgmt.getSingletonInstance()
//		.saveCachedDataFileContents(
//				resultJSONAsBytes, 
//				ReplaceExistingValue.NO,
//				PREFIX_FOR_CACHING /* namePrefix */, 
//				VERSION_FOR_CACHING /* version */, 
//				requestJSONBytes, 
//				ids,
//				IdParamType.SCAN_FILE_ID );
//		
//	}
//
//	/* 
//	 * Called from CachedDataInFileMgmtRegistration
//	 * 
//	 * Register all prefixes and version used with CachedDataInFileMgmt
//	 */
//	@Override
//	public void register() throws Exception {
//		
//		//  Register for full width
//		{
//			CachedDataInFileMgmtRegistration.getSingletonInstance()
//			.register( PREFIX_FOR_CACHING, VERSION_FOR_CACHING );
//		}
//		
//	}
	
}
