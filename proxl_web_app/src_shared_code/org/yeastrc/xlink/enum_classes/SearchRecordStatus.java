package org.yeastrc.xlink.enum_classes;


/**
 * Enum for is this Search record 
 * DB field search.status_id
 * 
 * Keep these values in sync with the values in the table 
 * 'search_record_status_lookup'
 * 
 */
public enum SearchRecordStatus {

	IMPORTING( 1 ),
    IMPORT_COMPLETE_VIEW( 2 ),
    IMPORT_FAIL( 3 ),
    IMPORT_CANCELED_INCOMPLETE( 4 ),
    MARKED_FOR_DELETION( 5 ),
	DELETION_IN_PROGRESS( 6 );
	
    private final int value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private SearchRecordStatus( int v) {
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
    public static SearchRecordStatus fromValue( int value_ ) {
        for (SearchRecordStatus c: SearchRecordStatus.values()) {
            if (c.value == value_ ) {
                return c;
            }
        }
        throw new IllegalArgumentException( "SearchRecordStatus not valid for value: " + value_ );
    }

}
