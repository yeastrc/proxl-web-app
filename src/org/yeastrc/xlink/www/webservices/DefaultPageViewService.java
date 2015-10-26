package org.yeastrc.xlink.www.webservices;


import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.default_page_view.DefaultPageViewSaveOrUpdate;
import org.yeastrc.xlink.www.default_page_view.GetDefaultURLFromPageURL;
import org.yeastrc.xlink.www.dto.DefaultPageViewDTO;
import org.yeastrc.xlink.www.objects.GenericWebserviceResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/defaultPageView")
public class DefaultPageViewService {

	private static final Logger log = Logger.getLogger(DefaultPageViewService.class);
	
	
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/saveOrUpdateDefaultPageView")
	public GenericWebserviceResult saveOrUpdateDefaultPageView( 
			@FormParam("searchId") int searchId, 
			@FormParam("pageName") String pageName, 
			@FormParam("pageUrl") String pageUrl, 
			@Context HttpServletRequest request ) throws Exception {


		GenericWebserviceResult genericWebserviceResult = new GenericWebserviceResult();

		try {

			if ( searchId == 0 ) {

				String msg = "Provided searchId is zero, is = " + searchId;

				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
//			if ( StringUtils.isEmpty( pageName ) ) {
//
//				String msg = "pageName is empty";
//
//				log.error( msg );
//
//			    throw new WebApplicationException(
//			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//			    	        .entity( msg )
//			    	        .build()
//			    	        );
//			}
			if ( StringUtils.isEmpty( pageUrl ) ) {

				String msg = "pageUrl is empty";

				log.error( msg );

			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			

			// Get the session first.  
			HttpSession session = request.getSession();

			
			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );

			searchIdsCollection.add( searchId );
			
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				
				String msg = "No project ids for search id: " + searchId;
				
				log.error( msg );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			if ( projectIdsFromSearchIds.size() > 1 ) {
				
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			int projectId = projectIdsFromSearchIds.get( 0 );
			

			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {

				//  No Access Allowed for this project id

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			

			
			
			String defaultURLFromPageURLString = GetDefaultURLFromPageURL.getInstance().getDefaultURLFromPageURL( pageUrl );
			
			if ( StringUtils.isEmpty(pageName)) {
				
				pageName = GetDefaultURLFromPageURL.getInstance().getPageNameFromStrutsActionInURL( pageUrl );
			}
			
			DefaultPageViewDTO defaultPageViewDTO = new DefaultPageViewDTO();
			
			defaultPageViewDTO.setPageName( pageName );
			defaultPageViewDTO.setUrl( defaultURLFromPageURLString );
			defaultPageViewDTO.setSearchId( searchId );
			defaultPageViewDTO.setAuthUserId( userSessionObject.getUserDBObject().getAuthUser().getId() );
			
			DefaultPageViewSaveOrUpdate.getInstance().defaultPageViewSaveOrUpdate( defaultPageViewDTO );
			
			genericWebserviceResult.setStatus(true);

			return genericWebserviceResult;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}

	}
	
	
	

	
	
}
