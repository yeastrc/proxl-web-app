package org.yeastrc.xlink.www.user_account_webservices;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthUserInviteTrackingDAO;
import org.yeastrc.auth.dto.AuthUserInviteTrackingDTO;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.UserInviteUpdateAccessLevelResult;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.user_web_utils.ValidateUserAccessLevel;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;



@Path("/user")
public class UserInviteUpdateService {

	private static final Logger log = LoggerFactory.getLogger( UserInviteUpdateService.class);
	
	/**
	 * 
	 *
	 */
	public static class WebserviceRequest {
		
		private String inviteId;
		private String personAccessLevel;
		private String projectId;
		public void setInviteId(String inviteId) {
			this.inviteId = inviteId;
		}
		public void setPersonAccessLevel(String personAccessLevel) {
			this.personAccessLevel = personAccessLevel;
		}
		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}
		
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updateInviteAccessLevel") 
	
	public UserInviteUpdateAccessLevelResult updateInviteAccessLevel(   
			byte[] requestJSONBytes,
			@Context HttpServletRequest request )
	throws Exception {

		if ( requestJSONBytes == null || requestJSONBytes.length == 0 ) {
			String msg = "requestJSONBytes is null or requestJSONBytes is empty";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity(  )
		    	        .build()
		    	        );
		}
		WebserviceRequest webserviceRequest = 
				Unmarshal_RestRequest_JSON_ToObject.getInstance()
				.getObjectFromJSONByteArray(requestJSONBytes, WebserviceRequest.class );
		
		String inviteIdString = webserviceRequest.inviteId;
		String personAccessLevelString = webserviceRequest.personAccessLevel;
		String projectIdString = webserviceRequest.projectId;
		
		UserInviteUpdateAccessLevelResult userInviteUpdateAccessLevelResult = new UserInviteUpdateAccessLevelResult();

		

		int inviteId = 0;
		int personAccessLevel = 0;
		Integer projectId = null;
		
		
		if ( inviteIdString != null ) {
			
			inviteIdString = inviteIdString.trim();
		}		
		if ( personAccessLevelString != null ) {
			
			personAccessLevelString = personAccessLevelString.trim();
		}		
		if ( projectIdString != null ) {
			
			projectIdString = projectIdString.trim();
		}		

		if ( StringUtils.isEmpty( inviteIdString ) ) {

			log.warn( "UserInviteUpdateService:  inviteId empty: " + inviteIdString );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		try {
			
			inviteId = Integer.parseInt( inviteIdString );
			
		} catch (Exception e) {

			log.warn( "UserInviteUpdateService:  inviteId is not valid integer: " + inviteIdString, e );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		if ( StringUtils.isEmpty( personAccessLevelString ) ) {

			log.warn( "UserInviteUpdateService:  personAccessLevel empty: " + personAccessLevelString );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}

		try {
			
			personAccessLevel = Integer.parseInt( personAccessLevelString );
			
		} catch (Exception e) {

			log.warn( "UserInviteUpdateService:  invitedPersonAccessLevel is not valid integer: " + personAccessLevelString, e );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		if ( StringUtils.isNotEmpty( projectIdString ) ) {

			try {

				projectId = Integer.parseInt( projectIdString );

			} catch (Exception e) {

				log.warn( "UserInviteUpdateService:  projectId is not valid integer: " + projectIdString, e );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
		}		

		
		
		
//		if (true)
//		throw new Exception("Forced Error");
		
		try {
			//  Test for Admin level 

			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionNoProjectId( request );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			
			
			

			
			
			Integer projectAuthShareableObjectId = null;

			//  Test access at global level

			WebSessionAuthAccessLevel authAccessLevelGlobal = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();

			if ( authAccessLevelGlobal.isAdminAllowed() ) {

				// User is Admin so always allow
				
				if ( projectId != null ) {
				
					Integer projectAuthShareableObjectIdFromDB = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );

					projectAuthShareableObjectId = projectAuthShareableObjectIdFromDB;
				}
				
			} else {

				if ( projectId == null ) {

					//  Admin level is required for invite without project id

					if ( ! authAccessLevelGlobal.isAdminAllowed() ) {

						//  No Access Allowed for this user

						throw new WebApplicationException(
								Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
								.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
								.build()
								);
					}

				} else {

					//  Test access to the project id

					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResultProjectLevel =
							GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );

					WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResultProjectLevel.getWebSessionAuthAccessLevel();

					if ( ! authAccessLevel.isProjectOwnerAllowed() ) {

						//  Access only allows to Project Owner.

						//     Assistant Project Owner (aka researcher) not allowed 
						//          since cannot increase researcher level or decrease owner level  

						//  No Access Allowed for this project id

						throw new WebApplicationException(
								Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
								.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
								.build()
								);
					}

					Integer projectAuthShareableObjectIdFromDB = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );

					if ( projectAuthShareableObjectIdFromDB == null ) {

						log.warn( "UserInviteService:  projectId is not in database: " + projectId );

						throw new WebApplicationException(
								Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
								.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
								.build()
								);
					}

					projectAuthShareableObjectId = projectAuthShareableObjectIdFromDB;

				}
			}
			
			ValidateUserAccessLevel.UserAccessLevelTypes userAccessLevelType = ValidateUserAccessLevel.UserAccessLevelTypes.GLOBAL;
			
			if ( projectAuthShareableObjectId != null ) {
				
				userAccessLevelType = ValidateUserAccessLevel.UserAccessLevelTypes.PROJECT;
			}
			
			if ( ! ValidateUserAccessLevel.validateUserAccessLevel( personAccessLevel, userAccessLevelType ) ) {
				
				//  The personAccessLevel is not valid

				log.warn( "UserInviteService:  personAccessLevel is not valid: " + personAccessLevel );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			
			AuthUserInviteTrackingDAO authUserInviteTrackingDAO = AuthUserInviteTrackingDAO.getInstance();
			
			
			AuthUserInviteTrackingDTO authUserInviteTrackingDTO = authUserInviteTrackingDAO.getForInviteTrackingId(inviteId);
			
			
			if ( authUserInviteTrackingDTO.isInviteUsed() ) {
				
				log.warn( "UserInviteUpdateService:  isInviteUsed is true:  inviteId: " + inviteIdString );

				userInviteUpdateAccessLevelResult.setInviteAlreadyUsed(true);
				userInviteUpdateAccessLevelResult.setStatus(false);
				
				return userInviteUpdateAccessLevelResult;  ///  !!!!!  EARLY RETURN
			}
			
			
			if ( authUserInviteTrackingDTO.isCodeReplacedByNewer() ) {
				
				log.warn( "UserInviteUpdateService:  isCodeReplacedByNewer is true:  inviteId: " + inviteIdString );

				userInviteUpdateAccessLevelResult.setInviteNoLongerValid(true);
				userInviteUpdateAccessLevelResult.setStatus(false);
				
				return userInviteUpdateAccessLevelResult;  ///  !!!!!  EARLY RETURN
			}

			if ( authUserInviteTrackingDTO.isInviteRevoked() ) {
				
				log.warn( "UserInviteUpdateService:  isInviteRevoked is true:  inviteId: " + inviteIdString );


				userInviteUpdateAccessLevelResult.setInviteNoLongerValid(true);
				userInviteUpdateAccessLevelResult.setStatus(false);
				
				return userInviteUpdateAccessLevelResult;  ///  !!!!!  EARLY RETURN
			}
			
			///  Ensure the SharedObjectId for the project id passed in 
			///    matches the SharedObjectId on the invite
			
			Integer invitedSharedObjectId = authUserInviteTrackingDTO.getInvitedSharedObjectId();
			
			if ( ( invitedSharedObjectId == null && projectAuthShareableObjectId == null )
					|| invitedSharedObjectId.equals(invitedSharedObjectId) ) {
				
				//  The invite matches
				
			} else {
				
				log.warn( "UserInviteUpdateService:  invitedSharedObjectId not match projectAuthShareableObjectId: "
						+ "inviteId: " + inviteIdString );

				userInviteUpdateAccessLevelResult.setProjectIdIncorrectForThisInviteId(true);
				userInviteUpdateAccessLevelResult.setStatus(false);
				
				return userInviteUpdateAccessLevelResult;  ///  !!!!!  EARLY RETURN
			}
			
			
			//  Ensure that if a project Id was passed in for access control, 
			//   that it matches the SharedObjectId on the invite
			
//			Integer invitedSharedObjectId = authUserInviteTrackingDTO.getInvitedSharedObjectId();
//			
//			if ( ( invitedSharedObjectId == null && projectAuthShareableObjectId == null )
//					|| ( projectAuthShareableObjectId != null 
//							&& invitedSharedObjectId.equals(invitedSharedObjectId) ) ) {
//				
//				//  The invite matches or projectAuthShareableObjectId is null
//				
//			} else {
//				
//				log.warn( "UserInviteUpdateService:  invitedSharedObjectId not match projectAuthShareableObjectId: "
//						+ "inviteId: " + inviteIdString );
//
//				userInviteUpdateAccessLevelResult.setProjectIdIncorrectForThisInviteId(true);
//				userInviteUpdateAccessLevelResult.setStatus(false);
//				
//				return userInviteUpdateAccessLevelResult;  ///  !!!!!  EARLY RETURN
//			}
			
			
			
			authUserInviteTrackingDAO.updateInvitedUserAccessLevelFields( inviteId, personAccessLevel );
			
			
			userInviteUpdateAccessLevelResult.setStatus(true);
			
			return userInviteUpdateAccessLevelResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
				
	}
	

}
