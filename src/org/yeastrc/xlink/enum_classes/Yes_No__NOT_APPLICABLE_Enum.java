
package org.yeastrc.xlink.enum_classes;


/**
 * Enum for values of yes, no, and not applicable 
 * 
 * Keep these values in sync with the enum field 'peptide_meets_default_cutoffs' 
 * in the tables unified_rp__rep_pept__search__generic_lookup and other places where it is used
 */
public enum Yes_No__NOT_APPLICABLE_Enum {

    /**
     * 
     */
    YES("yes"),
    
    /**
     * 
     */
    NO("no"),

    /**
     * not_applicable
     */
    NOT_APPLICABLE("not_applicable");

    
    private final String value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private Yes_No__NOT_APPLICABLE_Enum(String v) {
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
    public static Yes_No__NOT_APPLICABLE_Enum fromValue( String value_ ) {
        for (Yes_No__NOT_APPLICABLE_Enum c: Yes_No__NOT_APPLICABLE_Enum.values()) {
            if (c.value.equals( value_ )) {
                return c;
            }
        }
        throw new IllegalArgumentException( "Yes_No__NOT_APPLICABLE_Enum not valid for value: " + value_ );
    }

}
