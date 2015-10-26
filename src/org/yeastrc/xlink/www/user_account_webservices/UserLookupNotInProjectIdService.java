package org.yeastrc.xlink.www.user_account_webservices;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.searcher.UserSearcherForSearchStringNotSharedObjectIdNotUserDisabled;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.UserQueryResult;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;



@Path("/user")
public class UserLookupNotInProjectIdService {

	private static final Logger log = Logger.getLogger(UserLookupNotInProjectIdService.class);
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/lookupLastNameNotInProjectId") 
	public UserQueryResult lookupLastNameNotInProjectId( @QueryParam( "query" ) String query,
										  @QueryParam( "projectId" ) Integer projectId,
										  @Context HttpServletRequest request )
	throws Exception {
		
		//  Restricted to users with ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER or better
		
		
		
		try {

			// Get the session first.  
			HttpSession session = request.getSession();



			if ( projectId == null ) {

				//  No Project Id 
				
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);

			} 
			
			


			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			//  Test access to the project id

			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			
			Integer notSharedObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			
			if ( notSharedObjectId == null ) {

				log.warn( "UserLookupNotInProjectIdService:  projectId is not in database: " + projectId );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			List<Integer> userIds = 
					UserSearcherForSearchStringNotSharedObjectIdNotUserDisabled.getInstance().
						getAuthUserIdListForLastNameAndNotSharedObjectIdNotUserDisabled( query, notSharedObjectId );

			XLinkUserDAO xLinkUserDAO = XLinkUserDAO.getInstance();
			
			List<XLinkUserDTO> queryResultList = new ArrayList<XLinkUserDTO>( userIds.size() );
					
			for ( int authUserId : userIds ) {
			
				XLinkUserDTO xLinkUserDTO = xLinkUserDAO.getXLinkUserDTOForAuthUserId( authUserId );
				
				queryResultList.add(xLinkUserDTO);
			}
			
			UserQueryResult userQueryResult = new UserQueryResult();
			
			userQueryResult.setQueryResultList( queryResultList );
			
			return userQueryResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}
	
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/lookupEmailNotInProjectId") 
	public UserQueryResult lookupEmailNotInProjectId( @QueryParam( "query" ) String query,
										  @QueryParam( "projectId" ) Integer projectId,
										  @Context HttpServletRequest request )
	throws Exception {
		
		//  Restricted to users with ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER or better
		
		
		
		try {

			// Get the session first.  
			HttpSession session = request.getSession();

			if ( projectId == null ) {

				//  No Project Id 
				
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);

			} 
			
			


			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			
			//  Test access to the project id

			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			
			Integer notSharedObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			
			if ( notSharedObjectId == null ) {

				log.warn( "UserLookupNotInProjectIdService:  projectId is not in database: " + projectId );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			List<Integer> userIds = 
					UserSearcherForSearchStringNotSharedObjectIdNotUserDisabled.getInstance().
							getAuthUserIdListForEmailAndNotSharedObjectIdNotUserDisabled( query, notSharedObjectId );

			XLinkUserDAO xLinkUserDAO = XLinkUserDAO.getInstance();
			
			List<XLinkUserDTO> queryResultList = new ArrayList<XLinkUserDTO>( userIds.size() );
					
			for ( int authUserId : userIds ) {
			
				XLinkUserDTO xLinkUserDTO = xLinkUserDAO.getXLinkUserDTOForAuthUserId( authUserId );
				
				queryResultList.add(xLinkUserDTO);
			}
			
			UserQueryResult userQueryResult = new UserQueryResult();
			
			userQueryResult.setQueryResultList( queryResultList );
			
			return userQueryResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}
	
	
	
	//////////////////////////////////////////////
	
	///////    !!!!!!!!!!!!!! OLD
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/lookupNotInProjectId") 
	public UserQueryResult getViewerData( @QueryParam( "query" ) String query,
										  @QueryParam( "projectId" ) Integer projectId,
										  @Context HttpServletRequest request )
	throws Exception {
		
		//  Restricted to users with ACCESS_LEVEL_ASSISTANT_PROJECT_OWNER_AKA_RESEARCHER or better
		
		
		
		try {

			// Get the session first.  
			HttpSession session = request.getSession();


			if ( projectId == null ) {

				//  No Project Id 
				
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);

			} 
			
			


			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			
			//  Test access to the project id

			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			
			Integer notSharedObjectId = ProjectDAO.getInstance().getAuthShareableObjectIdForProjectId( projectId );
			
			if ( notSharedObjectId == null ) {

				log.warn( "UserLookupNotInProjectIdService:  projectId is not in database: " + projectId );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			List<Integer> userIds = 
					UserSearcherForSearchStringNotSharedObjectIdNotUserDisabled.getInstance().
						getAuthUserIdListForQueryAndNotSharedObjectIdNotUserDisabled( query, notSharedObjectId );

			XLinkUserDAO xLinkUserDAO = XLinkUserDAO.getInstance();
			
			List<XLinkUserDTO> queryResultList = new ArrayList<XLinkUserDTO>( userIds.size() );
					
			for ( int authUserId : userIds ) {
			
				XLinkUserDTO xLinkUserDTO = xLinkUserDAO.getXLinkUserDTOForAuthUserId( authUserId );
				
				queryResultList.add(xLinkUserDTO);
			}
			
			UserQueryResult userQueryResult = new UserQueryResult();
			
			userQueryResult.setQueryResultList( queryResultList );
			
			return userQueryResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}

}
