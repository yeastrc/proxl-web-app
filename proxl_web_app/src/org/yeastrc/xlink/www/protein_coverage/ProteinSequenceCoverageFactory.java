package org.yeastrc.xlink.www.protein_coverage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.searcher.PeptideProteinPositionsForCutoffsAndProtSeqIdsSearcher;

/**
 * !!!!!!!!!!!!!   Commented out Caching of coverage since combining coverage in this code
 *
 */
public class ProteinSequenceCoverageFactory {
	
	private static final Logger log = Logger.getLogger( ProteinSequenceCoverageFactory.class );
	
	private ProteinSequenceCoverageFactory() { }
	public static ProteinSequenceCoverageFactory getInstance() { return new ProteinSequenceCoverageFactory(); }
	
	/**
	 * Get the protein sequence coverage objects per protein sequence id
	 * 
	 * @param protein
	 * @param searcherCutoffValuesRootLevel
	 * @return A map, keyed on protein ID of the protein sequence coverage objects
	 * @throws Exception
	 */
	public Map<Integer, ProteinSequenceCoverage> getProteinSequenceCoveragesForProteins( 
			List<ProteinSequenceVersionObject> proteinList, 
			Collection<SearchDTO> searches,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel ) throws Exception {
		
		//  First Build a map of coverages on Search Id, Protein Sequence Id
		Map<Integer, Map<Integer, ProteinSequenceCoverage>> proteinSequenceCoverages_KeyedOn_SeachId_ProtId_Map = new HashMap<>();
		//  First: Process everything per search
		for ( SearchDTO searchDTO : searches ) {
			//  For each search, process the Protein Sequence Ids to get data from cache and compute entries not in cache 
			Integer projectSearchId = searchDTO.getProjectSearchId();
			Integer searchId = searchDTO.getSearchId();
			Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_KeyedOnProtId_Map = new HashMap<>();
			proteinSequenceCoverages_KeyedOn_SeachId_ProtId_Map.put( searchId, proteinSequenceCoverages_KeyedOnProtId_Map );
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
			if ( searcherCutoffValuesSearchLevel == null ) {
				String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			//  Collection of protein sequence ids we need to compute coverage for
			Map<Integer, ProteinSequenceVersionObject> proteinSequenceVersionObjectsToComputeCoverageFor_KeyedOnProtSeqId = new HashMap<>();
			for ( ProteinSequenceVersionObject protein : proteinList ) {
				Integer proteinSequenceVersionId = protein.getProteinSequenceVersionId();

				
				//  !!!!!!!!!!!!!   Commented out Caching of coverage since combining coverage in this code
				
//				ProteinSequenceCoverage coverageInCache = null;
				
				//  First check if in cache and use that
//				coverageInCache = 
//						ProteinSequenceCoverageCachedData.getInstance()
//						.getProteinSequenceCoverage( proteinSequenceVersionId, searcherCutoffValuesSearchLevel );
				
				
//				if( coverageInCache != null ) {
//					proteinSequenceCoverages_KeyedOnProtId_Map.put( proteinSequenceVersionId, coverageInCache );
//				} else {
					//  Not in cache, add to collection of proteins to compute coverage
				
					proteinSequenceVersionObjectsToComputeCoverageFor_KeyedOnProtSeqId.put( proteinSequenceVersionId, protein );
					
//				}
			}
			if ( ! proteinSequenceVersionObjectsToComputeCoverageFor_KeyedOnProtSeqId.isEmpty() ) {
				Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages = 
						computeCoverageForproteinSequenceVersionIdsForSearch( 
								proteinSequenceVersionObjectsToComputeCoverageFor_KeyedOnProtSeqId, 
								searchDTO, 
								searcherCutoffValuesSearchLevel );
				proteinSequenceCoverages_KeyedOnProtId_Map.putAll( proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages );
			}
		}
		//  Create output map per protein sequence id
		Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_KeyedOnProtId_Map_Result = null;
		if ( proteinSequenceCoverages_KeyedOn_SeachId_ProtId_Map.size() == 1 ) {
			//  Only one search so copy to output
			Map.Entry<Integer, Map<Integer, ProteinSequenceCoverage>> entry = 
					proteinSequenceCoverages_KeyedOn_SeachId_ProtId_Map.entrySet().iterator().next();
			proteinSequenceCoverages_KeyedOnProtId_Map_Result = entry.getValue();
		} else {
			//  More than one search
			//  combine coverages per protein sequence id for output from the per search data
			proteinSequenceCoverages_KeyedOnProtId_Map_Result = new HashMap<>();
			for ( Map.Entry<Integer, Map<Integer, ProteinSequenceCoverage>> entryPerSearch :
					proteinSequenceCoverages_KeyedOn_SeachId_ProtId_Map.entrySet() ) {
				Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_KeyedOnProtId_Map = entryPerSearch.getValue();
				for ( Map.Entry<Integer, ProteinSequenceCoverage> entryPerProtSeqId : proteinSequenceCoverages_KeyedOnProtId_Map.entrySet() ) {
					Integer proteinSequenceVersionId = entryPerProtSeqId.getKey();
					ProteinSequenceCoverage coveragePerSearchProtSeqId = entryPerProtSeqId.getValue();
					ProteinSequenceCoverage coverageResultProtSeqId = 
							proteinSequenceCoverages_KeyedOnProtId_Map_Result.get( proteinSequenceVersionId );
					if ( coverageResultProtSeqId == null ) {
						proteinSequenceCoverages_KeyedOnProtId_Map_Result.put( proteinSequenceVersionId, coveragePerSearchProtSeqId );
					} else {
						
						//  !!!!!!!!!!!   ERROR  Cannot just combine coverage since the coverage is cached.  
						//                Always need to create a new coverage object for each protein sequence id
						
						//  already have entry for proteinSequenceVersionId so combine the coverage
						coverageResultProtSeqId.addSequenceCoverageObject(coveragePerSearchProtSeqId);
					}
				}
			}
		}
		return proteinSequenceCoverages_KeyedOnProtId_Map_Result;
	}
	
	/**
	 * @param proteinSequenceVersionObjectsToComputeCoverageFor_KeyedOnProtSeqId
	 * @param searchDTO
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, ProteinSequenceCoverage> computeCoverageForproteinSequenceVersionIdsForSearch(
			Map<Integer, ProteinSequenceVersionObject> proteinSequenceVersionObjectsToComputeCoverageFor_KeyedOnProtSeqId,
			SearchDTO searchDTO,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel
			) throws Exception {
		
		Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages = new HashMap<>();
		Set<Integer> proteinSequenceVersionIdsToComputeCoverageFor = new HashSet<>();
		for ( Map.Entry<Integer, ProteinSequenceVersionObject> entry : proteinSequenceVersionObjectsToComputeCoverageFor_KeyedOnProtSeqId.entrySet() ) {
			proteinSequenceVersionIdsToComputeCoverageFor.add( entry.getValue().getProteinSequenceVersionId() );
			ProteinSequenceCoverage coverage = new ProteinSequenceCoverage( entry.getValue() );
			proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages.put( entry.getValue().getProteinSequenceVersionId(), coverage );
		}
		List<ProteinCoverageForCutoffsAndProtSeqIdsResultItem> peptideProteinPositionsList = 
				PeptideProteinPositionsForCutoffsAndProtSeqIdsSearcher.getInstance()
				.searchOnSearchIdPsmCutoffPeptideCutoff( proteinSequenceVersionIdsToComputeCoverageFor, searchDTO, searcherCutoffValuesSearchLevel );
		for ( ProteinCoverageForCutoffsAndProtSeqIdsResultItem peptideProteinPositionsItem : peptideProteinPositionsList ) {
			Integer proteinSequenceVersionId = peptideProteinPositionsItem.getProteinSequenceVersionId();
			ProteinSequenceCoverage coverage = proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages.get(proteinSequenceVersionId);
			if ( coverage == null ) {
				String msg = "Internal Error: proteinSequenceCovarege not found in map for proteinSequenceVersionId: " + proteinSequenceVersionId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			coverage.addStartEndBoundary( peptideProteinPositionsItem.getProteinStartPosition(), peptideProteinPositionsItem.getProteinEndPosition() );
		}
		//  Add computed coverages to cache
		// First make copy of data inside synchronized to ensure written to main memory before add to cache
//		List<ProteinSequenceCoverageCacheAddEntry> proteinSequenceCoverageCacheAddEntryList = new ArrayList<>( proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages.size() );
//		synchronized ( this ) {
//			for ( Map.Entry<Integer, ProteinSequenceCoverage> entry : proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages.entrySet() ) {
//				Integer proteinSequenceVersionId = entry.getKey();
//				ProteinSequenceCoverage coverage = entry.getValue(); 
//				ProteinSequenceCoverage coverageCopy = coverage.copy();
//				ProteinSequenceCoverageCacheAddEntry psccae = new ProteinSequenceCoverageCacheAddEntry();
//				psccae.proteinSequenceVersionId = proteinSequenceVersionId;
//				psccae.searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel;
//				psccae.coverage = coverageCopy;
//				proteinSequenceCoverageCacheAddEntryList.add( psccae );
//			}
//		}
		

		//  !!!!!!!!!!!!!   Commented out Caching of coverage since combining coverage in this code
		
//		ProteinSequenceCoverageCachedData proteinSequenceCoverageCachedData = ProteinSequenceCoverageCachedData.getInstance();
//		for ( ProteinSequenceCoverageCacheAddEntry entry : proteinSequenceCoverageCacheAddEntryList ) {
//			/*
//			 * add coverage object to the cache
//			 */
//			proteinSequenceCoverageCachedData
//			.addProteinSequenceCoverage( entry.proteinSequenceVersionId, entry.searcherCutoffValuesSearchLevel, entry.coverage );
//		}


		return proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages;
	}
	

//	/**
//	 * 
//	 * Cache Entry to add
//	 */
//	private class ProteinSequenceCoverageCacheAddEntry {
//		
//		int proteinSequenceVersionId;
//		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel;
//		ProteinSequenceCoverage coverage;
//	}
}
