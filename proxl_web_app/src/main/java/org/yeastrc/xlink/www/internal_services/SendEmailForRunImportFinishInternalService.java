package org.yeastrc.xlink.www.internal_services;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportRunSubStatus;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
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
	private static final Logger log = LoggerFactory.getLogger( SendEmailForRunImportFinishInternalService.class);
	
	private static final int MAX_FAILURE_MESSAGE_LENGTH = 500;
	
	private enum Email_Contents_Control { 
		FOR_USER, 
		FOR_OTHER // used when send email to addresses configured in config_system table 
	}
	
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
		
		//  Generate email 
		// Generate and send the email to the user.
		try {
        	SendEmailDTO sendEmailDTO = createMailMessageToSend( 
        			Email_Contents_Control.FOR_USER,
        			proxlXMLFileImportTrackingDTO, 
        			proxlXMLFileImportTrackingRunDTO,
        			userMgmtGetUserDataResponse.getEmail(), // toEmailAddressParam
        			null, // userEmailAddressParam
        			null // importerBaseDir
        			);
			
        	if ( sendEmailDTO != null ) {
        		SendEmail.getInstance().sendEmail( sendEmailDTO );
        		
        		String extraEmailAddressesToSendTo_CommaDelim =
        				ConfigSystemCaching.getInstance()
        				.getConfigValueForConfigKey( ConfigSystemsKeysConstants.RUN_IMPORT_EXTRA_EMAILS_TO_SEND_TO_KEY );
        		
        		String importerBaseDir = null;
        		
        		try {
        			//  Get File Import base dir
        			importerBaseDir = ConfigSystemCaching.getInstance()
        					.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.file_import_proxl_xml_scans_TEMP_DIR_KEY );
    			} catch ( Throwable t ) {
    				// Log and eat exception
    				
    			}
        		
        		if ( StringUtils.isNotEmpty( extraEmailAddressesToSendTo_CommaDelim ) ) {
        			String[] extraEmailAddressesToSendTo_Array = extraEmailAddressesToSendTo_CommaDelim.split( "," );
        			for ( String extraEmailAddressesToSendTo : extraEmailAddressesToSendTo_Array ) {
        				
        				sendEmailDTO = createMailMessageToSend(
        						Email_Contents_Control.FOR_OTHER, // used when send email to addresses configured in config_system table
        	        			proxlXMLFileImportTrackingDTO, 
        	        			proxlXMLFileImportTrackingRunDTO,
        	        			extraEmailAddressesToSendTo, // toEmailAddressParam
        	        			userMgmtGetUserDataResponse.getEmail(), // userEmailAddressParam
        	        			importerBaseDir // from config
        	        			);
        				
        				sendEmailDTO.setToEmailAddress( extraEmailAddressesToSendTo );
        				SendEmail.getInstance().sendEmail( sendEmailDTO );
        			}
        		}
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
			Email_Contents_Control email_Contents_Control,
			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO,
			ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO,
			String toEmailAddressParam,
			String userEmailAddressParam,
			String importerBaseDir ) throws Exception {
		
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

		if ( email_Contents_Control == Email_Contents_Control.FOR_OTHER
				&& status == ProxlXMLFileImportStatus.FAILED ) {
			ProxlXMLFileImportRunSubStatus proxlXMLFileImportRunSubStatus = proxlXMLFileImportTrackingRunDTO.getRunSubStatus();
			if ( proxlXMLFileImportRunSubStatus == ProxlXMLFileImportRunSubStatus.SYSTEM_ERROR ) {
				statusText = "failed with System Error";
			}
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
		String importedShortDescription = null;
		if ( StringUtils.isNotEmpty( proxlXMLFileImportTrackingDTO.getSearchName() ) ) {
			importedShortDescription = "Imported short description: " + proxlXMLFileImportTrackingDTO.getSearchName();
		} else {
			importedShortDescription = "No Imported short description";
		}
			
		
		// set the message body
		String text = 
				"The ProXL Import has " + statusText
				+ ".\n\n"
				+ importedShortDescription
				+ searchPathWithLabel
				+ failedMessage
				+ "\n\n"
				+ "Thank you\n\nThe ProXL DB";
		
		if ( email_Contents_Control == Email_Contents_Control.FOR_OTHER ) {
			
			String importTrackingLine = "";
			if ( proxlXMLFileImportTrackingDTO != null ) {
				importTrackingLine = "\nImportTrackId: " + proxlXMLFileImportTrackingDTO.getId();
			}
			
			String importerBaseDirLine = "";
			if ( StringUtils.isNotEmpty( importerBaseDir ) ) {
				importerBaseDirLine = "\n Importer Base dir: " + importerBaseDir;
			}
			
			
			text += "\n\nThe above text was sent to email address: " + userEmailAddressParam + "\n"
					+ "Project Id: " + proxlXMLFileImportTrackingDTO.getProjectId()
					+ importTrackingLine
					+ importerBaseDirLine;
		}
		
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
