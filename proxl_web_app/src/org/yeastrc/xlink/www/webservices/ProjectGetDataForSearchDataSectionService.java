package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.searcher.SearchSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 * Get List of searches for Updating Search List in Search Data Section
 * 
 *  Used by searchesForPageChooser.js
 *
 */
@Path("/project")
public class ProjectGetDataForSearchDataSectionService {
	
	private static final Logger log = Logger.getLogger(ProjectGetDataForSearchDataSectionService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getSearchDataList")
	public WebserviceResult projectOrganizeSearchesGetData( 
			@QueryParam( "project_id" ) Integer projectId,
			@Context HttpServletRequest request ) throws Exception {
		try {
			if ( projectId == null ) {
				String msg = "Provided project_id is null";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( projectId == 0 ) {
				String msg = "Provided project_id is 0, is = " + projectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
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
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////

			ProjectDAO projectDAO = ProjectDAO.getInstance();
			ProjectDTO projectDTO = projectDAO.getProjectDTOForProjectId( projectId );
			if ( projectDTO == null ) {
				log.warn( "projectId is not in database: " + projectId );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			WebserviceResult webserviceResult = new WebserviceResult();
			if ( projectDTO.isMarkedForDeletion() ) {
				webserviceResult.setProjectMarkedForDeletion(true);
				webserviceResult.setStatus(false);
				return webserviceResult;  //  EARLY Return
			} 
			if ( ! projectDTO.isEnabled() ) {
				webserviceResult.setProjectDisabled(true);
				webserviceResult.setStatus(false);
				return webserviceResult;  //  EARLY Return
			}
			//  Get data to return

			List<SearchDTO> searches = SearchSearcher.getInstance().getSearchsForProjectId( projectId );

			if ( searches.isEmpty() ) {
				webserviceResult.status = true;
				webserviceResult.noSearchesFound = true;
				return webserviceResult;  //  EARLY Return
			}
			
			// Sort searches list
			Collections.sort( searches, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getSearchId() - o2.getSearchId();
				}
			});
			
			List<WebserviceResultSearchItem> searchesList = new ArrayList<>( searches.size() );
			for ( SearchDTO searchDTO : searches ) {
				WebserviceResultSearchItem searchItem = new WebserviceResultSearchItem();
				searchItem.id = searchDTO.getProjectSearchId();
				searchItem.searchId = searchDTO.getSearchId();
				searchItem.name = searchDTO.getName();
				searchesList.add( searchItem );
			}
			
			// populate webserviceResult
			webserviceResult.setStatus( true );
			webserviceResult.setSearchesList( searchesList );
			return webserviceResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	//////////////////////
	//  Classes for web service request and response
	
	/**
	 * Webservice Result Class
	 *
	 */
	public static class WebserviceResult {
		private boolean status;
		private boolean projectMarkedForDeletion;
		private boolean projectDisabled;
		private boolean noSearchesFound;
		private List<WebserviceResultSearchItem> searchesList;
		
		public boolean isStatus() {
			return status;
		}
		public void setStatus(boolean status) {
			this.status = status;
		}
		public boolean isProjectMarkedForDeletion() {
			return projectMarkedForDeletion;
		}
		public void setProjectMarkedForDeletion(boolean projectMarkedForDeletion) {
			this.projectMarkedForDeletion = projectMarkedForDeletion;
		}
		public boolean isProjectDisabled() {
			return projectDisabled;
		}
		public void setProjectDisabled(boolean projectDisabled) {
			this.projectDisabled = projectDisabled;
		}
		public boolean isNoSearchesFound() {
			return noSearchesFound;
		}
		public void setNoSearchesFound(boolean noSearchesFound) {
			this.noSearchesFound = noSearchesFound;
		}
		public List<WebserviceResultSearchItem> getSearchesList() {
			return searchesList;
		}
		public void setSearchesList(List<WebserviceResultSearchItem> searchesList) {
			this.searchesList = searchesList;
		}
	}		
	
	/**
	 * 
	 *
	 */
	public static class WebserviceResultSearchItem {
		private int id;
		private int searchId;
		private String name;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getSearchId() {
			return searchId;
		}
		public void setSearchId(int searchId) {
			this.searchId = searchId;
		}
	}
}
