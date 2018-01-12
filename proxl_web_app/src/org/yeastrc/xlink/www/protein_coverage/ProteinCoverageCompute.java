package org.yeastrc.xlink.www.protein_coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.factories.ProteinSequenceVersionObjectFactory;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkablePositionsForLinkers;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.linked_positions.CrosslinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.LinkedPositions_FilterExcludeLinksWith_Param;
import org.yeastrc.xlink.www.linked_positions.MonolinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.UnlinkedDimerPeptideProteinMapping;
import org.yeastrc.xlink.www.linked_positions.LooplinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.UnlinkedDimerPeptideProteinMapping.UnlinkedDimerPeptideProteinMappingResult;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.ProteinCoverageData;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinDimer;
import org.yeastrc.xlink.www.objects.SearchProteinDimerWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;
import org.yeastrc.xlink.www.objects.SearchProteinMonolinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinked;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinkedWrapper;
import org.yeastrc.xlink.www.searcher.LinkersForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_TaxonomyIdsFor_ProtSeqVersionId_SearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.TaxonomyIdsForProtSeqIdSearchId_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.TaxonomyIdsForProtSeqIdSearchId_Result;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;

import com.google.common.collect.Range;

/**
 * Compute Protein Coverage
 *
 */
public class ProteinCoverageCompute {
	
	private static final Logger log = Logger.getLogger(ProteinCoverageCompute.class);
	
	private Collection<SearchDTO> searches;
	private SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel;
	private boolean filterNonUniquePeptides = false;
	private boolean filterOnlyOnePSM = false;
	private boolean filterOnlyOnePeptide = false;
	private int[] excludedproteinSequenceVersionIds;
	private int[] excludedTaxonomyIds;
	private LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param;
	
	/**
	 * Get a list describing the coverage of each protein in the requested collection of searches, given the
	 * set properties.
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ProteinCoverageData> getProteinCoverageData() throws Exception {
		////////////
		//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup
		Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
		Set<Integer> excludeProtein_Ids_Set_UserInput = new HashSet<>();
		if ( excludedTaxonomyIds != null ) {
			for ( Integer taxonomyId : excludedTaxonomyIds ) {
				excludeTaxonomy_Ids_Set_UserInput.add( taxonomyId );
			}
		}
		if ( excludedproteinSequenceVersionIds != null ) {
			for ( Integer proteinId : excludedproteinSequenceVersionIds ) {
				excludeProtein_Ids_Set_UserInput.add( proteinId );
			}
		}
		List<ProteinCoverageData> proteinCoverageDataList = new ArrayList<ProteinCoverageData>();
		Collection<MergedSearchProtein> proteins = new ArrayList<>();
		Map<Integer,List<SearchProteinCrosslinkWrapper>> wrappedCrosslinks_MappedOnSearchId = new HashMap<>();
		Map<Integer,List<SearchProteinLooplinkWrapper>> wrappedLooplinks_MappedOnSearchId = new HashMap<>();
		Map<Integer,List<SearchProteinMonolinkWrapper>> wrappedMonolinks_MappedOnSearchId = new HashMap<>();
		Set<Integer> proteinIds = new HashSet<>();
		{
			for ( SearchDTO search : searches ) {
				Integer projectSearchId = search.getProjectSearchId();
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
						searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
				if ( searcherCutoffValuesSearchLevel == null ) {
					String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				{
					List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
							CrosslinkLinkedPositions.getInstance()
							.getSearchProteinCrosslinkWrapperList( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
					wrappedCrosslinks_MappedOnSearchId.put( search.getSearchId(), wrappedCrosslinks );
					for ( SearchProteinCrosslinkWrapper wrappedCrosslink : wrappedCrosslinks ) {
						SearchProteinCrosslink crosslink = wrappedCrosslink.getSearchProteinCrosslink();
						proteinIds.add( crosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
						proteinIds.add( crosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
					}
				}
				{
					List<SearchProteinLooplinkWrapper> wrappedLooplinks = 
							LooplinkLinkedPositions.getInstance()
							.getSearchProteinLooplinkWrapperList( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
					wrappedLooplinks_MappedOnSearchId.put( search.getSearchId(), wrappedLooplinks );
					for ( SearchProteinLooplinkWrapper wrappedLooplink : wrappedLooplinks ) {
						SearchProteinLooplink looplink = wrappedLooplink.getSearchProteinLooplink();
						proteinIds.add( looplink.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
					}
				}
				{
					List<SearchProteinMonolinkWrapper> wrappedMonolinks = 
							MonolinkLinkedPositions.getInstance()
							.getSearchProteinMonolinkWrapperList( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
					wrappedMonolinks_MappedOnSearchId.put( search.getSearchId(), wrappedMonolinks );
					for ( SearchProteinMonolinkWrapper wrappedMonolink : wrappedMonolinks ) {
						SearchProteinMonolink link = wrappedMonolink.getSearchProteinMonolink();
						proteinIds.add( link.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
					}
				}
				{
					UnlinkedDimerPeptideProteinMappingResult unlinkedDimerPeptideProteinMappingResult =
							UnlinkedDimerPeptideProteinMapping.getInstance()
							.getSearchProteinUnlinkedAndDimerWrapperLists( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
					List<SearchProteinDimerWrapper> wrappedDimerLinks = 
							unlinkedDimerPeptideProteinMappingResult.getSearchProteinDimerWrapperList();
					for ( SearchProteinDimerWrapper wrappedDimer : wrappedDimerLinks ) {
						SearchProteinDimer dimer = wrappedDimer.getSearchProteinDimer();
						proteinIds.add( dimer.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
						proteinIds.add( dimer.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
					}
					List<SearchProteinUnlinkedWrapper> wrappedUnlinkedLinks = 
							unlinkedDimerPeptideProteinMappingResult.getSearchProteinUnlinkedWrapperList();
					for ( SearchProteinUnlinkedWrapper wrappedUnlinked : wrappedUnlinkedLinks ) {
						SearchProteinUnlinked unlinked = wrappedUnlinked.getSearchProteinUnlinked();
						proteinIds.add( unlinked.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() );
					}
				}
			}
			for ( int proteinId : proteinIds ) {
				proteins.add( new MergedSearchProtein( searches, ProteinSequenceVersionObjectFactory.getProteinSequenceVersionObject( proteinId ) ) );
			}
		}
		////////////////////////////////////////////////////////////////////////
		Map<Integer,List<SearchProteinCrosslinkWrapper>> wrappedCrosslinksFiltered_MappedOnSearchId = new HashMap<>();
		Map<Integer,List<SearchProteinLooplinkWrapper>> wrappedLooplinksFiltered_MappedOnSearchId = new HashMap<>();
		Map<Integer,List<SearchProteinMonolinkWrapper>> wrappedMonolinksFiltered_MappedOnSearchId = new HashMap<>();
		int totalLinkableResidues = 0;
		int totalLinkableResiduesCovered = 0;
		int totalMLCResidues = 0;			// total residues with a mono, loop, or cross-link
		int totalLCResidues = 0;			// total residues with a loop or cross-link
		int totalMonolinkResidues = 0;
		int totalLooplinkResidues = 0;
		int totalCrosslinkResidues = 0;
		int totalResidues = 0;
		int totalCoveredResidues = 0;
		// filter out links that only contain non unique peptides, if requested
		if( filterNonUniquePeptides || filterOnlyOnePSM || filterOnlyOnePeptide ) {
			// filter looplinks
			{
				for ( Map.Entry<Integer, List<SearchProteinLooplinkWrapper>> wrappedLooplinks_MappedOnSearchId_Entry :
					wrappedLooplinks_MappedOnSearchId.entrySet() ) {
					Integer searchId = wrappedLooplinks_MappedOnSearchId_Entry.getKey();
					List<SearchProteinLooplinkWrapper> wrappedLooplinks = wrappedLooplinks_MappedOnSearchId_Entry.getValue();
					List<SearchProteinLooplinkWrapper> wrappedLooplinks_Filtered = new ArrayList<>( wrappedLooplinks.size() );
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
//							int taxonomyId = link.getProtein().getProteinSequenceVersionObject().getTaxonomyId();
//							
//							if ( excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId ) ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did user request removal of certain protein IDs?
						if( ! excludeProtein_Ids_Set_UserInput.isEmpty() ) {
							int proteinId = link.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
							if ( excludeProtein_Ids_Set_UserInput.contains( proteinId ) ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}									
						// did they request to removal of non unique peptides?
						if( filterNonUniquePeptides  ) {
							if( link.getNumUniquePeptides() < 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with only one PSM?
						if( filterOnlyOnePSM  ) {
							int psmCountForSearchId = link.getNumPsms();
							if ( psmCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with only one Reported Peptide?
						if( filterOnlyOnePeptide ) {
							int peptideCountForSearchId = link.getNumPeptides();
							if ( peptideCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						wrappedLooplinks_Filtered.add( searchProteinLooplinkWrapper );
					}
					if ( ! wrappedLooplinks_Filtered.isEmpty() ) {
						wrappedLooplinksFiltered_MappedOnSearchId.put( searchId, wrappedLooplinks_Filtered );
					}
				}
			}
			// filter monolinks
			{
				for ( Map.Entry<Integer, List<SearchProteinMonolinkWrapper>> wrappedMonolinks_MappedOnSearchId_Entry :
					wrappedMonolinks_MappedOnSearchId.entrySet() ) {
					Integer searchId = wrappedMonolinks_MappedOnSearchId_Entry.getKey();
					List<SearchProteinMonolinkWrapper> wrappedMonolinks = wrappedMonolinks_MappedOnSearchId_Entry.getValue();
					List<SearchProteinMonolinkWrapper> wrappedMonolinks_Filtered = new ArrayList<>( wrappedMonolinks.size() );
					for ( SearchProteinMonolinkWrapper searchProteinMonolinkWrapper : wrappedMonolinks ) {
						SearchProteinMonolink link = searchProteinMonolinkWrapper.getSearchProteinMonolink();
						// did user request removal of certain taxonomy IDs?
						if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
							boolean excludeOnProtein =
									ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
									.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
											excludeTaxonomy_Ids_Set_UserInput, 
											link.getProtein().getProteinSequenceVersionObject(), 
											searchId );
							if ( excludeOnProtein ) {
//							int taxonomyId = link.getProtein().getProteinSequenceVersionObject().getTaxonomyId();
//							
//							if ( excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId ) ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did user request removal of certain protein IDs?
						if( ! excludeProtein_Ids_Set_UserInput.isEmpty() ) {
							int proteinId = link.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
							if ( excludeProtein_Ids_Set_UserInput.contains( proteinId ) ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}									
						// did they request to removal of non unique peptides?
						if( filterNonUniquePeptides  ) {
							if( link.getNumUniquePeptides() < 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with only one PSM?
						if( filterOnlyOnePSM  ) {
							int psmCountForSearchId = link.getNumPsms();
							if ( psmCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with only one Reported Peptide?
						if( filterOnlyOnePeptide ) {
							int peptideCountForSearchId = link.getNumPeptides();
							if ( peptideCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						wrappedMonolinks_Filtered.add( searchProteinMonolinkWrapper );
					}
					if ( ! wrappedMonolinks_Filtered.isEmpty() ) {
						wrappedMonolinksFiltered_MappedOnSearchId.put( searchId, wrappedMonolinks_Filtered );
					}
				}
			}
			// filter crosslinks
			{
				for ( Map.Entry<Integer, List<SearchProteinCrosslinkWrapper>> wrappedCrosslinks_MappedOnSearchId_Entry :
					wrappedCrosslinks_MappedOnSearchId.entrySet() ) {
					Integer searchId = wrappedCrosslinks_MappedOnSearchId_Entry.getKey();
					List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = wrappedCrosslinks_MappedOnSearchId_Entry.getValue();
					List<SearchProteinCrosslinkWrapper> wrappedCrosslinks_Filtered = new ArrayList<>( wrappedCrosslinks.size() );
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
//							int taxonomyId_1 = link.getProtein1().getProteinSequenceVersionObject().getTaxonomyId();
//							int taxonomyId_2 = link.getProtein2().getProteinSequenceVersionObject().getTaxonomyId();
//							
//							if ( excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId_1 ) 
//									|| excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId_2 ) ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did user request removal of certain protein IDs?
						if( ! excludeProtein_Ids_Set_UserInput.isEmpty() ) {
							int proteinId_1 = link.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
							int proteinId_2 = link.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
							if ( excludeProtein_Ids_Set_UserInput.contains( proteinId_1 ) 
									|| excludeProtein_Ids_Set_UserInput.contains( proteinId_2 ) ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}		
						// did they request to removal of non unique peptides?
						if( filterNonUniquePeptides  ) {
							if( link.getNumUniqueLinkedPeptides() < 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with only one PSM?
						if( filterOnlyOnePSM  ) {
							int psmCountForSearchId = link.getNumPsms();
							if ( psmCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with only one Reported Peptide?
						if( filterOnlyOnePeptide ) {
							int peptideCountForSearchId = link.getNumLinkedPeptides();
							if ( peptideCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}			
						wrappedCrosslinks_Filtered.add( searchProteinCrosslinkWrapper );
					}
					if ( ! wrappedCrosslinks_Filtered.isEmpty() ) {
						wrappedCrosslinksFiltered_MappedOnSearchId.put( searchId, wrappedCrosslinks_Filtered );
					}
				}
			}
		} else {
			//  No Filtering so just copy
			wrappedCrosslinksFiltered_MappedOnSearchId = wrappedCrosslinks_MappedOnSearchId;
			wrappedLooplinksFiltered_MappedOnSearchId = wrappedLooplinks_MappedOnSearchId;
			wrappedMonolinksFiltered_MappedOnSearchId = wrappedMonolinks_MappedOnSearchId;
		}
		//  Get the linker abbreviations for the searches
		Set<Integer> searchIds = new HashSet<>();
		for ( SearchDTO search : searches ) {
			int searchId = search.getSearchId();
			searchIds.add( searchId );
		}
		List<LinkerDTO>  linkerList = LinkersForSearchIdsSearcher.getInstance().getLinkersForSearchIds( searchIds );
		if ( linkerList == null || linkerList.isEmpty() ) {
			String errorMsgSearchIdList = null;
			for ( Integer searchId : searchIds ) {
				if ( errorMsgSearchIdList == null ) {
					errorMsgSearchIdList = searchId.toString();
				} else {
					errorMsgSearchIdList += "," + searchId.toString();
				}
			}
			String msg = "No linkers found for Search Ids: " + errorMsgSearchIdList;
			log.error( msg );
//			throw new Exception(msg);
		}
		Set<String> linkerAbbrSet = new HashSet<>();
		for ( LinkerDTO linker : linkerList ) {
			String linkerAbbr = linker.getAbbr();
			linkerAbbrSet.add( linkerAbbr );
		}
		/////////////////
		//   Get Sequence coverage for all proteins
		Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverage_KeyedOnProteinId_Map = new HashMap<>();
		//  Combine MergedSearchProtein with the same set of search ids
		Map<Set<Integer>, List<MergedSearchProtein>> mergedSearchProtein_KeyedOnSearchIds_Map = new HashMap<>();
		for( MergedSearchProtein protein : proteins ) {
			Set<Integer> searchIdsSet = new HashSet<>();
			for ( SearchDTO search : protein.getSearchs() ) {
				searchIdsSet.add( search.getSearchId() );
			}
			List<MergedSearchProtein> mergedSearchProteinList = mergedSearchProtein_KeyedOnSearchIds_Map.get( searchIdsSet );
			if ( mergedSearchProteinList == null ) {
				mergedSearchProteinList = new ArrayList<>();
				mergedSearchProtein_KeyedOnSearchIds_Map.put( searchIdsSet, mergedSearchProteinList );
			}
			mergedSearchProteinList.add( protein );
		}
		for ( Map.Entry<Set<Integer>, List<MergedSearchProtein>> mergedSearchProtein_KeyedOnSearchIds_Map_Entry :
			mergedSearchProtein_KeyedOnSearchIds_Map.entrySet() ) {
			List<MergedSearchProtein> mergedSearchProteinList = mergedSearchProtein_KeyedOnSearchIds_Map_Entry.getValue();
			List<ProteinSequenceVersionObject> proteinSequenceVersionObjectList = new ArrayList<>( mergedSearchProteinList.size() );
			MergedSearchProtein lastMergedSearchProtein = null;
			for ( MergedSearchProtein mergedSearchProtein : mergedSearchProteinList ) {
				lastMergedSearchProtein = mergedSearchProtein;
				proteinSequenceVersionObjectList.add( mergedSearchProtein.getProteinSequenceVersionObject() );
			}
			Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_Partial = 
					ProteinSequenceCoverageFactory.getInstance()
					.getProteinSequenceCoveragesForProteins(proteinSequenceVersionObjectList, lastMergedSearchProtein.getSearchs(), searcherCutoffValuesRootLevel);
			for ( Map.Entry<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_Partial_Entry : proteinSequenceCoverages_Partial.entrySet() ) {
				proteinSequenceCoverage_KeyedOnProteinId_Map.put( proteinSequenceCoverages_Partial_Entry.getKey(), proteinSequenceCoverages_Partial_Entry.getValue() );
			}
		}
		// for each protein calculate statistics
		for( MergedSearchProtein protein : proteins ) {
			// skip this protein if it is an excluded taxonomy
			if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
				boolean excludeForAllSearches = true;
				for ( SearchDTO searchDTO : protein.getSearchs() ) {
					//  Get all taxonomy ids for protein sequence id and search id
					TaxonomyIdsForProtSeqIdSearchId_Request taxonomyIdsForProtSeqIdSearchId_Request =
							new TaxonomyIdsForProtSeqIdSearchId_Request();
					taxonomyIdsForProtSeqIdSearchId_Request.setSearchId( searchDTO.getSearchId() );
					taxonomyIdsForProtSeqIdSearchId_Request.setProteinSequenceVersionId( protein.getProteinSequenceVersionObject().getProteinSequenceVersionId() );
					TaxonomyIdsForProtSeqIdSearchId_Result taxonomyIdsForProtSeqIdSearchId_Result =
							Cached_TaxonomyIdsFor_ProtSeqVersionId_SearchId.getInstance()
							.getTaxonomyIdsForProtSeqIdSearchId_Result( taxonomyIdsForProtSeqIdSearchId_Request );
					Set<Integer> taxonomyIds = taxonomyIdsForProtSeqIdSearchId_Result.getTaxonomyIds();
					for ( Integer taxonomyId : taxonomyIds ) {
						if( ! excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId ) ) { 
							excludeForAllSearches = false;
							break;
						}
					}
					if ( ! excludeForAllSearches ) {
						break;
					}
				}
				if ( excludeForAllSearches ) {
//				int taxonomyId = protein.getProteinSequenceVersionObject().getTaxonomyId();
//
//				if ( excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId ) ) {
					//  Skip to next entry in list, dropping this entry from output list
					continue;  // EARLY CONTINUE
				}
			}
			// did user request removal of certain protein IDs?
			if( ! excludeProtein_Ids_Set_UserInput.isEmpty() ) {
				int proteinId = protein.getProteinSequenceVersionObject().getProteinSequenceVersionId();
				if ( excludeProtein_Ids_Set_UserInput.contains( proteinId ) ) {
					//  Skip to next entry in list, dropping this entry from output list
					continue;  // EARLY CONTINUE
				}
			}
			ProteinSequenceVersionObject proteinSequenceVersionObject = protein.getProteinSequenceVersionObject();
			ProteinSequenceObject proteinSequenceObject = proteinSequenceVersionObject.getProteinSequenceObject();
			
			String proteinSequence = proteinSequenceObject.getSequence();
			int proteinSequenceLength = proteinSequence.length();
			
			ProteinCoverageData pcd = new ProteinCoverageData();
			pcd.setName( protein.getName() );
			pcd.setNumResidues( proteinSequenceLength );
			totalResidues += pcd.getNumResidues();
			//  report sequence coverage
			ProteinSequenceCoverage psc = proteinSequenceCoverage_KeyedOnProteinId_Map.get( protein.getProteinSequenceVersionObject().getProteinSequenceVersionId() );
			if ( psc == null ) {
				String msg = "proteinSequenceCoverage_KeyedOnProteinId_Map does not contain entry for protein id: " + protein.getProteinSequenceVersionObject();
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			pcd.setSequenceCoverage( psc.getSequenceCoverage() );
			totalCoveredResidues += (int)Math.round( pcd.getNumResidues() * psc.getSequenceCoverage() );
			Set<Integer> linkablePositionsForProtein = GetLinkablePositionsForLinkers.getLinkablePositionsForProteinSequenceAndLinkerAbbrSet( proteinSequence, linkerAbbrSet );
			// calculate and report number of modifiable residues
			int numLinkableResidues = linkablePositionsForProtein.size();
			totalLinkableResidues += numLinkableResidues;
			pcd.setNumLinkableResidues( numLinkableResidues );
			int numLinkableResiduesCovered = 0;
			Set<Range<Integer>> proteinSequenceCoverageRanges = psc.getRanges();
			for ( Integer linkablePosition : linkablePositionsForProtein ) {
				for ( Range<Integer> proteinSequenceCoverageRange : proteinSequenceCoverageRanges ) {
					if ( proteinSequenceCoverageRange.contains( linkablePosition ) ) {
						numLinkableResiduesCovered++;
					}
				}
			}
			totalLinkableResiduesCovered += numLinkableResiduesCovered;
			double linkableResiduesCoverage = 0;
			if ( numLinkableResidues != 0 ) {
				linkableResiduesCoverage = (double) numLinkableResiduesCovered / (double) numLinkableResidues;
			}
			pcd.setNumLinkableResiduesCovered( numLinkableResiduesCovered );
			pcd.setLinkableResiduesCoverage( linkableResiduesCoverage );
			// Set of distinct, linked positions
			Set<Integer> mlcResidues = new HashSet<Integer>();
			Set<Integer> lcResidues = new HashSet<Integer>();
			Set<Integer> monolinkedResidues = new HashSet<Integer>();
			for( Map.Entry<Integer,List<SearchProteinMonolinkWrapper>> wrappedMonolinksFiltered_MappedOnSearchId_Entry :
				wrappedMonolinksFiltered_MappedOnSearchId.entrySet() ) {
				List<SearchProteinMonolinkWrapper> wrappedMonolinksFiltered = wrappedMonolinksFiltered_MappedOnSearchId_Entry.getValue();
				for ( SearchProteinMonolinkWrapper searchProteinMonolinkWrapper : wrappedMonolinksFiltered ) {
					SearchProteinMonolink searchProteinMonolink = searchProteinMonolinkWrapper.getSearchProteinMonolink();
					if( searchProteinMonolink.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() != protein.getProteinSequenceVersionObject().getProteinSequenceVersionId() ) { 
						//  Protein id for this link is not the protein id being processed in this iteration through the proteins
						//     so skip to next link
						continue; 
					}
					monolinkedResidues.add( searchProteinMonolink.getProteinPosition() );
					mlcResidues.add( searchProteinMonolink.getProteinPosition() );
				}
			}
			Set<Integer> looplinkedResidues = new HashSet<Integer>();
			for( Map.Entry<Integer,List<SearchProteinLooplinkWrapper>> wrappedLooplinksFiltered_MappedOnSearchId_Entry :
				wrappedLooplinksFiltered_MappedOnSearchId.entrySet() ) {
				List<SearchProteinLooplinkWrapper> wrappedLooplinksFiltered = wrappedLooplinksFiltered_MappedOnSearchId_Entry.getValue();
				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinksFiltered ) {
					SearchProteinLooplink searchProteinLooplink = searchProteinLooplinkWrapper.getSearchProteinLooplink();
					if( searchProteinLooplink.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() != protein.getProteinSequenceVersionObject().getProteinSequenceVersionId() ) { 
						//  Protein id for this link is not the protein id being processed in this iteration through the proteins
						//     so skip to next link
						continue;
					}
					looplinkedResidues.add( searchProteinLooplink.getProteinPosition1() );
					looplinkedResidues.add( searchProteinLooplink.getProteinPosition2() );
					mlcResidues.add( searchProteinLooplink.getProteinPosition1() );
					mlcResidues.add( searchProteinLooplink.getProteinPosition2() );
					lcResidues.add( searchProteinLooplink.getProteinPosition1() );
					lcResidues.add( searchProteinLooplink.getProteinPosition2() );
				}
			}
			Set<Integer> crosslinkedResidues = new HashSet<Integer>();
			for( Map.Entry<Integer,List<SearchProteinCrosslinkWrapper>> wrappedCrosslinksFiltered_MappedOnSearchId_Entry :
				wrappedCrosslinksFiltered_MappedOnSearchId.entrySet() ) {
				List<SearchProteinCrosslinkWrapper> wrappedCrosslinksFiltered = wrappedCrosslinksFiltered_MappedOnSearchId_Entry.getValue();
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinksFiltered ) {
					SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					if( searchProteinCrosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() == protein.getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
						//  Protein id 1 for this link is the protein id being processed in this iteration through the proteins
						//     so process this link
						crosslinkedResidues.add( searchProteinCrosslink.getProtein1Position() );					
						mlcResidues.add( searchProteinCrosslink.getProtein1Position() );
						lcResidues.add( searchProteinCrosslink.getProtein1Position() );
					}
					if( searchProteinCrosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() == protein.getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
						//  Protein id 2 for this link is the protein id being processed in this iteration through the proteins
						//     so process this link
						crosslinkedResidues.add( searchProteinCrosslink.getProtein2Position() );					
						mlcResidues.add( searchProteinCrosslink.getProtein2Position() );
						lcResidues.add( searchProteinCrosslink.getProtein2Position() );
					}
				}
			}
			// # of linked residues
			pcd.setNumMLCResidues( mlcResidues.size() );
			pcd.setNuMLCResidues( lcResidues.size() );
			totalMLCResidues += mlcResidues.size();
			totalLCResidues += lcResidues.size();
			pcd.setMonolinkedResidues( monolinkedResidues.size() );
			totalMonolinkResidues += monolinkedResidues.size();
			pcd.setLooplinkedResidues( looplinkedResidues.size() );
			totalLooplinkResidues += looplinkedResidues.size();
			pcd.setCrosslinkedResidues( crosslinkedResidues.size() );
			totalCrosslinkResidues += crosslinkedResidues.size();
			proteinCoverageDataList.add( pcd );
		}
		//  Sort the list on protein name and then add the TOTAL line to the bottom
		Collections.sort( proteinCoverageDataList, new Comparator<ProteinCoverageData>() {
			@Override
			public int compare(ProteinCoverageData o1, ProteinCoverageData o2) {
				return o1.getName().compareToIgnoreCase( o2.getName() );
			}
		});
		{
			ProteinCoverageData pcd = new ProteinCoverageData();
			pcd.setName( "TOTAL" );
			pcd.setNumResidues( totalResidues );
			double sequenceCoverage = 0;
			if ( totalResidues != 0 ) {
				sequenceCoverage = (double)totalCoveredResidues / (double)totalResidues;
			}
			pcd.setSequenceCoverage( sequenceCoverage );
			pcd.setNumLinkableResidues( totalLinkableResidues );
			double totalLinkableResiduesCoverage = 0;
			if ( totalResidues != 0 ) {
				totalLinkableResiduesCoverage = (double)totalLinkableResiduesCovered / (double)totalLinkableResidues;
			}
			pcd.setNumLinkableResiduesCovered( totalLinkableResiduesCovered );
			pcd.setLinkableResiduesCoverage( totalLinkableResiduesCoverage );
			pcd.setNumMLCResidues( totalMLCResidues );
			pcd.setNuMLCResidues( totalLCResidues );
			pcd.setMonolinkedResidues( totalMonolinkResidues );
			pcd.setLooplinkedResidues( totalLooplinkResidues );
			pcd.setCrosslinkedResidues( totalCrosslinkResidues );
			proteinCoverageDataList.add( pcd );
		}
		return proteinCoverageDataList;
	}
	
	public boolean isFilterOnlyOnePSM() {
		return filterOnlyOnePSM;
	}
	public void setFilterOnlyOnePSM(boolean filterOnlyOnePSM) {
		this.filterOnlyOnePSM = filterOnlyOnePSM;
	}
	public boolean isFilterOnlyOnePeptide() {
		return filterOnlyOnePeptide;
	}
	public void setFilterOnlyOnePeptide(boolean filterOnlyOnePeptide) {
		this.filterOnlyOnePeptide = filterOnlyOnePeptide;
	}
	public SearcherCutoffValuesRootLevel getSearcherCutoffValuesRootLevel() {
		return searcherCutoffValuesRootLevel;
	}
	public void setSearcherCutoffValuesRootLevel(
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel) {
		this.searcherCutoffValuesRootLevel = searcherCutoffValuesRootLevel;
	}
	public Collection<SearchDTO> getSearches() {
		return searches;
	}
	public void setSearches(Collection<SearchDTO> searches) {
		this.searches = searches;
	}
	public boolean isFilterNonUniquePeptides() {
		return filterNonUniquePeptides;
	}
	public void setFilterNonUniquePeptides(boolean filterNonUniquePeptides) {
		this.filterNonUniquePeptides = filterNonUniquePeptides;
	}
	public int[] getExcludedproteinSequenceVersionIds() {
		return excludedproteinSequenceVersionIds;
	}
	public void setExcludedproteinSequenceVersionIds(int[] excludedproteinSequenceVersionIds) {
		this.excludedproteinSequenceVersionIds = excludedproteinSequenceVersionIds;
	}
	public int[] getExcludedTaxonomyIds() {
		return excludedTaxonomyIds;
	}
	public void setExcludedTaxonomyIds(int[] excludedTaxonomyIds) {
		this.excludedTaxonomyIds = excludedTaxonomyIds;
	}

	public LinkedPositions_FilterExcludeLinksWith_Param getLinkedPositions_FilterExcludeLinksWith_Param() {
		return linkedPositions_FilterExcludeLinksWith_Param;
	}

	public void setLinkedPositions_FilterExcludeLinksWith_Param(
			LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param) {
		this.linkedPositions_FilterExcludeLinksWith_Param = linkedPositions_FilterExcludeLinksWith_Param;
	}
}
