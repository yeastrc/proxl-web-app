package org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes;


/**
 * Enum for is this Proxl XML File Import record for the status 
 * 
 * Keep these values in sync with the values in the table 
 * 'file_import_proxl_xml_scans_tracking_status_values_lookup'
 * 
 */
public enum ProxlXMLFileImportStatus {

	INIT_INSERT_PRE_QUEUED( 1 ),
    QUEUED( 2 ),
    RE_QUEUED( 3 ),
    STARTED( 4 ),
    COMPLETE( 5 ),
    FAILED( 6 );

    
    private final int value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private ProxlXMLFileImportStatus( int v) {
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
    public static ProxlXMLFileImportStatus fromValue( int value_ ) {
        for (ProxlXMLFileImportStatus c: ProxlXMLFileImportStatus.values()) {
            if (c.value == value_ ) {
                return c;
            }
        }
        throw new IllegalArgumentException( "ProxlXMLFileImportStatus not valid for value: " + value_ );
    }
}
