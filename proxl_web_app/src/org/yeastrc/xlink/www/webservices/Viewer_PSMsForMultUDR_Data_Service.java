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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.SearchPeptideCommonLinkAnnDataWrapperIF;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmAnnotationDataSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideCrosslink_LinkedPosition_Searcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideLooplink_LinkedPosition_Searcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Get the PSMs for Multiple UDR
 * 
 * One method for crosslink and one method for looplink
 *
 */
@Path("/imageViewer")
public class Viewer_PSMsForMultUDR_Data_Service {

	private static final Logger log = Logger.getLogger(Viewer_PSMsForMultUDR_Data_Service.class);

	private static enum LinkType { Crosslink, Looplink }
	
	/**
	 * Request for multiple UDRs
	 *
	 */
	public static class Viewer_UDR_Data_Service_Request {

		private List<Integer> searchIds;
		private List<Viewer_UDR_Data_Service_Single_UDR_Request> udrRequestList;

		public List<Viewer_UDR_Data_Service_Single_UDR_Request> getUdrRequestList() {
			return udrRequestList;
		}
		public void setUdrRequestList(List<Viewer_UDR_Data_Service_Single_UDR_Request> udrRequestList) {
			this.udrRequestList = udrRequestList;
		}
		public List<Integer> getSearchIds() {
			return searchIds;
		}
		public void setSearchIds(List<Integer> searchIds) {
			this.searchIds = searchIds;
		}
	}

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
	

	public static class Viewer_UDR_Data_Service_Single_Search_Result {
		
		private List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> udrItemList;
		
		private List<String> psmValuesNames;

		public List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> getUdrItemList() {
			return udrItemList;
		}
		public void setUdrItemList(List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> udrItemList) {
			this.udrItemList = udrItemList;
		}
		public List<String> getPsmValuesNames() {
			return psmValuesNames;
		}
		public void setPsmValuesNames(List<String> psmValuesNames) {
			this.psmValuesNames = psmValuesNames;
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
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getMultPsmUDRDataCrosslink") 
	public Viewer_UDR_Data_Service_Result getViewerDataCrosslink(
			@QueryParam( "query" ) String queryJSONString,
			@Context HttpServletRequest request )
	throws Exception {
		return getViewerDataMultUDRInternalFromJSON( queryJSONString, LinkType.Crosslink, request );
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getMultPsmUDRDataLooplink") 
	public Viewer_UDR_Data_Service_Result getViewerDataLooplink(
			@QueryParam( "query" ) String queryJSONString,
			@Context HttpServletRequest request )
	throws Exception {
		return getViewerDataMultUDRInternalFromJSON( queryJSONString, LinkType.Looplink, request );
	}
	
	@Path("/getMultPsmUDRDataCrosslink") 
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	public Viewer_UDR_Data_Service_Result getViewerDataMultUDRCrosslinkPost(
			Viewer_UDR_Data_Service_Request webserviceRequest,
			@Context HttpServletRequest request )
					throws Exception {
		
		return getViewerDataMultUDRInternal( webserviceRequest, LinkType.Crosslink, request );
	}
	

	@Path("/getMultPsmUDRDataLooplink") 
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	public Viewer_UDR_Data_Service_Result getViewerDataMultUDRLooplinkPost(
			Viewer_UDR_Data_Service_Request webserviceRequest,
			@Context HttpServletRequest request )
					throws Exception {
		
		return getViewerDataMultUDRInternal( webserviceRequest, LinkType.Looplink, request );
	}
	
	/**
	 * @param queryJSONString
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private Viewer_UDR_Data_Service_Result getViewerDataMultUDRInternalFromJSON(
			String queryJSONString,
			LinkType linkType,
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

		return getViewerDataMultUDRInternal( webserviceRequest, linkType, request );
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
			LinkType linkType,
			HttpServletRequest request )
					throws Exception {

		try {

			//  Check Auth
			authCheck(webserviceRequest.getSearchIds(), request);

			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			
			////////   Generic Param processing
			
			Viewer_UDR_Data_Service_Result viewer_UDR_Data_Service_Result = new Viewer_UDR_Data_Service_Result();
			Map<Integer,Viewer_UDR_Data_Service_Single_Search_Result> dataForSearches = new HashMap<>();
			viewer_UDR_Data_Service_Result.dataForSearches = dataForSearches;
			
			
			//  Get Annotation Type records for PSM and Peptide
			
			
			//  Get  Annotation Type records for PSM
			
			Map<Integer, Map<Integer, AnnotationTypeDTO>> 
			srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( webserviceRequest.searchIds );
			
			//  Create empty searcherCutoffValuesSearchLevel so returns everything
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
			
			

			for ( int searchId : webserviceRequest.searchIds ) {
				
				Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOMap = 
						srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );

				if ( srchPgm_Filterable_Psm_AnnotationType_DTOMap == null ) {
					//  No records were found, probably an error   TODO
					srchPgm_Filterable_Psm_AnnotationType_DTOMap = new HashMap<>();
				}
				
				List<AnnotationTypeDTO> annotationTypesOrderByNameList = new ArrayList<>( srchPgm_Filterable_Psm_AnnotationType_DTOMap.size() );
				Set<Integer> annotationTypeIdsForGettingAnnotationData = new HashSet<>();
				
				for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgm_Filterable_Psm_AnnotationType_DTOMap.entrySet() ) {
					annotationTypesOrderByNameList.add( entry.getValue() );
					annotationTypeIdsForGettingAnnotationData.add( entry.getKey() );
				}
				
				Collections.sort( annotationTypesOrderByNameList, new Comparator<AnnotationTypeDTO>() {
					@Override
					public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {
						return o1.getName().compareToIgnoreCase( o2.getName() );
					}
				});
				
				List<String> psmValuesNames = new ArrayList<>();
				for ( AnnotationTypeDTO annotationTypeDTO : annotationTypesOrderByNameList ) {
					psmValuesNames.add( annotationTypeDTO.getName() );
				}
				
				
				//  Output Result Data
				Viewer_UDR_Data_Service_Single_Search_Result single_Search_Result = new Viewer_UDR_Data_Service_Single_Search_Result();
				dataForSearches.put( searchId, single_Search_Result );
				
				
				
				
				List<Viewer_PSM_UDR_Data_Service_Result_UDR_Item> udrItemList = new ArrayList<>();
				single_Search_Result.setUdrItemList( udrItemList );
				single_Search_Result.setPsmValuesNames( psmValuesNames );
				
				for ( Viewer_UDR_Data_Service_Single_UDR_Request singleUDRRequest : webserviceRequest.udrRequestList  ) {

					Viewer_PSM_UDR_Data_Service_Result_UDR_Item udrItem = new Viewer_PSM_UDR_Data_Service_Result_UDR_Item(); 
					udrItemList.add( udrItem );
					
					udrItem.protId1 = singleUDRRequest.protId1;
					udrItem.protId2 = singleUDRRequest.protId2;
					udrItem.pos1 = singleUDRRequest.pos1;
					udrItem.pos2 = singleUDRRequest.pos2;
					
					List<Viewer_PSM_UDR_Data_Service_Result_PSM_Item> udrPsmItemList = new ArrayList<>();
					udrItem.psmItemList = udrPsmItemList;


					List<? extends SearchPeptideCommonLinkAnnDataWrapperIF> reportedPeptideList = null;

					if ( linkType == LinkType.Crosslink ) {
						//  Get peptides for these parameters for Crosslinks

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
						reportedPeptideList = 
								SearchPeptideCrosslink_LinkedPosition_Searcher.getInstance()
								.searchOnSearchProteinCrosslink( 
										searchId, 
										searcherCutoffValuesSearchLevel, // empty so retrieving everything 
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
						reportedPeptideList = 
								SearchPeptideLooplink_LinkedPosition_Searcher.getInstance()
								.searchOnSearchProteinLooplink( 
										searchId, 
										searcherCutoffValuesSearchLevel, 
										singleUDRRequest.protId1, 
										singleUDRRequest.pos1, 
										singleUDRRequest.pos2 );
					}


					for ( SearchPeptideCommonLinkAnnDataWrapperIF reportedPeptideEntry : reportedPeptideList ) {

						//  Get PSM data for each reported peptide
						List<PsmWebDisplayWebServiceResult> psmWebDisplayList = 
								PsmWebDisplaySearcher.getInstance()
								.getPsmsWebDisplay( searchId, 
										reportedPeptideEntry.getReportedPeptideId(), 
										searcherCutoffValuesSearchLevel // empty so retrieving everything  
										);
						// Process PSMs
						for ( PsmWebDisplayWebServiceResult psmWebDisplayEntry :psmWebDisplayList ) {

							Viewer_PSM_UDR_Data_Service_Result_PSM_Item udrPsmItem = new Viewer_PSM_UDR_Data_Service_Result_PSM_Item();
							udrPsmItemList.add(udrPsmItem);
							
							List<String> psmValues = new ArrayList<>();
							udrPsmItem.setPsmValues( psmValues );
							udrPsmItem.setPsmId( psmWebDisplayEntry.getPsmDTO().getId() );

							// For each PSM, get the annotation data for it
							List<PsmAnnotationDTO> psmAnnotationDataList = 
									PsmAnnotationDataSearcher.getInstance()
									.getPsmAnnotationDTOList( 
											psmWebDisplayEntry.getPsmDTO().getId(), 
											annotationTypeIdsForGettingAnnotationData );
							// Transfer to map for lookup by ann type id
							Map<Integer,PsmAnnotationDTO> psmAnnotationDataMap = new HashMap<>();
							for ( PsmAnnotationDTO item : psmAnnotationDataList ) {
								psmAnnotationDataMap.put( item.getAnnotationTypeId(), item );
							}
							//  Copy to output by ann type id
							for ( AnnotationTypeDTO annotationType : annotationTypesOrderByNameList ) {
								PsmAnnotationDTO psmAnnotationDTO = psmAnnotationDataMap.get( annotationType.getId() );
								if ( psmAnnotationDTO == null ) {
									String msg = "No data for ann type id: " + annotationType.getId() 
									+ ", for PSM id: " + psmWebDisplayEntry.getPsmDTO().getId();
									log.error( msg );
									throw new ProxlWebappDataException( msg );
								}
								psmValues.add( psmAnnotationDTO.getValueString() );
							}
						}

					}

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
	 * @param searchIds
	 * @param request
	 * @throws Exception
	 */
	public void authCheck(List<Integer> searchIds, HttpServletRequest request) throws Exception {
		if ( searchIds.isEmpty() ) {

			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
					.build()
					);
		}


		//   Get the project id for this search

		Set<Integer> searchIdsSet = new HashSet<Integer>( );

		searchIdsSet.addAll( searchIds );

		for ( int searchId : searchIds ) {

			searchIdsSet.add( searchId );
		}


		List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsSet );

		if ( projectIdsFromSearchIds.isEmpty() ) {

			// should never happen
			String msg = "No project ids for search ids: ";
			for ( int searchId : searchIds ) {

				msg += searchId + ", ";
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

		//  Test access to the project id

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
	}



}
