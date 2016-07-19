package org.yeastrc.proxl.import_xml_to_db.process_input;


// import org.apache.log4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptPeptDynamicModDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptPeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosMonolinkDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.objects.MonolinkContainer;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * Save Per Peptide data: SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptProtSeqIdPosMonolinkDTO
 *
 */
public class SavePerPeptideData {


//	private static final Logger log = Logger.getLogger( SavePerPeptideData.class );
	
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
		
		for ( SrchRepPeptPeptDynamicModDTO srchRepPeptPeptDynamicModDTO : perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide() ) {
			
			srchRepPeptPeptDynamicModDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			
			DB_Insert_SrchRepPeptPeptDynamicModDAO.getInstance().save( srchRepPeptPeptDynamicModDTO );
		}
		
		//  Save SrchRepPeptProtSeqIdPosMonolinkDTO
		
		for ( MonolinkContainer monolinkContainer : perPeptideData.getMonolinkContainerList() ) {
			
			SrchRepPeptProtSeqIdPosMonolinkDTO srchRepPeptProtSeqIdPosMonolinkDTO = monolinkContainer.getSrchRepPeptProtSeqIdPosMonolinkDTO();
			ProteinImporterContainer proteinImporterContainer = monolinkContainer.getProteinImporterContainer();
			
			srchRepPeptProtSeqIdPosMonolinkDTO.setSearchId( searchId );
			srchRepPeptProtSeqIdPosMonolinkDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptProtSeqIdPosMonolinkDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			
			srchRepPeptProtSeqIdPosMonolinkDTO.setProteinSequenceId( proteinImporterContainer.getProteinSequenceDTO().getId() );

			
			DB_Insert_SrchRepPeptProtSeqIdPosMonolinkDAO.getInstance().save( srchRepPeptProtSeqIdPosMonolinkDTO );
		}
	}
		
}
