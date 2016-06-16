package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptNrseqIdPosMonolinkDTO;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;

/**
 * 
 *
 */
public class PopulateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject {

	private static final Logger log = Logger.getLogger(PopulateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject.class);

	private PopulateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject() { }
	public static PopulateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject getInstance() { return new PopulateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject(); }
	


	/**
	 * Update monolinkDTOList in perPeptideData object adding monolinks based on monolinkPositionList
	 * 
	 * @param perPeptideData
	 * @param linkerList
	 * @param proteinMatches_ForPeptide
	 * @throws Exception
	 */
	public void populateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject( 
			
			PerPeptideData perPeptideData, 
			
			List<ILinker> linkerList,
			Collection<NRProteinDTO> proteinMatches_ForPeptide ) throws Exception {
		
		
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		
		
		List<SrchRepPeptNrseqIdPosMonolinkDTO> monolinkDTOList = new ArrayList<>();
		perPeptideData.setSrchRepPeptNrseqIdPosMonolinkDTOList( monolinkDTOList );

		
		List<Integer> monolinkPositionList = perPeptideData.getMonolinkPositionList();
		
		for ( int monolinkPosition : monolinkPositionList ) {

			// get proteins and linkable positions in those proteins that are mapped to by the given peptides and positions
			Map<NRProteinDTO, Collection<Integer>> proteinMap = 
					GetLinkableProteinsAndPositions.getInstance()
					.getLinkableProteinsAndPositions( peptideDTO, monolinkPosition, linkerList, proteinMatches_ForPeptide );


			if( proteinMap.keySet().size() < 1 ) {
				String msg = "populateSrchRepPeptNrseqIdPosMonolinkDTOListOnPerPeptideDataObject(...): No linkable protein positions found for " + peptideDTO.getSequence() +
						" at position " + monolinkPosition + " for "
						+ " linker.";
				log.error( msg );

				throw new Exception( msg );
			}
			
			for ( Map.Entry<NRProteinDTO, Collection<Integer>> proteinPositionMapEntry : proteinMap.entrySet() ) {

				NRProteinDTO protein = proteinPositionMapEntry.getKey();
				
				Collection<Integer> proteinPositions = proteinPositionMapEntry.getValue();
				
				for ( int proteinPosition : proteinPositions ) {

					SrchRepPeptNrseqIdPosMonolinkDTO srchRepPeptNrseqIdPosMonolinkDTO = new SrchRepPeptNrseqIdPosMonolinkDTO();

					srchRepPeptNrseqIdPosMonolinkDTO.setPeptidePosition( monolinkPosition );

					srchRepPeptNrseqIdPosMonolinkDTO.setNrseqId( protein.getNrseqId() );
					srchRepPeptNrseqIdPosMonolinkDTO.setNrseqPosition( proteinPosition );

					monolinkDTOList.add(srchRepPeptNrseqIdPosMonolinkDTO);
				}
			}
		}
	}
	
}
