package org.yeastrc.xlink.www.internal_services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.auth.exceptions.AuthSharedObjectRecordNotFoundException;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.searcher.UserSearcherForAccessLevel;

/**
 * 
 *
 */
public class GetXLinkUserDTOListForGlobalNoAccessUsers {
	
	private static final Logger log = Logger.getLogger(GetXLinkUserDTOListForGlobalNoAccessUsers.class);
	

	//  private constructor
	GetXLinkUserDTOListForGlobalNoAccessUsers() { }
	
	/**
	 * @return newly created instance
	 */
	public static GetXLinkUserDTOListForGlobalNoAccessUsers getInstance() { 
		return new GetXLinkUserDTOListForGlobalNoAccessUsers(); 
	}
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<XLinkUserDTO> getXLinkUserDTOListForAdminUsers(  ) throws AuthSharedObjectRecordNotFoundException, Exception {
		
		int authAccessLevel = AuthAccessLevelConstants.ACCESS_LEVEL_NONE;
		
		List<Integer> userIds = UserSearcherForAccessLevel.getInstance().getAuthUserIdsForAuthAccessLevel( authAccessLevel );

		
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
