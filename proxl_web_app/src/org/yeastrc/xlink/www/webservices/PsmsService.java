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
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AnnotationTypeDTOListForSearchId;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.PsmsServiceResult;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmAnnotationDataSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.annotation_display.AddAddAnnTypeIdToAnnotationTypeIdSet;
import org.yeastrc.xlink.www.annotation_display.AddIncludeAnnTypeIdToAnnotationTypeIdSet;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSON_PsmPeptide;
import org.yeastrc.xlink.www.annotation_display.DeserializeAnnTypeIdDisplayIncludeExclude;
import org.yeastrc.xlink.www.annotation_display.RemoveExcludeAnnTypeIdFromAnnotationTypeDTOList;
import org.yeastrc.xlink.www.annotation_display.RemoveExcludeAnnTypeIdFromAnnotationTypeIdSet;
import org.yeastrc.xlink.www.annotation_display.RemoveFromAnnotationTypeDTOListEntriesNotInIncludeAnnTypeId;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataDefaultDisplayInDisplayOrder;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataInSortOrder;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesAnnotationLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.DeserializeCutoffForWebservices;


@Path("/data")
public class PsmsService {

	private static final Logger log = Logger.getLogger(PsmsService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getPsms") 
	public PsmsServiceResult getViewerData( @QueryParam( "search_id" ) Integer projectSearchId,
										  @QueryParam( "reported_peptide_id" ) Integer reportedPeptideId,
										  @QueryParam( "psmPeptideCutoffsForSearchId" ) String psmPeptideCutoffsForSearchId_JSONString,
										  @QueryParam( "psmAnnTypeDisplayIncludeExclude" ) String psmAnnTypeDisplayIncludeExclude_JSONString,
										  @Context HttpServletRequest request )
	throws Exception {
	
		if ( projectSearchId == null ) {
			String msg = "Provided search_id is null or search_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( reportedPeptideId == null ) {
			String msg = "Provided reported_peptide_id is null or reported_peptide_id is missing";
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
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
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
			
			//   Get PSM and Peptide Cutoff data from JSON
			CutoffValuesSearchLevel cutoffValuesSearchLevel = 
					DeserializeCutoffForWebservices.getInstance().deserialize_JSON_ToCutoffSearchLevel( psmPeptideCutoffsForSearchId_JSONString );
			
			//    Get PSM annotation type ids to include or exclude from display
			AnnTypeIdDisplayJSON_PsmPeptide annTypeIdDisplayPsm = null;
			if ( StringUtils.isNotEmpty( psmAnnTypeDisplayIncludeExclude_JSONString ) ) {
				annTypeIdDisplayPsm =
					DeserializeAnnTypeIdDisplayIncludeExclude.getInstance()
					.deserialize_JSON_ToAnnTypeIdDisplayJSON_PsmPeptide( psmAnnTypeDisplayIncludeExclude_JSONString );
			}
			
			//  Get PSMs for cutoffs and other data
			PsmsServiceResult psmsServiceResult = 
					getPsmData( cutoffValuesSearchLevel, annTypeIdDisplayPsm, searchId, reportedPeptideId, searchIdsCollection );
			
			return psmsServiceResult;
			
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
	 * @param cutoffValuesSearchLevel
	 * @param annTypeIdDisplayPsm
	 * @param searchId
	 * @param reportedPeptideId
	 * @param searchIdsCollection
	 * @return
	 * @throws Exception
	 */
	private PsmsServiceResult getPsmData( 
			CutoffValuesSearchLevel cutoffValuesSearchLevel,
			AnnTypeIdDisplayJSON_PsmPeptide annTypeIdDisplayPsm,
			Integer searchId,
			Integer reportedPeptideId,
			Collection<Integer> searchIdsCollection ) throws Exception {
		
		//  Get Annotation Type records for PSM and Peptide
		//  Get  Annotation Type records for PSM
		//    Filterable annotations
		Map<Integer, Map<Integer, AnnotationTypeDTO>> psmFilterableAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsCollection );
		Map<Integer, AnnotationTypeDTO> psmFilterableAnnotationTypeDTOMap = 
				psmFilterableAnnotationTypeDTOListPerSearchIdMap.get( searchId );
		if ( psmFilterableAnnotationTypeDTOMap == null ) {
			//  No records were found, probably an error   TODO
			psmFilterableAnnotationTypeDTOMap = new HashMap<>();
		}
		//    Descriptive annotations
		Map<Integer, Map<Integer, AnnotationTypeDTO>> psmDescriptiveAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Psm_Descriptive_ForSearchIds( searchIdsCollection );
		Map<Integer, AnnotationTypeDTO> psmDescriptiveAnnotationTypeDTOMap = 
				psmDescriptiveAnnotationTypeDTOListPerSearchIdMap.get( searchId );
		if ( psmDescriptiveAnnotationTypeDTOMap == null ) {
			//  No records were found, probably an error   TODO
			psmDescriptiveAnnotationTypeDTOMap = new HashMap<>();
		}
		
		/////////////
		//  Get  Annotation Type records for Reported Peptides
		Map<Integer, Map<Integer, AnnotationTypeDTO>> reportedPeptideFilterableAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsCollection );
		Map<Integer, AnnotationTypeDTO> reportedPeptideFilterableAnnotationTypeDTOMap = 
				reportedPeptideFilterableAnnotationTypeDTOListPerSearchIdMap.get( searchId );
		if ( reportedPeptideFilterableAnnotationTypeDTOMap == null ) {
			//  No records were found, allowable for Reported Peptides
			reportedPeptideFilterableAnnotationTypeDTOMap = new HashMap<>();
		}
		
		//////////
		//  Copy cutoff data to searcher cutoff data
		Z_CutoffValuesObjectsToOtherObjects_PerSearchResult z_CutoffValuesObjectsToOtherObjects_PerSearchResult = 
				Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesSearchLevel( 
						searchIdsCollection, cutoffValuesSearchLevel );
		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = z_CutoffValuesObjectsToOtherObjects_PerSearchResult.getSearcherCutoffValuesSearchLevel();
		
		//  Get PSM data
		List<PsmWebDisplayWebServiceResult> psmWebDisplayList = 
				PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( searchId, reportedPeptideId, searcherCutoffValuesSearchLevel );
		PsmsServiceResult psmsServiceResult =
				getAnnotationDataAndSort( 
						searchId, 
						searchIdsCollection, 
						cutoffValuesSearchLevel,
						annTypeIdDisplayPsm,
						psmFilterableAnnotationTypeDTOMap, 
						psmDescriptiveAnnotationTypeDTOMap, 
						psmWebDisplayList );
		
		return psmsServiceResult;
	}

	/**
	 * @param searchId
	 * @param searchIdsCollection
	 * @param cutoffValuesSearchLevel
	 * @param annTypeIdDisplayPsm
	 * @param psmFilterableAnnotationTypeDTOMap
	 * @param psmDescriptiveAnnotationTypeDTOMap
	 * @param psmWebDisplayList
	 * @return
	 * @throws Exception
	 */
	private PsmsServiceResult getAnnotationDataAndSort(
			Integer searchId,
			Collection<Integer> searchIdsCollection,
			CutoffValuesSearchLevel cutoffValuesSearchLevel,
			AnnTypeIdDisplayJSON_PsmPeptide annTypeIdDisplayPsm,
			Map<Integer, AnnotationTypeDTO> psmFilterableAnnotationTypeDTOMap,
			Map<Integer, AnnotationTypeDTO> psmDescriptiveAnnotationTypeDTOMap,
			List<PsmWebDisplayWebServiceResult> psmWebDisplayList 
			) throws Exception {
		
		/////////////
		//   Create PSM Annotation List sorted on Sort Order 
		Map<Integer, AnnotationTypeDTOListForSearchId> annotationTypeDTO_SortOrder_MainMap =
				GetAnnotationTypeDataInSortOrder.getInstance().getPsmAnnotationTypeDataInSortOrder( searchIdsCollection );
		if ( annotationTypeDTO_SortOrder_MainMap.size() != 1 ) {
			String msg = "getPsmAnnotationTypeDataInSortOrder returned other than 1 entry at searchId level ";
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		final List<AnnotationTypeDTO> psm_AnnotationTypeDTO_SortOrder_List = 
				annotationTypeDTO_SortOrder_MainMap.get( searchId ).getAnnotationTypeDTOList();
		
		//  Validate all records in psm_AnnotationTypeDTO_SortOrder_List have AnnotationTypeFilterableDTO and 
		for ( AnnotationTypeDTO annotationTypeDTO : psm_AnnotationTypeDTO_SortOrder_List ) {
			AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO(); 
			if ( annotationTypeFilterableDTO == null ) {
				String msg = "annotationTypeFilterableDTO == null for annotationTypeDTO.id: " + annotationTypeDTO.getId();
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			if ( annotationTypeFilterableDTO.getFilterDirectionType() == null ) {
				String msg = "annotationTypeFilterableDTO.getFilterDirectionType() == null for annotationTypeDTO.id: " + annotationTypeDTO.getId();
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
		}
		
		/////////////
		//   Create PSM Annotation List sorted on Display Order 
		Map<Integer, AnnotationTypeDTOListForSearchId> annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap = 
				GetAnnotationTypeDataDefaultDisplayInDisplayOrder.getInstance()
				.getPsmAnnotationTypeDataDefaultDisplayInDisplayOrder( searchIdsCollection );
		if ( annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.size() != 1 ) {
			String msg = "getPsmAnnotationTypeDataDefaultDisplayInDisplayOrder returned other than 1 entry at searchId level ";
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		List<AnnotationTypeDTO> psm_AnnotationTypeDTO_DefaultDisplay_List_workingList = 
				annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.get( searchId ).getAnnotationTypeDTOList();
		if ( annTypeIdDisplayPsm != null ) {
			//   Alter psm_AnnotationTypeDTO_DefaultDisplay_List for User Input Exclude and Include displayed data 
			//   Remove from psm_AnnotationTypeDTO_DefaultDisplay_List  the excluded annotation type ids
			//   ( the entries in annTypeIdDisplayPsm.getExclAnnTypeId() )
			RemoveExcludeAnnTypeIdFromAnnotationTypeDTOList.getInstance()
			.removeExcludeAnnTypeIdFromAnnotationTypeDTOList( annTypeIdDisplayPsm, psm_AnnotationTypeDTO_DefaultDisplay_List_workingList );
			//   Remove from AnnotationTypeDTO List not on Include AnnTypeId  
			//   ( the entries in annTypeIdDisplayPsm.getInclAnnTypeId() )
			RemoveFromAnnotationTypeDTOListEntriesNotInIncludeAnnTypeId.getInstance()
			.removeFromAnnotationTypeDTOListEntriesNotInIncludeAnnTypeId( annTypeIdDisplayPsm, psm_AnnotationTypeDTO_DefaultDisplay_List_workingList );
		}
		////////
		///   Create set of annotation type ids that are not displayed by default.
		///   Those annotation values will be displayed after the default, in name order
		Set<Integer> annotationTypesToAddToOutputAnnotationData = new HashSet<>();
		/////////////////////////////////////////
		//  Do ONLY if no "Include" Ann Type Ids
		if ( annTypeIdDisplayPsm == null 
				|| annTypeIdDisplayPsm.getInclAnnTypeId() == null
				|| annTypeIdDisplayPsm.getInclAnnTypeId().length == 0 ) {
			///   Add annotation type ids that were searched for but are not displayed by default.
			Map<String,CutoffValuesAnnotationLevel> psmCutoffValues = cutoffValuesSearchLevel.getPsmCutoffValues();
			if ( psmCutoffValues != null ) {
				for (  Map.Entry<String,CutoffValuesAnnotationLevel> psmCutoffEntry : psmCutoffValues.entrySet() ) {
					CutoffValuesAnnotationLevel cutoffValuesAnnotationLevel = psmCutoffEntry.getValue();
					int annTypeId = cutoffValuesAnnotationLevel.getId();
					annotationTypesToAddToOutputAnnotationData.add( annTypeId );
				}
			}
		}
		//  Remove Excluded ann type ids
		RemoveExcludeAnnTypeIdFromAnnotationTypeIdSet.getInstance()
		.removeExcludeAnnTypeIdFromAnnotationTypeIdSet( annTypeIdDisplayPsm, annotationTypesToAddToOutputAnnotationData );
		//  Add Added ann type ids
		AddAddAnnTypeIdToAnnotationTypeIdSet.getInstance()
		.addIncludeAnnTypeIdToAnnotationTypeIdSet( annTypeIdDisplayPsm, annotationTypesToAddToOutputAnnotationData );
		//  Add Included ann type ids
		AddIncludeAnnTypeIdToAnnotationTypeIdSet.getInstance()
		.addIncludeAnnTypeIdToAnnotationTypeIdSet( annTypeIdDisplayPsm, annotationTypesToAddToOutputAnnotationData );
		// Remove annotation type ids that are in default display
		for ( AnnotationTypeDTO item : psm_AnnotationTypeDTO_DefaultDisplay_List_workingList ) {
			annotationTypesToAddToOutputAnnotationData.remove( item.getId() );
		}
		//  Get AnnotationTypeDTO for ids not in default display and sort in name order
		List<AnnotationTypeDTO> psmAnnotationTypesToAddFromQuery = new ArrayList<>();
		if ( ! annotationTypesToAddToOutputAnnotationData.isEmpty() ) {
			//   Add in Psm annotation types the user searched for
			for ( Integer psmAnnotationTypeToAdd : annotationTypesToAddToOutputAnnotationData ) {
				AnnotationTypeDTO annotationTypeDTO = psmFilterableAnnotationTypeDTOMap.get( psmAnnotationTypeToAdd );
				if ( annotationTypeDTO == null ) {
					annotationTypeDTO = psmDescriptiveAnnotationTypeDTOMap.get( psmAnnotationTypeToAdd );
				}
				if ( annotationTypeDTO == null ) {
					String msg = "annotationTypeDTO == null for psmAnnotationTypeToAdd: " + psmAnnotationTypeToAdd;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
				psmAnnotationTypesToAddFromQuery.add( annotationTypeDTO );
			}
			// sort on ann type name
			Collections.sort( psmAnnotationTypesToAddFromQuery, new Comparator<AnnotationTypeDTO>() {
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
		for ( AnnotationTypeDTO annotationTypeDTO : psmAnnotationTypesToAddFromQuery ) {
			psm_AnnotationTypeDTO_DefaultDisplay_List_workingList.add( annotationTypeDTO );
		}
		/////////////////////////////////////////
		//  Do ONLY if "Include" Ann Type Ids
		if ( annTypeIdDisplayPsm != null 
				&& annTypeIdDisplayPsm.getInclAnnTypeId() != null
				&& annTypeIdDisplayPsm.getInclAnnTypeId().length != 0 ) {
			//  Change the psm_AnnotationTypeDTO_DefaultDisplay_List:
			//  Only contain the annotation type ids in  annTypeIdDisplayPsm.getInclAnnTypeId()
			//  Sorted in the order of annTypeIdDisplayPsm.getInclAnnTypeId()
			List<AnnotationTypeDTO> psm_AnnotationTypeDTO_DefaultDisplay_List_OnlyIncludedAnnTypeIds = new ArrayList<>( psm_AnnotationTypeDTO_DefaultDisplay_List_workingList.size() );
			for ( int includedAnnotationTypeId : annTypeIdDisplayPsm.getInclAnnTypeId() ) {
				AnnotationTypeDTO foundAnnotationTypeDTO = null;
				for ( AnnotationTypeDTO listItem : psm_AnnotationTypeDTO_DefaultDisplay_List_workingList ) {
					if ( listItem.getId() == includedAnnotationTypeId ) {
						foundAnnotationTypeDTO = listItem;
						break;
					}
				}
				if ( foundAnnotationTypeDTO == null ) {
					String msg = "No AnnotationTypeDTO found for includedAnnotationTypeId: " + includedAnnotationTypeId;
					log.error( msg );
					throw new ProxlWebappDataException(msg);
				}
				psm_AnnotationTypeDTO_DefaultDisplay_List_OnlyIncludedAnnTypeIds.add( foundAnnotationTypeDTO );
			}
			psm_AnnotationTypeDTO_DefaultDisplay_List_workingList = psm_AnnotationTypeDTO_DefaultDisplay_List_OnlyIncludedAnnTypeIds;
		}
		final List<AnnotationTypeDTO> psm_AnnotationTypeDTO_DefaultDisplay_List = 
				psm_AnnotationTypeDTO_DefaultDisplay_List_workingList;
		////////////////////////////////////
		//  Get set of annotation type ids for getting annotation data
		Set<Integer> annotationTypeIdsForGettingAnnotationData = new HashSet<>();
		for ( AnnotationTypeDTO annotationTypeDTO : psm_AnnotationTypeDTO_SortOrder_List ) {
			annotationTypeIdsForGettingAnnotationData.add( annotationTypeDTO.getId() );
		}
		for ( AnnotationTypeDTO annotationTypeDTO : psm_AnnotationTypeDTO_DefaultDisplay_List ) {
			annotationTypeIdsForGettingAnnotationData.add( annotationTypeDTO.getId() );
		}
		//  Get Annotation data
		//  Internal holder
		List<InternalPsmWebDisplayHolder> psmWebDisplayHolderList = new ArrayList<>( psmWebDisplayList.size() );
		for ( PsmWebDisplayWebServiceResult psmWebDisplayItem : psmWebDisplayList ) {
			int psmId = psmWebDisplayItem.getPsmDTO().getId();
			Map<Integer, PsmAnnotationDTO> psmAnnotationDTOMapOnTypeId = new HashMap<>();
			//  Process annotation type ids to get annotation data
			{
				Set<Integer> notFoundAnnotationTypeIds = new HashSet<>( annotationTypeIdsForGettingAnnotationData );
				List<PsmAnnotationDTO> psmAnnotationDataList = 
						PsmAnnotationDataSearcher.getInstance().getPsmAnnotationDTOList( psmId, annotationTypeIdsForGettingAnnotationData );
				for ( PsmAnnotationDTO psmAnnotationDataItem : psmAnnotationDataList ) {
					psmAnnotationDTOMapOnTypeId.put( psmAnnotationDataItem.getAnnotationTypeId(), psmAnnotationDataItem );
					notFoundAnnotationTypeIds.remove( psmAnnotationDataItem.getAnnotationTypeId() );
				}
				//  Allow PSM Descriptive Annotations to be missing 
				for ( Map.Entry<Integer, AnnotationTypeDTO> entry : psmDescriptiveAnnotationTypeDTOMap.entrySet() ) {
					notFoundAnnotationTypeIds.remove( entry.getKey() );
				}
				if ( ! notFoundAnnotationTypeIds.isEmpty() ) {
					String notFoundAnnotationTypeIdsString = StringUtils.join( notFoundAnnotationTypeIds, ", " );
					String msg = "PSM annotation records not found for these annotation type ids: " + notFoundAnnotationTypeIdsString;
					log.error( msg );
					throw new ProxlWebappDataException(msg);
				}
			}
			InternalPsmWebDisplayHolder internalPsmWebDisplayHolder = new InternalPsmWebDisplayHolder();
			internalPsmWebDisplayHolder.psmWebDisplay = psmWebDisplayItem;
			internalPsmWebDisplayHolder.psmAnnotationDTOMapOnTypeId = psmAnnotationDTOMapOnTypeId;
			psmWebDisplayHolderList.add( internalPsmWebDisplayHolder );
		}
		
		//  Sort PSMs on sort order
		InternalPsmWebDisplayHolderSorter internalPsmWebDisplayHolderSorter = new InternalPsmWebDisplayHolderSorter();
		internalPsmWebDisplayHolderSorter.psm_AnnotationTypeDTO_SortOrder_List = psm_AnnotationTypeDTO_SortOrder_List;
		Collections.sort( psmWebDisplayHolderList, internalPsmWebDisplayHolderSorter );
		
		//  Build output list of PsmWebDisplay
		List<PsmWebDisplayWebServiceResult> psmWebDisplayListOutput = new ArrayList<>( psmWebDisplayHolderList.size() );
		for ( InternalPsmWebDisplayHolder internalPsmWebDisplayHolder : psmWebDisplayHolderList ) {
			//  Get annotations
			List<String> psmAnnotationValueList = new ArrayList<>( psm_AnnotationTypeDTO_DefaultDisplay_List.size() );
			for ( AnnotationTypeDTO annotationTypeDTO : psm_AnnotationTypeDTO_DefaultDisplay_List ) {
				Integer annotationTypeId = annotationTypeDTO.getId();
				PsmAnnotationDTO psmAnnotationDTO = 
						internalPsmWebDisplayHolder.psmAnnotationDTOMapOnTypeId.get( annotationTypeId );
				String annotationValueString = null;
				if ( psmAnnotationDTO != null ) {
					annotationValueString = psmAnnotationDTO.getValueString();
				} else {
					if ( ! psmDescriptiveAnnotationTypeDTOMap.containsKey( annotationTypeId ) ) {
						String msg = "ERROR.  Cannot find AnnotationDTO for type id: " + annotationTypeDTO.getId();
						log.error( msg );
						throw new ProxlWebappDataException(msg);
					}
					//  Allow PSM Descriptive Annotations to be missing 
					annotationValueString = "";
				}
				psmAnnotationValueList.add( annotationValueString );
			}
			PsmWebDisplayWebServiceResult psmWebDisplay = internalPsmWebDisplayHolder.psmWebDisplay;
			psmWebDisplay.setPsmAnnotationValueList( psmAnnotationValueList  );
			psmWebDisplayListOutput.add( psmWebDisplay );
		}
		//  Get PSM column headers data
		List<AnnotationDisplayNameDescription> annotationDisplayNameDescriptionList = new ArrayList<>( psm_AnnotationTypeDTO_DefaultDisplay_List.size() );
		for ( AnnotationTypeDTO annotationTypeDTO : psm_AnnotationTypeDTO_DefaultDisplay_List ) {
			AnnotationDisplayNameDescription annotationDisplayNameDescription = new AnnotationDisplayNameDescription();
			annotationDisplayNameDescription.setDisplayName( annotationTypeDTO.getName() );
			annotationDisplayNameDescription.setDescription( annotationTypeDTO.getDescription() );
			annotationDisplayNameDescriptionList.add( annotationDisplayNameDescription );
		}
		///////////
		PsmsServiceResult psmsServiceResult = new PsmsServiceResult();
		psmsServiceResult.setAnnotationDisplayNameDescriptionList( annotationDisplayNameDescriptionList );
		psmsServiceResult.setPsmWebDisplayList( psmWebDisplayListOutput );
		return psmsServiceResult;
	}
	/**
	 *   Sort PSMs on sort order
	 *
	 */
	private static class InternalPsmWebDisplayHolderSorter implements Comparator<InternalPsmWebDisplayHolder> {
		private List<AnnotationTypeDTO> psm_AnnotationTypeDTO_SortOrder_List;
		@Override
		public int compare(InternalPsmWebDisplayHolder o1, InternalPsmWebDisplayHolder o2) {
			//  Loop through the annotation types (sorted on sort order), comparing the values
			for ( AnnotationTypeDTO annotationTypeDTO : psm_AnnotationTypeDTO_SortOrder_List ) {
				int typeId = annotationTypeDTO.getId();
				PsmAnnotationDTO o1_PsmAnnotationDTO = o1.psmAnnotationDTOMapOnTypeId.get( typeId );
				if ( o1_PsmAnnotationDTO == null ) {
					String msg = "Unable to get PSM Filterable Annotation data for type id: " + typeId;
					log.error( msg );
					throw new RuntimeException(msg);
				}
				double o1Value = o1_PsmAnnotationDTO.getValueDouble();
				PsmAnnotationDTO o2_PsmAnnotationDTO = o2.psmAnnotationDTOMapOnTypeId.get( typeId );
				if ( o2_PsmAnnotationDTO == null ) {
					String msg = "Unable to get PSM Filterable Annotation data for type id: " + typeId;
					log.error( msg );
					throw new RuntimeException(msg);
				}
				double o2Value = o2_PsmAnnotationDTO.getValueDouble();
				if ( o1Value != o2Value ) {
					AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO(); 
					if ( annotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.ABOVE ) {
						if ( o1Value > o2Value ) {
							return -1;
						} else {
							return 1;
						}	
					} else if ( annotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.BELOW ) {
						if ( o1Value < o2Value ) {
							return -1;
						} else {
							return 1;
						}
					} else {
						String msg = "Unexpected value for FilterDirectionType for annotationTypeDTO.id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
				}
			}
			//  If everything matches, sort on psm.id
			return o1.psmWebDisplay.getPsmDTO().getId() - o2.psmWebDisplay.getPsmDTO().getId();
		}
	}
	/**
	 * 
	 *
	 */
	private static class InternalPsmWebDisplayHolder {
		PsmWebDisplayWebServiceResult psmWebDisplay;
		Map<Integer, PsmAnnotationDTO> psmAnnotationDTOMapOnTypeId;
	}
}
