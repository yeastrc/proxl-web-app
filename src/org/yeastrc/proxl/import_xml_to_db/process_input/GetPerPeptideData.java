package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl_import.api.xml_dto.Modification;
import org.yeastrc.proxl_import.api.xml_dto.Modifications;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dto.DynamicModDTO;
import org.yeastrc.xlink.dto.MatchedPeptideDTO;
import org.yeastrc.xlink.dto.PeptideDTO;

/**
 * 
 *
 */
public class GetPerPeptideData {

	private static final Logger log = Logger.getLogger(GetPerPeptideData.class);

	private GetPerPeptideData() { }
	public static GetPerPeptideData getInstance() { return new GetPerPeptideData(); }
	

	/**
	 * @param peptide
	 * @return
	 * @throws Exception 
	 */
	public PerPeptideData getPerPeptideData( Peptide peptide, int nrseqDatabaseId ) throws Exception {
		
		PerPeptideData perPeptideData = new PerPeptideData();
		
		//  Add the peptide sequence if not in DB, otherwise retrieve existing record.
		PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTO( peptide.getSequence() );

		perPeptideData.setPeptideDTO( peptideDTO );
		
		MatchedPeptideDTO matchedPeptideDTO = new MatchedPeptideDTO();
		perPeptideData.setMatchedPeptideDTO( matchedPeptideDTO );
		matchedPeptideDTO.setPeptide_id( peptideDTO.getId() );
		
		
		Modifications modifications = peptide.getModifications();
		List<Modification> modificationList = modifications.getModification();
		

		List<DynamicModDTO> dynamicModDTOList_Peptide = new ArrayList<>();
		List<Integer> monolinkPositionList = new ArrayList<>();
		
		perPeptideData.setDynamicModDTOList_Peptide( dynamicModDTOList_Peptide );
		perPeptideData.setMonolinkPositionList( monolinkPositionList );
		
		for ( Modification modification : modificationList ) {
			
			int position = modification.getPosition().intValue();
			BigDecimal mass = modification.getMass();
			boolean monolink = modification.isIsMonolink();
			
			DynamicModDTO dynamicModDTO = new DynamicModDTO();
			dynamicModDTO.setPosition( position );
			dynamicModDTO.setMass( mass.doubleValue() );
			dynamicModDTOList_Peptide.add( dynamicModDTO );
			
			if ( monolink ) {
				
				monolinkPositionList.add( position );
			}
		}
		
		return perPeptideData;
	}
	
}
