package org.yeastrc.auth.services;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthSharedObjectUsersDAO;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;
import org.yeastrc.auth.exceptions.AuthSharedObjectRecordNotFoundException;
/**
 * 
 *
 */
public class GetAuthLevel {
	
	private static final Logger log = LoggerFactory.getLogger( GetAuthLevel.class);
	private GetAuthLevel() { }
	private static final GetAuthLevel _INSTANCE = new GetAuthLevel();
	public static GetAuthLevel getInstance() { return _INSTANCE; }
	
	/**
	 * @param authUserId
	 * @param authShareableObjectId
	 * @return
	 * @throws AuthSharedObjectRecordNotFoundException
	 * @throws Exception 
	 */
	public int getAuthLevelForSharableObject( int authUserId, int authShareableObjectId  ) throws AuthSharedObjectRecordNotFoundException, Exception {
		AuthSharedObjectUsersDTO authSharedObjectUsersDTO 
				= AuthSharedObjectUsersDAO.getInstance().getAuthSharedObjectUsersDTOForSharedObjectIdAndUserId( authShareableObjectId, authUserId );
		if ( authSharedObjectUsersDTO == null ) {
			String msg = "no authShareableObject found for authUserId: " + authUserId + ", authShareableObjectId: " + authShareableObjectId;
			if ( log.isInfoEnabled() ) {
				log.info( msg );
			}
			throw new AuthSharedObjectRecordNotFoundException( msg );
		}
		int authAccessLevel = authSharedObjectUsersDTO.getAccessLevel();
		return authAccessLevel;
	}
}
