package org.yeastrc.xlink.www.qc_data.scan_level_data.main;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.www.qc_data.scan_level_data.objects.Scan_MS_AllScans_AllButPeaks_Result;
import org.yeastrc.xlink.www.qc_data.scan_level_data.objects.Scan_MS_AllScans_AllButPeaks_Result.Scan_MS_AllScans_AllButPeaks_Result_SingleScan;
import org.yeastrc.xlink.www.spectral_storage_service_interface.Call_Get_ScanData_AllScans_AllButPeaks_SpectralStorageWebservice;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Create JSON for MS 1 Histograms for MS 1 Ion Current VS Retention Time and M/Z
 *
 */
public class Scan_MS_AllScans_AllButPeaks {

	private static final Logger log = LoggerFactory.getLogger(  Scan_MS_AllScans_AllButPeaks.class );

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 *  
	 *   !!!  Skip Caching for now since result will change when mzML files are reprocessed to latest data file format version 
	 *         and data needed for these charts will be available.
	 */
//	static final int VERSION_FOR_CACHING = 1;
	
	
	/**
	 * private constructor
	 */
	private Scan_MS_AllScans_AllButPeaks(){}
	public static Scan_MS_AllScans_AllButPeaks getInstance( ) throws Exception {
		Scan_MS_AllScans_AllButPeaks instance = new Scan_MS_AllScans_AllButPeaks();
		return instance;
	}
	
	/**
	 * @param scanFileId
	 * @return
	 * @throws Exception 
	 */
	public byte[] getScan_MS_AllScans_AllButPeaks( int scanFileId, byte[] requestJSONBytes ) throws Exception {

//		 *   !!!  Skip Caching for now since result will change when mzML files are reprocessed to latest data file format version 
//		 *         and data needed for these charts will be available.
		
//		{
//			byte[] resultsAsBytes = 
//					Scan_MS_AllScans_AllButPeaks_CachedResultManager.getSingletonInstance()
//					.retrieveDataFromCache( scanFileId, requestJSONBytes );
//	
//			if ( resultsAsBytes != null ) {
//				//  Have Cached data so return it
//				
//				return resultsAsBytes;  //  EARLY RETURN
//			}
//		}

		
		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

		List<SingleScan_SubResponse> singleScan_SubResponseList = getSingleScan_SubResponseList( scanFileId );

		if ( singleScan_SubResponseList == null ) {
			//  No data found for scanFileId so return
			return jacksonJSON_Mapper.writeValueAsBytes( new Scan_MS_AllScans_AllButPeaks_Result() );
		}

		List<Scan_MS_AllScans_AllButPeaks_Result_SingleScan> scans = new ArrayList<>();
		
		for ( SingleScan_SubResponse singleScan_SubResponse : singleScan_SubResponseList ) {
			
			Scan_MS_AllScans_AllButPeaks_Result_SingleScan scan = new Scan_MS_AllScans_AllButPeaks_Result_SingleScan( singleScan_SubResponse );
			scans.add(scan);
		}
		
		Scan_MS_AllScans_AllButPeaks_Result result = new Scan_MS_AllScans_AllButPeaks_Result();
		result.setScans( scans );

		byte[] resultsAsBytes = jacksonJSON_Mapper.writeValueAsBytes( result );
		
//		 *   !!!  Skip Caching for now since result will change when mzML files are reprocessed to latest data file format version 
//		 *         and data needed for these charts will be available.

//		Scan_MS_AllScans_AllButPeaks_CachedResultManager.getSingletonInstance()
//		.saveDataToCache( scanFileId, resultsAsBytes, requestJSONBytes );
		
		return resultsAsBytes;
	}


	/**
	 * @param scanFileId
	 * @return null if not in db
	 * @throws Exception
	 */
	private List<SingleScan_SubResponse> getSingleScan_SubResponseList( int scanFileId ) throws Exception {

		//  Get from Spectral Storage Service

		String spectralStorageAPIKey = ScanFileDAO.getInstance().getSpectralStorageAPIKeyById( scanFileId );

		if ( spectralStorageAPIKey == null ) {
			log.error( "No spectralStorageAPIKey value in scan file table for scanFileId: " + scanFileId );
			return null;  // EARLY RETURN
		}

		List<SingleScan_SubResponse> singleScan_SubResponseList =
				Call_Get_ScanData_AllScans_AllButPeaks_SpectralStorageWebservice.getSingletonInstance()
				.getScanData_AllScans_AllButPeaks_FromSpectralStorageService( spectralStorageAPIKey );

		if ( singleScan_SubResponseList == null ) {

			log.error( "No data in Spectral Storage from call Call_Get_ScanData_AllScans_AllButPeaks_SpectralStorageWebservice.getSingletonInstance().getScanData_AllScans_AllButPeaks_FromSpectralStorageService( spectralStorageAPIKey ); . scanFileId: " + scanFileId 
					+ ", spectralStorageAPIKey: "
					+ spectralStorageAPIKey );

			return null;  // EARLY RETURN
		}

		return singleScan_SubResponseList;
	}
}
