package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortAnnotationDTORecords;
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
import org.yeastrc.xlink.www.searcher.SearchProteinCrosslinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinLooplinkSearcher;

/**
 * 
 *
 */
public class ProteinsMergedCommonPageDownload {

	private static final Logger log = Logger.getLogger( ProteinsMergedCommonPageDownload.class );
	

	private ProteinsMergedCommonPageDownload() { }

	public static ProteinsMergedCommonPageDownload getInstance() { 
		return new ProteinsMergedCommonPageDownload(); 
	}
	
	
	public static enum ForCrosslinksOrLooplinkOrBoth { BOTH_CROSSLINKS_AND_LOOPLINKS, CROSSLINKS, LOOPLINKS }
	
	
	public static class ProteinsMergedCommonPageDownloadResult {
		
		ProteinQueryJSONRoot proteinQueryJSONRoot;
		

		Set<Integer> excludeTaxonomy_Ids_Set_UserInput;
		Set<Integer> excludeProtein_Ids_Set_UserInput;
		
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
			return excludeProtein_Ids_Set_UserInput;
		}
		public void setExcludeProtein_Ids_Set_UserInput(
				Set<Integer> excludeProtein_Ids_Set_UserInput) {
			this.excludeProtein_Ids_Set_UserInput = excludeProtein_Ids_Set_UserInput;
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
	
	
	
	public ProteinsMergedCommonPageDownloadResult getCrosslinksAndLooplinkWrapped(
			
			MergedSearchViewProteinsForm form,
			ForCrosslinksOrLooplinkOrBoth forCrosslinksOrLooplinkOrBoth,
			List<Integer> searchIdsListDeduppedSorted,
			List<SearchDTO> searches,
			Map<Integer, SearchDTO> searchesMapOnId
			
			) throws Exception, ProxlWebappDataException {
		
		


		ProteinsMergedCommonPageDownloadResult proteinsMergedCommonPageDownloadResult = new ProteinsMergedCommonPageDownloadResult();
			

		/////////////////////

		// all possible proteins for Crosslinks and Looplinks across all searches (for "Exclude Protein" list on web page)

		Map<Integer,Set<Integer>> allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds = new HashMap<>();
		proteinsMergedCommonPageDownloadResult.allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds =
				allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds;
	

		//   Get Query JSON from the form and if not empty, deserialize it

		ProteinQueryJSONRoot proteinQueryJSONRoot = 
				GetProteinQueryJSONRootFromFormData.getInstance()
				.getProteinQueryJSONRootFromFormData( form, searchIdsListDeduppedSorted );

		
		proteinsMergedCommonPageDownloadResult.proteinQueryJSONRoot = proteinQueryJSONRoot;
		
		////////////
		
		//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup

		Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();


		Set<Integer> excludeProtein_Ids_Set_UserInput = new HashSet<>();
		
		proteinsMergedCommonPageDownloadResult.excludeTaxonomy_Ids_Set_UserInput = excludeTaxonomy_Ids_Set_UserInput;
		proteinsMergedCommonPageDownloadResult.excludeProtein_Ids_Set_UserInput = excludeProtein_Ids_Set_UserInput;
		

		if ( proteinQueryJSONRoot.getExcludeTaxonomy() != null ) {

			for ( Integer taxonomyId : proteinQueryJSONRoot.getExcludeTaxonomy() ) {
			
				excludeTaxonomy_Ids_Set_UserInput.add( taxonomyId );
			}
		}

		if ( proteinQueryJSONRoot.getExcludeProtein() != null ) {

			for ( Integer proteinId : proteinQueryJSONRoot.getExcludeProtein() ) {

				excludeProtein_Ids_Set_UserInput.add( proteinId );
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

		//   Primary Key is the combination of protein id(s) and protein positions


		Map<CrosslinkPrimaryKey, Map<Integer, SearchProteinCrosslinkWrapper>> crosslinksWrapperMapOnCrosslinkKeySearchId = new HashMap<>();
		Map<LooplinkPrimaryKey, Map<Integer, SearchProteinLooplinkWrapper>> looplinksWrapperMapOnLooplinkKeySearchId = new HashMap<>();


		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = new HashMap<>();
		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap = new HashMap<>();
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

			Integer searchId = searchDTO.getId();

			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );

			if ( searcherCutoffValuesSearchLevel == null ) {

				searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
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

			final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted = new ArrayList<>( psmCutoffValuesList.size() );

			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {

				psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}

			SortAnnotationDTORecords.getInstance()
			.sortPsmAnnotationTypeDTOForBestPsmAnnotations_AnnotationDisplayOrder( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );

			psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSortedBySearchIdMap.put(searchId, psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );





			/////////////////////////////////////////////////////////////

			//////////////////   Get Crosslinks data from DATABASE  from database



			List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
					SearchProteinCrosslinkSearcher.getInstance().searchOnSearchIdandCutoffs( searchDTO, searcherCutoffValuesSearchLevel );


			/////////////////////////////////////////////////////////////

			//////////////////   Get Looplinks data from DATABASE   from database



			List<SearchProteinLooplinkWrapper> wrappedLooplinks = 
					SearchProteinLooplinkSearcher.getInstance().searchOnSearchIdandCutoffs( searchDTO, searcherCutoffValuesSearchLevel );


			
			/////////////////////////////////////////////////////////
			
			//////////    Add to list of all proteins (for "Exclude Protein" list on web page) 

			for ( SearchProteinCrosslinkWrapper  searchProteinCrosslinkWrapper : wrappedCrosslinks ) {

				SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();

				SearchProtein searchProtein_1 = searchProteinCrosslink.getProtein1();
				SearchProtein searchProtein_2 = searchProteinCrosslink.getProtein2();
				
				Integer searchProtein_id_1 = searchProtein_1.getNrProtein().getNrseqId();
				Integer searchProtein_id_2 = searchProtein_2.getNrProtein().getNrseqId();
									
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
				
				Integer searchProtein_id = searchProtein.getNrProtein().getNrseqId();
				
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

					|| ( proteinQueryJSONRoot.getExcludeTaxonomy() != null && proteinQueryJSONRoot.getExcludeTaxonomy().length > 0 ) ||
					( proteinQueryJSONRoot.getExcludeProtein() != null && proteinQueryJSONRoot.getExcludeProtein().length > 0 ) ) {


				///////  Output Lists, Results After Filtering

				List<SearchProteinCrosslinkWrapper> wrappedCrosslinksAfterFilter = new ArrayList<>( wrappedLooplinks.size() );

				List<SearchProteinLooplinkWrapper> wrappedLooplinksAfterFilter = new ArrayList<>( wrappedLooplinks.size() );



				///  Filter CROSSLINKS

				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {

					SearchProteinCrosslink link = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();

					// did they request to removal of non unique peptides?

					if( proteinQueryJSONRoot.isFilterNonUniquePeptides()  ) {

						if( link.getNumUniqueLinkedPeptides() < 1 ) {

							//  Skip to next entry in list, dropping this entry from output list

							continue;  // EARLY CONTINUE
						}
					}


					//
					//						
					//							link.getNumPsms() <= 1 WILL NOT WORK if more than one search since it is across all searches
					//						
					//						// did they request to removal of links with only one PSM?
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

					// did user request removal of certain taxonomy IDs?
					
					if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {

						int taxonomyId_1 = link.getProtein1().getNrProtein().getTaxonomyId();
						int taxonomyId_2 = link.getProtein2().getNrProtein().getTaxonomyId();
						
						if ( excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId_1 ) 
								|| excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId_2 ) ) {

							//  Skip to next entry in list, dropping this entry from output list

							continue;  // EARLY CONTINUE
						}
					}

					// did user request removal of certain protein IDs?
					
					if( ! excludeProtein_Ids_Set_UserInput.isEmpty() ) {

						int proteinId_1 = link.getProtein1().getNrProtein().getNrseqId();
						int proteinId_2 = link.getProtein2().getNrProtein().getNrseqId();
						
						if ( excludeProtein_Ids_Set_UserInput.contains( proteinId_1 ) 
								|| excludeProtein_Ids_Set_UserInput.contains( proteinId_2 ) ) {

							//  Skip to next entry in list, dropping this entry from output list

							continue;  // EARLY CONTINUE
						}
					}					

					wrappedCrosslinksAfterFilter.add( searchProteinCrosslinkWrapper );
				}




				///  Filter LOOPLINKS

				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {

					SearchProteinLooplink link = searchProteinLooplinkWrapper.getSearchProteinLooplink();

					// did they request to removal of non unique peptides?

					if( proteinQueryJSONRoot.isFilterNonUniquePeptides()  ) {

						if( link.getNumUniquePeptides() < 1 ) {

							//  Skip to next entry in list, dropping this entry from output list

							continue;  // EARLY CONTINUE
						}
					}


					//
					//						
					//							link.getNumPsms() <= 1 WILL NOT WORK if more than one search since it is across all searches
					//						
					//						// did they request to removal of links with only one PSM?
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


					// did user request removal of certain taxonomy IDs?
					
					if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {

						int taxonomyId = link.getProtein().getNrProtein().getTaxonomyId();
						
						if ( excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId ) ) {

							//  Skip to next entry in list, dropping this entry from output list

							continue;  // EARLY CONTINUE
						}
					}

					// did user request removal of certain protein IDs?
					
					if( ! excludeProtein_Ids_Set_UserInput.isEmpty() ) {

						int proteinId = link.getProtein().getNrProtein().getNrseqId();
						
						if ( excludeProtein_Ids_Set_UserInput.contains( proteinId ) ) {

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

				crosslinksWrapperMapOnSearchId.put( searchId, searchProteinCrosslinkWrapper );
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

				looplinksWrapperMapOnSearchId.put( searchId, searchProteinLooplinkWrapper );
			}

		}  //  End processing by Search Id



		/////////////////////////

		//////////   Get the list of column headers for the searches

		List<AnnDisplayNameDescPeptPsmListsPair> peptidePsmAnnotationNameDescListsForEachSearch = new ArrayList<>( searchIdsListDeduppedSorted.size() );


		//			request.setAttribute( "peptidePsmAnnotationNameDescListsForEachSearch", peptidePsmAnnotationNameDescListsForEachSearch );

		for ( Integer searchId : searchIdsListDeduppedSorted ) {


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

		//              Process Crosslinks or Looplinks, depending on which page is being displayed
		//					or Both for Download


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

				List<SearchDTO> searchesForThisItem = new ArrayList<>( searchesMapOnId.size() );
				List<Integer> searchIdsForThisItem = new ArrayList<>( searchesMapOnId.size() );


				for ( Map.Entry<Integer, SearchProteinCrosslinkWrapper> crosslinksWrapperEntry : crosslinksWrapperMapOnSearchId.entrySet() ) {

					Integer searchId = crosslinksWrapperEntry.getKey();

					SearchDTO searchDTO = searchesMapOnId.get( searchId );

					if ( searchDTO == null ) {

						String msg = "Failed to get searchDTO list for searchId : " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}

					searchesForThisItem.add( searchDTO );

					searchIdsForThisItem.add(searchId);

				}
				
				SearchProteinCrosslinkWrapper anySearchProteinCrosslinkWrapper = crosslinksWrapperMapOnSearchId.entrySet().iterator().next().getValue();

				SearchProteinCrosslink anySearchProteinCrosslink = anySearchProteinCrosslinkWrapper.getSearchProteinCrosslink();

				
				NRProteinDTO nrProteinDTO_1 = new NRProteinDTO();
				nrProteinDTO_1.setNrseqId( anySearchProteinCrosslink.getProtein1().getNrProtein().getNrseqId() );

				NRProteinDTO nrProteinDTO_2 = new NRProteinDTO();
				nrProteinDTO_2.setNrseqId( anySearchProteinCrosslink.getProtein2().getNrProtein().getNrseqId() );

				MergedSearchProtein protein_1 = new MergedSearchProtein( searchesForThisItem, nrProteinDTO_1 );
				MergedSearchProtein protein_2 = new MergedSearchProtein( searchesForThisItem, nrProteinDTO_2 );


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
    
			
			////     Collect Annotation data for Crosslinks and sort
			

			/////////////////////

			//   Transfer Crosslinks into a list



			List<MergedSearchProteinCrosslinkWrapper> mergedSearchProteinCrosslinkWrapperList = new ArrayList<>( crosslinksWrapperMapOnCrosslinkKeySearchId.size() );

			for ( Map.Entry<CrosslinkPrimaryKey, Map<Integer, SearchProteinCrosslinkWrapper>> entry : crosslinksWrapperMapOnCrosslinkKeySearchId.entrySet() ) {

				Map<Integer, SearchProteinCrosslinkWrapper> crosslinksWrapperMapOnSearchId = entry.getValue();
				
				MergedSearchProteinCrosslinkWrapper mergedSearchProteinCrosslinkWrapper = new MergedSearchProteinCrosslinkWrapper();
				mergedSearchProteinCrosslinkWrapperList.add( mergedSearchProteinCrosslinkWrapper );

				mergedSearchProteinCrosslinkWrapper.setSearchProteinCrosslinkWrapperMapOnSearchId( crosslinksWrapperMapOnSearchId );

				SearchProteinCrosslinkWrapper anySearchProteinCrosslinkWrapper = crosslinksWrapperMapOnSearchId.entrySet().iterator().next().getValue();

				SearchProteinCrosslink anySearchProteinCrosslink = anySearchProteinCrosslinkWrapper.getSearchProteinCrosslink();

				mergedSearchProteinCrosslinkWrapper.setProteinId_1( anySearchProteinCrosslink.getProtein1().getNrProtein().getNrseqId() );
				mergedSearchProteinCrosslinkWrapper.setProteinId_2( anySearchProteinCrosslink.getProtein2().getNrProtein().getNrseqId() );
				mergedSearchProteinCrosslinkWrapper.setProtein_1_Position( anySearchProteinCrosslink.getProtein1Position() );
				mergedSearchProteinCrosslinkWrapper.setProtein_2_Position( anySearchProteinCrosslink.getProtein2Position() );
			}

			//////////  

			//   Sort Crosslinks on values in first search, or so on if no data for first search

			MergedSearchProteinCrosslinkWrapperSorter crosslinkSorter = new MergedSearchProteinCrosslinkWrapperSorter();

			crosslinkSorter.peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
			crosslinkSorter.searchIdsListDeduppedSorted = searchIdsListDeduppedSorted;

			Collections.sort( mergedSearchProteinCrosslinkWrapperList , crosslinkSorter );



			//  Create Merged Crosslink Protein object

			//  Copy Peptide and PSM annotations to display lists

			for ( MergedSearchProteinCrosslinkWrapper mergedSearchProteinCrosslinkWrapper : mergedSearchProteinCrosslinkWrapperList ) {

				
				Map<Integer, SearchProteinCrosslinkWrapper> searchProteinCrosslinkWrapperWrapperMapOnSearchId =
						mergedSearchProteinCrosslinkWrapper.getSearchProteinCrosslinkWrapperMapOnSearchId();

				/////////

				//  Get searches for this item

				List<SearchDTO> searchesForThisItem = new ArrayList<>( searchesMapOnId.size() );
				List<Integer> searchIdsForThisItem = new ArrayList<>( searchesMapOnId.size() );


				for ( Map.Entry<Integer, SearchProteinCrosslinkWrapper> crosslinksWrapperEntry : searchProteinCrosslinkWrapperWrapperMapOnSearchId.entrySet() ) {

					Integer searchId = crosslinksWrapperEntry.getKey();

					SearchDTO searchDTO = searchesMapOnId.get( searchId );

					if ( searchDTO == null ) {

						String msg = "Failed to get searchDTO list for searchId : " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}

					searchesForThisItem.add( searchDTO );

					searchIdsForThisItem.add(searchId);

				}
				

				NRProteinDTO nrProteinDTO_1 = new NRProteinDTO();
				nrProteinDTO_1.setNrseqId( mergedSearchProteinCrosslinkWrapper.getProteinId_1() );

				NRProteinDTO nrProteinDTO_2 = new NRProteinDTO();
				nrProteinDTO_2.setNrseqId( mergedSearchProteinCrosslinkWrapper.getProteinId_2() );

				MergedSearchProtein protein_1 = new MergedSearchProtein( searchesForThisItem, nrProteinDTO_1 );
				MergedSearchProtein protein_2 = new MergedSearchProtein( searchesForThisItem, nrProteinDTO_2 );


				
				MergedSearchProteinCrosslink mergedSearchProteinCrosslink = new MergedSearchProteinCrosslink();
				crosslinks.add( mergedSearchProteinCrosslink );
				
				mergedSearchProteinCrosslink.setProtein1( protein_1 );
				mergedSearchProteinCrosslink.setProtein2( protein_2 );
				mergedSearchProteinCrosslink.setProtein1Position( mergedSearchProteinCrosslinkWrapper.getProtein_1_Position() );
				mergedSearchProteinCrosslink.setProtein2Position( mergedSearchProteinCrosslinkWrapper.getProtein_2_Position() );

				mergedSearchProteinCrosslink.setSearches( searchesForThisItem );
				mergedSearchProteinCrosslink.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );


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

			}  //  END:   for ( MergedSearchProteinCrosslinkWrapper webMergedReportedPeptideWrapper : mergedSearchProteinCrosslinkWrapperList ) {


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

				List<SearchDTO> searchesForThisItem = new ArrayList<>( searchesMapOnId.size() );
				List<Integer> searchIdsForThisItem = new ArrayList<>( searchesMapOnId.size() );


				for ( Map.Entry<Integer, SearchProteinLooplinkWrapper> looplinksWrapperEntry : looplinksWrapperMapOnSearchId.entrySet() ) {

					Integer searchId = looplinksWrapperEntry.getKey();

					SearchDTO searchDTO = searchesMapOnId.get( searchId );

					if ( searchDTO == null ) {

						String msg = "Failed to get searchDTO list for searchId : " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}

					searchesForThisItem.add( searchDTO );

					searchIdsForThisItem.add(searchId);

				}
				
				SearchProteinLooplinkWrapper anySearchProteinLooplinkWrapper = looplinksWrapperMapOnSearchId.entrySet().iterator().next().getValue();

				SearchProteinLooplink anySearchProteinLooplink = anySearchProteinLooplinkWrapper.getSearchProteinLooplink();

				
				NRProteinDTO nrProteinDTO = new NRProteinDTO();
				nrProteinDTO.setNrseqId( anySearchProteinLooplink.getProtein().getNrProtein().getNrseqId() );

				MergedSearchProtein protein = new MergedSearchProtein( searchesForThisItem, nrProteinDTO );


				MergedSearchProteinLooplink mergedSearchProteinLooplink = new MergedSearchProteinLooplink();
				looplinks.add( mergedSearchProteinLooplink );

				mergedSearchProteinLooplink.setProtein( protein );
				mergedSearchProteinLooplink.setProteinPosition1( anySearchProteinLooplink.getProteinPosition1() );
				mergedSearchProteinLooplink.setProteinPosition2( anySearchProteinLooplink.getProteinPosition2() );

				mergedSearchProteinLooplink.setSearches( searchesForThisItem );
				mergedSearchProteinLooplink.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );


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

				mergedSearchProteinLooplinkWrapper.setProteinId( anySearchProteinLooplink.getProtein().getNrProtein().getNrseqId() );
				mergedSearchProteinLooplinkWrapper.setProteinPosition_1( anySearchProteinLooplink.getProteinPosition1() );
				mergedSearchProteinLooplinkWrapper.setProteinPosition_2( anySearchProteinLooplink.getProteinPosition2() );
			}

			//////////  

			//   Sort Looplinks on values in first search, or so on if no data for first search

			MergedSearchProteinLooplinkWrapperSorter looplinkSorter = new MergedSearchProteinLooplinkWrapperSorter();

			looplinkSorter.peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap = peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;
			looplinkSorter.searchIdsListDeduppedSorted = searchIdsListDeduppedSorted;

			Collections.sort( mergedSearchProteinLooplinkWrapperList , looplinkSorter );



			//  Create Merged Looplink Protein object

			//  Copy Peptide and PSM annotations to display lists

			for ( MergedSearchProteinLooplinkWrapper mergedSearchProteinLooplinkWrapper : mergedSearchProteinLooplinkWrapperList ) {

				
				Map<Integer, SearchProteinLooplinkWrapper> searchProteinLooplinkWrapperWrapperMapOnSearchId =
						mergedSearchProteinLooplinkWrapper.getSearchProteinLooplinkWrapperMapOnSearchId();

				/////////

				//  Get searches for this item

				List<SearchDTO> searchesForThisItem = new ArrayList<>( searchesMapOnId.size() );
				List<Integer> searchIdsForThisItem = new ArrayList<>( searchesMapOnId.size() );


				for ( Map.Entry<Integer, SearchProteinLooplinkWrapper> looplinksWrapperEntry : searchProteinLooplinkWrapperWrapperMapOnSearchId.entrySet() ) {

					Integer searchId = looplinksWrapperEntry.getKey();

					SearchDTO searchDTO = searchesMapOnId.get( searchId );

					if ( searchDTO == null ) {

						String msg = "Failed to get searchDTO list for searchId : " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}

					searchesForThisItem.add( searchDTO );

					searchIdsForThisItem.add(searchId);

				}
				

				NRProteinDTO nrProteinDTO = new NRProteinDTO();
				nrProteinDTO.setNrseqId( mergedSearchProteinLooplinkWrapper.getProteinId() );

				MergedSearchProtein protein = new MergedSearchProtein( searchesForThisItem, nrProteinDTO );

				MergedSearchProteinLooplink mergedSearchProteinLooplink = new MergedSearchProteinLooplink();
				looplinks.add( mergedSearchProteinLooplink );
				
				mergedSearchProteinLooplink.setProtein( protein );
				mergedSearchProteinLooplink.setProteinPosition1( mergedSearchProteinLooplinkWrapper.getProteinPosition_1() );
				mergedSearchProteinLooplink.setProteinPosition2( mergedSearchProteinLooplinkWrapper.getProteinPosition_2() );

				mergedSearchProteinLooplink.setSearches( searchesForThisItem );
				mergedSearchProteinLooplink.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );


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

			}  //  END:   for ( MergedSearchProteinLooplinkWrapper webMergedReportedPeptideWrapper : mergedSearchProteinLooplinkWrapperList ) {


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

			proteinId_1 = link.getProtein1().getNrProtein().getNrseqId();
			proteinId_2 = link.getProtein2().getNrProtein().getNrseqId();

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

			proteinId = link.getProtein().getNrProtein().getNrseqId();

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


	}



	////////////////////////////////////////

	////////   Sorter Class to Sort Crosslink Proteins

	/**
	 * 
	 *
	 */
	private class MergedSearchProteinCrosslinkWrapperSorter implements Comparator<MergedSearchProteinCrosslinkWrapper> {

		List<Integer> searchIdsListDeduppedSorted;
		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;

		@Override
		public int compare(MergedSearchProteinCrosslinkWrapper o1, MergedSearchProteinCrosslinkWrapper o2) {

			Map<Integer, SearchProteinCrosslinkWrapper> searchProteinCrosslinkWrapperMapOnSearchId_o1 = o1.getSearchProteinCrosslinkWrapperMapOnSearchId();
			Map<Integer, SearchProteinCrosslinkWrapper> searchProteinCrosslinkWrapperMapOnSearchId_o2 = o2.getSearchProteinCrosslinkWrapperMapOnSearchId();

			for ( Integer searchId : searchIdsListDeduppedSorted ) {

				List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted =
						peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap.get( searchId );

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



				//  Cross through the annotation types (sorted on sort order), comparing the values

				for ( AnnotationTypeDTO annotationTypeDTO : peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted ) {

					int typeId = annotationTypeDTO.getId();

					AnnotationDataBaseDTO o1_WebReportedPeptide = searchProteinCrosslinkWrapper_o1.getPeptideAnnotationDTOMap().get( typeId );
					if ( o1_WebReportedPeptide == null ) {

						String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}

					double o1Value = o1_WebReportedPeptide.getValueDouble();


					AnnotationDataBaseDTO o2_WebReportedPeptide = searchProteinCrosslinkWrapper_o2.getPeptideAnnotationDTOMap().get( typeId );
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

				//  If everything matches, sort on protein id, protein positions

				SearchProteinCrosslink searchProteinCrosslink_o1 = searchProteinCrosslinkWrapper_o1.getSearchProteinCrosslink();
				SearchProteinCrosslink searchProteinCrosslink_o2 = searchProteinCrosslinkWrapper_o2.getSearchProteinCrosslink();


				if ( searchProteinCrosslink_o1.getProtein1().getNrProtein().getNrseqId() != searchProteinCrosslink_o2.getProtein1().getNrProtein().getNrseqId() ) {

					return searchProteinCrosslink_o1.getProtein1().getNrProtein().getNrseqId() - searchProteinCrosslink_o2.getProtein1().getNrProtein().getNrseqId();
				}
				if ( searchProteinCrosslink_o1.getProtein2().getNrProtein().getNrseqId() != searchProteinCrosslink_o2.getProtein2().getNrProtein().getNrseqId() ) {

					return searchProteinCrosslink_o1.getProtein2().getNrProtein().getNrseqId() - searchProteinCrosslink_o2.getProtein2().getNrProtein().getNrseqId();
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

	////////   Sorter Class to Sort Crosslink Proteins

	/**
	 * 
	 *
	 */
	private class MergedSearchProteinLooplinkWrapperSorter implements Comparator<MergedSearchProteinLooplinkWrapper> {

		List<Integer> searchIdsListDeduppedSorted;
		Map<Integer, List<AnnotationTypeDTO>> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap;

		@Override
		public int compare(MergedSearchProteinLooplinkWrapper o1, MergedSearchProteinLooplinkWrapper o2) {

			Map<Integer, SearchProteinLooplinkWrapper> searchProteinLooplinkWrapperMapOnSearchId_o1 = o1.getSearchProteinLooplinkWrapperMapOnSearchId();
			Map<Integer, SearchProteinLooplinkWrapper> searchProteinLooplinkWrapperMapOnSearchId_o2 = o2.getSearchProteinLooplinkWrapperMapOnSearchId();

			for ( Integer searchId : searchIdsListDeduppedSorted ) {

				List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted =
						peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSortedBySearchIdMap.get( searchId );

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



				//  Loop through the annotation types (sorted on sort order), comparing the values

				for ( AnnotationTypeDTO annotationTypeDTO : peptideCutoffsAnnotationTypeDTOListAnnotationSortOrderSorted ) {

					int typeId = annotationTypeDTO.getId();

					AnnotationDataBaseDTO o1_WebReportedPeptide = searchProteinLooplinkWrapper_o1.getPeptideAnnotationDTOMap().get( typeId );
					if ( o1_WebReportedPeptide == null ) {

						String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}

					double o1Value = o1_WebReportedPeptide.getValueDouble();


					AnnotationDataBaseDTO o2_WebReportedPeptide = searchProteinLooplinkWrapper_o2.getPeptideAnnotationDTOMap().get( typeId );
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

				//  If everything matches, sort on protein id, protein positions

				SearchProteinLooplink searchProteinLooplink_o1 = searchProteinLooplinkWrapper_o1.getSearchProteinLooplink();
				SearchProteinLooplink searchProteinLooplink_o2 = searchProteinLooplinkWrapper_o2.getSearchProteinLooplink();


				if ( searchProteinLooplink_o1.getProtein().getNrProtein().getNrseqId() != searchProteinLooplink_o2.getProtein().getNrProtein().getNrseqId() ) {

					return searchProteinLooplink_o1.getProtein().getNrProtein().getNrseqId() - searchProteinLooplink_o2.getProtein().getNrProtein().getNrseqId();
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

}
