package org.yeastrc.xlink.www.objects;


/**
 * This is used for displaying lists of invited people along with their access privileges
 *
 */
public class InvitedPersonDisplay {

	private int inviteId;

	private String invitedUserEmail;
	
	private int invitedUserAccessLevel;

	private String inviteDate;
	
	private Integer projectId;
	
//	//  Access level for this specific item
//	private String userAccessLabel;
//	private String userAccessDescription;
//	private int userAccessLevelId;
	
	

	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public int getInviteId() {
		return inviteId;
	}
	public void setInviteId(int inviteId) {
		this.inviteId = inviteId;
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
	public String getInviteDate() {
		return inviteDate;
	}
	public void setInviteDate(String inviteDate) {
		this.inviteDate = inviteDate;
	}
}

