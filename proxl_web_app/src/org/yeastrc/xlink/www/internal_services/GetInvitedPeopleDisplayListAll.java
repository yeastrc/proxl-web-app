package org.yeastrc.xlink.www.internal_services;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.auth.searchers.AuthUserInvitesActiveUnusedNotReplacedNotRevokedSearcher;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.InvitedPersonDisplay;
import org.yeastrc.xlink.www.web_utils.FormatDateToYYYYMMDD;

/**
 * 
 *
 */
public class GetInvitedPeopleDisplayListAll {

	private static final Logger log = Logger.getLogger(GetInvitedPeopleDisplayListAll.class);
	//  private constructor
	private GetInvitedPeopleDisplayListAll() { }
	/**
	 * @return newly created instance
	 */
	public static GetInvitedPeopleDisplayListAll getInstance() { 
		return new GetInvitedPeopleDisplayListAll(); 
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<InvitedPersonDisplay> getInvitedPersonDisplayListAll( ) throws Exception {
		try {
			FormatDateToYYYYMMDD formatDateToYYYYMMDD = FormatDateToYYYYMMDD.getInstance();
			List<AuthUserInviteTrackingDTO> inviteList = AuthUserInvitesActiveUnusedNotReplacedNotRevokedSearcher.getInstance().getAuthUserInvitesActiveAllInvites();
			List<InvitedPersonDisplay> returnList = new ArrayList<InvitedPersonDisplay>( inviteList.size() );
			for ( AuthUserInviteTrackingDTO invite : inviteList ) {
				InvitedPersonDisplay invitedPersonDisplay = new InvitedPersonDisplay();
				invitedPersonDisplay.setInviteId( invite.getId() );
				invitedPersonDisplay.setInvitedUserEmail( invite.getInvitedUserEmail() );
				invitedPersonDisplay.setInvitedUserAccessLevel( invite.getInvitedUserAccessLevel() );
				String formattedDate = formatDateToYYYYMMDD.formatDateToYYYY_MM_DD( invite.getInviteCreateDate() );
				invitedPersonDisplay.setInviteDate( formattedDate );
				Integer inviteAuthShareableObjectId = invite.getInvitedSharedObjectId();
				if ( inviteAuthShareableObjectId != null ) {
					ProjectDTO inviteProjectDTO = ProjectDAO.getInstance().getProjectDTOForAuthShareableObjectId( inviteAuthShareableObjectId );
					if ( inviteProjectDTO != null ) {
						int projectId = inviteProjectDTO.getId();
						invitedPersonDisplay.setProjectId( projectId );
					}
				}
				returnList.add( invitedPersonDisplay );
			}
			return returnList;
		} catch ( Exception ex ) {
			String msg = "Error processing getInvitedPersonDisplayListAll()";
			log.error( msg, ex );
			throw ex;
		}
	}
}
