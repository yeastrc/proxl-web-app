package org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.peptide_page;

import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;


/**
 * 
 * Manage Cached results for PeptidePage_MultipleSearches_MainDisplayService
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Added to CachedDataInFileMgmtRegistration to register it
 * 
 * Singleton
 */
public class PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager implements CachedDataInFileMgmtRegistrationIF {

	private static final Logger log = LoggerFactory.getLogger(  PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager.class );
	
	private static final String PREFIX_FOR_CACHING = "PeptidePage_MultipleSearches_MainDisplayService_";

	static final int VERSION_FOR_CACHING_FROM_MAIN_CLASS = PeptidePage_MultipleSearches_MainDisplayService.VERSION_FOR_CACHING;
	
	
	private static final PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager instance = new PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager();

	// private constructor
	private PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager() {}

	/**
	 * @return Singleton instance
	 */
	public static PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager getSingletonInstance() {
		return instance;
	}
	
	public static class PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager_Result {
		private byte[] webserviceResponseJSONAsBytes;
		public byte[] getWebserviceResponseJSONAsBytes() {
			return webserviceResponseJSONAsBytes;
		}
		public void setWebserviceResponseJSONAsBytes(byte[] webserviceResponseJSONAsBytes) {
			this.webserviceResponseJSONAsBytes = webserviceResponseJSONAsBytes;
		}
	}

	/**
	 * @param projectSearchIds
	 * @param requestQueryString
	 * @return
	 * @throws Exception
	 */
	public PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager_Result retrieveDataFromCache( 
			List<Integer> projectSearchIds, 
			byte[] requestIdentifierBytes ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}
						
		PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager_Result result = new PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager_Result();
		
		result.webserviceResponseJSONAsBytes =
				CachedDataInFileMgmt.getSingletonInstance().retrieveCachedDataFileContents( 
						PREFIX_FOR_CACHING /* namePrefix */, 
						VERSION_FOR_CACHING_FROM_MAIN_CLASS /* version */, 
						requestIdentifierBytes, 
						projectSearchIds /* ids */,
						IdParamType.PROJECT_SEARCH_ID );
				
		return result;
	}

	/**
	 * @param projectSearchIds
	 * @param webserviceResponseJSONAsBytes
	 * @param requestQueryString
	 * @throws Exception
	 */
	public void saveDataToCache( 
			List<Integer> projectSearchIds, 
			byte[] webserviceResponseJSONAsBytes, 
			byte[] requestIdentifierBytes ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return;  //  EARLY EXIT
		}

		CachedDataInFileMgmt.getSingletonInstance().saveCachedDataFileContents( 
				webserviceResponseJSONAsBytes,
				ReplaceExistingValue.NO,
				PREFIX_FOR_CACHING /* namePrefix */, 
				VERSION_FOR_CACHING_FROM_MAIN_CLASS /* version */, 
				requestIdentifierBytes, 
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
