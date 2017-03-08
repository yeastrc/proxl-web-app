package org.yeastrc.xlink.www.internal_services;

import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.auth.exceptions.AuthSharedObjectRecordNotFoundException;
import org.yeastrc.auth.services.GetAuthLevel;
import org.yeastrc.xlink.www.dao.XLUserAccessLevelLabelDescriptionDAO;
import org.yeastrc.xlink.dto.XLUserAccessLevelLabelDescriptionDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.objects.UserDisplay;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataResponse;

/**
 * 
 *
 */
public class GetUserDisplayDTOFromAuthUserIdSharedObjectId {
	
	private static final Logger log = Logger.getLogger(GetUserDisplayDTOFromAuthUserIdSharedObjectId.class);

	//  private constructor
	private GetUserDisplayDTOFromAuthUserIdSharedObjectId() { }
	/**
	 * @return newly created instance
	 */
	public static GetUserDisplayDTOFromAuthUserIdSharedObjectId getInstance() { 
		return new GetUserDisplayDTOFromAuthUserIdSharedObjectId(); 
	}
	
	/**
	 * @param authUserId
	 * @param sharedObjectId
	 * @return
	 * @throws AuthSharedObjectRecordNotFoundException
	 * @throws Exception
	 */
	public UserDisplay getUserDisplayDTOFromAuthUserIdSharedObjectId( int authUserId, int sharedObjectId ) throws AuthSharedObjectRecordNotFoundException, Exception {
		
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
			

		XLinkUserDTO xLinkUserDTO = new XLinkUserDTO();
		AuthUserDTO authUserDTO = new AuthUserDTO();
		xLinkUserDTO.setAuthUser(authUserDTO);
		
		authUserDTO.setId( authUserId );
		authUserDTO.setUsername( userMgmtGetUserDataResponse.getUsername() );
		authUserDTO.setEmail( userMgmtGetUserDataResponse.getEmail() );
		authUserDTO.setUserAccessLevel( userAccessLevel );
		authUserDTO.setEnabledUserMgmtGlobalLevel( userMgmtGetUserDataResponse.isEnabled() );

		xLinkUserDTO.setFirstName( userMgmtGetUserDataResponse.getFirstName() );
		xLinkUserDTO.setLastName( userMgmtGetUserDataResponse.getLastName() );
		xLinkUserDTO.setOrganization( userMgmtGetUserDataResponse.getOrganization() );
		
		returnItem.setxLinkUserDTO( xLinkUserDTO );
		
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
