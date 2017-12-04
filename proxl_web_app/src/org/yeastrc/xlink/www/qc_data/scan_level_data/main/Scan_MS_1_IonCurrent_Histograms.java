package org.yeastrc.xlink.www.qc_data.scan_level_data.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto.MS1_IntensitiesBinnedSummedMapRoot;
import org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto.MS1_IntensitiesBinnedSummed_Summary_DataRoot;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.www.qc_data.scan_level_data.objects.Scan_MS_1_IonCurrent_HistogramsResult;
import org.yeastrc.xlink.www.qc_data.scan_level_data.objects.Scan_MS_1_IonCurrent_HistogramsResult.Scan_MS_1_IonCurrent_HistogramsResultChartBucket;
import org.yeastrc.xlink.www.qc_data.scan_level_data.objects.Scan_MS_1_IonCurrent_HistogramsResult.Scan_MS_1_IonCurrent_HistogramsResultForChartType;
import org.yeastrc.xlink.www.spectral_storage_service_interface.Call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Create JSON for MS 1 Histograms for MS 1 Ion Current VS Retention Time and M/Z
 *
 */
public class Scan_MS_1_IonCurrent_Histograms {

	private static final Logger log = Logger.getLogger( Scan_MS_1_IonCurrent_Histograms.class );

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 */
	static final int VERSION_FOR_CACHING = 1;
	
	
	/**
	 * private constructor
	 */
	private Scan_MS_1_IonCurrent_Histograms(){}
	public static Scan_MS_1_IonCurrent_Histograms getInstance( ) throws Exception {
		Scan_MS_1_IonCurrent_Histograms instance = new Scan_MS_1_IonCurrent_Histograms();
		return instance;
	}
	
	private enum ChartType { RETENTION_TIME, M_OVER_Z } // for shared charting code
	
	/**
	 * @param scanFileId
	 * @return
	 * @throws Exception 
	 */
	public byte[] getScan_MS_1_IonCurrent_HistogramsResult( int scanFileId ) throws Exception {

		byte[] resultsAsBytes = 
				Scan_MS_1_IonCurrent_Histograms_CachedResultManager.getSingletonInstance()
				.retrieveDataFromCache( scanFileId );

		if ( resultsAsBytes != null ) {
			//  Have Cached data so return it
			return resultsAsBytes;  //  EARLY RETURN
		}

		
		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

		MS1_IntensitiesBinnedSummedMapRoot ms1_IntensitiesBinnedSummedMapRoot =
			getMS1_IntensitiesBinnedSummedMapRoot( scanFileId );

		if ( ms1_IntensitiesBinnedSummedMapRoot == null ) {
			//  No data found for scanFileId so return
			return jacksonJSON_Mapper.writeValueAsBytes( new Scan_MS_1_IonCurrent_HistogramsResult() );
		}

		MS1_IntensitiesBinnedSummed_Summary_DataRoot summaryData =
				ms1_IntensitiesBinnedSummedMapRoot.getSummaryData();
		Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap =
				ms1_IntensitiesBinnedSummedMapRoot.getMs1_IntensitiesBinnedSummedMap();
		
//		long binnedSummedIntensityCount = summaryData.getBinnedSummedIntensityCount();

		double intensityBinnedMinActual = summaryData.getIntensityBinnedMin();
		double intensityBinnedMaxActual = summaryData.getIntensityBinnedMax();

		if ( log.isDebugEnabled() ) {
			log.debug( "summaryData.getRtBinMaxInSeconds(): " + summaryData.getRtBinMaxInSeconds() );
			log.debug( "summaryData.getRtMaxPossibleValueInSeconds(): " + summaryData.getRtMaxPossibleValueInSeconds() );

			log.debug( "summaryData.getMzBinMaxInMZ(): " + summaryData.getMzBinMaxInMZ() );
			log.debug( "summaryData.getMzMaxPossibleValueInMZ(): " + summaryData.getMzMaxPossibleValueInMZ() );

			log.debug( "intensityBinnedMinActual: " + intensityBinnedMinActual );
			log.debug( "intensityBinnedMaxActual: " + intensityBinnedMaxActual );
		}
		
		//  Sum Intensities by both Retention Time and M over Z

		Map<Long, MutableDouble> ms1_IntensitiesBinnedSummedMappedByRetentionTime = new HashMap<>();
		Map<Long, MutableDouble> ms1_IntensitiesBinnedSummedMappedBy_M_Over_Z = new HashMap<>();
		
		for ( Map.Entry<Long, Map<Long, Double>> retentionTimeEntry : ms1_IntensitiesBinnedSummedMap.entrySet() ) {
			double intensitySummedForRetentionTime = 0;
			for ( Map.Entry<Long, Double> m_Over_Z_Entry : retentionTimeEntry.getValue().entrySet() ) {
				//  intensityForBin is for Retention Time / M over Z bin
				double intensityForBin = m_Over_Z_Entry.getValue();
				intensitySummedForRetentionTime += intensityForBin;
				MutableDouble intensitySummedFor_M_Over_Z = ms1_IntensitiesBinnedSummedMappedBy_M_Over_Z.get( m_Over_Z_Entry.getKey() );
				if ( intensitySummedFor_M_Over_Z == null ) {
					intensitySummedFor_M_Over_Z = new MutableDouble( intensityForBin );
					ms1_IntensitiesBinnedSummedMappedBy_M_Over_Z.put( m_Over_Z_Entry.getKey(), intensitySummedFor_M_Over_Z );
				} else {
					intensitySummedFor_M_Over_Z.add( intensityForBin );
				}
			}
			ms1_IntensitiesBinnedSummedMappedByRetentionTime.put( 
					retentionTimeEntry.getKey(), new MutableDouble( intensitySummedForRetentionTime ) );
		}

		Scan_MS_1_IonCurrent_HistogramsResultForChartType dataForRetentionTimeChart =
				getChartData( 
						ChartType.RETENTION_TIME,
						ms1_IntensitiesBinnedSummedMappedByRetentionTime,
						summaryData.getRtBinMinInSeconds(),
						summaryData.getRtBinMaxInSeconds(),
						summaryData.getRtMaxPossibleValueInSeconds()
						);
		Scan_MS_1_IonCurrent_HistogramsResultForChartType dataFor_M_Over_Z_Chart =
				getChartData( 
						ChartType.M_OVER_Z,
						ms1_IntensitiesBinnedSummedMappedBy_M_Over_Z,
						summaryData.getMzBinMinInMZ(),
						summaryData.getMzBinMaxInMZ(),
						summaryData.getMzMaxPossibleValueInMZ()
						);
		
		Scan_MS_1_IonCurrent_HistogramsResult result = new Scan_MS_1_IonCurrent_HistogramsResult();
		result.setDataForRetentionTimeChart( dataForRetentionTimeChart );
		result.setDataFor_M_Over_Z_Chart( dataFor_M_Over_Z_Chart );
		

		resultsAsBytes = jacksonJSON_Mapper.writeValueAsBytes( result );
		
		Scan_MS_1_IonCurrent_Histograms_CachedResultManager.getSingletonInstance()
		.saveDataToCache( scanFileId, resultsAsBytes );
		
		return resultsAsBytes;
	}

	
	/**
	 * @param ms1_IntensitiesBinnedSummedMapped
	 * @param min
	 * @param max
	 * @param maxPossible
	 * @return
	 */
	private Scan_MS_1_IonCurrent_HistogramsResultForChartType getChartData( 
			ChartType chartType,
			Map<Long, MutableDouble> ms1_IntensitiesBinnedSummedMapped,
			long min, // Smallest bin value 
			long max, // Largest bin value
			long maxPossible //  Since data is binned
			) {
		
		if ( chartType == ChartType.RETENTION_TIME ) {
			// Convert to minutes from seconds
			min = min / 60;
			max = max / 60;
			maxPossible = maxPossible / 60;
		}
		
		long maxMinusMin = maxPossible - min;
		double maxMinusMinAsDouble = maxMinusMin; 
		
		//  Process data into bins
		int binCount = (int) ( Math.sqrt( ms1_IntensitiesBinnedSummedMapped.size() ) );
		double[] intensitySumsBinnedForChart = new double[ binCount ];
		double binSizeAsDouble = ( maxMinusMinAsDouble ) / binCount;
		
		for ( Map.Entry<Long, MutableDouble> entry : ms1_IntensitiesBinnedSummedMapped.entrySet() ) {
			long mapKey = entry.getKey();
			double intensitySummed = entry.getValue().doubleValue();
			double preMZ_Or_RT_Fraction = ( mapKey - min ) / maxMinusMinAsDouble;

			if ( chartType == ChartType.RETENTION_TIME ) {
				// Convert to minutes from seconds
				preMZ_Or_RT_Fraction = preMZ_Or_RT_Fraction / 60;
			}
			
			int bin = (int) ( (  preMZ_Or_RT_Fraction ) * binCount );
			if ( bin < 0 ) {
				bin = 0;
			} else if ( bin >= binCount ) {
				bin = binCount - 1;
			} 
			intensitySumsBinnedForChart[ bin ] += intensitySummed;  // Add to bins
		}
		
		List<Scan_MS_1_IonCurrent_HistogramsResultChartBucket> chartBuckets = new ArrayList<>();
		double binHalf = binSizeAsDouble / 2 ;
		//  Take the data in the bins and  create "buckets" in the format required for the charting API
		for ( int binIndex = 0; binIndex < intensitySumsBinnedForChart.length; binIndex++ ) {
			Scan_MS_1_IonCurrent_HistogramsResultChartBucket chartBucket = new Scan_MS_1_IonCurrent_HistogramsResultChartBucket();
			chartBuckets.add( chartBucket );
			double intensitySummed = intensitySumsBinnedForChart[ binIndex ];
			double binStartDouble = ( ( binIndex * binSizeAsDouble ) ) + min;
			if ( binIndex == 0 && binStartDouble < 0.1 ) {
				chartBucket.setBinStart( 0 );
			} else { 
				int binStart = (int)Math.round( binStartDouble );
				chartBucket.setBinStart( binStart );
			}
			int binEnd = (int)Math.round( ( ( binIndex + 1 ) * binSizeAsDouble ) + min );
			chartBucket.setBinEnd( binEnd );
			double binMiddleDouble = binStartDouble + binHalf;
			chartBucket.setBinCenter( binMiddleDouble );
			chartBucket.setIntensitySummed( intensitySummed );
		}
		
		Scan_MS_1_IonCurrent_HistogramsResultForChartType result = new Scan_MS_1_IonCurrent_HistogramsResultForChartType();
		
		result.setChartBuckets( chartBuckets );
		result.setMin( min );
		result.setMax( max );
		result.setMaxPossibleValue( maxPossible );
		
		return result;
	}

	/**
	 * @param scanFileId
	 * @return null if not in db
	 * @throws Exception
	 */
	private MS1_IntensitiesBinnedSummedMapRoot getMS1_IntensitiesBinnedSummedMapRoot( int scanFileId ) throws Exception {

		//  Get from Spectral Storage Service

		String spectralStorageAPIKey = ScanFileDAO.getInstance().getSpectralStorageAPIKeyById( scanFileId );

		if ( spectralStorageAPIKey == null ) {
			log.error( "No spectralStorageAPIKey value in scan file table for scanFileId: " + scanFileId );
			return null;  // EARLY RETURN
		}

		MS1_IntensitiesBinnedSummedMapRoot ms1_IntensitiesBinnedSummedMapRoot_FromSpectralStorage =
				Call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice.getSingletonInstance()
				.getScanPeakIntensityBinnedOn_RT_MZFromSpectralStorageService( spectralStorageAPIKey );

		if ( ms1_IntensitiesBinnedSummedMapRoot_FromSpectralStorage == null ) {

			log.error( "No data in Spectral Storage for ms1_IntensitiesBinnedSummedMapRoot_FromSpectralStorage. scanFileId: " + scanFileId 
					+ ", spectralStorageAPIKey: "
					+ spectralStorageAPIKey );

			return null;  // EARLY RETURN
		}

		return ms1_IntensitiesBinnedSummedMapRoot_FromSpectralStorage;
	}
}
