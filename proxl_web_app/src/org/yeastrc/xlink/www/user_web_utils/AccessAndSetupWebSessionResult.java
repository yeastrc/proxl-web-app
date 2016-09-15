package org.yeastrc.xlink.www.user_web_utils;

import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.user_account.UserSessionObject;

public class AccessAndSetupWebSessionResult {

	private boolean noSession;
	private UserSessionObject userSessionObject;
	private AuthAccessLevel authAccessLevel;

	
	
	public UserSessionObject getUserSessionObject() {
		return userSessionObject;
	}

	public void setUserSessionObject(UserSessionObject userSessionObject) {
		this.userSessionObject = userSessionObject;
	}
	public boolean isNoSession() {
		return noSession;
	}

	public void setNoSession(boolean noSession) {
		this.noSession = noSession;
	}

	public AuthAccessLevel getAuthAccessLevel() {
		return authAccessLevel;
	}
	public void setAuthAccessLevel(AuthAccessLevel authAccessLevel) {
		this.authAccessLevel = authAccessLevel;
	}
}
