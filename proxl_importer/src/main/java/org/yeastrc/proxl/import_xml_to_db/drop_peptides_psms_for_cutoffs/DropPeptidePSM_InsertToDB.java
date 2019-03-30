package org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs;

import java.util.Map;

import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_CutoffsAppliedOnImportDAO;
import org.yeastrc.xlink.dto.CutoffsAppliedOnImportDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;

/**
 * 
 * Insert DB records for the Drop Peptide and/or PSM entries on the command line
 */
public class DropPeptidePSM_InsertToDB {

	private static final Logger log = LoggerFactory.getLogger( DropPeptidePSM_InsertToDB.class);

	//  private constructor
	private DropPeptidePSM_InsertToDB() { }
	
	public static DropPeptidePSM_InsertToDB getInstance() { return new DropPeptidePSM_InsertToDB(); }
	
	/**
	 * @param dropPeptidePSMCutoffValues
	 * @param searchProgramEntryMap
	 * @throws Exception 
	 */
	public void insertDBEntries( DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues, Map<String, SearchProgramEntry> searchProgramEntryMap ) throws Exception {


		Map<String,Map<String,DropPeptidePSMCutoffValue>> dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName =
				dropPeptidePSMCutoffValues.getDropPeptideCutoffValueKeyedOnSearchPgmNameAnnName();

		Map<String,Map<String,DropPeptidePSMCutoffValue>> dropPSMCutoffValueKeyedOnSearchPgmNameAnnName =
				dropPeptidePSMCutoffValues.getDropPSMCutoffValueKeyedOnSearchPgmNameAnnName();
		
		//  process peptide cutoffs
		
		if ( dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName != null && ( ! dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName.isEmpty() ) ) {

			for ( Map.Entry<String,Map<String, DropPeptidePSMCutoffValue>> dropPeptidePSMCutoffValueEntry : 
				dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName.entrySet() ) {
				
				String searchPgmName = dropPeptidePSMCutoffValueEntry.getKey();
				Map<String, DropPeptidePSMCutoffValue> dropPeptideCutoffValueKeyedOnAnnName = dropPeptidePSMCutoffValueEntry.getValue();

				SearchProgramEntry searchProgramEntry = searchProgramEntryMap.get( searchPgmName );

				if ( searchProgramEntry == null ) {

					String msg = "No searchProgram for searchPgmName in cutoff: " + searchPgmName;
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}

				insertReportedPeptideOrPSMEntries( 
						searchProgramEntry.getReportedPeptideAnnotationTypeDTOMap(), 
						dropPeptideCutoffValueKeyedOnAnnName,
						searchPgmName );
			}
		}
				
		//  process PSM cutoffs
		
		if ( dropPSMCutoffValueKeyedOnSearchPgmNameAnnName != null && ( ! dropPSMCutoffValueKeyedOnSearchPgmNameAnnName.isEmpty() ) ) {

			for ( Map.Entry<String,Map<String, DropPeptidePSMCutoffValue>> dropPeptidePSMCutoffValueEntry : 
				dropPSMCutoffValueKeyedOnSearchPgmNameAnnName.entrySet() ) {
				
				String searchPgmName = dropPeptidePSMCutoffValueEntry.getKey();
				Map<String, DropPeptidePSMCutoffValue> dropPeptideCutoffValueKeyedOnAnnName = dropPeptidePSMCutoffValueEntry.getValue();

				SearchProgramEntry searchProgramEntry = searchProgramEntryMap.get( searchPgmName );

				if ( searchProgramEntry == null ) {

					String msg = "No searchProgram for searchPgmName in cutoff: " + searchPgmName;
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}

				insertReportedPeptideOrPSMEntries( 
						searchProgramEntry.getPsmAnnotationTypeDTOMap(), 
						dropPeptideCutoffValueKeyedOnAnnName,
						searchPgmName );
			}
		}
				
		
		
	}
	

	
	/**
	 * @param reportedPeptide_Or_PSM_AnnotationTypeDTOMap
	 * @param dropPeptide_Or_PSM_CutoffValueMap
	 * @param searchPgmName
	 * @throws Exception
	 */
	private void insertReportedPeptideOrPSMEntries( 
			Map<String, AnnotationTypeDTO> reportedPeptide_Or_PSM_AnnotationTypeDTOMap, 
			Map<String, DropPeptidePSMCutoffValue>  dropPeptide_Or_PSM_CutoffValueMap,
			String searchPgmName ) throws Exception {
		

		if ( dropPeptide_Or_PSM_CutoffValueMap != null && ( ! dropPeptide_Or_PSM_CutoffValueMap.isEmpty() ) ) {

			if ( reportedPeptide_Or_PSM_AnnotationTypeDTOMap != null && ( ! reportedPeptide_Or_PSM_AnnotationTypeDTOMap.isEmpty() ) ) {

				for ( Map.Entry<String, DropPeptidePSMCutoffValue>  dropPeptide_Or_PSM_CutoffValueEntry :
					dropPeptide_Or_PSM_CutoffValueMap.entrySet() ) {
					
					DropPeptidePSMCutoffValue dropPeptide_Or_PSM_CutoffValue = dropPeptide_Or_PSM_CutoffValueEntry.getValue();

					AnnotationTypeDTO annotationTypeDTO = 
							reportedPeptide_Or_PSM_AnnotationTypeDTOMap.get( dropPeptide_Or_PSM_CutoffValue.getAnnotationName() );

					if ( annotationTypeDTO == null ) {

						//  This annotation type name is not present for this search program

						String msg = "No Annotation for Annotation name in cutoff: " 
								+ dropPeptide_Or_PSM_CutoffValue.getAnnotationName()
								+ ", searchPgmName: "
								+ searchPgmName;
						log.error( msg );
						throw new ProxlImporterDataException(msg);
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
