package org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_unified_rep_peptide_for_search.populate_per_annotation;


import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.xlink.dao.SearchReportedPeptideAnnotationDAO;
import org.yeastrc.xlink.dao.UnifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.dto.UnifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO;
import org.yeastrc.xlink.dto.UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO;

/**
 * 
 * 
 */
public class PopulateUnifiedReportedPeptideLevelFilterableAnnotationBySearchReptPeptide {
	
	private static final Logger log = Logger.getLogger(PopulateUnifiedReportedPeptideLevelFilterableAnnotationBySearchReptPeptide.class);
	

	// private constructor
	private PopulateUnifiedReportedPeptideLevelFilterableAnnotationBySearchReptPeptide() { }
	
	public static PopulateUnifiedReportedPeptideLevelFilterableAnnotationBySearchReptPeptide getInstance() { 
		return new PopulateUnifiedReportedPeptideLevelFilterableAnnotationBySearchReptPeptide(); 
	}
	
	
	
	
	public void insertAnnotationSpecificRecordsForSearchIdReportedPeptideId( 
			
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO,
			List<AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOList ) throws Exception {

		
		for ( AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO : srchPgmFilterableReportedPeptideAnnotationTypeDTOList ) {

			populatePsmFilterableAnnotationSummaryByAnnTypeIdSearchReptPeptide( srchPgmFilterableReportedPeptideAnnotationTypeDTO, unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO );
		}
		
	}
	
	

	/**
	 * @param annotationTypeDTO
	 * @param unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO
	 * @throws Exception
	 */
	private void populatePsmFilterableAnnotationSummaryByAnnTypeIdSearchReptPeptide(
			
			AnnotationTypeDTO annotationTypeDTO, 
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO ) throws Exception {
		
		try {

			int annotationTypeId = annotationTypeDTO.getId();
			
			int searchId = unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getSearchId();
			int reportedPeptideId = unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getReportedPeptideId();
			
			SearchReportedPeptideAnnotationDTO searchReportedPeptideFilterableAnnotationDTO =
			SearchReportedPeptideAnnotationDAO.getInstance().getItemForSearchIdReportedPeptideIdAnnotationId( searchId, reportedPeptideId, annotationTypeId );
			
			if ( searchReportedPeptideFilterableAnnotationDTO == null ) {
				
				String msg = "No searchReportedPeptideFilterableAnnotationDTO found for "
						+ " searchId: " + searchId
						+ ", reportedPeptideId: " + reportedPeptideId
						+ ", annotationTypeId: " + annotationTypeId;

				log.error( msg );
				
				throw new ProxlImporterDataException(msg);
			}
			
			double peptideValueForAnnTypeId = searchReportedPeptideFilterableAnnotationDTO.getValueDouble();
			String peptideValueStringForAnnTypeId = searchReportedPeptideFilterableAnnotationDTO.getValueString();
			
			UnifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO unifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO =
					new UnifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO( unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO );
			
			
			unifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO.setAnnotationTypeId( annotationTypeId );
			
			unifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO.setPeptideValueForAnnTypeId( peptideValueForAnnTypeId );;
			unifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO.setPeptideValueStringForAnnTypeId( peptideValueStringForAnnTypeId );;
			
			UnifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DAO.getInstance()
			.saveToDatabase( unifiedRepPep_ReportedPeptide_Search_PeptideValue_Generic_Lookup__DTO );
			
		} catch ( Exception e ) {
			
			String msg = "populatePsmFilterableAnnotationSummaryBySearchReptPeptide() ";
			
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			
		}
		

	}
	
	
}
