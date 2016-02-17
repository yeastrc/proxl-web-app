package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collections;
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
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
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
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinCrosslinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinLooplinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinMonolinkSearcher;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortOnBestAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortOnBestAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchIdResult;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.DeserializeCutoffForWebservices;


@Path("/data")
public class ProteinsService {

	private static final Logger log = Logger.getLogger(ProteinsService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCrosslinkProteinsPerSearchIdsProteinIdsPositions") 
	public ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult getCrosslinkProteins( 
			@QueryParam( "search_ids" ) List<Integer> searchIds,
			@QueryParam( "psmPeptideCutoffsForSearchIds" ) String psmPeptideCutoffsForSearchIds_JSONString,
			@QueryParam( "protein_1_id" ) Integer protein1Id,
			@QueryParam( "protein_2_id" ) Integer protein2Id,
			@QueryParam( "protein_1_position" ) Integer protein1Position,
			@QueryParam( "protein_2_position" ) Integer protein2Position,
			@Context HttpServletRequest request )
	throws Exception {
		

		ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult webserviceResult = new ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult();

		
		
		if ( searchIds == null || searchIds.isEmpty() ) {

			String msg = "Provided search_ids is null or search_ids is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		if ( StringUtils.isEmpty( psmPeptideCutoffsForSearchIds_JSONString ) ) {

			String msg = "Provided psmPeptideCutoffsForSearchIds is null or psmPeptideCutoffsForSearchIds is missing";

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


//			if ( searchIds.isEmpty() ) {
//				
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}

			
			//   Get the project id for this search
			
			Set<Integer> searchIdsSet = new HashSet<Integer>( searchIds );

			
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
			


			////////   Auth complete

			//////////////////////////////////////////

			
			
			
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = getSearcherCutoffValuesRootLevel( psmPeptideCutoffsForSearchIds_JSONString, searchIdsSet );
			
			
			Collections.sort( searchIds );

		
			for ( Integer searchId : searchIds ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {

					String msg = "Provided search_id " + searchId + " is not found in database.";

					log.error( msg );

				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );

				if ( searcherCutoffValuesSearchLevel == null ) {

					String msg = "Provided search_id " + searchId + " is not found in psm peptide cutoffs.";

					log.error( msg );

				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				NRProteinDTO nrProteinDTO_1 = new NRProteinDTO();
				nrProteinDTO_1.setNrseqId( protein1Id );
				
				NRProteinDTO nrProteinDTO_2 = new NRProteinDTO();
				nrProteinDTO_2.setNrseqId( protein2Id );
			
				SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper =
						SearchProteinCrosslinkSearcher.getInstance().search( search, 
								searcherCutoffValuesSearchLevel, 
								nrProteinDTO_1,
								nrProteinDTO_2,
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
						
						SortOnBestAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchIdResult sortResultGetHeaders =
								SortOnBestAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId.getInstance()
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
						
						item.setSearchId( searchId );
						item.setSearchName( search.getName() );
						
						item.setNumPeptides( searchProteinCrosslink.getNumLinkedPeptides() );
						item.setNumUniquePeptides( searchProteinCrosslink.getNumUniqueLinkedPeptides() );
						item.setNumPsms( searchProteinCrosslink.getNumPsms() );
						
						item.setPsmAnnotationValueList( searchProteinCrosslinkWrapper.getPsmAnnotationValueList() );
						item.setPeptideAnnotationValueList( searchProteinCrosslinkWrapper.getPeptideAnnotationValueList() );
						
						webserviceResult.addEntryToProteinsPerSearchIdMap( searchId, entry );
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
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getLooplinkProteinsPerSearchIdsProteinIdsPositions") 
	public ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult getLooplinkProteins( 
			@QueryParam( "search_ids" ) List<Integer> searchIds,
			@QueryParam( "psmPeptideCutoffsForSearchIds" ) String psmPeptideCutoffsForSearchIds_JSONString,
			@QueryParam( "protein_id" ) Integer proteinId,
			@QueryParam( "protein_position_1" ) Integer proteinPosition1,
			@QueryParam( "protein_position_2" ) Integer proteinPosition2,
			@Context HttpServletRequest request )
	throws Exception {
		
		

		ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult webserviceResult = new ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult();


		if ( searchIds == null || searchIds.isEmpty() ) {

			String msg = "Provided search_ids is null or search_ids is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}

		if ( StringUtils.isEmpty( psmPeptideCutoffsForSearchIds_JSONString ) ) {

			String msg = "Provided psmPeptideCutoffsForSearchIds is null or psmPeptideCutoffsForSearchIds is missing";

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


//			if ( searchIds.isEmpty() ) {
//				
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}

			
			//   Get the project id for this search
			
			Set<Integer> searchIdsSet = new HashSet<Integer>( searchIds );

			
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
			


			////////   Auth complete

			//////////////////////////////////////////

			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = getSearcherCutoffValuesRootLevel( psmPeptideCutoffsForSearchIds_JSONString, searchIdsSet );
			
			
			Collections.sort( searchIds );
			

			for ( Integer searchId : searchIds ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {

					String msg = "Provided search_id " + searchId + " is not found in database.";

					log.error( msg );

				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );

				if ( searcherCutoffValuesSearchLevel == null ) {

					String msg = "Provided search_id " + searchId + " is not found in psm peptide cutoffs.";

					log.error( msg );

				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				NRProteinDTO nrProteinDTO = new NRProteinDTO();
				nrProteinDTO.setNrseqId( proteinId );
				
				SearchProteinLooplinkWrapper searchProteinLooplinkWrapper = SearchProteinLooplinkSearcher.getInstance().search( search, 
						searcherCutoffValuesSearchLevel, 
						 nrProteinDTO,
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
						
						SortOnBestAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchIdResult sortResultGetHeaders =
								SortOnBestAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId.getInstance()
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
						
						item.setSearchId( searchId );
						item.setSearchName( search.getName() );
						
						item.setNumPeptides( searchProteinLooplink.getNumPeptides() );
						item.setNumUniquePeptides( searchProteinLooplink.getNumUniquePeptides() );
						item.setNumPsms( searchProteinLooplink.getNumPsms() );
						
						item.setPsmAnnotationValueList( searchProteinLooplinkWrapper.getPsmAnnotationValueList() );
						item.setPeptideAnnotationValueList( searchProteinLooplinkWrapper.getPeptideAnnotationValueList() );
						
						webserviceResult.addEntryToProteinsPerSearchIdMap( searchId, entry );
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


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getMonolinkProteinsPerSearchIdsProteinIdsPositions") 
	public ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult getMonolinkProteins( 
			@QueryParam( "search_ids" ) List<Integer> searchIds,
			@QueryParam( "psmPeptideCutoffsForSearchIds" ) String psmPeptideCutoffsForSearchIds_JSONString,
			@QueryParam( "protein_id" ) Integer proteinId,
			@QueryParam( "protein_position" ) Integer proteinPosition,
			@Context HttpServletRequest request )
	throws Exception {
		

		ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult webserviceResult = new ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult();

		
		
		if ( searchIds == null || searchIds.isEmpty() ) {

			String msg = "Provided search_ids is null or search_ids is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		if ( StringUtils.isEmpty( psmPeptideCutoffsForSearchIds_JSONString ) ) {

			String msg = "Provided psmPeptideCutoffsForSearchIds is null or psmPeptideCutoffsForSearchIds is missing";

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
			
			Set<Integer> searchIdsSet = new HashSet<Integer>( searchIds );

			
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
			


			////////   Auth complete

			//////////////////////////////////////////

			
			
			
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = getSearcherCutoffValuesRootLevel( psmPeptideCutoffsForSearchIds_JSONString, searchIdsSet );
			
			
			Collections.sort( searchIds );

		
			for ( Integer searchId : searchIds ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {

					String msg = "Provided search_id " + searchId + " is not found in database.";

					log.error( msg );

				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );

				if ( searcherCutoffValuesSearchLevel == null ) {

					String msg = "Provided search_id " + searchId + " is not found in psm peptide cutoffs.";

					log.error( msg );

				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				NRProteinDTO nrProteinDTO = new NRProteinDTO();
				nrProteinDTO.setNrseqId( proteinId );
				
			
				SearchProteinMonolinkWrapper searchProteinMonolinkWrapper =
						SearchProteinMonolinkSearcher.getInstance().searchOnSearch( search, 
								searcherCutoffValuesSearchLevel, 
								nrProteinDTO,
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
						
						SortOnBestAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchIdResult sortResultGetHeaders =
								SortOnBestAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId.getInstance()
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
						
						item.setSearchId( searchId );
						item.setSearchName( search.getName() );
						
						item.setNumPeptides( searchProteinMonolink.getNumPeptides() );
						item.setNumUniquePeptides( searchProteinMonolink.getNumUniquePeptides() );
						item.setNumPsms( searchProteinMonolink.getNumPsms() );
						
						item.setPsmAnnotationValueList( searchProteinMonolinkWrapper.getPsmAnnotationValueList() );
						item.setPeptideAnnotationValueList( searchProteinMonolinkWrapper.getPeptideAnnotationValueList() );
						
						webserviceResult.addEntryToProteinsPerSearchIdMap( searchId, entry );
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
	
	
	///////////////////////////////////////////////////////
	
	
	private SearcherCutoffValuesRootLevel getSearcherCutoffValuesRootLevel( String psmPeptideCutoffsForSearchIds_JSONString, Set<Integer> searchIdsSet ) throws Exception {
		

		CutoffValuesRootLevel cutoffValuesRootLevel = 
				DeserializeCutoffForWebservices.getInstance().deserialize_JSON_ToCutoffRoot( psmPeptideCutoffsForSearchIds_JSONString );



		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( 
						searchIdsSet, 
						cutoffValuesRootLevel ); 
		
		
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		

		return searcherCutoffValuesRootLevel;
	}
	
	
}
