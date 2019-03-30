package org.yeastrc.xlink.ms1_binned_summed_intensities.main;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableDouble;
//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.yeastrc.xlink.ms1_binned_summed_intensities.objects.MS1_IntensitiesBinnedSummedMapToJSONRoot;
import org.yeastrc.xlink.ms1_binned_summed_intensities.objects.MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot;

/**
 * Converts MS1 Binned Summed Intensities To/From JSON
 */
public class MS1_BinnedSummedIntensitiesProcessing {

//	private static final Logger log = LoggerFactory.getLogger( MS1_BinnedSummedIntensitiesProcessing.class);
	private MS1_BinnedSummedIntensitiesProcessing() { }
	public static MS1_BinnedSummedIntensitiesProcessing getInstance() { return new MS1_BinnedSummedIntensitiesProcessing(); }

	/**
	 * Significant digits for rounding the Total Ion Current for a single bin
	 */
	private static final int BINNED_SUMMED_INTENSITY_SIGNIFICANT_DIGITS = 5;
	
	private static final String MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot_JSON_CONTENTS_TEXT =
			" 'BinMax' props are max bin values.  bin values are 'floor' of actual values. "
				+ " Have 'MaxPossibleValue' props since MaxPossibleValue is BinMax + 1 " ;
	
	private static final String MS1_IntensitiesBinnedSummedMapToJSONRoot_JSON_CONTENTS_TEXT =
			"ms1_IntensitiesBinnedSummedMap outer key is RT, inner Key is m/z. "
				+ "Both have been binned using 'floor' to next smaller int."
				+ " Have 'MaxPossibleValue' props since MaxPossibleValue is BinMax + 1 " ;
	
	/**
	 * Result class
	 *
	 */
	public static class MS1_BinnedSummedIntensitiesProcessingResult {
		
		byte[] fullJSON;
		byte[] summaryJSON;
		MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot summaryObject;
		MS1_IntensitiesBinnedSummedMapToJSONRoot intensitiesMapFullData;
		
		public byte[] getFullJSON() {
			return fullJSON;
		}
		public void setFullJSON(byte[] fullJSON) {
			this.fullJSON = fullJSON;
		}
		public byte[] getSummaryJSON() {
			return summaryJSON;
		}
		public void setSummaryJSON(byte[] summaryJSON) {
			this.summaryJSON = summaryJSON;
		}
		public MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot getSummaryObject() {
			return summaryObject;
		}
		public void setSummaryObject(MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot summaryObject) {
			this.summaryObject = summaryObject;
		}
		public MS1_IntensitiesBinnedSummedMapToJSONRoot getIntensitiesMapFullData() {
			return intensitiesMapFullData;
		}
		public void setIntensitiesMapFullData(MS1_IntensitiesBinnedSummedMapToJSONRoot intensitiesMapFullData) {
			this.intensitiesMapFullData = intensitiesMapFullData;
		}
	}
	
	/**
	 * Process input parameter, rounding the summed intensity, generating a result map that is then serialized to JSON.
	 * Create a summary object, inserting that into main returned object and also serializing it to JSON.
	 * 
	 * @param ms1_IntensitiesBinnedSummedMap: for ms 1 scans: Map<RetentionTime_Floor,Map<MZ_Floor,SummedIntensity>
	 *        "_Floor" means truncate any decimal fraction part
	 * @return bytes for storage in DB, Map of data with rounded summed intensities, summary object
	 * @throws Exception 
	 */
	public MS1_BinnedSummedIntensitiesProcessingResult getBytesAndSummaryObjectFrom_Ms1_IntensitiesBinnedSummedMap( 
			Map<Long, Map<Long, MutableDouble>> ms1_IntensitiesMutableDoubleBinnedSummedMap ) throws Exception {

		MS1_BinnedSummedIntensitiesProcessingResult ms1_BinnedSummedIntensitiesProcessingResult = new MS1_BinnedSummedIntensitiesProcessingResult();
		
		Map<Long, Map<Long, Double>> ms1_IntensitiesDoubleBinnedSummedMap = new HashMap<>();
		
		boolean firstRT = true;
		boolean firstMZ = true;
		long rtBinMin = 0;
		long rtBinMax = 0;
		long mzBinMin = 0;
		long mzBinMax = 0;
		double intensityBinnedMin = 0;
		double intensityBinnedMax = 0;
		
		 // Count # binned Summed Intensity values
		long binnedSummedIntensityCount = 0;
		
		MathContext mathContextSignificantDigits = new MathContext( BINNED_SUMMED_INTENSITY_SIGNIFICANT_DIGITS );
		
		for ( Map.Entry<Long, Map<Long, MutableDouble>> entryKeyedRT : ms1_IntensitiesMutableDoubleBinnedSummedMap.entrySet() ) {
			long rtValue = entryKeyedRT.getKey();
			if ( firstRT ) {
				firstRT = false;
				rtBinMin = rtValue;
				rtBinMax = rtValue;
			} else {
				if ( rtValue < rtBinMin ) {
					rtBinMin = rtValue;
				}
				if ( rtValue > rtBinMax ) {
					rtBinMax = rtValue;
				}
			}
			Map<Long, Double> ms1_IntensitiesDoubleBinnedSummedMapKeyMZ = new HashMap<>();
			ms1_IntensitiesDoubleBinnedSummedMap.put( entryKeyedRT.getKey(), ms1_IntensitiesDoubleBinnedSummedMapKeyMZ );
			for ( Map.Entry<Long, MutableDouble> entryKeyedMZ : entryKeyedRT.getValue().entrySet() ) {
				long mzValue = entryKeyedMZ.getKey();
				
				//  Round binnedIntensity
				double binnedIntensityNotRounded = entryKeyedMZ.getValue().getValue();
				BigDecimal binnedIntensityRoundedBD = new BigDecimal( binnedIntensityNotRounded, mathContextSignificantDigits );
				double binnedIntensityRounded = binnedIntensityRoundedBD.doubleValue();
						
				binnedSummedIntensityCount++; // Count # binned Summed Intensity values
				if ( firstMZ) {
					firstMZ = false;
					mzBinMin = mzValue;
					mzBinMax = mzValue;
					intensityBinnedMin = binnedIntensityRounded;
					intensityBinnedMax = binnedIntensityRounded;
				} else {
					if ( mzValue < mzBinMin ) {
						mzBinMin = mzValue;
					}
					if ( mzValue > mzBinMax ) {
						mzBinMax = mzValue;
					}
					if ( binnedIntensityRounded < intensityBinnedMin ) {
						intensityBinnedMin = binnedIntensityRounded;
					}
					if ( binnedIntensityRounded > intensityBinnedMax ) {
						intensityBinnedMax = binnedIntensityRounded;
					}
				}
				ms1_IntensitiesDoubleBinnedSummedMapKeyMZ.put( entryKeyedMZ.getKey(), binnedIntensityRounded );
			}
		}
		
		MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot summaryData = new MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot();
		summaryData.setJsonContents( MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot_JSON_CONTENTS_TEXT );
		summaryData.setBinnedSummedIntensityCount( binnedSummedIntensityCount );
		summaryData.setRtBinMax( rtBinMax );
		summaryData.setRtBinMin( rtBinMin );
		summaryData.setMzBinMax( mzBinMax );
		summaryData.setMzBinMin( mzBinMin );
		summaryData.setIntensityBinnedMin( intensityBinnedMin );
		summaryData.setIntensityBinnedMax( intensityBinnedMax );
		
		// Have 'MaxPossibleValue' props since Max Possible Value is BinMax + 1 ( The Bin value is Floor(value) )
		summaryData.setRtMaxPossibleValue( rtBinMax + 1 );
		summaryData.setMzMaxPossibleValue( mzBinMax + 1 );
		
		MS1_IntensitiesBinnedSummedMapToJSONRoot intensitiesMapToJSONRoot = new MS1_IntensitiesBinnedSummedMapToJSONRoot();
		intensitiesMapToJSONRoot.setJsonContents( MS1_IntensitiesBinnedSummedMapToJSONRoot_JSON_CONTENTS_TEXT );
		intensitiesMapToJSONRoot.setSummaryData( summaryData );
		intensitiesMapToJSONRoot.setMs1_IntensitiesBinnedSummedMap( ms1_IntensitiesDoubleBinnedSummedMap );

		
		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		
		//  Serialize intensitiesMapToJSONRoot to JSON
		byte[] intensitiesMapToJSONRootAsBytes = jacksonJSON_Mapper.writeValueAsBytes( intensitiesMapToJSONRoot );
		//  Serialize summaryData to JSON
		byte[] summaryDataAsBytes = jacksonJSON_Mapper.writeValueAsBytes( summaryData );

		ms1_BinnedSummedIntensitiesProcessingResult.setFullJSON( intensitiesMapToJSONRootAsBytes );
		ms1_BinnedSummedIntensitiesProcessingResult.setSummaryJSON( summaryDataAsBytes );
		ms1_BinnedSummedIntensitiesProcessingResult.setSummaryObject( summaryData );
		ms1_BinnedSummedIntensitiesProcessingResult.setIntensitiesMapFullData( intensitiesMapToJSONRoot );
		
//		//  START: sanity check can deserialize map
//		MS1_IntensitiesBinnedSummedMapToJSONRoot intensitiesMapToJSONRootDeserialized = jacksonJSON_Mapper.readValue( ms1_IntensitiesBinnedSummedMapAsBytes, MS1_IntensitiesBinnedSummedMapToJSONRoot.class );
//		
//		Long ms1_IntensitiesBinnedSummedMapFirstKey = ms1_IntensitiesMutableDoubleBinnedSummedMap.entrySet().iterator().next().getKey();
//		
//		if ( intensitiesMapToJSONRootDeserialized.getMs1_IntensitiesBinnedSummedMap().get( ms1_IntensitiesBinnedSummedMapFirstKey ) == null ) {
//			String msg = "ms1_IntensitiesBinnedSummedMapFirstKey NOT found in deserialized map: " + ms1_IntensitiesBinnedSummedMapFirstKey;
//			System.err.println( msg );
//			throw new Exception( msg );
//		}
//		{
//			String msg = "ms1_IntensitiesBinnedSummedMapFirstKey IS found in deserialized map: " + ms1_IntensitiesBinnedSummedMapFirstKey;
//			System.out.println( msg );
//		}
//		//  END: sanity check can deserialize map
		
		
		
		return ms1_BinnedSummedIntensitiesProcessingResult;
	}
	
	
	/**
	 * Converts byte[] into MS1_IntensitiesBinnedSummedMapToJSONRoot
	 * 
	 * @param bytes
	 * @return
	 */
	public MS1_IntensitiesBinnedSummedMapToJSONRoot getMainObjectFromBytes( byte[] bytes ) throws Exception {

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		
//		//  deserialize JSON
		MS1_IntensitiesBinnedSummedMapToJSONRoot intensitiesMapToJSONRootDeserialized = 
				jacksonJSON_Mapper.readValue( bytes, MS1_IntensitiesBinnedSummedMapToJSONRoot.class );

		return intensitiesMapToJSONRootDeserialized;
	}
	
}
