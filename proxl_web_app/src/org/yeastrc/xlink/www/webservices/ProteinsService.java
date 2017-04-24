package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
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
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.linked_positions.CrosslinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.LooplinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.MonolinkLinkedPositions;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult;
import org.yeastrc.xlink.www.objects.ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry;
import org.yeastrc.xlink.www.objects.ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;
import org.yeastrc.xlink.www.objects.SearchProteinMonolinkWrapper;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.actions.ProteinsAllCommonAll;
import org.yeastrc.xlink.www.actions.ProteinsAllCommonAll.ProteinSingleEntry;
import org.yeastrc.xlink.www.actions.ProteinsAllCommonAll.ProteinsAllCommonAllResult;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.DeserializeCutoffForWebservices;



@Path("/data")
public class ProteinsService {
	
	private static final Logger log = Logger.getLogger(ProteinsService.class);
	
	/**
	 * @param projectSearchIdList
	 * @param psmPeptideCutoffsForSearchIds_JSONString
	 * @param protein1Id
	 * @param protein2Id
	 * @param protein1Position
	 * @param protein2Position
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCrosslinkProteinsPerSearchIdsProteinIdsPositions") 
	public ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult getCrosslinkProteins( 
			@QueryParam( "project_search_ids" ) List<Integer> projectSearchIdList,
			@QueryParam( "psmPeptideCutoffsForProjectSearchIds" ) String psmPeptideCutoffsForProjectSearchIds_JSONString,
			@QueryParam( "protein_1_id" ) Integer protein1Id,
			@QueryParam( "protein_2_id" ) Integer protein2Id,
			@QueryParam( "protein_1_position" ) Integer protein1Position,
			@QueryParam( "protein_2_position" ) Integer protein2Position,
			@Context HttpServletRequest request )
	throws Exception {
		
		ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult webserviceResult = new ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult();
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided project_search_ids is null or project_search_ids is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForProjectSearchIds_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForProjectSearchIds is null or psmPeptideCutoffsForProjectSearchIds is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		///////////////////////
		if ( protein1Id == null ) {
			String msg = "Provided protein_1_id is null or protein_1_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( protein2Id == null ) {
			String msg = "Provided protein_2_id is null or protein_2_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( protein1Position == null ) {
			String msg = "Provided protein_1_position is null or protein_1_position is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( protein2Position == null ) {
			String msg = "Provided protein_2_position is null or protein_2_position is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.addAll( projectSearchIdList );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchIds: ";
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
			
			////////   Auth complete
			//////////////////////////////////////////

			Set<Integer> searchIdsSet = new HashSet<>( projectSearchIdsSet.size() );
			List<SearchDTO> searchList = new ArrayList<>( projectSearchIdsSet.size() );
			
			for ( Integer projectSearchId : projectSearchIdsSet ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = ": No search found for projectSearchId: " + projectSearchId;
					log.warn( msg );
				    throw new WebApplicationException(
				    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
				    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
				    	        .build()
				    	        );
				}
				Integer searchId = search.getSearchId();
				searchIdsSet.add( searchId );
				searchList.add( search );
			}
			
			Collections.sort( searchList, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getProjectSearchId() - o2.getProjectSearchId();
				}
			});
			
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = getSearcherCutoffValuesRootLevel( psmPeptideCutoffsForProjectSearchIds_JSONString, searchIdsSet );

			for ( SearchDTO search : searchList ) {
				int projectSearchId = search.getProjectSearchId();
				int searchId = search.getSearchId();
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
				if ( searcherCutoffValuesSearchLevel == null ) {
					String msg = "Provided search_id " + projectSearchId + " is not found in psm peptide cutoffs.";
					log.error( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				ProteinSequenceObject ProteinSequenceObject_1 = new ProteinSequenceObject();
				ProteinSequenceObject_1.setProteinSequenceId( protein1Id );
				ProteinSequenceObject ProteinSequenceObject_2 = new ProteinSequenceObject();
				ProteinSequenceObject_2.setProteinSequenceId( protein2Id );

				SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper =
						CrosslinkLinkedPositions.getInstance()
						.getSearchProteinCrosslinkWrapperForSearchCutoffsProtIdsPositions(
								search, 
								searcherCutoffValuesSearchLevel, 
								ProteinSequenceObject_1,
								ProteinSequenceObject_2,
								protein1Position,
								protein2Position
								);
				if( searchProteinCrosslinkWrapper != null ) {
					SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					if( searchProteinCrosslink != null ) {
						//  Create list of Best Peptide annotation names to display as column headers
						List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
								searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
						final List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList = new ArrayList<>( peptideCutoffValuesList.size() );
						for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : peptideCutoffValuesList ) {
							peptideCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
						}
						//  Create list of 
						List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
								searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
						final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );
						for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {
							psmCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
						}
						///////////////////
						///////////
						List<SearchProteinCrosslinkWrapper>  searchProteinCrosslinkWrapperList = new ArrayList<>( 1 );
						searchProteinCrosslinkWrapperList.add(searchProteinCrosslinkWrapper);
						SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt sortResultGetHeaders =
								SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
								.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId( 
										searchId, 
										searchProteinCrosslinkWrapperList, 
										peptideCutoffsAnnotationTypeDTOList, 
										psmCutoffsAnnotationTypeDTOList );
						ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry entry = new ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry();
						List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList = 
								sortResultGetHeaders.getPeptideAnnotationDisplayNameDescriptionList();
						List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList = 
								sortResultGetHeaders.getPsmAnnotationDisplayNameDescriptionList();
						List<ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult> proteins = new ArrayList<>();
						entry.setPeptideAnnotationDisplayNameDescriptionList( peptideAnnotationDisplayNameDescriptionList );
						entry.setPsmAnnotationDisplayNameDescriptionList( psmAnnotationDisplayNameDescriptionList );
						entry.setProteins( proteins );
						ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult item = new ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult();
						proteins.add( item );
						item.setProjectSearchId( projectSearchId );
						item.setSearchName( search.getName() );
						item.setNumPeptides( searchProteinCrosslink.getNumLinkedPeptides() );
						item.setNumUniquePeptides( searchProteinCrosslink.getNumUniqueLinkedPeptides() );
						item.setNumPsms( searchProteinCrosslink.getNumPsms() );
						item.setPsmAnnotationValueList( searchProteinCrosslinkWrapper.getPsmAnnotationValueList() );
						item.setPeptideAnnotationValueList( searchProteinCrosslinkWrapper.getPeptideAnnotationValueList() );
						webserviceResult.addEntryToProteinsPerProjectSearchIdMap( projectSearchId, entry );
					}
				}
			}
			return webserviceResult;
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
	
	/////////////////////////////////////////////////////
	/**
	 * @param projectSearchIdList
	 * @param psmPeptideCutoffsForProjectSearchIds_JSONString
	 * @param proteinId
	 * @param proteinPosition1
	 * @param proteinPosition2
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getLooplinkProteinsPerSearchIdsProteinIdsPositions") 
	public ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult getLooplinkProteins( 
			@QueryParam( "project_search_ids" ) List<Integer> projectSearchIdList,
			@QueryParam( "psmPeptideCutoffsForProjectSearchIds" ) String psmPeptideCutoffsForProjectSearchIds_JSONString,
			@QueryParam( "protein_id" ) Integer proteinId,
			@QueryParam( "protein_position_1" ) Integer proteinPosition1,
			@QueryParam( "protein_position_2" ) Integer proteinPosition2,
			@Context HttpServletRequest request )
	throws Exception {
		ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult webserviceResult = new ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult();
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided project_search_ids is null or project_search_ids is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForProjectSearchIds_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForProjectSearchIds is null or psmPeptideCutoffsForProjectSearchIds is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		///////////////////////
		if ( proteinId == null ) {
			String msg = "Provided protein_id is null or protein_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( proteinPosition1 == null ) {
			String msg = "Provided protein_position_1 is null or protein_position_1 is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( proteinPosition2 == null ) {
			String msg = "Provided protein_position_2 is null or protein_position_2 is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
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
			
			////////   Auth complete
			//////////////////////////////////////////

			Set<Integer> searchIdsSet = new HashSet<>( projectSearchIdsSet.size() );
			List<SearchDTO> searchList = new ArrayList<>( projectSearchIdsSet.size() );
			
			for ( Integer projectSearchId : projectSearchIdsSet ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = ": No search found for projectSearchId: " + projectSearchId;
					log.warn( msg );
				    throw new WebApplicationException(
				    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
				    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
				    	        .build()
				    	        );
				}
				Integer searchId = search.getSearchId();
				searchIdsSet.add( searchId );
				searchList.add( search );
			}
			
			Collections.sort( searchList, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getProjectSearchId() - o2.getProjectSearchId();
				}
			});
			
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = getSearcherCutoffValuesRootLevel( psmPeptideCutoffsForProjectSearchIds_JSONString, searchIdsSet );

			for ( SearchDTO search : searchList ) {
				int projectSearchId = search.getProjectSearchId();
				int searchId = search.getSearchId();
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
				if ( searcherCutoffValuesSearchLevel == null ) {
					String msg = "Provided search_id " + projectSearchId + " is not found in psm peptide cutoffs.";
					log.error( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				ProteinSequenceObject ProteinSequenceObject = new ProteinSequenceObject();
				ProteinSequenceObject.setProteinSequenceId( proteinId );

				SearchProteinLooplinkWrapper searchProteinLooplinkWrapper =
						LooplinkLinkedPositions.getInstance()
						.getSearchProteinLooplinkWrapperForSearchCutoffsProtIdsPositions(
								search, 
								searcherCutoffValuesSearchLevel, 
								ProteinSequenceObject,
								proteinPosition1,
								proteinPosition2
								);
				if( searchProteinLooplinkWrapper != null ) {
					SearchProteinLooplink searchProteinLooplink = searchProteinLooplinkWrapper.getSearchProteinLooplink();
					if( searchProteinLooplink != null ) {
						//  Create list of Best Peptide annotation names to display as column headers
						List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
								searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
						final List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList = new ArrayList<>( peptideCutoffValuesList.size() );
						for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : peptideCutoffValuesList ) {
							peptideCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
						}
						//  Create list of 
						List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
								searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
						final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );
						for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {
							psmCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
						}
						///////////////////
						///////////
						List<SearchProteinLooplinkWrapper>  searchProteinLooplinkWrapperList = new ArrayList<>( 1 );
						searchProteinLooplinkWrapperList.add(searchProteinLooplinkWrapper);
						SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt sortResultGetHeaders =
								SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
								.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId( 
										searchId, 
										searchProteinLooplinkWrapperList, 
										peptideCutoffsAnnotationTypeDTOList, 
										psmCutoffsAnnotationTypeDTOList );
						ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry entry = new ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry();
						List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList = 
								sortResultGetHeaders.getPeptideAnnotationDisplayNameDescriptionList();
						List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList = 
								sortResultGetHeaders.getPsmAnnotationDisplayNameDescriptionList();
						List<ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult> proteins = new ArrayList<>();
						entry.setPeptideAnnotationDisplayNameDescriptionList( peptideAnnotationDisplayNameDescriptionList );
						entry.setPsmAnnotationDisplayNameDescriptionList( psmAnnotationDisplayNameDescriptionList );
						entry.setProteins( proteins );
						ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult item = new ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult();
						proteins.add( item );
						item.setProjectSearchId( projectSearchId );
						item.setSearchName( search.getName() );
						item.setNumPeptides( searchProteinLooplink.getNumPeptides() );
						item.setNumUniquePeptides( searchProteinLooplink.getNumUniquePeptides() );
						item.setNumPsms( searchProteinLooplink.getNumPsms() );
						item.setPsmAnnotationValueList( searchProteinLooplinkWrapper.getPsmAnnotationValueList() );
						item.setPeptideAnnotationValueList( searchProteinLooplinkWrapper.getPeptideAnnotationValueList() );
						webserviceResult.addEntryToProteinsPerProjectSearchIdMap( projectSearchId, entry );
					}
				}
			}
			return webserviceResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
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
	 * @param searchIds
	 * @param psmPeptideCutoffsForSearchIds_JSONString
	 * @param proteinId
	 * @param proteinPosition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getMonolinkProteinsPerSearchIdsProteinIdsPositions") 
	public ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult getMonolinkProteins( 
			@QueryParam( "project_search_ids" ) List<Integer> projectSearchIdList,
			@QueryParam( "psmPeptideCutoffsForProjectSearchIds" ) String psmPeptideCutoffsForProjectSearchIds_JSONString,
			@QueryParam( "protein_id" ) Integer proteinId,
			@QueryParam( "protein_position" ) Integer proteinPosition,
			@Context HttpServletRequest request )
	throws Exception {
		
		ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult webserviceResult = new ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult();
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided project_search_ids is null or project_search_ids is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForProjectSearchIds_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForProjectSearchIds is null or psmPeptideCutoffsForProjectSearchIds is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		///////////////////////
		if ( proteinId == null ) {
			String msg = "Provided protein_id is null or protein_1_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( proteinPosition == null ) {
			String msg = "Provided protein_position is null or protein_position is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
//			if ( searchIds.isEmpty() ) {
//				
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
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
			
			////////   Auth complete
			//////////////////////////////////////////

			Set<Integer> searchIdsSet = new HashSet<>( projectSearchIdsSet.size() );
			List<SearchDTO> searchList = new ArrayList<>( projectSearchIdsSet.size() );
			
			for ( Integer projectSearchId : projectSearchIdsSet ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = ": No search found for projectSearchId: " + projectSearchId;
					log.warn( msg );
				    throw new WebApplicationException(
				    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
				    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
				    	        .build()
				    	        );
				}
				Integer searchId = search.getSearchId();
				searchIdsSet.add( searchId );
				searchList.add( search );
			}
			
			Collections.sort( searchList, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getProjectSearchId() - o2.getProjectSearchId();
				}
			});
			
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = getSearcherCutoffValuesRootLevel( psmPeptideCutoffsForProjectSearchIds_JSONString, searchIdsSet );
			
			for ( SearchDTO search : searchList ) {
				int projectSearchId = search.getProjectSearchId();
				int searchId = search.getSearchId();
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
				if ( searcherCutoffValuesSearchLevel == null ) {
					String msg = "Provided search_id " + projectSearchId + " is not found in psm peptide cutoffs.";
					log.error( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				ProteinSequenceObject ProteinSequenceObject = new ProteinSequenceObject();
				ProteinSequenceObject.setProteinSequenceId( proteinId );

				SearchProteinMonolinkWrapper searchProteinMonolinkWrapper =
						MonolinkLinkedPositions.getInstance()
						.getSearchProteinMonolinkWrapperForSearchCutoffsProtIdsPositions(
								search, 
								searcherCutoffValuesSearchLevel, 
								ProteinSequenceObject,
								proteinPosition
								);
				if( searchProteinMonolinkWrapper != null ) {
					SearchProteinMonolink searchProteinMonolink = searchProteinMonolinkWrapper.getSearchProteinMonolink();
					if( searchProteinMonolink != null ) {
						//  Create list of Best Peptide annotation names to display as column headers
						List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
								searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
						final List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList = new ArrayList<>( peptideCutoffValuesList.size() );
						for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : peptideCutoffValuesList ) {
							peptideCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
						}
						//  Create list of 
						List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
								searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
						final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );
						for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {
							psmCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
						}
						///////////////////
						///////////
						List<SearchProteinMonolinkWrapper>  searchProteinMonolinkWrapperList = new ArrayList<>( 1 );
						searchProteinMonolinkWrapperList.add(searchProteinMonolinkWrapper);
						SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt sortResultGetHeaders =
								SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
								.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId( 
										searchId, 
										searchProteinMonolinkWrapperList, 
										peptideCutoffsAnnotationTypeDTOList, 
										psmCutoffsAnnotationTypeDTOList );
						ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry entry = new ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry();
						List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList = 
								sortResultGetHeaders.getPeptideAnnotationDisplayNameDescriptionList();
						List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList = 
								sortResultGetHeaders.getPsmAnnotationDisplayNameDescriptionList();
						List<ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult> proteins = new ArrayList<>();
						entry.setPeptideAnnotationDisplayNameDescriptionList( peptideAnnotationDisplayNameDescriptionList );
						entry.setPsmAnnotationDisplayNameDescriptionList( psmAnnotationDisplayNameDescriptionList );
						entry.setProteins( proteins );
						ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult item = new ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult();
						proteins.add( item );
						item.setProjectSearchId( projectSearchId );
						item.setSearchName( search.getName() );
						item.setNumPeptides( searchProteinMonolink.getNumPeptides() );
						item.setNumUniquePeptides( searchProteinMonolink.getNumUniquePeptides() );
						item.setNumPsms( searchProteinMonolink.getNumPsms() );
						item.setPsmAnnotationValueList( searchProteinMonolinkWrapper.getPsmAnnotationValueList() );
						item.setPeptideAnnotationValueList( searchProteinMonolinkWrapper.getPeptideAnnotationValueList() );
						webserviceResult.addEntryToProteinsPerProjectSearchIdMap( projectSearchId, entry );
					}
				}
			}
			return webserviceResult;
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
	

	
	/////////////////////////////////////////////////////
	/**
	 * @param projectSearchIdList
	 * @param psmPeptideCutoffsForProjectSearchIds_JSONString
	 * @param proteinId
	 * @param proteinPosition1
	 * @param proteinPosition2
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getProteinsAllPerSearchIdsProteinIds") 
	public ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult getProteinsAllTypes( 
			@QueryParam( "project_search_ids" ) List<Integer> projectSearchIdList,
			@QueryParam( "psmPeptideCutoffsForProjectSearchIds" ) String psmPeptideCutoffsForProjectSearchIds_JSONString,
			@QueryParam( "protein_id" ) Integer proteinId,
			@QueryParam( "link_type" ) List<String> linkTypesList,
			@Context HttpServletRequest request )
	throws Exception {
		
		String[] linkTypes = null; // populated from linkTypesList
		
		ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult webserviceResult = new ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult();
		
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided project_search_ids is null or project_search_ids is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForProjectSearchIds_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForProjectSearchIds is null or psmPeptideCutoffsForProjectSearchIds is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		///////////////////////
		if ( proteinId == null ) {
			String msg = "Provided protein_id is null or protein_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( linkTypesList != null && ( ! linkTypesList.isEmpty() ) ) {
			linkTypes = new String[ linkTypesList.size() ];
			int index = 0;
			for ( String linkType : linkTypesList ) {
				if ( ! ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) 
						|| PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType )
						|| PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkType ) ) ) {
					String msg = "linkType is invalid, linkType: " + linkType;
					log.warn( linkType );
					throw new WebApplicationException(
							Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
							.entity( msg )
							.build()
							);
				}
				linkTypes[ index ] = linkType;
				index++;
			}
		}

		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
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
			
			////////   Auth complete
			//////////////////////////////////////////

			Set<Integer> searchIdsSet = new HashSet<>( projectSearchIdsSet.size() );
			List<SearchDTO> searchList = new ArrayList<>( projectSearchIdsSet.size() );
			
			for ( Integer projectSearchId : projectSearchIdsSet ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = ": No search found for projectSearchId: " + projectSearchId;
					log.warn( msg );
				    throw new WebApplicationException(
				    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
				    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
				    	        .build()
				    	        );
				}
				Integer searchId = search.getSearchId();
				searchIdsSet.add( searchId );
				searchList.add( search );
			}
			
			Collections.sort( searchList, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getProjectSearchId() - o2.getProjectSearchId();
				}
			});
			
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = getSearcherCutoffValuesRootLevel( psmPeptideCutoffsForProjectSearchIds_JSONString, searchIdsSet );

			CutoffValuesRootLevel cutoffValuesRootLevel = 
					DeserializeCutoffForWebservices.getInstance().deserialize_JSON_ToCutoffRoot( psmPeptideCutoffsForProjectSearchIds_JSONString );

			//  Empty sets since nothing to exclude
			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
			Set<Integer> excludeProteinSequenceIds_Set_UserInput = new HashSet<>();
			
			ProteinQueryJSONRoot proteinQueryJSONRoot = new ProteinQueryJSONRoot();
			proteinQueryJSONRoot.setLinkTypes( linkTypes );
			proteinQueryJSONRoot.setCutoffs( cutoffValuesRootLevel );

			for ( SearchDTO search : searchList ) {
				int projectSearchId = search.getProjectSearchId();
				int searchId = search.getSearchId();
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
				if ( searcherCutoffValuesSearchLevel == null ) {
					String msg = "Provided search_id " + projectSearchId + " is not found in psm peptide cutoffs.";
					log.error( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				ProteinSequenceObject ProteinSequenceObject = new ProteinSequenceObject();
				ProteinSequenceObject.setProteinSequenceId( proteinId );

				ProteinsAllCommonAllResult proteinsAllCommonAllResult =
						ProteinsAllCommonAll.getInstance().getProteinSingleEntryList(
								proteinId, //  onlyReturnThisProteinSequenceId
								search, 
								searchId, 
								proteinQueryJSONRoot, 
								excludeTaxonomy_Ids_Set_UserInput, 
								excludeProteinSequenceIds_Set_UserInput, 
								searcherCutoffValuesSearchLevel );

				List<ProteinSingleEntry> proteinSingleEntryList = proteinsAllCommonAllResult.getProteinSingleEntryList();

				if ( proteinSingleEntryList.size() > 1 ) {
					String msg = "getProteinSingleEntryList(...) returned more than one entry.  Not Valid. searchId: " + searchId
							+ ", proteinId: " + proteinId;
					log.error( msg );
					throw new ProxlWebappDataException(msg);
				}
				
				if( ! proteinSingleEntryList.isEmpty() ) {
					ProteinSingleEntry proteinSingleEntry = proteinSingleEntryList.get( 0 );
					//  Create list of Best Peptide annotation names to display as column headers
					List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
							searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
					final List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList = new ArrayList<>( peptideCutoffValuesList.size() );
					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : peptideCutoffValuesList ) {
						peptideCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
					}
					//  Create list of 
					List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
							searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
					final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );
					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {
						psmCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
					}
					///////////////////
					///////////
					SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt sortResultGetHeaders =
							SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
							.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId( 
									searchId, 
									proteinSingleEntryList, 
									peptideCutoffsAnnotationTypeDTOList, 
									psmCutoffsAnnotationTypeDTOList );
					ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry entry = new ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry();
					List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList = 
							sortResultGetHeaders.getPeptideAnnotationDisplayNameDescriptionList();
					List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList = 
							sortResultGetHeaders.getPsmAnnotationDisplayNameDescriptionList();
					List<ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult> proteins = new ArrayList<>();
					entry.setPeptideAnnotationDisplayNameDescriptionList( peptideAnnotationDisplayNameDescriptionList );
					entry.setPsmAnnotationDisplayNameDescriptionList( psmAnnotationDisplayNameDescriptionList );
					entry.setProteins( proteins );
					ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult item = new ProteinCommonDataForPerSearchIdsProteinIdsPositionsResult();
					proteins.add( item );
					item.setProjectSearchId( projectSearchId );
					item.setSearchName( search.getName() );
					item.setNumPeptides( proteinSingleEntry.getNumPeptides() );
					item.setNumUniquePeptides( proteinSingleEntry.getNumUniquePeptides() );
					item.setNumPsms( proteinSingleEntry.getNumPsms() );
					item.setPsmAnnotationValueList( proteinSingleEntry.getPsmAnnotationValueList() );
					item.setPeptideAnnotationValueList( proteinSingleEntry.getPeptideAnnotationValueList() );
					webserviceResult.addEntryToProteinsPerProjectSearchIdMap( projectSearchId, entry );
				}
			}
			return webserviceResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
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
	
	
	
	///////////////////////////////////////////////////////
	/**
	 * @param psmPeptideCutoffsForProjectSearchIds_JSONString
	 * @param searchIdsSet
	 * @return
	 * @throws Exception
	 */
	private SearcherCutoffValuesRootLevel getSearcherCutoffValuesRootLevel( String psmPeptideCutoffsForProjectSearchIds_JSONString, Set<Integer> searchIdsSet ) throws Exception {
		CutoffValuesRootLevel cutoffValuesRootLevel = 
				DeserializeCutoffForWebservices.getInstance().deserialize_JSON_ToCutoffRoot( psmPeptideCutoffsForProjectSearchIds_JSONString );
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( 
						searchIdsSet, 
						cutoffValuesRootLevel ); 
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		return searcherCutoffValuesRootLevel;
	}
}
