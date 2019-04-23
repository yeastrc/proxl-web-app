package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.searcher.SavedViewListForProjectIdSearcher;
import org.yeastrc.xlink.www.searcher_results.SavedViewListForProjectIdItem;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;


@Path("/savedView")
public class SavedView_ListSaveViews_Service {
	
	private static final Logger log = LoggerFactory.getLogger( SavedView_ListSaveViews_Service.class);
	
	public static class WebserviceRequest {
		Integer projectId;

		public void setProjectId(Integer projectId) {
			this.projectId = projectId;
		} 
	}
	

    public static class WebserviceResult {
    	List<SavedViewItem> savedViewList;
		public List<SavedViewItem> getSavedViewList() {
			return savedViewList;
		}
    }
    
    public static class SavedViewItem {
    	
    	private int id;
    	private String label;
    	private String url;
    	private boolean canEdit;
    	private boolean canDelete;
    	
		public int getId() {
			return id;
		}
		public String getLabel() {
			return label;
		}
		public String getUrl() {
			return url;
		}
		public boolean isCanEdit() {
			return canEdit;
		}
		public boolean isCanDelete() {
			return canDelete;
		}
		public void setId(int id) {
			this.id = id;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public void setCanEdit(boolean canEdit) {
			this.canEdit = canEdit;
		}
		public void setCanDelete(boolean canDelete) {
			this.canDelete = canDelete;
		}
    }
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listSavedViews")
	public WebserviceResult webserviceMethod( 
			byte[] requestJSONBytes,
			@Context HttpServletRequest request ) throws Exception {
		
		if ( requestJSONBytes == null || requestJSONBytes.length == 0 ) {
			String msg = "No Request body";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		WebserviceRequest webserviceRequest = 
				Unmarshal_RestRequest_JSON_ToObject.getInstance() // throws WebApplicationException if fail
				.getObjectFromJSONByteArray( requestJSONBytes, WebserviceRequest.class );
		
		Integer projectId = webserviceRequest.projectId;

		try {
			if ( projectId == null || projectId == 0 ) {
				String msg = "Provided projectId is not provided or is zero";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			// Auth check complete
			
			////////////////////

    		List<SavedViewListForProjectIdItem> savedViewListDB = 
    				SavedViewListForProjectIdSearcher.getInstance().getSavedViewListForProjectId( projectId );

    		List<SavedViewItem> savedViewList = null;
    		
    		if ( ! savedViewListDB.isEmpty() ) {

    			savedViewList = new ArrayList<>( savedViewListDB.size() );
    			
        		Integer authUserId = null;
        		
        		if ( userSession != null ) {
        			authUserId = userSession.getAuthUserId();
        		}
        		
    			boolean requestFromActualUser = false;
    			
    			if ( userSession != null && userSession.isActualUser() && authUserId != null ) {
    				requestFromActualUser = true;
    			}

    			for ( SavedViewListForProjectIdItem savedViewItenDB : savedViewListDB ) {
    				
    				SavedViewItem savedViewItem = new SavedViewItem();
    				savedViewItem.setId( savedViewItenDB.getId() );
    				savedViewItem.setLabel( savedViewItenDB.getLabel() );
    				savedViewItem.setUrl( savedViewItenDB.getUrl() );
    				
					if ( requestFromActualUser ) {
    					if ( authAccessLevel.isProjectOwnerAllowed() ) {
    						// Project owner so can change or delete this entry
    						savedViewItem.setCanEdit(true);
    						savedViewItem.setCanDelete(true);
    					} else if ( authAccessLevel.isAssistantProjectOwnerAllowed() ) {
    						if ( savedViewItenDB.getAuthUserIdCreated() == authUserId ) {
        						// Researcher can only alter or delete their own saved views
        						savedViewItem.setCanEdit(true);
        						savedViewItem.setCanDelete(true);
    						}
    					}
    				}
    				savedViewList.add( savedViewItem );
    			}

    		} else {
    			//  No items
    			savedViewList = new ArrayList<>();
    		}
    		
    		WebserviceResult webserviceResult = new WebserviceResult();
    		webserviceResult.savedViewList = savedViewList;

			return webserviceResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
