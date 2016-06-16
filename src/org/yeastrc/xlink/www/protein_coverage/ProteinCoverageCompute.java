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
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkablePositionsForLinkers;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.linked_positions.CrosslinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.MonolinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.UnlinkedDimerPeptideProteinMapping;
import org.yeastrc.xlink.www.linked_positions.LooplinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.UnlinkedDimerPeptideProteinMapping.UnlinkedDimerPeptideProteinMappingResult;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.ProteinCoverageData;
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


	private int[] excludedProteinIds;
	private int[] excludedTaxonomyIds;
	
	

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

		if ( excludedProteinIds != null ) {

			for ( Integer proteinId : excludedProteinIds ) {

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

				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
						searcherCutoffValuesRootLevel.getPerSearchCutoffs( search.getId() );
				
				if ( searcherCutoffValuesSearchLevel == null ) {
					
					searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
				}

				{
					List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
							CrosslinkLinkedPositions.getInstance()
							.getSearchProteinCrosslinkWrapperList( search, searcherCutoffValuesSearchLevel );
					
					wrappedCrosslinks_MappedOnSearchId.put( search.getId(), wrappedCrosslinks );

					for ( SearchProteinCrosslinkWrapper wrappedCrosslink : wrappedCrosslinks ) {

						SearchProteinCrosslink crosslink = wrappedCrosslink.getSearchProteinCrosslink();

						proteinIds.add( crosslink.getProtein1().getNrProtein().getNrseqId() );

						proteinIds.add( crosslink.getProtein2().getNrProtein().getNrseqId() );
					}
				}
				
				{
					List<SearchProteinLooplinkWrapper> wrappedLooplinks = 
							LooplinkLinkedPositions.getInstance()
							.getSearchProteinLooplinkWrapperList( search, searcherCutoffValuesSearchLevel );

					wrappedLooplinks_MappedOnSearchId.put( search.getId(), wrappedLooplinks );
					
					for ( SearchProteinLooplinkWrapper wrappedLooplink : wrappedLooplinks ) {

						SearchProteinLooplink looplink = wrappedLooplink.getSearchProteinLooplink();

						proteinIds.add( looplink.getProtein().getNrProtein().getNrseqId() );
					}
				}
				

				{
					List<SearchProteinMonolinkWrapper> wrappedMonolinks = 
							MonolinkLinkedPositions.getInstance()
							.getSearchProteinMonolinkWrapperList( search, searcherCutoffValuesSearchLevel );

					wrappedMonolinks_MappedOnSearchId.put( search.getId(), wrappedMonolinks );
					
					for ( SearchProteinMonolinkWrapper wrappedMonolink : wrappedMonolinks ) {

						SearchProteinMonolink link = wrappedMonolink.getSearchProteinMonolink();

						proteinIds.add( link.getProtein().getNrProtein().getNrseqId() );
					}
					
				}

				{
					
					UnlinkedDimerPeptideProteinMappingResult unlinkedDimerPeptideProteinMappingResult =
							UnlinkedDimerPeptideProteinMapping.getInstance()
							.getSearchProteinUnlinkedAndDimerWrapperLists( search, searcherCutoffValuesSearchLevel );

					List<SearchProteinDimerWrapper> wrappedDimerLinks = 
							unlinkedDimerPeptideProteinMappingResult.getSearchProteinDimerWrapperList();
					
					for ( SearchProteinDimerWrapper wrappedDimer : wrappedDimerLinks ) {

						SearchProteinDimer dimer = wrappedDimer.getSearchProteinDimer();

						proteinIds.add( dimer.getProtein1().getNrProtein().getNrseqId() );

						proteinIds.add( dimer.getProtein2().getNrProtein().getNrseqId() );
					}

					List<SearchProteinUnlinkedWrapper> wrappedUnlinkedLinks = 
							unlinkedDimerPeptideProteinMappingResult.getSearchProteinUnlinkedWrapperList();
					
					for ( SearchProteinUnlinkedWrapper wrappedUnlinked : wrappedUnlinkedLinks ) {

						SearchProteinUnlinked unlinked = wrappedUnlinked.getSearchProteinUnlinked();

						proteinIds.add( unlinked.getProtein().getNrProtein().getNrseqId() );
					}
				}
				
			}

			for ( int proteinId : proteinIds ) {

				proteins.add( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( proteinId ) ) );
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
			int searchId = search.getId();
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
				
				searchIdsSet.add( search.getId() );
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
			
			List<NRProteinDTO> nrProteinList = new ArrayList<>( mergedSearchProteinList.size() );
		
			MergedSearchProtein lastMergedSearchProtein = null;
			
			for ( MergedSearchProtein mergedSearchProtein : mergedSearchProteinList ) {
				
				lastMergedSearchProtein = mergedSearchProtein;
				
				nrProteinList.add( mergedSearchProtein.getNrProtein() );
			}
		
			Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_Partial = 
					ProteinSequenceCoverageFactory.getInstance()
					.getProteinSequenceCoveragesForProteins(nrProteinList, lastMergedSearchProtein.getSearchs(), searcherCutoffValuesRootLevel);

			for ( Map.Entry<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_Partial_Entry : proteinSequenceCoverages_Partial.entrySet() ) {
				
				proteinSequenceCoverage_KeyedOnProteinId_Map.put( proteinSequenceCoverages_Partial_Entry.getKey(), proteinSequenceCoverages_Partial_Entry.getValue() );
			}
		}
		
		
		
		// for each protein calculate statistics
		for( MergedSearchProtein protein : proteins ) {
			
			// skip this protein if it is an excluded taxonomy
			
			if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {

				int taxonomyId = protein.getNrProtein().getTaxonomyId();
				
				if ( excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId ) ) {

					//  Skip to next entry in list, dropping this entry from output list

					continue;  // EARLY CONTINUE
				}
			}

			// did user request removal of certain protein IDs?
			
			if( ! excludeProtein_Ids_Set_UserInput.isEmpty() ) {

				int proteinId = protein.getNrProtein().getNrseqId();
				
				if ( excludeProtein_Ids_Set_UserInput.contains( proteinId ) ) {

					//  Skip to next entry in list, dropping this entry from output list

					continue;  // EARLY CONTINUE
				}
			}									
			
			ProteinCoverageData pcd = new ProteinCoverageData();
			
			pcd.setName( protein.getName() );
			pcd.setNumResidues( protein.getNrProtein().getSequence().length() );
			
			totalResidues += pcd.getNumResidues();
			
			//  report sequence coverage
			ProteinSequenceCoverage psc = proteinSequenceCoverage_KeyedOnProteinId_Map.get( protein.getNrProtein().getNrseqId() );
			
			if ( psc == null ) {
				
				String msg = "proteinSequenceCoverage_KeyedOnProteinId_Map does not contain entry for protein id: " + protein.getNrProtein();
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			
			pcd.setSequenceCoverage( psc.getSequenceCoverage() );

			totalCoveredResidues += (int)Math.round( pcd.getNumResidues() * psc.getSequenceCoverage() );


			String proteinSequence = protein.getNrProtein().getSequence();

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
		

					if( searchProteinMonolink.getProtein().getNrProtein().getNrseqId() != protein.getNrProtein().getNrseqId() ) { 

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
					
					if( searchProteinLooplink.getProtein().getNrProtein().getNrseqId() != protein.getNrProtein().getNrseqId() ) { 

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

					if( searchProteinCrosslink.getProtein1().getNrProtein().getNrseqId() == protein.getNrProtein().getNrseqId() ) {

						//  Protein id 1 for this link is the protein id being processed in this iteration through the proteins
						//     so process this link

						crosslinkedResidues.add( searchProteinCrosslink.getProtein1Position() );					
						mlcResidues.add( searchProteinCrosslink.getProtein1Position() );
						lcResidues.add( searchProteinCrosslink.getProtein1Position() );
					}

					if( searchProteinCrosslink.getProtein2().getNrProtein().getNrseqId() == protein.getNrProtein().getNrseqId() ) {

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

	public int[] getExcludedProteinIds() {
		return excludedProteinIds;
	}

	public void setExcludedProteinIds(int[] excludedProteinIds) {
		this.excludedProteinIds = excludedProteinIds;
	}

	public int[] getExcludedTaxonomyIds() {
		return excludedTaxonomyIds;
	}

	public void setExcludedTaxonomyIds(int[] excludedTaxonomyIds) {
		this.excludedTaxonomyIds = excludedTaxonomyIds;
	}

}
