package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosMonolinkDTO;
import org.yeastrc.proxl.import_xml_to_db.objects.MonolinkContainer;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;

/**
 * 
 *
 */
public class PopulateSrchRepPeptProtSeqIdPosMonolinkDTOListOnPerPeptideDataObject {

	private static final Logger log = Logger.getLogger(PopulateSrchRepPeptProtSeqIdPosMonolinkDTOListOnPerPeptideDataObject.class);

	private PopulateSrchRepPeptProtSeqIdPosMonolinkDTOListOnPerPeptideDataObject() { }
	public static PopulateSrchRepPeptProtSeqIdPosMonolinkDTOListOnPerPeptideDataObject getInstance() { return new PopulateSrchRepPeptProtSeqIdPosMonolinkDTOListOnPerPeptideDataObject(); }
	


	/**
	 * Update monolinkDTOList in perPeptideData object adding monolinks based on monolinkPositionList
	 * 
	 * @param perPeptideData
	 * @param linkerList
	 * @param proteinMatches_ForPeptide
	 * @throws Exception
	 */
	public void populateSrchRepPeptProtSeqIdPosMonolinkDTOListOnPerPeptideDataObject( 
			
			PerPeptideData perPeptideData, 
			
			List<ILinker> linkerList,
			Collection<ProteinImporterContainer> proteinMatches_ForPeptide ) throws Exception {
		
		
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		
		
		List<MonolinkContainer> monolinkContainerList = new ArrayList<>();
		perPeptideData.setMonolinkContainerList( monolinkContainerList );

		
		List<Integer> monolinkPositionList = perPeptideData.getMonolinkPositionList();
		
		if ( monolinkPositionList.isEmpty() ) {
			
			//  Exit since no data to process
			
			return;  //  EARLY RETURN
		}
		
		for ( int monolinkPosition : monolinkPositionList ) {

			// get proteins and linkable positions in those proteins that are mapped to by the given peptides and positions
			Map<ProteinImporterContainer, Collection<Integer>> proteinMap = 
					GetLinkableProteinsAndPositions.getInstance()
					.getLinkableProteinsAndPositions( peptideDTO.getSequence(), monolinkPosition, linkerList, proteinMatches_ForPeptide );


			if( proteinMap.keySet().size() < 1 ) {
				String msg = "Could not map this peptide and link position to any protein in the Proxl XML file for " + peptideDTO.getSequence() +
						" at position " + monolinkPosition + " for "
						+ " linker.";
				log.error( "populateSrchRepPeptProtSeqIdPosMonolinkDTOListOnPerPeptideDataObject(...): " + msg );

				throw new ProxlImporterDataException( msg );
			}
			
			for ( Map.Entry<ProteinImporterContainer, Collection<Integer>> proteinPositionMapEntry : proteinMap.entrySet() ) {

				ProteinImporterContainer proteinImporterContainer = proteinPositionMapEntry.getKey();
				
				Collection<Integer> proteinPositions = proteinPositionMapEntry.getValue();
				
				for ( int proteinPosition : proteinPositions ) {

					SrchRepPeptProtSeqIdPosMonolinkDTO srchRepPeptProtSeqIdPosMonolinkDTO = new SrchRepPeptProtSeqIdPosMonolinkDTO();

					srchRepPeptProtSeqIdPosMonolinkDTO.setPeptidePosition( monolinkPosition );

					srchRepPeptProtSeqIdPosMonolinkDTO.setProteinSequencePosition( proteinPosition );
					
					MonolinkContainer monolinkContainer = new MonolinkContainer();
					
					monolinkContainer.setProteinImporterContainer( proteinImporterContainer );
					monolinkContainer.setSrchRepPeptProtSeqIdPosMonolinkDTO( srchRepPeptProtSeqIdPosMonolinkDTO );

					monolinkContainerList.add( monolinkContainer );
				}
			}
		}
		
		return;
	}
	
}
