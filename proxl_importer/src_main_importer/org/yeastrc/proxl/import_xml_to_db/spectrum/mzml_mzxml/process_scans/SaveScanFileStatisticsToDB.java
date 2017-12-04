package org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.process_scans;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.ScanFileStatistics_Importer_DAO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.xlink.dao.ScanFileMS_1_IntensityBinnedSummedSummaryDAO;
import org.yeastrc.xlink.dao.ScanFileMS_1_IntensityBinnedSummedSummaryDataDAO;
import org.yeastrc.xlink.dao.ScanFileMS_1_PerScanData_Num_TIC_RT_DAO;
import org.yeastrc.xlink.dao.ScanFileMS_2_PerScanData_Num_TIC_RT_DAO;
import org.yeastrc.xlink.dao.ScanFileStatisticsDAO;
import org.yeastrc.xlink.dto.ScanFileMS_1_IntensityBinnedSummedSummaryDTO;
import org.yeastrc.xlink.dto.ScanFileMS_1_IntensityBinnedSummedSummaryDataDTO;
import org.yeastrc.xlink.dto.ScanFileMS_1_PerScanData_Num_TIC_RT_DTO;
import org.yeastrc.xlink.dto.ScanFileMS_2_PerScanData_Num_TIC_RT_DTO;
import org.yeastrc.xlink.dto.ScanFileStatisticsDTO;
import org.yeastrc.xlink.ms1_binned_summed_intensities.main.MS1_BinnedSummedIntensitiesProcessing;
import org.yeastrc.xlink.ms1_binned_summed_intensities.main.MS1_BinnedSummedIntensitiesProcessing.MS1_BinnedSummedIntensitiesProcessingResult;
import org.yeastrc.xlink.ms1_binned_summed_intensities.objects.MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot;
import org.yeastrc.xlink.utils.ZipUnzipByteArray;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class SaveScanFileStatisticsToDB {

	private static final Logger log = Logger.getLogger(SaveScanFileStatisticsToDB.class);
	private SaveScanFileStatisticsToDB() { }
	public static SaveScanFileStatisticsToDB getInstance() { return new SaveScanFileStatisticsToDB(); }
	
	/**
	 * @param accumScanFileStatistics
	 * @param scanFileDTO
	 * @throws Exception 
	 */
	public void saveScanFileStatisticsToDB( AccumScanFileStatistics accumScanFileStatistics, int scanFileId, String scanFilename ) throws Exception {
		
		saveScanFileStatisticsDTO( accumScanFileStatistics, scanFileId, scanFilename );
		
		saveMs1_IntensitiesSummedMap( accumScanFileStatistics, scanFileId, scanFilename );
			
	}
	
	/**
	 * @param accumScanFileStatistics
	 * @param scanFileId
	 * @param scanFilename
	 * @throws ProxlImporterInteralException
	 */
	private void saveScanFileStatisticsDTO( AccumScanFileStatistics accumScanFileStatistics, int scanFileId, String scanFilename ) throws ProxlImporterInteralException {
		
		ScanFileStatisticsDTO scanFileStatisticsDTO = null;
		try {
			//  First get from DB for scan file id:
			scanFileStatisticsDTO = ScanFileStatisticsDAO.getInstance().getScanFileStatisticsDTOForScanFileId( scanFileId );
		} catch ( Exception e ) {
			String msg = "Failed to retrieve Scan File Statistics table";
			log.error( msg, e );
			throw new ProxlImporterInteralException(msg, e);
		}

		if ( scanFileStatisticsDTO != null ) {
			//  In DB so compare statistics and error if different
			if ( accumScanFileStatistics.getMs1_ScanCounter() != scanFileStatisticsDTO.getMs_1_ScanCount()
					|| accumScanFileStatistics.getMs1_ScanIntensitiesSummed() != scanFileStatisticsDTO.getMs_1_ScanIntensitiesSummed()
					|| accumScanFileStatistics.getMs2_ScanCounter() != scanFileStatisticsDTO.getMs_2_ScanCount() 
					|| accumScanFileStatistics.getMs2_ScanIntensitiesSummed() != scanFileStatisticsDTO.getMs_2_ScanIntensitiesSummed()
					) {
				String msg = "Statistics in DB are diffent than computed for scanFileId: " + scanFileId
						+ ", scanFilename: " + scanFilename
						+ ".\n scanFile.getMs1_ScanCounter(): " +  accumScanFileStatistics.getMs1_ScanCounter()
						+ ",  scanFile.getMs1_ScanIntensitiesSummed(): " +  accumScanFileStatistics.getMs1_ScanIntensitiesSummed()
						+ ",  scanFile.getMs2_ScanCounter(): " +  accumScanFileStatistics.getMs2_ScanCounter()
						+ ",  scanFile.getMs2_ScanIntensitiesSummed(): " +  accumScanFileStatistics.getMs2_ScanIntensitiesSummed()
						+ ".\n  db.getMs_1_ScanCount(): " +  scanFileStatisticsDTO.getMs_1_ScanCount()
						+ ", db.getMs_1_ScanIntensitiesSummed(): " +  scanFileStatisticsDTO.getMs_1_ScanIntensitiesSummed()
						+ ", db.getMs_2_ScanCount(): " +  scanFileStatisticsDTO.getMs_2_ScanCount()
						+ ", db.getMs_2_ScanIntensitiesSummed(): " +  scanFileStatisticsDTO.getMs_2_ScanIntensitiesSummed()
						;
				log.error( msg );
				throw new ProxlImporterInteralException(msg);
			}
			if ( log.isInfoEnabled() ) {
				log.info( "Statistics for this scan file are already in the database and match the statistics calculated processing the file this time." 
						+ "  Scan File Id: " + scanFileId + ", scanFilename: " + scanFilename );
			}
		} else {
			//  Not in DB so add
			if ( log.isInfoEnabled() ) {
				log.info( "Statistics for this scan file are NOT in the database so adding to the database." 
						+ "  Scan File Id: " + scanFileId + ", scanFilename: " + scanFilename );
			}
			try {
				scanFileStatisticsDTO = new ScanFileStatisticsDTO();
				scanFileStatisticsDTO.setScanFileId( scanFileId );
				scanFileStatisticsDTO.setMs_1_ScanCount( accumScanFileStatistics.getMs1_ScanCounter() );
				scanFileStatisticsDTO.setMs_1_ScanIntensitiesSummed( accumScanFileStatistics.getMs1_ScanIntensitiesSummed() );
				scanFileStatisticsDTO.setMs_2_ScanCount( accumScanFileStatistics.getMs2_ScanCounter() );
				scanFileStatisticsDTO.setMs_2_ScanIntensitiesSummed( accumScanFileStatistics.getMs2_ScanIntensitiesSummed() );

				ScanFileStatistics_Importer_DAO.getInstance().saveToDatabase( scanFileStatisticsDTO );
			} catch ( Exception e ) {
				String msg = "Failed to insert into Scan File Statistics table. Scan File Id: " + scanFileId + ", scanFilename: " + scanFilename;
				log.error( msg, e );
				throw new ProxlImporterInteralException(msg, e);
			}
		}
		
	}
	

	/**
	 * @param accumScanFileStatistics
	 * @param scanFileId
	 * @param scanFilename
	 * @throws Exception 
	 */
	private void saveMs1_IntensitiesSummedMap( AccumScanFileStatistics accumScanFileStatistics, int scanFileId, String scanFilename ) throws Exception {

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object


		///  output ms1_IntensitiesSummedMap
		
		MS1_BinnedSummedIntensitiesProcessingResult ms1_BinnedSummedIntensitiesProcessingResult = 
				MS1_BinnedSummedIntensitiesProcessing.getInstance()
				.getBytesAndSummaryObjectFrom_Ms1_IntensitiesBinnedSummedMap( accumScanFileStatistics.getMs1_IntensitiesSummedMap() );

		byte[] ms1_IntensitiesBinnedSummedDataJSONAsBytes = ms1_BinnedSummedIntensitiesProcessingResult.getFullJSON();
		byte[] ms1_IntensitiesBinnedSummedSummaryDataJSONAsBytes = ms1_BinnedSummedIntensitiesProcessingResult.getSummaryJSON();
		
		byte[] ms1_IntensitiesBinnedSummedDataJSONAsBytes_Gzipped = 
				ZipUnzipByteArray.getInstance().zipByteArray( ms1_IntensitiesBinnedSummedDataJSONAsBytes );

		MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot summaryObject =
				ms1_BinnedSummedIntensitiesProcessingResult.getSummaryObject();
		
		//  For each database table (independently), query the table for scanFileId and save if no record found
		
		{
			Integer scanFileIdFromDB = ScanFileMS_1_IntensityBinnedSummedSummaryDAO.getScanFileIdFromScanFileId( scanFileId );
			if ( scanFileIdFromDB == null ) {
				// Record not in DB so add it
				try {
					ScanFileMS_1_IntensityBinnedSummedSummaryDTO item = new ScanFileMS_1_IntensityBinnedSummedSummaryDTO();
					item.setScanFileId( scanFileId );
					item.setRetentionTimeMaxBinMinusMinBinPlusOne( summaryObject.getRtBinMax() - summaryObject.getRtBinMin() + 1 );
					item.setMzMaxBinMinusMinBinPlusOne( summaryObject.getMzBinMax() - summaryObject.getMzBinMin() + 1 );
					item.setIntensityMaxBinMinusMinBin( summaryObject.getIntensityBinnedMax() - summaryObject.getIntensityBinnedMin() );
					ScanFileMS_1_IntensityBinnedSummedSummaryDAO.save( item );
				} catch ( Exception e ) {
					Integer scanFileIdFromDBAfterInsert = ScanFileMS_1_IntensityBinnedSummedSummaryDAO.getScanFileIdFromScanFileId( scanFileId );
					if ( scanFileIdFromDBAfterInsert == null ) {
						//  Failed to insert and wasn't already inserted (if was already inserted, Duplicate key would be thrown to here)
						throw e;
					}
				}
			}
		}
		{
			Integer scanFileIdFromDB = ScanFileMS_1_IntensityBinnedSummedSummaryDataDAO.getScanFileIdFromScanFileId( scanFileId );
			if ( scanFileIdFromDB == null ) {
				// Record not in DB so add it
				try {
					ScanFileMS_1_IntensityBinnedSummedSummaryDataDTO item = new ScanFileMS_1_IntensityBinnedSummedSummaryDataDTO();
					item.setScanFileId( scanFileId );
					item.setSummaryDataJSON( ms1_IntensitiesBinnedSummedSummaryDataJSONAsBytes );
					ScanFileMS_1_IntensityBinnedSummedSummaryDataDAO.save( item );
				} catch ( Exception e ) {
					Integer scanFileIdFromDBAfterInsert = ScanFileMS_1_IntensityBinnedSummedSummaryDataDAO.getScanFileIdFromScanFileId( scanFileId );
					if ( scanFileIdFromDBAfterInsert == null ) {
						//  Failed to insert and wasn't already inserted (if was already inserted, Duplicate key would be thrown to here)
						throw e;
					}
				}
			}
		}
//		{
//			Integer scanFileIdFromDB = ScanFileMS_1_IntensityBinnedSummedDataDAO.getScanFileIdFromScanFileId( scanFileId );
//			if ( scanFileIdFromDB == null ) {
//				// Record not in DB so add it
//				try {
//					ScanFileMS_1_IntensityBinnedSummedDataDTO item = new ScanFileMS_1_IntensityBinnedSummedDataDTO();
//					item.setScanFileId( scanFileId );
//					item.setDataJSON_Gzipped( ms1_IntensitiesBinnedSummedDataJSONAsBytes_Gzipped );
//					ScanFileMS_1_IntensityBinnedSummedDataDAO.save( item );
//				} catch ( Exception e ) {
//					Integer scanFileIdFromDBAfterInsert = ScanFileMS_1_IntensityBinnedSummedDataDAO.getScanFileIdFromScanFileId( scanFileId );
//					if ( scanFileIdFromDBAfterInsert == null ) {
//						//  Failed to insert and wasn't already inserted (if was already inserted, Duplicate key would be thrown to here)
//						throw e;
//					}
//				}
//			}
//		}
		
		{
			Integer scanFileIdFromDB = ScanFileMS_1_PerScanData_Num_TIC_RT_DAO.getScanFileIdFromScanFileId( scanFileId );
			if ( scanFileIdFromDB == null ) {
				// Record not in DB so add it
				try {
					//  Serialize intensitiesMapToJSONRoot to JSON
					byte[] ms_1_PerScanDataJSONAsBytes = jacksonJSON_Mapper.writeValueAsBytes( accumScanFileStatistics.getMS_1_SummedIntensities_TIC_PerScan_JSONRoot() );
					//  Serialize summaryData to JSON
					
					byte[] ms_1_PerScanDataJSONAsBytes_Gzipped = 
							ZipUnzipByteArray.getInstance().zipByteArray( ms_1_PerScanDataJSONAsBytes );

					ScanFileMS_1_PerScanData_Num_TIC_RT_DTO item = new ScanFileMS_1_PerScanData_Num_TIC_RT_DTO();
					item.setScanFileId( scanFileId );
					item.setDataJSON_Gzipped( ms_1_PerScanDataJSONAsBytes_Gzipped );
					ScanFileMS_1_PerScanData_Num_TIC_RT_DAO.save( item );
				} catch ( Exception e ) {
					Integer scanFileIdFromDBAfterInsert = ScanFileMS_1_PerScanData_Num_TIC_RT_DAO.getScanFileIdFromScanFileId( scanFileId );
					if ( scanFileIdFromDBAfterInsert == null ) {
						//  Failed to insert and wasn't already inserted (if was already inserted, Duplicate key would be thrown to here)
						throw e;
					}
				}
			}
		}

		{
			Integer scanFileIdFromDB = ScanFileMS_2_PerScanData_Num_TIC_RT_DAO.getScanFileIdFromScanFileId( scanFileId );
			if ( scanFileIdFromDB == null ) {
				// Record not in DB so add it
				try {
					//  Serialize intensitiesMapToJSONRoot to JSON
					byte[] ms_2_PerScanDataJSONAsBytes = jacksonJSON_Mapper.writeValueAsBytes( accumScanFileStatistics.getMS_2_SummedIntensities_TIC_PerScan_JSONRoot() );
					//  Serialize summaryData to JSON
					
					byte[] ms_2_PerScanDataJSONAsBytes_Gzipped = 
							ZipUnzipByteArray.getInstance().zipByteArray( ms_2_PerScanDataJSONAsBytes );

					ScanFileMS_2_PerScanData_Num_TIC_RT_DTO item = new ScanFileMS_2_PerScanData_Num_TIC_RT_DTO();
					item.setScanFileId( scanFileId );
					item.setDataJSON_Gzipped( ms_2_PerScanDataJSONAsBytes_Gzipped );
					ScanFileMS_2_PerScanData_Num_TIC_RT_DAO.save( item );
				} catch ( Exception e ) {
					Integer scanFileIdFromDBAfterInsert = ScanFileMS_2_PerScanData_Num_TIC_RT_DAO.getScanFileIdFromScanFileId( scanFileId );
					if ( scanFileIdFromDBAfterInsert == null ) {
						//  Failed to insert and wasn't already inserted (if was already inserted, Duplicate key would be thrown to here)
						throw e;
					}
				}
			}
		}

		/**
		 * for ms 1 scans: Map<RetentionTime_Floor,Map<MZ_Floor,SummedIntensity>
		 * 
		 * "_Floor" means truncate any decimal fraction part
		 */
		
//		System.out.println( "************************************************************************" );
//		System.out.println( "************************************************************************" );
//		System.out.println( "************************************************************************" );
//		System.out.println( "START:  accumScanFileStatistics statistics for scanFileId: " + scanFileId );
//		
//		accumScanFileStatistics.printStats();
//
//		System.out.println( "END:  ms1_IntensitiesSummedMap for scanFileId: " + scanFileId );
//		System.out.println( "************************************************************************" );
//		System.out.println( "************************************************************************" );
//		System.out.println( "************************************************************************" );
		
//		if ( true ) {
//			
//			throw new ProxlImporterInteralException( "!!!!!!!!!!!!!!!!!!  FORCE EXCEPTION  !!!!!!!!!!!!!!!!!!!" );
//		}
	}

}
