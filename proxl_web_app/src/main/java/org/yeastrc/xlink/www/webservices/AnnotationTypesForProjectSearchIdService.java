package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.yeastrc.xlink.dao.SearchProgramsPerSearchDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDBDataOutOfSyncException;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.UnmarshalJSON_ToObject;
/**
 * 
 *
 */
@Path("/annotationTypes")
public class AnnotationTypesForProjectSearchIdService {
	
	private static final Logger log = LoggerFactory.getLogger( AnnotationTypesForProjectSearchIdService.class);

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
	@Path("/getAnnotationTypesPsmFilterableForProjectSearchId") 
	public WebserviceResult getPSMFilterableAnnTypesForProjectSearchId( 
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
				String msg = "No project ids for search id: " + projectSearchId;
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
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
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
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			searchIdsCollection.add( searchId );
			//  Get  Annotation Type records for PSM
			//    Filterable annotations
			Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgmFilterablePsmAnnotationTypeDTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsCollection );
			Map<Integer, AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOMap = 
					srchPgmFilterablePsmAnnotationTypeDTOListPerSearchIdMap.get( searchId );
			if ( srchPgmFilterablePsmAnnotationTypeDTOMap == null ) {
				//  No records were found, probably an error   TODO
				srchPgmFilterablePsmAnnotationTypeDTOMap = new HashMap<>();
			}
			Map<Integer,SearchProgramsPerSearchDTO> searchProgramsPerSearchDTOMap = new HashMap<>();
			List<WebserviceResultEntry> annotationTypeList = new ArrayList<>( srchPgmFilterablePsmAnnotationTypeDTOMap.size() );
			for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgmFilterablePsmAnnotationTypeDTOMap.entrySet() ) {
				AnnotationTypeDTO annotationTypeDTO = entry.getValue();
				Integer searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();
				WebserviceResultEntry psmAnnotationFilterableTypesForSearchIdServiceResultEntry = new WebserviceResultEntry();
				psmAnnotationFilterableTypesForSearchIdServiceResultEntry.setAnnotationTypeDTO( annotationTypeDTO );
				SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = searchProgramsPerSearchDTOMap.get( searchProgramsPerSearchId );
				if ( searchProgramsPerSearchDTO == null ) {
					searchProgramsPerSearchDTO = SearchProgramsPerSearchDAO.getInstance().getSearchProgramDTOForId( searchProgramsPerSearchId ) ;
					if ( searchProgramsPerSearchDTO == null ) {
						String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
						log.error( msg );
						throw new ProxlWebappDBDataOutOfSyncException( msg );
					}
					searchProgramsPerSearchDTOMap.put( searchProgramsPerSearchId, searchProgramsPerSearchDTO );
				}
				psmAnnotationFilterableTypesForSearchIdServiceResultEntry.setSearchProgramsPerSearchDTO( searchProgramsPerSearchDTO );
				annotationTypeList.add( psmAnnotationFilterableTypesForSearchIdServiceResultEntry );
			}
			Collections.sort( annotationTypeList, new Comparator<WebserviceResultEntry>() {
				@Override
				public int compare(WebserviceResultEntry o1, WebserviceResultEntry o2) {
					return o1.getAnnotationTypeDTO().getName().compareToIgnoreCase( o2.getAnnotationTypeDTO().getName() );
				}
			});
			WebserviceResult result = new WebserviceResult();
			result.setAnnotationTypeList( annotationTypeList );;
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
	
	/**
	 * result from Webservice
	 *
	 */
	public static class WebserviceResult {
		private List<WebserviceResultEntry> annotationTypeList;
		public List<WebserviceResultEntry> getAnnotationTypeList() {
			return annotationTypeList;
		}
		public void setAnnotationTypeList(
				List<WebserviceResultEntry> annotationTypeList) {
			this.annotationTypeList = annotationTypeList;
		}
	}
	
	/**
	 * Entry in WebserviceResult
	 *
	 */
	public static class WebserviceResultEntry {
		private AnnotationTypeDTO annotationTypeDTO;
		private SearchProgramsPerSearchDTO searchProgramsPerSearchDTO;
		public AnnotationTypeDTO getAnnotationTypeDTO() {
			return annotationTypeDTO;
		}
		public void setAnnotationTypeDTO(AnnotationTypeDTO annotationTypeDTO) {
			this.annotationTypeDTO = annotationTypeDTO;
		}
		public SearchProgramsPerSearchDTO getSearchProgramsPerSearchDTO() {
			return searchProgramsPerSearchDTO;
		}
		public void setSearchProgramsPerSearchDTO(
				SearchProgramsPerSearchDTO searchProgramsPerSearchDTO) {
			this.searchProgramsPerSearchDTO = searchProgramsPerSearchDTO;
		}
	}
}
