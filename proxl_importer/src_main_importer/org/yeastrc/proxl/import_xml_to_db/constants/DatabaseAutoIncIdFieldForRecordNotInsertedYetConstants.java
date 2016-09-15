package org.yeastrc.proxl.import_xml_to_db.constants;

/**
 * 
 *
 */
public class DatabaseAutoIncIdFieldForRecordNotInsertedYetConstants {

	/**
	 * Set to -1 since the field is unsigned so if try to insert this the insert will fail
	 */
	public static final int DB_AUTO_INC_FIELD_INITIAL_VALUE_FOR_NOT_INSERTED_YET = -1;
}
