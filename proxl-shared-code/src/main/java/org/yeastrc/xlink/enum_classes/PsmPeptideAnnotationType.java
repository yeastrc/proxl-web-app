
package org.yeastrc.xlink.enum_classes;


/**
 * Enum for is this Annotation Type record for PSM or Peptide 
 * 
 * Keep these values in sync with the enum field 'psm_peptide_type' 
 * in the tables annotation_type and 
 */
public enum PsmPeptideAnnotationType {

    /**
     * Annotation type records for PSMs
     */
    PSM("psm"),

    /**
     * Annotation type records for PSM Per Peptide
     */
    PSM_PER_PEPTIDE("psm_per_peptide"),
    
    /**
     * Annotation type records for peptides
     */
    PEPTIDE("peptide");

    
    private final String value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private PsmPeptideAnnotationType(String v) {
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
    public static PsmPeptideAnnotationType fromValue( String value_ ) {
        for (PsmPeptideAnnotationType c: PsmPeptideAnnotationType.values()) {
            if (c.value.equals( value_ )) {
                return c;
            }
        }
        throw new IllegalArgumentException( "PsmPeptideAnnotationType not valid for value: " + value_ );
    }

}
