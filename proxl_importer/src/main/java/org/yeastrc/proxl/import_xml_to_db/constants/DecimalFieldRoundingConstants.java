package org.yeastrc.proxl.import_xml_to_db.constants;

public class DecimalFieldRoundingConstants {

	//  Round to 9 decimal places since db field is DECIMAL(18,9)
	
	public static final int DECIMAL_FIELD_VALUE_MAX_SCALE = 9;
	

	/**
	 * psm table, field precursor_retention_time, DECIMAL(9,4)  So round to 4 decimal places
	 */
	public static final int PSM_PRECURSOR_RETENTION_TIME__DECIMAL_FIELD_VALUE_MAX_SCALE = 4;

	/**
	 * psm table, field precursor_m_z, DECIMAL(10,4)  So round to 4 decimal places
	 */
	public static final int PSM_PRECURSOR_M_Z__DECIMAL_FIELD_VALUE_MAX_SCALE = 4;

}
