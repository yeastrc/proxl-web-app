package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * 
 *
 */
public class GetLinkableProteinsAndPositions {

	
	private static final Logger log = Logger.getLogger(GetLinkableProteinsAndPositions.class);
	
	
	
	//  private constructor
	private GetLinkableProteinsAndPositions() {  }
	
	public static GetLinkableProteinsAndPositions getInstance() { return new GetLinkableProteinsAndPositions(); }
	

	/**
	 * For Crosslinks and Monolinks:
	 * 
	 * Get a map of proteins and associated positions in those proteins that are mapped to by the supplied peptide and position, but
	 * only include positions that are linkable positions as defined by the supplied linker. Only proteins where linkable positions
	 * are found will be inclue (ie, no empty collections for protein keys)
	 * 
	 * @param peptide
	 * @param position
	 * @param linker
	 * @param peptideProteins
	 * @return
	 * @throws Exception
	 */
	public  Map<NRProteinDTO, Collection<Integer>> getLinkableProteinsAndPositions( 
			PeptideDTO peptide, 
			int position, 
			List<ILinker> linkerList, 
			Collection<NRProteinDTO> proteins ) throws Exception {
		
		Map<NRProteinDTO, Collection<Integer>> retMap = new HashMap<NRProteinDTO, Collection<Integer>>();
		
		for( NRProteinDTO protein : proteins ) {
			
			List<Integer> linkedPositions = XLinkUtils.getProteinPosition( protein, peptide, position );
			
			//  Get linkable positions for all the linkers
			
			Collection<Integer> linkablePositionsCollection = new HashSet<Integer>();
			
			for ( ILinker linker : linkerList ) {
			
				Collection<Integer> linkablePositionsCollectionForLinker = linker.getLinkablePositions( protein.getSequence() );
				
				linkablePositionsCollection.addAll( linkablePositionsCollectionForLinker );
			}
			
			List<Integer> sortedLinkablePositions = new ArrayList<Integer>();
			sortedLinkablePositions.addAll( linkablePositionsCollection );
			Collections.sort( sortedLinkablePositions );
			
			for( int i = 0; i < linkedPositions.size(); i++ ) {
				int linkedPosition = linkedPositions.get( i );
				
				if( Collections.binarySearch( sortedLinkablePositions, linkedPosition ) >= 0 ) {
					if( !retMap.containsKey( protein ) ) {
						retMap.put( protein, new ArrayList<Integer>() );
					}
					
					retMap.get( protein ).add( linkedPosition );
				} else {
					
					if ( log.isInfoEnabled() ) {

						String msg = " Skipping link for protein " + protein.getNrseqId() + " for peptide " + peptide.getSequence() + " at position " +
								position + " because it was not a linkable residue in the protein at position "+
								linkedPosition + ".";
						log.info( msg );
					}
				}
			}
		}
		
		return retMap;
	}

	

	/**
	 * Get a map of proteins and associated positions in those proteins that are mapped to by the supplied peptide and positions. Will only
	 * return proteins and positions for proteins where both positions are linkable according to the supplied linker. Only proteins
	 * with at least one valid pair of positions are included in the map.
	 * 
	 * @param peptide
	 * @param position1
	 * @param position2
	 * @param linker
	 * @param peptideProteins
	 * @return
	 * @throws Exception
	 */
	public Map<NRProteinDTO, Collection<List<Integer>>> getLinkableProteinsAndPositionsForLooplink( 
			
			PeptideDTO peptide, 
			int position1, 
			int position2, 
			List<ILinker> linkerList, 
			
			Collection<NRProteinDTO> proteins ) throws Exception {
		
		Map<NRProteinDTO, Collection<List<Integer>>> retMap = new HashMap<NRProteinDTO, Collection<List<Integer>>>();
		
		
		for( NRProteinDTO protein : proteins ) {

			List<List<Integer>> proteinPositions = XLinkUtils.getLooplinkProteinPosition( protein, peptide, position1, position2 );
			
			//  Get linkable positions for all the linkers
			
			Collection<Integer> linkablePositionsCollection = new HashSet<Integer>();
			
			for ( ILinker linker : linkerList ) {
			
				Collection<Integer> linkablePositionsCollectionForLinker = linker.getLinkablePositions( protein.getSequence() );
				
				linkablePositionsCollection.addAll( linkablePositionsCollectionForLinker );
			}
			
			List<Integer> sortedLinkablePositions = new ArrayList<Integer>();
			sortedLinkablePositions.addAll( linkablePositionsCollection );
			Collections.sort( sortedLinkablePositions );
			
			
			for( int i = 0; i < proteinPositions.size(); i++ ) {
				int linkedPosition1 = proteinPositions.get( i ).get( 0 );
				int linkedPosition2 = proteinPositions.get( i ).get( 1 );
				
				if( Collections.binarySearch( sortedLinkablePositions, linkedPosition1 ) >= 0 &&
						Collections.binarySearch( sortedLinkablePositions, linkedPosition2 ) >= 0	) {
					if( !retMap.containsKey( protein ) ) {
						retMap.put( protein, new ArrayList<List<Integer>>() );
					}
					
					List<Integer> pps = new ArrayList<Integer>( 2 );
					pps.add( linkedPosition1 );
					pps.add( linkedPosition2 );
					
					retMap.get( protein ).add( pps );
				} else {
					String msg = "Warning: Skipping looplink for protein " + protein.getNrseqId() + " for peptide " + peptide.getSequence() + " at positions " +
								position1 + " and " + position2 + " because they were not linkable residues in the protein at positions "+
								linkedPosition1 + " and " + linkedPosition2 + ".";
					log.warn( msg );
				}
			}
		}
		
		return retMap;
	}
}
