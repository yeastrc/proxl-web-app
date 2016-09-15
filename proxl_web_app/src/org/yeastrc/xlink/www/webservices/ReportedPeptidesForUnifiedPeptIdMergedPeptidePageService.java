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
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult;
import org.yeastrc.xlink.www.objects.ReportedPeptidesForMergedPeptidePage;
import org.yeastrc.xlink.www.objects.ReportedPeptidesForMergedPeptidePageWrapper;
import org.yeastrc.xlink.www.objects.ReportedPeptidesPerSearchForMergedPeptidePageResult;
import org.yeastrc.xlink.www.objects.ReportedPeptidesPerSearchForMergedPeptidePageResultEntry;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideAnnotationDataSearcher;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortAnnotationDTORecords;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesAnnotationLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;




@Path("/data")
public class ReportedPeptidesForUnifiedPeptIdMergedPeptidePageService {

	private static final Logger log = Logger.getLogger(ReportedPeptidesForUnifiedPeptIdMergedPeptidePageService.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getReportedPeptidesForUnifiedPeptId") 
	public ReportedPeptidesPerSearchForMergedPeptidePageResult getViewerData( 

			@QueryParam( "search_ids" ) List<Integer> searchIdList,
			@QueryParam( "unified_reported_peptide_id" ) Integer unifiedReportedPeptideId,
			@QueryParam( "psmPeptideCutoffsForSearchIds" ) String psmPeptideCutoffsForSearchIds_JSONString,
			@Context HttpServletRequest request )
					throws Exception {

		if ( searchIdList == null || searchIdList.isEmpty() ) {

			String msg = "Provided searchIds is null or searchIds is missing or searchIds list is empty";

			log.error( msg );

			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}

		if ( unifiedReportedPeptideId == null ) {

			String msg = "Provided unified_reported_peptide_id is null or unified_reported_peptide_id is missing";

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

			Set<Integer> searchIdsCollection = new HashSet<Integer>( );


			searchIdsCollection.addAll( searchIdList );


			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );

			if ( projectIdsFromSearchIds.isEmpty() ) {

				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIdList ) {

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



			//   Get PSM and Peptide Cutoff data from JSON


			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization


			CutoffValuesRootLevel cutoffValuesRootLevel = null;

			try {
				cutoffValuesRootLevel = jacksonJSON_Mapper.readValue( psmPeptideCutoffsForSearchIds_JSONString, CutoffValuesRootLevel.class );
				
			} catch ( JsonParseException e ) {

				String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', JsonParseException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
				log.error( msg, e );
				throw e;

			} catch ( JsonMappingException e ) {

				String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', JsonMappingException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
				log.error( msg, e );
				throw e;

			} catch ( IOException e ) {

				String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', IOException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			}

			
			
			ReportedPeptidesPerSearchForMergedPeptidePageResult results =
					getPeptideData( cutoffValuesRootLevel, searchIdsCollection, unifiedReportedPeptideId );

			return results;


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
	 * @param cutoffValuesRootLevel
	 * @param searchIdsCollection
	 * @param unifiedReportedPeptideId
	 * @return
	 * @throws Exception
	 */
	private ReportedPeptidesPerSearchForMergedPeptidePageResult getPeptideData( 

			CutoffValuesRootLevel cutoffValuesRootLevel,
			Set<Integer> searchIdsSet,
			int unifiedReportedPeptideId ) throws Exception {


		Map<Integer, ReportedPeptidesPerSearchForMergedPeptidePageResultEntry> reportedPeptidesPerSearchIdMap = new HashMap<>();

		/////////////

		//  Get  Annotation Type records for Reported Peptides and PSMs

		Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgmFilterableReportedPeptideAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsSet );


		//    Descriptive annotations for reported Peptides


		Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgmDescriptiveReportedPeptideAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Descriptive_ForSearchIds( searchIdsSet );



		//  Get  Annotation Type records for PSM

		//    Filterable annotations

		Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgmFilterablePsmAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsSet );



		List<Integer> searchIdsList = new ArrayList<>( searchIdsSet );

		Collections.sort( searchIdsList );

		//  Process for each search id:

		for ( Integer eachSearchIdToProcess : searchIdsList ) {


			ReportedPeptidesPerSearchForMergedPeptidePageResultEntry serviceResultEntry = new ReportedPeptidesPerSearchForMergedPeptidePageResultEntry();

			List<ReportedPeptidesForMergedPeptidePage> reportedPepidesListOutput = new ArrayList<>();

			
			//   Get Annotation Type records for the Search Id being processed in this loop 
			
			//    Peptide Filterable
			
			Map<Integer, AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = 
					srchPgmFilterableReportedPeptideAnnotationTypeDTOListPerSearchIdMap.get( eachSearchIdToProcess );


			//  Peptide Descriptive

			Map<Integer, AnnotationTypeDTO> srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap = 
					srchPgmDescriptiveReportedPeptideAnnotationTypeDTOListPerSearchIdMap.get( eachSearchIdToProcess );


			//  PSM filterable

			Map<Integer, AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOMap = 
					srchPgmFilterablePsmAnnotationTypeDTOListPerSearchIdMap.get( eachSearchIdToProcess );

			
			
			//  For each of annotation type data, if they are null, create empty hash map 
			
			if ( srchPgmFilterablePsmAnnotationTypeDTOMap == null ) {

				//  No records were found, probably an error   TODO

				srchPgmFilterablePsmAnnotationTypeDTOMap = new HashMap<>();
			}
			
			
			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap == null ) {

				//  No records were found, allowable for Reported Peptides

				srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = new HashMap<>();
			}
			

			if ( srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap == null ) {

				//  No records were found, Probably an error, unless no peptide data

				srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap = new HashMap<>();
			}



			Set<Integer> peptideAnnotationTypeIdsForAnnotationDataRetrieval = new HashSet<>(); 


			/////////////
			
			//   Create Peptide Annotation List for Sort Order and sort it on Sort order
			

			final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_SortOrder_List =
					new ArrayList<>( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap.size() );
			
			for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgmFilterableReportedPeptideAnnotationTypeDTOMap.entrySet() ) {
				
				AnnotationTypeDTO item = entry.getValue();
				
				peptideAnnotationTypeIdsForAnnotationDataRetrieval.add( item.getId() );
				
				if ( item.getAnnotationTypeFilterableDTO() != null 
						&& item.getAnnotationTypeFilterableDTO().getSortOrder() != null ) {
					
					reportedPeptide_AnnotationTypeDTO_SortOrder_List.add( item );
				}
			}

			//  Sort Peptide Ann Type records on sort order
			
			
			Collections.sort( reportedPeptide_AnnotationTypeDTO_SortOrder_List, new Comparator<AnnotationTypeDTO>() {

				@Override
				public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {

					return o1.getAnnotationTypeFilterableDTO().getSortOrder() - o2.getAnnotationTypeFilterableDTO().getSortOrder();
				}
			});
			
			
			/////////////
			
			//   Create Peptide Annotation List for Display Order and sort it on Display order
			
			
			final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List =
					new ArrayList<>( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap.size() + srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap.size() );
			
			for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgmFilterableReportedPeptideAnnotationTypeDTOMap.entrySet() ) {
				
				AnnotationTypeDTO item = entry.getValue();
				
				peptideAnnotationTypeIdsForAnnotationDataRetrieval.add( item.getId() );
				
				if ( item.isDefaultVisible() ) {
					
					reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.add( item );
				}
			}

			for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap.entrySet() ) {
				
				AnnotationTypeDTO item = entry.getValue();
				
				peptideAnnotationTypeIdsForAnnotationDataRetrieval.add( item.getId() );
				
				if ( item.isDefaultVisible() ) {
					
					reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.add( item );
				}
			}
			

			//  Sort Ann type records on display order
			
			
			Collections.sort( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List, new Comparator<AnnotationTypeDTO>() {

				@Override
				public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {

					return o1.getDisplayOrder() - o2.getDisplayOrder();
				}
			});
			


			String searchIdString = eachSearchIdToProcess.toString();

			CutoffValuesSearchLevel cutoffValuesSearchLevel = cutoffValuesRootLevel.getSearches().get( searchIdString );

			if ( cutoffValuesSearchLevel == null ) {
				
				cutoffValuesSearchLevel = new CutoffValuesSearchLevel();

				//  TODO  maybe throw exception instead

//				continue;  //  EARLY continue
			}



			

			/////////////////////////////////////////
			
			///   Create sets of annotation type ids that were searched for but are not displayed by default.
			///   Those annotation values will be displayed after the default, in name order
			
			Set<Integer> peptideAnnotationTypesSearchedFor = new HashSet<>();
			

			Map<String,CutoffValuesAnnotationLevel> peptideCutoffValues = cutoffValuesSearchLevel.getPeptideCutoffValues();
			
			if ( peptideCutoffValues != null ) {

				for (  Map.Entry<String,CutoffValuesAnnotationLevel> peptideCutoffEntry : peptideCutoffValues.entrySet() ) {

					CutoffValuesAnnotationLevel cutoffValuesAnnotationLevel = peptideCutoffEntry.getValue();

					int annTypeId = cutoffValuesAnnotationLevel.getId();
					peptideAnnotationTypesSearchedFor.add( annTypeId );
				}
			}

			// Remove annotation type ids that are in default display

			for ( AnnotationTypeDTO item : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

				peptideAnnotationTypesSearchedFor.remove( item.getId() );
			}

			//  Get AnnotationTypeDTO for ids not in default display and sort in name order
			
			List<AnnotationTypeDTO> peptideAnnotationTypesToAddFromQuery = new ArrayList<>();
			
			if ( ! peptideAnnotationTypesSearchedFor.isEmpty() ) {
				
				//   Add in Peptide annotation types the user searched for
				
				List<Integer> searchIdList = new ArrayList<>( 1 );
				searchIdList.add( eachSearchIdToProcess );
				
				Map<Integer, Map<Integer, AnnotationTypeDTO>> peptideFilterableAnnotationTypesForSearchIds =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdList );

				Map<Integer, AnnotationTypeDTO> peptideFilterableAnnotationTypesForSearchId =
						peptideFilterableAnnotationTypesForSearchIds.get( eachSearchIdToProcess );
				
				for ( Integer peptideAnnotationTypeToAdd : peptideAnnotationTypesSearchedFor ) {
				
					AnnotationTypeDTO annotationTypeDTO = peptideFilterableAnnotationTypesForSearchId.get( peptideAnnotationTypeToAdd );

					if ( annotationTypeDTO == null ) {
						
						
					}
					
					peptideAnnotationTypesToAddFromQuery.add( annotationTypeDTO );
				}
				
				// sort on ann type name
				Collections.sort( peptideAnnotationTypesToAddFromQuery, new Comparator<AnnotationTypeDTO>() {

					@Override
					public int compare(AnnotationTypeDTO o1,
							AnnotationTypeDTO o2) {

						return o1.getName().compareTo( o2.getName() );
					}
				} );
			}
			
			//   Add the searched for but not in default display AnnotationTypeDTO 
			//   to the default display list.
			//   The annotation data will be loaded from the DB in the searcher since they were searched for
			
			for ( AnnotationTypeDTO annotationTypeDTO : peptideAnnotationTypesToAddFromQuery ) {
				
				reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.add( annotationTypeDTO );
			}


			/////////////////////


			List<Integer> singeSearchIdList = new ArrayList<>( 1 );
			
			singeSearchIdList.add( eachSearchIdToProcess );
			

			//  Copy cutoff data to searcher cutoff data


			Z_CutoffValuesObjectsToOtherObjects_PerSearchResult z_CutoffValuesObjectsToOtherObjects_PerSearchResult = 
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesSearchLevel( 
							singeSearchIdList, cutoffValuesSearchLevel );


			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = z_CutoffValuesObjectsToOtherObjects_PerSearchResult.getSearcherCutoffValuesSearchLevel();


			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();


//			final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListRecordSortOrderSorted = new ArrayList<>( psmCutoffValuesList.size() );
			final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted = new ArrayList<>( psmCutoffValuesList.size() );

			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {

//				psmCutoffsAnnotationTypeDTOListRecordSortOrderSorted.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
				psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}
			

//			SortAnnotationDTORecords.getInstance()
//			.sortPsmAnnotationTypeDTOForBestPsmAnnotations_RecordsSortOrder( psmCutoffsAnnotationTypeDTOListRecordSortOrderSorted );

			SortAnnotationDTORecords.getInstance()
			.sortPsmAnnotationTypeDTOForBestPsmAnnotations_AnnotationDisplayOrder( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );


			
			List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList = new ArrayList<>( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size() );
			
			for ( AnnotationTypeDTO psmAnnotationTypeDTO : psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
				
				AnnotationDisplayNameDescription psmAnnotationDisplayNameDescription = new AnnotationDisplayNameDescription();
				
				psmAnnotationDisplayNameDescription.setDisplayName( psmAnnotationTypeDTO.getName() );
				psmAnnotationDisplayNameDescription.setDescription( psmAnnotationTypeDTO.getDescription() );
				
				psmAnnotationDisplayNameDescriptionList.add(psmAnnotationDisplayNameDescription);
			}
			
			
			serviceResultEntry.setPsmAnnotationDisplayNameDescriptionList( psmAnnotationDisplayNameDescriptionList );


			List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList = new ArrayList<>( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.size() );


			//  Get Peptide column headers data

			for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

				AnnotationDisplayNameDescription annotationDisplayNameDescription = new AnnotationDisplayNameDescription();

				annotationDisplayNameDescription.setDisplayName( annotationTypeDTO.getName() );
				annotationDisplayNameDescription.setDescription( annotationTypeDTO.getDescription() );

				peptideAnnotationDisplayNameDescriptionList.add( annotationDisplayNameDescription );
			}

			
			
			//  First get list of reported peptide ids for unifiedReportedPeptideId and search id

			List<Integer> singleSearchIdList = new ArrayList<>( 1 );

			singleSearchIdList.add( eachSearchIdToProcess );

			List<ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult>  resultList = 
					ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher.getInstance()
					.getReportedPeptideIdsForSearchIdsAndUnifiedReportedPeptideId( singleSearchIdList, unifiedReportedPeptideId );

			
			//  Sort on search id, then reported peptide id

			Collections.sort( resultList, new Comparator<ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult>() {

				@Override
				public int compare(ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult o1, ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult o2) {

					if ( o1.getSearchId() == o2.getSearchId() ) {

						return o1.getReportedPeptideId() - o2.getReportedPeptideId();
					}
					return o1.getSearchId() - o2.getSearchId();
				}
			});		



			
			//  Process each search id, reported peptide id pair

			for ( ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult item : resultList ) {

				int reportedPeptideId = item.getReportedPeptideId();




				//  Get Peptide data

				List<ReportedPeptidesForMergedPeptidePageWrapper> peptideWebDisplayList = 
						ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher.getInstance().getReportedPeptidesWebDisplay( eachSearchIdToProcess, reportedPeptideId, searcherCutoffValuesSearchLevel );


				//  Get Peptide Annotation data

				//  Internal holder

				List<InternalReportedPeptideWebDisplayHolder> searchReportedPeptideWebDisplayHolderList = new ArrayList<>( peptideWebDisplayList.size() );

				for ( ReportedPeptidesForMergedPeptidePageWrapper webDisplayItemWrapper : peptideWebDisplayList ) {


					ReportedPeptidesForMergedPeptidePage webDisplayItem = webDisplayItemWrapper.getReportedPeptidesForMergedPeptidePage();

					
					//  Add sorted Best PSM data to webDisplayItem from webDisplayItemWrapper
					{

						List<String> psmAnnotationValues = new ArrayList<>( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size() );
						
						//  psmAnnotationDTOMap added in Searcher for Best PSM data
						Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap = webDisplayItemWrapper.getPsmAnnotationDTOMap();
						
						for ( AnnotationTypeDTO psmCutoffsAnnotationTypeDTO : psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
							
							AnnotationDataBaseDTO psmAnnotationDataBaseDTO = psmAnnotationDTOMap.get( psmCutoffsAnnotationTypeDTO.getId() );
							
							psmAnnotationValues.add( psmAnnotationDataBaseDTO.getValueString() );
						}
						
						webDisplayItem.setPsmAnnotationValues( psmAnnotationValues );
					}
					
					
					
					
					
					{

						Map<Integer, SearchReportedPeptideAnnotationDTO> searchReportedPeptideAnnotationDTOMapOnTypeId = new HashMap<>();

						if ( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List != null 
								&& ( ! reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.isEmpty() ) ) {

							List<SearchReportedPeptideAnnotationDTO> searchReportedPeptideFilterableAnnotationDataList = 
									SearchReportedPeptideAnnotationDataSearcher.getInstance()
									.getSearchReportedPeptideAnnotationDTOList( 
											eachSearchIdToProcess, reportedPeptideId, peptideAnnotationTypeIdsForAnnotationDataRetrieval );


							for ( SearchReportedPeptideAnnotationDTO searchReportedPeptideFilterableAnnotationDataItem : searchReportedPeptideFilterableAnnotationDataList ) {

								searchReportedPeptideAnnotationDTOMapOnTypeId.put( searchReportedPeptideFilterableAnnotationDataItem.getAnnotationTypeId(), searchReportedPeptideFilterableAnnotationDataItem );
							}
						}

						InternalReportedPeptideWebDisplayHolder internalReportedPeptideWebDisplayHolder = new InternalReportedPeptideWebDisplayHolder();

						internalReportedPeptideWebDisplayHolder.reportedPeptidesForUnifiedPeptIdMergedPeptidePage = webDisplayItem;

						internalReportedPeptideWebDisplayHolder.searchReportedPeptideAnnotationDTOMapOnTypeId = searchReportedPeptideAnnotationDTOMapOnTypeId;

						
						
						searchReportedPeptideWebDisplayHolderList.add( internalReportedPeptideWebDisplayHolder );
					}


					//  Sort Peptides on sort order

					Collections.sort( searchReportedPeptideWebDisplayHolderList, new Comparator<InternalReportedPeptideWebDisplayHolder>() {

						@Override
						public int compare(InternalReportedPeptideWebDisplayHolder o1, InternalReportedPeptideWebDisplayHolder o2) {

							//  Loop through the Peptide annotation types (sorted on sort order), comparing the values

							for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_SortOrder_List ) {

								int typeId = annotationTypeDTO.getId();

								SearchReportedPeptideAnnotationDTO o1_SearchReportedPeptideAnnotationDTO = o1.searchReportedPeptideAnnotationDTOMapOnTypeId.get( typeId );
								if ( o1_SearchReportedPeptideAnnotationDTO == null ) {

									String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
									log.error( msg );
									throw new RuntimeException(msg);
								}

								double o1Value = o1_SearchReportedPeptideAnnotationDTO.getValueDouble();


								SearchReportedPeptideAnnotationDTO o2_SearchReportedPeptideAnnotationDTO = o2.searchReportedPeptideAnnotationDTOMapOnTypeId.get( typeId );
								if ( o2_SearchReportedPeptideAnnotationDTO == null ) {

									String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
									log.error( msg );
									throw new RuntimeException(msg);
								}

								double o2Value = o2_SearchReportedPeptideAnnotationDTO.getValueDouble();

								if ( o1Value != o2Value ) {

									if ( o1Value < o2Value ) {

										return -1;
									} else {
										return 1;
									}
								}

							}

							//  If everything matches, sort on reported peptide id

							return o1.reportedPeptidesForUnifiedPeptIdMergedPeptidePage.getReportedPeptide().getId() - o2.reportedPeptidesForUnifiedPeptIdMergedPeptidePage.getReportedPeptide().getId();
						}
					});



					for ( InternalReportedPeptideWebDisplayHolder internalReportedPeptideWebDisplayHolder : searchReportedPeptideWebDisplayHolderList ) {


						//  Get annotation values

						List<String> peptideAnnotationValues = new ArrayList<>( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.size() );

						for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

							Integer annotationTypeId = annotationTypeDTO.getId();
						
							SearchReportedPeptideAnnotationDTO peptideFilterableAnnotationDTO = 
									internalReportedPeptideWebDisplayHolder.searchReportedPeptideAnnotationDTOMapOnTypeId.get( annotationTypeId );

							String annotationValueString = null;
							
							if ( peptideFilterableAnnotationDTO != null ) {

								annotationValueString = peptideFilterableAnnotationDTO.getValueString();

							} else {
																
								if ( ! srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap.containsKey( annotationTypeId ) ) {
									
									String msg = "ERROR.  Cannot find AnnotationDTO for type id: " + annotationTypeDTO.getId();
									log.error( msg );
									throw new ProxlWebappDataException(msg);
								}

								//  Allow Peptide Descriptive Annotations to be missing 
								
								annotationValueString = "";
							}
							
							peptideAnnotationValues.add( annotationValueString );
						}


						ReportedPeptidesForMergedPeptidePage peptideWebDisplay = internalReportedPeptideWebDisplayHolder.reportedPeptidesForUnifiedPeptIdMergedPeptidePage;

						peptideWebDisplay.setPeptideAnnotationValues( peptideAnnotationValues );


						reportedPepidesListOutput.add( peptideWebDisplay );
					}


				}  //  END:   for ( ReportedPeptidesForMergedPeptidePage webDisplayItem : peptideWebDisplayList ) {

			}   //   END  for ( ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult item : resultList ) {

			if ( ! reportedPepidesListOutput.isEmpty() ) {
				
				//   Only add entry to per searches map if there are records for that search

				serviceResultEntry.setPeptideAnnotationDisplayNameDescriptionList( peptideAnnotationDisplayNameDescriptionList );

				serviceResultEntry.setReportedPepides( reportedPepidesListOutput );

				reportedPeptidesPerSearchIdMap.put( eachSearchIdToProcess, serviceResultEntry );
			}
			
			
		}  //  END:  for each search id


		///////////

		ReportedPeptidesPerSearchForMergedPeptidePageResult serviceResult = new ReportedPeptidesPerSearchForMergedPeptidePageResult();

		serviceResult.setReportedPeptidesPerSearchIdMap( reportedPeptidesPerSearchIdMap );

		return serviceResult;

	}

	/**
	 * 
	 *
	 */
	private static class InternalReportedPeptideWebDisplayHolder {

		ReportedPeptidesForMergedPeptidePage reportedPeptidesForUnifiedPeptIdMergedPeptidePage;

		Map<Integer, SearchReportedPeptideAnnotationDTO> searchReportedPeptideAnnotationDTOMapOnTypeId;

	}	
}
