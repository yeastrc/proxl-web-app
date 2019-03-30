package org.yeastrc.xlink.www.webservices_cache_response;

import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;
import org.yeastrc.xlink.www.webservices.ViewerMonolinkService;


/**
 * 
 * Manage Cached results for ViewerMonolinkService Main
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Added to CachedDataInFileMgmtRegistration to register it
 * 
 * Singleton
 */
public class ViewerMonolinkService_Results_Main_CachedResultManager implements CachedDataInFileMgmtRegistrationIF {

	private static final Logger log = LoggerFactory.getLogger(  ViewerMonolinkService_Results_Main_CachedResultManager.class );
	
	private static final String PREFIX_FOR_CACHING = "ViewerMonolinkService_Main_";

	static final int VERSION_FOR_CACHING_FROM_MAIN_CLASS = ViewerMonolinkService.VERSION_FOR_CACHING;
	
	
	private static final ViewerMonolinkService_Results_Main_CachedResultManager instance = new ViewerMonolinkService_Results_Main_CachedResultManager();

	// private constructor
	private ViewerMonolinkService_Results_Main_CachedResultManager() {}

	/**
	 * @return Singleton instance
	 */
	public static ViewerMonolinkService_Results_Main_CachedResultManager getSingletonInstance() {
		return instance;
	}
	
	public static class ViewerMonolinkService_Results_Main_CachedResultManager_Result {
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
	public ViewerMonolinkService_Results_Main_CachedResultManager_Result retrieveDataFromCache( List<Integer> projectSearchIds, String requestQueryString ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}
				
		ViewerMonolinkService_Results_Main_CachedResultManager_Result result = new ViewerMonolinkService_Results_Main_CachedResultManager_Result();
		
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
