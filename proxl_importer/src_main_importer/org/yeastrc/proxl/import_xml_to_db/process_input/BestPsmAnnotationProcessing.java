package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO;
import org.yeastrc.proxl.import_xml_to_db.dto.UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;

/**
 * Best PSM Annotation Processing
 * 
 * One instance of this class per search/reported peptide
 *
 */
public class BestPsmAnnotationProcessing {

	private static final Logger log = Logger.getLogger( BestPsmAnnotationProcessing.class );
	
	//  private constructor 
	private BestPsmAnnotationProcessing() {}
	
	/**
	 * Get One instance of this class per search/reported peptide
	 * 
	 * @return
	 */
	public static BestPsmAnnotationProcessing getInstance( Map<Integer, AnnotationTypeDTO> filterablePsmAnnotationTypesOnId ) {
		
		BestPsmAnnotationProcessing bestPsmAnnotationProcessing = new BestPsmAnnotationProcessing();
		
		bestPsmAnnotationProcessing.filterablePsmAnnotationTypesOnId = filterablePsmAnnotationTypesOnId;
		
		return bestPsmAnnotationProcessing;
	}
	

	private static class BestPsmAnnotationDTO {

		PsmAnnotationDTO bestPsmAnnotationDTO;
		List<PsmAnnotationDTO> associated_psmAnnotationDTO_Filterable_List;
	}

	
	
	private Map<Integer, AnnotationTypeDTO> filterablePsmAnnotationTypesOnId;
	
	private Map<Integer, BestPsmAnnotationDTO> bestPsmAnnotationDTO_KeyedOn_AnnotationTypeId = new HashMap<>();

	
	public void updateForCurrentPsmAnnotationData( 
			
			List<PsmAnnotationDTO> currentPsm_psmAnnotationDTO_Filterable_List
			) throws ProxlImporterDataException {
		
		
		//  TODO  
		
//			Optimize this so as many of the best currentPsm_psmAnnotationDTO_Filterable 
//			have the same psm id


		for ( PsmAnnotationDTO currentPsm_psmAnnotationDTO_Filterable : currentPsm_psmAnnotationDTO_Filterable_List ) {

			Integer currentPsm_annotationTypeId = currentPsm_psmAnnotationDTO_Filterable.getAnnotationTypeId();
			
			AnnotationTypeDTO currentPsm_annotationType =   filterablePsmAnnotationTypesOnId.get( currentPsm_annotationTypeId );

			AnnotationTypeFilterableDTO currentPsm_AnnotationTypeFilterableDTO = currentPsm_annotationType.getAnnotationTypeFilterableDTO();

			if ( currentPsm_AnnotationTypeFilterableDTO == null ) {

				String msg = "currentPsm_AnnotationTypeFilterableDTO == null for currentPsm_annotationTypeId: " + currentPsm_annotationTypeId;
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}


			/////////   Update Best PSM Annotation DTO

			BestPsmAnnotationDTO bestPsmAnnotationDTO = 
					bestPsmAnnotationDTO_KeyedOn_AnnotationTypeId.get( currentPsm_annotationTypeId );

			if ( bestPsmAnnotationDTO == null ) {

				bestPsmAnnotationDTO = new BestPsmAnnotationDTO();

				bestPsmAnnotationDTO.bestPsmAnnotationDTO = currentPsm_psmAnnotationDTO_Filterable;
				bestPsmAnnotationDTO.associated_psmAnnotationDTO_Filterable_List = currentPsm_psmAnnotationDTO_Filterable_List;

				bestPsmAnnotationDTO_KeyedOn_AnnotationTypeId.put( currentPsm_annotationTypeId, bestPsmAnnotationDTO );

			} else {


				if ( currentPsm_AnnotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.ABOVE ) {

					if ( currentPsm_psmAnnotationDTO_Filterable.getValueDouble() 
							> bestPsmAnnotationDTO.bestPsmAnnotationDTO.getValueDouble() ) {

						bestPsmAnnotationDTO.bestPsmAnnotationDTO = currentPsm_psmAnnotationDTO_Filterable;
						bestPsmAnnotationDTO.associated_psmAnnotationDTO_Filterable_List = currentPsm_psmAnnotationDTO_Filterable_List;
					}

				} else if ( currentPsm_AnnotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.BELOW ) {

					if ( currentPsm_psmAnnotationDTO_Filterable.getValueDouble() 
							< bestPsmAnnotationDTO.bestPsmAnnotationDTO.getValueDouble() ) {

						bestPsmAnnotationDTO.bestPsmAnnotationDTO = currentPsm_psmAnnotationDTO_Filterable;
						bestPsmAnnotationDTO.associated_psmAnnotationDTO_Filterable_List = currentPsm_psmAnnotationDTO_Filterable_List;
					}

				} else {

					String msg = " Unexpected FilterDirectionType value:  " + currentPsm_AnnotationTypeFilterableDTO.getFilterDirectionType()
							+ ", for currentPsm_annotationTypeId: " + currentPsm_annotationTypeId;
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}

			}

		}		
	}
	
	
	/**
	 * @param unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO
	 * @return
	 */
	public List<UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO> getBestPsmValues( 
			
			UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO
			) {
		
		
		List<UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO> results = new ArrayList<>();
		
		for ( Map.Entry<Integer, BestPsmAnnotationDTO> entry : bestPsmAnnotationDTO_KeyedOn_AnnotationTypeId.entrySet() ) {
			
			BestPsmAnnotationDTO bestPsmAnnotationDTO = entry.getValue();
			
			PsmAnnotationDTO psmAnnotationDTO = bestPsmAnnotationDTO.bestPsmAnnotationDTO;
			
			UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO unifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO =
					new UnifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO( unifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DTO );
			
			unifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO.setAnnotationTypeId( psmAnnotationDTO.getAnnotationTypeId() );
			unifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO.setBestPsmValueForAnnTypeId( psmAnnotationDTO.getValueDouble() );
			unifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO.setPsmIdForBestValue( psmAnnotationDTO.getPsmId() );
			
			results.add( unifiedRepPep_Search_ReportedPeptide_BestPsmValue_Generic_Lookup__DTO );
		}	
		
		return results;
	}
	
}
