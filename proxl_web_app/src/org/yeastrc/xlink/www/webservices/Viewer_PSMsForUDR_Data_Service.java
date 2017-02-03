package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.SearchPeptideCommonLinkAnnDataWrapperIF;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmAnnotationDataSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideCrosslink_LinkedPosition_Searcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideLooplink_LinkedPosition_Searcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 * Not Currently Used.  !!! Not Tested  !!!
 * 
 * Get the PSMs for a given UDR
 * 
 * One method for crosslink and one method for looplink
 *
 */
@Path("/imageViewer")
public class Viewer_PSMsForUDR_Data_Service {
	
//	private static final Logger log = Logger.getLogger(Viewer_PSMsForUDR_Data_Service.class);
//	
//	public static class Viewer_UDR_Data_Service_Result {
//		private Map<Integer,Viewer_UDR_Data_Service_Single_Search_Result> dataForSearches;
//		public Map<Integer, Viewer_UDR_Data_Service_Single_Search_Result> getDataForSearches() {
//			return dataForSearches;
//		}
//		public void setDataForSearches(Map<Integer, Viewer_UDR_Data_Service_Single_Search_Result> dataForSearches) {
//			this.dataForSearches = dataForSearches;
//		}
//	}
//	public static class Viewer_UDR_Data_Service_Single_Search_Result {
//		private List<Viewer_PSM_UDR_Data_Service_Result_Item> udrItemList;
//		private List<String> psmValuesNames;
//		public List<Viewer_PSM_UDR_Data_Service_Result_Item> getUdrItemList() {
//			return udrItemList;
//		}
//		public void setUdrItemList(List<Viewer_PSM_UDR_Data_Service_Result_Item> udrItemList) {
//			this.udrItemList = udrItemList;
//		}
//		public List<String> getPsmValuesNames() {
//			return psmValuesNames;
//		}
//		public void setPsmValuesNames(List<String> psmValuesNames) {
//			this.psmValuesNames = psmValuesNames;
//		}
//	}
//	public static class Viewer_PSM_UDR_Data_Service_Result_Item {
//		private int psmId;
//		private List<String> psmValues;
//		public int getPsmId() {
//			return psmId;
//		}
//		public void setPsmId(int psmId) {
//			this.psmId = psmId;
//		}
//		public List<String> getPsmValues() {
//			return psmValues;
//		}
//		public void setPsmValues(List<String> psmValues) {
//			this.psmValues = psmValues;
//		}
//	}
//	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/getPsmUDRDataCrosslink") 
//	public Viewer_UDR_Data_Service_Result getViewerDataCrosslink(
//			@QueryParam( "searchIds" ) List<Integer> projectSearchIdList,
//			@QueryParam( "protSeqId_1" ) Integer protSeqId_1,
//			@QueryParam( "prot_1_position" ) Integer prot_1_position,
//			@QueryParam( "protSeqId_2" ) Integer protSeqId_2,
//			@QueryParam( "prot_2_position" ) Integer prot_2_position,
//			@Context HttpServletRequest request )
//	throws Exception {
//		
//		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
//			String msg = "Provided searchIds is null or empty";
//			log.error( msg );
//		    throw new WebApplicationException(
//		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity( msg )
//		    	        .build()
//		    	        );
//		}
//		if ( protSeqId_1 == null ) {
//			String msg = "Provided protSeqId_1 is null or missing";
//			log.error( msg );
//		    throw new WebApplicationException(
//		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity( msg ).build() );
//		}
//		if ( prot_1_position == null ) {
//			String msg = "Provided prot_1_position is null or missing";
//			log.error( msg );
//		    throw new WebApplicationException(
//		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity( msg ).build() );
//		}
//		if ( protSeqId_2 == null ) {
//			String msg = "Provided protSeqId_2 is null or missing";
//			log.error( msg );
//		    throw new WebApplicationException(
//		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity( msg ).build() );
//		}
//		if ( prot_2_position == null ) {
//			String msg = "Provided prot_2_position is null or missing";
//			log.error( msg );
//		    throw new WebApplicationException(
//		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity( msg ).build() );
//		}
//		return getViewerDataInternal( projectSearchIdList, protSeqId_1, prot_1_position, protSeqId_2, prot_2_position, request );
//	}
//	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Path("/getPsmUDRDataLooplink") 
//	public Viewer_UDR_Data_Service_Result getViewerDataLooplink(
//			@QueryParam( "searchIds" ) List<Integer> projectSearchIdList,
//			@QueryParam( "protSeqId" ) Integer protSeqId_1,
//			@QueryParam( "prot_position_1" ) Integer prot_1_position,
//			@QueryParam( "prot_position_2" ) Integer prot_2_position,
//			@Context HttpServletRequest request )
//	throws Exception {
//		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
//			String msg = "Provided searchIds is null or empty";
//			log.error( msg );
//		    throw new WebApplicationException(
//		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity( msg )
//		    	        .build()
//		    	        );
//		}
//		if ( protSeqId_1 == null ) {
//			String msg = "Provided protSeqId is null or missing";
//			log.error( msg );
//		    throw new WebApplicationException(
//		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity( msg ).build() );
//		}
//		if ( prot_1_position == null ) {
//			String msg = "Provided prot_position_1 is null or missing";
//			log.error( msg );
//		    throw new WebApplicationException(
//		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity( msg ).build() );
//		}
//		if ( prot_2_position == null ) {
//			String msg = "Provided prot_position_2 is null or missing";
//			log.error( msg );
//		    throw new WebApplicationException(
//		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity( msg ).build() );
//		}
//		return getViewerDataInternal( projectSearchIdList, protSeqId_1, prot_1_position, null /* protSeqId_2 */, prot_2_position, request);
//	}
//	
//	/**
//	 * @param projectSearchIdList
//	 * @param protSeqId_1
//	 * @param prot_1_position
//	 * @param protSeqId_2
//	 * @param prot_2_position
//	 * @param request
//	 * @return
//	 * @throws Exception
//	 */
//	private Viewer_UDR_Data_Service_Result getViewerDataInternal(
//			List<Integer> projectSearchIdList,
//			Integer protSeqId_1,
//			Integer prot_1_position,
//			Integer protSeqId_2,
//			Integer prot_2_position,
//			HttpServletRequest request )
//	throws Exception {
//		try {
//			// Get the session first.  
////			HttpSession session = request.getSession();
//			if ( projectSearchIdList.isEmpty() ) {
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
//			//   Get the project id for this search
//		//   Get the project id for this search
//			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
//			projectSearchIdsSet.addAll( projectSearchIdList );
//			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
//			if ( projectIdsFromSearchIds.isEmpty() ) {
//				// should never happen
//				String msg = "No project ids for search ids: ";
//				for ( int projectSearchId : projectSearchIdList ) {
//					msg += projectSearchId + ", ";
//				}				
//				log.error( msg );
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
//			if ( projectIdsFromSearchIds.size() > 1 ) {
//				//  Invalid request, searches across projects
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
//			int projectId = projectIdsFromSearchIds.get( 0 );
//			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
//					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
////			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
//			if ( accessAndSetupWebSessionResult.isNoSession() ) {
//				//  No User session 
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
//			//  Test access to the project id
//			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
//			//  Test access to the project id
//			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
//				//  No Access Allowed for this project id
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
//			////////   Auth complete
//			//////////////////////////////////////////
//
//			////////   Generic Param processing
//			Viewer_UDR_Data_Service_Result viewer_UDR_Data_Service_Result = new Viewer_UDR_Data_Service_Result();
//			Map<Integer,Viewer_UDR_Data_Service_Single_Search_Result> dataForSearches = new HashMap<>();
//			viewer_UDR_Data_Service_Result.dataForSearches = dataForSearches;
//
//			List<SearchDTO> searches = new ArrayList<SearchDTO>();
//			Set<Integer> searchIds = new HashSet<>();
//			for( int projectSearchId : projectSearchIdsSet ) {
//				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
//				if ( search == null ) {
//					String msg = "search id '" + projectSearchId + "' not found in the database. User taken to home page.";
//					log.warn( msg );
//					//  Search not found, the data on the page they are requesting does not exist.
//					//  The data on the user's previous page no longer reflects what is in the database.
//					throw new WebApplicationException(
//							Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
//							.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
//							.build()
//							);
//				}
//				searches.add( search );
//				searchIds.add( search.getSearchId() );
//			}
//			// Sort searches list
//			Collections.sort( searches, new Comparator<SearchDTO>() {
//				@Override
//				public int compare(SearchDTO o1, SearchDTO o2) {
//					return o1.getProjectSearchId() - o2.getProjectSearchId();
//				}
//			});
//			
//			//  Get Annotation Type records for PSM and Peptide
//			//  Get  Annotation Type records for PSM
//			Map<Integer, Map<Integer, AnnotationTypeDTO>> 
//			srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap =
//					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIds );
//			
//			//  Create empty searcherCutoffValuesSearchLevel so returns everything
//			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
//			
//			for ( SearchDTO searchDTO : searches ) {
////				Integer projectSearchId = searchDTO.getProjectSearchId();
//				Integer searchId = searchDTO.getSearchId();
//
//				Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOMap = 
//						srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );
//				if ( srchPgm_Filterable_Psm_AnnotationType_DTOMap == null ) {
//					//  No records were found, probably an error   TODO
//					srchPgm_Filterable_Psm_AnnotationType_DTOMap = new HashMap<>();
//				}
//				List<AnnotationTypeDTO> annotationTypesOrderByNameList = new ArrayList<>( srchPgm_Filterable_Psm_AnnotationType_DTOMap.size() );
//				Set<Integer> annotationTypeIdsForGettingAnnotationData = new HashSet<>();
//				for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgm_Filterable_Psm_AnnotationType_DTOMap.entrySet() ) {
//					annotationTypesOrderByNameList.add( entry.getValue() );
//					annotationTypeIdsForGettingAnnotationData.add( entry.getKey() );
//				}
//				Collections.sort( annotationTypesOrderByNameList, new Comparator<AnnotationTypeDTO>() {
//					@Override
//					public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {
//						return o1.getName().compareToIgnoreCase( o2.getName() );
//					}
//				});
//				List<String> psmValuesNames = new ArrayList<>();
//				for ( AnnotationTypeDTO annotationTypeDTO : annotationTypesOrderByNameList ) {
//					psmValuesNames.add( annotationTypeDTO.getName() );
//				}
//				//  Output Result Data
//				Viewer_UDR_Data_Service_Single_Search_Result single_Search_Result = new Viewer_UDR_Data_Service_Single_Search_Result();
//				dataForSearches.put( searchId, single_Search_Result );
//				List<Viewer_PSM_UDR_Data_Service_Result_Item> udrItemList = new ArrayList<>();
//				single_Search_Result.setUdrItemList( udrItemList );
//				single_Search_Result.setPsmValuesNames( psmValuesNames );
//				List<? extends SearchPeptideCommonLinkAnnDataWrapperIF> reportedPeptideList = null;
//				if ( protSeqId_2 != null ) {
//					//  Get peptides for these parameters
//					reportedPeptideList = 
//							SearchPeptideCrosslink_LinkedPosition_Searcher.getInstance()
//							.searchOnSearchProteinCrosslink( 
//									searchDTO, 
//									searcherCutoffValuesSearchLevel, // empty so retrieving everything 
//									protSeqId_1, 
//									protSeqId_2, 
//									prot_1_position, 
//									prot_2_position );
//				} else {
//				//  Get peptides for these parameters
//					reportedPeptideList = 
//							SearchPeptideLooplink_LinkedPosition_Searcher.getInstance()
//							.searchOnSearchProteinLooplink( searchDTO, searcherCutoffValuesSearchLevel, protSeqId_1, prot_1_position, prot_2_position );
//				}
//				for ( SearchPeptideCommonLinkAnnDataWrapperIF reportedPeptideEntry : reportedPeptideList ) {
//					//  Get PSM data for each reported peptide
//					List<PsmWebDisplayWebServiceResult> psmWebDisplayList = 
//							PsmWebDisplaySearcher.getInstance()
//							.getPsmsWebDisplay( searchId, 
//									reportedPeptideEntry.getReportedPeptideId(), 
//									searcherCutoffValuesSearchLevel // empty so retrieving everything  
//									);
//					// Process PSMs
//					for ( PsmWebDisplayWebServiceResult psmWebDisplayEntry :psmWebDisplayList ) {
//						Viewer_PSM_UDR_Data_Service_Result_Item udrItem = new Viewer_PSM_UDR_Data_Service_Result_Item();
//						udrItemList.add( udrItem );
//						List<String> psmValues = new ArrayList<>();
//						udrItem.setPsmValues( psmValues );
//						udrItem.setPsmId( psmWebDisplayEntry.getPsmDTO().getId() );
//						// For each PSM, get the annotation data for it
//						List<PsmAnnotationDTO> psmAnnotationDataList = 
//								PsmAnnotationDataSearcher.getInstance()
//								.getPsmAnnotationDTOList( 
//										psmWebDisplayEntry.getPsmDTO().getId(), 
//										annotationTypeIdsForGettingAnnotationData );
//						// Transfer to map for lookup by ann type id
//						Map<Integer,PsmAnnotationDTO> psmAnnotationDataMap = new HashMap<>();
//						for ( PsmAnnotationDTO item : psmAnnotationDataList ) {
//							psmAnnotationDataMap.put( item.getAnnotationTypeId(), item );
//						}
//						//  Copy to output by ann type id
//						for ( AnnotationTypeDTO annotationType : annotationTypesOrderByNameList ) {
//							PsmAnnotationDTO psmAnnotationDTO = psmAnnotationDataMap.get( annotationType.getId() );
//							if ( psmAnnotationDTO == null ) {
//								String msg = "No data for ann type id: " + annotationType.getId() 
//									+ ", for PSM id: " + psmWebDisplayEntry.getPsmDTO().getId();
//								log.error( msg );
//								throw new ProxlWebappDataException( msg );
//							}
//							psmValues.add( psmAnnotationDTO.getValueString() );
//						}
//					}
//				}
//			}
//			return viewer_UDR_Data_Service_Result;
//		} catch ( WebApplicationException e ) {
//			throw e;
//		} catch ( Exception e ) {
//			String msg = "Exception caught: " + e.toString();
//			log.error( msg, e );
//			throw e;
//		}
//	}
}
