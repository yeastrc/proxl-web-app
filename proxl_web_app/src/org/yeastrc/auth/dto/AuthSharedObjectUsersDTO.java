package org.yeastrc.auth.dto;



/**
 * auth_shared_object_users table
 *
 */
public class AuthSharedObjectUsersDTO {

	private int sharedObjectId;
	private int userId;
	private int accessLevel;

	public int getSharedObjectId() {
		return sharedObjectId;
	}
	public void setSharedObjectId(int sharedObjectId) {
		this.sharedObjectId = sharedObjectId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}
	

	
}

//CREATE TABLE IF NOT EXISTS auth_shared_object_users (
//		  shared_object_id INT UNSIGNED NOT NULL,
//		  user_id INT UNSIGNED NOT NULL,
//		  access_level SMALLINT UNSIGNED NOT NULL,
//
