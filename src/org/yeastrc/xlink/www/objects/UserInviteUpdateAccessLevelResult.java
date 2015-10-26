package org.yeastrc.xlink.www.objects;

/**
 * This is returned from the web service UserInviteUpdateService
 *
 */
public class UserInviteUpdateAccessLevelResult {

	private boolean status;
	
	private boolean inviteAlreadyUsed;
	private boolean inviteNoLongerValid;
	private boolean projectIdIncorrectForThisInviteId;

	public boolean isInviteAlreadyUsed() {
		return inviteAlreadyUsed;
	}

	public void setInviteAlreadyUsed(boolean inviteAlreadyUsed) {
		this.inviteAlreadyUsed = inviteAlreadyUsed;
	}

	public boolean isProjectIdIncorrectForThisInviteId() {
		return projectIdIncorrectForThisInviteId;
	}

	public void setProjectIdIncorrectForThisInviteId(
			boolean projectIdIncorrectForThisInviteId) {
		this.projectIdIncorrectForThisInviteId = projectIdIncorrectForThisInviteId;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isInviteNoLongerValid() {
		return inviteNoLongerValid;
	}

	public void setInviteNoLongerValid(boolean inviteNoLongerValid) {
		this.inviteNoLongerValid = inviteNoLongerValid;
	}

}
