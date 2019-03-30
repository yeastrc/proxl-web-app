package org.yeastrc.xlink.www.user_account_webservices;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.UpdateUserAccessToProjectResult;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;



@Path("/user")
public class UpdateUserEnabledFlagService {

	private static final Logger log = LoggerFactory.getLogger( UpdateUserEnabledFlagService.class);
	
	
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updateEnabledFlag") 
	public UpdateUserAccessToProjectResult userId(   @FormParam( "personId" ) String personIdString,
			@FormParam( "personEnabledFlag" ) String personEnabledFlagString,
			@Context HttpServletRequest request )
	throws Exception {
		
		//  Restricted to users with ACCESS_LEVEL_ADMIN
		

		int personId = 0;
		boolean personEnabledFlag = false;
		
		
		if ( StringUtils.isEmpty( personIdString ) ) {

			log.warn( "UpdateUserEnabledFlagService:  personId empty: " + personIdString );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		try {
			
			personId = Integer.parseInt( personIdString );
			
		} catch (Exception e) {

			log.warn( "UpdateUserEnabledFlagService:  personId is not valid integer: " + personIdString, e );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		if ( StringUtils.isEmpty( personEnabledFlagString ) ) {
			
			log.warn( "UpdateUserEnabledFlagService:  PersonEnabledFlag empty: " + personEnabledFlagString );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		} else {

			try {

				personEnabledFlag = Boolean.parseBoolean( personEnabledFlagString );

			} catch (Exception e) {

				log.warn( "UpdateUserEnabledFlagService:  PersonEnabledFlag is not valid boolean: " + personEnabledFlagString, e );

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

			// Get the session first.  
//			HttpSession session = request.getSession();



			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			
			

			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			
			if ( userSessionObject.getUserDBObject().getAuthUser().getId() == personId ) {

				//  Not allowed to update own access
				
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			//  Test access at global level


			if ( ! authAccessLevel.isAdminAllowed() ) {

				//  No Access Allowed 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			AuthUserDAO authUserDAO = AuthUserDAO.getInstance();
			
			Integer authUserIdFromDB = authUserDAO.getIdForId(personId);
			
			if ( authUserIdFromDB == null ) {
				log.warn( "UpdateUserEnabledFlagService:  personId is not in database: " + personId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			AuthUserDAO.getInstance().updateEnabledAppSpecific( personId, personEnabledFlag );

			UpdateUserAccessToProjectResult updateUserAccessToProjectResult = new UpdateUserAccessToProjectResult();
			
			updateUserAccessToProjectResult.setStatus(true);
			
			return updateUserAccessToProjectResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
				
	}
	

}
