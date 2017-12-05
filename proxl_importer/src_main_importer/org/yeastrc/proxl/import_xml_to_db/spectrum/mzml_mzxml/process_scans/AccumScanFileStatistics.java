package org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.process_scans;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableDouble;
//import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.dto.MzML_MzXmlScan;
import org.yeastrc.xlink.base.spectrum.common.dto.Peak;
import org.yeastrc.xlink.ms_1_2_summed_intensities_per_scan.objects.MS_1_SummedIntensities_TIC_PerScan_Entry;
import org.yeastrc.xlink.ms_1_2_summed_intensities_per_scan.objects.MS_1_SummedIntensities_TIC_PerScan_JSONRoot;
import org.yeastrc.xlink.ms_1_2_summed_intensities_per_scan.objects.MS_2_SummedIntensities_TIC_PerScan_Entry;
import org.yeastrc.xlink.ms_1_2_summed_intensities_per_scan.objects.MS_2_SummedIntensities_TIC_PerScan_JSONRoot;

/**
 * Accumulate Scan File Statistics
 *
 */
public class AccumScanFileStatistics {

//
////	private static final Logger log = Logger.getLogger(AccumScanFileStatistics.class);
//	private AccumScanFileStatistics() { }
//	public static AccumScanFileStatistics getInstance() { return new AccumScanFileStatistics(); }
//
//	/**
//	 * Significant digits for rounding the Retention Time and Total Ion Current for a single scan
//	 */
//	private static final int SUMMED_INTENSITY_FOR_SINGLE_SCAN_SIGNIFICANT_DIGITS = 7;
//	
//	
//	private long scanCounter = 0;
//	private long ms1_ScanCounter = 0;
//	private long ms2_ScanCounter = 0;
//	
//	private long ms1_RT_NotMatchesPrevRT_Counter = 0;
//	private long ms1_MZ_NotMatchesPrevMZ_Counter = 0;
//	
//	private double ms1_ScanIntensitiesSummed = 0;
//	private double ms2_ScanIntensitiesSummed = 0;
//	
//	/**
//	 * for ms 1 scans: Map<RetentionTime_Floor,Map<MZ_Floor,SummedIntensity>
//	 * 
//	 * "_Floor" means truncate any decimal fraction part
//	 */
//	private Map<Long, Map<Long, MutableDouble>> ms1_IntensitiesBinnedSummedMap = new HashMap<>();
//	
//	//  Optimization for when prev retentionTimeFloor is same as current retentionTimeFloor
//	private long prevRetentionTimeFloor = 0;
//	private Map<Long, MutableDouble> ms1_IntensitiesSummedMapForRetentionTime = null;
//	
//	//  Collect data for each ms1 and ms2 scans
//	private List<MS_1_SummedIntensities_TIC_PerScan_Entry> ms_1_PerScanDataList = new ArrayList<>();
//	private List<MS_2_SummedIntensities_TIC_PerScan_Entry> ms_2_PerScanDataList = new ArrayList<>();
//
//	
//	/**
//	 * @param scanIn
//	 */
//	public void processScanForAccum( MzML_MzXmlScan scanIn ) {
//		//  Sum up intensities
//        if ( scanIn.getMsLevel() == 1 )  {
//        	ms1_ScanCounter++;
//        } else if ( scanIn.getMsLevel() == 2 )  {
//        	ms2_ScanCounter++;
//        } else {
//
//        }
//        
//        BigDecimal retentionTime = scanIn.getRetentionTime();
//        long retentionTimeFloor = retentionTime.longValue();  // Truncate the decimal fraction
//
//    	//  Optimization for when prev retentionTimeFloor is same as current retentionTimeFloor
//		if ( ms1_IntensitiesSummedMapForRetentionTime == null || retentionTimeFloor != prevRetentionTimeFloor ) {
//			ms1_RT_NotMatchesPrevRT_Counter++;
//			ms1_IntensitiesSummedMapForRetentionTime = ms1_IntensitiesBinnedSummedMap.get( retentionTimeFloor );
//			if ( ms1_IntensitiesSummedMapForRetentionTime == null ) {
//				ms1_IntensitiesSummedMapForRetentionTime = new HashMap<>();
//				ms1_IntensitiesBinnedSummedMap.put( retentionTimeFloor, ms1_IntensitiesSummedMapForRetentionTime );
//			}
//		}
//		prevRetentionTimeFloor = retentionTimeFloor;
//        
//		//  Optimization for when prev mzFloor is same as current mzFloor
//        long prevMZFloor = 0;
//        MutableDouble intensitySummedForRT_MZ = null;
//        
//        double scanIntensitiesSummedForScan = 0;
//        
//		List<Peak> peakList = scanIn.getPeaks();
//		for ( Peak peak : peakList ) {
//			
//			float peakIntensity = peak.getIntensity();
//
//        	scanIntensitiesSummedForScan += peakIntensity;
//        	
//	        if ( scanIn.getMsLevel() == 1)  {
//	        	
//				long mzFloor = (long)peak.getMz();  // Truncate the decimal fraction
//				//  Optimization for when prev mzFloor is same as current mzFloor
//				if ( intensitySummedForRT_MZ == null ||  mzFloor != prevMZFloor ) {
//					ms1_MZ_NotMatchesPrevMZ_Counter++;
//					intensitySummedForRT_MZ = ms1_IntensitiesSummedMapForRetentionTime.get( mzFloor );
//					if ( intensitySummedForRT_MZ == null ) {
//						intensitySummedForRT_MZ = new MutableDouble();
//						ms1_IntensitiesSummedMapForRetentionTime.put( mzFloor, intensitySummedForRT_MZ );
//					}
//				}
//		        intensitySummedForRT_MZ.add( peakIntensity );
//		        prevMZFloor = mzFloor;
//		        
//	        } else {
//	        }
//		}
//		
//		MathContext mathContextSignificantDigits = 
//				new MathContext( SUMMED_INTENSITY_FOR_SINGLE_SCAN_SIGNIFICANT_DIGITS );
//
//		BigDecimal scanIntensitiesSummedForScanRoundedBD = new BigDecimal( scanIntensitiesSummedForScan, mathContextSignificantDigits );
//		double scanIntensitiesSummedForScanRounded = scanIntensitiesSummedForScanRoundedBD.doubleValue();
//
//		BigDecimal scanRetentionTimeRounded = scanIn.getRetentionTime().round( mathContextSignificantDigits );
//
//        if ( scanIn.getMsLevel() == 1)  {
//        	ms1_ScanIntensitiesSummed += scanIntensitiesSummedForScan;
//
//        	MS_1_SummedIntensities_TIC_PerScan_Entry perScanEntry = new MS_1_SummedIntensities_TIC_PerScan_Entry();
//        	ms_1_PerScanDataList.add(perScanEntry);
//        	perScanEntry.setSn( scanIn.getStartScanNum() );
//        	perScanEntry.setRt( scanRetentionTimeRounded );
//        	perScanEntry.setTic( scanIntensitiesSummedForScanRounded );
//
//        } else {
//        	ms2_ScanIntensitiesSummed += scanIntensitiesSummedForScan;
//
//        	MS_2_SummedIntensities_TIC_PerScan_Entry perScanEntry = new MS_2_SummedIntensities_TIC_PerScan_Entry();
//        	ms_2_PerScanDataList.add(perScanEntry);
//        	perScanEntry.setSn( scanIn.getStartScanNum() );
//        	perScanEntry.setRt( scanRetentionTimeRounded );
//        	perScanEntry.setTic( scanIntensitiesSummedForScanRounded );
//        }
//	}
//	
//	/**
//	 * 
//	 */
//	public void printStats() {
//		System.out.println( "**********************************************" );
//		System.out.println( "AccumScanFileStatistics.printStats()");
//		System.out.println();
//		System.out.println( "ms1_ScanCounter: " + ms1_ScanCounter);
//		System.out.println( "ms2_ScanCounter: " + ms2_ScanCounter);
//		System.out.println();
//		System.out.println( "ms1_ScanIntensitiesSummed: " + ms1_ScanIntensitiesSummed);
//		System.out.println( "ms2_ScanIntensitiesSummed: " + ms2_ScanIntensitiesSummed);
//		System.out.println();
//		System.out.println( "ms1_RT_NotMatchesPrevRT_Counter: " + ms1_RT_NotMatchesPrevRT_Counter );
//		System.out.println( "ms1_MZ_NotMatchesPrevMZ_Counter: " + ms1_MZ_NotMatchesPrevMZ_Counter );
//		System.out.println();
//		
//		Set<Long> uniqueRT = new HashSet<>();
//		Set<Long> uniqueMZ = new HashSet<>();
//
//		double ms1_IntensitiesBinnedSummedMap_SummedIntensities = 0;
//		double max_ms1_IntensitiesBinnedSummed = 0;
//		double min_ms1_IntensitiesBinnedSummed = 0;
//		long summedIntensityCount = 0;
//		long largestPerMZMapSize = 0;
//		boolean firstIntensityEntry = true;
//		for ( Map.Entry<Long, Map<Long, MutableDouble>> entryKeyedRT : ms1_IntensitiesBinnedSummedMap.entrySet() ) {
////			System.out.println( "RetentionTime: " + entryKeyedRT.getKey() );
//			uniqueRT.add( entryKeyedRT.getKey() );
//			double ms1_IntensitiesBinnedSummedMap_SummedIntensitiesForRT = 0;
//			for ( Map.Entry<Long, MutableDouble> entryKeyedMZ : entryKeyedRT.getValue().entrySet() ) {
//				double intensity = entryKeyedMZ.getValue().doubleValue();
//				uniqueMZ.add( entryKeyedMZ.getKey() );
//				summedIntensityCount++;
//				ms1_IntensitiesBinnedSummedMap_SummedIntensitiesForRT += intensity;
////				System.out.println( "         MZ: " + entryKeyedMZ.getKey() 
////				+ ", Binned Summed Intensity: " + entryKeyedMZ.getValue() );
//				if ( firstIntensityEntry ) {
//					firstIntensityEntry = false;
//					max_ms1_IntensitiesBinnedSummed = intensity;
//					min_ms1_IntensitiesBinnedSummed = intensity;
//				}
//				if ( intensity > max_ms1_IntensitiesBinnedSummed ) {
//					max_ms1_IntensitiesBinnedSummed = intensity;
//				}
//				if ( intensity < min_ms1_IntensitiesBinnedSummed ) {
//					min_ms1_IntensitiesBinnedSummed = intensity;
//				}
//			}
//			ms1_IntensitiesBinnedSummedMap_SummedIntensities += ms1_IntensitiesBinnedSummedMap_SummedIntensitiesForRT;
//			int perMZMapSize = entryKeyedRT.getValue().size();
//			if ( perMZMapSize > largestPerMZMapSize ) {
//				largestPerMZMapSize = perMZMapSize;
//			}
//		}
//		
//		System.out.println( "ms1_IntensitiesBinnedSummedMap_SummedIntensities: " + ms1_IntensitiesBinnedSummedMap_SummedIntensities );
//		System.out.println( "summedIntensityCount: " + summedIntensityCount );
//		System.out.println( "largestPerMZMapSize: " + largestPerMZMapSize );
//		System.out.println( "ms1_IntensitiesBinnedSummedMap Size: " + ms1_IntensitiesBinnedSummedMap.size() );
//
//		System.out.println( "uniqueRT Size: " + uniqueRT.size() );
//		System.out.println( "uniqueMZ Size: " + uniqueMZ.size() );
//
//		System.out.println( "**********************************************");
//	}
//
//	/**
//	 * @return
//	 */
//	public MS_1_SummedIntensities_TIC_PerScan_JSONRoot getMS_1_SummedIntensities_TIC_PerScan_JSONRoot() {
//		MS_1_SummedIntensities_TIC_PerScan_JSONRoot result = new MS_1_SummedIntensities_TIC_PerScan_JSONRoot();
//		result.setScanEntries( ms_1_PerScanDataList );
//		return result;
//	}
//	
//	/**
//	 * @return
//	 */
//	public MS_2_SummedIntensities_TIC_PerScan_JSONRoot getMS_2_SummedIntensities_TIC_PerScan_JSONRoot() {
//		MS_2_SummedIntensities_TIC_PerScan_JSONRoot result = new MS_2_SummedIntensities_TIC_PerScan_JSONRoot();
//		result.setScanEntries( ms_2_PerScanDataList );
//		return result;
//	}
//	
//	
//	public long getMs2_ScanCounter() {
//		return ms2_ScanCounter;
//	}
//	public double getMs2_ScanIntensitiesSummed() {
//		return ms2_ScanIntensitiesSummed;
//	}
//	public long getMs1_ScanCounter() {
//		return ms1_ScanCounter;
//	}
//	public double getMs1_ScanIntensitiesSummed() {
//		return ms1_ScanIntensitiesSummed;
//	}
//	public long getScanCounter() {
//		return scanCounter;
//	}
//	public Map<Long, Map<Long, MutableDouble>> getMs1_IntensitiesSummedMap() {
//		return ms1_IntensitiesBinnedSummedMap;
//	}

}
