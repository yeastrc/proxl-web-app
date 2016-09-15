package org.yeastrc.xlink.www.objects;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;

/**
 * 
 *
 */
public class ProjectPublicAccessData {

	
	
	
	private Integer publicAccessLevel;
	private boolean publicAccessLocked;
	
	private boolean publicAccessCodeEnabled;
	private String publicAccessCode;
	
	
	/**
	 * @return true if either public access is active, otherwise false
	 */
	public boolean isAnyPublicAccessEnabled() {
		
		if ( ( publicAccessCodeEnabled && StringUtils.isNotEmpty( publicAccessCode ) )
				|| ( publicAccessLevel != null 
						&& publicAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY ) ) {
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return true if public access without an access code is active, otherwise false
	 */
	public boolean isPublicAccessEnabled() {
		
		if ( publicAccessLevel != null 
				&& publicAccessLevel <= AuthAccessLevelConstants.ACCESS_LEVEL__PUBLIC_ACCESS_CODE_READ_ONLY__PUBLIC_PROJECT_READ_ONLY ) {
			
			return true;
		}
		
		return false;
	}
	
	
	public Integer getPublicAccessLevel() {
		return publicAccessLevel;
	}
	public void setPublicAccessLevel(Integer publicAccessLevel) {
		this.publicAccessLevel = publicAccessLevel;
	}
	public boolean isPublicAccessLocked() {
		return publicAccessLocked;
	}
	public void setPublicAccessLocked(boolean publicAccessLocked) {
		this.publicAccessLocked = publicAccessLocked;
	}
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
}
