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
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;
import org.yeastrc.xlink.www.searcher.PeptideProteinPositionsForCutoffsAndProtSeqIdsSearcher;

/**
 * 
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
			List<ProteinSequenceObject> proteinList, 
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
			Map<Integer, ProteinSequenceObject> proteinSequenceObjectsToComputeCoverageFor_KeyedOnProtSeqId = new HashMap<>();
			for ( ProteinSequenceObject protein : proteinList ) {
				Integer proteinSequenceId = protein.getProteinSequenceId();
				ProteinSequenceCoverage coverageInCache = null;
				//  First check if in cache and use that
				coverageInCache = 
						ProteinSequenceCoverageCachedData.getInstance()
						.getProteinSequenceCoverage( proteinSequenceId, searcherCutoffValuesSearchLevel );
				if( coverageInCache != null ) {
					proteinSequenceCoverages_KeyedOnProtId_Map.put( proteinSequenceId, coverageInCache );
				} else {
					//  Not in cache, add to collection of proteins to compute coverage
					proteinSequenceObjectsToComputeCoverageFor_KeyedOnProtSeqId.put( proteinSequenceId, protein );
				}
			}
			if ( ! proteinSequenceObjectsToComputeCoverageFor_KeyedOnProtSeqId.isEmpty() ) {
				Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages = 
						computeCoverageForProteinSequenceIdsForSearch( 
								proteinSequenceObjectsToComputeCoverageFor_KeyedOnProtSeqId, 
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
					Integer proteinSequenceId = entryPerProtSeqId.getKey();
					ProteinSequenceCoverage coveragePerSearchProtSeqId = entryPerProtSeqId.getValue();
					ProteinSequenceCoverage coverageResultProtSeqId = 
							proteinSequenceCoverages_KeyedOnProtId_Map_Result.get( proteinSequenceId );
					if ( coverageResultProtSeqId == null ) {
						proteinSequenceCoverages_KeyedOnProtId_Map_Result.put( proteinSequenceId, coveragePerSearchProtSeqId );
					} else {
						//  already have entry for proteinSequenceId so combine the coverage
						coverageResultProtSeqId.addSequenceCoverageObject(coveragePerSearchProtSeqId);
					}
				}
			}
		}
		return proteinSequenceCoverages_KeyedOnProtId_Map_Result;
	}
	
	/**
	 * @param proteinSequenceObjectsToComputeCoverageFor_KeyedOnProtSeqId
	 * @param searchDTO
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	private Map<Integer, ProteinSequenceCoverage> computeCoverageForProteinSequenceIdsForSearch(
			Map<Integer, ProteinSequenceObject> proteinSequenceObjectsToComputeCoverageFor_KeyedOnProtSeqId,
			SearchDTO searchDTO,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel
			) throws Exception {
		
		Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages = new HashMap<>();
		Set<Integer> proteinSequenceIdsToComputeCoverageFor = new HashSet<>();
		for ( Map.Entry<Integer, ProteinSequenceObject> entry : proteinSequenceObjectsToComputeCoverageFor_KeyedOnProtSeqId.entrySet() ) {
			proteinSequenceIdsToComputeCoverageFor.add( entry.getValue().getProteinSequenceId() );
			ProteinSequenceCoverage coverage = new ProteinSequenceCoverage( entry.getValue() );
			proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages.put( entry.getValue().getProteinSequenceId(), coverage );
		}
		List<PeptideProteinPositionsForCutoffsAndProtSeqIdsResultItem> peptideProteinPositionsList = 
				PeptideProteinPositionsForCutoffsAndProtSeqIdsSearcher.getInstance()
				.searchOnSearchIdPsmCutoffPeptideCutoff( proteinSequenceIdsToComputeCoverageFor, searchDTO, searcherCutoffValuesSearchLevel );
		for ( PeptideProteinPositionsForCutoffsAndProtSeqIdsResultItem peptideProteinPositionsItem : peptideProteinPositionsList ) {
			Integer proteinSequenceId = peptideProteinPositionsItem.getProteinSequenceId();
			ProteinSequenceCoverage coverage = proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages.get(proteinSequenceId);
			if ( coverage == null ) {
				String msg = "Internal Error: proteinSequenceCovarege not found in map for proteinSequenceId: " + proteinSequenceId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			coverage.addStartEndBoundary( peptideProteinPositionsItem.getProteinStartPosition(), peptideProteinPositionsItem.getProteinEndPosition() );
		}
		//  Add computed coverages to cache
//		ProteinSequenceCoverageCachedData proteinSequenceCoverageCachedData = ProteinSequenceCoverageCachedData.getInstance();
//		for ( Map.Entry<Integer, ProteinSequenceCoverage> entry : proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages.entrySet() ) {
//			Integer proteinSequenceId = entry.getKey();
//			ProteinSequenceCoverage coverage = entry.getValue(); 
//			/*
//			 * add coverage object to the cache
//			 */
//			ProteinSequenceCoverage coverageCopy = coverage.copy();
//			proteinSequenceCoverageCachedData
//			.addProteinSequenceCoverage( proteinSequenceId, searcherCutoffValuesSearchLevel, coverageCopy );
//		}

		return proteinSequenceCoverages_KeyedOnProtId_Map_ComputedCoverages;
	}
}
