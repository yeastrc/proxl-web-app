package org.yeastrc.xlink.www.objects;




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

	private String firstName;
	private String lastName;

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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
}

