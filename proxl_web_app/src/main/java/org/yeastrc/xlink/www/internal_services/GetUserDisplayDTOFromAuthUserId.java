package org.yeastrc.xlink.www.internal_services;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dao.XLUserAccessLevelLabelDescriptionDAO;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
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
public class GetUserDisplayDTOFromAuthUserId {

	private static final Logger log = LoggerFactory.getLogger( GetUserDisplayDTOFromAuthUserId.class);
	//  private constructor
	private GetUserDisplayDTOFromAuthUserId() { }
	/**
	 * @return newly created instance
	 */
	public static GetUserDisplayDTOFromAuthUserId getInstance() { 
		return new GetUserDisplayDTOFromAuthUserId(); 
	}
	
	/**
	 * @param authUserId
	 * @return
	 * @throws Exception
	 */
	public UserDisplay getUserDisplayDTOFromAuthUserId( int authUserId ) throws Exception {
		UserDisplay returnItem = new UserDisplay();
		//  Get User Mgmt User Id for authUserId
		Integer userMgmtUserId = AuthUserDAO.getInstance().getUserMgmtUserIdForId( authUserId );
		if ( userMgmtUserId == null ) {
			String msg = "Failed to get userMgmtUserId for Proxl auth user id: " + authUserId;
			log.warn( msg );
	        return null;  //  Early Exit
		}
		//  Get full user data
		UserMgmtGetUserDataRequest userMgmtGetUserDataRequest = new UserMgmtGetUserDataRequest();
//		userMgmtGetUserDataRequest.setSessionKey( userMgmtLoginResponse.getSessionKey() );
		userMgmtGetUserDataRequest.setUserId( userMgmtUserId );
		UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = 
				UserMgmtCentralWebappWebserviceAccess.getInstance().getUserData( userMgmtGetUserDataRequest );
		if ( ! userMgmtGetUserDataResponse.isSuccess() ) {
			String msg = "Failed to get Full user data from User Mgmt Webapp for authUserId: " + authUserId
					+ ", userMgmtUserId: " + userMgmtUserId;
			log.error( msg );
			return null;  //  EARLY RETURN
		}
		//  Get user Access level at account level from proxl db
		Integer userAccessLevel = AuthUserDAO.getInstance().getUserAccessLevel( authUserId );
		if ( userAccessLevel == null ) {
			String msg = "Failed to get userAccessLevel from proxl auth_user table for user id: " + authUserId;
			log.error( msg );
			return null;  //  EARLY RETURN
		}
		XLinkUserDTO xLinkUserDTO = new XLinkUserDTO();
		AuthUserDTO authUserDTO = new AuthUserDTO();
		xLinkUserDTO.setAuthUser(authUserDTO);
		authUserDTO.setId( authUserId );
		authUserDTO.setUsername( userMgmtGetUserDataResponse.getUsername() );
		authUserDTO.setEmail( userMgmtGetUserDataResponse.getEmail() );
		authUserDTO.setUserAccessLevel( userAccessLevel );
		xLinkUserDTO.setFirstName( userMgmtGetUserDataResponse.getFirstName() );
		xLinkUserDTO.setLastName( userMgmtGetUserDataResponse.getLastName() );
		xLinkUserDTO.setOrganization( userMgmtGetUserDataResponse.getOrganization() );
		returnItem.setxLinkUserDTO( xLinkUserDTO );
		if ( userAccessLevel != null ) {
			XLUserAccessLevelLabelDescriptionDTO xlUserAccessLevelLabelDescriptionDTO 
				= XLUserAccessLevelLabelDescriptionDAO.getInstance().getXLUserAccessLevelLabelDescriptionDTOForAuthUserId( userAccessLevel );
			if ( xlUserAccessLevelLabelDescriptionDTO != null ) {
				returnItem.setUserAccessLabel( xlUserAccessLevelLabelDescriptionDTO.getLabel() );
				returnItem.setUserAccessDescription( xlUserAccessLevelLabelDescriptionDTO.getDescription() );
			}
		}
		return returnItem;
	}
}
