package org.yeastrc.xlink.www.user_account;

import java.util.HashSet;
import java.util.Set;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
/**
 * The User object stored in the HTTP Session
 *
 */
public class UserSessionObject {
	/**
	 * Populated if a user is logged in
	 */
	private String userLoginSessionKey;
	private XLinkUserDTO userDBObject;
	private volatile long lastPingToSSOServer;
	private Set<Integer> allowedReadAccessProjectIds;
	private Set<String> allowedReadAccessProjectPublicAccessCodes;
	
	/**
	 * @param projectId
	 */
	public void addAllowedReadAccessProjectId( int projectId ) {
		if ( allowedReadAccessProjectIds == null ) {
			allowedReadAccessProjectIds = new HashSet<Integer>();
		}
		allowedReadAccessProjectIds.add( projectId );
	}
	/**
	 * @param publicAccessCode
	 */
	public void addAllowedReadAccessProjectPublicAccessCodes( String publicAccessCode ) {
		if ( allowedReadAccessProjectPublicAccessCodes == null ) {
			allowedReadAccessProjectPublicAccessCodes = new HashSet<String>();
		}
		allowedReadAccessProjectPublicAccessCodes.add( publicAccessCode );
	}
	/**
	 * @param projectId
	 */
	public boolean isProjectIdAllowedReadAccess( int projectId ) {
		if ( allowedReadAccessProjectIds == null ) {
			return false;
		}
		return allowedReadAccessProjectIds.contains( projectId );
	}
	///////////
	//  auth access checks for JSPs
//	/**
//	 * Check if Admin user
//	 */
//	public boolean isUserAdmin( ) {
//		
//		if ( userDBObject == null ) {
//			
//			return false;
//		}
//		
//		//  TODO  Need to actually do a lookup here but the table doesn't exist yet
//		
//		boolean isUserAdmin = true;
//		
//		return isUserAdmin;
//	}
	///////////
	//  Setters and Getters
	public Set<Integer> getAllowedReadAccessProjectIds() {
		return allowedReadAccessProjectIds;
	}
	public void setAllowedReadAccessProjectIds(
			Set<Integer> allowedReadAccessProjectIds) {
		this.allowedReadAccessProjectIds = allowedReadAccessProjectIds;
	}
	public XLinkUserDTO getUserDBObject() {
		return userDBObject;
	}
	public void setUserDBObject(XLinkUserDTO userDBObject) {
		this.userDBObject = userDBObject;
	}
	public Set<String> getAllowedReadAccessProjectPublicAccessCodes() {
		return allowedReadAccessProjectPublicAccessCodes;
	}
	public void setAllowedReadAccessProjectPublicAccessCodes(
			Set<String> allowedReadAccessProjectPublicAccessCodes) {
		this.allowedReadAccessProjectPublicAccessCodes = allowedReadAccessProjectPublicAccessCodes;
	}
	public String getUserLoginSessionKey() {
		return userLoginSessionKey;
	}
	public void setUserLoginSessionKey(String userLoginSessionKey) {
		this.userLoginSessionKey = userLoginSessionKey;
	}
	public long getLastPingToSSOServer() {
		return lastPingToSSOServer;
	}
	public void setLastPingToSSOServer(long lastPingToSSOServer) {
		this.lastPingToSSOServer = lastPingToSSOServer;
	}
}
