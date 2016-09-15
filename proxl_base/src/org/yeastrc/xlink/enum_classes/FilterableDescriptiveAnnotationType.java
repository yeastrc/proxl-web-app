
package org.yeastrc.xlink.enum_classes;


/**
 * Enum for is this Annotation Type record for Filterable or Descriptive 
 * 
 * Keep these values in sync with the enum field 'filterable_descriptive_type' 
 * in the tables annotation_type and 
 */
public enum FilterableDescriptiveAnnotationType {

    /**
     * Annotation type records for filterable
     */
    FILTERABLE("filterable"),
    
    /**
     * Annotation type records for descriptive
     */
    DESCRIPTIVE("descriptive");

    
    private final String value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private FilterableDescriptiveAnnotationType(String v) {
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
    public static FilterableDescriptiveAnnotationType fromValue( String value_ ) {
        for (FilterableDescriptiveAnnotationType c: FilterableDescriptiveAnnotationType.values()) {
            if (c.value.equals( value_ )) {
                return c;
            }
        }
        throw new IllegalArgumentException( "PsmPeptideAnnotationType not valid for value: " + value_ );
    }

}
