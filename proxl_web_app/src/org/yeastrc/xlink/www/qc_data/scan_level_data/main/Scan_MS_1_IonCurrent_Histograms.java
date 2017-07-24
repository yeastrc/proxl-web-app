package org.yeastrc.xlink.www.qc_data.scan_level_data.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ScanFileMS_1_IntensityBinnedSummedDataDAO;
import org.yeastrc.xlink.dto.ScanFileMS_1_IntensityBinnedSummedDataDTO;
import org.yeastrc.xlink.ms1_binned_summed_intensities.main.MS1_BinnedSummedIntensitiesProcessing;
import org.yeastrc.xlink.ms1_binned_summed_intensities.objects.MS1_IntensitiesBinnedSummedMapToJSONRoot;
import org.yeastrc.xlink.ms1_binned_summed_intensities.objects.MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot;
import org.yeastrc.xlink.utils.ZipUnzipByteArray;
import org.yeastrc.xlink.www.qc_data.scan_level_data.objects.Scan_MS_1_IonCurrent_HistogramsResult;
import org.yeastrc.xlink.www.qc_data.scan_level_data.objects.Scan_MS_1_IonCurrent_HistogramsResult.Scan_MS_1_IonCurrent_HistogramsResultChartBucket;
import org.yeastrc.xlink.www.qc_data.scan_level_data.objects.Scan_MS_1_IonCurrent_HistogramsResult.Scan_MS_1_IonCurrent_HistogramsResultForChartType;

/**
 * 
 *
 */
public class Scan_MS_1_IonCurrent_Histograms {

	private static final Logger log = Logger.getLogger( Scan_MS_1_IonCurrent_Histograms.class );

	/**
	 * private constructor
	 */
	private Scan_MS_1_IonCurrent_Histograms(){}
	public static Scan_MS_1_IonCurrent_Histograms getInstance( ) throws Exception {
		Scan_MS_1_IonCurrent_Histograms instance = new Scan_MS_1_IonCurrent_Histograms();
		return instance;
	}
	
	/**
	 * @param scanFileId
	 * @return
	 * @throws Exception 
	 */
	public Scan_MS_1_IonCurrent_HistogramsResult getScan_MS_1_IonCurrent_HistogramsResult( int scanFileId ) throws Exception {
		
		MS1_IntensitiesBinnedSummedMapToJSONRoot ms1_IntensitiesBinnedSummedMapToJSONRoot =
				getMS1_IntensitiesBinnedSummedMapToJSONRoot( scanFileId );

		if ( ms1_IntensitiesBinnedSummedMapToJSONRoot == null ) {
			//  No data found for scanFileId so return
			return new Scan_MS_1_IonCurrent_HistogramsResult();
		}

		MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot summaryData =
				ms1_IntensitiesBinnedSummedMapToJSONRoot.getSummaryData();
		Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap =
				ms1_IntensitiesBinnedSummedMapToJSONRoot.getMs1_IntensitiesBinnedSummedMap();
		
//		long binnedSummedIntensityCount = summaryData.getBinnedSummedIntensityCount();

		double intensityBinnedMinActual = summaryData.getIntensityBinnedMin();
		double intensityBinnedMaxActual = summaryData.getIntensityBinnedMax();

		if ( log.isDebugEnabled() ) {
			log.debug( "summaryData.getRtBinMax(): " + summaryData.getRtBinMax() );
			log.debug( "summaryData.getRtMaxPossibleValue(): " + summaryData.getRtMaxPossibleValue() );

			log.debug( "summaryData.getMzBinMax(): " + summaryData.getMzBinMax() );
			log.debug( "summaryData.getMzMaxPossibleValue(): " + summaryData.getMzMaxPossibleValue() );

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
						ms1_IntensitiesBinnedSummedMappedByRetentionTime,
						summaryData.getRtBinMin(),
						summaryData.getRtBinMax(),
						summaryData.getRtMaxPossibleValue()
						);
		Scan_MS_1_IonCurrent_HistogramsResultForChartType dataFor_M_Over_Z_Chart =
				getChartData( 
						ms1_IntensitiesBinnedSummedMappedBy_M_Over_Z,
						summaryData.getMzBinMin(),
						summaryData.getMzBinMax(),
						summaryData.getMzMaxPossibleValue()
						);
		
		Scan_MS_1_IonCurrent_HistogramsResult result = new Scan_MS_1_IonCurrent_HistogramsResult();
		result.setDataForRetentionTimeChart( dataForRetentionTimeChart );
		result.setDataFor_M_Over_Z_Chart( dataFor_M_Over_Z_Chart );
		return result;
	}

	
	/**
	 * @param ms1_IntensitiesBinnedSummedMapped
	 * @param min
	 * @param max
	 * @param maxPossible
	 * @return
	 */
	private Scan_MS_1_IonCurrent_HistogramsResultForChartType getChartData( 
			Map<Long, MutableDouble> ms1_IntensitiesBinnedSummedMapped,
			long min, // Smallest bin value 
			long max, // Largest bin value
			long maxPossible //  Since data is binned
			) {
		
		long maxMinusMin = maxPossible - min;
		double maxMinusMinAsDouble = maxMinusMin; 
		
		//  Process data into bins
		int binCount = (int) ( Math.sqrt( ms1_IntensitiesBinnedSummedMapped.size() ) );
		double[] intensitySumsBinnedForChart = new double[ binCount ];
		double binSizeAsDouble = ( maxMinusMinAsDouble ) / binCount;
		
		for ( Map.Entry<Long, MutableDouble> entry : ms1_IntensitiesBinnedSummedMapped.entrySet() ) {
			long mapKey = entry.getKey();
			double intensitySummed = entry.getValue().doubleValue();
			double preMZFraction = ( mapKey - min ) / maxMinusMinAsDouble;
			int bin = (int) ( (  preMZFraction ) * binCount );
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
	private MS1_IntensitiesBinnedSummedMapToJSONRoot getMS1_IntensitiesBinnedSummedMapToJSONRoot( int scanFileId ) throws Exception {

		ScanFileMS_1_IntensityBinnedSummedDataDTO scanFileMS_1_IntensityBinnedSummedDataDTO =
				ScanFileMS_1_IntensityBinnedSummedDataDAO.getFromScanFileId( scanFileId );
		if ( scanFileMS_1_IntensityBinnedSummedDataDTO == null ) {
			return null;
		}
		byte[] dataJSON_Gzipped = scanFileMS_1_IntensityBinnedSummedDataDTO.getDataJSON_Gzipped();
		byte[] dataJSON = ZipUnzipByteArray.getInstance().unzipByteArray( dataJSON_Gzipped );

		MS1_IntensitiesBinnedSummedMapToJSONRoot ms1_IntensitiesBinnedSummedMapToJSONRoot =
				MS1_BinnedSummedIntensitiesProcessing.getInstance().getMainObjectFromBytes( dataJSON );

		return ms1_IntensitiesBinnedSummedMapToJSONRoot;
	}
}
