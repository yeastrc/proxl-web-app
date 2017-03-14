package org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto;

import java.util.Date;

import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportRunSubStatus;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus;

/**
 * table  file_import_proxl_xml_scans_tracking_run
 *
 */
public class ProxlXMLFileImportTrackingRunDTO {

	private int id;

	private int proxlXmlFileImportTrackingId;
	
	private boolean currentRun;
	
	private ProxlXMLFileImportStatus runStatus;
	
	private ProxlXMLFileImportRunSubStatus runSubStatus;

	// TODO  Not populated yet:  importer_percent_psms_processed
	
	private Integer insertedSearchId;
	
	/**
	 * TODO  currently populated with same as dataErrorText when data error
	 */
	private String importResultText;
	
	private String dataErrorText;
	

	private Date startDateTime;
	private Date lastUpdatedDateTime;

	
	@Override
	public String toString() {

		Integer runSubStatusId = null;
		
		if ( runSubStatus != null ) {
			
			runSubStatusId = runSubStatus.value();
		}
		
		return "ProxlXMLFileImportTrackingRunDTO [id=" + id
				+ ", proxlXmlFileImportTrackingId="
				+ proxlXmlFileImportTrackingId + ", currentRun=" + currentRun
				+ ", runStatus=" + runStatus 
				+ ", runSubStatus=" + runSubStatus
				+ ", runSubStatusId=" + runSubStatusId
				+ ", insertedSearchId=" + insertedSearchId
				+ ", importResultText=" + importResultText + ", dataErrorText="
				+ dataErrorText + ", startDateTime=" + startDateTime
				+ ", lastUpdatedDateTime=" + lastUpdatedDateTime + "]";
	}

	
	
	/**
	 * TODO  currently populated with same as dataErrorText when data error
	 * @return
	 */
	public String getImportResultText() {
		return importResultText;
	}
	/**
	 * TODO  currently populated with same as dataErrorText when data error
	 * @param importResultText
	 */
	public void setImportResultText(String importResultText) {
		this.importResultText = importResultText;
	}

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProxlXmlFileImportTrackingId() {
		return proxlXmlFileImportTrackingId;
	}
	public void setProxlXmlFileImportTrackingId(int proxlXmlFileImportTrackingId) {
		this.proxlXmlFileImportTrackingId = proxlXmlFileImportTrackingId;
	}
	public ProxlXMLFileImportStatus getRunStatus() {
		return runStatus;
	}
	public void setRunStatus(ProxlXMLFileImportStatus runStatus) {
		this.runStatus = runStatus;
	}
	public String getDataErrorText() {
		return dataErrorText;
	}
	public void setDataErrorText(String dataErrorText) {
		this.dataErrorText = dataErrorText;
	}
	public Date getLastUpdatedDateTime() {
		return lastUpdatedDateTime;
	}
	public void setLastUpdatedDateTime(Date lastUpdatedDateTime) {
		this.lastUpdatedDateTime = lastUpdatedDateTime;
	}
	public Integer getInsertedSearchId() {
		return insertedSearchId;
	}
	public void setInsertedSearchId(Integer insertedSearchId) {
		this.insertedSearchId = insertedSearchId;
	}
	public Date getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}


	public ProxlXMLFileImportRunSubStatus getRunSubStatus() {
		return runSubStatus;
	}


	public void setRunSubStatus(ProxlXMLFileImportRunSubStatus runSubStatus) {
		this.runSubStatus = runSubStatus;
	}


	public boolean isCurrentRun() {
		return currentRun;
	}


	public void setCurrentRun(boolean currentRun) {
		this.currentRun = currentRun;
	}

	

}
