package org.yeastrc.xlink.www.qc_data.psm_error_estimates.main;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;

/**
 * 
 * Manage Cached results for PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Singleton
 */
public class PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager implements CachedDataInFileMgmtRegistrationIF {

	private static final Logger log = Logger.getLogger( PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager.class );
	
	private static final String PREFIX_FOR_CACHING = "PPM_Error_Vs_M_over_Z_ScatterPlot_";

	private static final PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager instance = new PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager();

	// private constructor
	private PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager() {}

	/**
	 * @return Singleton instance
	 */
	public static PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager getSingletonInstance() {
		return instance;
	}
	
	public static class PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager_Result {
		private byte[] chartJSONAsBytes;
		public byte[] getChartJSONAsBytes() {
			return chartJSONAsBytes;
		}
		public void setChartJSONAsBytes(byte[] chartJSONAsBytes) {
			this.chartJSONAsBytes = chartJSONAsBytes;
		}
	}

	/**
	 * @param searchId
	 * @param requestedImageWidth
	 * @param imageAsBytes
	 * @throws Exception
	 */
	public PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager_Result retrieveDataFromCache( int searchId, String requestQueryString ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}
		
		List<Integer> ids = new ArrayList<>( 1 );
		ids.add( searchId );
		
		PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager_Result result = new PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager_Result();
		
		result.chartJSONAsBytes =
				CachedDataInFileMgmt.getSingletonInstance().retrieveCachedDataFileContents( 
						PREFIX_FOR_CACHING /* namePrefix */, 
						PPM_Error_Histogram_For_PSMPeptideCutoffs.VERSION_FOR_CACHING /* version */, 
						requestQueryString, 
						ids,
						IdParamType.SEARCH_ID );
				
		return result;
	}
	
	/**
	 * @param searchId
	 * @param imageAsBytes
	 * @throws Exception
	 */
	public void saveDataToCache( int searchId, byte[] chartJSONAsBytes, String requestQueryString ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return;  //  EARLY EXIT
		}

		List<Integer> ids = new ArrayList<>( 1 );
		ids.add( searchId );
		
		CachedDataInFileMgmt.getSingletonInstance().saveCachedDataFileContents( 
				chartJSONAsBytes,
				ReplaceExistingValue.NO,
				PREFIX_FOR_CACHING /* namePrefix */, 
				PPM_Error_Histogram_For_PSMPeptideCutoffs.VERSION_FOR_CACHING /* version */, 
				requestQueryString, 
				ids,
				IdParamType.SEARCH_ID );
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
			.register( PREFIX_FOR_CACHING, PPM_Error_Histogram_For_PSMPeptideCutoffs.VERSION_FOR_CACHING );
		}
		
	}

}
