package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.proxl.import_xml_to_db.utils.ProteinPositionUtils;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;

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
	 * @param peptidePosition
	 * @param linker
	 * @param peptideProteins
	 * @return
	 * @throws Exception
	 */
	public  Map<ProteinImporterContainer, Collection<Integer>> getLinkableProteinsAndPositions( 
			String peptideSequence, 
			int peptidePosition, 
			List<ILinker> linkerList, 
			Collection<ProteinImporterContainer> proteinImporterContainerCollection ) throws Exception {
		
		Map<ProteinImporterContainer, Collection<Integer>> results = new HashMap<>();
		
		for( ProteinImporterContainer proteinImporterContainer : proteinImporterContainerCollection ) {
			
			String proteinSequence = proteinImporterContainer.getProteinSequenceDTO().getSequence();
			
			
			List<Integer> linkedPositions = ProteinPositionUtils.getProteinPosition( proteinSequence, peptideSequence, peptidePosition );
			
			//  Get linkable positions for all the linkers
			
			Collection<Integer> linkablePositionsCollection = new HashSet<Integer>();
			
			for ( ILinker linker : linkerList ) {
			
				Collection<Integer> linkablePositionsCollectionForLinker = linker.getLinkablePositions( proteinSequence );
				
				linkablePositionsCollection.addAll( linkablePositionsCollectionForLinker );
			}
			
			List<Integer> sortedLinkablePositions = new ArrayList<Integer>();
			sortedLinkablePositions.addAll( linkablePositionsCollection );
			Collections.sort( sortedLinkablePositions );
			
			List<Integer> proteinPositionList = null;
			
			for( Integer linkedPosition : linkedPositions ) {
				
				if( Collections.binarySearch( sortedLinkablePositions, linkedPosition ) >= 0 ) {
					
					if ( proteinPositionList == null ) {
						
						//  First position found, create list and put in map
						
						proteinPositionList = new ArrayList<>();
						
						Collection<Integer> proteinPositionListPrev =
								results.put(proteinImporterContainer, proteinPositionList);
						
						if ( proteinPositionListPrev != null ) {
							
							String msg = "proteinImporterContainer already in map. protein sequence: "
									+ proteinImporterContainer.getProteinSequenceDTO().getSequence();
							log.error( msg );
							throw new ProxlImporterInteralException(msg);
						}
					}
					
					proteinPositionList.add( linkedPosition );
				} else {
					
					if ( log.isInfoEnabled() ) {

						String msg = " Skipping link for protein " + proteinImporterContainer.getProteinSequenceDTO().getSequence() 
								+ " for peptide " + peptideSequence 
								+ " at position " + peptidePosition 
								+ " because it was not a linkable residue in the protein at position "
								+ linkedPosition + ".";
						log.info( msg );
					}
				}
			}
		}
		
		return results;
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
	public  Map<ProteinImporterContainer, Collection<List<Integer>>> getLinkableProteinsAndPositionsForLooplink( 
			String peptideSequence, 
			int peptidePosition1, 
			int peptidePosition2, 
			List<ILinker> linkerList, 
			Collection<ProteinImporterContainer> proteinImporterContainerCollection ) throws Exception {

		Map<ProteinImporterContainer, Collection<List<Integer>>> results = new HashMap<>();
		
		for( ProteinImporterContainer proteinImporterContainer : proteinImporterContainerCollection ) {
			
			String proteinSequence = proteinImporterContainer.getProteinSequenceDTO().getSequence();
						
			List<List<Integer>> proteinPositions = 
					ProteinPositionUtils.getLooplinkProteinPosition( proteinSequence, peptideSequence, peptidePosition1, peptidePosition2 );
			
			//  Get linkable positions for all the linkers
			
			Collection<Integer> linkablePositionsCollection = new HashSet<Integer>();
			
			for ( ILinker linker : linkerList ) {
			
				Collection<Integer> linkablePositionsCollectionForLinker = linker.getLinkablePositions( proteinSequence );
				
				linkablePositionsCollection.addAll( linkablePositionsCollectionForLinker );
			}
			
			List<Integer> sortedLinkablePositions = new ArrayList<Integer>();
			sortedLinkablePositions.addAll( linkablePositionsCollection );
			Collections.sort( sortedLinkablePositions );
			

			List<List<Integer>> proteinPosition_1_2_List = null;
			
			for( List<Integer> linkedPositions_1_2 : proteinPositions ) {
			
				int linkedPosition1 = linkedPositions_1_2.get( 0 );
				int linkedPosition2 = linkedPositions_1_2.get( 1 );
				
				if( Collections.binarySearch( sortedLinkablePositions, linkedPosition1 ) >= 0 &&
						Collections.binarySearch( sortedLinkablePositions, linkedPosition2 ) >= 0	) {

					if ( proteinPosition_1_2_List == null ) {
						
						//  First position found, create list and put in map
						
						proteinPosition_1_2_List = new ArrayList<>();
						
						Object proteinPositionListPrev =
								results.put(proteinImporterContainer, proteinPosition_1_2_List);
						
						if ( proteinPositionListPrev != null ) {
							
							String msg = "proteinImporterContainer already in map. protein sequence: "
									+ proteinImporterContainer.getProteinSequenceDTO().getSequence();
							log.error( msg );
							throw new ProxlImporterInteralException(msg);
						}
					}
					
					List<Integer> proteinPositions_1_2_list = new ArrayList<Integer>( 2 );
					proteinPositions_1_2_list.add( linkedPosition1 );
					proteinPositions_1_2_list.add( linkedPosition2 );
					
					proteinPosition_1_2_List.add( proteinPositions_1_2_list );
					
				} else {
					String msg = "Warning: Skipping looplink for protein " + proteinImporterContainer.getProteinSequenceDTO().getSequence()
							+ " for peptide " + peptideSequence 
							+ " at positions " + peptidePosition1 + " and " + peptidePosition2 
							+ " because they were not linkable residues in the protein at positions "
							+ linkedPosition1 + " and " + linkedPosition2 + ".";
					log.warn( msg );
				}
			}
		}
		
		return results;
	}
}
