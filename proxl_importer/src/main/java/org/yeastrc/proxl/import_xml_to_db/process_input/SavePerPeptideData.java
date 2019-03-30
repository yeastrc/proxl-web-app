package org.yeastrc.proxl.import_xml_to_db.process_input;

// import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO;

import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptPeptDynamicModDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptPeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptPeptide_IsotopeLabel_DAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosMonolinkDTO;
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

//	private static final Logger log = LoggerFactory.getLogger(  SavePerPeptideData.class );
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

				DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO.getInstance().save( srchRepPeptProtSeqIdPosMonolinkDTO );
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
