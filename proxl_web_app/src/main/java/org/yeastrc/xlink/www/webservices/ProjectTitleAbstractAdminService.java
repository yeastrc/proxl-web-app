package org.yeastrc.xlink.www.webservices;

import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.objects.ProjectTitleAbstractAdminResult;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ProjectTblSubPartsForProjectLists;
//import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.user_web_utils.TruncateProjectTitleForDisplay;

@Path("/project")
public class ProjectTitleAbstractAdminService {

	private static final Logger log = LoggerFactory.getLogger( ProjectTitleAbstractAdminService.class);
	
	/**
	 * @param projectId
	 * @param title
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updateTitle")
	public ProjectTitleAbstractAdminResult updateTitle( 
			@FormParam("projectId") int projectId, 
			@FormParam("title") String title, 
			@Context HttpServletRequest request ) throws Exception {
		ProjectTitleAbstractAdminResult projectTitleAbstractAdminResult = new ProjectTitleAbstractAdminResult();
		try {
			if ( projectId == 0 ) {
				String msg = "Provided projectId is zero, is = " + projectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(jakarta.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( StringUtils.isEmpty(title) ) {
				String msg = "Provided title is empty, is = " + title;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(jakarta.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			ProjectDAO projectDAO = ProjectDAO.getInstance();
			Integer authShareableObjectId = projectDAO.getAuthShareableObjectIdForProjectId( projectId );
			if ( authShareableObjectId == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			projectDAO.updateTitle(projectId, title);
			Cached_ProjectTblSubPartsForProjectLists.getInstance().invalidateProjectId( projectId );
			ProjectDTO projectDTO = projectDAO.getProjectDTOForProjectId( projectId );
			projectTitleAbstractAdminResult.setStatus(true);
			projectTitleAbstractAdminResult.setTitle( projectDTO.getTitle() );
			String titleHeaderDisplay = TruncateProjectTitleForDisplay.truncateProjectTitleForHeader( projectDTO.getTitle() );
			projectTitleAbstractAdminResult.setTitleHeaderDisplay( titleHeaderDisplay );
			return projectTitleAbstractAdminResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	/**
	 * @param projectId
	 * @param abstractText
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updateAbstract")
	public ProjectTitleAbstractAdminResult updateAbstract( 
			@FormParam("projectId") int projectId, 
			@FormParam("abstractText") String abstractText, 
			@Context HttpServletRequest request ) throws Exception {
		ProjectTitleAbstractAdminResult projectTitleAbstractAdminResult = new ProjectTitleAbstractAdminResult();
		try {
			if ( projectId == 0 ) {
				String msg = "Provided projectId is zero, is = " + projectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(jakarta.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			//  allow abstractText to be empty
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			ProjectDAO projectDAO = ProjectDAO.getInstance();
			Integer authShareableObjectId = projectDAO.getAuthShareableObjectIdForProjectId( projectId );
			if ( authShareableObjectId == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			projectDAO.updateAbstract(projectId, abstractText);
			Cached_ProjectTblSubPartsForProjectLists.getInstance().invalidateProjectId( projectId );
			ProjectDTO projectDTO = projectDAO.getProjectDTOForProjectId( projectId );
			projectTitleAbstractAdminResult.setStatus(true);
			projectTitleAbstractAdminResult.setAbstractText( projectDTO.getAbstractText() );
			return projectTitleAbstractAdminResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
