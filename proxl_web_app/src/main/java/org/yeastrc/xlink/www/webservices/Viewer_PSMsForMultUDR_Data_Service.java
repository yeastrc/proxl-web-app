package org.yeastrc.xlink.www.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
//import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDBDataOutOfSyncException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.search_programs_per_search_utils.GetSearchProgramsPerSearchData;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmAnnFromCrosslinkProteinSearcher;
import org.yeastrc.xlink.www.searcher.PsmAnnFromLooplinkProteinSearcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Get the PSMs for Multiple UDR
 * 
 * Only used by Structure page
 * 
 * Data is Unfiltered, no filters are used.  Not Used: "PSM, Peptide Cutoffs", "Exclude links with:", "Exclude proteins with:", "Exclude organisms:"
 */
@Path("/imageViewer")
public class Viewer_PSMsForMultUDR_Data_Service {

	private static final Logger log = LoggerFactory.getLogger( Viewer_PSMsForMultUDR_Data_Service.class);
	
	private static enum LinkType { Crosslink, Looplink }
	
	/**
	 * Request for multiple UDRs
	 *
	 */
	public static class Viewer_UDR_Data_Service_Request {
		private List<Integer> projectSearchIds;
		private List<Viewer_UDR_Data_Service_Single_UDR_Request> crosslinkUdrRequestList;
		private List<Viewer_UDR_Data_Service_Single_UDR_Request> looplinkUdrRequestList;
		
		public List<Viewer_UDR_Data_Service_Single_UDR_Request> getCrosslinkUdrRequestList() {
			return crosslinkUdrRequestList;
		}
		public void setCrosslinkUdrRequestList(List<Viewer_UDR_Data_Service_Single_UDR_Request> crosslinkUdrRequestList) {
			this.crosslinkUdrRequestList = crosslinkUdrRequestList;
		}
		public List<Viewer_UDR_Data_Service_Single_UDR_Request> getLooplinkUdrRequestList() {
			return looplinkUdrRequestList;
		}
		public void setLooplinkUdrRequestList(List<Viewer_UDR_Data_Service_Single_UDR_Request> looplinkUdrRequestList) {
			this.looplinkUdrRequestList = looplinkUdrRequestList;
		}
		public List<Integer> getProjectSearchIds() {
			return projectSearchIds;
		}
		public void setProjectSearchIds(List<Integer> projectSearchIds) {
			this.projectSearchIds = projectSearchIds;
		}
	}
	
	/**
	 * Request Sub part for single UDR
	 *
	 */
	public static class Viewer_UDR_Data_Service_Single_UDR_Request {
		private Integer protId1;
		private Integer pos1;
		private Integer protId2;
		private Integer pos2;
		
		public Integer getProtId1() {
			return protId1;
		}
		public void setProtId1(Integer protId1) {
			this.protId1 = protId1;
		}
		public Integer getPos1() {
			return pos1;
		}
		public void setPos1(Integer pos1) {
			this.pos1 = pos1;
		}
		public Integer getProtId2() {
			return protId2;
		}
		public void setProtId2(Integer protId2) {
			this.protId2 = protId2;
		}
		public Integer getPos2() {
			return pos2;
		}
		public void setPos2(Integer pos2) {
			this.pos2 = pos2;
		}
	}
	
	/**
	 * Result for request
	 *
	 */
	public static class Viewer_UDR_Data_Service_Result {
		private Map<Integer,Viewer_UDR_Data_Service_Single_Search_Result> dataForSearches;
		
		public Map<Integer, Viewer_UDR_Data_Service_Single_Search_Result> getDataForSearches() {
			return dataForSearches;
		}
		public void setDataForSearches(Map<Integer, Viewer_UDR_Data_Service_Single_Search_Result> dataForSearches) {
			this.dataForSearches = dataForSearches;
		}
	}
	/**
	 * Result for single search
	 *
	 */
	public static class Viewer_UDR_Data_Service_Single_Search_Result {
		private List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> crosslinkUdrItemList;
		private List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> looplinkUdrItemList;
		private List<String> psmValuesNames;
		
		public List<String> getPsmValuesNames() {
			return psmValuesNames;
		}
		public void setPsmValuesNames(List<String> psmValuesNames) {
			this.psmValuesNames = psmValuesNames;
		}
		public List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> getCrosslinkUdrItemList() {
			return crosslinkUdrItemList;
		}
		public void setCrosslinkUdrItemList(List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> crosslinkUdrItemList) {
			this.crosslinkUdrItemList = crosslinkUdrItemList;
		}
		public List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> getLooplinkUdrItemList() {
			return looplinkUdrItemList;
		}
		public void setLooplinkUdrItemList(List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> looplinkUdrItemList) {
			this.looplinkUdrItemList = looplinkUdrItemList;
		}
	}
	/**
	 * Result for 1 UDR within a search
	 *
	 */
	public static class Viewer_PSM_UDR_Data_Service_Result_UDR_Item {
		private Integer protId1;
		private Integer pos1;
		private Integer protId2;
		private Integer pos2;
		private List<Viewer_PSM_UDR_Data_Service_Result_PSM_Item> psmItemList;
		public Integer getProtId1() {
			return protId1;
		}
		public void setProtId1(Integer protId1) {
			this.protId1 = protId1;
		}
		public Integer getPos1() {
			return pos1;
		}
		public void setPos1(Integer pos1) {
			this.pos1 = pos1;
		}
		public Integer getProtId2() {
			return protId2;
		}
		public void setProtId2(Integer protId2) {
			this.protId2 = protId2;
		}
		public Integer getPos2() {
			return pos2;
		}
		public void setPos2(Integer pos2) {
			this.pos2 = pos2;
		}
		public List<Viewer_PSM_UDR_Data_Service_Result_PSM_Item> getPsmItemList() {
			return psmItemList;
		}
		public void setPsmItemList(List<Viewer_PSM_UDR_Data_Service_Result_PSM_Item> psmItemList) {
			this.psmItemList = psmItemList;
		}
	}
	public static class Viewer_PSM_UDR_Data_Service_Result_PSM_Item {
		private int psmId;
		private List<String> psmValues;
		public int getPsmId() {
			return psmId;
		}
		public void setPsmId(int psmId) {
			this.psmId = psmId;
		}
		public List<String> getPsmValues() {
			return psmValues;
		}
		public void setPsmValues(List<String> psmValues) {
			this.psmValues = psmValues;
		}
	}
	
	/**
	 * See comments for class
	 * 
	 * @param queryJSONString
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getMultPsmUDRData") 
	public Viewer_UDR_Data_Service_Result getViewerDataCrosslink(
			@QueryParam( "query" ) String queryJSONString,
			@Context HttpServletRequest request )
	throws Exception {
		return getViewerDataMultUDRInternalFromJSON( queryJSONString, request );
	}
	
	/**
	 * See comments for class
	 * 
	 * @param webserviceRequest
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getMultPsmUDRData") 
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	public Viewer_UDR_Data_Service_Result getViewerDataMultUDRCrosslinkPost(
			Viewer_UDR_Data_Service_Request webserviceRequest,
			@Context HttpServletRequest request )
					throws Exception {
		return getViewerDataMultUDRInternal( webserviceRequest, request );
	}
	
	/**
	 * @param queryJSONString
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private Viewer_UDR_Data_Service_Result getViewerDataMultUDRInternalFromJSON(
			String queryJSONString,
			HttpServletRequest request )
	throws Exception {
		if ( StringUtils.isEmpty( queryJSONString ) ) {
			String msg = "Provided 'query' is null or empty";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		Viewer_UDR_Data_Service_Request webserviceRequest = null;
		try {
			webserviceRequest = jacksonJSON_Mapper.readValue( queryJSONString, Viewer_UDR_Data_Service_Request.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'queryJSONString', JsonParseException.  queryJSONString: " + queryJSONString;
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'queryJSONString', JsonMappingException.  queryJSONString: " + queryJSONString;
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		} catch ( IOException e ) {
			String msg = "Failed to parse 'queryJSONString', IOException.  queryJSONString: " + queryJSONString;
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		return getViewerDataMultUDRInternal( webserviceRequest, request );
	}
	
	/**
	 * @param webserviceRequest
	 * @param linkType
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private Viewer_UDR_Data_Service_Result getViewerDataMultUDRInternal(
			Viewer_UDR_Data_Service_Request webserviceRequest,
			HttpServletRequest request )
					throws Exception {
		try {
			List<Integer> projectSearchIdList = webserviceRequest.projectSearchIds;
			if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
				String msg = "Provided searchIds is null or empty, searchIds = " + projectSearchIdList;
				log.warn(msg);
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//   Get the project id for this search
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.addAll( projectSearchIdList );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int projectSearchId : projectSearchIdList ) {
					msg += projectSearchId + ", ";
				}				
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
			
			Viewer_UDR_Data_Service_Result viewer_UDR_Data_Service_Result = new Viewer_UDR_Data_Service_Result();
			Map<Integer,Viewer_UDR_Data_Service_Single_Search_Result> dataForSearches = new HashMap<>();
			viewer_UDR_Data_Service_Result.dataForSearches = dataForSearches;
			Set<Integer> projectSearchIdSet = new HashSet<>( projectSearchIdList );
			List<Integer> projectSearchIdListDeduppedSorted = new ArrayList<Integer>( projectSearchIdSet );
			Collections.sort( projectSearchIdListDeduppedSorted );
			Set<Integer> searchIds = new HashSet<>();
			Map<Integer,Integer> projectSearchIdToSearchIdMap = new HashMap<>();
			for ( Integer projectSearchId : projectSearchIdListDeduppedSorted ) {
				Integer searchId = MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
				if ( searchId == null ) {
					String msg = "searchId not found for projectSearchId = " + projectSearchId;
					log.error( msg );
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
							.build() );
				}
				searchIds.add( searchId );
				projectSearchIdToSearchIdMap.put( projectSearchId, searchId );
			}			
			//             Get Annotation Type records for PSM and Peptide
			//  Get  Annotation Type records for PSM
			Map<Integer, Map<Integer, AnnotationTypeDTO>> 
			srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIds );
			
			for ( Integer projectSearchId : projectSearchIdListDeduppedSorted ) {
				Integer searchId = projectSearchIdToSearchIdMap.get( projectSearchId );
				Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOMap = 
						srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );
				if ( srchPgm_Filterable_Psm_AnnotationType_DTOMap == null ) {
					//  No records were found, probably an error   TODO
					srchPgm_Filterable_Psm_AnnotationType_DTOMap = new HashMap<>();
				}
				List<AnnotationTypeDTO> annotationTypesOrderByNameList = new ArrayList<>( srchPgm_Filterable_Psm_AnnotationType_DTOMap.size() );
				for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgm_Filterable_Psm_AnnotationType_DTOMap.entrySet() ) {
					annotationTypesOrderByNameList.add( entry.getValue() );
				}

				Collections.sort( annotationTypesOrderByNameList, new Comparator<AnnotationTypeDTO>() {
					@Override
					public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {
						return o1.getName().compareToIgnoreCase( o2.getName() );
					}
				});
				Map<Integer,SearchProgramsPerSearchDTO> searchProgramsPerSearchDTO_MappedOnId = new HashMap<>();
				List<String> psmValuesNames = new ArrayList<>();
				for ( AnnotationTypeDTO annotationTypeDTO : annotationTypesOrderByNameList ) {
					Integer searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();
					SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = searchProgramsPerSearchDTO_MappedOnId.get( searchProgramsPerSearchId );
					if ( searchProgramsPerSearchDTO == null ) {
						searchProgramsPerSearchDTO =
							GetSearchProgramsPerSearchData.getInstance().getSearchProgramsPerSearchDTO( searchProgramsPerSearchId );
					}
					if ( searchProgramsPerSearchDTO == null ) {
						String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
						log.error( msg );
						throw new ProxlWebappDBDataOutOfSyncException( msg );
					}
					String annTypeNameDesc = annotationTypeDTO.getName() + "(" + searchProgramsPerSearchDTO.getDisplayName() + ")";
					psmValuesNames.add( annTypeNameDesc );
				}
				//  Output Result Data
				Viewer_UDR_Data_Service_Single_Search_Result single_Search_Result = new Viewer_UDR_Data_Service_Single_Search_Result();
				dataForSearches.put( searchId, single_Search_Result );
				single_Search_Result.setPsmValuesNames( psmValuesNames );
				if ( webserviceRequest.crosslinkUdrRequestList != null 
						&& ( ! webserviceRequest.crosslinkUdrRequestList.isEmpty() ) ) {
					single_Search_Result.crosslinkUdrItemList = 
							processCrosslinkOrLooplink(
									searchId, 
									LinkType.Crosslink, 
									webserviceRequest.crosslinkUdrRequestList,
									annotationTypesOrderByNameList );
				}
				if ( webserviceRequest.looplinkUdrRequestList != null 
						&& ( ! webserviceRequest.looplinkUdrRequestList.isEmpty() ) ) {
					single_Search_Result.looplinkUdrItemList = 
							processCrosslinkOrLooplink(
									searchId, 
									LinkType.Looplink, 
									webserviceRequest.looplinkUdrRequestList,
									annotationTypesOrderByNameList );
				}
			}
			return viewer_UDR_Data_Service_Result;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE)  //  return 500 error
			    	        .entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT )
			    	        .build()
			    	        );
		}
	}
	
	/**
	 * @param searchId
	 * @param linkType
	 * @param udrRequestList
	 * @param annotationTypesOrderByNameList
	 * @return
	 * @throws Exception
	 * @throws ProxlWebappDataException
	 */
	private List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> processCrosslinkOrLooplink(
			int searchId,
			LinkType linkType,
			List<Viewer_UDR_Data_Service_Single_UDR_Request> udrRequestList,
			List<AnnotationTypeDTO> annotationTypesOrderByNameList
			) throws Exception, ProxlWebappDataException {
		
		List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> udrItemList = new ArrayList<>();
		
		for ( Viewer_UDR_Data_Service_Single_UDR_Request singleUDRRequest : udrRequestList ) {
			
			Viewer_PSM_UDR_Data_Service_Result_UDR_Item udrItem = new Viewer_PSM_UDR_Data_Service_Result_UDR_Item(); 
			udrItemList.add( udrItem );
			udrItem.protId1 = singleUDRRequest.protId1;
			udrItem.protId2 = singleUDRRequest.protId2;
			udrItem.pos1 = singleUDRRequest.pos1;
			udrItem.pos2 = singleUDRRequest.pos2;
			List<Viewer_PSM_UDR_Data_Service_Result_PSM_Item> udrPsmItemList = new ArrayList<>();
			udrItem.psmItemList = udrPsmItemList;
			
			//  Map<PSM_ID,Map<AnnTypeId,Value>
			Map<Integer,Map<Integer,Double>> psmAnnValues = null; 
			if ( linkType == LinkType.Crosslink ) {
				//  Get psm data for these parameters for Crosslinks
				if ( singleUDRRequest.protId1 == null ) {
					String msg = "Provided protId1 is null or missing";
					log.error( msg );
					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg ).build() );
				}
				if ( singleUDRRequest.pos1 == null ) {
					String msg = "Provided pos1 is null or missing";
					log.error( msg );
					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg ).build() );
				}
				if ( singleUDRRequest.protId2 == null ) {
					String msg = "Provided protId2 is null or missing";
					log.error( msg );
					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg ).build() );
				}
				if ( singleUDRRequest.pos2 == null ) {
					String msg = "Provided pos2 is null or missing";
					log.error( msg );
					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg ).build() );
				}
				psmAnnValues = 
						PsmAnnFromCrosslinkProteinSearcher.getInstance()
						.searchOnSearchProteinCrosslink( 
								searchId, 
								singleUDRRequest.protId1, 
								singleUDRRequest.protId2, 
								singleUDRRequest.pos1, 
								singleUDRRequest.pos2 );
			} else {
				//  Get peptides for these parameters for Looplinks
				if ( singleUDRRequest.protId1 == null ) {
					String msg = "Provided protId1 is null or missing";
					log.error( msg );
					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg ).build() );
				}
				if ( singleUDRRequest.pos1 == null ) {
					String msg = "Provided pos1 is null or missing";
					log.error( msg );
					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg ).build() );
				}
				if ( singleUDRRequest.pos2 == null ) {
					String msg = "Provided pos2 is null or missing";
					log.error( msg );
					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg ).build() );
				}
				psmAnnValues =
						PsmAnnFromLooplinkProteinSearcher.getInstance()
						.searchOnSearchProteinLooplink( 
								searchId, singleUDRRequest.protId1, singleUDRRequest.pos1, singleUDRRequest.pos2 );
			}
			for ( Map.Entry<Integer,Map<Integer,Double>> psmAnnValuesEntry : psmAnnValues.entrySet() ) {
				Integer psmId = psmAnnValuesEntry.getKey();
				Map<Integer,Double> psmAnnotationDataMap = psmAnnValuesEntry.getValue();
				Viewer_PSM_UDR_Data_Service_Result_PSM_Item udrPsmItem = new Viewer_PSM_UDR_Data_Service_Result_PSM_Item();
				udrPsmItemList.add(udrPsmItem);
				List<String> psmValues = new ArrayList<>();
				udrPsmItem.setPsmValues( psmValues );
				udrPsmItem.setPsmId( psmId );
				//  Copy to output by ann type id
				for ( AnnotationTypeDTO annotationType : annotationTypesOrderByNameList ) {
					Double psmAnnValue = psmAnnotationDataMap.get( annotationType.getId() );
					if ( psmAnnValue == null ) {
						String msg = "No data for ann type id: " + annotationType.getId() 
						+ ", for PSM id: " + psmId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					psmValues.add( Double.toString( psmAnnValue ) );
				}
			}
		}
		return udrItemList;
	}
}
