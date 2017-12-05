package org.yeastrc.xlink.www.spectral_storage_service_interface;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_SummaryDataPerScanLevel_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_SummaryDataPerScanLevel_Response;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.sub_parts.SingleScanLevelSummaryData_SubResponse;
import org.yeastrc.spectral_storage.webservice_connect.main.CallSpectralStorageWebservice;

/**
 * 
 *
 */
public class Call_Get_GetSummaryDataPerScanLevel_FromSpectralStorageService {

	private static final Logger log = Logger.getLogger(Call_Get_GetSummaryDataPerScanLevel_FromSpectralStorageService.class);

	private static final Call_Get_GetSummaryDataPerScanLevel_FromSpectralStorageService instance = new Call_Get_GetSummaryDataPerScanLevel_FromSpectralStorageService();
	private Call_Get_GetSummaryDataPerScanLevel_FromSpectralStorageService() { }
	public static Call_Get_GetSummaryDataPerScanLevel_FromSpectralStorageService getSingletonInstance() { return instance; }

	/**
	 * Get Scan Partial Data from Spectral Storage Service
	 * 
	 * @param scanFileAPIKey
	 * @throws Exception 
	 */
	public List<SingleScanLevelSummaryData_SubResponse> get_GetSummaryDataPerScanLevel_All( String scanFileAPIKey ) throws Exception {
		if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
			String msg = "No value for scanFileAPIKey.  ";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}

		Get_SummaryDataPerScanLevel_Request webserviceRequest = new Get_SummaryDataPerScanLevel_Request();
		webserviceRequest.setScanFileAPIKey( scanFileAPIKey );
	
		CallSpectralStorageWebservice callSpectralStorageWebservice = 
				CallSpectralStorageWebservice_ForProxl_Factory.getSingletonInstance().getCallSpectralStorageWebservice();
		
		Get_SummaryDataPerScanLevel_Response get_SummaryDataPerScanLevel_Response =
				callSpectralStorageWebservice.call_GetSummaryDataPerScanLevel_Webservice( webserviceRequest );

		List<SingleScanLevelSummaryData_SubResponse> scanSummaryPerScanLevelList = get_SummaryDataPerScanLevel_Response.getScanSummaryPerScanLevelList();
		
		return scanSummaryPerScanLevelList;
	}
}
