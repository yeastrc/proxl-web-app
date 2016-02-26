package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collection;
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
import org.yeastrc.xlink.www.objects.AnnotationTypeDTOListForSearchId;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.GetCrosslinkReportedPeptidesServiceResult;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslinkAnnDataWrapper;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslinkWebserviceResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideCrosslinkSearcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataDefaultDisplayInDisplayOrder;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataInSortOrder;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideAnnotationDataSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesAnnotationLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.DeserializeCutoffForWebservices;

@Path("/data")
public class ReportedPeptides_Crosslink_Service {

	private static final Logger log = Logger.getLogger(ReportedPeptides_Crosslink_Service.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCrosslinkReportedPeptides") 
	public GetCrosslinkReportedPeptidesServiceResult getCrosslinkReportedPeptides( 
			@QueryParam( "search_id" ) Integer searchId,
			@QueryParam( "psmPeptideCutoffsForSearchId" ) String psmPeptideCutoffsForSearchId_JSONString,
			@QueryParam( "protein_1_id" ) Integer protein1Id,
			@QueryParam( "protein_2_id" ) Integer protein2Id,
			@QueryParam( "protein_1_position" ) Integer protein1Position,
			@QueryParam( "protein_2_position" ) Integer protein2Position,
			@Context HttpServletRequest request )
	throws Exception {
		
		if ( searchId == null ) {

			String msg = "Provided search_id is null or search_id is missing";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		if ( StringUtils.isEmpty( psmPeptideCutoffsForSearchId_JSONString ) ) {

			String msg = "Provided psmPeptideCutoffsForSearchId is null or psmPeptideCutoffsForSearchId is missing";

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
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );

			searchIdsCollection.add( searchId );
		
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				String msg = "No project ids for search id: " + searchId;
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



			CutoffValuesSearchLevel cutoffValuesSearchLevel = 
					DeserializeCutoffForWebservices.getInstance().deserialize_JSON_ToCutoffSearchLevel( psmPeptideCutoffsForSearchId_JSONString );


			//  Get Annotation Type records for PSM and Peptide
			
			
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
			

			/////////////

			//  Get  Annotation Type records for Reported Peptides
			
			Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgmFilterableReportedPeptideAnnotationTypeDTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsCollection );
			
			
			Map<Integer, AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = 
					srchPgmFilterableReportedPeptideAnnotationTypeDTOListPerSearchIdMap.get( searchId );
			
			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap == null ) {
				
				//  No records were found, allowable for Reported Peptides
				
				srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = new HashMap<>();
			}
			

			//    Descriptive annotations
			

			Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgmDescriptiveReportedPeptideAnnotationTypeDTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Peptide_Descriptive_ForSearchIds( searchIdsCollection );
			
			
			Map<Integer, AnnotationTypeDTO> srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap = 
					srchPgmDescriptiveReportedPeptideAnnotationTypeDTOListPerSearchIdMap.get( searchId );
			
			if ( srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap == null ) {
				
				//  No records were found, probably an error   TODO
				
				srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap = new HashMap<>();
			}

			
			//////////////
			

			//  Copy cutoff data to searcher cutoff data
			
			
			Z_CutoffValuesObjectsToOtherObjects_PerSearchResult z_CutoffValuesObjectsToOtherObjects_PerSearchResult = 
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesSearchLevel( 
							searchIdsCollection, cutoffValuesSearchLevel );
			

			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = z_CutoffValuesObjectsToOtherObjects_PerSearchResult.getSearcherCutoffValuesSearchLevel();
			

			//  Get Peptide data from DATABASE    from database


			
			
			List<SearchPeptideCrosslinkAnnDataWrapper> searchPeptideCrosslinkList = 
					SearchPeptideCrosslinkSearcher.getInstance().searchOnSearchProteinCrosslink( 
							searchId, searcherCutoffValuesSearchLevel, protein1Id, protein2Id, protein1Position, protein2Position );
					
			
			
			
			
			GetCrosslinkReportedPeptidesServiceResult getCrosslinkReportedPeptidesServiceResult =
					getAnnotationDataAndSort( 
							searchPeptideCrosslinkList,
							searchId, 
							cutoffValuesSearchLevel,
							srchPgmFilterableReportedPeptideAnnotationTypeDTOMap, 
							srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap, 
							searcherCutoffValuesSearchLevel );

					
			return getCrosslinkReportedPeptidesServiceResult;

			
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
	
	
	////////////////////////////////////////////
	
	/**
	 * @param searchPeptideCrosslinkWrappedList
	 * @param searchId
	 * @param srchPgmFilterableReportedPeptideAnnotationTypeDTOMap
	 * @param srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	private GetCrosslinkReportedPeptidesServiceResult getAnnotationDataAndSort(
			
			List<SearchPeptideCrosslinkAnnDataWrapper> searchPeptideCrosslinkWrappedList,
			
			int searchId,
			CutoffValuesSearchLevel cutoffValuesSearchLevel,
			Map<Integer, AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOMap,
			Map<Integer, AnnotationTypeDTO> srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel
			
			) throws Exception {
		
		
		

		Collection<Integer> searchIdsCollection = new ArrayList<>( 1 );
		
		searchIdsCollection.add( searchId );

		

		//  Create list of Best PSM annotation names to display as column headers

		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

		
		final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );

		for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {

			psmCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
		}
		

		
		
		/////////////

		//   Get Peptide Annotation Types Map of Lists which are Sorted on Sort Order 


		Map<Integer, AnnotationTypeDTOListForSearchId> peptideAnnotationTypeDTO_SortOrder_MainMap =
				GetAnnotationTypeDataInSortOrder.getInstance()
				.getPeptide_AnnotationTypeDataInSortOrder( searchIdsCollection );
		
		if ( peptideAnnotationTypeDTO_SortOrder_MainMap.size() != 1 ) {
			
			String msg = "getPeptide_AnnotationTypeDataInSortOrder returned other than 1 entry at searchId level ";
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_SortOrder_List = peptideAnnotationTypeDTO_SortOrder_MainMap.get( searchId ).getAnnotationTypeDTOList();
				

		/////////////
		
		//   Get Peptide Annotation Types Map of Lists which are Sorted on Display Order 
		
		Map<Integer, AnnotationTypeDTOListForSearchId> peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap = 
				GetAnnotationTypeDataDefaultDisplayInDisplayOrder.getInstance()
				.getPeptide_AnnotationTypeDataDefaultDisplayInDisplayOrder( searchIdsCollection );
		

		if ( peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.size() != 1 ) {
			
			String msg = "getPeptide_AnnotationTypeDataDefaultDisplayInDisplayOrder returned other than 1 entry at searchId level ";
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List = peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.get( searchId ).getAnnotationTypeDTOList();
						
		

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
			
			Map<Integer, Map<Integer, AnnotationTypeDTO>> peptideFilterableAnnotationTypesForSearchIds =
			GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsCollection );

			Map<Integer, AnnotationTypeDTO> peptideFilterableAnnotationTypesForSearchId =
					peptideFilterableAnnotationTypesForSearchIds.get( searchId );
			
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




		//  Get set of Peptide annotation type ids for getting annotation data
		
		Set<Integer> annotationTypeIdsForAnnotationDataRetrieval = new HashSet<>();

		for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_SortOrder_List ) {
			
			annotationTypeIdsForAnnotationDataRetrieval.add( annotationTypeDTO.getId() );
		}
		
		for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {
			
			annotationTypeIdsForAnnotationDataRetrieval.add( annotationTypeDTO.getId() );
		}
		
		//  Get Annotation data
		

		for ( SearchPeptideCrosslinkAnnDataWrapper searchPeptideCrosslinkWrappedItem : searchPeptideCrosslinkWrappedList ) {
		
			SearchPeptideCrosslink searchPeptideCrosslinkItem = searchPeptideCrosslinkWrappedItem.getSearchPeptideCrosslink();
			
			int reportedPeptideId = searchPeptideCrosslinkItem.getReportedPeptide().getId();

			Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap = new HashMap<>();


			//  Process annotation type ids to get annotation data

			{
				List<SearchReportedPeptideAnnotationDTO> searchReportedPeptideFilterableAnnotationDataList = 
						SearchReportedPeptideAnnotationDataSearcher.getInstance()
						.getSearchReportedPeptideAnnotationDTOList( searchId, reportedPeptideId, annotationTypeIdsForAnnotationDataRetrieval );

				for ( SearchReportedPeptideAnnotationDTO searchReportedPeptideFilterableAnnotationDataItem : searchReportedPeptideFilterableAnnotationDataList ) {

					peptideAnnotationDTOMap.put( searchReportedPeptideFilterableAnnotationDataItem.getAnnotationTypeId(), searchReportedPeptideFilterableAnnotationDataItem );
				}
			}
			
			searchPeptideCrosslinkWrappedItem.setPeptideAnnotationDTOMap( peptideAnnotationDTOMap );

		}



		//  Sort Peptide records on sort order, then best PSM values

		Collections.sort( searchPeptideCrosslinkWrappedList, new Comparator<SearchPeptideCrosslinkAnnDataWrapper>() {

			@Override
			public int compare(SearchPeptideCrosslinkAnnDataWrapper o1, SearchPeptideCrosslinkAnnDataWrapper o2) {

				//  Process the Peptide annotation types (sorted on sort order), comparing the values

				for ( AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO : reportedPeptide_AnnotationTypeDTO_SortOrder_List ) {

					int typeId = srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();

					AnnotationDataBaseDTO o1_SearchReportedPeptideAnnotationDTO = o1.getPeptideAnnotationDTOMap().get( typeId );
					if ( o1_SearchReportedPeptideAnnotationDTO == null ) {

						String msg = "Unable to get Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}

					double o1Value = o1_SearchReportedPeptideAnnotationDTO.getValueDouble();


					AnnotationDataBaseDTO o2_SearchReportedPeptideAnnotationDTO = o2.getPeptideAnnotationDTOMap().get( typeId );
					if ( o2_SearchReportedPeptideAnnotationDTO == null ) {

						String msg = "Unable to get Filterable Annotation data for type id: " + typeId;
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
				
				

				//  If everything matches, process the PSM annotation types (sorted on some order), comparing the values

				for ( AnnotationTypeDTO psmAnnotationTypeDTO : psmCutoffsAnnotationTypeDTOList ) {

					int typeId = psmAnnotationTypeDTO.getId();

					AnnotationDataBaseDTO o1_PsmPeptideAnnotationDTO = o1.getPeptideAnnotationDTOMap().get( typeId );
					if ( o1_PsmPeptideAnnotationDTO == null ) {

						String msg = "Unable to get Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}

					double o1Value = o1_PsmPeptideAnnotationDTO.getValueDouble();


					AnnotationDataBaseDTO o2_PsmPeptideAnnotationDTO = o2.getPeptideAnnotationDTOMap().get( typeId );
					if ( o2_PsmPeptideAnnotationDTO == null ) {

						String msg = "Unable to get Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}

					double o2Value = o2_PsmPeptideAnnotationDTO.getValueDouble();

					if ( o1Value != o2Value ) {

						if ( o1Value < o2Value ) {

							return -1;
						} else {
							return 1;
						}
					}

				}

				//  If everything matches, sort on reported peptide id

				try {
					return o1.getSearchPeptideCrosslink().getReportedPeptide().getId() - o2.getSearchPeptideCrosslink().getReportedPeptide().getId();
				} catch (Exception e) {
					
					throw new RuntimeException( e );
				}
			}
		});


		//  Build output list of ReportedPeptideWebDisplay

		List<SearchPeptideCrosslinkWebserviceResult> searchPeptideCrosslinkWebserviceResultListOutput = new ArrayList<>( searchPeptideCrosslinkWrappedList.size() );

		for ( SearchPeptideCrosslinkAnnDataWrapper searchPeptideCrosslinkWrapped : searchPeptideCrosslinkWrappedList ) {


			SearchPeptideCrosslink searchPeptideCrosslink = searchPeptideCrosslinkWrapped.getSearchPeptideCrosslink();

			SearchPeptideCrosslinkWebserviceResult searchPeptideCrosslinkWebserviceResult = 
					new SearchPeptideCrosslinkWebserviceResult( searchPeptideCrosslink );
			
			{
				//  Get Peptide annotation values

				List<String> peptideAnnotationValues = new ArrayList<>( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.size() );

				for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

					AnnotationDataBaseDTO peptideAnnotationDTO = 
							searchPeptideCrosslinkWrapped.getPeptideAnnotationDTOMap().get( annotationTypeDTO.getId() );

					if ( peptideAnnotationDTO == null ) {

						String msg = "ERROR.  Cannot AnnotationDTO for type id: " + annotationTypeDTO.getId();
						log.error( msg );
						throw new Exception(msg);
					}

					peptideAnnotationValues.add( peptideAnnotationDTO.getValueString() );
				}

				searchPeptideCrosslinkWebserviceResult.setPeptideAnnotationValueList( peptideAnnotationValues );
			}
			

			{
				//  Get PSM annotation values

				List<String> psmAnnotationValues = new ArrayList<>( psmCutoffsAnnotationTypeDTOList.size() );

				for ( AnnotationTypeDTO annotationTypeDTO : psmCutoffsAnnotationTypeDTOList ) {

					AnnotationDataBaseDTO psmAnnotationDTO = 
							searchPeptideCrosslinkWrapped.getPsmAnnotationDTOMap().get( annotationTypeDTO.getId() );

					if ( psmAnnotationDTO == null ) {

						String msg = "ERROR.  Cannot AnnotationDTO for type id: " + annotationTypeDTO.getId();
						log.error( msg );
						throw new Exception(msg);
					}

					psmAnnotationValues.add( psmAnnotationDTO.getValueString() );
				}

				searchPeptideCrosslinkWebserviceResult.setPsmAnnotationValueList( psmAnnotationValues );
			}


			searchPeptideCrosslinkWebserviceResultListOutput.add( searchPeptideCrosslinkWebserviceResult );
		}





		//  Put column headers data into output webservice for Peptides

		List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList = 
				new ArrayList<>( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.size() );


		for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

			AnnotationDisplayNameDescription annotationDisplayNameDescription = new AnnotationDisplayNameDescription();

			annotationDisplayNameDescription.setDisplayName( annotationTypeDTO.getName() );
			annotationDisplayNameDescription.setDescription( annotationTypeDTO.getDescription() );

			peptideAnnotationDisplayNameDescriptionList.add( annotationDisplayNameDescription );
		}
		


		//  Put column headers data into output webservice for PSM

		List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList = 
				new ArrayList<>( psmCutoffsAnnotationTypeDTOList.size() );


		for ( AnnotationTypeDTO annotationTypeDTO : psmCutoffsAnnotationTypeDTOList ) {

			AnnotationDisplayNameDescription annotationDisplayNameDescription = new AnnotationDisplayNameDescription();

			annotationDisplayNameDescription.setDisplayName( annotationTypeDTO.getName() );
			annotationDisplayNameDescription.setDescription( annotationTypeDTO.getDescription() );

			psmAnnotationDisplayNameDescriptionList.add( annotationDisplayNameDescription );
		}



		///////////////

		GetCrosslinkReportedPeptidesServiceResult getCrosslinkReportedPeptidesServiceResult = new GetCrosslinkReportedPeptidesServiceResult();

		getCrosslinkReportedPeptidesServiceResult.setPeptideAnnotationDisplayNameDescriptionList( peptideAnnotationDisplayNameDescriptionList );
		getCrosslinkReportedPeptidesServiceResult.setPsmAnnotationDisplayNameDescriptionList( psmAnnotationDisplayNameDescriptionList );
		
		getCrosslinkReportedPeptidesServiceResult.setSearchPeptideCrosslinkList( searchPeptideCrosslinkWebserviceResultListOutput );

		return getCrosslinkReportedPeptidesServiceResult;
	}
	
	
		
}
