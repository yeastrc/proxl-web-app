package org.yeastrc.xlink.ms_1_2_summed_intensities_per_scan.objects;

import java.util.List;

/**
 * Data per ms1 scan:
 * 
 * Scan Number (sn)
 * Retention Time (rt)
 * Total Ion Current (tic)
 *
 */
public class MS_1_SummedIntensities_TIC_PerScan_JSONRoot {
	
	List<MS_1_SummedIntensities_TIC_PerScan_Entry> scanEntries;
	
	public List<MS_1_SummedIntensities_TIC_PerScan_Entry> getScanEntries() {
		return scanEntries;
	}
	public void setScanEntries(List<MS_1_SummedIntensities_TIC_PerScan_Entry> scanEntries) {
		this.scanEntries = scanEntries;
	}
	
}


//{
//	ScanFileMS_1_PerScanData_Num_TIC_RT_DTO scanFileMS_1_PerScanData_Num_TIC_RT_DTO =
//			ScanFileMS_1_PerScanData_Num_TIC_RT_DAO.getFromScanFileId( scanFileId );
//
//	if ( scanFileMS_1_PerScanData_Num_TIC_RT_DTO != null ) {
//		byte[] dataJSON_Gzipped = scanFileMS_1_PerScanData_Num_TIC_RT_DTO.getDataJSON_Gzipped();
//		byte[] dataJSON = ZipUnzipByteArray.getInstance().unzipByteArray( dataJSON_Gzipped );
//
//		int dataJSON_GzippedLength = dataJSON_Gzipped.length;
//		int dataJSONLength = dataJSON.length;
//
//		//  Jackson JSON Mapper object for JSON deserialization and serialization
//		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
//
//		//			//  deserialize JSON
//		MS_1_SummedIntensities_TIC_PerScan_JSONRoot result = 
//				jacksonJSON_Mapper.readValue( dataJSON, MS_1_SummedIntensities_TIC_PerScan_JSONRoot.class );
//
//		int ms1Count = result.getScanEntries().size();
//		
//		int z = 0;
//	}
//}