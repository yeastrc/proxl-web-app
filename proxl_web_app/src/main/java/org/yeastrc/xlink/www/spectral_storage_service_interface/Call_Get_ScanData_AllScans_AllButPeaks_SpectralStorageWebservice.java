package org.yeastrc.xlink.www.spectral_storage_service_interface;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_IncludeReturnIonInjectionTimeData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ScanFileAPI_Key_NotFound;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanData_AllScans_ExcludePeaks_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanData_AllScans_ExcludePeaks_Response;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;
import org.yeastrc.spectral_storage.get_data_webapp.webservice_connect.main.CallSpectralStorageGetDataWebservice;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;

/**
 * 
 *
 */
public class Call_Get_ScanData_AllScans_AllButPeaks_SpectralStorageWebservice {

	private static final Logger log = LoggerFactory.getLogger( Call_Get_ScanData_AllScans_AllButPeaks_SpectralStorageWebservice.class);

	private static final Call_Get_ScanData_AllScans_AllButPeaks_SpectralStorageWebservice instance = new Call_Get_ScanData_AllScans_AllButPeaks_SpectralStorageWebservice();
	private Call_Get_ScanData_AllScans_AllButPeaks_SpectralStorageWebservice() { }
	public static Call_Get_ScanData_AllScans_AllButPeaks_SpectralStorageWebservice getSingletonInstance() { return instance; }

	/**
	 * Get Scan Data from Spectral Storage Service
	 * 
	 * Number of scan numbers cannot exceed max as specified in Spectral Storage Service.  
	 *   ProxlWebappInternalErrorException thrown with max allowed if max exceeded.
	 * 
	 * @param scanFileAPIKey
	 * @throws Exception 
	 */
	public List<SingleScan_SubResponse> getScanData_AllScans_AllButPeaks_FromSpectralStorageService( String scanFileAPIKey ) throws Exception {
		
		if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
			String msg = "No value for scanFileAPIKey. ";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		CallSpectralStorageGetDataWebservice callSpectralStorageWebservice = 
				CallSpectralStorageWebservice_ForProxl_Factory.getSingletonInstance().getCallSpectralStorageWebservice();
		
		
		Get_ScanData_AllScans_ExcludePeaks_Request webserviceRequest = new Get_ScanData_AllScans_ExcludePeaks_Request();
		webserviceRequest.setScanFileAPIKey( scanFileAPIKey );
		
		webserviceRequest.setIncludeReturnScanLevelTotalIonCurrentData( Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData.YES );
		webserviceRequest.setIncludeReturnIonInjectionTimeData( Get_ScanData_IncludeReturnIonInjectionTimeData.YES );

		Get_ScanData_AllScans_ExcludePeaks_Response webserviceResonse =
				callSpectralStorageWebservice.call_Get_ScanData_AllScans_ExcludePeaks_Webservice( webserviceRequest );

		if ( webserviceResonse.getStatus_scanFileAPIKeyNotFound() 
				== Get_ScanData_ScanFileAPI_Key_NotFound.YES ) {
			String msg = "No data in Spectral Storage for API Key: " + scanFileAPIKey;
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
				
		List<SingleScan_SubResponse> scans = webserviceResonse.getScans();
		
		if ( scans == null ) {
			String msg = "Returned Scans property is null: Spectral Storage API Key: " + scanFileAPIKey;
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
		
		return scans;
	}
}
