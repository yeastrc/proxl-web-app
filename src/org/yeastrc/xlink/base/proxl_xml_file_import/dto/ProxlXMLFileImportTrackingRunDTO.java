package org.yeastrc.xlink.base.proxl_xml_file_import.dto;

import java.util.Date;

import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;

/**
 * table  proxl_xml_file_import_tracking_run
 *
 */
public class ProxlXMLFileImportTrackingRunDTO {

	private int id;

	private int proxlXmlFileImportTrackingId;
	
	private ProxlXMLFileImportStatus runStatus;
	
	// TODO  Not populated yet:  importer_sub_status_id
	
	// TODO  Not populated yet:  importer_percent_psms_processed
	
	private Integer insertedSearchId;
	
	/**
	 * TODO  Not currently populated
	 */
	private String importResultText;
	
	private String dataErrorText;
	

	private Date startDateTime;
	private Date lastUpdatedDateTime;
	
	@Override
	public String toString() {
		return "ProxlXMLFileImportTrackingRunDTO [id=" + id
				+ ", proxlXmlFileImportTrackingId="
				+ proxlXmlFileImportTrackingId + ", runStatus=" + runStatus
				+ ", insertedSearchId=" + insertedSearchId
				+ ", importResultText=" + importResultText + ", dataErrorText="
				+ dataErrorText + ", startDateTime=" + startDateTime
				+ ", lastUpdatedDateTime=" + lastUpdatedDateTime + "]";
	}
	
	
	/**
	 * TODO  Not currently populated
	 * @return
	 */
	public String getImportResultText() {
		return importResultText;
	}
	/**
	 * TODO  Not currently populated
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

	

}
