package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.www.objects.AnnotationMinMaxFilterableValues;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.AnnotationTypesMinMaxValuesForAnnTypeIdsSearchIdServiceResult;
import org.yeastrc.xlink.www.objects.AnnotationTypesMinMaxValuesForAnnTypeIdsSearchIdServiceResult.AnnotationTypesMinMaxValuesEntry;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmMinMaxForSearchIdAnnotationTypeIdSearcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

@Path("/annotationTypes")
public class AnnotationTypesMinMaxValuesForAnnTypeIdsProjectSearchIdService {

	private static final Logger log = LoggerFactory.getLogger( AnnotationTypesMinMaxValuesForAnnTypeIdsProjectSearchIdService.class);
	
	/**
	 * Min and Max values for:
	 * projectSearchId
	 * PSM Filterable Annotation Type Ids
	 * 
	 * @param projectSearchId
	 * @param annotationTypeIds
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getMinMaxValuesForPsmFilterableAnnTypeIdsProjectSearchId") 
	public AnnotationTypesMinMaxValuesForAnnTypeIdsSearchIdServiceResult getPSMFilterableAnnTypesForProjectSearchId( 
			@QueryParam( "projectSearchId" ) int projectSearchId,
			@QueryParam( "ann_type_id" ) List<Integer> annotationTypeIds,
			@Context HttpServletRequest request )
	throws Exception {
		if ( projectSearchId == 0 ) {
			String msg = ": projectSearchId is zero or not provided";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		if ( annotationTypeIds == null || annotationTypeIds.isEmpty() ) {
			String msg = ": No provided 'ann_type_id'";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
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
			///////
			//  Get annotation type DTO for each annotation type id in the parameter list
			List<AnnotationTypeDTO> annotationTypeDTOList = new ArrayList<>( srchPgmFilterablePsmAnnotationTypeDTOMap.size() );
			for ( Integer annotationTypeId : annotationTypeIds ) {
				AnnotationTypeDTO annotationTypeDTO = srchPgmFilterablePsmAnnotationTypeDTOMap.get( annotationTypeId );
				if ( annotationTypeDTO == null ) {
					String msg = ": ann_type_id " + annotationTypeId + " not valid for search id";
					log.error( msg );
					throw new WebApplicationException(
							Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
							.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
							.build()
							);
				}
				annotationTypeDTOList.add( annotationTypeDTO );
			}
			Map<Integer, AnnotationTypesMinMaxValuesEntry> minMaxValuesPerAnnType = new HashMap<>();
			for ( AnnotationTypeDTO annotationTypeDTO : annotationTypeDTOList ) {
				AnnotationMinMaxFilterableValues annotationMinMaxFilterableValues =
						PsmMinMaxForSearchIdAnnotationTypeIdSearcher.getInstance()
						.getPsmMinMaxForSearchIdAnnotationTypeIdSearcher( searchId, annotationTypeDTO.getId() );
				AnnotationTypesMinMaxValuesEntry annotationTypesMinMaxValuesEntry = new AnnotationTypesMinMaxValuesEntry();
				annotationTypesMinMaxValuesEntry.setMinValue( annotationMinMaxFilterableValues.getMinValue() );
				annotationTypesMinMaxValuesEntry.setMaxValue( annotationMinMaxFilterableValues.getMaxValue() );
				minMaxValuesPerAnnType.put( annotationTypeDTO.getId(), annotationTypesMinMaxValuesEntry );
			}
			AnnotationTypesMinMaxValuesForAnnTypeIdsSearchIdServiceResult result = new AnnotationTypesMinMaxValuesForAnnTypeIdsSearchIdServiceResult();
			result.setMinMaxValuesPerAnnType( minMaxValuesPerAnnType );
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
}
