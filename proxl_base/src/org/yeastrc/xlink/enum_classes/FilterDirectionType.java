
package org.yeastrc.xlink.enum_classes;


/**
 * Enum for Filterable Annotation Sort Direction 
 *
 * If set to "below", attributes with lower values are considered more significant (such as in the case of p-values). 
 * If set to "above", attributes with higher values are considered more significant (such as in the case of XCorr).
 * 
 * Keep these values in sync with the enum field 'filter_direction' 
 * in the tables annotation_type_filterable and others derived from it
 */
public enum FilterDirectionType {

    /**
     * Attributes with higher values are considered more significant (such as in the case of XCorr).
     */
    ABOVE("above"),
    
    /**
     * Attributes with lower values are considered more significant (such as in the case of p-values).
     */
    BELOW("below");

    
    private final String value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private FilterDirectionType(String v) {
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
    public static FilterDirectionType fromValue( String value_ ) {
        for (FilterDirectionType c: FilterDirectionType.values()) {
            if (c.value.equals( value_ )) {
                return c;
            }
        }
        throw new IllegalArgumentException( "FilterDirectionType not valid for value: " + value_ );
    }

}
