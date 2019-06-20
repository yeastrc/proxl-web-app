package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collection;
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
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.SearchSearcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDBDataOutOfSyncException;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.UnmarshalJSON_ToObject;

/**
 * Get all Filterable Annotation Types for Project Id
 *
 */
@Path("/getAnnotationTypesFilterableForProjectId")
public class AnnotationTypesForProjectIdService {
	
	private static final Logger log = LoggerFactory.getLogger( AnnotationTypesForProjectIdService.class);

	/**
	 * Input to function webserviceMethod(..)
	 */
	public static class WebserviceRequest {
		private Integer projectId;

		public void setProjectId(Integer projectId) {
			this.projectId = projectId;
		}
	}

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	public WebserviceResult webserviceMethod( 
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
		
		if ( webserviceRequest.projectId == null || webserviceRequest.projectId == 0 ) {
			String msg = ": Provided projectId is not provided or is zero";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
		    	        .build()
		    	        );
		}
		
		Integer projectId = webserviceRequest.projectId;
		
		try {
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
			

			WebserviceResult result = new WebserviceResult();
			
			List<Integer> projectSearchIds = SearchSearcher.getInstance().getProjectSearchIdsForProjectId( projectId );
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			
			for ( Integer projectSearchId : projectSearchIds ) {
				Integer searchId =
						MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
				if ( searchId == null ) {
					String msg = ": No searchId found for projectSearchId: " + projectSearchId + ", projectId: " + projectId;
					log.warn( msg );
				    throw new WebApplicationException(
				    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
				    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
				    	        .build()
				    	        );
				}
				searchIdsCollection.add( searchId );

			}
			
			Map<Integer,SearchProgramsPerSearchDTO> searchProgramsPerSearchDTOMap = new HashMap<>();

			{
				//  Get  Annotation Type records for Reported Peptide
				List<WebserviceResultEntry> annotationTypeWebserviceEntryResultList =  
						process_AnnotationTypeEntries_ForLevel( searchIdsCollection, PsmPeptideAnnotationType.PEPTIDE, searchProgramsPerSearchDTOMap);
				result.reportedPeptideAnnotationTypeList = annotationTypeWebserviceEntryResultList;
			}
			{
				//  Get  Annotation Type records for PSM
				List<WebserviceResultEntry> annotationTypeWebserviceEntryResultList =  
						process_AnnotationTypeEntries_ForLevel( searchIdsCollection, PsmPeptideAnnotationType.PSM, searchProgramsPerSearchDTOMap);
				result.psmAnnotationTypeList = annotationTypeWebserviceEntryResultList;
			}
			{
				//  SKIP PSM Per Peptide entries since user cannot filter on those yet.
				
				//  Get  Annotation Type records for PSM per Peptide
//				List<WebserviceResultEntry> annotationTypeWebserviceEntryResultList =  
//						process_AnnotationTypeEntries_ForLevel( searchIdsCollection, PsmPeptideAnnotationType.PSM_PER_PEPTIDE, searchProgramsPerSearchDTOMap);
//				result.psmPerPeptideAnnotationTypeList = annotationTypeWebserviceEntryResultList;
			}
			
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
	 * @param searchIdsCollection
	 * @param psmPeptideAnnotationType
	 * @param searchProgramsPerSearchDTOMap
	 * @return
	 * @throws Exception
	 */
	private List<WebserviceResultEntry>  process_AnnotationTypeEntries_ForLevel(
			Collection<Integer> searchIdsCollection,
			PsmPeptideAnnotationType psmPeptideAnnotationType,
			Map<Integer, SearchProgramsPerSearchDTO> searchProgramsPerSearchDTOMap)
			throws Exception {
		
		//  Get  Annotation Type records for PSM
		//    Filterable annotations
		Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeDTOMapPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAnnTypeForSearchIdsFiltDescPsmPeptide( searchIdsCollection, FilterableDescriptiveAnnotationType.FILTERABLE, psmPeptideAnnotationType );

		List<WebserviceResultEntry> annotationTypeWebserviceEntryResultList = new ArrayList<>();
		
		for ( Map.Entry<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeDTOMapPerSearchIdMap_Entry : annotationTypeDTOMapPerSearchIdMap.entrySet() ) {
			Integer searchId = annotationTypeDTOMapPerSearchIdMap_Entry.getKey();
			Map<Integer, AnnotationTypeDTO> annotationTypeDTOMapPerSearchIdMap_Entry_Value = annotationTypeDTOMapPerSearchIdMap_Entry.getValue();
			for ( Map.Entry<Integer, AnnotationTypeDTO> entry : annotationTypeDTOMapPerSearchIdMap_Entry_Value.entrySet() ) {
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
				annotationTypeWebserviceEntryResultList.add( psmAnnotationFilterableTypesForSearchIdServiceResultEntry );
			}
		}
		
		return annotationTypeWebserviceEntryResultList;
	}
	
	/**
	 * result from Webservice
	 *
	 */
	public static class WebserviceResult {
		
		private List<WebserviceResultEntry> reportedPeptideAnnotationTypeList;
		private List<WebserviceResultEntry> psmAnnotationTypeList;
		private List<WebserviceResultEntry> psmPerPeptideAnnotationTypeList;
		
		public List<WebserviceResultEntry> getPsmAnnotationTypeList() {
			return psmAnnotationTypeList;
		}
		public List<WebserviceResultEntry> getReportedPeptideAnnotationTypeList() {
			return reportedPeptideAnnotationTypeList;
		}
		public List<WebserviceResultEntry> getPsmPerPeptideAnnotationTypeList() {
			return psmPerPeptideAnnotationTypeList;
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
