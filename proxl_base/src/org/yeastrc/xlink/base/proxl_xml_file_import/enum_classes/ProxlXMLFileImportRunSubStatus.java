package org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes;


/**
 * Enum for is this Proxl XML File Import Run record for the sub status 
 * 
 * Keep these values in sync with the values in the table 
 * 'proxl_xml_file_import_tracking_run_sub_status_values_lookup'
 * 
 */
public enum ProxlXMLFileImportRunSubStatus {

    SYSTEM_ERROR( 1 ),
	DATA_ERROR( 2 ),
    PROJECT_NOT_ALLOW_IMPORT( 3 )
    ;

	
    
    private final int value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private ProxlXMLFileImportRunSubStatus( int v) {
        value = v;
    }

    public int value() {
        return value;
    }

    /**
     * Get the enum from the String value
     * 
     * @param value_
     * @return
     */
    public static ProxlXMLFileImportRunSubStatus fromValue( int value_ ) {
        for (ProxlXMLFileImportRunSubStatus c: ProxlXMLFileImportRunSubStatus.values()) {
            if (c.value == value_ ) {
                return c;
            }
        }
        throw new IllegalArgumentException( "ProxlXMLFileImportStatus not valid for value: " + value_ );
    }
}
