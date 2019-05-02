package org.yeastrc.xlink.www.actions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProjectPageFoldersSearches;
import org.yeastrc.xlink.www.objects.ProjectPublicAccessData;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.searchers.ProxlXMLFileImportTracking_PendingCount_Searcher;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.IsProxlXMLFileImportFullyConfigured;
import org.yeastrc.xlink.www.searcher.NoteSearcher;
import org.yeastrc.xlink.www.searcher.ProjectToCopyToSearcher;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetProjectPublicAccessData;
import org.yeastrc.xlink.www.web_utils.ViewProjectSearchesInFolders;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.AuthAccessLevelConstants;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.dto.NoteDTO;
import org.yeastrc.xlink.www.dto.ProjectDTO;

/**
 * 
 * 
 */
public class ViewProjectAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( ViewProjectAction.class);
	
	//  Struts Config forward to projectNotFound.jsp
	public final static String PROJECT_NOT_FOUND = "ProjectNotFound";
		
	//  For use in projectNotFound.jsp
	private static final String REQUEST_PROJECT_ID_FROM_VIEW_PROJECT_ACTION = "projectId_FromViewProjectAction";
	//  For use in projectNotFound.jsp
	private static final String REQUEST_ADMIN_EMAIL_ADDRESS = "adminEmailAddress";
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			//  First try to get project id from request as passed from another struts action
			String projectIdString = null;
			int projectId = 0;
			Integer projectIdInteger = (Integer)request.getAttribute( WebConstants.REQUEST_PROJECT_ID );
			if ( projectIdInteger != null ) {
				projectId = projectIdInteger;
				projectIdString = projectIdInteger.toString();

				request.setAttribute( REQUEST_PROJECT_ID_FROM_VIEW_PROJECT_ACTION, projectIdString );
				
			} else {
				//  get project id from query string
				projectIdString = request.getParameter( WebConstants.PARAMETER_PROJECT_ID );
				
				request.setAttribute( REQUEST_PROJECT_ID_FROM_VIEW_PROJECT_ACTION, projectIdString );
				
				try {
					projectId = Integer.parseInt( projectIdString );
				} catch ( Exception ex ) {
					log.warn( "Failed to parse project id: " + projectIdString );
					this.getDataForProjectNotFoundPage(request); 
					return mapping.findForward( PROJECT_NOT_FOUND );
				}
			}
			{
				//  Confirm projectId is in database and is not marked for deletion and is enabled
				// !!!  Only populates properties projectLocked, publicAccessLevel, public_access_locked, enabled, markedForDeletion,
				ProjectDTO projectDTO_Partial =	ProjectDAO.getInstance().getProjectLockedPublicAccessLevelPublicAccessLockedForProjectId( projectId );
				if ( projectDTO_Partial == null ) {
					String msg = "Project id is not in database: " + projectId;
					log.warn( msg );
					this.getDataForProjectNotFoundPage(request);
					return mapping.findForward( PROJECT_NOT_FOUND );
				}
				if ( ( ! projectDTO_Partial.isEnabled() ) || projectDTO_Partial.isMarkedForDeletion() ) {
					String msg = "Project is not enabled or is marked for deletion for id: " + projectId;
					log.warn( msg );
					this.getDataForProjectNotFoundPage(request);
					return mapping.findForward( PROJECT_NOT_FOUND );
				}
			}
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
				}
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );

			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			
			request.setAttribute( "project_id", projectId );
			
			ProjectDAO projectDAO = ProjectDAO.getInstance();
			ProjectDTO projectDTO = projectDAO.getProjectDTOForProjectId( projectId );
			
			if ( projectDTO == null || ( ! projectDTO.isEnabled() ) || ( projectDTO.isMarkedForDeletion() )  ) {
				this.getDataForProjectNotFoundPage(request);
				return mapping.findForward( PROJECT_NOT_FOUND );
			}
			
			ProjectPublicAccessData projectPublicAccessData =
					GetProjectPublicAccessData.getInstance().getProjectPublicAccessData( projectId );
			if ( projectPublicAccessData == null ) {
				this.getDataForProjectNotFoundPage(request);
				return mapping.findForward( PROJECT_NOT_FOUND );
			}
			
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );
			
			boolean showStructureLink = true;
			if ( authAccessLevel.isAssistantProjectOwnerAllowed()
					|| authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
			} else {
				//  Public access user:
				showStructureLink = AnyPDBFilesForProjectId.getInstance().anyPDBFilesForProjectId( projectId );
			}
			request.setAttribute( WebConstants.REQUEST_SHOW_STRUCTURE_LINK, showStructureLink );
			
			List<NoteDTO> notes = NoteSearcher.getInstance().getSearchsForProjectId( projectId );
			
			//  Get the searches and put them in folders
			ProjectPageFoldersSearches projectPageFoldersSearches = 
					ViewProjectSearchesInFolders.getInstance()
					.getProjectPageFoldersSearches( projectId );
			
			//  If user is Researcher or better and Proxl XML File Import is Fully Configured, 
			//  get submitted Proxl XML files
			if ( authAccessLevel.isAssistantProjectOwnerAllowed() 
					&& IsProxlXMLFileImportFullyConfigured.getInstance().isProxlXMLFileImportFullyConfigured() ) {
				int pendingCount = 
						ProxlXMLFileImportTracking_PendingCount_Searcher.getInstance().getPendingCountForProject( projectId );
				request.setAttribute( "proxlXMLFileImportTrackingPendingCount", pendingCount );
			}
			
			request.setAttribute( "project", projectDTO );
			request.setAttribute( "projectPublicAccessData", projectPublicAccessData );
			request.setAttribute( "notes", notes );
			request.setAttribute( "projectPageFoldersSearches", projectPageFoldersSearches );
						
			//  If user is project owner or better, determine if there are other projects this user has rights to
			if ( authAccessLevel.isProjectOwnerAllowed() ) {
				
				boolean otherProjectsExistForUser = false;
				if ( authAccessLevel.isAdminAllowed() ) {
					// Do other projects exist
					otherProjectsExistForUser = 
							ProjectToCopyToSearcher.getInstance().anyProjectsExistExcludingProjectId( projectId );
				} else {
					//  Do other projects exist that this user is owner for
					int authUserId = userSession.getAuthUserId();
					int maxAuthLevel = AuthAccessLevelConstants.ACCESS_LEVEL_PROJECT_OWNER;
					otherProjectsExistForUser = 
							ProjectToCopyToSearcher.getInstance()
							.anyProjectsExistForAuthUserExcludingProjectId( authUserId, maxAuthLevel, projectId );
				}
				request.setAttribute( "otherProjectsExistForUser", otherProjectsExistForUser );
			}
			
			ActionForward actionForward =  mapping.findForward( "Success" );
			return actionForward;
			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	/**
	 * Set up request attributes for projectNotFound.jsp
	 * @param request
	 * @throws Exception 
	 */
	private void getDataForProjectNotFoundPage(HttpServletRequest request) throws Exception {
		
		String adminEmailAddress =
				ConfigSystemCaching.getInstance()
				.getConfigValueForConfigKey( ConfigSystemsKeysConstants.ADMIN_EMAIL_ADDRESS_KEY );
		request.setAttribute( REQUEST_ADMIN_EMAIL_ADDRESS, adminEmailAddress );
	}
	

}
