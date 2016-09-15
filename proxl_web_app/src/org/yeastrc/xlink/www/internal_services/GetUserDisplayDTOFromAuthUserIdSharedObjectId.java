package org.yeastrc.xlink.www.internal_services;

import org.apache.log4j.Logger;
import org.yeastrc.auth.exceptions.AuthSharedObjectRecordNotFoundException;
import org.yeastrc.auth.services.GetAuthLevel;
import org.yeastrc.xlink.www.dao.XLUserAccessLevelLabelDescriptionDAO;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.dto.XLUserAccessLevelLabelDescriptionDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.objects.UserDisplay;

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
		
		XLinkUserDTO xLinkUserDTO = XLinkUserDAO.getInstance().getXLinkUserDTOForAuthUserId( authUserId );
		
		if ( xLinkUserDTO == null ) {
			
			return null;
		}
		
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
