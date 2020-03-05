package org.yeastrc.xlink.www.qc_data.scan_level_data.objects;

import java.util.List;

import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;

public class Scan_MS_AllScans_AllButPeaks_Result {
	
	private List<Scan_MS_AllScans_AllButPeaks_Result_SingleScan> scans;

	
	/**
	 * Single Scan
	 *
	 */
	public static class Scan_MS_AllScans_AllButPeaks_Result_SingleScan {

		private byte level;
		private int scanNumber;
		private float retentionTime_Seconds;
		
		/**
		 * Not Populated when Data file is version < 5 since not stored in those data files
		 */
		private Float totalIonCurrent_ForScan;

		/**
		 * Not Populated when Data file is version < 5 since not stored in those data files
		 */
		private Float ionInjectionTime_MilliSeconds;  // In Milliseconds

		/**
		 * Not populated if request other than peaks and scan file contains more than one unique value
		 */
		private Boolean isCentroid;

		//  Only applicable where level > 1

		private Integer parentScanNumber;
		private Byte precursorCharge;
		private Double precursor_M_Over_Z;
		
		/**Constructor
		 * 
		 */
		public Scan_MS_AllScans_AllButPeaks_Result_SingleScan() {}

		/**
		 * Constructor
		 * 
		 * @param spectralStorageServiceScan
		 */
		public Scan_MS_AllScans_AllButPeaks_Result_SingleScan( SingleScan_SubResponse spectralStorageServiceScan ) {
			
			super();
			this.level = spectralStorageServiceScan.getLevel();
			this.scanNumber = spectralStorageServiceScan.getScanNumber();
			this.retentionTime_Seconds = spectralStorageServiceScan.getRetentionTime();
			this.totalIonCurrent_ForScan = spectralStorageServiceScan.getTotalIonCurrent_ForScan();
			this.ionInjectionTime_MilliSeconds = spectralStorageServiceScan.getIonInjectionTime();
			this.parentScanNumber = spectralStorageServiceScan.getParentScanNumber();
			this.precursorCharge = spectralStorageServiceScan.getPrecursorCharge();
			this.precursor_M_Over_Z = spectralStorageServiceScan.getPrecursor_M_Over_Z();
			
			if ( spectralStorageServiceScan.getIsCentroid() != null ) {
				if ( spectralStorageServiceScan.getIsCentroid() == 1 ) {
					this.isCentroid = true;
				} else {
					this.isCentroid = false;
				}
			}
		}

		/**
		 * Constructor
		 * 
		 * @param level
		 * @param scanNumber
		 * @param retentionTime
		 * @param totalIonCurrent_ForScan
		 * @param ionInjectionTime_MilliSeconds
		 * @param isCentroid
		 * @param parentScanNumber
		 * @param precursorCharge
		 * @param precursor_M_Over_Z
		 */
		public Scan_MS_AllScans_AllButPeaks_Result_SingleScan(byte level, int scanNumber, float retentionTime_Seconds,
				Float totalIonCurrent_ForScan, Float ionInjectionTime_MilliSeconds, Boolean isCentroid, Integer parentScanNumber,
				Byte precursorCharge, Double precursor_M_Over_Z) {
			super();
			this.level = level;
			this.scanNumber = scanNumber;
			this.retentionTime_Seconds = retentionTime_Seconds;
			this.totalIonCurrent_ForScan = totalIonCurrent_ForScan;
			this.ionInjectionTime_MilliSeconds = ionInjectionTime_MilliSeconds;
			this.isCentroid = isCentroid;
			this.parentScanNumber = parentScanNumber;
			this.precursorCharge = precursorCharge;
			this.precursor_M_Over_Z = precursor_M_Over_Z;
		}
		
		public byte getLevel() {
			return level;
		}
		public void setLevel(byte level) {
			this.level = level;
		}
		public int getScanNumber() {
			return scanNumber;
		}
		public void setScanNumber(int scanNumber) {
			this.scanNumber = scanNumber;
		}
		public float getRetentionTime_Seconds() {
			return retentionTime_Seconds;
		}
		public void setRetentionTime_Seconds(float retentionTime_Seconds) {
			this.retentionTime_Seconds = retentionTime_Seconds;
		}
		/**
		 * Not Populated when Data file is version < 5 since not stored in those data files
		 * @return
		 */
		public Float getTotalIonCurrent_ForScan() {
			return totalIonCurrent_ForScan;
		}
		public void setTotalIonCurrent_ForScan(float totalIonCurrent_ForScan) {
			this.totalIonCurrent_ForScan = totalIonCurrent_ForScan;
		}
		/**
		 * Not Populated when Data file is version < 5 since not stored in those data files
		 * @return
		 */
		public Float getIonInjectionTime_MilliSeconds() {
			return ionInjectionTime_MilliSeconds;
		}
		public void setIonInjectionTime_MilliSeconds(Float ionInjectionTime_MilliSeconds) {
			this.ionInjectionTime_MilliSeconds = ionInjectionTime_MilliSeconds;
		}
		public Boolean getIsCentroid() {
			return isCentroid;
		}
		public void setIsCentroid(Boolean isCentroid) {
			this.isCentroid = isCentroid;
		}
		public Integer getParentScanNumber() {
			return parentScanNumber;
		}
		public void setParentScanNumber(Integer parentScanNumber) {
			this.parentScanNumber = parentScanNumber;
		}
		public Byte getPrecursorCharge() {
			return precursorCharge;
		}
		public void setPrecursorCharge(Byte precursorCharge) {
			this.precursorCharge = precursorCharge;
		}
		public Double getPrecursor_M_Over_Z() {
			return precursor_M_Over_Z;
		}
		public void setPrecursor_M_Over_Z(Double precursor_M_Over_Z) {
			this.precursor_M_Over_Z = precursor_M_Over_Z;
		}

	}


	public List<Scan_MS_AllScans_AllButPeaks_Result_SingleScan> getScans() {
		return scans;
	}


	public void setScans(List<Scan_MS_AllScans_AllButPeaks_Result_SingleScan> scans) {
		this.scans = scans;
	}
}
