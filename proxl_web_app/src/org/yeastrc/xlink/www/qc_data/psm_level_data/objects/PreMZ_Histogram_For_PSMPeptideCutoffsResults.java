package org.yeastrc.xlink.www.qc_data.psm_level_data.objects;

import java.math.BigDecimal;
import java.util.List;

/**
 * Results from PreMZ_Histogram_For_PSMPeptideCutoffs
 *
 */
public class PreMZ_Histogram_For_PSMPeptideCutoffsResults {

	private List<PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList;

	public static class PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType {
		
		private String linkType;
		private int numScans;
		private BigDecimal preMZMin;
		private BigDecimal preMZMax;
		private List<PreMZ_Histogram_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets;
		public String getLinkType() {
			return linkType;
		}
		public void setLinkType(String linkType) {
			this.linkType = linkType;
		}
		public int getNumScans() {
			return numScans;
		}
		public void setNumScans(int numScans) {
			this.numScans = numScans;
		}
		public BigDecimal getPreMZMin() {
			return preMZMin;
		}
		public void setPreMZMin(BigDecimal preMZMin) {
			this.preMZMin = preMZMin;
		}
		public BigDecimal getPreMZMax() {
			return preMZMax;
		}
		public void setPreMZMax(BigDecimal preMZMax) {
			this.preMZMax = preMZMax;
		}
		public List<PreMZ_Histogram_For_PSMPeptideCutoffsResultsChartBucket> getChartBuckets() {
			return chartBuckets;
		}
		public void setChartBuckets(List<PreMZ_Histogram_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets) {
			this.chartBuckets = chartBuckets;
		}
	}
	
	public static class PreMZ_Histogram_For_PSMPeptideCutoffsResultsChartBucket {
		
		private int binStart;
		private int binEnd;
		private double binCenter;
		private int count;
		
		public int getBinStart() {
			return binStart;
		}
		public void setBinStart(int binStart) {
			this.binStart = binStart;
		}
		public int getBinEnd() {
			return binEnd;
		}
		public void setBinEnd(int binEnd) {
			this.binEnd = binEnd;
		}
		public double getBinCenter() {
			return binCenter;
		}
		public void setBinCenter(double binCenter) {
			this.binCenter = binCenter;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		
	}

	public List<PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType> getDataForChartPerLinkTypeList() {
		return dataForChartPerLinkTypeList;
	}

	public void setDataForChartPerLinkTypeList(
			List<PreMZ_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList) {
		this.dataForChartPerLinkTypeList = dataForChartPerLinkTypeList;
	}

}
