package org.yeastrc.xlink.www.qc_data.scan_level_data.objects;

import java.util.List;

/**
 * 
 *
 */
public class Scan_MS_1_IonCurrent_HistogramsResult {

	private Scan_MS_1_IonCurrent_HistogramsResultForChartType dataForRetentionTimeChart;
	private Scan_MS_1_IonCurrent_HistogramsResultForChartType dataFor_M_Over_Z_Chart;

	/**
	 * 
	 *
	 */
	public static class Scan_MS_1_IonCurrent_HistogramsResultForChartType {
		
		private long min;
		private long max;
		private long maxPossibleValue;
		private List<Scan_MS_1_IonCurrent_HistogramsResultChartBucket> chartBuckets;
		public long getMin() {
			return min;
		}
		public void setMin(long min) {
			this.min = min;
		}
		public long getMax() {
			return max;
		}
		public void setMax(long max) {
			this.max = max;
		}
		public long getMaxPossibleValue() {
			return maxPossibleValue;
		}
		public void setMaxPossibleValue(long maxPossibleValue) {
			this.maxPossibleValue = maxPossibleValue;
		}
		public List<Scan_MS_1_IonCurrent_HistogramsResultChartBucket> getChartBuckets() {
			return chartBuckets;
		}
		public void setChartBuckets(List<Scan_MS_1_IonCurrent_HistogramsResultChartBucket> chartBuckets) {
			this.chartBuckets = chartBuckets;
		}

	}
	
	public static class Scan_MS_1_IonCurrent_HistogramsResultChartBucket {
		
		private int binStart;
		private int binEnd;
		private double binCenter;
		private double intensitySummed;
		
		private int barColorRed;
		private int barColorGreen;
		private int barColorBlue;
		
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
		public double getIntensitySummed() {
			return intensitySummed;
		}
		public void setIntensitySummed(double intensitySummed) {
			this.intensitySummed = intensitySummed;
		}
		public int getBarColorRed() {
			return barColorRed;
		}
		public void setBarColorRed(int barColorRed) {
			this.barColorRed = barColorRed;
		}
		public int getBarColorGreen() {
			return barColorGreen;
		}
		public void setBarColorGreen(int barColorGreen) {
			this.barColorGreen = barColorGreen;
		}
		public int getBarColorBlue() {
			return barColorBlue;
		}
		public void setBarColorBlue(int barColorBlue) {
			this.barColorBlue = barColorBlue;
		}
		
	}

	public Scan_MS_1_IonCurrent_HistogramsResultForChartType getDataForRetentionTimeChart() {
		return dataForRetentionTimeChart;
	}

	public void setDataForRetentionTimeChart(Scan_MS_1_IonCurrent_HistogramsResultForChartType dataForRetentionTimeChart) {
		this.dataForRetentionTimeChart = dataForRetentionTimeChart;
	}

	public Scan_MS_1_IonCurrent_HistogramsResultForChartType getDataFor_M_Over_Z_Chart() {
		return dataFor_M_Over_Z_Chart;
	}

	public void setDataFor_M_Over_Z_Chart(Scan_MS_1_IonCurrent_HistogramsResultForChartType dataFor_M_Over_Z_Chart) {
		this.dataFor_M_Over_Z_Chart = dataFor_M_Over_Z_Chart;
	}

}

