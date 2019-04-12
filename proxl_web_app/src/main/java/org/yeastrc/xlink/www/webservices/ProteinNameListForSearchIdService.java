package org.yeastrc.xlink.www.webservices;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionIdProteinAnnotationName;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ProteinSequenceVersionIdAnnotationNameSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.CombineProteinAnnNamesForSameSeqVId;
import org.yeastrc.xlink.www.web_utils.UnmarshalJSON_ToObject;


@Path("/proteinNames")
public class ProteinNameListForSearchIdService {

	private static final Logger log = LoggerFactory.getLogger( ProteinNameListForSearchIdService.class);
	
	/**
	 * Input to function getProteinNameListForProjectSearchId(..)
	 */
	public static class WebserviceRequest {
		private Integer projectSearchId;

		public void setProjectSearchId(Integer projectSearchId) {
			this.projectSearchId = projectSearchId;
		}
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getProteinNameListForProjectSearchId") 
	public ProteinNameListForSearchIdServiceResult getProteinNameListForProjectSearchId( 
			byte[] requestJSONBytes,
			@Context HttpServletRequest request )
	throws Exception {

		if ( requestJSONBytes == null || requestJSONBytes.length == 0 ) {
			String msg = "requestJSONBytes is null or requestJSONBytes is empty";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity(  )
		    	        .build()
		    	        );
		}
		WebserviceRequest webserviceRequest = null;
		try {
			webserviceRequest =
					UnmarshalJSON_ToObject.getInstance().getObjectFromJSONByteArray( requestJSONBytes, WebserviceRequest.class );
		} catch ( Exception e ) {
			String msg = "parse request failed";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity(  )
		    	        .build()
		    	        );
		}
		
		if ( webserviceRequest.projectSearchId == null || webserviceRequest.projectSearchId == 0 ) {
			String msg = ": Provided projectSearchId is not provided or is zero";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		
		Integer projectSearchId = webserviceRequest.projectSearchId;
		
		try {
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsCollection = new HashSet<Integer>( );
			projectSearchIdsCollection.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchId: " + projectSearchId;
				log.warn( msg );
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
			

			Integer searchId =
					MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
			
			if ( searchId == null ) {
				String msg = ": No searchId found for projectSearchId: " + projectSearchId;
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
			    	        .build()
			    	        );
			}
			
			//  Get  proteinSequenceVersionId and AnnotationName for search
			
			Set<ProteinSequenceVersionIdProteinAnnotationName> proteinSequenceVersionIdProteinAnnotationNameSet = 
					ProteinSequenceVersionIdAnnotationNameSearcher.getInstance()
					.getProteinSequenceVersionIdAnnotationNameForSearch( searchId );
			
			Set<ProteinSequenceVersionIdProteinAnnotationName> proteinSequenceVersionIdProteinAnnotationNameCombinedSet = 
				CombineProteinAnnNamesForSameSeqVId.getInstance()
				.combineProteinAnnNamesForSameSeqVId( proteinSequenceVersionIdProteinAnnotationNameSet );
			
			List<ProteinSequenceVersionIdProteinAnnotationName> proteinSequenceVersionIdProteinAnnotationNameList =
					new ArrayList<>( proteinSequenceVersionIdProteinAnnotationNameCombinedSet );

			Collections.sort( proteinSequenceVersionIdProteinAnnotationNameList, new Comparator<ProteinSequenceVersionIdProteinAnnotationName>() {
				@Override
				public int compare(ProteinSequenceVersionIdProteinAnnotationName o1, ProteinSequenceVersionIdProteinAnnotationName o2) {
					return o1.getAnnotationName().compareToIgnoreCase( o2.getAnnotationName() );
				}
			});
			ProteinNameListForSearchIdServiceResult result = new ProteinNameListForSearchIdServiceResult();
			result.proteinSequenceVersionIdProteinAnnotationNameList = proteinSequenceVersionIdProteinAnnotationNameList;
			return result;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	
	public static final class ProteinNameListForSearchIdServiceResult {
		
		List<ProteinSequenceVersionIdProteinAnnotationName> proteinSequenceVersionIdProteinAnnotationNameList;

		public List<ProteinSequenceVersionIdProteinAnnotationName> getProteinSequenceVersionIdProteinAnnotationNameList() {
			return proteinSequenceVersionIdProteinAnnotationNameList;
		}

		public void setProteinSequenceVersionIdProteinAnnotationNameList(
				List<ProteinSequenceVersionIdProteinAnnotationName> proteinSequenceVersionIdProteinAnnotationNameList) {
			this.proteinSequenceVersionIdProteinAnnotationNameList = proteinSequenceVersionIdProteinAnnotationNameList;
		}
		
	}
}
