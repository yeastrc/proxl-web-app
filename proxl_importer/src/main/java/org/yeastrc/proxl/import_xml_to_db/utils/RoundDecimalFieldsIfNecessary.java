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
	 * @param inputValue
	 * @return
	 */
	public static BigDecimal roundDecimalFieldsIfNecessary_18comma9( BigDecimal inputValue ) {

		if ( inputValue == null ) {
			
			return inputValue;
		}

		if ( inputValue.scale()  > DecimalFieldRoundingConstants.DECIMAL_FIELD_VALUE_MAX_SCALE ) {
			
			inputValue =
					inputValue.setScale( 
							DecimalFieldRoundingConstants.DECIMAL_FIELD_VALUE_MAX_SCALE, 
							RoundingMode.HALF_UP );
		}
		
		return inputValue;
	}
	

	/**
	 * Rounds the value if necessary to fit in database DECIMAL(9,4)  psm.precursor_retention_time.
	 * 
	 * @param inputValue
	 * @return
	 */
	public static BigDecimal roundDecimalField__psm_precursor_retention_time( BigDecimal inputValue ) {

		if ( inputValue == null ) {
			
			return inputValue;
		}

		if ( inputValue.scale()  > DecimalFieldRoundingConstants.PSM_PRECURSOR_RETENTION_TIME__DECIMAL_FIELD_VALUE_MAX_SCALE ) {
			
			inputValue =
					inputValue.setScale( 
							DecimalFieldRoundingConstants.PSM_PRECURSOR_RETENTION_TIME__DECIMAL_FIELD_VALUE_MAX_SCALE, 
							RoundingMode.HALF_UP );
		}
		
		return inputValue;
	}

	/**
	 * Rounds the value if necessary to fit in database DECIMAL(10,4)  psm.precursor_m_z.
	 * 
	 * @param inputValue
	 * @return
	 */
	public static BigDecimal roundDecimalField__psm_precursor_m_z( BigDecimal inputValue ) {

		if ( inputValue == null ) {
			
			return inputValue;
		}

		if ( inputValue.scale()  > DecimalFieldRoundingConstants.PSM_PRECURSOR_M_Z__DECIMAL_FIELD_VALUE_MAX_SCALE ) {
			
			inputValue =
					inputValue.setScale( 
							DecimalFieldRoundingConstants.PSM_PRECURSOR_M_Z__DECIMAL_FIELD_VALUE_MAX_SCALE, 
							RoundingMode.HALF_UP );
		}
		
		return inputValue;
	}
	
}
