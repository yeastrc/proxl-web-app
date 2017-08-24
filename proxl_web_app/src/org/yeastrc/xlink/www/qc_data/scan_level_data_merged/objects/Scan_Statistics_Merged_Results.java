package org.yeastrc.xlink.www.qc_data.scan_level_data_merged.objects;

import java.util.List;

/**
 * Results from Scan_Statistics_Merged
 *
 */
public class Scan_Statistics_Merged_Results {
	
	private List<Scan_Statistics_PerSearch> dataPerSearchList;
	
	private boolean haveData;

	public static class Scan_Statistics_PerSearch {
		
		private int searchId;
		
		private boolean haveScanData;

		//  Overall statistics
		private boolean haveSscanOverallData;
		
		private long ms_1_ScanCount;
		private double ms_1_ScanIntensitiesSummed;
		private long ms_2_ScanCount;
		private double ms_2_ScanIntensitiesSummed;
		
		//  MS2 Scans that meet PSM/Peptide Cutoff
		private long crosslinkCount;
		private long looplinkCount;
		/**
		 * includes dimers
		 */
		private long unlinkedCount;
		
		
		public int getSearchId() {
			return searchId;
		}
		public void setSearchId(int searchId) {
			this.searchId = searchId;
		}
		public long getMs_1_ScanCount() {
			return ms_1_ScanCount;
		}
		public void setMs_1_ScanCount(long ms_1_ScanCount) {
			this.ms_1_ScanCount = ms_1_ScanCount;
		}
		public double getMs_1_ScanIntensitiesSummed() {
			return ms_1_ScanIntensitiesSummed;
		}
		public void setMs_1_ScanIntensitiesSummed(double ms_1_ScanIntensitiesSummed) {
			this.ms_1_ScanIntensitiesSummed = ms_1_ScanIntensitiesSummed;
		}
		public long getMs_2_ScanCount() {
			return ms_2_ScanCount;
		}
		public void setMs_2_ScanCount(long ms_2_ScanCount) {
			this.ms_2_ScanCount = ms_2_ScanCount;
		}
		public double getMs_2_ScanIntensitiesSummed() {
			return ms_2_ScanIntensitiesSummed;
		}
		public void setMs_2_ScanIntensitiesSummed(double ms_2_ScanIntensitiesSummed) {
			this.ms_2_ScanIntensitiesSummed = ms_2_ScanIntensitiesSummed;
		}
		public long getCrosslinkCount() {
			return crosslinkCount;
		}
		public void setCrosslinkCount(long crosslinkCount) {
			this.crosslinkCount = crosslinkCount;
		}
		public long getLooplinkCount() {
			return looplinkCount;
		}
		public void setLooplinkCount(long looplinkCount) {
			this.looplinkCount = looplinkCount;
		}
		public long getUnlinkedCount() {
			return unlinkedCount;
		}
		public void setUnlinkedCount(long unlinkedCount) {
			this.unlinkedCount = unlinkedCount;
		}
		public boolean isHaveScanData() {
			return haveScanData;
		}
		public void setHaveScanData(boolean haveScanData) {
			this.haveScanData = haveScanData;
		}
		public boolean isHaveSscanOverallData() {
			return haveSscanOverallData;
		}
		public void setHaveSscanOverallData(boolean haveSscanOverallData) {
			this.haveSscanOverallData = haveSscanOverallData;
		}

		
		
	}

	public List<Scan_Statistics_PerSearch> getDataPerSearchList() {
		return dataPerSearchList;
	}

	public void setDataPerSearchList(List<Scan_Statistics_PerSearch> dataPerSearchList) {
		this.dataPerSearchList = dataPerSearchList;
	}

	public boolean isHaveData() {
		return haveData;
	}

	public void setHaveData(boolean haveData) {
		this.haveData = haveData;
	}
	
}
