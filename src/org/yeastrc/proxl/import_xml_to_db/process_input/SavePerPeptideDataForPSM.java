package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_MonolinkDAO;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.xlink.dao.DynamicModDAO;
import org.yeastrc.xlink.dao.MatchedPeptideDAO;
import org.yeastrc.xlink.dto.DynamicModDTO;
import org.yeastrc.xlink.dto.MatchedPeptideDTO;
import org.yeastrc.xlink.dto.MonolinkDTO;
import org.yeastrc.xlink.dto.PsmDTO;

/**
 * Save Per Peptide data for the PSM.  ie: Matched Peptide, Dynamic Mods, and Monolinks
 *
 */
public class SavePerPeptideDataForPSM {


	private static final Logger log = Logger.getLogger( SavePerPeptideDataForPSM.class );
	
	/**
	 * private constructor
	 */
	private SavePerPeptideDataForPSM(){}
	
	public static SavePerPeptideDataForPSM getInstance() {
		
		return new SavePerPeptideDataForPSM();
	}


	
	/**
	 * @param psmDTO
	 * @param perPeptideData
	 * @throws Exception
	 */
	public void savePerPeptideDataForPSM( PsmDTO psmDTO, PerPeptideData perPeptideData ) throws Exception {
		

		MatchedPeptideDTO matchedPeptideDTO = perPeptideData.getMatchedPeptideDTO();
		
		List<DynamicModDTO> dynamicModDTOList_Peptide = perPeptideData.getDynamicModDTOList_Peptide();
		
		List<MonolinkDTO> monolinkDTOList = perPeptideData.getMonolinkDTOList();

		matchedPeptideDTO.setPsm_id( psmDTO.getId() );
		
		MatchedPeptideDAO.getInstance().save( matchedPeptideDTO );
		
		
		
		for ( DynamicModDTO dynamicModDTO : dynamicModDTOList_Peptide ) {
			
			dynamicModDTO.setMatched_peptide_id( matchedPeptideDTO.getId() );
			
			DynamicModDAO.getInstance().save( dynamicModDTO );
		}
		

		for ( MonolinkDTO monolinkDTO : monolinkDTOList ) {
			
			monolinkDTO.setPsm( psmDTO );
			
			DB_Insert_MonolinkDAO.getInstance().save( monolinkDTO );
		}
		
		
		
	}
	
		
}
