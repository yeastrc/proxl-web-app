package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.actions.ProteinsAllCommonAll.ProteinSingleEntry;
import org.yeastrc.xlink.www.actions.ProteinsAllCommonAll.ProteinsAllCommonAllResult;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortAnnotationDTORecords;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.form_utils.GetProteinQueryJSONRootFromFormData;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.AnnDisplayNameDescPeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnValuePeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.IMergedSearchLink;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.SearchBooleanWrapper;
import org.yeastrc.xlink.www.objects.SearchProtein;

/**
 *  Common code for Merged All Proteins and Download Merged All Proteins 
 *
 */
public class ProteinsAllMergedCommonPageDownload {
	
	private static final Logger log = LoggerFactory.getLogger(  ProteinsAllMergedCommonPageDownload.class );
	private ProteinsAllMergedCommonPageDownload() { }
	public static ProteinsAllMergedCommonPageDownload getInstance() { 
		return new ProteinsAllMergedCommonPageDownload(); 
	}

	/**
	 * @param form
	 * @param projectSearchIdsListDeduppedSorted
	 * @param searches
	 * @param searchesMapOnSearchId
	 * @return
	 * @throws Exception
	 * @throws ProxlWebappDataException
	 */
	public AllProteinsMergedCommonPageDownloadResult getAllProteinsWrapped(
			MergedSearchViewProteinsForm form,
			List<Integer> projectSearchIdsListDeduppedSorted,
			List<SearchDTO> searches, 
			Map<Integer, SearchDTO> searchesMapOnSearchId
			) throws Exception, ProxlWebappDataException {
		
		AllProteinsMergedCommonPageDownloadResult allProteinsMergedCommonPageDownloadResult = new AllProteinsMergedCommonPageDownloadResult();
		
		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}
		/////////////////////
		// all possible proteins for Proteins across all searches (for "Exclude Protein" list on web page)
		Map<Integer,Set<Integer>> allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds = new HashMap<>();
		allProteinsMergedCommonPageDownloadResult.allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds =
				allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds;
		//   Get Query JSON from the form and if not empty, deserialize it
		ProteinQueryJSONRoot proteinQueryJSONRoot = 
				GetProteinQueryJSONRootFromFormData.getInstance()
				.getProteinQueryJSONRootFromFormData( 
						form, 
						projectSearchIdsListDeduppedSorted,
						searchIds,
						mapProjectSearchIdToSearchId );
		allProteinsMergedCommonPageDownloadResult.proteinQueryJSONRoot = proteinQueryJSONRoot;
		
		////////////
		//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup
		Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
		Set<Integer> excludeproteinSequenceVersionIds_Set_UserInput = new HashSet<>();
		allProteinsMergedCommonPageDownloadResult.excludeTaxonomy_Ids_Set_UserInput = excludeTaxonomy_Ids_Set_UserInput;
		allProteinsMergedCommonPageDownloadResult.excludeproteinSequenceVersionIds_Set_UserInput = excludeproteinSequenceVersionIds_Set_UserInput;
		if ( proteinQueryJSONRoot.getExcludeTaxonomy() != null ) {
			for ( Integer taxonomyId : proteinQueryJSONRoot.getExcludeTaxonomy() ) {
				excludeTaxonomy_Ids_Set_UserInput.add( taxonomyId );
			}
		}
		//  First convert the protein sequence ids that come from the JS code to standard integers and put
		//   in the property excludeproteinSequenceVersionIds
		ProteinsMergedProteinsCommon.getInstance().processExcludeproteinSequenceVersionIdsFromJS( proteinQueryJSONRoot );
		if ( proteinQueryJSONRoot.getExcludeproteinSequenceVersionIds() != null ) {
			for ( Integer proteinId : proteinQueryJSONRoot.getExcludeproteinSequenceVersionIds() ) {
				excludeproteinSequenceVersionIds_Set_UserInput.add( proteinId );
			}
		}

		/////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////
		////////   Generic Param processing
		
		CutoffValuesRootLevel cutoffValuesRootLevel = proteinQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( 
						searchIdsListDeduppedSorted, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		/////////////////////////////////////
		//////    Get data from database per search and put in Map by Primary Key then SearchId
		//   Primary Key is protein sequence id
		Map<Integer, Map<Integer, ProteinSingleEntry>> proteinMapOnproteinSequenceVersionIdSubKeySearchId = new HashMap<>();
		
		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = new HashMap<>();
		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap = new HashMap<>();
		Map<Integer, List<AnnotationTypeDTO>> psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = new HashMap<>();
		Map<Integer, List<AnnotationTypeDTO>> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap = new HashMap<>();
		
		///////////////////////
		////    Get the Protein data per search
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
			///    Get PSM AnnotationDTO Sorted In Sort Order and Display order 
			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
			final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted = new ArrayList<>( psmCutoffValuesList.size() );
			final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted = new ArrayList<>( psmCutoffValuesList.size() );
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {
				psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
				psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}
			SortAnnotationDTORecords.getInstance()
			.sortPsmAnnotationTypeDTOForBestPsmAnnotations_RecordsSortOrder( psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted );
			SortAnnotationDTORecords.getInstance()
			.sortPsmAnnotationTypeDTOForBestPsmAnnotations_AnnotationDisplayOrder( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );
			psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap.put(searchId, psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted );
			psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap.put(searchId, psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );
			
			//  Get Protein Data from DATABASE

			ProteinsAllCommonAllResult proteinsAllCommonAllResult =
					ProteinsAllCommonAll.getInstance().getProteinSingleEntryList(
							null /* onlyReturnThisproteinSequenceVersionId */, 
							searchDTO, 
							searchId,
							proteinQueryJSONRoot, 
							excludeTaxonomy_Ids_Set_UserInput, 
							excludeproteinSequenceVersionIds_Set_UserInput,
							searcherCutoffValuesSearchLevel );

			List<ProteinSingleEntry> proteinSingleEntryList = proteinsAllCommonAllResult.getProteinSingleEntryList();
			Set<SearchProtein> searchProteinUnfilteredForSearch = proteinsAllCommonAllResult.getSearchProteinUnfilteredForSearch();
					
			/////////////////////////////////////////////////////////
			//////////    Add to list of all proteins (for "Exclude Protein" list on web page) 
			for ( SearchProtein  searchProtein : searchProteinUnfilteredForSearch ) {
				Integer searchProtein_id = searchProtein.getProteinSequenceVersionObject().getProteinSequenceVersionId();
				Set<Integer> searchIdsForProtein =
						allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id );
				if ( searchIdsForProtein == null ) {
					searchIdsForProtein = new HashSet<>();
					allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id, searchIdsForProtein );
				}
				searchIdsForProtein.add( searchId );
			}
			
			//  Add ProteinSingleEntry to map on proteinSequenceVersionId SubKey SearchId
			for ( ProteinSingleEntry proteinSingleEntry : proteinSingleEntryList ) {

				Map<Integer, ProteinSingleEntry>  proteinMapOnSearchId = 
						proteinMapOnproteinSequenceVersionIdSubKeySearchId.get( proteinSingleEntry.getProteinSequenceVersionId() );
				if ( proteinMapOnSearchId == null ) {
					proteinMapOnSearchId = new HashMap<>();
					proteinMapOnproteinSequenceVersionIdSubKeySearchId.put( proteinSingleEntry.getProteinSequenceVersionId(), proteinMapOnSearchId );
				}
				ProteinSingleEntry prevProteinSingleEntry =
						proteinMapOnSearchId.put( searchId, proteinSingleEntry );
				if ( prevProteinSingleEntry != null ) {
					String msg = "proteinSingleEntry already contains entry for search id: " + searchId
							+ ", proteinSequenceVersionId: " + proteinSingleEntry.getProteinSequenceVersionId();
					log.error( msg );
					throw new ProxlWebappDataException(msg);
				}
			}
		}  //  End processing by Search Id
		
		/////////////////////////
		//////////   Get the list of column headers for the searches
		List<AnnDisplayNameDescPeptPsmListsPair> peptidePsmAnnotationNameDescListsForEachSearch = new ArrayList<>( searchIdsListDeduppedSorted.size() );
		
		for ( SearchDTO searchDTO : searches ) {
			Integer projectSearchId = searchDTO.getProjectSearchId();
			Integer searchId = searchDTO.getSearchId();
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

		/////           
		////     Prepare Proteins for display
		/////////////////////
		//   Transfer Proteins into a list of MergedProteinSingleEntry
		List<MergedProteinSingleEntry> mergedProteinSingleEntryList = new ArrayList<>( proteinMapOnproteinSequenceVersionIdSubKeySearchId.size() );
		
		for ( Map.Entry<Integer, Map<Integer, ProteinSingleEntry>> entry : proteinMapOnproteinSequenceVersionIdSubKeySearchId.entrySet() ) {
			Map<Integer, ProteinSingleEntry> proteinMapOnSearchId = entry.getValue();
			MergedProteinSingleEntry mergedProteinSingleEntry = new MergedProteinSingleEntry();
			mergedProteinSingleEntryList.add( mergedProteinSingleEntry );
			mergedProteinSingleEntry.setProteinMapOnSearchId( proteinMapOnSearchId );
			ProteinSingleEntry anyProteinSingleEntry = proteinMapOnSearchId.entrySet().iterator().next().getValue();
			mergedProteinSingleEntry.setProteinSequenceVersionId( anyProteinSingleEntry.getProteinSequenceVersionId() );
		}
		//////////  
		//   Sort Proteins on values in first search, or so on if no data for first search
		MergedProteinSingleEntrySorter proteinSorter = new MergedProteinSingleEntrySorter();
		proteinSorter.sortAnnDataCommon.peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
		proteinSorter.sortAnnDataCommon.psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
		proteinSorter.searchIdsListDeduppedSorted = searchIdsListDeduppedSorted;
		Collections.sort( mergedProteinSingleEntryList , proteinSorter );

		//  Create Merged Protein object
		//     set Peptide, unique Peptide, and PSM counts 
		//  Copy Peptide and PSM annotations to display lists
		for ( MergedProteinSingleEntry mergedProteinSingleEntry : mergedProteinSingleEntryList ) {
			
			Map<Integer, ProteinSingleEntry> proteinMapOnSearchId = mergedProteinSingleEntry.getProteinMapOnSearchId();
			/////////
			//  Get searches for this item
			List<SearchDTO> searchesForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
			List<Integer> searchIdsForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
			Map<SearchDTO, ProteinSingleEntry> proteinSingleEntryMapOnSearchDTOForThisItem = new TreeMap<>();
			int numPsms = 0;
			Set<Integer> associatedReportedPeptideIdsMergedSet = new HashSet<>();
			Set<Integer> associatedReportedPeptideIdsRelatedPeptidesUniqueMergedSet = new HashSet<>();
			for ( Map.Entry<Integer, ProteinSingleEntry> proteinEntry : proteinMapOnSearchId.entrySet() ) {
				Integer searchId = proteinEntry.getKey();
				ProteinSingleEntry proteinSingleEntry = proteinEntry.getValue();
				SearchDTO searchDTO = searchesMapOnSearchId.get( searchId );
				if ( searchDTO == null ) {
					String msg = "Failed to get searchDTO list for searchId : " + searchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				searchesForThisItem.add( searchDTO );
				searchIdsForThisItem.add( searchId );
				numPsms += proteinSingleEntry.getNumPsms();
				associatedReportedPeptideIdsMergedSet.addAll( proteinSingleEntry.getReportedPeptideIds() );
				associatedReportedPeptideIdsRelatedPeptidesUniqueMergedSet.addAll( proteinSingleEntry.getReportedPeptideIdsRelatedPeptidesUnique() );
				proteinSingleEntryMapOnSearchDTOForThisItem.put(searchDTO, proteinSingleEntry );
			}
			ProteinSequenceVersionObject ProteinSequenceObject = new ProteinSequenceVersionObject();
			ProteinSequenceObject.setProteinSequenceVersionId( mergedProteinSingleEntry.getProteinSequenceVersionId() );
			MergedSearchProtein protein = new MergedSearchProtein( searchesForThisItem, ProteinSequenceObject );
			
			mergedProteinSingleEntry.setProtein( protein );
			mergedProteinSingleEntry.setSearches( searchesForThisItem );
			mergedProteinSingleEntry.setNumPsms( numPsms );
			mergedProteinSingleEntry.setNumPeptides( associatedReportedPeptideIdsMergedSet.size() );
			mergedProteinSingleEntry.setNumUniquePeptides( associatedReportedPeptideIdsRelatedPeptidesUniqueMergedSet.size() );
			
			//  Copy Peptide and PSM annotations to display lists 
			List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch = new ArrayList<>( searchIdsListDeduppedSorted.size() );
			mergedProteinSingleEntry.setPeptidePsmAnnotationValueListsForEachSearch( peptidePsmAnnotationValueListsForEachSearch );
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
				ProteinSingleEntry proteinSingleEntry = proteinMapOnSearchId.get( searchId );
				if ( proteinSingleEntry == null ) {
					//  Create empty entries in list
					for ( int counter = 0; counter < peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size(); counter++ ) {
						psmAnnotationValueList.add( "" );
					}
					for ( int counter = 0; counter < psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size(); counter++ ) {
						psmAnnotationValueList.add( "" );
					}
				} else {
					//  Populate from wrapper
					Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap = proteinSingleEntry.getPeptideAnnotationDTOMap();
					Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap = proteinSingleEntry.getPsmAnnotationDTOMap();
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
			}  //  END  for ( Integer searchId : searchIdsListDeduppedSorted ) {
		}  //  END:   for ( MergedProteinSingleEntry mergedProteinSingleEntry : mergedProteinSingleEntryList ) {

		////////////////////////////////////////////
		/////////   Add SearchBooleanWrapper for the searches for showing which searches the protein is in
		for( MergedProteinSingleEntry mergedProteinSingleEntry : mergedProteinSingleEntryList ) {
			List<SearchBooleanWrapper> booleanWrapper = new ArrayList<SearchBooleanWrapper>( searches.size() );
			for( SearchDTO search : searches ) {
				if( mergedProteinSingleEntry.getSearches().contains( search ) ) {
					booleanWrapper.add( new SearchBooleanWrapper( search, true ) );
				} else {
					booleanWrapper.add( new SearchBooleanWrapper( search, false ) );
				}					
			}
			mergedProteinSingleEntry.setSearchContainsProtein( booleanWrapper );
		}

		allProteinsMergedCommonPageDownloadResult.peptidePsmAnnotationNameDescListsForEachSearch = peptidePsmAnnotationNameDescListsForEachSearch;
		allProteinsMergedCommonPageDownloadResult.proteins = mergedProteinSingleEntryList;
		
		return allProteinsMergedCommonPageDownloadResult;
	}
	
	////////////////////////////////////////
	////////   Sorter Class to Sort Proteins
	/**
	 * 
	 *
	 */
	private class MergedProteinSingleEntrySorter implements Comparator<MergedProteinSingleEntry> {

		SortAnnDataCommon sortAnnDataCommon = new SortAnnDataCommon();
		
		List<Integer> searchIdsListDeduppedSorted;

		@Override
		public int compare(MergedProteinSingleEntry o1, MergedProteinSingleEntry o2) {
			
			Map<Integer, ProteinSingleEntry> o1_ProteinMapOnSearchId = o1.getProteinMapOnSearchId();
			Map<Integer, ProteinSingleEntry> o2_ProteinMapOnSearchId = o2.getProteinMapOnSearchId();
			
			for ( Integer searchId : searchIdsListDeduppedSorted ) {

				ProteinSingleEntry o1_ProteinSingleEntry = o1_ProteinMapOnSearchId.get( searchId );
				ProteinSingleEntry o2_ProteinSingleEntry = o2_ProteinMapOnSearchId.get( searchId );
				if ( o1_ProteinSingleEntry == null && o2_ProteinSingleEntry == null ) {
					continue;  //  Sort on a later search id where there is data
				}
				if ( o1_ProteinSingleEntry == null ) {
					return 1;  //  Sort o1 after o2
				}
				if ( o2_ProteinSingleEntry == null ) {
					return -1; //  Sort o1 before o2  
				}

				//  Sort data on Peptide then PSM Ann Data.  If all match, return null, else return sort order
				Integer returnValue = sortAnnDataCommon.compare( o1_ProteinSingleEntry, o2_ProteinSingleEntry, searchId );
				
				if ( returnValue != null ) {
					return returnValue; // Only return if not null
				}

				//  If everything matches, sort on protein id
				return o1_ProteinSingleEntry.getProteinSequenceVersionId() - o2_ProteinSingleEntry.getProteinSequenceVersionId();
			}
			//  No data in the same search so sort on protein sequence id
			return o1.getProteinSequenceVersionId() - o2.getProteinSequenceVersionId();
		}
	}
	

	
	/**
	 * 
	 *  Sort Ann Data for Proteins
	 */
	private class SortAnnDataCommon {

		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
		Map<Integer, List<AnnotationTypeDTO>> psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;

		/**
		 * @param SortDisplayRecordsWrapperBase_o1
		 * @param SortDisplayRecordsWrapperBase_o2
		 * @param searchId
		 * @return
		 */
		public Integer compare( 
				SortDisplayRecordsWrapperBase sortDisplayRecordsWrapperBase_o1, 
				SortDisplayRecordsWrapperBase sortDisplayRecordsWrapperBase_o2, 
				Integer searchId ) {

			//  Next sort on Peptide Annotation Data
			List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted =
					peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap.get( searchId );
			
			//  Process through the Peptide annotation types (sorted on sort order), comparing the values
			for ( AnnotationTypeDTO annotationTypeDTO : peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted ) {
				int typeId = annotationTypeDTO.getId();
				AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
				if ( annotationTypeFilterableDTO == null ) {
					String msg = "Peptide AnnotationTypeFilterableDTO == null for type id: " + typeId;
					log.error( msg );
					throw new RuntimeException(msg);
				}
				FilterDirectionType annTypeFilterDirectionType = annotationTypeFilterableDTO.getFilterDirectionType();
				if ( annTypeFilterDirectionType == null ) {
					String msg = "Peptide FilterDirectionType == null for type id: " + typeId;
					log.error( msg );
					throw new RuntimeException(msg);
				}
				
				AnnotationDataBaseDTO o1_WebReportedPeptide = sortDisplayRecordsWrapperBase_o1.getPeptideAnnotationDTOMap().get( typeId );
				if ( o1_WebReportedPeptide == null ) {
					String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
					log.error( msg );
					throw new RuntimeException(msg);
				}
				double o1Value = o1_WebReportedPeptide.getValueDouble();
				AnnotationDataBaseDTO o2_WebReportedPeptide = sortDisplayRecordsWrapperBase_o2.getPeptideAnnotationDTOMap().get( typeId );
				if ( o2_WebReportedPeptide == null ) {
					String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
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
				
				AnnotationDataBaseDTO o1_WebReportedPeptide = sortDisplayRecordsWrapperBase_o1.getPsmAnnotationDTOMap().get( typeId );
				if ( o1_WebReportedPeptide == null ) {
					String msg = "Unable to get PSM Filterable Annotation data for type id: " + typeId;
					log.error( msg );
					throw new RuntimeException(msg);
				}
				double o1Value = o1_WebReportedPeptide.getValueDouble();
				AnnotationDataBaseDTO o2_WebReportedPeptide = sortDisplayRecordsWrapperBase_o2.getPsmAnnotationDTOMap().get( typeId );
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
			
			return null;
			
		}
	}
	
	//////////////////////////////////////////////////////////////
    /**
     * 
     *
     */
    public class SortSearchProtein implements Comparator<SearchProtein> {
        @Override
		public int compare(SearchProtein o1, SearchProtein o2) {
            try { return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()); }
            catch( Exception e ) { return 0; }
        }
    }
    
    /**
     * 
     *
     */
    public static class MergedProteinSingleEntry implements IMergedSearchLink {

    	private List<SearchBooleanWrapper> searchContainsProtein;
    	private int proteinSequenceVersionId;
    	private MergedSearchProtein protein;
    	private Map<Integer, ProteinSingleEntry> proteinMapOnSearchId;

    	private Integer numPsms;
    	private int numPeptides = -1;
    	private int numUniquePeptides = -1;
    	
    	private List<SearchDTO> searches;

    	/**
    	 * For web display
    	 */
    	private List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch;

    	public int getNumSearches() {
    		if ( searches == null ) {
    			return 0;
    		}
    		return searches.size();
    	}
    	
		public List<SearchBooleanWrapper> getSearchContainsProtein() {
			return searchContainsProtein;
		}
		public void setSearchContainsProtein(List<SearchBooleanWrapper> searchContainsProtein) {
			this.searchContainsProtein = searchContainsProtein;
		}
		public int getProteinSequenceVersionId() {
			return proteinSequenceVersionId;
		}
		public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
			this.proteinSequenceVersionId = proteinSequenceVersionId;
		}
		public MergedSearchProtein getProtein() {
			return protein;
		}
		public void setProtein(MergedSearchProtein protein) {
			this.protein = protein;
		}
		public Integer getNumPsms() {
			return numPsms;
		}
		public void setNumPsms(Integer numPsms) {
			this.numPsms = numPsms;
		}
		public int getNumPeptides() {
			return numPeptides;
		}
		public void setNumPeptides(int numPeptides) {
			this.numPeptides = numPeptides;
		}
		public int getNumUniquePeptides() {
			return numUniquePeptides;
		}
		public void setNumUniquePeptides(int numUniquePeptides) {
			this.numUniquePeptides = numUniquePeptides;
		}
		public List<AnnValuePeptPsmListsPair> getPeptidePsmAnnotationValueListsForEachSearch() {
			return peptidePsmAnnotationValueListsForEachSearch;
		}
		public void setPeptidePsmAnnotationValueListsForEachSearch(
				List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch) {
			this.peptidePsmAnnotationValueListsForEachSearch = peptidePsmAnnotationValueListsForEachSearch;
		}
		public Map<Integer, ProteinSingleEntry> getProteinMapOnSearchId() {
			return proteinMapOnSearchId;
		}
		public void setProteinMapOnSearchId(Map<Integer, ProteinSingleEntry> proteinMapOnSearchId) {
			this.proteinMapOnSearchId = proteinMapOnSearchId;
		}
		@Override
		public List<SearchDTO> getSearches() {
			return searches;
		}
		public void setSearches(List<SearchDTO> searches) {
			this.searches = searches;
		}
    }
    
	/**
	 * Returned object
	 *
	 */
	public static class AllProteinsMergedCommonPageDownloadResult {
		
		ProteinQueryJSONRoot proteinQueryJSONRoot;
		Set<Integer> excludeTaxonomy_Ids_Set_UserInput;
		Set<Integer> excludeproteinSequenceVersionIds_Set_UserInput;
		List<AnnDisplayNameDescPeptPsmListsPair> peptidePsmAnnotationNameDescListsForEachSearch;
		
		List<MergedProteinSingleEntry> proteins;
		
		/////////////////////
		// all possible proteins across all searches (for "Exclude Protein" list on web page)
		Map<Integer,Set<Integer>> allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds;
		public Map<Integer, Set<Integer>> getAllProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds() {
			return allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds;
		}
		public void setAllProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds(
				Map<Integer, Set<Integer>> allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds) {
			this.allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds = allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds;
		}
		public ProteinQueryJSONRoot getProteinQueryJSONRoot() {
			return proteinQueryJSONRoot;
		}
		public void setProteinQueryJSONRoot(ProteinQueryJSONRoot proteinQueryJSONRoot) {
			this.proteinQueryJSONRoot = proteinQueryJSONRoot;
		}
		public Set<Integer> getExcludeTaxonomy_Ids_Set_UserInput() {
			return excludeTaxonomy_Ids_Set_UserInput;
		}
		public void setExcludeTaxonomy_Ids_Set_UserInput(
				Set<Integer> excludeTaxonomy_Ids_Set_UserInput) {
			this.excludeTaxonomy_Ids_Set_UserInput = excludeTaxonomy_Ids_Set_UserInput;
		}
		public Set<Integer> getExcludeProtein_Ids_Set_UserInput() {
			return excludeproteinSequenceVersionIds_Set_UserInput;
		}
		public void setExcludeProtein_Ids_Set_UserInput(
				Set<Integer> excludeProtein_Ids_Set_UserInput) {
			this.excludeproteinSequenceVersionIds_Set_UserInput = excludeProtein_Ids_Set_UserInput;
		}
		public List<AnnDisplayNameDescPeptPsmListsPair> getPeptidePsmAnnotationNameDescListsForEachSearch() {
			return peptidePsmAnnotationNameDescListsForEachSearch;
		}
		public void setPeptidePsmAnnotationNameDescListsForEachSearch(
				List<AnnDisplayNameDescPeptPsmListsPair> peptidePsmAnnotationNameDescListsForEachSearch) {
			this.peptidePsmAnnotationNameDescListsForEachSearch = peptidePsmAnnotationNameDescListsForEachSearch;
		}
		public List<MergedProteinSingleEntry> getProteins() {
			return proteins;
		}
		public void setProteins(List<MergedProteinSingleEntry> proteins) {
			this.proteins = proteins;
		}
	}
}
