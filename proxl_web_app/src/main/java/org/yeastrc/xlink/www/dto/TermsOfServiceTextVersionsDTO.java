package org.yeastrc.xlink.www.dto;

import java.util.Date;

/**
 * table  terms_of_service_text_versions
 *
 */
public class TermsOfServiceTextVersionsDTO {

	private int versionId;
	private String idString;
	private String termsOfServiceText;
	private int createdAuthUserId;
	private Date createdDateTime;
	
	
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
	public String getTermsOfServiceText() {
		return termsOfServiceText;
	}
	public void setTermsOfServiceText(String termsOfServiceText) {
		this.termsOfServiceText = termsOfServiceText;
	}
	public Date getCreatedDateTime() {
		return createdDateTime;
	}
	public void setCreatedDateTime(Date createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	public String getIdString() {
		return idString;
	}
	public void setIdString(String idString) {
		this.idString = idString;
	}
	public int getCreatedAuthUserId() {
		return createdAuthUserId;
	}
	public void setCreatedAuthUserId(int createdAuthUserId) {
		this.createdAuthUserId = createdAuthUserId;
	}

}
