package org.yeastrc.xlink.www.qc_data.psm_error_estimates_merged.main;

import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;

/**
 * 
 * Manage Cached results for PPM_Error_Chart_For_PSMPeptideCutoffs_Merged
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Added to CachedDataInFileMgmtRegistration to register it
 * 
 * Singleton
 */
public class PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager implements CachedDataInFileMgmtRegistrationIF {

	private static final Logger log = LoggerFactory.getLogger(  PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager.class );
	

	private static final String PREFIX_FOR_CACHING = "QC_PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_";

	static final int VERSION_FOR_CACHING_FROM_MAIN_CLASS = PPM_Error_Chart_For_PSMPeptideCutoffs_Merged.VERSION_FOR_CACHING;
	
	
	private static final PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager instance = new PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager();

	// private constructor
	private PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager() {}

	/**
	 * @return Singleton instance
	 */
	public static PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager getSingletonInstance() {
		return instance;
	}
	
	public static class PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager_Result {
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
	 * @param requestJSONBytes
	 * @return
	 * @throws Exception
	 */
	public PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager_Result retrieveDataFromCache( List<Integer> projectSearchIds, byte[] requestJSONBytes ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}
				
		PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager_Result result = new PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager_Result();
		
		result.chartJSONAsBytes =
				CachedDataInFileMgmt.getSingletonInstance().retrieveCachedDataFileContents( 
						PREFIX_FOR_CACHING /* namePrefix */, 
						VERSION_FOR_CACHING_FROM_MAIN_CLASS /* version */, 
						requestJSONBytes, 
						projectSearchIds /* ids */,
						IdParamType.PROJECT_SEARCH_ID );
				
		return result;
	}
	
	/**
	 * @param projectSearchIds
	 * @param chartJSONAsBytes
	 * @param requestJSONBytes
	 * @throws Exception
	 */
	public void saveDataToCache( List<Integer> projectSearchIds, byte[] chartJSONAsBytes, byte[] requestJSONBytes ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return;  //  EARLY EXIT
		}

		CachedDataInFileMgmt.getSingletonInstance().saveCachedDataFileContents( 
				chartJSONAsBytes,
				ReplaceExistingValue.NO,
				PREFIX_FOR_CACHING /* namePrefix */, 
				VERSION_FOR_CACHING_FROM_MAIN_CLASS /* version */, 
				requestJSONBytes, 
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
