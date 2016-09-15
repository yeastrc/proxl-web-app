package org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes;


/**
 * Enum for this Proxl XML File Import Single File record for the file type 
 * 
 * Keep these values in sync with the values in the table 
 * 'proxl_xml_file_import_tracking_single_file_type_lookup'
 * 
 */
public enum ProxlXMLFileImportFileType {

    PROXL_XML_FILE( 1 ),
    SCAN_FILE( 2 )
    ;

    
    private final int value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private ProxlXMLFileImportFileType( int v) {
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
    public static ProxlXMLFileImportFileType fromValue( int value_ ) {
        for (ProxlXMLFileImportFileType c: ProxlXMLFileImportFileType.values()) {
            if (c.value == value_ ) {
                return c;
            }
        }
        throw new IllegalArgumentException( "ProxlXMLFileImportFileType not valid for value: " + value_ );
    }
}
