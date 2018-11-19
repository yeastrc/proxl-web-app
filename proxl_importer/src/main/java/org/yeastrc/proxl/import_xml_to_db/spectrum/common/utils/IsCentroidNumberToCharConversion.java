package org.yeastrc.proxl.import_xml_to_db.spectrum.common.utils;

import org.yeastrc.xlink.base.constants.ScanIsCentroidValuesConstants;

/**
 * Converts from the number in the file to the character stored in the database
 *
 */
public class IsCentroidNumberToCharConversion {

	public static String convertMzMLisCentroidNumberToChar( int isCentroid ) {
		
		if ( isCentroid == 1 ) {
			
			return ScanIsCentroidValuesConstants.IS_CENTROID_TRUE;
		}
		
		return ScanIsCentroidValuesConstants.IS_CENTROID_FALSE; // default all others to false for MzML
		
	}
}
