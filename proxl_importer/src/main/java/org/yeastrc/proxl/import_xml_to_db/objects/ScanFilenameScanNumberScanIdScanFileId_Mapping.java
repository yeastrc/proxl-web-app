package org.yeastrc.proxl.import_xml_to_db.objects;

import java.util.Map;

/**
 * Result from Process_MzML_MzXml_File class
 * 
 * The result of processing a scan file.
 * 
 * One object per scan file processed.
 * 
 * Provides a mapping of scan filename and scan number to table field: scan.id
 *
 * Provides a mapping of scan filename to scan file id
 */
public class ScanFilenameScanNumberScanIdScanFileId_Mapping {

	private Map<Integer,Integer> mapOfScanNumbersToScanIds;
	private int scanFileId;
	private String scanFilename;
	
	public Map<Integer, Integer> getMapOfScanNumbersToScanIds() {
		return mapOfScanNumbersToScanIds;
	}
	public void setMapOfScanNumbersToScanIds(Map<Integer, Integer> mapOfScanNumbersToScanIds) {
		this.mapOfScanNumbersToScanIds = mapOfScanNumbersToScanIds;
	}
	public int getScanFileId() {
		return scanFileId;
	}
	public void setScanFileId(int scanFileId) {
		this.scanFileId = scanFileId;
	}
	public String getScanFilename() {
		return scanFilename;
	}
	public void setScanFilename(String scanFilename) {
		this.scanFilename = scanFilename;
	}
}
