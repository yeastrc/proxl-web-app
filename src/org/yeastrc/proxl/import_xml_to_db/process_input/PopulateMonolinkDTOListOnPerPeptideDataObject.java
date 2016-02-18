package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.xlink.dto.MonolinkDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.utils.IsDynamicModMassAMonolink;

/**
 * 
 *
 */
public class PopulateMonolinkDTOListOnPerPeptideDataObject {

	private static final Logger log = Logger.getLogger(PopulateMonolinkDTOListOnPerPeptideDataObject.class);

	private PopulateMonolinkDTOListOnPerPeptideDataObject() { }
	public static PopulateMonolinkDTOListOnPerPeptideDataObject getInstance() { return new PopulateMonolinkDTOListOnPerPeptideDataObject(); }
	


	/**
	 * Update monolinkDTOList in perPeptideData object adding monolinks based on monolinkPositionList
	 * 
	 * @param perPeptideData
	 * @param linkerList
	 * @param proteinMatches_ForPeptide
	 * @throws Exception
	 */
	public void populateMonolinkDTOListOnPerPeptideDataObject( 
			
			PerPeptideData perPeptideData, 
			
			List<ILinker> linkerList,
			Collection<NRProteinDTO> proteinMatches_ForPeptide ) throws Exception {
		
		
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		
		
		List<MonolinkDTO> monolinkDTOList = new ArrayList<>();
		perPeptideData.setMonolinkDTOList( monolinkDTOList );

		
		List<Integer> monolinkPositionList = perPeptideData.getMonolinkPositionList();
		
		for ( int monolinkPosition : monolinkPositionList ) {

			// get proteins and linkable positions in those proteins that are mapped to by the given peptides and positions
			Map<NRProteinDTO, Collection<Integer>> proteinMap = 
					GetLinkableProteinsAndPositions.getInstance()
					.getLinkableProteinsAndPositions( peptideDTO, monolinkPosition, linkerList, proteinMatches_ForPeptide );


			if( proteinMap.keySet().size() < 1 ) {
				String msg = "populateMonolinkDTOListOnPerPeptideDataObject(...): No linkable protein positions found for " + peptideDTO.getSequence() +
						" at position " + monolinkPosition + " for "
						+ " linker.";
				log.error( msg );

				throw new Exception( msg );
			}
			
			for ( Map.Entry<NRProteinDTO, Collection<Integer>> proteinPositionMapEntry : proteinMap.entrySet() ) {

				NRProteinDTO protein = proteinPositionMapEntry.getKey();
				
				Collection<Integer> proteinPositions = proteinPositionMapEntry.getValue();
				
				for ( int proteinPosition : proteinPositions ) {

					MonolinkDTO monolinkDTO = new MonolinkDTO();

					monolinkDTO.setPeptideId( peptideDTO.getId() );
					monolinkDTO.setPeptidePosition( monolinkPosition );

					monolinkDTO.setProtein( protein );
					monolinkDTO.setProteinPosition( proteinPosition );
					
					monolinkDTO.setLinkerId( IsDynamicModMassAMonolink.getInstance().getLinkerDTO().getId() );

					monolinkDTOList.add(monolinkDTO);
				}
			}
		}
	}
	
}
