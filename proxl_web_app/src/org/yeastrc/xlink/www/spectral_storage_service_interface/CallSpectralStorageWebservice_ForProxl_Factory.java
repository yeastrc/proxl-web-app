package org.yeastrc.xlink.www.spectral_storage_service_interface;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.webservice_connect.main.CallSpectralStorageGetDataWebservice;
import org.yeastrc.spectral_storage.get_data_webapp.webservice_connect.main.CallSpectralStorageGetDataWebserviceInitParameters;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.www.config_properties_file.ProxlConfigFileValues;
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappConfigException;

/**
 * Create object of Spectral Storage Service call interface class CallSpectralStorageWebservice
 * and configure it
 *
 * Singleton Class
 * 
 * Package Private
 */
class CallSpectralStorageWebservice_ForProxl_Factory {

	private static final Logger log = Logger.getLogger(CallSpectralStorageWebservice_ForProxl_Factory.class);

	private static final CallSpectralStorageWebservice_ForProxl_Factory instance = new CallSpectralStorageWebservice_ForProxl_Factory();
	private CallSpectralStorageWebservice_ForProxl_Factory() { }
	public static CallSpectralStorageWebservice_ForProxl_Factory getSingletonInstance() { return instance; }

	/**
	 * @return
	 */
	public CallSpectralStorageGetDataWebservice getCallSpectralStorageWebservice() throws Exception {
		
		// First get override URL from config file
		String spectralStorageWebserviceBaseURL = 
				ProxlConfigFileValues.getInstance().getSpectralStorageServerURLandAppContext();
				
		if ( StringUtils.isEmpty( spectralStorageWebserviceBaseURL ) ) {
			//  Not in config file so get from config_system table
			spectralStorageWebserviceBaseURL = 
					ConfigSystemDAO.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.SPECTRAL_STORAGE_SERVICE_GET_DATA_BASE_URL );
		}
		
		if ( StringUtils.isEmpty( spectralStorageWebserviceBaseURL ) ) {
			String msg = "No value in config for key '"
					+ ConfigSystemsKeysSharedConstants.SPECTRAL_STORAGE_SERVICE_GET_DATA_BASE_URL
					+ "'.";
			log.error( msg );
			throw new ProxlWebappConfigException( msg );
		}
		
		CallSpectralStorageGetDataWebserviceInitParameters initParams = new CallSpectralStorageGetDataWebserviceInitParameters();
		
		initParams.setSpectralStorageServerBaseURL( spectralStorageWebserviceBaseURL );
		
		CallSpectralStorageGetDataWebservice callSpectralStorageWebservice = CallSpectralStorageGetDataWebservice.getInstance();

		callSpectralStorageWebservice.init( initParams );
		
		return callSpectralStorageWebservice;
	}
}
