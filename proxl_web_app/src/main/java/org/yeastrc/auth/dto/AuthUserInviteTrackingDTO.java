package org.yeastrc.auth.dto;

import java.util.Date;
/**
 * auth_user_invite_tracking table
 *
 */
public class AuthUserInviteTrackingDTO {

	private int id;
	private int submittingAuthUserId;
	
	private String invitedUserEmail;
	private int invitedUserAccessLevel;
	private Integer invitedSharedObjectId;
	
	private Date inviteCreateDate;
	private boolean inviteUsed;
	private Date inviteUsedDate;
	private String inviteTrackingCode;
	private String submitIP;
	private String useIP;

	private boolean codeReplacedByNewer;
	
	private boolean inviteRevoked;
	private Integer revokingAuthUserId;
	private Date revokedDate;
	
	
	public Date getRevokedDate() {
		return revokedDate;
	}
	public void setRevokedDate(Date revokedDate) {
		this.revokedDate = revokedDate;
	}
	public int getId() {
		return id;
	}
	public void setRevokingAuthUserId(Integer revokingAuthUserId) {
		this.revokingAuthUserId = revokingAuthUserId;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSubmittingAuthUserId() {
		return submittingAuthUserId;
	}
	public void setSubmittingAuthUserId(int submittingAuthUserId) {
		this.submittingAuthUserId = submittingAuthUserId;
	}
	public String getInvitedUserEmail() {
		return invitedUserEmail;
	}
	public void setInvitedUserEmail(String invitedUserEmail) {
		this.invitedUserEmail = invitedUserEmail;
	}
	public int getInvitedUserAccessLevel() {
		return invitedUserAccessLevel;
	}
	public void setInvitedUserAccessLevel(int invitedUserAccessLevel) {
		this.invitedUserAccessLevel = invitedUserAccessLevel;
	}
	public Integer getInvitedSharedObjectId() {
		return invitedSharedObjectId;
	}
	public void setInvitedSharedObjectId(Integer invitedSharedObjectId) {
		this.invitedSharedObjectId = invitedSharedObjectId;
	}
	public Date getInviteCreateDate() {
		return inviteCreateDate;
	}
	public void setInviteCreateDate(Date inviteCreateDate) {
		this.inviteCreateDate = inviteCreateDate;
	}
	
	
	public boolean isInviteUsed() {
		return inviteUsed;
	}
	public void setInviteUsed(boolean inviteUsed) {
		this.inviteUsed = inviteUsed;
	}
	public Date getInviteUsedDate() {
		return inviteUsedDate;
	}
	public void setInviteUsedDate(Date inviteUsedDate) {
		this.inviteUsedDate = inviteUsedDate;
	}
	public String getInviteTrackingCode() {
		return inviteTrackingCode;
	}
	public void setInviteTrackingCode(String inviteTrackingCode) {
		this.inviteTrackingCode = inviteTrackingCode;
	}
	public String getSubmitIP() {
		return submitIP;
	}
	public void setSubmitIP(String submitIP) {
		this.submitIP = submitIP;
	}
	public String getUseIP() {
		return useIP;
	}
	public void setUseIP(String useIP) {
		this.useIP = useIP;
	}
	public boolean isCodeReplacedByNewer() {
		return codeReplacedByNewer;
	}
	public void setCodeReplacedByNewer(boolean codeReplacedByNewer) {
		this.codeReplacedByNewer = codeReplacedByNewer;
	}
	public boolean isInviteRevoked() {
		return inviteRevoked;
	}
	public void setInviteRevoked(boolean inviteRevoked) {
		this.inviteRevoked = inviteRevoked;
	}
	public int getRevokingAuthUserId() {
		return revokingAuthUserId;
	}

}
