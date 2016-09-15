package org.yeastrc.xlink.www.actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;

public class DeleteSearchAction extends Action {
	
	private static final Logger log = Logger.getLogger(DeleteSearchAction.class);
	

	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
				
		try {

			int searchId = Integer.parseInt( request.getParameter( "searchId" ) );


			// Get the session first.  
//			HttpSession session = request.getSession();


			
			
			//   Get the project id for this search
			
			Collection<Integer> searchIds = new HashSet<>();
			
			searchIds.add( searchId );
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIds );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				
				String msg = "No project ids for search id: " + searchId;
				
				log.error( msg );

				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			if ( projectIdsFromSearchIds.size() > 1 ) {
				
				//  Invalid request, searches across projects

				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			

			int projectId = projectIdsFromSearchIds.get( 0 );
			

			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			if ( ! authAccessLevel.isSearchDeleteAllowed() ) {

				//  No Access Allowed for this project id

				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			


			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );




//			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );
			

			AuthUserDTO authUserDTO = userSessionObject.getUserDBObject().getAuthUser();
		
			
			SearchDTO search = SearchDAO.getInstance().getSearch( searchId );

//			SearchDAO.getInstance().deleteSearch( searchId );
			
			SearchDAO.getInstance().markAsDeleted( searchId, authUserDTO.getId() );
			
			try {
				
				String msg = "Search id " + searchId 
						+ " successfully deleted by user (username: " +  authUserDTO.getUsername() 
						+ ", user id: " + authUserDTO.getId() 
						+ "), IP of request: " + request.getRemoteAddr()
						+ ", Search path: " + search.getPath()
						+ ", Search name: " + search.getName();

				log.warn( msg );
				
			} catch ( Exception e ) {
				
				log.warn( "Error logging delete search message", e );
			}

			ActionForward actionForward =  mapping.findForward( "Success" );
			
			String actionPath = actionForward.getPath();
			
			String redirectURL = CurrentContext.getCurrentWebAppContext() + actionPath 
					+ "?" + WebConstants.PARAMETER_PROJECT_ID + "=" + projectId;
			
			response.sendRedirect( redirectURL );
			 
			return null;  // nothing to forward to since setting redirect here
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}
}
