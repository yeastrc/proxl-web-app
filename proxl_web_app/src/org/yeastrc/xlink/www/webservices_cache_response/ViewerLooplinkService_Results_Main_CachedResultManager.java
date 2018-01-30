package org.yeastrc.xlink.www.webservices_cache_response;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;
import org.yeastrc.xlink.www.webservices.ViewerLooplinkService;


/**
 * 
 * Manage Cached results for ViewerLooplinkService Main
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Added to CachedDataInFileMgmtRegistration to register it
 * 
 * Singleton
 */
public class ViewerLooplinkService_Results_Main_CachedResultManager implements CachedDataInFileMgmtRegistrationIF {

	private static final Logger log = Logger.getLogger( ViewerLooplinkService_Results_Main_CachedResultManager.class );
	
	private static final String PREFIX_FOR_CACHING = "ViewerLooplinkService_Main_";

	static final int VERSION_FOR_CACHING_FROM_MAIN_CLASS = ViewerLooplinkService.VERSION_FOR_CACHING;
	
	
	private static final ViewerLooplinkService_Results_Main_CachedResultManager instance = new ViewerLooplinkService_Results_Main_CachedResultManager();

	// private constructor
	private ViewerLooplinkService_Results_Main_CachedResultManager() {}

	/**
	 * @return Singleton instance
	 */
	public static ViewerLooplinkService_Results_Main_CachedResultManager getSingletonInstance() {
		return instance;
	}
	
	public static class ViewerLooplinkService_Results_Main_CachedResultManager_Result {
		private byte[] chartJSONAsBytes;
		public byte[] getChartJSONAsBytes() {
			return chartJSONAsBytes;
		}
		public void setChartJSONAsBytes(byte[] chartJSONAsBytes) {
			this.chartJSONAsBytes = chartJSONAsBytes;
		}
	}

	/**
	 * @param projectSearchId
	 * @param requestedImageWidth
	 * @param imageAsBytes
	 * @throws Exception
	 */
	public ViewerLooplinkService_Results_Main_CachedResultManager_Result retrieveDataFromCache( List<Integer> projectSearchIds, String requestQueryString ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}
				
		ViewerLooplinkService_Results_Main_CachedResultManager_Result result = new ViewerLooplinkService_Results_Main_CachedResultManager_Result();
		
		result.chartJSONAsBytes =
				CachedDataInFileMgmt.getSingletonInstance().retrieveCachedDataFileContents( 
						PREFIX_FOR_CACHING /* namePrefix */, 
						VERSION_FOR_CACHING_FROM_MAIN_CLASS /* version */, 
						requestQueryString, 
						projectSearchIds /* ids */,
						IdParamType.PROJECT_SEARCH_ID );
				
		return result;
	}
	
	/**
	 * @param projectSearchId
	 * @param imageAsBytes
	 * @throws Exception
	 */
	public void saveDataToCache( List<Integer> projectSearchIds, byte[] chartJSONAsBytes, String requestQueryString ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return;  //  EARLY EXIT
		}
		
		CachedDataInFileMgmt.getSingletonInstance().saveCachedDataFileContents( 
				chartJSONAsBytes,
				ReplaceExistingValue.NO,
				PREFIX_FOR_CACHING /* namePrefix */, 
				VERSION_FOR_CACHING_FROM_MAIN_CLASS /* version */, 
				requestQueryString, 
				projectSearchIds /* ids */,
				IdParamType.PROJECT_SEARCH_ID );
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
			.register( PREFIX_FOR_CACHING, VERSION_FOR_CACHING_FROM_MAIN_CLASS );
		}
		
	}

}
