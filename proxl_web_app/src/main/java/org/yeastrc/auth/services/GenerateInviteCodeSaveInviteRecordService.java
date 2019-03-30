package org.yeastrc.auth.services;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthUserInviteTrackingDAO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.auth.utils.GenerateRandomStringForCode;

/**
 * 
 *	Generates a invite code and saves it and the rest of the record in the auth_user_invite_tracking table
 */
public class GenerateInviteCodeSaveInviteRecordService {
	
	private static final Logger log = LoggerFactory.getLogger( GenerateInviteCodeSaveInviteRecordService.class);
	
	private GenerateInviteCodeSaveInviteRecordService() { }
	private static final GenerateInviteCodeSaveInviteRecordService _INSTANCE = new GenerateInviteCodeSaveInviteRecordService();
	public static GenerateInviteCodeSaveInviteRecordService getInstance() { return _INSTANCE; }
	
	/**
	 * Generates a invite code and saves it and the rest of the record in the auth_user_invite_tracking table
	 * @param authUserInviteTrackingDTO
	 * @return invite code
	 * @throws Exception 
	 */
	public void generateInviteCodeSaveInviteRecordService( AuthUserInviteTrackingDTO authUserInviteTrackingDTO ) throws Exception {
		try {
			String inviteTrackingCode = GenerateRandomStringForCode.getInstance().generateRandomStringForCode();
			authUserInviteTrackingDTO.setInviteTrackingCode( inviteTrackingCode );
			AuthUserInviteTrackingDAO authUserInviteTrackingDAO = AuthUserInviteTrackingDAO.getInstance();
			authUserInviteTrackingDAO.save( authUserInviteTrackingDTO );
			authUserInviteTrackingDAO.updateCodeReplacedByNewerToTrueForPrevInvites( authUserInviteTrackingDTO );
		} catch ( Exception e ) {
			String msg = "Exception  SubmittingAuthUserId: " + authUserInviteTrackingDTO.getSubmittingAuthUserId()
					+ ", submitIP: " +  authUserInviteTrackingDTO.getSubmitIP() 
					+ ", Exception: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}

}
