package org.yeastrc.xlink.www.user_web_utils;

import java.nio.charset.StandardCharsets;

import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;

/**
 * Is a password being set longer than the User Mgmt maximum password length.
 *
 * The maximum is in UTF-8 bytes and comes from User Mgmt.
 *
 * Only for setting a password (create account, change password, reset password).  NOT for sign in.
 */
public class PasswordTooLong_Check {

	private static final PasswordTooLong_Check instance = new PasswordTooLong_Check();

	private PasswordTooLong_Check() { }

	public static PasswordTooLong_Check getInstance() {
		return instance;
	}

	/**
	 * @param password
	 * @return true if the UTF-8 byte length of password is greater than the User Mgmt maximum
	 */
	public boolean isPasswordTooLong( String password ) {

		int maxPasswordLength_UTF8_Bytes = UserMgmtCentralWebappWebserviceAccess.getInstance().getMaxPasswordLength_UTF8_Bytes();

		return password.getBytes( StandardCharsets.UTF_8 ).length > maxPasswordLength_UTF8_Bytes;
	}
}
