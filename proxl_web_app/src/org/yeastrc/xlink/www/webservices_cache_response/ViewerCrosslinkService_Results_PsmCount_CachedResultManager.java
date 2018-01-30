package org.yeastrc.xlink.www.webservices_cache_response;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;
import org.yeastrc.xlink.www.webservices.ViewerCrosslinkService;


/**
 * 
 * Manage Cached results for ViewerCrosslinkService - PSM Counts
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Added to CachedDataInFileMgmtRegistration to register it
 * 
 * Singleton
 */
public class ViewerCrosslinkService_Results_PsmCount_CachedResultManager implements CachedDataInFileMgmtRegistrationIF {

	private static final Logger log = Logger.getLogger( ViewerCrosslinkService_Results_PsmCount_CachedResultManager.class );
		
	private static final String PREFIX_FOR_CACHING = "ViewerCrosslinkService_PsmCount_";

	static final int VERSION_FOR_CACHING_FROM_MAIN_CLASS = ViewerCrosslinkService.VERSION_FOR_CACHING;
	
	
	private static final ViewerCrosslinkService_Results_PsmCount_CachedResultManager instance = new ViewerCrosslinkService_Results_PsmCount_CachedResultManager();

	// private constructor
	private ViewerCrosslinkService_Results_PsmCount_CachedResultManager() {}

	/**
	 * @return Singleton instance
	 */
	public static ViewerCrosslinkService_Results_PsmCount_CachedResultManager getSingletonInstance() {
		return instance;
	}
	
	public static class ViewerCrosslinkService_Results_PsmCount_CachedResultManager_Result {
		private byte[] chartJSONAsBytes;
		public byte[] getChartJSONAsBytes() {
			return chartJSONAsBytes;
		}
		public void setChartJSONAsBytes(byte[] chartJSONAsBytes) {
			this.chartJSONAsBytes = chartJSONAsBytes;
		}
	}

	/**
	 * @param projectSearchIds
	 * @param requestQueryString
	 * @return
	 * @throws Exception
	 */
	public ViewerCrosslinkService_Results_PsmCount_CachedResultManager_Result retrieveDataFromCache( 
			List<Integer> projectSearchIds, 
			String requestQueryString ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}
		
		ViewerCrosslinkService_Results_PsmCount_CachedResultManager_Result result = new ViewerCrosslinkService_Results_PsmCount_CachedResultManager_Result();
		
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
	 * @param projectSearchIds
	 * @param chartJSONAsBytes
	 * @param requestQueryString
	 * @throws Exception
	 */
	public void saveDataToCache( 
			List<Integer> projectSearchIds, 
			byte[] chartJSONAsBytes, 
			String requestQueryString ) throws Exception {
		
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
		
		//  Register for Both
		{
			CachedDataInFileMgmtRegistration.getSingletonInstance()
			.register( PREFIX_FOR_CACHING, VERSION_FOR_CACHING_FROM_MAIN_CLASS );
		}
		
	}

}
