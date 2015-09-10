/**
 * Peak.java
 * @author Vagisha Sharma
 * Apr 5, 2009
 * @version 1.0
 */
package org.yeastrc.xlink.base.spectrum.common.dto;

/**
 * 
 */
public class Peak {

    private double mz;
    private float intensity;
    
    public Peak(){}
    
    public Peak(double mz, float intensity) {
        this.mz = mz;
        this.intensity = intensity;
    }
    
    public double getMz() {
        return mz;
    }
    public void setMz(double mz) {
        this.mz = mz;
    }
    public float getIntensity() {
        return intensity;
    }
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    
    
}
