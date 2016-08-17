package org.yeastrc.proxl.import_xml_to_db.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.yeastrc.proxl.import_xml_to_db.constants.DecimalFieldRoundingConstants;


/**
 * Rounds a value if necessary to fit in database DECIMAL(18,9)
 *
 */
public class RoundDecimalFieldsIfNecessary {

	/**
	 * Rounds the field value if necessary to fit in database DECIMAL(18,9)
	 * 
	 * @param retentionTime
	 * @return
	 */
	public static BigDecimal roundDecimalFieldsIfNecessary( BigDecimal retentionTime ) {

		if ( retentionTime == null ) {
			
			return retentionTime;
		}

		if ( retentionTime.scale()  > DecimalFieldRoundingConstants.DECIMAL_FIELD_VALUE_MAX_SCALE ) {
			
			retentionTime =
					retentionTime.setScale( 
							DecimalFieldRoundingConstants.DECIMAL_FIELD_VALUE_MAX_SCALE, 
							RoundingMode.HALF_UP );
		}
		
		return retentionTime;
	}

}
