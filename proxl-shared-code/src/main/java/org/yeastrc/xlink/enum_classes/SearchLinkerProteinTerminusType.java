
package org.yeastrc.xlink.enum_classes;


/**
 * Enum for is this  Protein Terminus value
 * 
 * Keep these values in sync with the enum field 'n_terminus_c_terminus' 
 * in the tables search_linker_per_side_linkable_protein_termini_tbl 
 */
public enum SearchLinkerProteinTerminusType {

    /**
     * record for 'c' terminus
     */
    C("c"),
    
    /**
     * record for 'n' terminus
     */
    N("n");

    
    private final String value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private SearchLinkerProteinTerminusType(String v) {
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
    public static SearchLinkerProteinTerminusType fromValue( String value_ ) {
        for (SearchLinkerProteinTerminusType c: SearchLinkerProteinTerminusType.values()) {
            if (c.value.equals( value_ )) {
                return c;
            }
        }
        throw new IllegalArgumentException( "SearchLinkerProteinTerminusType not valid for value: " + value_ );
    }

}
