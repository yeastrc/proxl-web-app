package org.yeastrc.xlink.www.user_account_webservices;


import javax.servlet.http.HttpServletRequest;
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
public class UpdateUserGlobalAccessService {

	private static final Logger log = LoggerFactory.getLogger( UpdateUserGlobalAccessService.class);
	
	
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updateGlobalAccess") 
	public UpdateUserAccessToProjectResult userId(   @FormParam( "personId" ) String personIdString,
			@FormParam( "personAccessLevel" ) String personAccessLevelString,
			@Context HttpServletRequest request )
	throws Exception {
		
		//  Restricted to users with ACCESS_LEVEL_ADMIN
		

		int personId = 0;
		Integer personAccessLevel = null;
		
		
		if ( StringUtils.isEmpty( personIdString ) ) {

			log.warn( "UpdateUserGlobalAccessService:  personId empty: " + personIdString );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		try {
			
			personId = Integer.parseInt( personIdString );
			
		} catch (Exception e) {

			log.warn( "UpdateUserGlobalAccessService:  personId is not valid integer: " + personIdString, e );

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}
		

		if ( StringUtils.isEmpty( personAccessLevelString ) ) {

			//  Is empty for Project level access
			
//			log.warn( "UpdateUserGlobalAccessService:  PersonAccessLevel empty: " + personAccessLevelString );
//
//			throw new WebApplicationException(
//					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
//					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
//					.build()
//					);
		} else {

			try {

				personAccessLevel = Integer.parseInt( personAccessLevelString );

			} catch (Exception e) {

				log.warn( "UpdateUserGlobalAccessService:  PersonAccessLevel is not valid integer: " + personAccessLevelString, e );

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
			
			Integer idFromDB = authUserDAO.getIdForId(personId);
			
			if ( idFromDB == null ) {
				
				log.warn( "UpdateUserGlobalAccessService:  personId is not in database: " + personId );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			AuthUserDAO.getInstance().updateUserAccessLevel( personId, personAccessLevel );

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
