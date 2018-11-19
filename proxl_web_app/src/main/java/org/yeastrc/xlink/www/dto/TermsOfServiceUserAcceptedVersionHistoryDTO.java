package org.yeastrc.xlink.www.dto;

import java.util.Date;

/**
 * table  terms_of_service_user_accepted_version_history
 *
 */
public class TermsOfServiceUserAcceptedVersionHistoryDTO {

	private int authUserId;
	private int termsOfServiceVersionId;
	private Date acceptedDateTime;
	
	
	public int getAuthUserId() {
		return authUserId;
	}
	public void setAuthUserId(int authUserId) {
		this.authUserId = authUserId;
	}
	public int getTermsOfServiceVersionId() {
		return termsOfServiceVersionId;
	}
	public void setTermsOfServiceVersionId(int termsOfServiceVersionId) {
		this.termsOfServiceVersionId = termsOfServiceVersionId;
	}
	public Date getAcceptedDateTime() {
		return acceptedDateTime;
	}
	public void setAcceptedDateTime(Date acceptedDateTime) {
		this.acceptedDateTime = acceptedDateTime;
	}
	
	
}
