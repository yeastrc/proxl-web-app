package org.yeastrc.xlink.www.web_utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RetentionTimeScalingAndRounding {

	// Divide by 60 to change all retention times to minutes.
	private static final int  RETENTION_TIME_SECONDS_TO_MINUTES_DIVIDE_BY = 60;
	
	private static final BigDecimal RETENTION_TIME_SECONDS_TO_MINUTES_DIVIDE_BY_BD = new BigDecimal( RETENTION_TIME_SECONDS_TO_MINUTES_DIVIDE_BY );
	
	//  Number of places to right of decimal point
	private static final int NUMBER_OF_DECIMAL_PLACES = 2;
	
	/**
	 * @param initialRetentionTime
	 * @return
	 */
	public static BigDecimal retentionTimeToMinutesRounded( BigDecimal initialRetentionTime )  {
		BigDecimal retentionInMinutes = retentionTimeToMinutes( initialRetentionTime );
		BigDecimal retentionInMinutesRounded = retentionInMinutes.setScale( NUMBER_OF_DECIMAL_PLACES, RoundingMode.HALF_UP );
//		int retentionInMinutesRoundedInt = (int) retentionInMinutesRounded.longValueExact() ;
		return retentionInMinutesRounded;
	}
	/**
	 * @param initialRetentionTime
	 * @return
	 */
	public static BigDecimal retentionTimeToMinutes( BigDecimal initialRetentionTime )  {
		return initialRetentionTime.divide( RETENTION_TIME_SECONDS_TO_MINUTES_DIVIDE_BY_BD, RoundingMode.HALF_UP );
	}
	
	/**
	 * @param initialRetentionTime
	 * @return
	 */
	public static float retentionTimeToMinutes( float initialRetentionTime )  {
		return initialRetentionTime / RETENTION_TIME_SECONDS_TO_MINUTES_DIVIDE_BY;
	}
}
