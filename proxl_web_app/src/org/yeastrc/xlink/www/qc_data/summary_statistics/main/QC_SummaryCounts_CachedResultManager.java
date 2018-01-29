package org.yeastrc.xlink.www.qc_data.summary_statistics.main;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;

/**
 * 
 * Manage Cached results for QC_SummaryCounts
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Added to CachedDataInFileMgmtRegistration to register it
 * 
 * Singleton
 */
public class QC_SummaryCounts_CachedResultManager implements CachedDataInFileMgmtRegistrationIF {


	private static final Logger log = Logger.getLogger( QC_SummaryCounts_CachedResultManager.class );
	
	private static final String PREFIX_FOR_CACHING = "QC_Summary_";

	static final int VERSION_FOR_CACHING_FROM_MAIN_CLASS = QC_SummaryCounts.VERSION_FOR_CACHING;
	
	
	private static final QC_SummaryCounts_CachedResultManager instance = new QC_SummaryCounts_CachedResultManager();

	// private constructor
	private QC_SummaryCounts_CachedResultManager() {}

	/**
	 * @return Singleton instance
	 */
	public static QC_SummaryCounts_CachedResultManager getSingletonInstance() {
		return instance;
	}
	
	public static class QC_SummaryCounts_CachedResultManager_Result {
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
	public QC_SummaryCounts_CachedResultManager_Result retrieveDataFromCache( int projectSearchId, String requestQueryString ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}
		
		List<Integer> ids = new ArrayList<>( 1 );
		ids.add( projectSearchId );
		
		QC_SummaryCounts_CachedResultManager_Result result = new QC_SummaryCounts_CachedResultManager_Result();
		
		result.chartJSONAsBytes =
				CachedDataInFileMgmt.getSingletonInstance().retrieveCachedDataFileContents( 
						PREFIX_FOR_CACHING /* namePrefix */, 
						VERSION_FOR_CACHING_FROM_MAIN_CLASS /* version */, 
						requestQueryString, 
						ids,
						IdParamType.PROJECT_SEARCH_ID );
				
		return result;
	}
	
	/**
	 * @param projectSearchId
	 * @param imageAsBytes
	 * @throws Exception
	 */
	public void saveDataToCache( int projectSearchId, byte[] chartJSONAsBytes, String requestQueryString ) throws Exception {
		
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
				requestQueryString, 
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
