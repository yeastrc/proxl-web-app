package org.yeastrc.auth.dto;



/**
 * auth_shared_object table
 *
 */
public class AuthSharedObjectDTO {

	private int sharedObjectId;
	private boolean publicAccessCodeEnabled;
	private String publicAccessCode;
	
	
	public boolean isPublicAccessCodeEnabled() {
		return publicAccessCodeEnabled;
	}
	public void setPublicAccessCodeEnabled(boolean publicAccessCodeEnabled) {
		this.publicAccessCodeEnabled = publicAccessCodeEnabled;
	}
	public String getPublicAccessCode() {
		return publicAccessCode;
	}
	public void setPublicAccessCode(String publicAccessCode) {
		this.publicAccessCode = publicAccessCode;
	}
	public int getSharedObjectId() {
		return sharedObjectId;
	}
	public void setSharedObjectId(int sharedObjectId) {
		this.sharedObjectId = sharedObjectId;
	}
	

	
}
//
//CREATE TABLE IF NOT EXISTS auth_shared_object (
//		  shared_object_id INT UNSIGNED NOT NULL,
//		  public_access_code_enabled TINYINT(1) NOT NULL DEFAULT false,
//		  public_access_code VARCHAR(255) NULL,
//		  