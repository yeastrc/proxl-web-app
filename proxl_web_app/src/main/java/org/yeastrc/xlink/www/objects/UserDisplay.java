package org.yeastrc.xlink.www.objects;

import org.yeastrc.xlink.www.dto.XLinkUserDTO;


/**
 * This is used for displaying lists of users along with their access privileges
 *
 */
public class UserDisplay {

	private int userId;
	
	//  Access level for this specific item
	private String userAccessLabel;
	private String userAccessDescription;
	private int userAccessLevelId;

	private XLinkUserDTO xLinkUserDTO;

	

	public int getUserAccessLevelId() {
		return userAccessLevelId;
	}

	public void setUserAccessLevelId(int userAccessLevelId) {
		this.userAccessLevelId = userAccessLevelId;
	}


	
	public String getUserAccessLabel() {
		return userAccessLabel;
	}

	public void setUserAccessLabel(String userAccessLabel) {
		this.userAccessLabel = userAccessLabel;
	}

	public String getUserAccessDescription() {
		return userAccessDescription;
	}

	public void setUserAccessDescription(String userAccessDescription) {
		this.userAccessDescription = userAccessDescription;
	}

	
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}


	public XLinkUserDTO getxLinkUserDTO() {
		return xLinkUserDTO;
	}

	public void setxLinkUserDTO(XLinkUserDTO xLinkUserDTO) {
		this.xLinkUserDTO = xLinkUserDTO;
	}
}

