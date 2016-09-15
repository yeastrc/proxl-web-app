package org.yeastrc.xlink.utils;

import org.yeastrc.xlink.base.constants.ScanIsCentroidValuesConstants;

/**
 * 
 *
 */
public class ValidateIsCentroidValidValue {

	/**
	 * @param isCentroid
	 * @return
	 */
	public static boolean validateIsCentroidValidValue( String isCentroid ) {
		
		if ( ScanIsCentroidValuesConstants.IS_CENTROID_FALSE.equals(isCentroid) 
				|| ScanIsCentroidValuesConstants.IS_CENTROID_TRUE.equals(isCentroid)
				|| ScanIsCentroidValuesConstants.IS_CENTROID_UNKNOWN.equals(isCentroid) ) {
			
			return true;
		}
		
		return false;
	}
}
