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
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortAnnotationDTORecords;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortDisplayRecordsWrapperBase;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.form_utils.GetProteinQueryJSONRootFromFormData;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.linked_positions.CrosslinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.LinkedPositions_FilterExcludeLinksWith_Param;
import org.yeastrc.xlink.www.linked_positions.LooplinkLinkedPositions;
import org.yeastrc.xlink.www.objects.AnnDisplayNameDescPeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnValuePeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.objects.SearchBooleanWrapper;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;

/**
 *  Common code for Merged Protein and Download Merged Protein 
 *
 */
public class ProteinsMergedCommonPageDownload {
	
	private static final Logger log = Logger.getLogger( ProteinsMergedCommonPageDownload.class );
	private ProteinsMergedCommonPageDownload() { }
	public static ProteinsMergedCommonPageDownload getInstance() { 
		return new ProteinsMergedCommonPageDownload(); 
	}
	public static enum ForCrosslinksOrLooplinkOrBoth { BOTH_CROSSLINKS_AND_LOOPLINKS, CROSSLINKS, LOOPLINKS }

	/**
	 * @param form
	 * @param forCrosslinksOrLooplinkOrBoth
	 * @param projectSearchIdsListDeduppedSorted
	 * @param searches
	 * @param searchesMapOnSearchId
	 * @return
	 * @throws Exception
	 * @throws ProxlWebappDataException
	 */
	public ProteinsMergedCommonPageDownloadResult getCrosslinksAndLooplinkWrapped(
			MergedSearchViewProteinsForm form,
			ForCrosslinksOrLooplinkOrBoth forCrosslinksOrLooplinkOrBoth,
			List<Integer> projectSearchIdsListDeduppedSorted,
			List<SearchDTO> searches, 
			Map<Integer, SearchDTO> searchesMapOnSearchId
			) throws Exception, ProxlWebappDataException {
		
		ProteinsMergedCommonPageDownloadResult proteinsMergedCommonPageDownloadResult = new ProteinsMergedCommonPageDownloadResult();
		
		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}
		/////////////////////
		// all possible proteins for Crosslinks and Looplinks across all searches (for "Exclude Protein" list on web page)
		Map<Integer,Set<Integer>> allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds = new HashMap<>();
		proteinsMergedCommonPageDownloadResult.allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds =
				allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds;
		//   Get Query JSON from the form and if not empty, deserialize it
		ProteinQueryJSONRoot proteinQueryJSONRoot = 
				GetProteinQueryJSONRootFromFormData.getInstance()
				.getProteinQueryJSONRootFromFormData( 
						form, 
						projectSearchIdsListDeduppedSorted,
						searchIds,
						mapProjectSearchIdToSearchId );
		proteinsMergedCommonPageDownloadResult.proteinQueryJSONRoot = proteinQueryJSONRoot;
		
		////////////
		//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup
		Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
		Set<Integer> excludeproteinSequenceVersionIds_Set_UserInput = new HashSet<>();
		proteinsMergedCommonPageDownloadResult.excludeTaxonomy_Ids_Set_UserInput = excludeTaxonomy_Ids_Set_UserInput;
		proteinsMergedCommonPageDownloadResult.excludeproteinSequenceVersionIds_Set_UserInput = excludeproteinSequenceVersionIds_Set_UserInput;
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

		LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param = new LinkedPositions_FilterExcludeLinksWith_Param( proteinQueryJSONRoot );

		/////////////////////////////////////
		//////    Get data from database per search and put in Map by Primary Key then SearchId
		//   Primary Key is the combination of protein id(s) and protein positions
		Map<CrosslinkPrimaryKey, Map<Integer, SearchProteinCrosslinkWrapper>> crosslinksWrapperMapOnCrosslinkKeySearchId = new HashMap<>();
		Map<LooplinkPrimaryKey, Map<Integer, SearchProteinLooplinkWrapper>> looplinksWrapperMapOnLooplinkKeySearchId = new HashMap<>();
		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = new HashMap<>();
		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap = new HashMap<>();
		Map<Integer, List<AnnotationTypeDTO>> psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = new HashMap<>();
		Map<Integer, List<AnnotationTypeDTO>> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap = new HashMap<>();
		//////////////////////////////////////////////////////
		///   Crosslinks and Looplinks Wrapped
		///   Crosslinks
		List<MergedSearchProteinCrosslink> crosslinks = new ArrayList<>( crosslinksWrapperMapOnCrosslinkKeySearchId.size() );
		///   Looplinks
		List<MergedSearchProteinLooplink> looplinks = new ArrayList<>( looplinksWrapperMapOnLooplinkKeySearchId.size() );
		///////////////////////
		////    Get the Crosslinks and Looplinks data per search
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
			
			//////////////////   Get Crosslinks data from DATABASE  from database
			/////////////////////////////////////////////////////////////
			List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
					CrosslinkLinkedPositions.getInstance()
					.getSearchProteinCrosslinkWrapperList( searchDTO, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
			/////////////////////////////////////////////////////////////
			//////////////////   Get Looplinks data from DATABASE   from database
			List<SearchProteinLooplinkWrapper> wrappedLooplinks = 
					LooplinkLinkedPositions.getInstance()
					.getSearchProteinLooplinkWrapperList( searchDTO, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
			
			/////////////////////////////////////////////////////////
			//////////    Add to list of all proteins (for "Exclude Protein" list on web page) 
			for ( SearchProteinCrosslinkWrapper  searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
				SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
				SearchProtein searchProtein_1 = searchProteinCrosslink.getProtein1();
				SearchProtein searchProtein_2 = searchProteinCrosslink.getProtein2();
				Integer searchProtein_id_1 = searchProtein_1.getProteinSequenceVersionObject().getProteinSequenceVersionId();
				Integer searchProtein_id_2 = searchProtein_2.getProteinSequenceVersionObject().getProteinSequenceVersionId();
				{
					Set<Integer> searchIdsForProtein_1 =
							allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_1 );
					if ( searchIdsForProtein_1 == null ) {
						searchIdsForProtein_1 = new HashSet<>();
						allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_1, searchIdsForProtein_1 );
					}
					searchIdsForProtein_1.add( searchId );
				}
				{
					Set<Integer> searchIdsForProtein_2 =
							allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_2 );
					if ( searchIdsForProtein_2 == null ) {
						searchIdsForProtein_2 = new HashSet<>();
						allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_2, searchIdsForProtein_2 );
					}
					searchIdsForProtein_2.add( searchId );
				}
			}
			for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
				SearchProteinLooplink searchProteinLooplink = searchProteinLooplinkWrapper.getSearchProteinLooplink();
				SearchProtein searchProtein = searchProteinLooplink.getProtein();
				Integer searchProtein_id = searchProtein.getProteinSequenceVersionObject().getProteinSequenceVersionId();
				Set<Integer> searchIdsForProtein =
						allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id );
				if ( searchIdsForProtein == null ) {
					searchIdsForProtein = new HashSet<>();
					allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id, searchIdsForProtein );
				}
				searchIdsForProtein.add( searchId );
			}
			//////////////////////////////////////////////////////////////////
			//////////    Filter Links based on user request
			// Filter out links if requested
			if( proteinQueryJSONRoot.isFilterNonUniquePeptides() 
					|| proteinQueryJSONRoot.isFilterOnlyOnePSM() 
					|| proteinQueryJSONRoot.isFilterOnlyOnePeptide()
					
					//  || proteinQueryJSONRoot.isRemoveNonUniquePSMs() -- Handled in CrosslinkLinkedPositions and LooplinkLinkedPositions
					
					|| ( ! excludeproteinSequenceVersionIds_Set_UserInput.isEmpty() ) 
					|| ( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) ) {
				///////  Output Lists, Results After Filtering
				List<SearchProteinCrosslinkWrapper> wrappedCrosslinksAfterFilter = new ArrayList<>( wrappedCrosslinks.size() );
				List<SearchProteinLooplinkWrapper> wrappedLooplinksAfterFilter = new ArrayList<>( wrappedLooplinks.size() );
				///  Filter CROSSLINKS
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					SearchProteinCrosslink link = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					// did user request removal of certain taxonomy IDs?
					if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
						boolean excludeOnProtein_1 =
								ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein1().getProteinSequenceVersionObject(), 
										searchId );
						boolean excludeOnProtein_2 =
								ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein2().getProteinSequenceVersionObject(), 
										searchId );
						if ( excludeOnProtein_1 || excludeOnProtein_2 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain protein IDs?
					if( ! excludeproteinSequenceVersionIds_Set_UserInput.isEmpty() ) {
						int proteinId_1 = link.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						int proteinId_2 = link.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						if ( excludeproteinSequenceVersionIds_Set_UserInput.contains( proteinId_1 ) 
								|| excludeproteinSequenceVersionIds_Set_UserInput.contains( proteinId_2 ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}		
					// did they request to removal of non unique peptides?
					if( proteinQueryJSONRoot.isFilterNonUniquePeptides()  ) {
						if( link.getNumUniqueLinkedPeptides() < 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one PSM?
					if( proteinQueryJSONRoot.isFilterOnlyOnePSM()  ) {
						int psmCountForSearchId = link.getNumPsms();
						if ( psmCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one Reported Peptide?
					if( proteinQueryJSONRoot.isFilterOnlyOnePeptide() ) {
						int peptideCountForSearchId = link.getNumLinkedPeptides();
						if ( peptideCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}			
					wrappedCrosslinksAfterFilter.add( searchProteinCrosslinkWrapper );
				}
				///  Filter LOOPLINKS
				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
					SearchProteinLooplink link = searchProteinLooplinkWrapper.getSearchProteinLooplink();
					// did user request removal of certain taxonomy IDs?
					if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
						boolean excludeOnProtein =
								ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein().getProteinSequenceVersionObject(), 
										searchId );
						if ( excludeOnProtein ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain protein IDs?
					if( ! excludeproteinSequenceVersionIds_Set_UserInput.isEmpty() ) {
						int proteinId = link.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						if ( excludeproteinSequenceVersionIds_Set_UserInput.contains( proteinId ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}									
					// did they request to removal of non unique peptides?
					if( proteinQueryJSONRoot.isFilterNonUniquePeptides()  ) {
						if( link.getNumUniquePeptides() < 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one PSM?
					if( proteinQueryJSONRoot.isFilterOnlyOnePSM()  ) {
						int psmCountForSearchId = link.getNumPsms();
						if ( psmCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one Reported Peptide?
					if( proteinQueryJSONRoot.isFilterOnlyOnePeptide() ) {
						int peptideCountForSearchId = link.getNumPeptides();
						if ( peptideCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					wrappedLooplinksAfterFilter.add( searchProteinLooplinkWrapper );
				}
				//  Copy new filtered lists to original input variable names to overlay them
				wrappedCrosslinks = wrappedCrosslinksAfterFilter;
				wrappedLooplinks = wrappedLooplinksAfterFilter;
			}
			///   Add the SearchProteinCrosslinkWrapper to the map on crosslink primary key and search id
			for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
				SearchProteinCrosslink link = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
				CrosslinkPrimaryKey crosslinkPrimaryKey = new CrosslinkPrimaryKey( link );
				Map<Integer, SearchProteinCrosslinkWrapper> crosslinksWrapperMapOnSearchId = 
						crosslinksWrapperMapOnCrosslinkKeySearchId.get( crosslinkPrimaryKey );
				if ( crosslinksWrapperMapOnSearchId == null ) {
					crosslinksWrapperMapOnSearchId = new HashMap<>();
					crosslinksWrapperMapOnCrosslinkKeySearchId.put( crosslinkPrimaryKey, crosslinksWrapperMapOnSearchId );
				}
				SearchProteinCrosslinkWrapper prevSearchProteinCrosslinkWrapper =
						crosslinksWrapperMapOnSearchId.put( searchId, searchProteinCrosslinkWrapper );
				if ( prevSearchProteinCrosslinkWrapper != null ) {
					String msg = "crosslinksWrapperMapOnSearchId already contains entry for search id: " + searchId
							+ ", crosslinkPrimaryKey: " + crosslinkPrimaryKey;
					log.error( msg );
					throw new ProxlWebappDataException(msg);
				}
			}
			///   Add the SearchProteinLooplinkWrapper to the map on looplink primary key and search id
			for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
				SearchProteinLooplink link = searchProteinLooplinkWrapper.getSearchProteinLooplink();
				LooplinkPrimaryKey looplinkPrimaryKey = new LooplinkPrimaryKey( link );
				Map<Integer, SearchProteinLooplinkWrapper> looplinksWrapperMapOnSearchId = 
						looplinksWrapperMapOnLooplinkKeySearchId.get( looplinkPrimaryKey );
				if ( looplinksWrapperMapOnSearchId == null ) {
					looplinksWrapperMapOnSearchId = new HashMap<>();
					looplinksWrapperMapOnLooplinkKeySearchId.put( looplinkPrimaryKey, looplinksWrapperMapOnSearchId );
				}
				SearchProteinLooplinkWrapper prevSearchProteinLooplinkWrapper =
						looplinksWrapperMapOnSearchId.put( searchId, searchProteinLooplinkWrapper );
				if ( prevSearchProteinLooplinkWrapper != null ) {
					String msg = "looplinksWrapperMapOnSearchId already contains entry for search id: " + searchId
							+ ", looplinkPrimaryKey: " + looplinkPrimaryKey;
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
		//////////////////////////////////////////////////////
		///   Process Crosslinks and Looplinks to get annotations and sort.
		//
		//              Process Crosslinks or Looplinks, depending on which page is being displayed
		//					or Both for Download
		//
		///   Process Crosslinks
		if ( forCrosslinksOrLooplinkOrBoth != ForCrosslinksOrLooplinkOrBoth.CROSSLINKS 
				&& forCrosslinksOrLooplinkOrBoth != ForCrosslinksOrLooplinkOrBoth.BOTH_CROSSLINKS_AND_LOOPLINKS ) {
			
			//////   Don't need Crosslinks Annotation data or sorting 
			//////      so only transfer data to MergedSearchProteinCrosslink objects
			/////////////////////
			//   Transfer Crosslinks into "crosslinks": a list of MergedSearchProteinCrosslink
			for ( Map.Entry<CrosslinkPrimaryKey, Map<Integer, SearchProteinCrosslinkWrapper>> entry : crosslinksWrapperMapOnCrosslinkKeySearchId.entrySet() ) {
				Map<Integer, SearchProteinCrosslinkWrapper> crosslinksWrapperMapOnSearchId = entry.getValue();
				/////////
				//  Get searches for this item
				List<SearchDTO> searchesForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
				List<Integer> searchIdsForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
				Map<SearchDTO, SearchProteinCrosslink> searchProteinCrosslinksMapOnSearchDTOForThisItem = new TreeMap<SearchDTO, SearchProteinCrosslink>();
				SearchProteinCrosslinkWrapper anySearchProteinCrosslinkWrapper = null;
				for ( Map.Entry<Integer, SearchProteinCrosslinkWrapper> crosslinksWrapperEntry : crosslinksWrapperMapOnSearchId.entrySet() ) {
					Integer searchId = crosslinksWrapperEntry.getKey();
					SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper = crosslinksWrapperEntry.getValue();
					anySearchProteinCrosslinkWrapper = searchProteinCrosslinkWrapper;
					SearchDTO searchDTO = searchesMapOnSearchId.get( searchId );
					if ( searchDTO == null ) {
						String msg = "Failed to get searchDTO list for searchId : " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					searchesForThisItem.add( searchDTO );
					searchIdsForThisItem.add(searchId);
					SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					searchProteinCrosslinksMapOnSearchDTOForThisItem.put(searchDTO, searchProteinCrosslink );
				}
				SearchProteinCrosslink anySearchProteinCrosslink = anySearchProteinCrosslinkWrapper.getSearchProteinCrosslink();
				ProteinSequenceVersionObject ProteinSequenceObject_1 = new ProteinSequenceVersionObject();
				ProteinSequenceObject_1.setProteinSequenceVersionId( anySearchProteinCrosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
				ProteinSequenceVersionObject ProteinSequenceObject_2 = new ProteinSequenceVersionObject();
				ProteinSequenceObject_2.setProteinSequenceVersionId( anySearchProteinCrosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
				MergedSearchProtein protein_1 = new MergedSearchProtein( searchesForThisItem, ProteinSequenceObject_1 );
				MergedSearchProtein protein_2 = new MergedSearchProtein( searchesForThisItem, ProteinSequenceObject_2 );
				MergedSearchProteinCrosslink mergedSearchProteinCrosslink = new MergedSearchProteinCrosslink();
				crosslinks.add( mergedSearchProteinCrosslink );
				mergedSearchProteinCrosslink.setProtein1( protein_1 );
				mergedSearchProteinCrosslink.setProtein2( protein_2 );
				mergedSearchProteinCrosslink.setProtein1Position( anySearchProteinCrosslink.getProtein1Position() );
				mergedSearchProteinCrosslink.setProtein2Position( anySearchProteinCrosslink.getProtein2Position() );
				mergedSearchProteinCrosslink.setSearches( searchesForThisItem );
				mergedSearchProteinCrosslink.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );
			}
		} else {
			////     Collect Annotation data, set Peptide, unique Peptide, and PSM counts for Crosslinks and sort
			/////////////////////
			//   Transfer Crosslinks into a list of MergedSearchProteinCrosslinkWrapper
			List<MergedSearchProteinCrosslinkWrapper> mergedSearchProteinCrosslinkWrapperList = new ArrayList<>( crosslinksWrapperMapOnCrosslinkKeySearchId.size() );
			for ( Map.Entry<CrosslinkPrimaryKey, Map<Integer, SearchProteinCrosslinkWrapper>> entry : crosslinksWrapperMapOnCrosslinkKeySearchId.entrySet() ) {
				Map<Integer, SearchProteinCrosslinkWrapper> crosslinksWrapperMapOnSearchId = entry.getValue();
				MergedSearchProteinCrosslinkWrapper mergedSearchProteinCrosslinkWrapper = new MergedSearchProteinCrosslinkWrapper();
				mergedSearchProteinCrosslinkWrapperList.add( mergedSearchProteinCrosslinkWrapper );
				mergedSearchProteinCrosslinkWrapper.setSearchProteinCrosslinkWrapperMapOnSearchId( crosslinksWrapperMapOnSearchId );
				SearchProteinCrosslinkWrapper anySearchProteinCrosslinkWrapper = crosslinksWrapperMapOnSearchId.entrySet().iterator().next().getValue();
				SearchProteinCrosslink anySearchProteinCrosslink = anySearchProteinCrosslinkWrapper.getSearchProteinCrosslink();
				mergedSearchProteinCrosslinkWrapper.setProteinId_1( anySearchProteinCrosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
				mergedSearchProteinCrosslinkWrapper.setProteinId_2( anySearchProteinCrosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
				mergedSearchProteinCrosslinkWrapper.setProtein_1_Position( anySearchProteinCrosslink.getProtein1Position() );
				mergedSearchProteinCrosslinkWrapper.setProtein_2_Position( anySearchProteinCrosslink.getProtein2Position() );
			}
			
			//////////  
			//   Sort Crosslinks on values in first search, or so on if no data for first search
			MergedSearchProteinCrosslinkWrapperSorter crosslinkSorter = new MergedSearchProteinCrosslinkWrapperSorter();
			crosslinkSorter.sortAnnDataCommon.peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
			crosslinkSorter.sortAnnDataCommon.psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
			crosslinkSorter.searchIdsListDeduppedSorted = searchIdsListDeduppedSorted;
			Collections.sort( mergedSearchProteinCrosslinkWrapperList , crosslinkSorter );
			
			//  Create Merged Crosslink Protein object
			//     set Peptide, unique Peptide, and PSM counts 
			//  Copy Peptide and PSM annotations to display lists
			for ( MergedSearchProteinCrosslinkWrapper mergedSearchProteinCrosslinkWrapper : mergedSearchProteinCrosslinkWrapperList ) {
				Map<Integer, SearchProteinCrosslinkWrapper> searchProteinCrosslinkWrapperWrapperMapOnSearchId =
						mergedSearchProteinCrosslinkWrapper.getSearchProteinCrosslinkWrapperMapOnSearchId();
				/////////
				//  Get searches for this item
				List<SearchDTO> searchesForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
				List<Integer> searchIdsForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
				Map<SearchDTO, SearchProteinCrosslink> searchProteinCrosslinksMapOnSearchDTOForThisItem = new TreeMap<SearchDTO, SearchProteinCrosslink>();
				int numPsms = 0;
				Set<Integer> associatedReportedPeptideIdsMergedSet = new HashSet<>();
				Set<Integer> associatedReportedPeptideIdsRelatedPeptidesUniqueMergedSet = new HashSet<>();
				for ( Map.Entry<Integer, SearchProteinCrosslinkWrapper> crosslinksWrapperEntry : searchProteinCrosslinkWrapperWrapperMapOnSearchId.entrySet() ) {
					Integer searchId = crosslinksWrapperEntry.getKey();
					SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper = crosslinksWrapperEntry.getValue();
					SearchDTO searchDTO = searchesMapOnSearchId.get( searchId );
					if ( searchDTO == null ) {
						String msg = "Failed to get searchDTO list for searchId : " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					searchesForThisItem.add( searchDTO );
					searchIdsForThisItem.add( searchId );
					SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					numPsms += searchProteinCrosslink.getNumPsms();
					associatedReportedPeptideIdsMergedSet.addAll( searchProteinCrosslink.getAssociatedReportedPeptideIds() );
					associatedReportedPeptideIdsRelatedPeptidesUniqueMergedSet.addAll( searchProteinCrosslink.getAssociatedReportedPeptideIdsRelatedPeptidesUnique() );
					searchProteinCrosslinksMapOnSearchDTOForThisItem.put(searchDTO, searchProteinCrosslink );
				}
				ProteinSequenceVersionObject ProteinSequenceObject_1 = new ProteinSequenceVersionObject();
				ProteinSequenceObject_1.setProteinSequenceVersionId( mergedSearchProteinCrosslinkWrapper.getProteinId_1() );
				ProteinSequenceVersionObject ProteinSequenceObject_2 = new ProteinSequenceVersionObject();
				ProteinSequenceObject_2.setProteinSequenceVersionId( mergedSearchProteinCrosslinkWrapper.getProteinId_2() );
				MergedSearchProtein protein_1 = new MergedSearchProtein( searchesForThisItem, ProteinSequenceObject_1 );
				MergedSearchProtein protein_2 = new MergedSearchProtein( searchesForThisItem, ProteinSequenceObject_2 );
				MergedSearchProteinCrosslink mergedSearchProteinCrosslink = new MergedSearchProteinCrosslink();
				crosslinks.add( mergedSearchProteinCrosslink );
				mergedSearchProteinCrosslink.setProtein1( protein_1 );
				mergedSearchProteinCrosslink.setProtein2( protein_2 );
				mergedSearchProteinCrosslink.setProtein1Position( mergedSearchProteinCrosslinkWrapper.getProtein_1_Position() );
				mergedSearchProteinCrosslink.setProtein2Position( mergedSearchProteinCrosslinkWrapper.getProtein_2_Position() );
				int protein1CompareProtein2 = protein_1.getName().compareToIgnoreCase( protein_2.getName() );
				if ( protein1CompareProtein2 > 0 
						|| ( protein1CompareProtein2 == 0
							&& mergedSearchProteinCrosslinkWrapper.getProtein_1_Position() > mergedSearchProteinCrosslinkWrapper.getProtein_2_Position() ) ) {
					//  Protein_1 name > protein_2 name or ( Protein_1 name == protein_2 name and pos1 > pos2 )
					//  so re-order
					mergedSearchProteinCrosslink.setProtein1( protein_2 );
					mergedSearchProteinCrosslink.setProtein2( protein_1 );
					mergedSearchProteinCrosslink.setProtein1Position( mergedSearchProteinCrosslinkWrapper.getProtein_2_Position() );
					mergedSearchProteinCrosslink.setProtein2Position( mergedSearchProteinCrosslinkWrapper.getProtein_1_Position() );
				}
				mergedSearchProteinCrosslink.setSearches( searchesForThisItem );
				mergedSearchProteinCrosslink.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );
				mergedSearchProteinCrosslink.setSearchProteinCrosslinks( searchProteinCrosslinksMapOnSearchDTOForThisItem );
				mergedSearchProteinCrosslink.setNumPsms( numPsms );
				mergedSearchProteinCrosslink.setNumLinkedPeptides( associatedReportedPeptideIdsMergedSet.size() );
				mergedSearchProteinCrosslink.setNumUniqueLinkedPeptides( associatedReportedPeptideIdsRelatedPeptidesUniqueMergedSet.size() );
				//  Copy Peptide and PSM annotations to display lists 
				List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch = new ArrayList<>( searchIdsListDeduppedSorted.size() );
				mergedSearchProteinCrosslink.setPeptidePsmAnnotationValueListsForEachSearch( peptidePsmAnnotationValueListsForEachSearch );
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
					SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper = searchProteinCrosslinkWrapperWrapperMapOnSearchId.get( searchId );
					if ( searchProteinCrosslinkWrapper == null ) {
						//  Create empty entries in list
						for ( int counter = 0; counter < peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size(); counter++ ) {
							psmAnnotationValueList.add( "" );
						}
						for ( int counter = 0; counter < psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size(); counter++ ) {
							psmAnnotationValueList.add( "" );
						}
					} else {
						//  Populate from wrapper
						Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap = searchProteinCrosslinkWrapper.getPeptideAnnotationDTOMap();
						Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap = searchProteinCrosslinkWrapper.getPsmAnnotationDTOMap();
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
			}  //  END:   for ( MergedSearchProteinCrosslinkWrapper mergedSearchProteinCrosslinkWrapper : mergedSearchProteinCrosslinkWrapperList ) {
			List<MergedSearchProteinCrosslinkWrapper> wrappedLinks = new ArrayList<MergedSearchProteinCrosslinkWrapper>( crosslinks.size() );
			////////////////////////////////////////////
			/////////   Add SearchBooleanWrapper for the searches for showing which searches the protein is in
			for( MergedSearchProteinCrosslink link : crosslinks ) {
				MergedSearchProteinCrosslinkWrapper wrapper = new MergedSearchProteinCrosslinkWrapper();
				List<SearchBooleanWrapper> booleanWrapper = new ArrayList<SearchBooleanWrapper>( searches.size() );
				for( SearchDTO search : searches ) {
					if( link.getSearches().contains( search ) ) {
						booleanWrapper.add( new SearchBooleanWrapper( search, true ) );
					} else {
						booleanWrapper.add( new SearchBooleanWrapper( search, false ) );
					}					
				}
				wrapper.setMergedSearchProteinCrosslink( link );
				wrapper.setSearchContainsCrosslink( booleanWrapper );
				wrappedLinks.add( wrapper );
			}
			proteinsMergedCommonPageDownloadResult.wrappedCrossLinks = wrappedLinks;			
		}  	//   END:   Else of:  if ( forCrosslinksOrLooplinkOrBoth != ForCrosslinksOrLooplinkOrBoth.CROSSLINKS && forCrosslinksOrLooplinkOrBoth != ForCrosslinksOrLooplinkOrBoth.BOTH_CROSSLINKS_AND_LOOPLINKS 
		//////////////////////////////////////////////////////////////
		///   Process Looplinks
		if ( forCrosslinksOrLooplinkOrBoth != ForCrosslinksOrLooplinkOrBoth.LOOPLINKS
				&& forCrosslinksOrLooplinkOrBoth != ForCrosslinksOrLooplinkOrBoth.BOTH_CROSSLINKS_AND_LOOPLINKS ) {
			//////   Don't need Looplinks Annotation data or sorting 
			//////      so only transfer data to MergedSearchProteinCrosslink objects
			/////////////////////
			//   Transfer Looplinks into "looplinks": a list of MergedSearchProteinLooplink
			for ( Map.Entry<LooplinkPrimaryKey, Map<Integer, SearchProteinLooplinkWrapper>> entry : looplinksWrapperMapOnLooplinkKeySearchId.entrySet() ) {
				Map<Integer, SearchProteinLooplinkWrapper> looplinksWrapperMapOnSearchId = entry.getValue();
				/////////
				//  Get searches for this item
				List<SearchDTO> searchesForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
				List<Integer> searchIdsForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
				Map<SearchDTO, SearchProteinLooplink> searchProteinLooplinksMapOnSearchDTOForThisItem = new TreeMap<>();
				SearchProteinLooplinkWrapper anySearchProteinLooplinkWrapper = null;
				for ( Map.Entry<Integer, SearchProteinLooplinkWrapper> looplinksWrapperEntry : looplinksWrapperMapOnSearchId.entrySet() ) {
					Integer searchId = looplinksWrapperEntry.getKey();
					SearchProteinLooplinkWrapper searchProteinLooplinkWrapper = looplinksWrapperEntry.getValue();
					anySearchProteinLooplinkWrapper = searchProteinLooplinkWrapper;
					SearchDTO searchDTO = searchesMapOnSearchId.get( searchId );
					if ( searchDTO == null ) {
						String msg = "Failed to get searchDTO list for searchId : " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					searchesForThisItem.add( searchDTO );
					searchIdsForThisItem.add(searchId);
					SearchProteinLooplink searchProteinLooplink = searchProteinLooplinkWrapper.getSearchProteinLooplink();
					searchProteinLooplinksMapOnSearchDTOForThisItem.put(searchDTO, searchProteinLooplink );
				}
				SearchProteinLooplink anySearchProteinLooplink = anySearchProteinLooplinkWrapper.getSearchProteinLooplink();
				ProteinSequenceVersionObject ProteinSequenceObject = new ProteinSequenceVersionObject();
				ProteinSequenceObject.setProteinSequenceVersionId( anySearchProteinLooplink.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
				MergedSearchProtein protein = new MergedSearchProtein( searchesForThisItem, ProteinSequenceObject );
				MergedSearchProteinLooplink mergedSearchProteinLooplink = new MergedSearchProteinLooplink();
				looplinks.add( mergedSearchProteinLooplink );
				mergedSearchProteinLooplink.setProtein( protein );
				mergedSearchProteinLooplink.setProteinPosition1( anySearchProteinLooplink.getProteinPosition1() );
				mergedSearchProteinLooplink.setProteinPosition2( anySearchProteinLooplink.getProteinPosition2() );
				mergedSearchProteinLooplink.setSearches( searchesForThisItem );
				mergedSearchProteinLooplink.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );
				mergedSearchProteinLooplink.setSearchProteinLooplinks( searchProteinLooplinksMapOnSearchDTOForThisItem );
			}
		} else {
			/////   Looplinks Page        
			////     Prepare Looplinks for display
			/////////////////////
			//   Transfer Looplinks into a list
			List<MergedSearchProteinLooplinkWrapper> mergedSearchProteinLooplinkWrapperList = new ArrayList<>( looplinksWrapperMapOnLooplinkKeySearchId.size() );
			for ( Map.Entry<LooplinkPrimaryKey, Map<Integer, SearchProteinLooplinkWrapper>> entry : looplinksWrapperMapOnLooplinkKeySearchId.entrySet() ) {
				Map<Integer, SearchProteinLooplinkWrapper> looplinksWrapperMapOnSearchId = entry.getValue();
				MergedSearchProteinLooplinkWrapper mergedSearchProteinLooplinkWrapper = new MergedSearchProteinLooplinkWrapper();
				mergedSearchProteinLooplinkWrapperList.add( mergedSearchProteinLooplinkWrapper );
				mergedSearchProteinLooplinkWrapper.setSearchProteinLooplinkWrapperMapOnSearchId( looplinksWrapperMapOnSearchId );
				SearchProteinLooplinkWrapper anySearchProteinLooplinkWrapper = looplinksWrapperMapOnSearchId.entrySet().iterator().next().getValue();
				SearchProteinLooplink anySearchProteinLooplink = anySearchProteinLooplinkWrapper.getSearchProteinLooplink();
				mergedSearchProteinLooplinkWrapper.setProteinId( anySearchProteinLooplink.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
				mergedSearchProteinLooplinkWrapper.setProteinPosition_1( anySearchProteinLooplink.getProteinPosition1() );
				mergedSearchProteinLooplinkWrapper.setProteinPosition_2( anySearchProteinLooplink.getProteinPosition2() );
			}
			//////////  
			//   Sort Looplinks on values in first search, or so on if no data for first search
			MergedSearchProteinLooplinkWrapperSorter looplinkSorter = new MergedSearchProteinLooplinkWrapperSorter();
			looplinkSorter.sortAnnDataCommon.peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
			looplinkSorter.sortAnnDataCommon.psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = psmCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
			looplinkSorter.searchIdsListDeduppedSorted = searchIdsListDeduppedSorted;
			Collections.sort( mergedSearchProteinLooplinkWrapperList , looplinkSorter );
			
			//  Create Merged Looplink Protein object
			//     set Peptide, unique Peptide, and PSM counts 
			//  Copy Peptide and PSM annotations to display lists
			for ( MergedSearchProteinLooplinkWrapper mergedSearchProteinLooplinkWrapper : mergedSearchProteinLooplinkWrapperList ) {
				Map<Integer, SearchProteinLooplinkWrapper> searchProteinLooplinkWrapperWrapperMapOnSearchId =
						mergedSearchProteinLooplinkWrapper.getSearchProteinLooplinkWrapperMapOnSearchId();
				/////////
				//  Get searches for this item
				List<SearchDTO> searchesForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
				List<Integer> searchIdsForThisItem = new ArrayList<>( searchesMapOnSearchId.size() );
				Map<SearchDTO, SearchProteinLooplink> searchProteinLooplinksMapOnSearchDTOForThisItem = new TreeMap<>();
				int numPsms = 0;
				Set<Integer> associatedReportedPeptideIdsMergedSet = new HashSet<>();
				Set<Integer> associatedReportedPeptideIdsRelatedPeptidesUniqueMergedSet = new HashSet<>();
				for ( Map.Entry<Integer, SearchProteinLooplinkWrapper> looplinksWrapperEntry : searchProteinLooplinkWrapperWrapperMapOnSearchId.entrySet() ) {
					Integer searchId = looplinksWrapperEntry.getKey();
					SearchProteinLooplinkWrapper searchProteinLooplinkWrapper = looplinksWrapperEntry.getValue();
					SearchDTO searchDTO = searchesMapOnSearchId.get( searchId );
					if ( searchDTO == null ) {
						String msg = "Failed to get searchDTO list for searchId : " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					searchesForThisItem.add( searchDTO );
					searchIdsForThisItem.add( searchId );
					SearchProteinLooplink searchProteinLooplink = searchProteinLooplinkWrapper.getSearchProteinLooplink();
					numPsms += searchProteinLooplink.getNumPsms();
					associatedReportedPeptideIdsMergedSet.addAll( searchProteinLooplink.getAssociatedReportedPeptideIds() );
					associatedReportedPeptideIdsRelatedPeptidesUniqueMergedSet.addAll( searchProteinLooplink.getAssociatedReportedPeptideIdsRelatedPeptidesUnique() );
					searchProteinLooplinksMapOnSearchDTOForThisItem.put(searchDTO, searchProteinLooplink );
				}
				ProteinSequenceVersionObject ProteinSequenceObject = new ProteinSequenceVersionObject();
				ProteinSequenceObject.setProteinSequenceVersionId( mergedSearchProteinLooplinkWrapper.getProteinId() );
				MergedSearchProtein protein = new MergedSearchProtein( searchesForThisItem, ProteinSequenceObject );
				MergedSearchProteinLooplink mergedSearchProteinLooplink = new MergedSearchProteinLooplink();
				looplinks.add( mergedSearchProteinLooplink );
				mergedSearchProteinLooplink.setProtein( protein );
				mergedSearchProteinLooplink.setProteinPosition1( mergedSearchProteinLooplinkWrapper.getProteinPosition_1() );
				mergedSearchProteinLooplink.setProteinPosition2( mergedSearchProteinLooplinkWrapper.getProteinPosition_2() );
				mergedSearchProteinLooplink.setSearches( searchesForThisItem );
				mergedSearchProteinLooplink.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );
				mergedSearchProteinLooplink.setSearchProteinLooplinks( searchProteinLooplinksMapOnSearchDTOForThisItem );
				mergedSearchProteinLooplink.setNumPsms( numPsms );
				mergedSearchProteinLooplink.setNumPeptides( associatedReportedPeptideIdsMergedSet.size() );
				mergedSearchProteinLooplink.setNumUniquePeptides( associatedReportedPeptideIdsRelatedPeptidesUniqueMergedSet.size() );
				//  Copy Peptide and PSM annotations to display lists 
				List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch = new ArrayList<>( searchIdsListDeduppedSorted.size() );
				mergedSearchProteinLooplink.setPeptidePsmAnnotationValueListsForEachSearch( peptidePsmAnnotationValueListsForEachSearch );
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
					SearchProteinLooplinkWrapper searchProteinLooplinkWrapper = searchProteinLooplinkWrapperWrapperMapOnSearchId.get( searchId );
					if ( searchProteinLooplinkWrapper == null ) {
						//  Create empty entries in list
						for ( int counter = 0; counter < peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size(); counter++ ) {
							psmAnnotationValueList.add( "" );
						}
						for ( int counter = 0; counter < psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size(); counter++ ) {
							psmAnnotationValueList.add( "" );
						}
					} else {
						//  Populate from wrapper
						Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap = searchProteinLooplinkWrapper.getPeptideAnnotationDTOMap();
						Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap = searchProteinLooplinkWrapper.getPsmAnnotationDTOMap();
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
			}  //  END:   for ( MergedSearchProteinLooplinkWrapper mergedSearchProteinLooplinkWrapper : mergedSearchProteinLooplinkWrapperList ) {
			
			List<MergedSearchProteinLooplinkWrapper> wrappedLinks = new ArrayList<MergedSearchProteinLooplinkWrapper>( looplinks.size() );
			////////////////////////////////////////////
			/////////   Add SearchBooleanWrapper for the searches for showing which searches the protein is in
			for( MergedSearchProteinLooplink link : looplinks ) {
				MergedSearchProteinLooplinkWrapper wrapper = new MergedSearchProteinLooplinkWrapper();
				List<SearchBooleanWrapper> booleanWrapper = new ArrayList<SearchBooleanWrapper>( searches.size() );
				for( SearchDTO search : searches ) {
					if( link.getSearches().contains( search ) ) {
						booleanWrapper.add( new SearchBooleanWrapper( search, true ) );
					} else {
						booleanWrapper.add( new SearchBooleanWrapper( search, false ) );
					}					
				}
				wrapper.setMergedSearchProteinLooplink( link );
				wrapper.setSearchContainsLooplink( booleanWrapper );
				wrappedLinks.add( wrapper );
			}
			proteinsMergedCommonPageDownloadResult.wrappedLoopLinks = wrappedLinks;
		} 	//   END:   Else of:  if ( forCrosslinksOrLooplinkOrBoth != ForCrosslinksOrLooplinkOrBoth.LOOPLINKS && forCrosslinksOrLooplinkOrBoth != ForCrosslinksOrLooplinkOrBoth.BOTH_CROSSLINKS_AND_LOOPLINKS
		
		proteinsMergedCommonPageDownloadResult.peptidePsmAnnotationNameDescListsForEachSearch = peptidePsmAnnotationNameDescListsForEachSearch;
		proteinsMergedCommonPageDownloadResult.crosslinks = crosslinks;
		proteinsMergedCommonPageDownloadResult.looplinks = looplinks;
		return proteinsMergedCommonPageDownloadResult;
	}
	/////////////////////////////////////////////////
	/**
	 * 
	 *
	 */
	private static class CrosslinkPrimaryKey {
		private int proteinId_1;
		private int proteinId_2;
		private int protein_1_Position;
		private int protein_2_Position;
		/**
		 * constructor
		 */
		public CrosslinkPrimaryKey( SearchProteinCrosslink link ) {
			proteinId_1 = link.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
			proteinId_2 = link.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
			protein_1_Position = link.getProtein1Position();
			protein_2_Position = link.getProtein2Position();
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + proteinId_1;
			result = prime * result + proteinId_2;
			result = prime * result + protein_1_Position;
			result = prime * result + protein_2_Position;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CrosslinkPrimaryKey other = (CrosslinkPrimaryKey) obj;
			if (proteinId_1 != other.proteinId_1)
				return false;
			if (proteinId_2 != other.proteinId_2)
				return false;
			if (protein_1_Position != other.protein_1_Position)
				return false;
			if (protein_2_Position != other.protein_2_Position)
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "CrosslinkPrimaryKey [proteinId_1=" + proteinId_1
					+ ", proteinId_2=" + proteinId_2 + ", protein_1_Position="
					+ protein_1_Position + ", protein_2_Position="
					+ protein_2_Position + "]";
		}
	}
	/**
	 * 
	 *
	 */
	private static class LooplinkPrimaryKey {
		private int proteinId;
		private int proteinPosition_1;
		private int proteinPosition_2;
		/**
		 * constructor
		 */
		public LooplinkPrimaryKey( SearchProteinLooplink link ) {
			proteinId = link.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
			proteinPosition_1 = link.getProteinPosition1();
			proteinPosition_2 = link.getProteinPosition2();
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + proteinId;
			result = prime * result + proteinPosition_1;
			result = prime * result + proteinPosition_2;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LooplinkPrimaryKey other = (LooplinkPrimaryKey) obj;
			if (proteinId != other.proteinId)
				return false;
			if (proteinPosition_1 != other.proteinPosition_1)
				return false;
			if (proteinPosition_2 != other.proteinPosition_2)
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "LooplinkPrimaryKey [proteinId=" + proteinId
					+ ", proteinPosition_1=" + proteinPosition_1
					+ ", proteinPosition_2=" + proteinPosition_2 + "]";
		}
	}
	////////////////////////////////////////
	////////   Sorter Class to Sort Crosslink Proteins
	/**
	 * 
	 *
	 */
	private class MergedSearchProteinCrosslinkWrapperSorter implements Comparator<MergedSearchProteinCrosslinkWrapper> {

		SortAnnDataCommon sortAnnDataCommon = new SortAnnDataCommon();
		
		List<Integer> searchIdsListDeduppedSorted;
		
		@Override
		public int compare(MergedSearchProteinCrosslinkWrapper o1, MergedSearchProteinCrosslinkWrapper o2) {
			Map<Integer, SearchProteinCrosslinkWrapper> searchProteinCrosslinkWrapperMapOnSearchId_o1 = o1.getSearchProteinCrosslinkWrapperMapOnSearchId();
			Map<Integer, SearchProteinCrosslinkWrapper> searchProteinCrosslinkWrapperMapOnSearchId_o2 = o2.getSearchProteinCrosslinkWrapperMapOnSearchId();

			for ( Integer searchId : searchIdsListDeduppedSorted ) {
				
				//  First Sort if both objects to sort have data for this search id
				SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper_o1 = searchProteinCrosslinkWrapperMapOnSearchId_o1.get( searchId );
				SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper_o2 = searchProteinCrosslinkWrapperMapOnSearchId_o2.get( searchId );
				if ( searchProteinCrosslinkWrapper_o1 == null && searchProteinCrosslinkWrapper_o2 == null ) {
					continue;  //  Sort on a later search id where there is data
				}
				if ( searchProteinCrosslinkWrapper_o1 == null ) {
					return 1;  //  Sort o1 after o2
				}
				if ( searchProteinCrosslinkWrapper_o2 == null ) {
					return -1; //  Sort o1 before o2  
				}

				//  Sort data on Peptide then PSM Ann Data.  If all match, return null, else return sort order
				Integer returnValue = sortAnnDataCommon.compare( searchProteinCrosslinkWrapper_o1, searchProteinCrosslinkWrapper_o2, searchId );
				
				if ( returnValue != null ) {
					return returnValue; // Only return if not null
				}

				//  If everything matches, sort on protein id, protein positions
				SearchProteinCrosslink searchProteinCrosslink_o1 = searchProteinCrosslinkWrapper_o1.getSearchProteinCrosslink();
				SearchProteinCrosslink searchProteinCrosslink_o2 = searchProteinCrosslinkWrapper_o2.getSearchProteinCrosslink();
				if ( searchProteinCrosslink_o1.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() != searchProteinCrosslink_o2.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
					return searchProteinCrosslink_o1.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() - searchProteinCrosslink_o2.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
				}
				if ( searchProteinCrosslink_o1.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() != searchProteinCrosslink_o2.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
					return searchProteinCrosslink_o1.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() - searchProteinCrosslink_o2.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
				}
				if ( searchProteinCrosslink_o1.getProtein1Position() != searchProteinCrosslink_o2.getProtein1Position() ) {
					return searchProteinCrosslink_o1.getProtein1Position() - searchProteinCrosslink_o2.getProtein1Position();
				}
				return searchProteinCrosslink_o1.getProtein2Position() - searchProteinCrosslink_o2.getProtein2Position();
			}
			//  No data in the same search so sort on protein id, protein positions
			if ( o1.getProteinId_1() != o2.getProteinId_1() ) {
				return o1.getProteinId_1() - o2.getProteinId_1();
			}
			if ( o1.getProteinId_2() != o2.getProteinId_2() ) {
				return o1.getProteinId_2() - o2.getProteinId_2();
			}
			if ( o1.getProtein_1_Position() != o2.getProtein_1_Position() ) {
				return o1.getProtein_1_Position() - o2.getProtein_1_Position();
			}
			return o1.getProtein_2_Position() - o2.getProtein_2_Position();
		}
	}
	
	////////////////////////////////////////
	////////   Sorter Class to Sort Looplink Proteins
	/**
	 * 
	 *
	 */
	private class MergedSearchProteinLooplinkWrapperSorter implements Comparator<MergedSearchProteinLooplinkWrapper> {

		SortAnnDataCommon sortAnnDataCommon = new SortAnnDataCommon();
		
		List<Integer> searchIdsListDeduppedSorted;

		@Override
		public int compare(MergedSearchProteinLooplinkWrapper o1, MergedSearchProteinLooplinkWrapper o2) {
			
			Map<Integer, SearchProteinLooplinkWrapper> searchProteinLooplinkWrapperMapOnSearchId_o1 = o1.getSearchProteinLooplinkWrapperMapOnSearchId();
			Map<Integer, SearchProteinLooplinkWrapper> searchProteinLooplinkWrapperMapOnSearchId_o2 = o2.getSearchProteinLooplinkWrapperMapOnSearchId();
			
			for ( Integer searchId : searchIdsListDeduppedSorted ) {

				SearchProteinLooplinkWrapper searchProteinLooplinkWrapper_o1 = searchProteinLooplinkWrapperMapOnSearchId_o1.get( searchId );
				SearchProteinLooplinkWrapper searchProteinLooplinkWrapper_o2 = searchProteinLooplinkWrapperMapOnSearchId_o2.get( searchId );
				if ( searchProteinLooplinkWrapper_o1 == null && searchProteinLooplinkWrapper_o2 == null ) {
					continue;  //  Sort on a later search id where there is data
				}
				if ( searchProteinLooplinkWrapper_o1 == null ) {
					return 1;  //  Sort o1 after o2
				}
				if ( searchProteinLooplinkWrapper_o2 == null ) {
					return -1; //  Sort o1 before o2  
				}

				//  Sort data on Peptide then PSM Ann Data.  If all match, return null, else return sort order
				Integer returnValue = sortAnnDataCommon.compare( searchProteinLooplinkWrapper_o1, searchProteinLooplinkWrapper_o2, searchId );
				
				if ( returnValue != null ) {
					return returnValue; // Only return if not null
				}

				//  If everything matches, sort on protein id, protein positions
				SearchProteinLooplink searchProteinLooplink_o1 = searchProteinLooplinkWrapper_o1.getSearchProteinLooplink();
				SearchProteinLooplink searchProteinLooplink_o2 = searchProteinLooplinkWrapper_o2.getSearchProteinLooplink();
				if ( searchProteinLooplink_o1.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() != searchProteinLooplink_o2.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
					return searchProteinLooplink_o1.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() - searchProteinLooplink_o2.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
				}
				if ( searchProteinLooplink_o1.getProteinPosition1() != searchProteinLooplink_o2.getProteinPosition1() ) {
					return searchProteinLooplink_o1.getProteinPosition1() - searchProteinLooplink_o2.getProteinPosition1();
				}
				return searchProteinLooplink_o1.getProteinPosition2() - searchProteinLooplink_o2.getProteinPosition2();
			}
			//  No data in the same search so sort on protein id, protein positions
			if ( o1.getProteinId() != o2.getProteinId() ) {
				return o1.getProteinId() - o2.getProteinId();
			}
			if ( o1.getProteinPosition_1() != o2.getProteinPosition_1() ) {
				return o1.getProteinPosition_1() - o2.getProteinPosition_1();
			}
			return o1.getProteinPosition_2() - o2.getProteinPosition_2();
		}
	}
	

	
	/**
	 * 
	 *  Sort Ann Data for both Crosslink and Looplink
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
        public int compare(SearchProtein o1, SearchProtein o2) {
            try { return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()); }
            catch( Exception e ) { return 0; }
        }
    }
    
	/**
	 * Returned object
	 *
	 */
	public static class ProteinsMergedCommonPageDownloadResult {
		
		ProteinQueryJSONRoot proteinQueryJSONRoot;
		Set<Integer> excludeTaxonomy_Ids_Set_UserInput;
		Set<Integer> excludeproteinSequenceVersionIds_Set_UserInput;
		List<AnnDisplayNameDescPeptPsmListsPair> peptidePsmAnnotationNameDescListsForEachSearch;
		List<MergedSearchProteinCrosslink> crosslinks;
		List<MergedSearchProteinLooplink> looplinks;
		List<MergedSearchProteinCrosslinkWrapper> wrappedCrossLinks;
		List<MergedSearchProteinLooplinkWrapper> wrappedLoopLinks;
		/////////////////////
		// all possible proteins for Crosslinks and Looplinks across all searches (for "Exclude Protein" list on web page)
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
		public List<MergedSearchProteinCrosslink> getCrosslinks() {
			return crosslinks;
		}
		public void setCrosslinks(List<MergedSearchProteinCrosslink> crosslinks) {
			this.crosslinks = crosslinks;
		}
		public List<MergedSearchProteinLooplink> getLooplinks() {
			return looplinks;
		}
		public void setLooplinks(List<MergedSearchProteinLooplink> looplinks) {
			this.looplinks = looplinks;
		}
		public List<MergedSearchProteinCrosslinkWrapper> getWrappedCrossLinks() {
			return wrappedCrossLinks;
		}
		public void setWrappedCrossLinks(
				List<MergedSearchProteinCrosslinkWrapper> wrappedCrossLinks) {
			this.wrappedCrossLinks = wrappedCrossLinks;
		}
		public List<MergedSearchProteinLooplinkWrapper> getWrappedLoopLinks() {
			return wrappedLoopLinks;
		}
		public void setWrappedLoopLinks(
				List<MergedSearchProteinLooplinkWrapper> wrappedLoopLinks) {
			this.wrappedLoopLinks = wrappedLoopLinks;
		}
	}
}
