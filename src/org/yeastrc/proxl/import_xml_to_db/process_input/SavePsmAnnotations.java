package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.xlink.dao.PsmAnnotationDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;

/**
 * Save PSM filterable and descriptive annotations to the DB
 *
 */
public class SavePsmAnnotations {


	private static final Logger log = Logger.getLogger( SavePsmAnnotations.class );
	
	/**
	 * private constructor
	 */
	private SavePsmAnnotations(){}
	
	public static SavePsmAnnotations getInstance() {
		
		return new SavePsmAnnotations();
	}

	/**
	 * @param psm
	 * @param psmDTO
	 * @param searchProgramEntryMap
	 * @throws Exception
	 */
	public void savePsmAnnotations( Psm psm, PsmDTO psmDTO, Map<String, SearchProgramEntry> searchProgramEntryMap ) throws Exception {
		
		
		savePsmFilterablePsmAnnotations( psm, psmDTO, searchProgramEntryMap );

		savePsmDescriptivePsmAnnotations( psm, psmDTO, searchProgramEntryMap );

	}
	

	/**
	 * @param psm
	 * @param psmDTO
	 * @param searchProgramEntryMap
	 * @throws Exception 
	 */
	private void savePsmFilterablePsmAnnotations( Psm psm, PsmDTO psmDTO, Map<String, SearchProgramEntry> searchProgramEntryMap ) throws Exception {
		
		FilterablePsmAnnotations filterablePsmAnnotations = psm.getFilterablePsmAnnotations();
		
		if ( filterablePsmAnnotations == null ) {
			
			String msg = "No Filterable PSM annotations";
			log.warn( msg );
			
		} else {

			List<FilterablePsmAnnotation> filterablePsmAnnotationList =
					filterablePsmAnnotations.getFilterablePsmAnnotation();
			
			if ( filterablePsmAnnotationList == null || filterablePsmAnnotationList.isEmpty() ) {
				
				String msg = "No Filterable PSM annotations";
				log.warn( msg );
				
			} else {
				

				for ( FilterablePsmAnnotation filterablePsmAnnotation : filterablePsmAnnotationList ) {

					String searchProgram = filterablePsmAnnotation.getSearchProgram();
					String annotationName = filterablePsmAnnotation.getAnnotationName();
					BigDecimal value = filterablePsmAnnotation.getValue();

					int annotationTypeId = 
							getPsmAnnotationTypeId( 
									searchProgram, 
									annotationName, 
									FilterableDescriptiveAnnotationType.FILTERABLE,
									searchProgramEntryMap );

					PsmAnnotationDTO psmAnnotationDTO = new PsmAnnotationDTO();

					psmAnnotationDTO.setPsmId( psmDTO.getId() );
					
					psmAnnotationDTO.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.FILTERABLE );
					
					psmAnnotationDTO.setAnnotationTypeId( annotationTypeId );

					psmAnnotationDTO.setValueDouble( value.doubleValue() );
					psmAnnotationDTO.setValueString( value.toString() );

					PsmAnnotationDAO.getInstance().saveToDatabase(psmAnnotationDTO);
				}
			}
		}
	}
	
	
	
	
	
	
	

	/**
	 * @param psm
	 * @param psmDTO
	 * @param searchProgramEntryMap
	 * @throws Exception 
	 */
	private void savePsmDescriptivePsmAnnotations( Psm psm, PsmDTO psmDTO, Map<String, SearchProgramEntry> searchProgramEntryMap ) throws Exception {
		
		DescriptivePsmAnnotations descriptivePsmAnnotations = psm.getDescriptivePsmAnnotations();

		if ( descriptivePsmAnnotations == null ) {
			
			String msg = "No Descriptive PSM annotations";
			log.warn( msg );
			
		} else {

			List<DescriptivePsmAnnotation> descriptivePsmAnnotationList =
				descriptivePsmAnnotations.getDescriptivePsmAnnotation();

			if ( descriptivePsmAnnotationList == null || descriptivePsmAnnotationList.isEmpty() ) {
				
				String msg = "No Descriptive PSM annotations";
				log.warn( msg );
				
			} else {
				
				for ( DescriptivePsmAnnotation descriptivePsmAnnotation : descriptivePsmAnnotationList ) {

					String searchProgram = descriptivePsmAnnotation.getSearchProgram();
					String annotationName = descriptivePsmAnnotation.getAnnotationName();
					String value = descriptivePsmAnnotation.getValue();


					int annotationTypeId = 
							getPsmAnnotationTypeId( 
									searchProgram, 
									annotationName, 
									FilterableDescriptiveAnnotationType.DESCRIPTIVE, 
									searchProgramEntryMap );

					PsmAnnotationDTO psmAnnotationDTO = new PsmAnnotationDTO();

					psmAnnotationDTO.setPsmId( psmDTO.getId() );
					
					psmAnnotationDTO.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.DESCRIPTIVE );
					
					psmAnnotationDTO.setAnnotationTypeId( annotationTypeId );

					psmAnnotationDTO.setValueString( value.toString() );

					PsmAnnotationDAO.getInstance().saveToDatabase(psmAnnotationDTO);
				}
			}
		}
	}
	

	/**
	 * @param searchProgram
	 * @param annotationName
	 * @param filterableDescriptiveAnnotationType
	 * @param searchProgramEntryMap
	 * @return
	 * @throws ProxlImporterDataException
	 */
	private int getPsmAnnotationTypeId( 
			
			String searchProgram, 
			String annotationName, 
			FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType,
			
			Map<String, SearchProgramEntry> searchProgramEntryMap ) throws ProxlImporterDataException {
		
		SearchProgramEntry searchProgramEntry =
				searchProgramEntryMap.get( searchProgram );
		
		if ( searchProgramEntry == null ) {
			
			String msg = "Processing filterablePsmAnnotations: "
					+ " search_program String |"
					+ searchProgram 
					+ "| on PSM not found under <search_programs> .";
			
			log.error( msg );
			
			throw new ProxlImporterDataException(msg);
		}
		
		Map<String, AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOMap =
				searchProgramEntry.getPsmAnnotationTypeDTOMap();
		
		AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = 
				srchPgmFilterablePsmAnnotationTypeDTOMap.get( annotationName );
		

		if ( srchPgmFilterablePsmAnnotationTypeDTO == null ) {
			
			String msg = "Processing PsmAnnotations: "
					+ " annotation name String |"
					+ annotationName 
					+ "| on PSM not found under <..._psm_annotation_types> under <search_programs> for search program: " + searchProgram;
			
			log.error( msg );
			
			throw new ProxlImporterDataException(msg);
		}
		
		if ( filterableDescriptiveAnnotationType != srchPgmFilterablePsmAnnotationTypeDTO.getFilterableDescriptiveAnnotationType() ) {
			
			String msg = "Processing PsmAnnotations: "
					+ "filterableDescriptiveAnnotationType for annotation name not same between types under <search_programs>"
					+ " and data under PSM."
					+ " annotation name String |"
					+ annotationName 
					+ "|.";
			
			log.error( msg );
			
			throw new ProxlImporterDataException(msg);
		}
		
		int id = srchPgmFilterablePsmAnnotationTypeDTO.getId();
		
		return id;
	}
		
}
