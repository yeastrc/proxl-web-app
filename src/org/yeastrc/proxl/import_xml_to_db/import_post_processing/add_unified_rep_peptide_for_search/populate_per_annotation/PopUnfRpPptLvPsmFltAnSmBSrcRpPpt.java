package org.yeastrc.proxl.import_xml_to_db.import_post_processing.add_unified_rep_peptide_for_search.populate_per_annotation;


import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetPsmCountByAnnTypeIdSearchReptPeptideDefaultCutoffSearcher;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers.GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchReptPeptideSearcher;
import org.yeastrc.xlink.dao.UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO;
import org.yeastrc.xlink.dto.UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;

/**
 * PopulateUnifiedReportedPeptideLevelPsmFilterableAnnotationSummaryBySearchReptPeptide
 * 
 * 
 */
public class PopUnfRpPptLvPsmFltAnSmBSrcRpPpt {
	
	private static final Logger log = Logger.getLogger(PopUnfRpPptLvPsmFltAnSmBSrcRpPpt.class);
	

	// private constructor
	private PopUnfRpPptLvPsmFltAnSmBSrcRpPpt() { }
	
	public static PopUnfRpPptLvPsmFltAnSmBSrcRpPpt getInstance() { 
		return new PopUnfRpPptLvPsmFltAnSmBSrcRpPpt(); 
	}
	
	
	
	
	public void insertAnnotationSpecificRecordsForSearchIdReportedPeptideId( 
			
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO,
			List<AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOList ) throws Exception {

		
		for ( AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO : srchPgmFilterablePsmAnnotationTypeDTOList ) {

			populatePsmFilterableAnnotationSummaryByAnnTypeIdSearchReptPeptide( srchPgmFilterablePsmAnnotationTypeDTO, unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO );
		}
		
	}
	
	

	/**
	 * @param srchPgmFilterablePsmAnnotationTypeDTO
	 * @param unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO
	 * @throws Exception
	 */
	private void populatePsmFilterableAnnotationSummaryByAnnTypeIdSearchReptPeptide(
			
			AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO, 
			UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO ) throws Exception {
		
		try {

			if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
				
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}

			int psmFilterableAnnotationTypeId = srchPgmFilterablePsmAnnotationTypeDTO.getId();
			
			int search_id = unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getSearchId();
			int reported_peptide_id = unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO.getReportedPeptideId();
			FilterDirectionType filterDirectionType = srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType();
			
			BestFilterableAnnotationValue bestFilterableAnnotationValue = 
					GetPsmFilterableAnnotationBestValueByAnnTypeIdSearchReptPeptideSearcher.getInstance().getBestAnnotationValue( 
							psmFilterableAnnotationTypeId, search_id, reported_peptide_id, filterDirectionType) ;
			
			UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO unifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO =
					new UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO( unifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DTO );
			
			if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().isDefaultFilter() ) {

				//  Default filter so get the PSM count and set on record
				
				Double defaultFilterValue = srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getDefaultFilterValue();
				
				if ( defaultFilterValue == null ) {
					
					String msg = "ERROR:  defaultFilterValue == null when DefaultFilter is true for psmFilterableAnnotationTypeId: " + psmFilterableAnnotationTypeId;
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}
				
				int psmNumForAnnTypeIdAtDefaultCutoff =
						GetPsmCountByAnnTypeIdSearchReptPeptideDefaultCutoffSearcher.getInstance()
						.getPsmCountForCutoffValue( psmFilterableAnnotationTypeId, search_id, reported_peptide_id, defaultFilterValue, filterDirectionType );
				
				unifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO.setPsmNumForAnnTypeIdAtDefaultCutoff( psmNumForAnnTypeIdAtDefaultCutoff );
			}
			
			unifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO.setPsmFilterableAnnotationTypeId( psmFilterableAnnotationTypeId );
			
			unifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO.setBestPsmValueForAnnTypeId( bestFilterableAnnotationValue.getBestValue() );
			unifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO.setBestPsmValueStringForAnnTypeId( bestFilterableAnnotationValue.getBestValueString() );
			
			UnifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DAO.getInstance()
			.saveToDatabase( unifiedRepPep_ReportedPeptide_Search_BestPsmValue_Generic_Lookup__DTO );
			
		} catch ( Exception e ) {
			
			String msg = "populatePsmFilterableAnnotationSummaryBySearchReptPeptide() ";
			
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			
		}
		

	}
	
	
}
