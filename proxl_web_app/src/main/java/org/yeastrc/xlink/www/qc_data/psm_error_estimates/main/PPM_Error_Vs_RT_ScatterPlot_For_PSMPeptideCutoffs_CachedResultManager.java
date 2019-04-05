package org.yeastrc.xlink.www.qc_data.psm_error_estimates.main;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;

/**
 * 
 * Manage Cached results for PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Added to CachedDataInFileMgmtRegistration to register it
 * 
 * Singleton
 */
public class PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager implements CachedDataInFileMgmtRegistrationIF {

	private static final Logger log = LoggerFactory.getLogger(  PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager.class );
	
	private static final String PREFIX_FOR_CACHING = "PPM_Error_Vs_RT_ScatterPlot_";
	
	static final int VERSION_FOR_CACHING_FROM_MAIN_CLASS = PPM_Error_Histogram_For_PSMPeptideCutoffs.VERSION_FOR_CACHING;

	private static final PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager instance = new PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager();

	// private constructor
	private PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager() {}

	/**
	 * @return Singleton instance
	 */
	public static PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager getSingletonInstance() {
		return instance;
	}
	
	public static class PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager_Result {
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
	 * @param requestJSONBytes
	 * @return
	 * @throws Exception
	 */
	public PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager_Result retrieveDataFromCache( int projectSearchId, byte[] requestJSONBytes ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}
		
		List<Integer> ids = new ArrayList<>( 1 );
		ids.add( projectSearchId );
		
		PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager_Result result = new PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager_Result();
		
		result.chartJSONAsBytes =
				CachedDataInFileMgmt.getSingletonInstance().retrieveCachedDataFileContents( 
						PREFIX_FOR_CACHING /* namePrefix */, 
						VERSION_FOR_CACHING_FROM_MAIN_CLASS /* version */, 
						requestJSONBytes, 
						ids,
						IdParamType.PROJECT_SEARCH_ID );
				
		return result;
	}
	
	/**
	 * @param projectSearchId
	 * @param chartJSONAsBytes
	 * @param requestJSONBytes
	 * @throws Exception
	 */
	public void saveDataToCache( int projectSearchId, byte[] chartJSONAsBytes, byte[] requestJSONBytes ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return;  //  EARLY EXIT
		}

		List<Integer> ids = new ArrayList<>( 1 );
		ids.add( projectSearchId );
		
		CachedDataInFileMgmt.getSingletonInstance().saveCachedDataFileContents( 
				chartJSONAsBytes,
				ReplaceExistingValue.NO,
				PREFIX_FOR_CACHING /* namePrefix */, 
				VERSION_FOR_CACHING_FROM_MAIN_CLASS /* version */, 
				requestJSONBytes, 
				ids,
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
