package org.yeastrc.xlink.www.protein_coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.WebProteinPosition;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher.ReturnOnlyReportedPeptidesWithMonolinks;

/**
 * 
 *
 */
public class ProteinSequenceCoverageFactory {

	private static final Logger log = Logger.getLogger( ProteinSequenceCoverageFactory.class );
			
	private ProteinSequenceCoverageFactory() { }
	public static ProteinSequenceCoverageFactory getInstance() { return new ProteinSequenceCoverageFactory(); }
	
	/**
	 * @param protein
	 * @param searcherCutoffValuesRootLevel
	 * @return
	 * @throws Exception
	 */
	public ProteinSequenceCoverage getProteinSequenceCoverage( NRProteinDTO protein, Collection<SearchDTO> searches, SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel ) throws Exception {
		
		List<NRProteinDTO> proteinList = new ArrayList<>( 1 );
		
		proteinList.add(protein);
		
		
		Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages =
				getProteinSequenceCoveragesForProteins( proteinList, searches, searcherCutoffValuesRootLevel );
		
		ProteinSequenceCoverage proteinSequenceCoverage = proteinSequenceCoverages.get( protein.getNrseqId() );
		
		if ( proteinSequenceCoverage == null ) {
			
			String msg = "Internal Proxl Error, proteinSequenceCoverage == null for protein.getNrseqId(): " + protein.getNrseqId();
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}
		
		return proteinSequenceCoverage;
	}
	

	/**
	 * @param protein
	 * @param searcherCutoffValuesRootLevel
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, ProteinSequenceCoverage> getProteinSequenceCoveragesForProteins( List<NRProteinDTO> proteinList, Collection<SearchDTO> searches, SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel ) throws Exception {
		
		Map<Integer, ProteinSequenceCoverage> proteinSequenceCoverages_KeyedOnProtId_Map = new HashMap<>();

		for ( NRProteinDTO protein : proteinList ) {

			ProteinSequenceCoverage proteinSequenceCoverage = new ProteinSequenceCoverage( protein );

			proteinSequenceCoverages_KeyedOnProtId_Map.put( protein.getNrseqId(), proteinSequenceCoverage );
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
					proteinSequenceCoverages_KeyedOnProtId_Map.get( webProteinPosition.getProtein().getNrProtein().getNrseqId() );
					
			if ( proteinSequenceCoverage != null ) {

				proteinSequenceCoverage.addPeptide( peptideDTO.getSequence() );
			}
		}
		
		return false;
	}
		
}
