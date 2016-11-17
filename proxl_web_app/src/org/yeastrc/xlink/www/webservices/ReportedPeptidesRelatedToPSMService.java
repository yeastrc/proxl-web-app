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
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AnnotationTypeDTOListForSearchId;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem;
import org.yeastrc.xlink.www.objects.ReportedPeptidesRelatedToPSMServiceResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWebserviceWrapper;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideAnnotationDataSearcher;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortAnnotationDTORecords;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.annotation_display.DeserializeAnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataDefaultDisplayInDisplayOrder;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataInSortOrder;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.DeserializeCutoffForWebservices;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode.SearchPeptideWebserviceCommonCodeGetDataResult;

/**
 * 
 *
 */
/**
 * @author danj
 *
 */
@Path("/reportedPeptidesRelatedToPSMService")
public class ReportedPeptidesRelatedToPSMService {

	private static final Logger log = Logger.getLogger(ReportedPeptidesRelatedToPSMService.class);
	
	/**
	 * @param searchId
	 * @param psmPeptideCutoffsForSearchId_JSONString
	 * @param annTypeIdDisplayJSON_PerSearch_JSONString
	 * @param psmId
	 * @param scanId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get") 
	public ReportedPeptidesRelatedToPSMServiceResult get( @QueryParam( "search_id" ) Integer searchId,
										  @QueryParam( "psmPeptideCutoffsForSearchId" ) String psmPeptideCutoffsForSearchId_JSONString,
										  @QueryParam( "peptideAnnTypeDisplayPerSearch" ) String annTypeIdDisplayJSON_PerSearch_JSONString,
										  @QueryParam( "psm_id" ) Integer psmId,
										  @QueryParam( "scan_id" ) Integer scanId,
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
		if ( psmId == null ) {
			String msg = "Provided psm_id is null or psm_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( scanId == null ) {
			String msg = "Provided scan_id is null or scan_id is missing";
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

			//    Get Peptide annotation type ids to include for display
			AnnTypeIdDisplayJSON_PerSearch annTypeIdDisplayJSON_PerSearch = null;
			if ( StringUtils.isNotEmpty( annTypeIdDisplayJSON_PerSearch_JSONString ) ) {
				annTypeIdDisplayJSON_PerSearch =
						DeserializeAnnTypeIdDisplayJSON_PerSearch.getInstance()
						.deserializeAnnTypeIdDisplayJSON_PerSearch( annTypeIdDisplayJSON_PerSearch_JSONString );
			}
			
			//  Get Result
			ReportedPeptidesRelatedToPSMServiceResult reportedPeptidesRelatedToPSMServiceResult = 
					getReportedPeptidesRelatedToPSMData( cutoffValuesSearchLevel, annTypeIdDisplayJSON_PerSearch, searchId, psmId, scanId , searchIdsCollection );
			
			return reportedPeptidesRelatedToPSMServiceResult;
			
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
	 * @param annTypeIdDisplayJSON_PerSearch
	 * @param searchId
	 * @param psmId
	 * @param scanId
	 * @param searchIdsCollection
	 * @return
	 * @throws Exception
	 */
	private ReportedPeptidesRelatedToPSMServiceResult getReportedPeptidesRelatedToPSMData( 
			CutoffValuesSearchLevel cutoffValuesSearchLevel,
			AnnTypeIdDisplayJSON_PerSearch annTypeIdDisplayJSON_PerSearch,
			Integer searchId,
			int psmId,
			int scanId,
			Collection<Integer> searchIdsCollection ) throws Exception {

		List<Integer> peptideDisplayAnnTypeIdList = null;
		if ( annTypeIdDisplayJSON_PerSearch != null ) {
			peptideDisplayAnnTypeIdList = annTypeIdDisplayJSON_PerSearch.getPeptide();
		}
		
		//  Copy cutoff data to searcher cutoff data
		Z_CutoffValuesObjectsToOtherObjects_PerSearchResult z_CutoffValuesObjectsToOtherObjects_PerSearchResult = 
				Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesSearchLevel( 
						searchIdsCollection, cutoffValuesSearchLevel );
		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = z_CutoffValuesObjectsToOtherObjects_PerSearchResult.getSearcherCutoffValuesSearchLevel();
		
		//  Get Reported Peptides From DB:
		List<ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem> reportedPeptideDBList = 
				ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher.getInstance()
				.reportedPeptideRecordsForAssociatedScanId( psmId, scanId, searchId, searcherCutoffValuesSearchLevel ); 
		
		//  Get Annotation Data for links and Sort Links
		SearchPeptideWebserviceCommonCodeGetDataResult searchPeptideWebserviceCommonCodeGetDataResult =
				SearchPeptideWebserviceCommonCode.getInstance()
				.getPeptideAndPSMDataForLinksAndSortLinks( 
						searchId, 
						reportedPeptideDBList, 
						searcherCutoffValuesSearchLevel, 
						peptideDisplayAnnTypeIdList );
		
		List<WebReportedPeptideWebserviceWrapper> webReportedPeptideWebserviceWrapperList = new ArrayList<>( reportedPeptideDBList.size() );
		
		for ( ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem dbItem : reportedPeptideDBList ) {

			WebReportedPeptideWebserviceWrapper webReportedPeptideWebserviceWrapper = new WebReportedPeptideWebserviceWrapper();
			webReportedPeptideWebserviceWrapper.setWebReportedPeptide( dbItem.getWebReportedPeptide() );
			//  Put Annotation data on the link
			SearchPeptideWebserviceCommonCode.getInstance()
			.putPeptideAndPSMDataOnWebserviceResultLinkOject( 
					searchPeptideWebserviceCommonCodeGetDataResult, 
					dbItem, 
					webReportedPeptideWebserviceWrapper);
			webReportedPeptideWebserviceWrapper.setWebReportedPeptide( dbItem.getWebReportedPeptide() );
			webReportedPeptideWebserviceWrapperList.add( webReportedPeptideWebserviceWrapper );
		}
		
		ReportedPeptidesRelatedToPSMServiceResult reportedPeptidesRelatedToPSMServiceResult = new ReportedPeptidesRelatedToPSMServiceResult();
		reportedPeptidesRelatedToPSMServiceResult.setPeptideAnnotationDisplayNameDescriptionList( searchPeptideWebserviceCommonCodeGetDataResult.getPeptideAnnotationDisplayNameDescriptionList() );
		reportedPeptidesRelatedToPSMServiceResult.setPsmAnnotationDisplayNameDescriptionList( searchPeptideWebserviceCommonCodeGetDataResult.getPsmAnnotationDisplayNameDescriptionList() );
		reportedPeptidesRelatedToPSMServiceResult.setWebReportedPeptideWebserviceWrapperList( webReportedPeptideWebserviceWrapperList );
		return reportedPeptidesRelatedToPSMServiceResult;
	}
//	
//	/**
//	 * @param searchId
//	 * @param webReportedPeptideWebserviceWrapperList
//	 * @return
//	 * @throws Exception
//	 */
//	private ReportedPeptidesRelatedToPSMServiceResult getAnnotationDataAndSort(
//			int searchId,
//			List<ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem> webReportedPeptideSearcherResultList,
//			List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted,
//			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesPerAnnotationIdList
//			) throws Exception {
//		
//		Set<Integer> searchIdsSet = new HashSet<>();
//		searchIdsSet.add( searchId );
//		
//		Map<Integer, Map<Integer, AnnotationTypeDTO>> peptideFilterableAnnotationTypesForSearchIds =
//				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsSet );
//		Map<Integer, AnnotationTypeDTO> peptideFilterableAnnotationTypesForSearchId =
//				peptideFilterableAnnotationTypesForSearchIds.get( searchId );
//		if ( peptideFilterableAnnotationTypesForSearchId == null ) {
//			peptideFilterableAnnotationTypesForSearchId = new HashMap<>();
////			String msg = "peptideFilterableAnnotationTypesForSearchId == null for searchId: " + searchId;
////			log.error( msg );
////			throw new ProxlWebappDataException( msg );
//		}
//		Map<Integer, Map<Integer, AnnotationTypeDTO>> peptideDescriptiveAnnotationTypesForSearchIds =
//		GetAnnotationTypeData.getInstance().getAll_Peptide_Descriptive_ForSearchIds( searchIdsSet );
//		Map<Integer, AnnotationTypeDTO> peptideDescriptiveAnnotationTypesForSearchId =
//				peptideDescriptiveAnnotationTypesForSearchIds.get( searchId );
//		if ( peptideDescriptiveAnnotationTypesForSearchId == null ) {
//			peptideDescriptiveAnnotationTypesForSearchId = new HashMap<>();
////			String msg = "peptideDescriptiveAnnotationTypesForSearchId == null for searchId: " + searchId;
////			log.error( msg );
////			throw new ProxlWebappDataException( msg );
//		}		
//		/////////////
//		//   Get Peptide Annotation Types List sorted on Sort Order 
//		Map<Integer, AnnotationTypeDTOListForSearchId> peptideAnnotationTypeDTO_SortOrder_MainMap =
//				GetAnnotationTypeDataInSortOrder.getInstance()
//				.getPeptide_AnnotationTypeDataInSortOrder( searchIdsSet );
//		if ( peptideAnnotationTypeDTO_SortOrder_MainMap.size() != 1 ) {
//			String msg = "getPeptide_AnnotationTypeDataInSortOrder returned other than 1 entry at searchId level ";
//			log.error( msg );
//			throw new ProxlWebappDataException( msg );
//		}
//		/////////////
//		//   Get Peptide Annotation Types List sorted on Display Order 
//		Map<Integer, AnnotationTypeDTOListForSearchId> peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap = 
//				GetAnnotationTypeDataDefaultDisplayInDisplayOrder.getInstance()
//				.getPeptideAnnotationTypeDataDefaultDisplayInDisplayOrder( searchIdsSet );
//		if ( peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.isEmpty() ) {
//			String msg = "getPeptide_AnnotationTypeDataDefaultDisplayInDisplayOrder returned empty Map at searchId level, searchId: " + searchId;
//			log.error( msg );
//			throw new ProxlWebappDataException( msg );
//		}
//		if ( peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.size() != 1 ) {
//			String msg = "getPeptide_AnnotationTypeDataDefaultDisplayInDisplayOrder returned other than 1 entry at searchId level , searchId: " + searchId;
//			log.error( msg );
//			throw new ProxlWebappDataException( msg );
//		}
//		AnnotationTypeDTOListForSearchId peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_Main =
//				peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.get( searchId );
//		if ( peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_Main == null ) {
//			String msg = "peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_Main == null for searchId: " + searchId;
//			log.error( msg );
//			throw new ProxlWebappDataException( msg );
//		}
//		AnnotationTypeDTOListForSearchId peptideAnnotationTypeDTO_DefaultDisplay_SortOrder_Main =
//				peptideAnnotationTypeDTO_SortOrder_MainMap.get( searchId );
//		if ( peptideAnnotationTypeDTO_DefaultDisplay_SortOrder_Main == null ) {
//			String msg = "peptideAnnotationTypeDTO_DefaultDisplay_SortOrder_Main == null for searchId: " + searchId;
//			log.error( msg );
//			throw new ProxlWebappDataException( msg );
//		}
//		final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List = 
//				peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_Main.getAnnotationTypeDTOList();
//		final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_SortOrder_List = 
//				peptideAnnotationTypeDTO_DefaultDisplay_SortOrder_Main.getAnnotationTypeDTOList();
//		/////////////////////////////////////////
//		///   Create sets of annotation type ids that were searched for but are not displayed by default.
//		///   Those annotation values will be displayed after the default, in name order
//		Set<Integer> peptideAnnotationTypesSearchedFor = new HashSet<>();
//		for (  SearcherCutoffValuesAnnotationLevel peptideCutoffEntry : peptideCutoffValuesPerAnnotationIdList ) {
//			int annTypeId = peptideCutoffEntry.getAnnotationTypeId();
//			peptideAnnotationTypesSearchedFor.add( annTypeId );
//		}
//		// Remove annotation type ids that are in default display
//		for ( AnnotationTypeDTO item : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {
//			peptideAnnotationTypesSearchedFor.remove( item.getId() );
//		}
//		//  Get AnnotationTypeDTO for ids not in default display and sort in name order
//		List<AnnotationTypeDTO> peptideAnnotationTypesToAddFromQuery = new ArrayList<>();
//		if ( ! peptideAnnotationTypesSearchedFor.isEmpty() ) {
//			//   Add in Peptide annotation types the user searched for
//			for ( Integer peptideAnnotationTypeToAdd : peptideAnnotationTypesSearchedFor ) {
//				AnnotationTypeDTO annotationTypeDTO = peptideFilterableAnnotationTypesForSearchId.get( peptideAnnotationTypeToAdd );
//				if ( annotationTypeDTO == null ) {
//				}
//				peptideAnnotationTypesToAddFromQuery.add( annotationTypeDTO );
//			}
//			// sort on ann type name
//			Collections.sort( peptideAnnotationTypesToAddFromQuery, new Comparator<AnnotationTypeDTO>() {
//				@Override
//				public int compare(AnnotationTypeDTO o1,
//						AnnotationTypeDTO o2) {
//					return o1.getName().compareTo( o2.getName() );
//				}
//			} );
//		}
//		//   Add the searched for but not in default display AnnotationTypeDTO 
//		//   to the default display list.
//		//   The annotation data will be loaded from the DB in the searcher since they were searched for
//		for ( AnnotationTypeDTO annotationTypeDTO : peptideAnnotationTypesToAddFromQuery ) {
//			reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.add( annotationTypeDTO );
//		}
//		//////////////////////
//		//  Get set of peptide annotation type ids to retrieve annotation data for
//		Set<Integer> peptideAnnotationTypeIdsForAnnotationDataRetrieval = new HashSet<>(); 
//		for ( AnnotationTypeDTO annotationTypeItem : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {
//			peptideAnnotationTypeIdsForAnnotationDataRetrieval.add( annotationTypeItem.getId() );
//		}
//		for ( AnnotationTypeDTO annotationTypeItem : reportedPeptide_AnnotationTypeDTO_SortOrder_List ) {
//			peptideAnnotationTypeIdsForAnnotationDataRetrieval.add( annotationTypeItem.getId() );
//		}
//		//////////////////////////////////////////
//		//  Get Peptide Annotation data for Sort and Display
//		for ( ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem webReportedPeptideSearcherResultItem : webReportedPeptideSearcherResultList ) {
//			WebReportedPeptide webReportedPeptide = webReportedPeptideSearcherResultItem.getWebReportedPeptide();
//			Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap = webReportedPeptideSearcherResultItem.getPeptideAnnotationDTOMap();
//			if ( peptideAnnotationDTOMap == null ) {
//				peptideAnnotationDTOMap = new HashMap<>();
//				webReportedPeptideSearcherResultItem.setPeptideAnnotationDTOMap( peptideAnnotationDTOMap );
//			}
//			if ( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List != null 
//					&& ( ! reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.isEmpty() ) ) {
//				List<SearchReportedPeptideAnnotationDTO> searchReportedPeptideFilterableAnnotationDataList = 
//						SearchReportedPeptideAnnotationDataSearcher.getInstance()
//						.getSearchReportedPeptideAnnotationDTOList( 
//								searchId, webReportedPeptide.getReportedPeptideId(), peptideAnnotationTypeIdsForAnnotationDataRetrieval );
//				for ( SearchReportedPeptideAnnotationDTO searchReportedPeptideFilterableAnnotationDataItem : searchReportedPeptideFilterableAnnotationDataList ) {
//					peptideAnnotationDTOMap.put( searchReportedPeptideFilterableAnnotationDataItem.getAnnotationTypeId(), searchReportedPeptideFilterableAnnotationDataItem );
//				}
//			}
//		}
//		/////////////////////
//		//   Copy Annotation Display Name and Descriptions to output lists, used for table headers in the HTML
//		List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList = new ArrayList<>( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.size() );
//		List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList = new ArrayList<>( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size() );
//		for ( AnnotationTypeDTO item : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {
//			AnnotationDisplayNameDescription output = new AnnotationDisplayNameDescription();
//			output.setDisplayName( item.getName() );
//			output.setDescription( item.getDescription() );
//			peptideAnnotationDisplayNameDescriptionList.add(output);
//		}
//		for ( AnnotationTypeDTO item : psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
//			AnnotationDisplayNameDescription output = new AnnotationDisplayNameDescription();
//			output.setDisplayName( item.getName() );
//			output.setDescription( item.getDescription() );
//			psmAnnotationDisplayNameDescriptionList.add(output);
//		}
//ss		List<WebReportedPeptideWebserviceWrapper> webReportedPeptideWebserviceWrapperList = null;
//		{
//			//////////////////////////////////////////
//			//  Sort Peptides on sort order
//			WebReportedPeptideWrapperSorter webReportedPeptideWrapperSorter = new WebReportedPeptideWrapperSorter();
//			webReportedPeptideWrapperSorter.reportedPeptide_AnnotationTypeDTO_SortOrder_List = 
//					reportedPeptide_AnnotationTypeDTO_SortOrder_List;
//			Collections.sort( webReportedPeptideSearcherResultList, webReportedPeptideWrapperSorter );
//			//  Copy the links out of the wrappers for output - and Copy searched for peptide and psm annotations to link
//			webReportedPeptideWebserviceWrapperList = new ArrayList<>( webReportedPeptideSearcherResultList.size() );
//			for ( ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem fromSearcherResultItem : webReportedPeptideSearcherResultList ) {
//				WebReportedPeptide webReportedPeptide = fromSearcherResultItem.getWebReportedPeptide();
//				//  Copy searched for peptide and psm annotations to link
//				List<String> peptideAnnotationValueList = new ArrayList<>( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.size() );
//				List<String> psmAnnotationValueList = new ArrayList<>( 20 );
//				Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap = fromSearcherResultItem.getPeptideAnnotationDTOMap();
//				Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap = fromSearcherResultItem.getPsmAnnotationDTOMap();
//				if (  peptideAnnotationDTOMap == null ) {
//					String msg = "  webReportedPeptideWrapper.getPeptideAnnotationDTOMap() is null ";
//					log.error( msg );
//					throw new ProxlWebappDataException(msg);
//				}
//				if ( psmAnnotationDTOMap == null ) {
//					String msg = "  webReportedPeptideWrapper.getPsmAnnotationDTOMap() is null ";
//					log.error( msg );
//					throw new ProxlWebappDataException(msg);
//				}
//				for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {
//					Integer annotationTypeId = annotationTypeDTO.getId();
//					AnnotationDataBaseDTO annotationDataBaseDTO = peptideAnnotationDTOMap.get( annotationTypeId );
//					String annotationValueString = null;
//					if ( annotationDataBaseDTO != null ) {
//						annotationValueString = annotationDataBaseDTO.getValueString();
//					} else {
//						if ( ! peptideDescriptiveAnnotationTypesForSearchId.containsKey( annotationTypeId ) ) {
//							String msg = "ERROR.  Cannot find AnnotationDTO for type id: " + annotationTypeDTO.getId();
//							log.error( msg );
//							throw new ProxlWebappDataException(msg);
//						}
//						//  Allow Peptide Descriptive Annotations to be missing 
//						annotationValueString = "";
//					}
//					peptideAnnotationValueList.add( annotationValueString );
//				}
//				// Add sorted Best PSM data to webDisplayItem from webDisplayItemWrapper
//				for ( AnnotationTypeDTO annotationTypeDTO : psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
//					AnnotationDataBaseDTO annotationDataBaseDTO = psmAnnotationDTOMap.get( annotationTypeDTO.getId() );
//					if ( annotationDataBaseDTO == null ) {
//						String msg = "Unable to find annotation data for type id: " + annotationTypeDTO.getId();
//						log.error( msg );
//						throw new ProxlWebappDataException(msg);
//					}
//					psmAnnotationValueList.add( annotationDataBaseDTO.getValueString() );
//				}
//				webReportedPeptide.setPeptideAnnotationValueList( peptideAnnotationValueList );
//				webReportedPeptide.setPsmAnnotationValueList( psmAnnotationValueList );
//				WebReportedPeptideWebserviceWrapper webReportedPeptideWebserviceWrapper = new WebReportedPeptideWebserviceWrapper();
//				webReportedPeptideWebserviceWrapper.setWebReportedPeptide( webReportedPeptide );
//				webReportedPeptideWebserviceWrapperList.add( webReportedPeptideWebserviceWrapper );
//			}
//		}
//		///////////////
//		ReportedPeptidesRelatedToPSMServiceResult reportedPeptidesRelatedToPSMServiceResult = new ReportedPeptidesRelatedToPSMServiceResult();
//		reportedPeptidesRelatedToPSMServiceResult.setPeptideAnnotationDisplayNameDescriptionList( peptideAnnotationDisplayNameDescriptionList );
//		reportedPeptidesRelatedToPSMServiceResult.setPsmAnnotationDisplayNameDescriptionList( psmAnnotationDisplayNameDescriptionList );
//		reportedPeptidesRelatedToPSMServiceResult.setWebReportedPeptideWebserviceWrapperList( webReportedPeptideWebserviceWrapperList );
//		return reportedPeptidesRelatedToPSMServiceResult;
//	}
//	////////////////////////////////////////
//	////////   Sorter Class to Sort Peptides
//	/**
//	 * 
//	 *
//	 */
//	private class WebReportedPeptideWrapperSorter implements Comparator<ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem> {
//		List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_SortOrder_List;
//		@Override
//		public int compare(ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem o1, ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem o2) {
//			//  Loop through the annotation types (sorted on sort order), comparing the values
//			for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_SortOrder_List ) {
//				int typeId = annotationTypeDTO.getId();
//				AnnotationDataBaseDTO o1_WebReportedPeptide = o1.getPeptideAnnotationDTOMap().get( typeId );
//				if ( o1_WebReportedPeptide == null ) {
//					String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
//					log.error( msg );
//					throw new RuntimeException(msg);
//				}
//				double o1Value = o1_WebReportedPeptide.getValueDouble();
//				AnnotationDataBaseDTO o2_WebReportedPeptide = o2.getPeptideAnnotationDTOMap().get( typeId );
//				if ( o2_WebReportedPeptide == null ) {
//					String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
//					log.error( msg );
//					throw new RuntimeException(msg);
//				}
//				double o2Value = o2_WebReportedPeptide.getValueDouble();
//				if ( o1Value != o2Value ) {
//					if ( o1Value < o2Value ) {
//						return -1;
//					} else {
//						return 1;
//					}
//				}
//			}
//			//  If everything matches, sort on reported peptide id
//			return o1.getWebReportedPeptide().getReportedPeptideId() - o2.getWebReportedPeptide().getReportedPeptideId();
//		}
//	}
}
