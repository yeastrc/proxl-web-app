package org.yeastrc.xlink.www.internal_services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.auth.exceptions.AuthSharedObjectRecordNotFoundException;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.searcher.UserSearcherAll;
import org.yeastrc.xlink.www.searcher.UserSearcherForAccessLevel;
import org.yeastrc.xlink.www.searcher.UserSearcherForEnabledFlag;

/**
 * 
 *
 */
public class GetXLinkUserDTOListForUsers {
	
	private static final Logger log = Logger.getLogger(GetXLinkUserDTOListForUsers.class);
	
	
	private UserSearcherForAccessLevel userSearcherForAccessLevel = UserSearcherForAccessLevel.getInstance();
	

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
	 * @throws AuthSharedObjectRecordNotFoundException
	 * @throws Exception
	 */
	public List<XLinkUserDTO> getXLinkUserDTOListForAdminUsers(  ) throws AuthSharedObjectRecordNotFoundException, Exception {
		
		int authAccessLevel = AuthAccessLevelConstants.ACCESS_LEVEL_ADMIN;
		
		return getXLinkUserDTOListForUsers( authAccessLevel );
	}
	
	/**
	 * @return
	 * @throws AuthSharedObjectRecordNotFoundException
	 * @throws Exception
	 */
	public List<XLinkUserDTO> getXLinkUserDTOListForGlobalNoAccessUsers(  ) throws AuthSharedObjectRecordNotFoundException, Exception {
		
		int authAccessLevel = AuthAccessLevelConstants.ACCESS_LEVEL_NONE;
		
		return getXLinkUserDTOListForUsers( authAccessLevel );
	}
	
	
	
	
	
	/**
	 * @param authAccessLevel
	 * @return
	 * @throws AuthSharedObjectRecordNotFoundException
	 * @throws Exception
	 */
	private List<XLinkUserDTO> getXLinkUserDTOListForUsers( int authAccessLevel ) throws AuthSharedObjectRecordNotFoundException, Exception {
		
		List<Integer> userIds = userSearcherForAccessLevel.getAuthUserIdsForAuthAccessLevel( authAccessLevel );

		
		List<XLinkUserDTO> returnList = new ArrayList<XLinkUserDTO>( userIds.size() );

		XLinkUserDAO xLinkUserDAO = XLinkUserDAO.getInstance();
		
		
		for ( int authUserId : userIds ) {
			
			XLinkUserDTO xLinkUserDTO = xLinkUserDAO.getXLinkUserDTOForAuthUserId( authUserId );

			if ( xLinkUserDTO == null ) {

				String msg = "Unexpected null for user id: " + authUserId;

				log.error( msg );

			} else {
				returnList.add( xLinkUserDTO );
			}
		}
		

		return returnList;
	}
	
	
	
	/**
	 * @return
	 * @throws AuthSharedObjectRecordNotFoundException
	 * @throws Exception
	 */
	public List<XLinkUserDTO> getXLinkUserDTOListForEnabledUsers(  ) throws AuthSharedObjectRecordNotFoundException, Exception {
		
		boolean enabledFlag = true;
		
		return getXLinkUserDTOListForUsersFromEnabledFlag( enabledFlag );
	}
	
	/**
	 * @return
	 * @throws AuthSharedObjectRecordNotFoundException
	 * @throws Exception
	 */
	public List<XLinkUserDTO> getXLinkUserDTOListForDisabledUsers(  ) throws AuthSharedObjectRecordNotFoundException, Exception {
		
		boolean enabledFlag = false;

		return getXLinkUserDTOListForUsersFromEnabledFlag( enabledFlag );
	}
	
	
	
	
	
	/**
	 * @param enabledFlag
	 * @return
	 * @throws AuthSharedObjectRecordNotFoundException
	 * @throws Exception
	 */
	private List<XLinkUserDTO> getXLinkUserDTOListForUsersFromEnabledFlag( boolean enabledFlag ) throws AuthSharedObjectRecordNotFoundException, Exception {
		
		List<Integer> userIds = UserSearcherForEnabledFlag.getInstance().getAuthUserIdsForEnabledFlag( enabledFlag );

		
		List<XLinkUserDTO> returnList = new ArrayList<XLinkUserDTO>( userIds.size() );

		XLinkUserDAO xLinkUserDAO = XLinkUserDAO.getInstance();
		
		
		for ( int authUserId : userIds ) {
			
			XLinkUserDTO xLinkUserDTO = xLinkUserDAO.getXLinkUserDTOForAuthUserId( authUserId );

			if ( xLinkUserDTO == null ) {

				String msg = "Unexpected null for user id: " + authUserId;

				log.error( msg );

			} else {
				returnList.add( xLinkUserDTO );
			}
		}
		

		return returnList;
	}
	
	
	
	
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<XLinkUserDTO> getXLinkUserDTOListForAllUsers( ) throws Exception {
		
		List<Integer> userIds = UserSearcherAll.getInstance().getAllAuthUserIds();

		
		List<XLinkUserDTO> returnList = new ArrayList<XLinkUserDTO>( userIds.size() );

		XLinkUserDAO xLinkUserDAO = XLinkUserDAO.getInstance();
		
		
		for ( int authUserId : userIds ) {
			
			XLinkUserDTO xLinkUserDTO = xLinkUserDAO.getXLinkUserDTOForAuthUserId( authUserId );

			if ( xLinkUserDTO == null ) {

				String msg = "Unexpected null for user id: " + authUserId;

				log.error( msg );

			} else {
				returnList.add( xLinkUserDTO );
			}
		}
		

		return returnList;
	}
	
	
}
