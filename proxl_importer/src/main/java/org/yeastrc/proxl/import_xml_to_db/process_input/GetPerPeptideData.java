package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//import org.apache.log4j.Logger;



import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.utils.GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile;
import org.yeastrc.proxl_import.api.xml_dto.Modification;
import org.yeastrc.proxl_import.api.xml_dto.Modifications;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl.import_xml_to_db.dao.PeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptide_IsotopeLabel_DTO;
import org.yeastrc.xlink.base.constants.IsotopeLabelsConstants;
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
		PeptideDTO peptideDTO = 
				PeptideDAO.getInstance().getPeptideDTOFromSequenceInsertIfNotInTable( peptide.getSequence() );

		perPeptideData.setPeptideDTO( peptideDTO );
		
		perPeptideData.setUniqueId( peptide.getUniqueId() );
		

		Modifications modifications = peptide.getModifications();
		
		if ( modifications != null ) {

			List<Modification> modificationList = modifications.getModification();

			if ( modificationList != null && ( ! modificationList.isEmpty() ) ) {

				List<SrchRepPeptPeptDynamicModDTO> dynamicModDTOList_Peptide = new ArrayList<>( modificationList.size() );
				List<Integer> monolinkPositionList = new ArrayList<>( modificationList.size() );

				perPeptideData.setSrchRepPeptPeptDynamicModDTOList_Peptide( dynamicModDTOList_Peptide );
				perPeptideData.setMonolinkPositionList( monolinkPositionList );

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
		
		GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile.GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile_Result result =
				GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile.getInstance()
				.getIsotopeLabelIdFor_Peptide_FromProxlXMLFile( peptide );

		int peptide_IsotopeLabelId = result.getIsotopeLabelId();

		if ( peptide_IsotopeLabelId != IsotopeLabelsConstants.ID_NONE ) {
			SrchRepPeptPeptide_IsotopeLabel_DTO srchRepPeptPeptide_IsotopeLabel_DTO = new SrchRepPeptPeptide_IsotopeLabel_DTO();
			srchRepPeptPeptide_IsotopeLabel_DTO.setIsotopeLabelId( peptide_IsotopeLabelId );
			
			List<SrchRepPeptPeptide_IsotopeLabel_DTO> srchRepPeptPeptide_IsotopeLabel_DTOList_Peptide = new ArrayList<>( 1 );
			srchRepPeptPeptide_IsotopeLabel_DTOList_Peptide.add( srchRepPeptPeptide_IsotopeLabel_DTO );
		
			perPeptideData.setSrchRepPeptPeptide_IsotopeLabel_DTOList_Peptide( srchRepPeptPeptide_IsotopeLabel_DTOList_Peptide );
		}
		
		return perPeptideData;
	}
	
}
