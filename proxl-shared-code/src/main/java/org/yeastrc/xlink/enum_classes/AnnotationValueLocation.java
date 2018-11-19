
package org.yeastrc.xlink.enum_classes;


/**
 * Enum for Annotation Value Location 
 *
 * If set to "local", the value_string in the current table contains the value. 
 * If set to "large_value_table", the value_string in the associated "...large_value" table contains the value. 
 * 
 * Keep these values in sync with the enum field 'value_location' 
 * in the tables psm_annotation and srch__rep_pept__annotation
 */
public enum AnnotationValueLocation {

    /**
     * Attributes with higher values are considered more significant (such as in the case of XCorr).
     */
    LOCAL("local"),
    
    /**
     * Attributes with lower values are considered more significant (such as in the case of p-values).
     */
    LARGE_VALUE_TABLE("large_value_table");

    
    private final String value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private AnnotationValueLocation(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    /**
     * Get the enum from the String value
     * 
     * @param value_
     * @return
     */
    public static AnnotationValueLocation fromValue( String value_ ) {
        for (AnnotationValueLocation c: AnnotationValueLocation.values()) {
            if (c.value.equals( value_ )) {
                return c;
            }
        }
        throw new IllegalArgumentException( "AnnotationValueLocation not valid for value: " + value_ );
    }

}
