package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;import org.slf4j.Logger;



import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.objects.MonolinkDataFromModificationContainer;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.utils.GetIsotopeLabelIdFor_Protein_or_Peptide_FromProxlXMLFile;
import org.yeastrc.proxl_import.api.xml_dto.Modification;
import org.yeastrc.proxl_import.api.xml_dto.Modifications;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl.import_xml_to_db.dao.PeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptide_IsotopeLabel_DTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.xlink.base.constants.IsotopeLabelsConstants;
import org.yeastrc.xlink.dto.PeptideDTO;

/**
 * 
 *
 */
public class GetPerPeptideData {

	private static final Logger log = LoggerFactory.getLogger( GetPerPeptideData.class);

	private GetPerPeptideData() { }
	public static GetPerPeptideData getInstance() { return new GetPerPeptideData(); }
	

	/**
	 * @param peptide
	 * @return
	 * @throws Exception 
	 */
	public PerPeptideData getPerPeptideData( Peptide peptide ) throws Exception {
		
		final String peptideSequenceString = peptide.getSequence();
		
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

				List<MonolinkDataFromModificationContainer> monolinkDataFromModificationContainerList = new ArrayList<>( modificationList.size() );
				
				perPeptideData.setSrchRepPeptPeptDynamicModDTOList_Peptide( dynamicModDTOList_Peptide );
				perPeptideData.setMonolinkDataFromModificationContainerList( monolinkDataFromModificationContainerList );

				for ( Modification modification : modificationList ) {

					BigInteger position = modification.getPosition();
					BigDecimal mass = modification.getMass();
					Boolean monolink = modification.isIsMonolink();
					Boolean is_N_Terminal = modification.isIsNTerminal();
					Boolean is_C_Terminal = modification.isIsCTerminal();
					
					final int DTO_POSITION_INITIAL_VALUE = -1;
					SrchRepPeptPeptDynamicModDTO srchRepPeptPeptDynamicModDTO = new SrchRepPeptPeptDynamicModDTO();

					srchRepPeptPeptDynamicModDTO.setPosition( DTO_POSITION_INITIAL_VALUE );
					
					if ( position != null ) {
						srchRepPeptPeptDynamicModDTO.setPosition( position.intValue() );
					}
					
					//   For Database, set position to first or last position of peptide if N or C terminus is set
					if ( is_N_Terminal != null && is_N_Terminal.booleanValue() ) {
						srchRepPeptPeptDynamicModDTO.setIs_N_Terminal(true);
						srchRepPeptPeptDynamicModDTO.setPosition( 1 );
					}
					if ( is_C_Terminal != null && is_C_Terminal.booleanValue() ) {
						srchRepPeptPeptDynamicModDTO.setIs_C_Terminal(true);
						srchRepPeptPeptDynamicModDTO.setPosition( peptideSequenceString.length() );
					}
					
					if (  srchRepPeptPeptDynamicModDTO.getPosition() == DTO_POSITION_INITIAL_VALUE ) {
						String msg = "ERROR: getPerPeptideData(...). Failed to set modification position. srchRepPeptPeptDynamicModDTO.getPosition() == DTO_POSITION_INITIAL_VALUE. peptideSequenceString: " + peptideSequenceString;
						log.error( msg );
						throw new ProxlImporterInteralException( msg );
					}

					srchRepPeptPeptDynamicModDTO.setMass( mass.doubleValue() );
					
					dynamicModDTOList_Peptide.add( srchRepPeptPeptDynamicModDTO );

					if ( monolink != null && monolink ) {
						
						srchRepPeptPeptDynamicModDTO.setMonolink( true );

						MonolinkDataFromModificationContainer monolinkDataFromModificationContainer = new MonolinkDataFromModificationContainer();
						monolinkDataFromModificationContainer.setPosition( srchRepPeptPeptDynamicModDTO.getPosition() );
						monolinkDataFromModificationContainer.setIs_N_Terminal( srchRepPeptPeptDynamicModDTO.isIs_N_Terminal() );
						monolinkDataFromModificationContainer.setIs_C_Terminal( srchRepPeptPeptDynamicModDTO.isIs_C_Terminal() );
						monolinkDataFromModificationContainerList.add(monolinkDataFromModificationContainer);
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
