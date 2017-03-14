package org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes;


/**
 * Enum for is this Proxl XML File Import 
 * DB field file_import_proxl_xml_scans_tracking_single_file.file_upload_status_id
 * 
 * Keep these values in sync with the values in the table 
 * 'file_import_proxl_xml_scans_tracking_single_file_upload_status_lookup'
 * 
 */
public enum ProxlXMLImportSingleFileUploadStatus {

	RECORD_INSERTED( 1 ),
    FILE_UPLOAD_STARTED( 2 ),
    FILE_UPLOAD_COMPLETE( 3 );

    
    private final int value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private ProxlXMLImportSingleFileUploadStatus( int v) {
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
    public static ProxlXMLImportSingleFileUploadStatus fromValue( int value_ ) {
        for (ProxlXMLImportSingleFileUploadStatus c: ProxlXMLImportSingleFileUploadStatus.values()) {
            if (c.value == value_ ) {
                return c;
            }
        }
        throw new IllegalArgumentException( "ProxlXMLFileImportStatus not valid for value: " + value_ );
    }
}
