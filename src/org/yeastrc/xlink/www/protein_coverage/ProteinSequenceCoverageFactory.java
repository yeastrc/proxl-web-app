package org.yeastrc.xlink.www.protein_coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.WebProteinPosition;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher.ReturnOnlyReportedPeptidesWithMonolinks;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class ProteinSequenceCoverageFactory {

	private static final Logger log = Logger.getLogger( ProteinSequenceCoverageFactory.class );
			
	private ProteinSequenceCoverageFactory() { }
	public static ProteinSequenceCoverageFactory getInstance() { return new ProteinSequenceCoverageFactory(); }
	
	/**
	 * Get the protein sequence coverage object for a single protein
	 * 
	 * @param protein
	 * @param searcherCutoffValuesRootLevel
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceCoverage getProteinSequenceCoverageForOneProteinForMultSearches( ProteinSequenceObject protein, Collection<SearchDTO> searches, SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel ) throws Exception {
		
		ProteinSequenceCoverage coverage = new ProteinSequenceCoverage( protein );
		
		for( SearchDTO searchDTO : searches ) {
			coverage.addSequenceCoverageObject(
					getProteinSequenceCoverageForProteinForOneSearch( 
							protein, 
							searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchDTO.getId() ), 
							searchDTO 
					) 
			 );
		}
		
		return coverage;
	}
	
	
	/**
	 * Get the protein sequence coverage object for single protein for a single search, given
	 * the supplied filter parameters
	 * 
	 * @param protein The protein
	 * @param searcherCutoffValuesRootLevel The filter parameters
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceCoverage getProteinSequenceCoverageForProteinForOneSearch( ProteinSequenceObject protein, SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel, SearchDTO searchDTO ) throws Exception {
		
		if ( searcherCutoffValuesSearchLevel == null ) {
			log.error( "Got null for search parameters." );
			throw new Exception( "Got null for search parameters." );
		}
		
		/*
		 * check the cache first
		 */
		ProteinSequenceCoverage coverage = ProteinSequenceCoverageCacheManager.getInstance().getProteinSequenceCoverageFromCache( protein.getProteinSequenceId(), searcherCutoffValuesSearchLevel );
		if( coverage != null )
			return coverage;

			
		/*
		 * if we get here, we couldn't get it from the cache
		 */
		
		coverage = new ProteinSequenceCoverage( protein );

		// get all peptides from the search, given the search parameters
		List<WebReportedPeptideWrapper> wrappedLinksPerForSearch = PeptideWebPageSearcher.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff( 
			searchDTO, 
			searcherCutoffValuesSearchLevel, 
			null /* linkTypesForDBQuery */, 
			null /* modsForDBQuery */, 
			ReturnOnlyReportedPeptidesWithMonolinks.NO );
		
		
		
		
		// iterate over those peptides and add to coverage object
		for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedLinksPerForSearch ) {
				
			WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
				
			PeptideDTO peptideDTO_1 = webReportedPeptide.getPeptide1();
			PeptideDTO peptideDTO_2 = webReportedPeptide.getPeptide2();
				
			if ( peptideDTO_1 != null ) {
				
				for( WebProteinPosition wpp : webReportedPeptide.getPeptide1ProteinPositions() ) {
					if( wpp.getProtein().getProteinSequenceObject().getProteinSequenceId() == protein.getProteinSequenceId() ) {
						coverage.addPeptide( peptideDTO_1.getSequence() );
						break;
					}
				}
				
			}

			if ( peptideDTO_2 != null ) {
				
				for( WebProteinPosition wpp : webReportedPeptide.getPeptide2ProteinPositions() ) {
					if( wpp.getProtein().getProteinSequenceObject().getProteinSequenceId() == protein.getProteinSequenceId() ) {
						coverage.addPeptide( peptideDTO_1.getSequence() );
						break;
					}
				}
				
			}

		}
		
		/*
		 * add coverage object to the cache
		 */
		ProteinSequenceCoverageCacheManager.getInstance().addProteinSequenceCoverageToCache( protein.getProteinSequenceId(), searcherCutoffValuesSearchLevel, coverage);
		
		return coverage;
	}
	
	

	/**
	 * Get the protein sequence coverage objects for multiple proteins
	 * 
	 * @param protein
	 * @param searcherCutoffValuesRootLevel
	 * @return A map, keyed on protein ID of the protein sequence coverage objects
	 * @throws Exception
	 */
	public Map<Integer, ProteinSequenceCoverage> getProteinSequenceCoveragesForProteins( List<ProteinSequenceObject> proteinList, Collection<SearchDTO> searches, SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel ) throws Exception {
		
		Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_KeyedOnProtId_Map = new HashMap<>();

		for ( ProteinSequenceObject protein : proteinList ) {

			ProteinSequenceCoverage proteinSequenceCoverage = new ProteinSequenceCoverage( protein );

			proteinSequenceCoverages_KeyedOnProtId_Map.put( protein.getProteinSequenceId(), proteinSequenceCoverage );
		}
		
		
		for ( SearchDTO searchDTO : searches ) {

			Integer searchId = searchDTO.getId();
			
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );
			
			if ( searcherCutoffValuesSearchLevel == null ) {
				
				searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
			}

			List<WebReportedPeptideWrapper> wrappedLinksPerForSearch =

					PeptideWebPageSearcher.getInstance()
					.searchOnSearchIdPsmCutoffPeptideCutoff( 
							searchDTO, 
							searcherCutoffValuesSearchLevel, 
							null /* linkTypesForDBQuery */, 
							null /* modsForDBQuery */, 
							ReturnOnlyReportedPeptidesWithMonolinks.NO );
			
			for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedLinksPerForSearch ) {
				
				WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
				
				PeptideDTO peptideDTO_1 = webReportedPeptide.getPeptide1();
				PeptideDTO peptideDTO_2 = webReportedPeptide.getPeptide2();
				
				if ( peptideDTO_1 != null ) {
					
					processPeptideAndItsProteins(peptideDTO_1, webReportedPeptide.getPeptide1ProteinPositions( ), proteinSequenceCoverages_KeyedOnProtId_Map );
				}

				if ( peptideDTO_2 != null ) {
					
					processPeptideAndItsProteins(peptideDTO_2, webReportedPeptide.getPeptide2ProteinPositions( ), proteinSequenceCoverages_KeyedOnProtId_Map );
				}
			}
			
		}
		
		return proteinSequenceCoverages_KeyedOnProtId_Map;
	}
	
	
	/**
	 * @param peptideDTO
	 * @param webProteinPositionList
	 * @param proteinSequenceCoverages_KeyedOnProtId_Map
	 * @return
	 * @throws Exception 
	 */
	private boolean processPeptideAndItsProteins( 
			
			PeptideDTO peptideDTO, 
			List<WebProteinPosition> webProteinPositionList,
			Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_KeyedOnProtId_Map
			) throws Exception {
		
		for ( WebProteinPosition webProteinPosition : webProteinPositionList ) {
			
			ProteinSequenceCoverage proteinSequenceCoverage = 
					proteinSequenceCoverages_KeyedOnProtId_Map.get( webProteinPosition.getProtein().getProteinSequenceObject().getProteinSequenceId() );
					
			if ( proteinSequenceCoverage != null ) {

				proteinSequenceCoverage.addPeptide( peptideDTO.getSequence() );
			}
		}
		
		return false;
	}
		
}
