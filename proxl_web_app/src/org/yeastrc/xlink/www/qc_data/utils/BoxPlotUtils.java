package org.yeastrc.xlink.www.qc_data.utils;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;

/**
 * 
 *
 */
public class BoxPlotUtils {

	private static final Logger log = Logger.getLogger(BoxPlotUtils.class);

	private static final int MEDIAN_PERCENTILE = 50;
	private static final int FIRST_QUARTER_PERCENTILE = 25;
	private static final int THIRD_QUARTER_PERCENTILE = 75;
	
	//  number of IQRs to add
	private static final double OUTLIER_FACTOR = 1.5;
	
	/**
	 * private constructor
	 */
	private BoxPlotUtils(){}
	public static BoxPlotUtils getInstance( ) {
		BoxPlotUtils instance = new BoxPlotUtils();
		return instance;
	}

	/**
	 * @param values
	 * @return
	 * @throws ProxlWebappInternalErrorException 
	 */
	public GetBoxPlotValuesResult getBoxPlotValues( List<Double> values ) throws ProxlWebappInternalErrorException {

		// Get a DescriptiveStatistics instance - Apache Commons
		DescriptiveStatistics stats = new DescriptiveStatistics();

		double maxValue = 0;
		double minValue = 0;
		
		boolean firstValue = true;
		
		// Add the data
		for( Double value : values ) {
			stats.addValue( value );
			if ( firstValue ) {
				maxValue = value;
				minValue = value;
				firstValue = false;
			} else {
				if ( value > maxValue ) {
					maxValue = value;
				}
				if ( value < minValue ) {
					minValue = value;
				}
			}
		}

		// Compute some statistics
		double median = stats.getPercentile( MEDIAN_PERCENTILE );
		double firstquarter = stats.getPercentile( FIRST_QUARTER_PERCENTILE );
		double thirdquarter = stats.getPercentile( THIRD_QUARTER_PERCENTILE );

		double interQuartileRegion = thirdquarter - firstquarter;
		double highCutoff = thirdquarter + ( OUTLIER_FACTOR * interQuartileRegion );
		double lowCutoff = firstquarter - ( OUTLIER_FACTOR * interQuartileRegion );

		//  Get chart interval max and min
		
		Double chartIntervalMax = null;
		Double chartIntervalMin = null;
		
		//  adjust chart interval max and min based on each value
		for( Double value : values ) {
			if ( value <= highCutoff ) {
				if ( chartIntervalMax == null || value > chartIntervalMax ) {
					chartIntervalMax = value;
				}
			}
			if ( value >= lowCutoff ) {
				if ( chartIntervalMin == null || value < chartIntervalMin ) {
					chartIntervalMin = value;
				}
			}
		}

		if ( chartIntervalMax == null ) {
			String msg = "chartIntervalMax not assigned";
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
		if ( chartIntervalMin == null ) {
			String msg = "chartIntervalMin not assigned";
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
		
		GetBoxPlotValuesResult result = new GetBoxPlotValuesResult();
		
		result.setChartIntervalMax( chartIntervalMax );
		result.setChartIntervalMin( chartIntervalMin );
		result.setFirstQuartile( firstquarter );
		result.setThirdQuartile( thirdquarter );
		result.setMedian( median );
		
		return result;
	}
	
	/**
	 * Results of method 
	 *
	 */
	public static class GetBoxPlotValuesResult {

		//  Box chart values
		private double chartIntervalMax;
		private double chartIntervalMin;
		private double firstQuartile;
		private double median;
		private double thirdQuartile;
		
		public double getChartIntervalMax() {
			return chartIntervalMax;
		}
		public void setChartIntervalMax(double chartIntervalMax) {
			this.chartIntervalMax = chartIntervalMax;
		}
		public double getChartIntervalMin() {
			return chartIntervalMin;
		}
		public void setChartIntervalMin(double chartIntervalMin) {
			this.chartIntervalMin = chartIntervalMin;
		}
		public double getFirstQuartile() {
			return firstQuartile;
		}
		public void setFirstQuartile(double firstQuartile) {
			this.firstQuartile = firstQuartile;
		}
		public double getMedian() {
			return median;
		}
		public void setMedian(double median) {
			this.median = median;
		}
		public double getThirdQuartile() {
			return thirdQuartile;
		}
		public void setThirdQuartile(double thirdQuartile) {
			this.thirdQuartile = thirdQuartile;
		}
		
	}
	
}
