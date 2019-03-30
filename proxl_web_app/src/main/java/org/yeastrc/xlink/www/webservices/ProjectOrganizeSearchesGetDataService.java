package org.yeastrc.xlink.www.webservices;

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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.dto.ProjectDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProjectPageFoldersSearches;
import org.yeastrc.xlink.www.objects.ProjectPageSingleFolder;
import org.yeastrc.xlink.www.objects.SearchDTODetailsDisplayWrapper;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.GetAuthAccessLevelForWebRequest;
import org.yeastrc.xlink.www.web_utils.ViewProjectSearchesInFolders;

@Path("/project")
public class ProjectOrganizeSearchesGetDataService {
	
	private static final Logger log = LoggerFactory.getLogger( ProjectOrganizeSearchesGetDataService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/organizeSearchesGetData")
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
			// Get the session first.  
			HttpSession session = request.getSession();
			UserSessionObject userSessionObject 
			= (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
			if ( userSessionObject == null ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			AuthAccessLevel authAccessLevel = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequestProjectId( userSessionObject, projectId );
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
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
			if ( projectDTO.isProjectLocked() ) {
				webserviceResult.setProjectLocked(true);
				webserviceResult.setStatus(false);
				return webserviceResult;  //  EARLY Return
			} 
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

			//  Get the searches and put them in folders
			ProjectPageFoldersSearches projectPageFoldersSearches = 
					ViewProjectSearchesInFolders.getInstance()
					.getProjectPageFoldersSearches( projectId );
			
			if ( projectPageFoldersSearches.isNoSearchesFound() ) {
				webserviceResult.status = true;
				webserviceResult.noSearchesFound = true;
				return webserviceResult;  //  EARLY Return
			}
			
			//  Convert the projectPageFoldersSearches to the webservice result
			
			List<WebserviceResultSearchItem> searchesNotInFoldersList = 
					convertSearchWrapperToWebserviceResultSearchItem( projectPageFoldersSearches.getSearchesNotInFolders() );

			List<WebserviceResultFolderItem> folderDataList = new ArrayList<>( projectPageFoldersSearches.getFolders().size() );
			for ( ProjectPageSingleFolder projectPageSingleFolder : projectPageFoldersSearches.getFolders() ) {
				List<WebserviceResultSearchItem> searchesInFolderList = 
						convertSearchWrapperToWebserviceResultSearchItem( projectPageSingleFolder.getSearches() );
				WebserviceResultFolderItem folderItem = new WebserviceResultFolderItem();
				folderItem.setSearchesForFolder( searchesInFolderList );
				folderItem.setName( projectPageSingleFolder.getFolderName() );
				folderItem.setId( projectPageSingleFolder.getId() );
				folderDataList.add( folderItem );
			}
			
			// populate webserviceResult
			webserviceResult.setStatus( true );
			webserviceResult.setSearchesNotInFoldersList( searchesNotInFoldersList );
			webserviceResult.setFolderDataList( folderDataList );
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
	
	/**
	 * Convert search wrapper to webservice result objects
	 * @param searches
	 * @return
	 */
	private List<WebserviceResultSearchItem> convertSearchWrapperToWebserviceResultSearchItem( List<SearchDTODetailsDisplayWrapper> searches ) {
		List<WebserviceResultSearchItem> webserviceResultSearchItemList = new ArrayList<>( searches.size() );
		for ( SearchDTODetailsDisplayWrapper searchWrapperItem : searches ) {
			SearchDTO searchDTO = searchWrapperItem.getSearchDTO();
			WebserviceResultSearchItem searchItem = new WebserviceResultSearchItem();
			searchItem.id = searchDTO.getProjectSearchId();
			searchItem.searchId = searchDTO.getSearchId();
			searchItem.name = searchDTO.getName();
			webserviceResultSearchItemList.add( searchItem );
		}
		return webserviceResultSearchItemList;
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
		private boolean projectLocked;
		private boolean noSearchesFound;
		private List<WebserviceResultSearchItem> searchesNotInFoldersList;
		private List<WebserviceResultFolderItem> folderDataList;
		
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
		public boolean isProjectLocked() {
			return projectLocked;
		}
		public void setProjectLocked(boolean projectLocked) {
			this.projectLocked = projectLocked;
		}
		public boolean isNoSearchesFound() {
			return noSearchesFound;
		}
		public void setNoSearchesFound(boolean noSearchesFound) {
			this.noSearchesFound = noSearchesFound;
		}
		public List<WebserviceResultSearchItem> getSearchesNotInFoldersList() {
			return searchesNotInFoldersList;
		}
		public void setSearchesNotInFoldersList(List<WebserviceResultSearchItem> searchesNotInFoldersList) {
			this.searchesNotInFoldersList = searchesNotInFoldersList;
		}
		public List<WebserviceResultFolderItem> getFolderDataList() {
			return folderDataList;
		}
		public void setFolderDataList(List<WebserviceResultFolderItem> folderDataList) {
			this.folderDataList = folderDataList;
		}
	}		
	
	/**
	 * 
	 *
	 */
	public static class WebserviceResultFolderItem {
		private int id;
		private String name;
		private List<WebserviceResultSearchItem> searchesForFolder;
		
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
		public List<WebserviceResultSearchItem> getSearchesForFolder() {
			return searchesForFolder;
		}
		public void setSearchesForFolder(List<WebserviceResultSearchItem> searchesForFolder) {
			this.searchesForFolder = searchesForFolder;
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
