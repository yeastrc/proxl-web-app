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
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetDefaultPsmPeptideCutoffs;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.objects.AnnDisplayNameDescPeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnValuePeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideDimer;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideLooplink;
import org.yeastrc.xlink.www.objects.MergedSearchPeptideUnlinked;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptideWrapper;
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
	/**
	 * Output from this class
	 *
	 */
	public static class PeptidesMergedCommonPageDownloadResult {
		private List<WebMergedReportedPeptide> webMergedReportedPeptideList;
		private MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot;
		private List<AnnDisplayNameDescPeptPsmListsPair> peptidePsmAnnotationNameDescListsForEachSearch;
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel;
		
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
			Map<Integer, SearchDTO> searchesMapOnSearchId
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
		//////    Get data from database per search and put in Map by UnifiedPeptideId then SearchId
		Map<Integer, Map<Integer, WebReportedPeptideWrapper>> webReportedPeptideWrapperMapOnUnifiedPeptideIdSearchId = new HashMap<>();
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
			
			///   Add the WebReportedPeptideWrapper to the map on unified peptide id and search id
			for ( WebReportedPeptideWrapper item : wrappedLinksPerForSearch ) {
				Integer unifiedReportedPeptideId = item.getWebReportedPeptide().getUnifiedReportedPeptideId();
				Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapperMapOnSearchId = 
						webReportedPeptideWrapperMapOnUnifiedPeptideIdSearchId.get( unifiedReportedPeptideId );
				if ( webReportedPeptideWrapperMapOnSearchId == null ) {
					webReportedPeptideWrapperMapOnSearchId = new HashMap<>();
					webReportedPeptideWrapperMapOnUnifiedPeptideIdSearchId.put( unifiedReportedPeptideId, webReportedPeptideWrapperMapOnSearchId );
				}
				webReportedPeptideWrapperMapOnSearchId.put(searchId, item);
			}
		}
		/////////////////////
		//   Transfer into a list
		List<WebMergedReportedPeptideWrapper> webMergedReportedPeptideWrapperList = new ArrayList<>( webReportedPeptideWrapperMapOnUnifiedPeptideIdSearchId.size() );
		for ( Map.Entry<Integer, Map<Integer, WebReportedPeptideWrapper>> entry : webReportedPeptideWrapperMapOnUnifiedPeptideIdSearchId.entrySet() ) {
			WebMergedReportedPeptideWrapper webMergedReportedPeptideWrapper = new WebMergedReportedPeptideWrapper();
			webMergedReportedPeptideWrapperList.add( webMergedReportedPeptideWrapper );
			webMergedReportedPeptideWrapper.setWebReportedPeptideWrapperMapOnSearchId( entry.getValue() );
			WebReportedPeptideWrapper anyWebReportedPeptideWrapper = entry.getValue().entrySet().iterator().next().getValue();
			int unifiedReportedPeptideId = anyWebReportedPeptideWrapper.getWebReportedPeptide().getUnifiedReportedPeptideId();
			webMergedReportedPeptideWrapper.setUnifiedReportedPeptideId( unifiedReportedPeptideId );
		}
		//////////  
		//   Sort Peptides on values in first search, or so on if no data for first search
		WebMergedReportedPeptideWrapperSorter sorter = new WebMergedReportedPeptideWrapperSorter();
		sorter.peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
		sorter.psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
		sorter.searchIdsListDeduppedSorted = searchIdsListDeduppedSorted;
		Collections.sort( webMergedReportedPeptideWrapperList , sorter );
		//  Build output list of WebMergedReportedPeptide 
		List<WebMergedReportedPeptide> webMergedReportedPeptideList = new ArrayList<>( webMergedReportedPeptideWrapperList.size() );
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
		for ( WebMergedReportedPeptideWrapper webMergedReportedPeptideWrapper : webMergedReportedPeptideWrapperList ) {
			WebMergedReportedPeptide webMergedReportedPeptide = new WebMergedReportedPeptide();
			webMergedReportedPeptideList.add(webMergedReportedPeptide);
			webMergedReportedPeptide.setUnifiedReportedPeptideId( webMergedReportedPeptideWrapper.getUnifiedReportedPeptideId() );
			webMergedReportedPeptide.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );
			Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapperMapOnSearchId =
					webMergedReportedPeptideWrapper.getWebReportedPeptideWrapperMapOnSearchId();
			/////////
			//  Get searches for this item
			List<SearchDTO> searchesForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
			List<Integer> searchIdsForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
			Map<Integer, Integer> reportedPeptideIds_KeyedOnSearchId_Map = new HashMap<>();
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
			}
			webMergedReportedPeptide.setSearches( searchesForThisItem );
			webMergedReportedPeptide.setSearchIds( searchIdsForThisItem );
			webMergedReportedPeptide.setNumSearches( searchIdsForThisItem.size() );
			//  Create Merged Peptide object
			WebReportedPeptide anyWebReportedPeptide = 
					webMergedReportedPeptideWrapper.getWebReportedPeptideWrapperMapOnSearchId().entrySet().iterator().next().getValue()
					.getWebReportedPeptide();
			if ( anyWebReportedPeptide.getSearchPeptideCrosslink() != null ) {
				MergedSearchPeptideCrosslink mergedSearchPeptideCrosslink = new MergedSearchPeptideCrosslink();
				mergedSearchPeptideCrosslink.setUnifiedReportedPeptideId( webMergedReportedPeptideWrapper.getUnifiedReportedPeptideId() );
				mergedSearchPeptideCrosslink.setSearches( searchesForThisItem );
				mergedSearchPeptideCrosslink.setReportedPeptideIds_KeyedOnSearchId_Map( reportedPeptideIds_KeyedOnSearchId_Map );
				webMergedReportedPeptide.setMergedSearchPeptideCrosslink( mergedSearchPeptideCrosslink );
			} else if ( anyWebReportedPeptide.getSearchPeptideLooplink() != null ) {
				MergedSearchPeptideLooplink mergedSearchPeptideLooplink = new MergedSearchPeptideLooplink();
				mergedSearchPeptideLooplink.setUnifiedReportedPeptideId( webMergedReportedPeptideWrapper.getUnifiedReportedPeptideId() );
				mergedSearchPeptideLooplink.setSearches( searchesForThisItem );
				mergedSearchPeptideLooplink.setReportedPeptideIds_KeyedOnSearchId_Map( reportedPeptideIds_KeyedOnSearchId_Map );
				webMergedReportedPeptide.setMergedSearchPeptideLooplink( mergedSearchPeptideLooplink );
			} else if ( anyWebReportedPeptide.getSearchPeptideDimer() != null ) {
				MergedSearchPeptideDimer mergedSearchPeptideDimer = new MergedSearchPeptideDimer();
				mergedSearchPeptideDimer.setUnifiedReportedPeptideId( webMergedReportedPeptideWrapper.getUnifiedReportedPeptideId() );
				mergedSearchPeptideDimer.setSearches( searchesForThisItem );
				mergedSearchPeptideDimer.setReportedPeptideIds_KeyedOnSearchId_Map( reportedPeptideIds_KeyedOnSearchId_Map );
				webMergedReportedPeptide.setMergedSearchPeptideDimer( mergedSearchPeptideDimer );
			} else if ( anyWebReportedPeptide.getSearchPeptideUnlinked() != null ) {
				MergedSearchPeptideUnlinked mergedSearchPeptideUnlinked = new MergedSearchPeptideUnlinked();
				mergedSearchPeptideUnlinked.setUnifiedReportedPeptideId( webMergedReportedPeptideWrapper.getUnifiedReportedPeptideId() );
				mergedSearchPeptideUnlinked.setSearches( searchesForThisItem );
				mergedSearchPeptideUnlinked.setReportedPeptideIds_KeyedOnSearchId_Map( reportedPeptideIds_KeyedOnSearchId_Map );
				webMergedReportedPeptide.setMergedSearchPeptideUnlinked( mergedSearchPeptideUnlinked );
			}
			//  Copy Peptide and PSM annotations to display lists 
			List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch = new ArrayList<>( searchIdsListDeduppedSorted.size() );
			webMergedReportedPeptide.setPeptidePsmAnnotationValueListsForEachSearch( peptidePsmAnnotationValueListsForEachSearch );
			for ( Integer searchId : searchIdsListDeduppedSorted ) {
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
						peptideAnnotationValueList.add( annData.getValueString() );
					}
					for ( AnnotationTypeDTO annotationTypeDTO : psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
						AnnotationDataBaseDTO annData = psmAnnotationDTOMap.get( annotationTypeDTO.getId() );
						if ( annData == null ) {
							String msg = "Failed to get annData list for annotationTypeDTO.getId() : " + annotationTypeDTO.getId();
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						psmAnnotationValueList.add( annData.getValueString() );
					}
				}
			}
		}
		PeptidesMergedCommonPageDownloadResult peptidesMergedCommonPageDownloadResult = new PeptidesMergedCommonPageDownloadResult();
		peptidesMergedCommonPageDownloadResult.peptidePsmAnnotationNameDescListsForEachSearch = peptidePsmAnnotationNameDescListsForEachSearch;
		peptidesMergedCommonPageDownloadResult.mergedPeptideQueryJSONRoot = mergedPeptideQueryJSONRoot;
		peptidesMergedCommonPageDownloadResult.webMergedReportedPeptideList = webMergedReportedPeptideList;
		peptidesMergedCommonPageDownloadResult.searcherCutoffValuesRootLevel = searcherCutoffValuesRootLevel;
		
		return peptidesMergedCommonPageDownloadResult;
	}
	
	////////////////////////////////////////
	////////   Sorter Class to Sort Peptides
	/**
	 * 
	 *
	 */
	private class WebMergedReportedPeptideWrapperSorter implements Comparator<WebMergedReportedPeptideWrapper> {

		List<Integer> searchIdsListDeduppedSorted;
		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
		Map<Integer, List<AnnotationTypeDTO>> psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;

		@Override
		public int compare(WebMergedReportedPeptideWrapper o1, WebMergedReportedPeptideWrapper o2) {
			
			Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapperMapOnSearchId_o1 = o1.getWebReportedPeptideWrapperMapOnSearchId();
			Map<Integer, WebReportedPeptideWrapper> webReportedPeptideWrapperMapOnSearchId_o2 = o2.getWebReportedPeptideWrapperMapOnSearchId();
			
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
			return o1.getUnifiedReportedPeptideId() - o2.getUnifiedReportedPeptideId();
		}
	}
}
