package org.yeastrc.xlink.www.spectral_storage_service_interface;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanRetentionTimes_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanRetentionTimes_Response;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.sub_parts.Single_ScanRetentionTime_ScanNumber_SubResponse;
import org.yeastrc.spectral_storage.webservice_connect.main.CallSpectralStorageWebservice;

/**
 * 
 *
 */
public class Call_Get_ScanRetentionTimes_FromSpectralStorageService {

	private static final Logger log = Logger.getLogger(Call_Get_ScanRetentionTimes_FromSpectralStorageService.class);

	private static final Call_Get_ScanRetentionTimes_FromSpectralStorageService instance = new Call_Get_ScanRetentionTimes_FromSpectralStorageService();
	private Call_Get_ScanRetentionTimes_FromSpectralStorageService() { }
	public static Call_Get_ScanRetentionTimes_FromSpectralStorageService getSingletonInstance() { return instance; }

	/**
	 * Get Scan Partial Data from Spectral Storage Service
	 * 
	 * @param scanFileAPIKey
	 * @throws Exception 
	 */
	public List<Single_ScanRetentionTime_ScanNumber_SubResponse> get_ScanRetentionTimes_All( String scanFileAPIKey ) throws Exception {
		if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
			String msg = "No value for scanFileAPIKey.  ";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}

		Get_ScanRetentionTimes_Request webserviceRequest = new Get_ScanRetentionTimes_Request();
		webserviceRequest.setScanFileAPIKey( scanFileAPIKey );
		
		return internal_ScanRetentionTimesFromSpectralStorageService( webserviceRequest );
	}
	
	/**
	 * Get Scan Partial Data from Spectral Storage Service
	 * 
	 * @param scanFileAPIKey
	 * @throws Exception 
	 */
	public List<Single_ScanRetentionTime_ScanNumber_SubResponse> get_ScanRetentionTimes_Exclude_ScanLevel( String scanFileAPIKey, int excludedScanLevel ) throws Exception {
		if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
			String msg = "No value for scanFileAPIKey.  ";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}

		List<Integer> scanLevelsToExclude = new ArrayList<>( 1 );
		scanLevelsToExclude.add( excludedScanLevel );

		Get_ScanRetentionTimes_Request webserviceRequest = new Get_ScanRetentionTimes_Request();
		webserviceRequest.setScanFileAPIKey( scanFileAPIKey );
		webserviceRequest.setScanLevelsToExclude( scanLevelsToExclude );
		
		return internal_ScanRetentionTimesFromSpectralStorageService( webserviceRequest );
	}

	/**
	 * Get Scan Partial Data from Spectral Storage Service
	 * 
	 * @param scanFileAPIKey
	 * @throws Exception 
	 */
	private List<Single_ScanRetentionTime_ScanNumber_SubResponse> internal_ScanRetentionTimesFromSpectralStorageService( Get_ScanRetentionTimes_Request webserviceRequest ) throws Exception {
		
		CallSpectralStorageWebservice callSpectralStorageWebservice = 
				CallSpectralStorageWebservice_ForProxl_Factory.getSingletonInstance().getCallSpectralStorageWebservice();
		
		Get_ScanRetentionTimes_Response get_ScanRetentionTimes_Response =
				callSpectralStorageWebservice.call_Get_ScanRetentionTimes_Webservice( webserviceRequest );

		List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanParts = get_ScanRetentionTimes_Response.getScanParts();
		
		return scanParts;
	}
}
