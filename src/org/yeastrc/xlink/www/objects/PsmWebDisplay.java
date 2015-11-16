package org.yeastrc.xlink.www.objects;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.www.searcher.Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher;



public class PsmWebDisplay {
	
	private static final Logger log = Logger.getLogger(PsmWebDisplay.class);

	private PsmDTO psmDTO;
	private Integer scanNumber;
	private String scanFilename;
	private BigDecimal retentionTime;
	private BigDecimal retentionTimeMinutesRounded;
	private String retentionTimeMinutesRoundedString;
	

	private double peptideQValueCutoff;
	private double psmQValueCutoff;
	private int searchId;
	
	private Integer charge;
	private BigDecimal preMZ;
	private String preMZRounded; 
	
	private int psmCountForAssocScanId;

	private boolean psmCountForAssocScanIdSet;
	
	

	public Integer getPsmCountForAssocScanId() throws Exception {
		
		if ( psmDTO.getScanId() == null ) {
			
			return null;
		}
		
		try {

			if ( psmCountForAssocScanIdSet ) {

				return psmCountForAssocScanId;
			}

			psmCountForAssocScanId =
					Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher.getInstance()
					.scanCountForAssociatedScanId( psmDTO.getId(), searchId, peptideQValueCutoff, psmQValueCutoff );


			psmCountForAssocScanIdSet = true;

			return psmCountForAssocScanId;
			
		} catch ( Exception e ) {
			
			log.error( "isPsmCountForAssocScanId() Exception " + e.toString(), e );
			
			throw e;
		}
	}
	
	
	/**
	 * @return
	 */
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
	public Integer getCharge() {
		return charge;
	}
	public void setCharge(Integer charge) {
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
	public Integer getScanNumber() {
		return scanNumber;
	}
	public void setScanNumber(Integer scanNumber) {
		this.scanNumber = scanNumber;
	}
	public String getScanFilename() {
		return scanFilename;
	}
	public void setScanFilename(String scanFilename) {
		this.scanFilename = scanFilename;
	}
	

	public double getPeptideQValueCutoff() {
		return peptideQValueCutoff;
	}


	public void setPeptideQValueCutoff(double peptideQValueCutoff) {
		this.peptideQValueCutoff = peptideQValueCutoff;
	}


	public double getPsmQValueCutoff() {
		return psmQValueCutoff;
	}


	public void setPsmQValueCutoff(double psmQValueCutoff) {
		this.psmQValueCutoff = psmQValueCutoff;
	}


	public int getSearchId() {
		return searchId;
	}


	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
}
