package org.yeastrc.xlink.www.internal_services;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.XLUserAccessLevelLabelDescriptionDAO;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.dto.XLUserAccessLevelLabelDescriptionDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.objects.UserDisplay;

/**
 * 
 *
 */
public class GetUserDisplayDTOFromAuthUserId {
	
	private static final Logger log = Logger.getLogger(GetUserDisplayDTOFromAuthUserId.class);
	

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
		
		XLinkUserDTO xLinkUserDTO = XLinkUserDAO.getInstance().getXLinkUserDTOForAuthUserId( authUserId );
		
		if ( xLinkUserDTO == null ) {
			
			return null;
		}
		
		
		returnItem.setxLinkUserDTO( xLinkUserDTO );

		
		Integer userAccessLevel = xLinkUserDTO.getAuthUser().getUserAccessLevel();
		
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
