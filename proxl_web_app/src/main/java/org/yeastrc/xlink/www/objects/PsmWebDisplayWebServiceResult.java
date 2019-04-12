package org.yeastrc.xlink.www.objects;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;
import org.yeastrc.xlink.www.searcher.Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher;

import com.fasterxml.jackson.annotation.JsonIgnore;



public class PsmWebDisplayWebServiceResult extends SortDisplayRecordsWrapperBase {
	
	private static final Logger log = LoggerFactory.getLogger( PsmWebDisplayWebServiceResult.class);

	private PsmDTO psmDTO;
	private Integer scanNumber;
	private String scanFilename;

	private int searchId;
	
	private Integer charge; // from psm
	

	//  From scan
	private BigDecimal retentionTime;
	private BigDecimal retentionTimeMinutesRounded;
	private String retentionTimeMinutesRoundedString;

	private BigDecimal preMZ;
	private String preMZRounded; 

	private Integer scanFileId;

	
	//  Searched for in DB:
	
	private int psmCountForAssocScanId;

	private boolean psmCountForAssocScanIdSet;
	
	//  Retrieved for psm
	
	private List<String> psmAnnotationValueList;
	
	
	
	/**
	 * Used for sorting when all the values match so that every time the data is displayed, it is in the same order
	 * @return
	 */
	@JsonIgnore
	@Override
	public int getFinalSortOrderKey() {

		return psmDTO.getId();
	}



	@JsonIgnore
	private SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel;
	

	/**
	 * psm.psmCountForOtherAssocScanId is count of psms with same scan id, excluding current psm
	 * 
	 * @return
	 * @throws Exception
	 */
	public Integer getPsmCountForOtherAssocScanId() throws Exception {
		
		
		if ( psmDTO.getScanId() == null ) {
			
			return null;
		}
		
		try {

			if ( psmCountForAssocScanIdSet ) {

				return psmCountForAssocScanId;
			}

			psmCountForAssocScanId =
					Psm_ScanCountForAssociatedScanId_From_PsmId_SearchId_Searcher.getInstance()
					.scanCountForAssociatedScanId( psmDTO, searchId, searcherCutoffValuesSearchLevel );


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

	public SearcherCutoffValuesSearchLevel getSearcherCutoffValuesSearchLevel() {
		return searcherCutoffValuesSearchLevel;
	}
	public void setSearcherCutoffValuesSearchLevel(
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel) {
		this.searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel;
	}


	
	

	@Override
	public List<String> getPsmAnnotationValueList() {
		return psmAnnotationValueList;
	}


	@Override
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		this.psmAnnotationValueList = psmAnnotationValueList;
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
	
	public int getSearchId() {
		return searchId;
	}


	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}


	@Override
	public List<String> getPeptideAnnotationValueList() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		// TODO Auto-generated method stub
		
	}


	public Integer getScanFileId() {
		return scanFileId;
	}


	public void setScanFileId(Integer scanFileId) {
		this.scanFileId = scanFileId;
	}


}
