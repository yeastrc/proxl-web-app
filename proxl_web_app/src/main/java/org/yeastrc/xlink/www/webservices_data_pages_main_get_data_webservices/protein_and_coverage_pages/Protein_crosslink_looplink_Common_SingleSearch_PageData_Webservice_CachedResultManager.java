package org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.protein_and_coverage_pages;

import java.security.InvalidParameterException;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistration;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmtRegistrationIF;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.IdParamType;
import org.yeastrc.xlink.www.cached_data_in_file.CachedDataInFileMgmt.ReplaceExistingValue;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.protein_and_coverage_pages.Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice.CrosslinkLooplink;


/**
 * 
 * Manage Cached results for Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice
 * 
 * Cache bytes to Disk File using CachedDataInFileMgmt
 * 
 * Interfaces with the CachedDataInFileMgmt for saving and retrieving the result JSON for caching
 * 
 * Added to CachedDataInFileMgmtRegistration to register it
 * 
 * Singleton
 */
public class Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager implements CachedDataInFileMgmtRegistrationIF {

	private static final Logger log = LoggerFactory.getLogger(  Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager.class );
	
	private static final String PREFIX_FOR_CACHING = "ProteinPage_SingleSearch_MainDisplayService_";
	
	private static final String SUBPREFIX_CROSSLINKS_FOR_CACHING = "Crosslinks_";
	private static final String SUBPREFIX_LOOPLINKS_FOR_CACHING = "Looplinks_";
	

	private static final String PREFIX_SUBPREFIX_CROSSLINKS_FOR_CACHING = PREFIX_FOR_CACHING + SUBPREFIX_CROSSLINKS_FOR_CACHING;
	private static final String PREFIX_SUBPREFIX_LOOPLINKS_FOR_CACHING  = PREFIX_FOR_CACHING + SUBPREFIX_LOOPLINKS_FOR_CACHING;

	static final int VERSION_FOR_CACHING_FROM_MAIN_CLASS = Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice.VERSION_FOR_CACHING;
	
	
	private static final Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager instance = new Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager();

	// private constructor
	private Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager() {}

	/**
	 * @return Singleton instance
	 */
	public static Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager getSingletonInstance() {
		return instance;
	}
	
	public static class Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager_Result {
		private byte[] webserviceResponseJSONAsBytes;
		public byte[] getWebserviceResponseJSONAsBytes() {
			return webserviceResponseJSONAsBytes;
		}
		public void setWebserviceResponseJSONAsBytes(byte[] webserviceResponseJSONAsBytes) {
			this.webserviceResponseJSONAsBytes = webserviceResponseJSONAsBytes;
		}
	}

	/**
	 * @param crosslinkLooplink
	 * @param projectSearchIds
	 * @param requestQueryString
	 * @return
	 * @throws Exception
	 */
	public Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager_Result retrieveDataFromCache( 
			CrosslinkLooplink crosslinkLooplink,
			List<Integer> projectSearchIds, 
			byte[] requestIdentifierBytes ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return null;  //  EARLY EXIT
		}
						
		Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager_Result result = new Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager_Result();
		
		String prefixForCaching = null;
		
		if ( crosslinkLooplink == CrosslinkLooplink.CROSSLINK ) {
			prefixForCaching = PREFIX_SUBPREFIX_CROSSLINKS_FOR_CACHING;
		} else if ( crosslinkLooplink == CrosslinkLooplink.LOOPLINK ) {
			prefixForCaching = PREFIX_SUBPREFIX_LOOPLINKS_FOR_CACHING;
		} else {
			String msg = "Invalid value for crosslinkLooplink: " + crosslinkLooplink;
			log.error( msg );
			throw new InvalidParameterException(msg);
		}

		result.webserviceResponseJSONAsBytes =
				CachedDataInFileMgmt.getSingletonInstance().retrieveCachedDataFileContents( 
						prefixForCaching /* namePrefix */, 
						VERSION_FOR_CACHING_FROM_MAIN_CLASS /* version */, 
						requestIdentifierBytes, 
						projectSearchIds /* ids */,
						IdParamType.PROJECT_SEARCH_ID );
				
		return result;
	}

	/**
	 * @param crosslinkLooplink
	 * @param projectSearchIds
	 * @param webserviceResponseJSONAsBytes
	 * @param requestQueryString
	 * @throws Exception
	 */
	public void saveDataToCache( 
			CrosslinkLooplink crosslinkLooplink,
			List<Integer> projectSearchIds, 
			byte[] webserviceResponseJSONAsBytes, 
			byte[] requestIdentifierBytes ) throws Exception {
		
		if ( ! CachedDataInFileMgmt.getSingletonInstance().isCachedDataFilesDirConfigured() ) {
			return;  //  EARLY EXIT
		}

		String prefixForCaching = null;
		
		if ( crosslinkLooplink == CrosslinkLooplink.CROSSLINK ) {
			prefixForCaching = PREFIX_SUBPREFIX_CROSSLINKS_FOR_CACHING;
		} else if ( crosslinkLooplink == CrosslinkLooplink.LOOPLINK ) {
			prefixForCaching = PREFIX_SUBPREFIX_LOOPLINKS_FOR_CACHING;
		} else {
			String msg = "Invalid value for crosslinkLooplink: " + crosslinkLooplink;
			log.error( msg );
			throw new InvalidParameterException(msg);
		}

		CachedDataInFileMgmt.getSingletonInstance().saveCachedDataFileContents( 
				webserviceResponseJSONAsBytes,
				ReplaceExistingValue.NO,
				prefixForCaching /* namePrefix */, 
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
