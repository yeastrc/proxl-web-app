package org.yeastrc.xlink.www.internal_services;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.auth.exceptions.AuthSharedObjectRecordNotFoundException;
import org.yeastrc.xlink.www.objects.UserDisplay;
import org.yeastrc.xlink.www.searcher.UsersForSharedObjectIdSearcher;

/**
 * 
 *
 */
public class GetUserDisplayListForSharedObjectId {
	
	private static final Logger log = LoggerFactory.getLogger( GetUserDisplayListForSharedObjectId.class);
	//  private constructor
	private GetUserDisplayListForSharedObjectId() { }
	/**
	 * @return newly created instance
	 */
	public static GetUserDisplayListForSharedObjectId getInstance() { 
		return new GetUserDisplayListForSharedObjectId(); 
	}

	/**
	 * @param sharedObjectId
	 * @return
	 * @throws Exception
	 */
	public List<UserDisplay> getUserDisplayListExcludeAdminGlobalNoAccessAccountsForSharedObjectId( int sharedObjectId ) throws AuthSharedObjectRecordNotFoundException, Exception {
		
		List<Integer> userIds = UsersForSharedObjectIdSearcher.getInstance().getAuthUserIdsExcludeGlobalNoAccessAccountsForSharedObjectId( sharedObjectId );
		
		List<UserDisplay> returnList = new ArrayList<UserDisplay>( userIds.size() );
		GetUserDisplayDTOFromAuthUserIdSharedObjectId getUserDisplayDTOFromAuthUserIdProjectId = GetUserDisplayDTOFromAuthUserIdSharedObjectId.getInstance();
		for ( int userId : userIds ) {
			try {
				UserDisplay userDisplay = getUserDisplayDTOFromAuthUserIdProjectId.getUserDisplayDTOFromAuthUserIdSharedObjectId( userId, sharedObjectId );
				if ( userDisplay == null ) {
					String msg = "getUserDisplayDTOFromAuthUserIdProjectId.getUserDisplayDTOFromAuthUserIdSharedObjectId returns null for user id: " + userId + ", projectId: " + sharedObjectId;
					log.error( msg );
				} else {
					AuthUserDTO authUserDTO = userDisplay.getxLinkUserDTO().getAuthUser();
					if ( authUserDTO.isEnabledUserMgmtGlobalLevel()
							&& authUserDTO.isEnabledAppSpecific() ) { 
						// Only return enabled user
						returnList.add( userDisplay );
					}
				}
			} catch ( AuthSharedObjectRecordNotFoundException e ) {
				String msg = "Unexpected AuthSharedObjectRecordNotFoundException for user id: " + userId + ", projectId: " + sharedObjectId;
				log.error( msg );
			}
		}
		return returnList;
	}
}
