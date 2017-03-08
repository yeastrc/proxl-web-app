package org.yeastrc.xlink.www.internal_services;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.send_email.GetEmailConfig;
import org.yeastrc.xlink.www.send_email.SendEmail;
import org.yeastrc.xlink.www.send_email.SendEmailDTO;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataResponse;
/**
 * 
 *
 */
public class SendEmailForRunImportFinishInternalService {
	private static final Logger log = Logger.getLogger(SendEmailForRunImportFinishInternalService.class);
	private static final int MAX_FAILURE_MESSAGE_LENGTH = 500;
	//  private constructor
	private SendEmailForRunImportFinishInternalService() { }
	/**
	 * @return newly created instance
	 */
	public static SendEmailForRunImportFinishInternalService getInstance() { 
		return new SendEmailForRunImportFinishInternalService();
	}
	
	/**
	 * @param proxlXMLFileImportTrackingDTO
	 * @param proxlXMLFileImportTrackingRunDTO
	 * @throws Exception
	 */
	public void sendEmailForRunImportFinishInternalService(
			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO,
			ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO
			) throws Exception {
		
		int authUserId = proxlXMLFileImportTrackingDTO.getAuthUserId();

		//  Get User Mgmt User Id for authUserId
		Integer userMgmtUserId = AuthUserDAO.getInstance().getUserMgmtUserIdForId( authUserId );
		if ( userMgmtUserId == null ) {
			String msg = "Failed to get userMgmtUserId for Proxl auth user id: " + authUserId;
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}
		
		//  Get full user data
		
		UserMgmtGetUserDataRequest userMgmtGetUserDataRequest = new UserMgmtGetUserDataRequest();
//		userMgmtGetUserDataRequest.setSessionKey( userMgmtLoginResponse.getSessionKey() );
		userMgmtGetUserDataRequest.setUserId( userMgmtUserId );
		
		UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = 
				UserMgmtCentralWebappWebserviceAccess.getInstance().getUserData( userMgmtGetUserDataRequest );
		
		if ( ! userMgmtGetUserDataResponse.isSuccess() ) {
			String msg = "Send import finish email: Failed to get Full user data from User Mgmt Webapp for authUserId: " + authUserId
					+ ", userMgmtUserId: " + userMgmtUserId
					+ ", import tracking id: " + proxlXMLFileImportTrackingDTO.getId();
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}
		
		//  Generate email with invite code
		// Generate and send the email to the user.
		try {
        	SendEmailDTO sendEmailDTO = createMailMessageToSend( 
        			proxlXMLFileImportTrackingDTO, 
        			proxlXMLFileImportTrackingRunDTO,
        			userMgmtGetUserDataResponse.getEmail() );
        	if ( sendEmailDTO != null ) {
        		SendEmail.getInstance().sendEmail( sendEmailDTO );
        	}
		}
		catch (Exception e) {
			log.error( "Send import finish email: Exception: import tracking id: " + proxlXMLFileImportTrackingDTO.getId(), e );
			throw e;
		}
	}
	/**
	 * @param proxlXMLFileImportTrackingDTO
	 * @param proxlXMLFileImportTrackingRunDTO
	 * @param toEmailAddressParam
	 * @return
	 * @throws Exception
	 */
	private SendEmailDTO createMailMessageToSend( 
			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO,
			ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO,
			String toEmailAddressParam ) throws Exception {
		ProxlXMLFileImportStatus status = proxlXMLFileImportTrackingDTO.getStatus();
		String statusText = null;
		if ( status == ProxlXMLFileImportStatus.COMPLETE ) {
			statusText = "finished successfully";
		} else if ( status == ProxlXMLFileImportStatus.FAILED ) {
			statusText = "failed";
		} else {
			log.error( "createMailMessageToSend: tracking status not complete or failed.  Not sending any email."
					+ "  tracking status : " + status.toString() );
			return null;  // EARLY RETURN
		}
		String searchPathWithLabel = "";
		if ( StringUtils.isNotEmpty( proxlXMLFileImportTrackingDTO.getSearchPath() ) ) {
			searchPathWithLabel = "\n\n"
					+ "Search Path: " + proxlXMLFileImportTrackingDTO.getSearchPath();
		}
		String failedMessage = "";
		if ( status == ProxlXMLFileImportStatus.FAILED 
				&& StringUtils.isNotEmpty( proxlXMLFileImportTrackingRunDTO.getDataErrorText() ) ) {
			String dataErrorTextTruncated = proxlXMLFileImportTrackingRunDTO.getDataErrorText();
			if ( dataErrorTextTruncated.length() > MAX_FAILURE_MESSAGE_LENGTH ) {
				dataErrorTextTruncated = dataErrorTextTruncated.substring(0, MAX_FAILURE_MESSAGE_LENGTH );
			}
			failedMessage = "\n\n"
					+ "Import Failure Message (truncated): " 
					+ "\n\n"
					+ dataErrorTextTruncated
					+ "\n\n"
					+ "** END Import Failure Message";
		}
		// set the message body
		String text = 
				"The ProXL Import has " + statusText
				+ ".\n\n"
				+ "Imported short description: " + proxlXMLFileImportTrackingDTO.getSearchName()
				+ searchPathWithLabel
				+ failedMessage
				+ "\n\n"
				+ "Thank you\n\nThe ProXL DB";
		
		String fromEmailAddress = GetEmailConfig.getFromAddress();
		String toEmailAddress = toEmailAddressParam;
		String emailSubject = "ProXL Import " + statusText;
		String emailBody = text;
		
		SendEmailDTO sendEmailDTO = new SendEmailDTO();
		sendEmailDTO.setFromEmailAddress( fromEmailAddress );
		sendEmailDTO.setToEmailAddress( toEmailAddress );
		sendEmailDTO.setEmailSubject( emailSubject );
		sendEmailDTO.setEmailBody( emailBody );
		
		return sendEmailDTO;
	}
}
