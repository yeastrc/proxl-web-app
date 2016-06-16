package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//import org.apache.log4j.Logger;



import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl_import.api.xml_dto.Modification;
import org.yeastrc.proxl_import.api.xml_dto.Modifications;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.dto.PeptideDTO;

/**
 * 
 *
 */
public class GetPerPeptideData {

//	private static final Logger log = Logger.getLogger(GetPerPeptideData.class);

	private GetPerPeptideData() { }
	public static GetPerPeptideData getInstance() { return new GetPerPeptideData(); }
	

	/**
	 * @param peptide
	 * @return
	 * @throws Exception 
	 */
	public PerPeptideData getPerPeptideData( Peptide peptide ) throws Exception {
		
		PerPeptideData perPeptideData = new PerPeptideData();
		
		//  Add the peptide sequence if not in DB, otherwise retrieve existing record.
		PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTO( peptide.getSequence() );

		perPeptideData.setPeptideDTO( peptideDTO );
		
		List<SrchRepPeptPeptDynamicModDTO> dynamicModDTOList_Peptide = new ArrayList<>();
		List<Integer> monolinkPositionList = new ArrayList<>();

		perPeptideData.setSrchRepPeptPeptDynamicModDTOList_Peptide( dynamicModDTOList_Peptide );
		perPeptideData.setMonolinkPositionList( monolinkPositionList );

		Modifications modifications = peptide.getModifications();
		
		if ( modifications != null ) {

			List<Modification> modificationList = modifications.getModification();

			if ( modificationList != null ) {

				for ( Modification modification : modificationList ) {

					int position = modification.getPosition().intValue();
					BigDecimal mass = modification.getMass();
					Boolean monolink = modification.isIsMonolink();

					SrchRepPeptPeptDynamicModDTO srchRepPeptPeptDynamicModDTO = new SrchRepPeptPeptDynamicModDTO();
					srchRepPeptPeptDynamicModDTO.setPosition( position );
					srchRepPeptPeptDynamicModDTO.setMass( mass.doubleValue() );
					dynamicModDTOList_Peptide.add( srchRepPeptPeptDynamicModDTO );

					if ( monolink != null && monolink ) {
						
						srchRepPeptPeptDynamicModDTO.setMonolink( true );

						monolinkPositionList.add( position );
					}
				}
			}
		}
		
		return perPeptideData;
	}
	
}
