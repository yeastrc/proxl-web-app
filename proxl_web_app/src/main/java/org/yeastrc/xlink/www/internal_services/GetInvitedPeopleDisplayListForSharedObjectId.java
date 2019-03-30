package org.yeastrc.xlink.www.internal_services;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.auth.exceptions.AuthSharedObjectRecordNotFoundException;
import org.yeastrc.auth.searchers.AuthUserInvitesActiveUnusedNotReplacedNotRevokedSearcher;
import org.yeastrc.xlink.www.objects.InvitedPersonDisplay;
import org.yeastrc.xlink.www.web_utils.FormatDateToYYYYMMDD;

/**
 * 
 *
 */
public class GetInvitedPeopleDisplayListForSharedObjectId {

	private static final Logger log = LoggerFactory.getLogger( GetInvitedPeopleDisplayListForSharedObjectId.class);
	//  private constructor
	private GetInvitedPeopleDisplayListForSharedObjectId() { }
	/**
	 * @return newly created instance
	 */
	public static GetInvitedPeopleDisplayListForSharedObjectId getInstance() { 
		return new GetInvitedPeopleDisplayListForSharedObjectId(); 
	}
	
	/**
	 * @param sharedObjectId
	 * @return
	 * @throws Exception
	 */
	public List<InvitedPersonDisplay> getInvitedPersonDisplayListForSharedObjectId( int sharedObjectId ) throws AuthSharedObjectRecordNotFoundException, Exception {
		try {
			FormatDateToYYYYMMDD formatDateToYYYYMMDD = FormatDateToYYYYMMDD.getInstance();
			List<AuthUserInviteTrackingDTO> inviteList = AuthUserInvitesActiveUnusedNotReplacedNotRevokedSearcher.getInstance().getAuthUserInvitesActiveForSharedObjectId( sharedObjectId );
			List<InvitedPersonDisplay> returnList = new ArrayList<InvitedPersonDisplay>( inviteList.size() );
			for ( AuthUserInviteTrackingDTO invite : inviteList ) {
				InvitedPersonDisplay invitedPersonDisplay = new InvitedPersonDisplay();
				invitedPersonDisplay.setInviteId( invite.getId() );
				invitedPersonDisplay.setInvitedUserEmail( invite.getInvitedUserEmail() );
				invitedPersonDisplay.setInvitedUserAccessLevel( invite.getInvitedUserAccessLevel() );
				String formattedDate = formatDateToYYYYMMDD.formatDateToYYYY_MM_DD( invite.getInviteCreateDate() );
				invitedPersonDisplay.setInviteDate( formattedDate );
				returnList.add( invitedPersonDisplay );
			}
			return returnList;
		} catch ( Exception ex ) {
			String msg = "Error processing getInvitedPersonDisplayListForSharedObjectId(...) for sharedObjectId: " + sharedObjectId;
			log.error( msg, ex );
			throw ex;
		}
	}
}
