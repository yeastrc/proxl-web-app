package org.yeastrc.xlink.www.spectral_storage_service_interface;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.exceptions.YRCSpectralStorageGetDataWebserviceCallErrorException;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanPeakIntensityBinnedOn_RT_MZ_Request;
import org.yeastrc.spectral_storage.get_data_webapp.webservice_connect.main.CallSpectralStorageGetDataWebservice;
import org.yeastrc.spectral_storage.get_data_webapp.webservice_connect.json_helper.CallSpectralStorageGetDataWebservice_JSON_Parse_Helper;
import org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto.MS1_IntensitiesBinnedSummedMapRoot;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;

/**
 * 
 *
 */
public class Call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice {

	private static final Logger log = LoggerFactory.getLogger( Call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice.class);

	private static final Call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice instance = new Call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice();
	private Call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice() { }
	public static Call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice getSingletonInstance() { return instance; }
	
	//   Bin size of 1 is only supported bin size currently
	
	private static final long RETENTION_TIME_BIN_SIZE = 1; // in seconds
	private static final long MZ_BIN_SIZE = 1;             // in m/z

	/**
	 * Get Scan Level 1 Peak Intensity binned Data from Spectral Storage Service
	 * 
	 * @param spectralStorageAPIKey
	 * @throws Exception 
	 */
	public MS1_IntensitiesBinnedSummedMapRoot getScanPeakIntensityBinnedOn_RT_MZFromSpectralStorageService( 
			String spectralStorageAPIKey ) throws Exception {
		
		if ( StringUtils.isEmpty( spectralStorageAPIKey ) ) {
			String msg = "No value for spectralStorageAPIKey.  ";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		CallSpectralStorageGetDataWebservice callSpectralStorageWebservice = 
				CallSpectralStorageWebservice_ForProxl_Factory.getSingletonInstance().getCallSpectralStorageWebservice();
		
		Get_ScanPeakIntensityBinnedOn_RT_MZ_Request webserviceRequest = new Get_ScanPeakIntensityBinnedOn_RT_MZ_Request();
		webserviceRequest.setScanFileAPIKey( spectralStorageAPIKey );
		webserviceRequest.setRetentionTimeBinSize( RETENTION_TIME_BIN_SIZE );
		webserviceRequest.setMzBinSize( MZ_BIN_SIZE );
		
		byte[] serverResponseBytes = null;
		try {
			serverResponseBytes =
				callSpectralStorageWebservice.call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice( webserviceRequest );
		
		} catch ( YRCSpectralStorageGetDataWebserviceCallErrorException e ) {
			
			throw e;
		}

		if ( serverResponseBytes == null ) {
			
			String msg = "No data return from server";
			log.error( msg );
			throw new ProxlWebappInternalErrorException( msg );
		}
		
		MS1_IntensitiesBinnedSummedMapRoot ms1_IntensitiesBinnedSummedMapRoot =
				CallSpectralStorageGetDataWebservice_JSON_Parse_Helper.getInstance()
				.deserialize_unGzip_Get_ScanPeakIntensityBinnedOn_RT_MZ_Response( serverResponseBytes );
		
		return ms1_IntensitiesBinnedSummedMapRoot;
	}
}
