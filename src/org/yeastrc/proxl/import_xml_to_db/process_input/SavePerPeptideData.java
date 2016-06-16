package org.yeastrc.proxl.import_xml_to_db.process_input;


// import org.apache.log4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptNrseqIdPosMonolinkDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptPeptDynamicModDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptPeptideDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptNrseqIdPosMonolinkDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * Save Per Peptide data: SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptNrseqIdPosMonolinkDTO
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
	 * Save SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptNrseqIdPosMonolinkDTO
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
		
		//  Save SrchRepPeptNrseqIdPosMonolinkDTO
		
		for ( SrchRepPeptNrseqIdPosMonolinkDTO srchRepPeptNrseqIdPosMonolinkDTO : perPeptideData.getSrchRepPeptNrseqIdPosMonolinkDTOList() ) {
			
			srchRepPeptNrseqIdPosMonolinkDTO.setSearchId( searchId );
			srchRepPeptNrseqIdPosMonolinkDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptNrseqIdPosMonolinkDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			
			DB_Insert_SrchRepPeptNrseqIdPosMonolinkDAO.getInstance().save( srchRepPeptNrseqIdPosMonolinkDTO );
		}
	}
		
}
