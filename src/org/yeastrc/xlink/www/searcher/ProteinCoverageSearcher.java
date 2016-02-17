package org.yeastrc.xlink.www.searcher;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkablePositionsForLinkers;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinMonolink;
import org.yeastrc.xlink.www.objects.ProteinCoverageData;
import org.yeastrc.xlink.www.objects.ProteinSequenceCoverage;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinDimer;
import org.yeastrc.xlink.www.objects.SearchProteinDimerWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinked;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinkedWrapper;

import com.google.common.collect.Range;

public class ProteinCoverageSearcher {
	
	private static final Logger log = Logger.getLogger(ProteinCoverageSearcher.class);

	private Collection<SearchDTO> searches;
	
	private SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel;
	
	private boolean filterNonUniquePeptides = false;
	private boolean filterOnlyOnePSM = false;
	private boolean filterOnlyOnePeptide = false;


	private int[] excludedProteinIds;
	private int[] excludedTaxonomyIds;
	
	
	
	
	
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


	/**
	 * Get a list describing the coverage of each protein in the requested collection of searches, given the
	 * set properties.
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ProteinCoverageData> getProteinCoverageData() throws Exception {
		
		List<ProteinCoverageData> proteinCoverageDataList = new ArrayList<ProteinCoverageData>();
		
		
		
		Collection<MergedSearchProtein> proteins = new ArrayList<>();
		
		

		Set<Integer> proteinIds = new HashSet<>();
		
		{

			List<MergedSearchProteinCrosslink> crosslinksProteins = MergedSearchProteinCrosslinkSearcher.getInstance().search( searches, searcherCutoffValuesRootLevel );

			List<MergedSearchProteinLooplink> looplinksProteins = MergedSearchProteinLooplinkSearcher.getInstance().search( searches, searcherCutoffValuesRootLevel );

			for( MergedSearchProteinCrosslink item : crosslinksProteins ) {

				proteinIds.add( item.getProtein1().getNrProtein().getNrseqId() );

				proteinIds.add( item.getProtein2().getNrProtein().getNrseqId() );
			}

			for( MergedSearchProteinLooplink item : looplinksProteins ) {

				proteinIds.add( item.getProtein().getNrProtein().getNrseqId() );
			}

			for ( SearchDTO search : searches ) {

				{
					List<SearchProteinDimerWrapper> wrappedDimerLinks = 
							SearchProteinDimerSearcher.getInstance()
							.searchOnSearchIdandCutoffs( search, searcherCutoffValuesRootLevel.getPerSearchCutoffs( search.getId() ) );

					for ( SearchProteinDimerWrapper wrappedDimer : wrappedDimerLinks ) {

						SearchProteinDimer dimer = wrappedDimer.getSearchProteinDimer();

						proteinIds.add( dimer.getProtein1().getNrProtein().getNrseqId() );

						proteinIds.add( dimer.getProtein2().getNrProtein().getNrseqId() );
					}
				}

				{
					List<SearchProteinUnlinkedWrapper> wrappedUnlinkedLinks = 
							SearchProteinUnlinkedSearcher.getInstance()
							.searchOnSearchIdandCutoffs( search, searcherCutoffValuesRootLevel.getPerSearchCutoffs( search.getId() ) );

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
		
		
		List<MergedSearchProteinMonolink> monolinks = 
				MergedSearchProteinMonolinkSearcher.getInstance().search( searches, searcherCutoffValuesRootLevel );
		
		List<MergedSearchProteinLooplink> looplinks = 
				MergedSearchProteinLooplinkSearcher.getInstance().search( searches, searcherCutoffValuesRootLevel );
		
		List<MergedSearchProteinCrosslink> crosslinks = 
				MergedSearchProteinCrosslinkSearcher.getInstance().search( searches, searcherCutoffValuesRootLevel );
		
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
			
			// filter monolinks
			{
				List<MergedSearchProteinMonolink> monolinksCopy = new ArrayList<MergedSearchProteinMonolink>();
				monolinksCopy.addAll( monolinks );
				
				for( MergedSearchProteinMonolink link : monolinksCopy ) {
					
					if ( filterNonUniquePeptides ) {
						
						if( link.getNumUniquePeptides() < 1 ) {
							monolinks.remove( link );
						}
					
					}
//					
//						link.getNumPsms() <= 1 WILL NOT WORK if more than one search since it is across all searches
//					
//					// did they request to removal of links with only one PSM?
					if( filterOnlyOnePSM ) {
						
						boolean foundSearchWithMoreThanOnePSM = false;
						

						Map<SearchDTO, SearchProteinMonolink> searchMonolinks = link.getSearchProteinMonolinks();

						for ( Map.Entry<SearchDTO, SearchProteinMonolink> searchEntry : searchMonolinks.entrySet() ) {

							SearchProteinMonolink searchProteinMonolink = searchEntry.getValue();

							int psmCountForSearchId = searchProteinMonolink.getNumPsms();

							if ( psmCountForSearchId > 1 ) {

								foundSearchWithMoreThanOnePSM = true;
								break;
							}
						}
						
						if (  ! foundSearchWithMoreThanOnePSM ) {
							monolinks.remove( link );
							continue;
						}

					}
					
					
					// did they request to removal of links with only one Reported Peptide?
					if( filterOnlyOnePeptide ) {
						
						boolean foundSearchWithMoreThanOneReportedPeptide = false;

						Map<SearchDTO, SearchProteinMonolink> searchMonolinks = link.getSearchProteinMonolinks();

						for ( Map.Entry<SearchDTO, SearchProteinMonolink> searchEntry : searchMonolinks.entrySet() ) {

							SearchProteinMonolink searchProteinMonolink = searchEntry.getValue();

							int peptideCountForSearchId = searchProteinMonolink.getNumPeptides();

							if ( peptideCountForSearchId > 1 ) {

								foundSearchWithMoreThanOneReportedPeptide = true;
								break;
							}
						}
						
						if (  ! foundSearchWithMoreThanOneReportedPeptide ) {
							monolinks.remove( link );
							continue;
						}
					}
					
				}
				
			}

			// filter looplinks
			{
				List<MergedSearchProteinLooplink> looplinksCopy = new ArrayList<MergedSearchProteinLooplink>();
				looplinksCopy.addAll( looplinks );
				
				for( MergedSearchProteinLooplink link : looplinksCopy ) {
					
					if( link.getNumUniquePeptides() < 1 ) {
						looplinks.remove( link );
					}
					
//					
//					link.getNumPsms() <= 1 WILL NOT WORK if more than one search since it is across all searches
//				
//				// did they request to removal of links with only one PSM?
				if( filterOnlyOnePSM ) {
					
					boolean foundSearchWithMoreThanOnePSM = false;
					

					Map<SearchDTO, SearchProteinLooplink> searchLooplinks = link.getSearchProteinLooplinks();

					for ( Map.Entry<SearchDTO, SearchProteinLooplink> searchEntry : searchLooplinks.entrySet() ) {

						SearchProteinLooplink searchProteinLooplink = searchEntry.getValue();

						int psmCountForSearchId = searchProteinLooplink.getNumPsms();

						if ( psmCountForSearchId > 1 ) {

							foundSearchWithMoreThanOnePSM = true;
							break;
						}
					}
					
					if (  ! foundSearchWithMoreThanOnePSM ) {
						looplinks.remove( link );
						continue;
					}

				}
				
				
				// did they request to removal of links with only one Reported Peptide?
				if( filterOnlyOnePeptide ) {
					
					boolean foundSearchWithMoreThanOneReportedPeptide = false;

					Map<SearchDTO, SearchProteinLooplink> searchLooplinks = link.getSearchProteinLooplinks();

					for ( Map.Entry<SearchDTO, SearchProteinLooplink> searchEntry : searchLooplinks.entrySet() ) {

						SearchProteinLooplink searchProteinLooplink = searchEntry.getValue();

						int peptideCountForSearchId = searchProteinLooplink.getNumPeptides();

						if ( peptideCountForSearchId > 1 ) {

							foundSearchWithMoreThanOneReportedPeptide = true;
							break;
						}
					}
					
					if (  ! foundSearchWithMoreThanOneReportedPeptide ) {
						looplinks.remove( link );
						continue;
					}
				}
				

				}
			}
			
			// filter crosslinks
			{
				List<MergedSearchProteinCrosslink> crosslinksCopy = new ArrayList<MergedSearchProteinCrosslink>();
				crosslinksCopy.addAll( crosslinks );
				
				for( MergedSearchProteinCrosslink link : crosslinksCopy ) {
					
					if( link.getNumUniqueLinkedPeptides() < 1 ) {
						crosslinks.remove( link );
					}
					
//					
//						link.getNumPsms() <= 1 WILL NOT WORK if more than one search since it is across all searches
//					
//					// did they request to removal of links with only one PSM?
					if( filterOnlyOnePSM ) {
						
						boolean foundSearchWithMoreThanOnePSM = false;

						Map<SearchDTO, SearchProteinCrosslink> searchCrosslinks = link.getSearchProteinCrosslinks();

						for ( Map.Entry<SearchDTO, SearchProteinCrosslink> searchEntry : searchCrosslinks.entrySet() ) {

							SearchProteinCrosslink searchProteinCrosslink = searchEntry.getValue();

							int psmCountForSearchId = searchProteinCrosslink.getNumPsms();

							if ( psmCountForSearchId > 1 ) {

								foundSearchWithMoreThanOnePSM = true;
								break;
								
							} else {
								
								int z = 0;
							}
						}
						
						if (  ! foundSearchWithMoreThanOnePSM ) {
							crosslinks.remove( link );
							continue;
						}

					}
					
					// did they request to removal of links with only one Reported Peptide?
					if( filterOnlyOnePeptide ) {
						
						boolean foundSearchWithMoreThanOneReportedPeptide = false;

						Map<SearchDTO, SearchProteinCrosslink> searchCrosslinks = link.getSearchProteinCrosslinks();

						for ( Map.Entry<SearchDTO, SearchProteinCrosslink> searchEntry : searchCrosslinks.entrySet() ) {

							SearchProteinCrosslink searchProteinCrosslink = searchEntry.getValue();

							int peptideCountForSearchId = searchProteinCrosslink.getNumLinkedPeptides();

							if ( peptideCountForSearchId > 1 ) {

								foundSearchWithMoreThanOneReportedPeptide = true;
								break;
							}
						}
						
						if (  ! foundSearchWithMoreThanOneReportedPeptide ) {
							crosslinks.remove( link );
							continue;
						}
					}
					
				}
			}
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
		
		
		
		
		// for each protein calculate statistics
		for( MergedSearchProtein protein : proteins ) {
			
			// skip this protein if it is an excluded taxonomy
			if( excludedTaxonomyIds != null && excludedTaxonomyIds.length > 0 ) {
				boolean skipProtein = false;
				for( int tid : excludedTaxonomyIds ) {
					if( protein.getNrProtein().getTaxonomyId() == tid ) {
						skipProtein = true;
						break;
					}
				}
				
				if( skipProtein ) {
					continue;
				}
			}
			
			// skip this protein if it is an excluded protein
			if( excludedProteinIds != null && excludedProteinIds.length > 0 ) {
				boolean skipProtein = false;
				for( int pid : excludedProteinIds ) {
					if( protein.getNrProtein().getNrseqId() == pid ) {
						skipProtein = true;
						break;
					}
				}
				
				if( skipProtein ) {
					continue;
				}
			}
			
			ProteinCoverageData pcd = new ProteinCoverageData();
			
			pcd.setName( protein.getName() );
			pcd.setNumResidues( protein.getNrProtein().getSequence().length() );
			
			totalResidues += pcd.getNumResidues();
			
			// calculate and report sequence coverage
			ProteinSequenceCoverage psc = MergedSearchProteinSequenceCoverageSearcher.getInstance().getProteinSequenceCoverage(protein, searcherCutoffValuesRootLevel);
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
			
			for( MergedSearchProteinMonolink monolink : monolinks ) {
				
				
				if( monolink.getProtein().getNrProtein().getNrseqId() != protein.getNrProtein().getNrseqId() ) { 
				
					//  Protein id for this link is not the protein id being processed in this iteration through the proteins
					//     so skip to next link
					continue; 
				}
				
				monolinkedResidues.add( monolink.getProteinPosition() );
				mlcResidues.add( monolink.getProteinPosition() );
			}
			
			Set<Integer> looplinkedResidues = new HashSet<Integer>();
			
			for( MergedSearchProteinLooplink looplink : looplinks ) {

				
				if( looplink.getProtein().getNrProtein().getNrseqId() != protein.getNrProtein().getNrseqId() ) { 
					
					//  Protein id for this link is not the protein id being processed in this iteration through the proteins
					//     so skip to next link
					continue;
				}
				
				looplinkedResidues.add( looplink.getProteinPosition1() );
				looplinkedResidues.add( looplink.getProteinPosition2() );
				
				mlcResidues.add( looplink.getProteinPosition1() );
				mlcResidues.add( looplink.getProteinPosition2() );
				
				lcResidues.add( looplink.getProteinPosition1() );
				lcResidues.add( looplink.getProteinPosition2() );
			}
			
			
			Set<Integer> crosslinkedResidues = new HashSet<Integer>();
			
			for( MergedSearchProteinCrosslink crosslink : crosslinks ) {
				
				if( crosslink.getProtein1().getNrProtein().getNrseqId() == protein.getNrProtein().getNrseqId() ) {
					
					//  Protein id 1 for this link is the protein id being processed in this iteration through the proteins
					//     so process this link

					crosslinkedResidues.add( crosslink.getProtein1Position() );					
					mlcResidues.add( crosslink.getProtein1Position() );
					lcResidues.add( crosslink.getProtein1Position() );
				}
				
				if( crosslink.getProtein2().getNrProtein().getNrseqId() == protein.getNrProtein().getNrseqId() ) {

					//  Protein id 2 for this link is the protein id being processed in this iteration through the proteins
					//     so process this link

					crosslinkedResidues.add( crosslink.getProtein2Position() );					
					mlcResidues.add( crosslink.getProtein2Position() );
					lcResidues.add( crosslink.getProtein2Position() );
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
	
}
