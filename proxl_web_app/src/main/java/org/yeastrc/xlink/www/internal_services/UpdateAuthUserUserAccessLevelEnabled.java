package org.yeastrc.xlink.www.internal_services;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;

/**
 * 
 *
 */
public class UpdateAuthUserUserAccessLevelEnabled {


	private static final Logger log = LoggerFactory.getLogger( UpdateAuthUserUserAccessLevelEnabled.class);
	UpdateAuthUserUserAccessLevelEnabled() { }
	private static UpdateAuthUserUserAccessLevelEnabled _INSTANCE = new UpdateAuthUserUserAccessLevelEnabled();
	public static UpdateAuthUserUserAccessLevelEnabled getInstance() { return _INSTANCE; }
	
	

	/**
	 * WARNING: Not yet coded:   Enabled from User Mgmt Web app
	 * 
	 * Update authUser with current userAccessLevel from DB and Enabled from User Mgmt Web app
	 * @param authUser
	 * @throws Exception
	 */
	public void updateAuthUserUserAccessLevelEnabled( AuthUserDTO authUser ) throws Exception {

		Integer userAccessLevel = AuthUserDAO.getInstance().getUserAccessLevel( authUser.getId() );
		authUser.setUserAccessLevel( userAccessLevel );
		
		//  TODO:  Update Enabled from User Mgmt Web app
	}
	
}
