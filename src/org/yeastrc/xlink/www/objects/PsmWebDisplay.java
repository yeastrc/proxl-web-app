package org.yeastrc.xlink.www.objects;

import java.math.BigDecimal;

import org.yeastrc.xlink.dto.PsmDTO;

public class PsmWebDisplay {

	private PsmDTO psmDTO;
	private int scanNumber;
	private String scanFilename;
	private BigDecimal retentionTime;
	private BigDecimal retentionTimeMinutesRounded;
	private String retentionTimeMinutesRoundedString;
	
	private int charge;
	private BigDecimal preMZ;
	private String preMZRounded; 

	public String getRetentionTimeMinutesRoundedString() {
		
		if ( retentionTimeMinutesRoundedString == null ) {
			
			if ( retentionTime == null ) {
				
				return null;
			}
			
			retentionTimeMinutesRoundedString = retentionTimeMinutesRounded.toString();
		}
		
		return retentionTimeMinutesRoundedString;
	}

	
	public BigDecimal getRetentionTimeMinutesRounded() {
		return retentionTimeMinutesRounded;
	}
	public void setRetentionTimeMinutesRounded(BigDecimal retentionTimeMinutesRounded) {
		this.retentionTimeMinutesRounded = retentionTimeMinutesRounded;
	}

	public String getPreMZRounded() {
		return preMZRounded;
	}
	public void setPreMZRounded(String preMZRounded) {
		this.preMZRounded = preMZRounded;
	}
	public BigDecimal getPreMZ() {
		return preMZ;
	}
	public void setPreMZ(BigDecimal preMZ) {
		this.preMZ = preMZ;
	}
	public int getCharge() {
		return charge;
	}
	public void setCharge(int charge) {
		this.charge = charge;
	}
	public BigDecimal getRetentionTime() {
		return retentionTime;
	}
	public void setRetentionTime(BigDecimal retentionTime) {
		this.retentionTime = retentionTime;
	}
	public PsmDTO getPsmDTO() {
		return psmDTO;
	}
	public void setPsmDTO(PsmDTO psmDTO) {
		this.psmDTO = psmDTO;
	}
	public int getScanNumber() {
		return scanNumber;
	}
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}
	public String getScanFilename() {
		return scanFilename;
	}
	public void setScanFilename(String scanFilename) {
		this.scanFilename = scanFilename;
	}
}