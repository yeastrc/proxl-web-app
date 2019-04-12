package org.yeastrc.xlink.www.internal_services;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.services.GetAuthLevel;
import org.yeastrc.xlink.dto.XLUserAccessLevelLabelDescriptionDTO;
import org.yeastrc.xlink.www.dao.XLUserAccessLevelLabelDescriptionDAO;
import org.yeastrc.xlink.www.objects.UserDisplay;
import org.yeastrc.xlink.www.searcher.UsersForSharedObjectIdSearcher;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataResponse;

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
	public List<UserDisplay> getUserDisplayListExcludeAdminGlobalNoAccessAccountsForSharedObjectId( int sharedObjectId ) throws Exception {
		
		List<Integer> userIds = UsersForSharedObjectIdSearcher.getInstance().getAuthUserIdsExcludeGlobalNoAccessAccountsForSharedObjectId( sharedObjectId );
		
		List<UserDisplay> returnList = new ArrayList<UserDisplay>( userIds.size() );
		for ( int userId : userIds ) {
				UserDisplay userDisplay = getUserDisplayDTOFromAuthUserIdSharedObjectId( userId, sharedObjectId );
				if ( userDisplay != null ) {
					returnList.add( userDisplay );
				}
		}
		return returnList;
	}
	

	/**
	 * @param authUserId
	 * @param sharedObjectId
	 * @return
	 * @throws Exception
	 */
	private UserDisplay getUserDisplayDTOFromAuthUserIdSharedObjectId( int authUserId, int sharedObjectId ) throws Exception {
		
		UserDisplay returnItem = new UserDisplay();
		
		returnItem.setUserId( authUserId );
		
		//  Get user data for authUserId
		
		//  Get User Mgmt User Id for authUserId
		Integer userMgmtUserId = AuthUserDAO.getInstance().getUserMgmtUserIdForId( authUserId );
		if ( userMgmtUserId == null ) {
			String msg = "Failed to get userMgmtUserId for Proxl auth user id: " + authUserId;
			log.warn( msg );
	        return null;  //  Early Exit
		}
		
		UserMgmtGetUserDataRequest userMgmtGetUserDataRequest = new UserMgmtGetUserDataRequest();
		// TODO Session Key check currently Disabled in web service 
//		userMgmtGetUserDataRequest.setSessionKey( userMgmtLoginResponse.getSessionKey() );
		userMgmtGetUserDataRequest.setUserId( userMgmtUserId );
		
		UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = 
				UserMgmtCentralWebappWebserviceAccess.getInstance().getUserData( userMgmtGetUserDataRequest );
		
		if ( ! userMgmtGetUserDataResponse.isSuccess() ) {
			String msg = "Failed to get Full user data from User Mgmt Webapp for authUserId: " + authUserId
					+ ", userMgmtUserId: " + userMgmtUserId;
			log.warn( msg );
	        return null;  //  Early Exit
		}

		//  Get user Access level at account level from proxl db
		
		Integer userAccessLevel = AuthUserDAO.getInstance().getUserAccessLevel( authUserId );
		if ( ! userMgmtGetUserDataResponse.isSuccess() ) {
			String msg = "Failed to get userAccessLevel from proxl auth_user table for user id: " + authUserId;
			log.error( msg );
			return null;  //  Early Exit
		}

		if ( ! userMgmtGetUserDataResponse.isEnabled() ) {
			//  Not enabled so skip
			return null;  //  Early Exit
		}

		returnItem.setFirstName( userMgmtGetUserDataResponse.getFirstName() );
		returnItem.setLastName( userMgmtGetUserDataResponse.getLastName() );
				
		int authLevel = GetAuthLevel.getInstance().getAuthLevelForSharableObject( authUserId, sharedObjectId );

		returnItem.setUserAccessLevelId( authLevel );
		
		XLUserAccessLevelLabelDescriptionDTO xlUserAccessLevelLabelDescriptionDTO 
			= XLUserAccessLevelLabelDescriptionDAO.getInstance().getXLUserAccessLevelLabelDescriptionDTOForAuthUserId( authLevel );

		if ( xlUserAccessLevelLabelDescriptionDTO != null ) {

			returnItem.setUserAccessLabel( xlUserAccessLevelLabelDescriptionDTO.getLabel() );
			returnItem.setUserAccessDescription( xlUserAccessLevelLabelDescriptionDTO.getDescription() );
		}
	
		return returnItem;
	}
	
}
