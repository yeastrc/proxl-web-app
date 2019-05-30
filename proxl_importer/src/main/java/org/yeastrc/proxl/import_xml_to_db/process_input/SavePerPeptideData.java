package org.yeastrc.proxl.import_xml_to_db.process_input;

 import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO;

import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptPeptDynamicModDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptPeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptPeptide_IsotopeLabel_DAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosMonolinkDTO;
import org.yeastrc.proxl.import_xml_to_db.exception.ProxlImporterDuplicateDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptide_IsotopeLabel_DTO;
import org.yeastrc.proxl.import_xml_to_db.objects.MonolinkContainer;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * Save Per Peptide data: SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptProtSeqIdPosMonolinkDTO
 *
 */
public class SavePerPeptideData {

	private static final Logger log = LoggerFactory.getLogger(  SavePerPeptideData.class );
	/**
	 * private constructor
	 */
	private SavePerPeptideData(){}
	public static SavePerPeptideData getInstance() { return new SavePerPeptideData(); }
	
	/**
	 * Save SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptProtSeqIdPosMonolinkDTO
	 * 
	 * @param perPeptideData
	 * @param searchId
	 * @param reportedPeptideDTO
	 * @throws Exception
	 */
	public void savePerPeptideData( 
			
			PerPeptideData perPeptideData,
			int searchId,
			ReportedPeptideDTO reportedPeptideDTO
			) throws Exception {
		
		//  Save SrchRepPeptPeptideDTO
		
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();
		
		srchRepPeptPeptideDTO.setSearchId( searchId );
		srchRepPeptPeptideDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
		
		DB_Insert_SrchRepPeptPeptideDAO.getInstance().save( srchRepPeptPeptideDTO );
		
		int searchReportedPeptidepeptideId = srchRepPeptPeptideDTO.getId();
		
		//  Save SrchRepPeptPeptDynamicModDTO
		
		if ( perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide() != null && ( ! perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide().isEmpty() ) ) {
			for ( SrchRepPeptPeptDynamicModDTO srchRepPeptPeptDynamicModDTO : perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide() ) {

				srchRepPeptPeptDynamicModDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
				
				DB_Insert_SrchRepPeptPeptDynamicModDAO.getInstance().save( srchRepPeptPeptDynamicModDTO );
			}
		}
		
		//  Save SrchRepPeptProtSeqIdPosMonolinkDTO
		if ( perPeptideData.getMonolinkContainerList() != null ) {
			for ( MonolinkContainer monolinkContainer : perPeptideData.getMonolinkContainerList() ) {
				SrchRepPeptProtSeqIdPosMonolinkDTO srchRepPeptProtSeqIdPosMonolinkDTO = monolinkContainer.getSrchRepPeptProtSeqIdPosMonolinkDTO();
				ProteinImporterContainer proteinImporterContainer = monolinkContainer.getProteinImporterContainer();

				srchRepPeptProtSeqIdPosMonolinkDTO.setSearchId( searchId );
				srchRepPeptProtSeqIdPosMonolinkDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
				srchRepPeptProtSeqIdPosMonolinkDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );

				srchRepPeptProtSeqIdPosMonolinkDTO.setProteinSequenceVersionId( proteinImporterContainer.getProteinSequenceVersionDTO().getId() );

				try {
					DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO.getSingletonInstance().save( srchRepPeptProtSeqIdPosMonolinkDTO );
					
				} catch ( ProxlImporterDuplicateDataException e ) {
					
					String peptidePosition_N_C_Terminus_errorMsg_Addition = "";
					
					if ( srchRepPeptProtSeqIdPosMonolinkDTO.isIs_N_Terminal() ) {
						
						peptidePosition_N_C_Terminus_errorMsg_Addition =
								"Monolink at N terminus of peptide";
						
					} else if ( srchRepPeptProtSeqIdPosMonolinkDTO.isIs_N_Terminal() ) {

						peptidePosition_N_C_Terminus_errorMsg_Addition =
								"Monolink at C terminus of peptide";
					} else {
						peptidePosition_N_C_Terminus_errorMsg_Addition =
								"Peptide Position: " 
										+ srchRepPeptProtSeqIdPosMonolinkDTO.getPeptidePosition();
					}
					
					String msg = "Reported Peptide has more than one monolink at the same position.  Reported peptide string: '"
							+ reportedPeptideDTO.getSequence()
							+ "'.  "
							+ peptidePosition_N_C_Terminus_errorMsg_Addition;
					log.warn(msg);
					throw new ProxlImporterDataException(msg);

					
				} catch ( Exception e ) {
					
					if ( e.toString() != null && e.toString().contains( "Duplicate entry" ) ) {

						String peptidePosition_N_C_Terminus_errorMsg_Addition = "";
						
						if ( srchRepPeptProtSeqIdPosMonolinkDTO.isIs_N_Terminal() ) {
							
							peptidePosition_N_C_Terminus_errorMsg_Addition =
									"Monolink at N terminus of peptide";
							
						} else if ( srchRepPeptProtSeqIdPosMonolinkDTO.isIs_N_Terminal() ) {

							peptidePosition_N_C_Terminus_errorMsg_Addition =
									"Monolink at C terminus of peptide";
						} else {
							peptidePosition_N_C_Terminus_errorMsg_Addition =
									"Peptide Position: " 
											+ srchRepPeptProtSeqIdPosMonolinkDTO.getPeptidePosition();
						}
						
						String msg = "Reported Peptide has more than one monolink at the same position.  Reported peptide string: '"
								+ reportedPeptideDTO.getSequence()
								+ "'.  "
								+ peptidePosition_N_C_Terminus_errorMsg_Addition;
						log.warn(msg);
						throw new ProxlImporterDataException(msg);
					}
					
					throw e;
				}
			}
		}

		//  Save SrchRepPeptPeptide_IsotopeLabel_DTO if have isotope labels
		if ( perPeptideData.getSrchRepPeptPeptide_IsotopeLabel_DTOList_Peptide() != null ) {
			for ( SrchRepPeptPeptide_IsotopeLabel_DTO srchRepPeptPeptide_IsotopeLabel_DTO : perPeptideData.getSrchRepPeptPeptide_IsotopeLabel_DTOList_Peptide() ) {
				srchRepPeptPeptide_IsotopeLabel_DTO.setSrchRepPeptPeptideId( searchReportedPeptidepeptideId );
				DB_Insert_SrchRepPeptPeptide_IsotopeLabel_DAO.getInstance().save( srchRepPeptPeptide_IsotopeLabel_DTO );
			}
		}
	}
		
}
