package org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_CutoffsAppliedOnImportDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.CutoffsAppliedOnImportDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;

/**
 * 
 * Insert DB records for the Drop Peptide and/or PSM entries on the command line
 */
public class DropPeptidePSM_InsertToDB {

	private static final Logger log = Logger.getLogger(DropPeptidePSM_InsertToDB.class);

	//  private constructor
	private DropPeptidePSM_InsertToDB() { }
	
	public static DropPeptidePSM_InsertToDB getInstance() { return new DropPeptidePSM_InsertToDB(); }
	
	/**
	 * @param dropPeptidePSMCutoffValues
	 * @param searchProgramEntryMap
	 * @throws Exception 
	 */
	public void insertDBEntries( DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues, Map<String, SearchProgramEntry> searchProgramEntryMap ) throws Exception {

		List<DropPeptidePSMCutoffValue> dropPeptideCutoffValueList = dropPeptidePSMCutoffValues.getDropPeptideCutoffValueList();
		List<DropPeptidePSMCutoffValue> dropPsmCutoffValueList = dropPeptidePSMCutoffValues.getDropPSMCutoffValueList();
		
		if ( ( dropPeptideCutoffValueList != null && ( ! dropPeptideCutoffValueList.isEmpty() ) ) 
				||  ( dropPsmCutoffValueList != null && ( ! dropPsmCutoffValueList.isEmpty() ) ) ) {

			if ( searchProgramEntryMap == null ) {

				String msg = "searchProgramEntryMap cannot be null";
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}

			for ( Map.Entry<String, SearchProgramEntry> searchProgramEntryEntry : searchProgramEntryMap.entrySet() ) {
				
				SearchProgramEntry searchProgramEntry = searchProgramEntryEntry.getValue();
				
				insertReportedPeptideOrPSMEntries( 
						searchProgramEntry.getReportedPeptideAnnotationTypeDTOMap(), 
						dropPeptideCutoffValueList );
				
				insertReportedPeptideOrPSMEntries( 
						searchProgramEntry.getPsmAnnotationTypeDTOMap(), 
						dropPsmCutoffValueList );
			}
		}
		
		
		
	}
	

	
	/**
	 * @param reportedPeptide_Or_PSM_AnnotationTypeDTOMap
	 * @param dropPeptide_Or_PSM_CutoffValueList
	 * @throws Exception
	 */
	private void insertReportedPeptideOrPSMEntries( 
			Map<String, AnnotationTypeDTO> reportedPeptide_Or_PSM_AnnotationTypeDTOMap, 
			List<DropPeptidePSMCutoffValue> dropPeptide_Or_PSM_CutoffValueList ) throws Exception {
		

		if ( dropPeptide_Or_PSM_CutoffValueList != null && ( ! dropPeptide_Or_PSM_CutoffValueList.isEmpty() ) ) {

			if ( reportedPeptide_Or_PSM_AnnotationTypeDTOMap != null && ( ! reportedPeptide_Or_PSM_AnnotationTypeDTOMap.isEmpty() ) ) {

				for ( DropPeptidePSMCutoffValue dropPeptide_Or_PSM_CutoffValue :dropPeptide_Or_PSM_CutoffValueList ) {

					AnnotationTypeDTO annotationTypeDTO = 
							reportedPeptide_Or_PSM_AnnotationTypeDTOMap.get( dropPeptide_Or_PSM_CutoffValue.getAnnotationName() );

					if ( annotationTypeDTO == null ) {

						//  This annotation type name is not present for this search program
						
						continue;  //  EARLY continue
					}
									
					String cutoffValueString = dropPeptide_Or_PSM_CutoffValue.getCutoffValue().toString();
					
					double cutoffValueDouble = dropPeptide_Or_PSM_CutoffValue.getCutoffValue().doubleValue();

					CutoffsAppliedOnImportDTO cutoffsAppliedOnImportDTO = new CutoffsAppliedOnImportDTO();

					cutoffsAppliedOnImportDTO.setSearchId( annotationTypeDTO.getSearchId() );
					cutoffsAppliedOnImportDTO.setAnnotationTypeId( annotationTypeDTO.getId() );
					cutoffsAppliedOnImportDTO.setCutoffValueString( cutoffValueString );
					cutoffsAppliedOnImportDTO.setCutoffValueDouble( cutoffValueDouble );

					DB_Insert_CutoffsAppliedOnImportDAO.getInstance()
					.save( cutoffsAppliedOnImportDTO );
				}
			}
		}
	}
	
}
