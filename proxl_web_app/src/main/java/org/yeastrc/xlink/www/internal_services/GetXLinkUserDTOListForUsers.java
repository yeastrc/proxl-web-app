package org.yeastrc.xlink.www.internal_services;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.searcher.UserSearcherAll;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataResponse;


/**
 * 
 *
 */
public class GetXLinkUserDTOListForUsers {

	private static final Logger log = LoggerFactory.getLogger( GetXLinkUserDTOListForUsers.class);
	//  private constructor
	private GetXLinkUserDTOListForUsers() { }
	/**
	 * @return newly created instance
	 */
	public static GetXLinkUserDTOListForUsers getInstance() { 
		return new GetXLinkUserDTOListForUsers();
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<XLinkUserDTO> getXLinkUserDTOListForAllUsers( ) throws Exception {
		List<Integer> userIds = UserSearcherAll.getInstance().getAllAuthUserIds();
		List<XLinkUserDTO> returnList = new ArrayList<XLinkUserDTO>( userIds.size() );
		for ( int authUserId : userIds ) {

			//  Get User Mgmt User Id for authUserId
			Integer userMgmtUserId = AuthUserDAO.getInstance().getUserMgmtUserIdForId( authUserId );
			if ( userMgmtUserId == null ) {
				String msg = "Failed to get userMgmtUserId for Proxl auth user id: " + authUserId;
				log.warn( msg );
		        return null;  //  Early Exit
			}
			
			//  Get full user data
			UserMgmtGetUserDataRequest userMgmtGetUserDataRequest = new UserMgmtGetUserDataRequest();
//			userMgmtGetUserDataRequest.setSessionKey( userMgmtLoginResponse.getSessionKey() );
			userMgmtGetUserDataRequest.setUserId( userMgmtUserId );
			UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = 
					UserMgmtCentralWebappWebserviceAccess.getInstance().getUserData( userMgmtGetUserDataRequest );
			if ( ! userMgmtGetUserDataResponse.isSuccess() ) {
				String msg = "Failed to get Full user data from User Mgmt Webapp for authUserId: " + authUserId
						+ ", userMgmtUserId: " + userMgmtUserId;
				log.error( msg );
				continue;  //  EARLY CONTINUE to next entry
			}
			
			Boolean enabledAppSpecific =
					AuthUserDAO.getInstance().getUserEnabledAppSpecific( authUserId );
			if ( enabledAppSpecific == null ) {
				String msg = "Failed to get enabledAppSpecific from proxl auth_user table for user id: " + authUserId;
				log.error( msg );
				continue;  //  EARLY CONTINUE to next entry
			}
			
			//  Get user Access level at account level from proxl db
			Integer userAccessLevel = AuthUserDAO.getInstance().getUserAccessLevel( authUserId );
			if ( userAccessLevel == null ) {
				String msg = "Failed to get userAccessLevel from proxl auth_user table for user id: " + authUserId;
				log.error( msg );
				continue;  //  EARLY CONTINUE to next entry
			}
			XLinkUserDTO xLinkUserDTO = new XLinkUserDTO();
			AuthUserDTO authUserDTO = new AuthUserDTO();
			xLinkUserDTO.setAuthUser(authUserDTO);
			authUserDTO.setId( authUserId );
			authUserDTO.setUserMgmtUserId( userMgmtUserId );
			authUserDTO.setUsername( userMgmtGetUserDataResponse.getUsername() );
			authUserDTO.setEmail( userMgmtGetUserDataResponse.getEmail() );
			authUserDTO.setUserAccessLevel( userAccessLevel );
			authUserDTO.setEnabledAppSpecific( enabledAppSpecific );
			authUserDTO.setEnabledUserMgmtGlobalLevel( userMgmtGetUserDataResponse.isEnabled() );
			
			xLinkUserDTO.setFirstName( userMgmtGetUserDataResponse.getFirstName() );
			xLinkUserDTO.setLastName( userMgmtGetUserDataResponse.getLastName() );
			xLinkUserDTO.setOrganization( userMgmtGetUserDataResponse.getOrganization() );
			returnList.add( xLinkUserDTO );
		}
		return returnList;
	}
}
