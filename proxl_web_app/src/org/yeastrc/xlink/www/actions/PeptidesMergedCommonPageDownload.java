package org.yeastrc.xlink.www.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortAnnotationDTORecords;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.constants.ReportedPeptideCombined_IdentifierFlag_Constants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetDefaultPsmPeptideCutoffs;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.form_utils.Update__A_QueryBase_JSONRoot__ForCurrentSearchIds;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.objects.AnnDisplayNameDescPeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnValuePeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideDimer;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideLooplink;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideUnlinked;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class PeptidesMergedCommonPageDownload {
	
	private static final Logger log = Logger.getLogger( PeptidesMergedCommonPageDownload.class );
	private PeptidesMergedCommonPageDownload() { }
	public static PeptidesMergedCommonPageDownload getInstance() { 
		return new PeptidesMergedCommonPageDownload(); 
	}
	
	public enum FlagCombinedReportedPeptideEntries { YES, NO }
	
	/**
	 * Output from this class
	 *
	 */
	public static class PeptidesMergedCommonPageDownloadResult {
		private List<WebMergedReportedPeptide> webMergedReportedPeptideList;
		private MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot;
		private List<AnnDisplayNameDescPeptPsmListsPair> peptidePsmAnnotationNameDescListsForEachSearch;
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel;
		boolean anyReportedPeptideEntriesWereCombined;
		boolean anyResultsHaveIsotopeLabels;
		
		public List<AnnDisplayNameDescPeptPsmListsPair> getPeptidePsmAnnotationNameDescListsForEachSearch() {
			return peptidePsmAnnotationNameDescListsForEachSearch;
		}
		public void setPeptidePsmAnnotationNameDescListsForEachSearch(
				List<AnnDisplayNameDescPeptPsmListsPair> peptidePsmAnnotationNameDescListsForEachSearch) {
			this.peptidePsmAnnotationNameDescListsForEachSearch = peptidePsmAnnotationNameDescListsForEachSearch;
		}
		public MergedPeptideQueryJSONRoot getMergedPeptideQueryJSONRoot() {
			return mergedPeptideQueryJSONRoot;
		}
		public void setMergedPeptideQueryJSONRoot(
				MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot) {
			this.mergedPeptideQueryJSONRoot = mergedPeptideQueryJSONRoot;
		}
		public List<WebMergedReportedPeptide> getWebMergedReportedPeptideList() {
			return webMergedReportedPeptideList;
		}
		public void setWebMergedReportedPeptideList(
				List<WebMergedReportedPeptide> webMergedReportedPeptideList) {
			this.webMergedReportedPeptideList = webMergedReportedPeptideList;
		}
		public SearcherCutoffValuesRootLevel getSearcherCutoffValuesRootLevel() {
			return searcherCutoffValuesRootLevel;
		}
		public void setSearcherCutoffValuesRootLevel(SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel) {
			this.searcherCutoffValuesRootLevel = searcherCutoffValuesRootLevel;
		}
		public boolean isAnyReportedPeptideEntriesWereCombined() {
			return anyReportedPeptideEntriesWereCombined;
		}
		public void setAnyReportedPeptideEntriesWereCombined(boolean anyReportedPeptideEntriesWereCombined) {
			this.anyReportedPeptideEntriesWereCombined = anyReportedPeptideEntriesWereCombined;
		}
		public boolean isAnyResultsHaveIsotopeLabels() {
			return anyResultsHaveIsotopeLabels;
		}
		public void setAnyResultsHaveIsotopeLabels(boolean anyResultsHaveIsotopeLabels) {
			this.anyResultsHaveIsotopeLabels = anyResultsHaveIsotopeLabels;
		}
	}
	
	/**
	 * @param form
	 * @param projectSearchIdsListDeduppedSorted
	 * @param searches
	 * @param searchesMapOnSearchId
	 * @param projectSearchIdsSet
	 * @return
	 * @throws Exception
	 * @throws ProxlWebappDataException
	 */
	public PeptidesMergedCommonPageDownloadResult getWebMergedPeptideRecords(
			MergedSearchViewPeptidesForm form,
			List<Integer> projectSearchIdsListDeduppedSorted,
			List<SearchDTO> searches, 
			Map<Integer, SearchDTO> searchesMapOnSearchId,
			FlagCombinedReportedPeptideEntries flagCombinedReportedPeptideEntries
			) throws Exception, ProxlWebappDataException {
		
		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		//   Get Query JSON from the form and if not empty, deserialize it
		String queryJSONFromForm = form.getQueryJSON();
		MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot = null;
		if ( StringUtils.isNotEmpty( queryJSONFromForm ) ) {
			try {
				mergedPeptideQueryJSONRoot = jacksonJSON_Mapper.readValue( queryJSONFromForm, MergedPeptideQueryJSONRoot.class );
			} catch ( JsonParseException e ) {
				String msg = "Failed to parse 'queryJSONFromForm', JsonParseException.  queryJSONFromForm: " + queryJSONFromForm;
				log.error( msg, e );
				throw e;
			} catch ( JsonMappingException e ) {
				String msg = "Failed to parse 'queryJSONFromForm', JsonMappingException.  queryJSONFromForm: " + queryJSONFromForm;
				log.error( msg, e );
				throw e;
			} catch ( IOException e ) {
				String msg = "Failed to parse 'queryJSONFromForm', IOException.  queryJSONFromForm: " + queryJSONFromForm;
				log.error( msg, e );
				throw e;
			}

			//  Update mergedPeptideQueryJSONRoot for current search ids and project search ids
			Update__A_QueryBase_JSONRoot__ForCurrentSearchIds.getInstance()
			.update__A_QueryBase_JSONRoot__ForCurrentSearchIds( mergedPeptideQueryJSONRoot, mapProjectSearchIdToSearchId );
			
		} else {
			//  Query JSON in the form is empty so create an empty object that will be populated.
			mergedPeptideQueryJSONRoot = new MergedPeptideQueryJSONRoot();
			//  Create cutoffs for default values
			CutoffValuesRootLevel cutoffValuesRootLevelDefaults =
					GetDefaultPsmPeptideCutoffs.getInstance()
					.getDefaultPsmPeptideCutoffs( projectSearchIdsListDeduppedSorted, searchIds, mapProjectSearchIdToSearchId );
			mergedPeptideQueryJSONRoot.setCutoffs( cutoffValuesRootLevelDefaults );
		}
		//   Update Link Type to default to Crosslink if no value was set
		String[] linkTypesInForm = mergedPeptideQueryJSONRoot.getLinkTypes();
		if ( linkTypesInForm == null || linkTypesInForm.length == 0 ) {
			String[] linkTypesCrosslink = { PeptideViewLinkTypesConstants.CROSSLINK_PSM };
			linkTypesInForm = linkTypesCrosslink;
			mergedPeptideQueryJSONRoot.setLinkTypes( linkTypesInForm );
		}
		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( linkTypesInForm );
		//   Mods for DB Query
		String[] modsForDBQuery = mergedPeptideQueryJSONRoot.getMods();
		////////////
		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = mergedPeptideQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		
		/////////////////////////////////////
		//////    Get data from database per search and put in Map by UnifiedPeptideId then SearchId then Reported Peptide Id
		//  Map<[UnifiedPeptideId], Map<[SearchId], Map<[Reported Peptide Id], WebReportedPeptideWrapper>>>
		Map<Integer, Map<Integer, Map<Integer, WebReportedPeptideWrapper>>> webReportedPeptideWrapper_KeyOn_UnifiedPeptideId_SearchId_ReportedPeptideId = new HashMap<>();
		
		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = new HashMap<>();
		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap = new HashMap<>();
		Map<Integer, List<AnnotationTypeDTO>> psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = new HashMap<>();
		Map<Integer, List<AnnotationTypeDTO>> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap = new HashMap<>();
		
		for ( SearchDTO searchDTO : searches ) {
			Integer projectSearchId = searchDTO.getProjectSearchId();
			Integer searchId = searchDTO.getSearchId();
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
			if ( searcherCutoffValuesSearchLevel == null ) {
				String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			////////////
			/////   Get annotation type data to sort and display data
			//////
			///    Get Peptide AnnotationDTO Sorted In Sort Order and Display order 
			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
			final List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted = new ArrayList<>( peptideCutoffValuesList.size() );
			final List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted = new ArrayList<>( peptideCutoffValuesList.size() );
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : peptideCutoffValuesList ) {
				peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
				peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}
			SortAnnotationDTORecords.getInstance()
			.sortPeptideAnnotationTypeDTOForBestPeptideAnnotations_RecordsSortOrder( peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted );
			
			SortAnnotationDTORecords.getInstance()
			.sortPeptideAnnotationTypeDTOForBestPeptideAnnotations_AnnotationDisplayOrder( peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );
			peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap.put(searchId, peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted );
			peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap.put(searchId, peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );
			/////
			///    Get PSM AnnotationDTO Sorted In Display order 
			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
			final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted = new ArrayList<>( psmCutoffValuesList.size() );
			final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted = new ArrayList<>( psmCutoffValuesList.size() );
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {
				psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
				psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}
			SortAnnotationDTORecords.getInstance()
			.sortPsmAnnotationTypeDTOForBestPsmAnnotations_AnnotationDisplayOrder( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );
			SortAnnotationDTORecords.getInstance()
			.sortPsmAnnotationTypeDTOForBestPsmAnnotations_AnnotationDisplayOrder( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );
			psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap.put(searchId, psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted );
			psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap.put(searchId, psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );
			///////////////////////////////////////////////
			//  Get peptides for this search from the DATABASE
			List<WebReportedPeptideWrapper> wrappedLinksPerForSearch =
					PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
							searchDTO, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery, 
							PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );

			//////////////////////////////////////////////////////////////////
			
			// Filter out links if requested, and Update PSM counts if "remove non-unique PSMs" selected 
			
			if( mergedPeptideQueryJSONRoot.isFilterOnlyOnePSM() 
					|| mergedPeptideQueryJSONRoot.isRemoveNonUniquePSMs() ) {
				///////  Output Lists, Results After Filtering
				List<WebReportedPeptideWrapper> wrappedlinksAfterFilter = new ArrayList<>( wrappedLinksPerForSearch.size() );

				///  Filter links
				for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedLinksPerForSearch ) {
					WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
					// did the user request to removal of links with only Non-Unique PSMs?
					if( mergedPeptideQueryJSONRoot != null && mergedPeptideQueryJSONRoot.isRemoveNonUniquePSMs()  ) {
						//  Update webReportedPeptide object to remove non-unique PSMs
						webReportedPeptide.updateNumPsmsToNotInclude_NonUniquePSMs();
						if ( webReportedPeptide.getNumPsms() <= 0 ) {
							// The number of PSMs after update is now zero
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did the user request to removal of links with only one PSM?
					if( mergedPeptideQueryJSONRoot.isFilterOnlyOnePSM()  ) {
						if ( webReportedPeptide.getNumPsms() <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					wrappedlinksAfterFilter.add( webReportedPeptideWrapper );
				}

				wrappedLinksPerForSearch = wrappedlinksAfterFilter;
			}
			
			///   Add the WebReportedPeptideWrapper to the map on unified peptide id and search id
			for ( WebReportedPeptideWrapper item : wrappedLinksPerForSearch ) {
				Integer unifiedReportedPeptideId = item.getWebReportedPeptide().getUnifiedReportedPeptideId();
				Integer reportedPeptideId = item.getWebReportedPeptide().getReportedPeptideId();
				
			//  Map<[UnifiedPeptideId], Map<[SearchId], Map<[Reported Peptide Id], WebReportedPeptideWrapper>>>
//				Map<Integer, Map<Integer, Map<Integer, WebReportedPeptideWrapper>>> webReportedPeptideWrapper_KeyOn_UnifiedPeptideId_SearchId_ReportedPeptideId = new HashMap<>();
				
				 Map<Integer, Map<Integer, WebReportedPeptideWrapper>> webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId =
						 webReportedPeptideWrapper_KeyOn_UnifiedPeptideId_SearchId_ReportedPeptideId.get( unifiedReportedPeptideId );
				if ( webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId == null ) {
					webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId = new HashMap<>();
					webReportedPeptideWrapper_KeyOn_UnifiedPeptideId_SearchId_ReportedPeptideId.put( unifiedReportedPeptideId, webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId );
				}
				Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapper_KeyOn_ReportedPeptideId =
						webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId.get( searchId );
				if ( webReportedPeptideWrapper_KeyOn_ReportedPeptideId == null ) {
					webReportedPeptideWrapper_KeyOn_ReportedPeptideId = new HashMap<>();
					webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId.put( searchId, webReportedPeptideWrapper_KeyOn_ReportedPeptideId );
//				} else {
//					int z = 0;
				}
				webReportedPeptideWrapper_KeyOn_ReportedPeptideId.put( reportedPeptideId, item);
			}
		}
		
		/////////////////////
		//   Transfer into a list
		List<PerUnifiedReportedPeptideIdIntermediateValueHolder> perUnififiedReportedPeptideIdIntermediateValueHolderList = new ArrayList<>( webReportedPeptideWrapper_KeyOn_UnifiedPeptideId_SearchId_ReportedPeptideId.size() );
		for ( Map.Entry<Integer, Map<Integer,  Map<Integer, WebReportedPeptideWrapper>>> entry : webReportedPeptideWrapper_KeyOn_UnifiedPeptideId_SearchId_ReportedPeptideId.entrySet() ) {
			PerUnifiedReportedPeptideIdIntermediateValueHolder perUnififiedReportedPeptideIdIntermediateValueHolder = new PerUnifiedReportedPeptideIdIntermediateValueHolder();
			perUnififiedReportedPeptideIdIntermediateValueHolderList.add( perUnififiedReportedPeptideIdIntermediateValueHolder );
			perUnififiedReportedPeptideIdIntermediateValueHolder.webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId = entry.getValue();
			WebReportedPeptideWrapper anyWebReportedPeptideWrapper = entry.getValue().entrySet().iterator().next().getValue().entrySet().iterator().next().getValue();
			int unifiedReportedPeptideId = anyWebReportedPeptideWrapper.getWebReportedPeptide().getUnifiedReportedPeptideId();
			perUnififiedReportedPeptideIdIntermediateValueHolder.unifiedReportedPeptideId = unifiedReportedPeptideId;
		}
		
		//  Combine reported peptide entries for the same Unified Reported Peptide Id and Search id
		
		boolean anyReportedPeptideEntriesWereCombined = 
				combineReportedPeptideEntriesForSameUnifiedReportedPeptideIdAndSearchId( perUnififiedReportedPeptideIdIntermediateValueHolderList, searchIds );
		
		//////////  
		//   Sort Peptides on values in first search, or so on if no data for first search
		PerUnififiedReportedPeptideIdIntermediateValueHolderSorter sorter = new PerUnififiedReportedPeptideIdIntermediateValueHolderSorter();
		sorter.peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
		sorter.psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
		sorter.searchIdsListDeduppedSorted = searchIdsListDeduppedSorted;
		Collections.sort( perUnififiedReportedPeptideIdIntermediateValueHolderList , sorter );
		
		
		//  Build output list of WebMergedReportedPeptide 
		List<WebMergedReportedPeptide> webMergedReportedPeptideList = new ArrayList<>( perUnififiedReportedPeptideIdIntermediateValueHolderList.size() );
		List<AnnDisplayNameDescPeptPsmListsPair> peptidePsmAnnotationNameDescListsForEachSearch = new ArrayList<>( searchIdsListDeduppedSorted.size() );
		
		for ( SearchDTO search : searches ) {
			int projectSearchId = search.getProjectSearchId();
			Integer searchId = search.getSearchId(); 
			List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted =
					peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap.get( searchId );
			List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted = 
					psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap.get( searchId );
			if ( peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted == null ) {
				String msg = "Failed to get peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted list for searchId : " + searchId
						+ ", projectSearchId: " + projectSearchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			if ( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted == null ) {
				String msg = "Failed to get psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted list for searchId : " + searchId
						+ ", projectSearchId: " + projectSearchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			AnnDisplayNameDescPeptPsmListsPair annDisplayNameDescPeptPsmListsPair = new AnnDisplayNameDescPeptPsmListsPair();
			peptidePsmAnnotationNameDescListsForEachSearch.add( annDisplayNameDescPeptPsmListsPair );
			List<AnnotationDisplayNameDescription> psmAnnotationNameDescriptionList = new ArrayList<>( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size() );
			List<AnnotationDisplayNameDescription> peptideAnnotationNameDescriptionList = new ArrayList<>( peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size() );
			annDisplayNameDescPeptPsmListsPair.setPsmAnnotationNameDescriptionList( psmAnnotationNameDescriptionList );
			annDisplayNameDescPeptPsmListsPair.setPeptideAnnotationNameDescriptionList( peptideAnnotationNameDescriptionList );
			annDisplayNameDescPeptPsmListsPair.setProjectSearchId( projectSearchId );
			annDisplayNameDescPeptPsmListsPair.setSearchId( searchId );
			for ( AnnotationTypeDTO annotationTypeDTO : psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
				AnnotationDisplayNameDescription annotationDisplayNameDescription = new AnnotationDisplayNameDescription();
				psmAnnotationNameDescriptionList.add( annotationDisplayNameDescription );
				annotationDisplayNameDescription.setDisplayName( annotationTypeDTO.getName() );
				annotationDisplayNameDescription.setDescription( annotationTypeDTO.getDescription() );
			}
			for ( AnnotationTypeDTO annotationTypeDTO : peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
				AnnotationDisplayNameDescription annotationDisplayNameDescription = new AnnotationDisplayNameDescription();
				peptideAnnotationNameDescriptionList.add( annotationDisplayNameDescription );
				annotationDisplayNameDescription.setDisplayName( annotationTypeDTO.getName() );
				annotationDisplayNameDescription.setDescription( annotationTypeDTO.getDescription() );
			}
		}
		//  Create Merged Peptide object
		//  Copy Peptide and PSM annotations to display lists
		for ( PerUnifiedReportedPeptideIdIntermediateValueHolder perUnififiedReportedPeptideIdIntermediateValueHolder : perUnififiedReportedPeptideIdIntermediateValueHolderList ) {
			
			int unifiedReportedPeptideId = perUnififiedReportedPeptideIdIntermediateValueHolder.unifiedReportedPeptideId;
			Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapperMapOnSearchId =
					perUnififiedReportedPeptideIdIntermediateValueHolder.webReportedPeptideWrapper_KeyOn_SearchId;

			WebMergedReportedPeptide webMergedReportedPeptide = new WebMergedReportedPeptide();
			webMergedReportedPeptideList.add(webMergedReportedPeptide);
			webMergedReportedPeptide.setUnifiedReportedPeptideId( perUnififiedReportedPeptideIdIntermediateValueHolder.unifiedReportedPeptideId );

			
			/////////
			//  Get searches, etc for this webMergedReportedPeptide entry
			List<SearchDTO> searchesForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
			List<Integer> searchIdsForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
			Map<Integer, Integer> reportedPeptideIds_KeyedOnSearchId_Map = new HashMap<>();
			int numPsms = 0;
			
			for ( Map.Entry<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapperEntry : webReportedPeptideWrapperMapOnSearchId.entrySet() ) {
				WebReportedPeptideWrapper webReportedPeptideWrapper = webReportedPeptideWrapperEntry.getValue();
				WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
				Integer searchId = webReportedPeptideWrapperEntry.getKey();
				Integer reportedPeptideId = webReportedPeptide.getReportedPeptideId();
				reportedPeptideIds_KeyedOnSearchId_Map.put( searchId, reportedPeptideId );
				SearchDTO searchDTO = searchesMapOnSearchId.get( searchId );
				if ( searchDTO == null ) {
					String msg = "Failed to get searchDTO list for searchId : " + searchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				searchesForThisItem.add( searchDTO );
				searchIdsForThisItem.add(searchId);
				numPsms += webReportedPeptide.getNumPsms();
			}
			
			webMergedReportedPeptide.setSearches( searchesForThisItem );
			webMergedReportedPeptide.setSearchIds( searchIdsForThisItem );
			webMergedReportedPeptide.setNumSearches( searchIdsForThisItem.size() );
			webMergedReportedPeptide.setNumPsms( numPsms );
			
			//  Create Merged Peptide object
			WebReportedPeptide anyWebReportedPeptide = 
					webReportedPeptideWrapperMapOnSearchId.entrySet().iterator().next().getValue()
					.getWebReportedPeptide();
			
			if ( anyWebReportedPeptide.getSearchPeptideCrosslink() != null ) {
				MergedSearchPeptideCrosslink mergedSearchPeptideCrosslink = new MergedSearchPeptideCrosslink();
				mergedSearchPeptideCrosslink.setUnifiedReportedPeptideId( unifiedReportedPeptideId );
				mergedSearchPeptideCrosslink.setSearches( searchesForThisItem );
				mergedSearchPeptideCrosslink.setReportedPeptideIds_KeyedOnSearchId_Map( reportedPeptideIds_KeyedOnSearchId_Map );
				webMergedReportedPeptide.setMergedSearchPeptideCrosslink( mergedSearchPeptideCrosslink );
			} else if ( anyWebReportedPeptide.getSearchPeptideLooplink() != null ) {
				MergedSearchPeptideLooplink mergedSearchPeptideLooplink = new MergedSearchPeptideLooplink();
				mergedSearchPeptideLooplink.setUnifiedReportedPeptideId( unifiedReportedPeptideId );
				mergedSearchPeptideLooplink.setSearches( searchesForThisItem );
				mergedSearchPeptideLooplink.setReportedPeptideIds_KeyedOnSearchId_Map( reportedPeptideIds_KeyedOnSearchId_Map );
				webMergedReportedPeptide.setMergedSearchPeptideLooplink( mergedSearchPeptideLooplink );
			} else if ( anyWebReportedPeptide.getSearchPeptideDimer() != null ) {
				MergedSearchPeptideDimer mergedSearchPeptideDimer = new MergedSearchPeptideDimer();
				mergedSearchPeptideDimer.setUnifiedReportedPeptideId( unifiedReportedPeptideId );
				mergedSearchPeptideDimer.setSearches( searchesForThisItem );
				mergedSearchPeptideDimer.setReportedPeptideIds_KeyedOnSearchId_Map( reportedPeptideIds_KeyedOnSearchId_Map );
				webMergedReportedPeptide.setMergedSearchPeptideDimer( mergedSearchPeptideDimer );
			} else if ( anyWebReportedPeptide.getSearchPeptideUnlinked() != null ) {
				MergedSearchPeptideUnlinked mergedSearchPeptideUnlinked = new MergedSearchPeptideUnlinked();
				mergedSearchPeptideUnlinked.setUnifiedReportedPeptideId( unifiedReportedPeptideId );
				mergedSearchPeptideUnlinked.setSearches( searchesForThisItem );
				mergedSearchPeptideUnlinked.setReportedPeptideIds_KeyedOnSearchId_Map( reportedPeptideIds_KeyedOnSearchId_Map );
				webMergedReportedPeptide.setMergedSearchPeptideUnlinked( mergedSearchPeptideUnlinked );
			} else {
				String msg = "anyWebReportedPeptide does not have SearchPeptide... populated for any type. ReportedPeptideId: " + anyWebReportedPeptide.getReportedPeptideId();
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			
			//  Copy Peptide and PSM annotations to display lists 
			List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch = new ArrayList<>( searchIdsListDeduppedSorted.size() );
			webMergedReportedPeptide.setPeptidePsmAnnotationValueListsForEachSearch( peptidePsmAnnotationValueListsForEachSearch );
			for ( Integer searchId : searchIdsListDeduppedSorted ) {
				
				boolean flagAsCombinedReportedPeptideEntries = false;
				if ( flagCombinedReportedPeptideEntries == FlagCombinedReportedPeptideEntries.YES ) {
					Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId__Submap =
							perUnififiedReportedPeptideIdIntermediateValueHolder.webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId.get( searchId );
					if ( webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId__Submap != null
							&& webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId__Submap.size() > 1 ) {
						
						flagAsCombinedReportedPeptideEntries = true;
					}
				}
				
				AnnValuePeptPsmListsPair annValuePeptPsmListsPair = new AnnValuePeptPsmListsPair();
				peptidePsmAnnotationValueListsForEachSearch.add( annValuePeptPsmListsPair );
				List<String> psmAnnotationValueList = new ArrayList<>();
				List<String> peptideAnnotationValueList = new ArrayList<>();
				annValuePeptPsmListsPair.setPeptideAnnotationValueList( peptideAnnotationValueList );
				annValuePeptPsmListsPair.setPsmAnnotationValueList( psmAnnotationValueList );
				List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted =
						peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap.get( searchId );
				List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted = 
						psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap.get( searchId );
				if ( peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted == null ) {
					String msg = "Failed to get peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted list for searchId : " + searchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				if ( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted == null ) {
					String msg = "Failed to get psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted list for searchId : " + searchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				WebReportedPeptideWrapper webReportedPeptideWrapper = webReportedPeptideWrapperMapOnSearchId.get( searchId );
				if ( webReportedPeptideWrapper == null ) {
					//  Create empty entries in list
					for ( int counter = 0; counter < peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size(); counter++ ) {
						psmAnnotationValueList.add( "" );
					}
					for ( int counter = 0; counter < psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size(); counter++ ) {
						psmAnnotationValueList.add( "" );
					}
				} else {
					//  Populate from wrapper
					Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap = webReportedPeptideWrapper.getPeptideAnnotationDTOMap();
					Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap = webReportedPeptideWrapper.getPsmAnnotationDTOMap();
					for ( AnnotationTypeDTO annotationTypeDTO : peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
						AnnotationDataBaseDTO annData = peptideAnnotationDTOMap.get( annotationTypeDTO.getId() );
						if ( annData == null ) {
							String msg = "Failed to get annData list for annotationTypeDTO.getId() : " + annotationTypeDTO.getId();
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						
						String annotationValueDisplay = annData.getValueString();
						if ( flagAsCombinedReportedPeptideEntries ) {
							//  More than one Reported Peptide entry so the values were combined
							annotationValueDisplay += ReportedPeptideCombined_IdentifierFlag_Constants.REPORTED_PEPTIDE_COMBINED__IDENTIFIER_FLAG;
						}
						peptideAnnotationValueList.add( annotationValueDisplay );
					}
					for ( AnnotationTypeDTO annotationTypeDTO : psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
						AnnotationDataBaseDTO annData = psmAnnotationDTOMap.get( annotationTypeDTO.getId() );
						if ( annData == null ) {
							String msg = "Failed to get annData list for annotationTypeDTO.getId() : " + annotationTypeDTO.getId();
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}

						String annotationValueDisplay = annData.getValueString();
						if ( flagAsCombinedReportedPeptideEntries ) {
							//  More than one Reported Peptide entry so the values were combined
							annotationValueDisplay += ReportedPeptideCombined_IdentifierFlag_Constants.REPORTED_PEPTIDE_COMBINED__IDENTIFIER_FLAG;
						}
						psmAnnotationValueList.add( annotationValueDisplay );
					}
				}
			}
		}
		
		boolean anyResultsHaveIsotopeLabels = false;
		
		for ( WebMergedReportedPeptide item : webMergedReportedPeptideList ) {
			String isotopeLabelsStringPeptide1 = item.getIsotopeLabelsStringPeptide1();
			String isotopeLabelsStringPeptide2 = item.getIsotopeLabelsStringPeptide2();
			if ( StringUtils.isNotEmpty( isotopeLabelsStringPeptide1 ) 
					|| StringUtils.isNotEmpty( isotopeLabelsStringPeptide2 ) ) {
				anyResultsHaveIsotopeLabels = true;
				break;
			}
		}
		
		PeptidesMergedCommonPageDownloadResult peptidesMergedCommonPageDownloadResult = new PeptidesMergedCommonPageDownloadResult();
		peptidesMergedCommonPageDownloadResult.peptidePsmAnnotationNameDescListsForEachSearch = peptidePsmAnnotationNameDescListsForEachSearch;
		peptidesMergedCommonPageDownloadResult.mergedPeptideQueryJSONRoot = mergedPeptideQueryJSONRoot;
		peptidesMergedCommonPageDownloadResult.webMergedReportedPeptideList = webMergedReportedPeptideList;
		peptidesMergedCommonPageDownloadResult.searcherCutoffValuesRootLevel = searcherCutoffValuesRootLevel;
		peptidesMergedCommonPageDownloadResult.anyReportedPeptideEntriesWereCombined = anyReportedPeptideEntriesWereCombined;
		peptidesMergedCommonPageDownloadResult.anyResultsHaveIsotopeLabels = anyResultsHaveIsotopeLabels;
		
		return peptidesMergedCommonPageDownloadResult;
	}
	

	/**
	 * Combine reported peptide entries for the same Unified Reported Peptide Id and Search id
	 * 
	 * @param perUnififiedReportedPeptideIdIntermediateValueHolderList
	 * @return true if any reported peptide entries were combined
	 * @throws Exception 
	 */
	private boolean combineReportedPeptideEntriesForSameUnifiedReportedPeptideIdAndSearchId( 
			
			List<PerUnifiedReportedPeptideIdIntermediateValueHolder> perUnififiedReportedPeptideIdIntermediateValueHolderList,
			Collection<Integer> searchIds
			) throws Exception {
		
		boolean anyReportedPeptideEntriesWereCombined = false;
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> all_Peptide_Descriptive_ForSearchIds =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Descriptive_ForSearchIds( searchIds );
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> all_Peptide_Filterable_ForSearchIds =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIds );
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> all_Psm_Descriptive_ForSearchIds =
				GetAnnotationTypeData.getInstance().getAll_Psm_Descriptive_ForSearchIds( searchIds );

		Map<Integer, Map<Integer, AnnotationTypeDTO>> all_Psm_Filterable_ForSearchIds =
				GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIds );
		
		for ( PerUnifiedReportedPeptideIdIntermediateValueHolder perUnififiedReportedPeptideIdIntermediateValueHolder : perUnififiedReportedPeptideIdIntermediateValueHolderList ) {
			
			Map<Integer, Map<Integer, WebReportedPeptideWrapper>> webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId = perUnififiedReportedPeptideIdIntermediateValueHolder.webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId;
			
			Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapper_KeyOn_SearchId = new HashMap<>( webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId.size() );
			perUnififiedReportedPeptideIdIntermediateValueHolder.webReportedPeptideWrapper_KeyOn_SearchId = webReportedPeptideWrapper_KeyOn_SearchId;
			

			for ( Map.Entry<Integer, Map<Integer, WebReportedPeptideWrapper>> webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId_Entry : webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId.entrySet() ) {
				Integer searchId = webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId_Entry.getKey();

				Map<Integer, AnnotationTypeDTO> peptide_Descriptive_ForSearchId = all_Peptide_Descriptive_ForSearchIds.get( searchId );
				Map<Integer, AnnotationTypeDTO> peptide_Filterable_ForSearchId = all_Peptide_Filterable_ForSearchIds.get( searchId );
				Map<Integer, AnnotationTypeDTO> psm_Descriptive_ForSearchId = all_Psm_Descriptive_ForSearchIds.get( searchId );
				Map<Integer, AnnotationTypeDTO> psm_Filterable_ForSearchId = all_Psm_Filterable_ForSearchIds.get( searchId );

				Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId_Submap = webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId_Entry.getValue();
				if ( webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId_Submap.size() == 1 ) {
					//  Only 1 reported peptide entry so just copy it over
					webReportedPeptideWrapper_KeyOn_SearchId.put( 
							webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId_Entry.getKey(),
							webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId_Submap.entrySet().iterator().next().getValue() );
				} else {
					//  > 1 reported peptide entry so combine them.
					
					anyReportedPeptideEntriesWereCombined = true;
					
					//  Use 'best' annotation values
					//  Add up psm and unique psm counts
					

					//  Combined data for output
					Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap_Combined = new HashMap<>();
					Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap_Combined = new HashMap<>();
							
					int numPsms = 0;
					int numUniquePsms = 0;
					
					WebReportedPeptide webReportedPeptide_Last = null; // here so have last value
					
					boolean firstEntry = true;
					
					for ( Map.Entry<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapper_KeyOn_SearchId_Entry : webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId_Submap.entrySet() ) {
						WebReportedPeptideWrapper webReportedPeptideWrapper = webReportedPeptideWrapper_KeyOn_SearchId_Entry.getValue();
						
						WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
						numPsms += webReportedPeptide.getNumPsms();
						numUniquePsms += webReportedPeptide.getNumUniquePsms();
						
						if ( firstEntry ) {
							//  First entry
							peptideAnnotationDTOMap_Combined.putAll( webReportedPeptideWrapper.getPeptideAnnotationDTOMap() );
							psmAnnotationDTOMap_Combined.putAll( webReportedPeptideWrapper.getPsmAnnotationDTOMap() );
						} else {
							//  Combine Peptide Annotation Data
							combine_Peptide_Or_Psm_AnnotationData(
									webReportedPeptideWrapper.getPeptideAnnotationDTOMap(), 
									peptideAnnotationDTOMap_Combined, 
									peptide_Filterable_ForSearchId, 
									peptide_Descriptive_ForSearchId);

							//  Combine Psm Annotation Data
							combine_Peptide_Or_Psm_AnnotationData(
									webReportedPeptideWrapper.getPsmAnnotationDTOMap(), 
									psmAnnotationDTOMap_Combined, 
									psm_Filterable_ForSearchId, 
									psm_Descriptive_ForSearchId);
						}
						
						webReportedPeptide_Last = webReportedPeptide;
						
						firstEntry = false;
					}
					
					WebReportedPeptideWrapper webReportedPeptideWrapper_New = new WebReportedPeptideWrapper();;
					
					webReportedPeptideWrapper_New.setPeptideAnnotationDTOMap(peptideAnnotationDTOMap_Combined);
					webReportedPeptideWrapper_New.setPsmAnnotationDTOMap( psmAnnotationDTOMap_Combined );
					
					WebReportedPeptide webReportedPeptide_New = new WebReportedPeptide();
					webReportedPeptideWrapper_New.setWebReportedPeptide( webReportedPeptide_New );
					
					webReportedPeptide_New.setSearch( webReportedPeptide_Last.getSearch() );
					webReportedPeptide_New.setReportedPeptide( webReportedPeptide_Last.getReportedPeptide() );
					webReportedPeptide_New.setReportedPeptideId( webReportedPeptide_Last.getReportedPeptideId() );
					webReportedPeptide_New.setUnifiedReportedPeptideId( webReportedPeptide_Last.getUnifiedReportedPeptideId() );
					webReportedPeptide_New.setSearcherCutoffValuesSearchLevel( webReportedPeptide_Last.getSearcherCutoffValuesSearchLevel() );
					
					webReportedPeptide_New.setSearchPeptideCrosslink( webReportedPeptide_Last.getSearchPeptideCrosslink() );
					webReportedPeptide_New.setSearchPeptideLooplink( webReportedPeptide_Last.getSearchPeptideLooplink() );
					webReportedPeptide_New.setSearchPeptideUnlinked( webReportedPeptide_Last.getSearchPeptideUnlinked() );
					webReportedPeptide_New.setSearchPeptideDimer( webReportedPeptide_Last.getSearchPeptideDimer() );
					webReportedPeptide_New.setSearchPeptideMonolink( webReportedPeptide_Last.getSearchPeptideMonolink() );
										
					webReportedPeptide_New.setNumPsms( numPsms );
					webReportedPeptide_New.setNumUniquePsms( numUniquePsms );

					webReportedPeptideWrapper_KeyOn_SearchId.put( 
							webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId_Entry.getKey(),
							webReportedPeptideWrapper_New );

					
				}
			}
		}
		
		return anyReportedPeptideEntriesWereCombined;
	}
	
	/**
	 * @param peptide_Or_Psm_AnnotationDTOMap
	 * @param peptide_Or_Psm_AnnotationDTOMap_Combined
	 * @param peptide_Or_Psm_Filterable_ForSearchId
	 * @param peptide_Or_Psm_Descriptive_ForSearchId
	 * @throws ProxlWebappDataException
	 */
	private void combine_Peptide_Or_Psm_AnnotationData( 
			
			Map<Integer, AnnotationDataBaseDTO> peptide_Or_Psm_AnnotationDTOMap, //  Input
			Map<Integer, AnnotationDataBaseDTO> peptide_Or_Psm_AnnotationDTOMap_Combined, //  Combine with this
			
			Map<Integer, AnnotationTypeDTO> peptide_Or_Psm_Filterable_ForSearchId, // Lookup
			Map<Integer, AnnotationTypeDTO> peptide_Or_Psm_Descriptive_ForSearchId //  Lookup
			 ) throws ProxlWebappDataException {
		
		for ( Map.Entry<Integer, AnnotationDataBaseDTO> peptide_Or_Psm_AnnotationDTO_Entry : peptide_Or_Psm_AnnotationDTOMap.entrySet() ) {
			Integer annotationTypeId = peptide_Or_Psm_AnnotationDTO_Entry.getKey();
			AnnotationDataBaseDTO annotationData = peptide_Or_Psm_AnnotationDTO_Entry.getValue();
			
			AnnotationDataBaseDTO annotationData_Combined = peptide_Or_Psm_AnnotationDTOMap_Combined.get( annotationTypeId );
			if ( annotationData_Combined == null ) {
				peptide_Or_Psm_AnnotationDTOMap_Combined.put( annotationTypeId, annotationData );
			} else {
				//  Get Filterable AnnotationTypeDTO, if filterable
				AnnotationTypeDTO annotationTypeDTO = peptide_Or_Psm_Filterable_ForSearchId.get( annotationTypeId );
				if ( annotationTypeDTO != null ) {
					// Is Filterable
					AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
					if ( annotationTypeFilterableDTO == null ) {
						String msg = "annotationTypeFilterableDTO not populated for Ann Type Filterable. annotationTypeDTO.id: " + annotationTypeDTO.getId();
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					//  Update value with new value if it is the 'best' value
					if ( annotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.ABOVE ) {
						if ( annotationData.getValueDouble() > annotationData_Combined.getValueDouble() ) {
							annotationData_Combined.setValueDouble( annotationData.getValueDouble() );
							annotationData_Combined.setValueString( annotationData.getValueString() );
						}
					} else if ( annotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.BELOW ) {
						if ( annotationData.getValueDouble() < annotationData_Combined.getValueDouble() ) {
							annotationData_Combined.setValueDouble( annotationData.getValueDouble() );
							annotationData_Combined.setValueString( annotationData.getValueString() );
						}
					} else {
						String msg = "FilterDirectionType not above or below. annotationTypeDTO.id: " + annotationTypeDTO.getId();
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
				} else {
					annotationTypeDTO = peptide_Or_Psm_Descriptive_ForSearchId.get( annotationTypeId );
					if ( annotationTypeDTO == null ) {
						String msg = "annotationTypeId is not in Filterable or Descriptive. annotationTypeId: " + annotationTypeId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
				}
			
			}
		}
	}
	
	
	/**
	 * 
	 *
	 */
	private class PerUnifiedReportedPeptideIdIntermediateValueHolder {
		
		int unifiedReportedPeptideId;
		
		Map<Integer, Map<Integer, WebReportedPeptideWrapper>> webReportedPeptideWrapper_KeyOn_SearchId_ReportedPeptideId;
		
		Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapper_KeyOn_SearchId;
		
	}
	
	
	
	////////////////////////////////////////
	////////   Sorter Class to Sort Peptides
	/**
	 * 
	 *
	 */
	private class PerUnififiedReportedPeptideIdIntermediateValueHolderSorter implements Comparator<PerUnifiedReportedPeptideIdIntermediateValueHolder> {

		List<Integer> searchIdsListDeduppedSorted;
		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
		Map<Integer, List<AnnotationTypeDTO>> psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;

		@Override
		public int compare(PerUnifiedReportedPeptideIdIntermediateValueHolder o1, PerUnifiedReportedPeptideIdIntermediateValueHolder o2) {
			
			Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapperMapOnSearchId_o1 = o1.webReportedPeptideWrapper_KeyOn_SearchId;
			Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapperMapOnSearchId_o2 = o2.webReportedPeptideWrapper_KeyOn_SearchId;
			
			for ( Integer searchId : searchIdsListDeduppedSorted ) {
				WebReportedPeptideWrapper webReportedPeptideWrapper_o1 = webReportedPeptideWrapperMapOnSearchId_o1.get( searchId );
				WebReportedPeptideWrapper webReportedPeptideWrapper_o2 = webReportedPeptideWrapperMapOnSearchId_o2.get( searchId );
				if ( webReportedPeptideWrapper_o1 == null && webReportedPeptideWrapper_o2 == null ) {
					continue;  //  Sort on a later search id where there is data
				}
				if ( webReportedPeptideWrapper_o1 == null ) {
					return 1;  //  Sort o1 after o2
				}
				if ( webReportedPeptideWrapper_o2 == null ) {
					return -1; //  Sort o1 before o2  
				}
				
				List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted =
						peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap.get( searchId );
				//  Loop through the Peptide annotation types (sorted on sort order), comparing the values
				for ( AnnotationTypeDTO annotationTypeDTO : peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted ) {
					int typeId = annotationTypeDTO.getId();
					AnnotationDataBaseDTO o1_WebReportedPeptide = webReportedPeptideWrapper_o1.getPeptideAnnotationDTOMap().get( typeId );
					if ( o1_WebReportedPeptide == null ) {
						String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o1Value = o1_WebReportedPeptide.getValueDouble();
					AnnotationDataBaseDTO o2_WebReportedPeptide = webReportedPeptideWrapper_o2.getPeptideAnnotationDTOMap().get( typeId );
					if ( o2_WebReportedPeptide == null ) {
						String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o2Value = o2_WebReportedPeptide.getValueDouble();
					if ( o1Value != o2Value ) {
						if ( o1Value < o2Value ) {
							return -1;
						} else {
							return 1;
						}
					}
				}
				
				//  All peptide data matches or no peptide Ann Data exists so sort on PSM Ann Data

				List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted =
						psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap.get( searchId );

				//  Process through the PSM annotation types (sorted on sort order), comparing the values
				for ( AnnotationTypeDTO annotationTypeDTO : psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted ) {
					int typeId = annotationTypeDTO.getId();
					AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
					if ( annotationTypeFilterableDTO == null ) {
						String msg = "PSM AnnotationTypeFilterableDTO == null for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					FilterDirectionType annTypeFilterDirectionType = annotationTypeFilterableDTO.getFilterDirectionType();
					if ( annTypeFilterDirectionType == null ) {
						String msg = "PSM FilterDirectionType == null for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					
					AnnotationDataBaseDTO o1_WebReportedPeptide = webReportedPeptideWrapper_o1.getPsmAnnotationDTOMap().get( typeId );
					if ( o1_WebReportedPeptide == null ) {
						String msg = "Unable to get PSM Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o1Value = o1_WebReportedPeptide.getValueDouble();
					AnnotationDataBaseDTO o2_WebReportedPeptide = webReportedPeptideWrapper_o2.getPsmAnnotationDTOMap().get( typeId );
					if ( o2_WebReportedPeptide == null ) {
						String msg = "Unable to get PSM Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o2Value = o2_WebReportedPeptide.getValueDouble();
					if ( o1Value != o2Value ) {
						if ( annTypeFilterDirectionType == FilterDirectionType.ABOVE ) {
							if ( o1Value > o2Value ) {
								return -1;
							} else {
								return 1;
							}
						} else {
							if ( o1Value < o2Value ) {
								return -1;
							} else {
								return 1;
							}
						}
					}
				}
				
				//  If everything matches, sort on unified reported peptide id
				return webReportedPeptideWrapper_o1.getWebReportedPeptide().getUnifiedReportedPeptideId()
						- webReportedPeptideWrapper_o2.getWebReportedPeptide().getUnifiedReportedPeptideId();
			}
			 //  No data in the same search so sort on unified reported peptide id
			return o1.unifiedReportedPeptideId - o2.unifiedReportedPeptideId;
		}
	}
}
