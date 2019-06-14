package org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.protein_and_coverage_pages;

import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;


/**
 * 
 * Manage Cached results for Protein_AllProteins_SingleSearch_PageData_Webservice
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Added to CachedDataInFileMgmtRegistration to register it
 * 
 * Singleton
 */
public class Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager implements CachedDataInFileMgmtRegistrationIF {

	private static final Logger log = LoggerFactory.getLogger(  Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager.class );
	
	private static final String PREFIX_FOR_CACHING = "ProteinsAll_SingleSearch_PageMainDisplayService_";

	static final int VERSION_FOR_CACHING_FROM_MAIN_CLASS = Protein_AllProteins_SingleSearch_PageData_Webservice.VERSION_FOR_CACHING;
	
	
	private static final Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager instance = new Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager();

	// private constructor
	private Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager() {}

	/**
	 * @return Singleton instance
	 */
	public static Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager getSingletonInstance() {
		return instance;
	}
	
	public static class Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager_Result {
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
	public Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager_Result retrieveDataFromCache( 
			List<Integer> projectSearchIds, 
			byte[] requestIdentifierBytes ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}
						
		Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager_Result result = new Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager_Result();
		
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
