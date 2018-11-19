/**
 * PeakStorageType.java
 * @author Vagisha Sharma
 * Apr 10, 2009
 * @version 1.0
 */
package org.yeastrc.xlink.base.spectrum.common.dto;

/**
 * 
 */
public enum PeakStorageType {
    
    DOUBLE_FLOAT("D"), STRING("S");
    
    private String code;
    
    private PeakStorageType(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    public static PeakStorageType instance(String val) {
            
        if(STRING.code.equals(val))
            return STRING;
        else if(DOUBLE_FLOAT.code.equals(val))
            return DOUBLE_FLOAT;
        else
            return null;
    }
}
