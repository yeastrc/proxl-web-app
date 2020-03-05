package org.yeastrc.xlink.www.qc_data.scan_level_data.main;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.qc_data.utils.BoxPlotUtils;
import org.yeastrc.xlink.www.qc_data.utils.BoxPlotUtils.GetBoxPlotValuesResult;
import org.yeastrc.xlink.www.spectral_storage_service_interface.Call_Get_ScanData_AllScans_AllButPeaks_SpectralStorageWebservice;

import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * Create JSON for 
 *
 */
public class QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan {

	private static final Logger log = LoggerFactory.getLogger(  QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan.class );

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 *  
	 *  !!!  Skip Caching for now since result will change when mzML files are reprocessed to latest data file format version 
	 *         and data needed for these charts will be available.
	 */
//	static final int VERSION_FOR_CACHING = 1;
	
	
	/**
	 * private constructor
	 */
	private QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan(){}
	public static QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan getInstance( ) throws Exception {
		QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan instance = new QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan();
		return instance;
	}
	
	/**
	 * 
	 *
	 */
	public static class QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result {
		
		private List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> perScan_IonInjectionTime_BoxplotData;

		private List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> perScan_TotalIonCurrent_BoxplotData;

		public List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> getPerScan_TotalIonCurrent_BoxplotData() {
			return perScan_TotalIonCurrent_BoxplotData;
		}
		public void setPerScan_TotalIonCurrent_BoxplotData(
				List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> perScan_TotalIonCurrent_BoxplotData) {
			this.perScan_TotalIonCurrent_BoxplotData = perScan_TotalIonCurrent_BoxplotData;
		}
		public List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> getPerScan_IonInjectionTime_BoxplotData() {
			return perScan_IonInjectionTime_BoxplotData;
		}
		public void setPerScan_IonInjectionTime_BoxplotData(
				List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> perScan_IonInjectionTime_BoxplotData) {
			this.perScan_IonInjectionTime_BoxplotData = perScan_IonInjectionTime_BoxplotData;
		}

	}
	
	/**
	 * 
	 *
	 */
	public static class QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData {
		
		private int ms1_ScanNumber;
		private float ms1_RetentionTime;
		
		private boolean ms2_dataFound;

		//  Box chart values
		private double chartIntervalMax;
		private double chartIntervalMin;
		private double firstQuartile;
		private double median;
		private double thirdQuartile;

		private List<Float> outlierValues;
		
		public int getMs1_ScanNumber() {
			return ms1_ScanNumber;
		}
		public void setMs1_ScanNumber(int ms1_ScanNumber) {
			this.ms1_ScanNumber = ms1_ScanNumber;
		}
		public double getChartIntervalMax() {
			return chartIntervalMax;
		}
		public void setChartIntervalMax(double chartIntervalMax) {
			this.chartIntervalMax = chartIntervalMax;
		}
		public double getChartIntervalMin() {
			return chartIntervalMin;
		}
		public void setChartIntervalMin(double chartIntervalMin) {
			this.chartIntervalMin = chartIntervalMin;
		}
		public double getFirstQuartile() {
			return firstQuartile;
		}
		public void setFirstQuartile(double firstQuartile) {
			this.firstQuartile = firstQuartile;
		}
		public double getMedian() {
			return median;
		}
		public void setMedian(double median) {
			this.median = median;
		}
		public double getThirdQuartile() {
			return thirdQuartile;
		}
		public void setThirdQuartile(double thirdQuartile) {
			this.thirdQuartile = thirdQuartile;
		}
		public List<Float> getOutlierValues() {
			return outlierValues;
		}
		public void setOutlierValues(List<Float> outlierValues) {
			this.outlierValues = outlierValues;
		}
		public float getMs1_RetentionTime() {
			return ms1_RetentionTime;
		}
		public void setMs1_RetentionTime(float ms1_RetentionTime) {
			this.ms1_RetentionTime = ms1_RetentionTime;
		}
		public boolean isMs2_dataFound() {
			return ms2_dataFound;
		}
		public void setMs2_dataFound(boolean ms2_dataFound) {
			this.ms2_dataFound = ms2_dataFound;
		}
	}
	
	/**
	 * @param scanFileId
	 * @return
	 * @throws Exception 
	 */
	public byte[] getQC_Scan_MS2_FillTime_TIC_BoxPlotData_Per_MS1_Scan( int scanFileId, byte[] requestJSONBytes ) throws Exception {

//		{
//			byte[] resultsAsBytes = 
//					QC_Scan_MS2_FillTime_TIC_BoxPlotData_Per_MS1_Scan_CachedResultManager.getSingletonInstance()
//					.retrieveDataFromCache( scanFileId, requestJSONBytes );
//
//			if ( resultsAsBytes != null ) {
//				//  Have Cached data so return it
//				return resultsAsBytes;  //  EARLY RETURN
//			}
//		}
		
		try {
	
			
			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
	
			List<SingleScan_SubResponse> singleScan_SubResponseList = getSingleScan_SubResponseList( scanFileId );
	
			if ( singleScan_SubResponseList == null ) {
				//  No data found for scanFileId so return
				return jacksonJSON_Mapper.writeValueAsBytes( new QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result() );
			}
			
			List<MS1_With_Its_MS2_Scans> ms1_With_Its_MS2_ScansList = getMS1_With_Its_MS2_Scans( singleScan_SubResponseList );
	
			List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> perScan_IonInjectionTime_BoxplotData = 
					compute_perScan_IonInjectionTime_BoxplotData( ms1_With_Its_MS2_ScansList );
			
			List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> perScan_TotalIonCurrent_BoxplotData =
					compute_perScan_TotalIonCurrent_BoxplotData( ms1_With_Its_MS2_ScansList );
			
			QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result result = new QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result();
			
			//  Either or both may be null if not all data available:
			
			//    perScan_IonInjectionTime_BoxplotData, perScan_TotalIonCurrent_BoxplotData
			
				
			result.perScan_IonInjectionTime_BoxplotData = perScan_IonInjectionTime_BoxplotData;
			result.perScan_TotalIonCurrent_BoxplotData = perScan_TotalIonCurrent_BoxplotData;
		
			byte[] resultsAsBytes = jacksonJSON_Mapper.writeValueAsBytes( result );
			
			if ( result.perScan_IonInjectionTime_BoxplotData != null && result.perScan_TotalIonCurrent_BoxplotData != null ) {
			
				//  Only Save to Cache when have data for all charts
				
//				QC_Scan_MS2_FillTime_TIC_BoxPlotData_Per_MS1_Scan_CachedResultManager.getSingletonInstance()
//				.saveDataToCache( scanFileId, resultsAsBytes, requestJSONBytes );
				
			}
			
			return resultsAsBytes;
			
		} catch ( Exception e ) {
			
			log.error( "Exception processing: " + e.toString(), e );
			throw e;
		}
	}
	
	/**
	 * @param ms1_With_Its_MS2_ScansList
	 * @return null if not all data available 
	 * @throws ProxlWebappInternalErrorException 
	 */
	private List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> compute_perScan_IonInjectionTime_BoxplotData( List<MS1_With_Its_MS2_Scans> ms1_With_Its_MS2_ScansList ) throws ProxlWebappInternalErrorException {
	
		List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> perScan_FillTime_BoxplotData = new ArrayList<>();
		
		for ( MS1_With_Its_MS2_Scans ms1_With_Its_MS2_Scans : ms1_With_Its_MS2_ScansList ) {
			
			QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData resultItem =
					compute_perScan_FillTime_BoxplotData_SingleScan( ms1_With_Its_MS2_Scans );
			
			if ( resultItem == null ) {
				
				//  Unable to compute values since needed values are null.  IonInjectionTime is null
				
				return null; //  EARLY RETURN
			}
			
			resultItem.ms1_ScanNumber = ms1_With_Its_MS2_Scans.ms1_Scan.getScanNumber();
			resultItem.ms1_RetentionTime = ms1_With_Its_MS2_Scans.ms1_Scan.getRetentionTime();
			
			perScan_FillTime_BoxplotData.add(resultItem);
		}
		
		return perScan_FillTime_BoxplotData;
	}
	
	
	/**
	 * @param ms1_With_Its_MS2_Scans
	 * @return null if not all data available
	 * @throws ProxlWebappInternalErrorException 
	 */
	private QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData compute_perScan_FillTime_BoxplotData_SingleScan( MS1_With_Its_MS2_Scans ms1_With_Its_MS2_Scans ) throws ProxlWebappInternalErrorException {

		for ( SingleScan_SubResponse singleScan_SubResponse : ms1_With_Its_MS2_Scans.its_MS2_Scans ) {
			if ( singleScan_SubResponse.getIonInjectionTime() == null ) {
				
				//  No Value for IonInjectionTime so return null
				return null; //  EARLY RETURN
			}
		}
		
		if ( ms1_With_Its_MS2_Scans.its_MS2_Scans.isEmpty() ) {
			return new QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData();
		}

		List<Double> values = new ArrayList<>( ms1_With_Its_MS2_Scans.its_MS2_Scans.size() );
		
		for ( SingleScan_SubResponse singleScan_SubResponse : ms1_With_Its_MS2_Scans.its_MS2_Scans ) {
			values.add( Double.valueOf( singleScan_SubResponse.getIonInjectionTime().floatValue() ) );
		}
		
		GetBoxPlotValuesResult getBoxPlotValuesResult =
				BoxPlotUtils.getInstance().getBoxPlotValues( values );
		
		double chartIntervalMax = getBoxPlotValuesResult.getChartIntervalMax();
		double chartIntervalMin = getBoxPlotValuesResult.getChartIntervalMin();
		
		// Add the values that are not within max (highcutoff) and min (lowcutoff) to a list and send to web app
		List<Float> outlierValues = new ArrayList<>( 200 );
		for ( SingleScan_SubResponse singleScan_SubResponse : ms1_With_Its_MS2_Scans.its_MS2_Scans ) {
			if ( singleScan_SubResponse.getIonInjectionTime().floatValue() < chartIntervalMin || singleScan_SubResponse.getIonInjectionTime().floatValue() > chartIntervalMax ) {
				outlierValues.add( singleScan_SubResponse.getIonInjectionTime() );
			}
		}
		
		QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData result = new QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData();

		result.setChartIntervalMax( getBoxPlotValuesResult.getChartIntervalMax() );
		result.setChartIntervalMin( getBoxPlotValuesResult.getChartIntervalMin() );
		result.setFirstQuartile( getBoxPlotValuesResult.getFirstQuartile() );
		result.setThirdQuartile( getBoxPlotValuesResult.getThirdQuartile() );
		result.setMedian( getBoxPlotValuesResult.getMedian() );
		
		result.setOutlierValues( outlierValues );
		
		result.setMs2_dataFound(true);
		
		return result;
	}
	
	///////////////////////////////

	/**
	 * @param ms1_With_Its_MS2_ScansList
	 * @return null if not all data available
	 * @throws ProxlWebappInternalErrorException 
	 */
	private List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> compute_perScan_TotalIonCurrent_BoxplotData( List<MS1_With_Its_MS2_Scans> ms1_With_Its_MS2_ScansList ) throws ProxlWebappInternalErrorException {
	
		List<QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData> perScan_FillTime_BoxplotData = new ArrayList<>();
		
		for ( MS1_With_Its_MS2_Scans ms1_With_Its_MS2_Scans : ms1_With_Its_MS2_ScansList ) {
			
			QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData resultItem =
					compute_perScan_TotalIonCurrent_BoxplotData_SingleScan( ms1_With_Its_MS2_Scans );

			if ( resultItem == null ) {
				
				//  Unable to compute values since needed values are null.  TotalIonCurrent_ForScan is null
				
				return null; //  EARLY RETURN
			}
			
			resultItem.ms1_ScanNumber = ms1_With_Its_MS2_Scans.ms1_Scan.getScanNumber();
			resultItem.ms1_RetentionTime = ms1_With_Its_MS2_Scans.ms1_Scan.getRetentionTime();
			
			perScan_FillTime_BoxplotData.add(resultItem);
		}
		
		return perScan_FillTime_BoxplotData;
	}

	
	/**
	 * @param ms1_With_Its_MS2_Scans
	 * @return null if not all data available
	 * @throws ProxlWebappInternalErrorException 
	 */
	private QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData compute_perScan_TotalIonCurrent_BoxplotData_SingleScan( MS1_With_Its_MS2_Scans ms1_With_Its_MS2_Scans ) throws ProxlWebappInternalErrorException {

		for ( SingleScan_SubResponse singleScan_SubResponse : ms1_With_Its_MS2_Scans.its_MS2_Scans ) {
			if ( singleScan_SubResponse.getTotalIonCurrent_ForScan() == null ) {
				
				//  No Value for TotalIonCurrent_ForScan so return null
				return null; //  EARLY RETURN
			}
		}
		
		if ( ms1_With_Its_MS2_Scans.its_MS2_Scans.isEmpty() ) {
			return new QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData();
		}

		List<Double> values = new ArrayList<>( ms1_With_Its_MS2_Scans.its_MS2_Scans.size() );
		
		for ( SingleScan_SubResponse singleScan_SubResponse : ms1_With_Its_MS2_Scans.its_MS2_Scans ) {
			values.add( Double.valueOf( singleScan_SubResponse.getTotalIonCurrent_ForScan() ) );
		}
		
		GetBoxPlotValuesResult getBoxPlotValuesResult =
				BoxPlotUtils.getInstance().getBoxPlotValues( values );
		
		double chartIntervalMax = getBoxPlotValuesResult.getChartIntervalMax();
		double chartIntervalMin = getBoxPlotValuesResult.getChartIntervalMin();
		
		// Add the values that are not within max (highcutoff) and min (lowcutoff) to a list and send to web app
		List<Float> outlierValues = new ArrayList<>( 200 );
		for ( SingleScan_SubResponse singleScan_SubResponse : ms1_With_Its_MS2_Scans.its_MS2_Scans ) {
			if ( singleScan_SubResponse.getTotalIonCurrent_ForScan() < chartIntervalMin || singleScan_SubResponse.getTotalIonCurrent_ForScan() > chartIntervalMax ) {
				outlierValues.add( singleScan_SubResponse.getTotalIonCurrent_ForScan() );
			}
		}
		
		QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData result = new QC_Scan_MS2_IonInjectionTime_TIC_BoxPlotData_Per_MS1_Scan_Result_SingleScan_BoxPlotData();

		result.setChartIntervalMax( getBoxPlotValuesResult.getChartIntervalMax() );
		result.setChartIntervalMin( getBoxPlotValuesResult.getChartIntervalMin() );
		result.setFirstQuartile( getBoxPlotValuesResult.getFirstQuartile() );
		result.setThirdQuartile( getBoxPlotValuesResult.getThirdQuartile() );
		result.setMedian( getBoxPlotValuesResult.getMedian() );
		
		result.setOutlierValues( outlierValues );
		
		result.setMs2_dataFound(true);
		
		return result;
	}
	
	////////////////////////////////////
	
	/**
	 * @param singleScan_SubResponseList
	 * @return
	 * @throws ProxlWebappInternalErrorException 
	 */
	private List<MS1_With_Its_MS2_Scans> getMS1_With_Its_MS2_Scans( List<SingleScan_SubResponse> singleScan_SubResponseList ) throws ProxlWebappInternalErrorException {
		
		List<MS1_With_Its_MS2_Scans> MS1_With_Its_MS2_ScansList = new ArrayList<>( singleScan_SubResponseList.size() );
		
		MS1_With_Its_MS2_Scans currentMS1_With_Its_MS2_Scans = null;
		for ( SingleScan_SubResponse singleScan_SubResponse : singleScan_SubResponseList ) {
			
			if ( singleScan_SubResponse.getLevel() == 1 ) {
				
				if ( currentMS1_With_Its_MS2_Scans != null ) {
					MS1_With_Its_MS2_ScansList.add(currentMS1_With_Its_MS2_Scans);
				}
				currentMS1_With_Its_MS2_Scans = new MS1_With_Its_MS2_Scans();
				currentMS1_With_Its_MS2_Scans.ms1_Scan = singleScan_SubResponse;
						
			} else if ( singleScan_SubResponse.getLevel() == 2 ) {
				if ( currentMS1_With_Its_MS2_Scans == null ) {
					throw new ProxlWebappInternalErrorException( "singleScan_SubResponse.getLevel() == 2 and currentMS1_With_Its_MS2_Scans == null" );
				}
				currentMS1_With_Its_MS2_Scans.its_MS2_Scans.add(singleScan_SubResponse);
			} else {
				
			}
		}
		if ( currentMS1_With_Its_MS2_Scans == null ) {
			throw new ProxlWebappInternalErrorException( "currentMS1_With_Its_MS2_Scans == null at end of processing singleScan_SubResponseList" );
		}
		// add final entry
		MS1_With_Its_MS2_ScansList.add(currentMS1_With_Its_MS2_Scans);
		
		return MS1_With_Its_MS2_ScansList;
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
	
	/////////////////////////////////////////
	/////////////////////////////////////////
	/////////////////////////////////////////

	//  Internal Classes
	
	private static class MS1_With_Its_MS2_Scans {
		
		SingleScan_SubResponse ms1_Scan;
		List<SingleScan_SubResponse> its_MS2_Scans = new ArrayList<>( 200 );
	}
}
