package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_PsmAnnotationDAO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
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
	
	/**
	 * @param searchProgramEntryMap
	 * @return
	 */
	public static SavePsmAnnotations getInstance( Map<String, SearchProgramEntry> searchProgramEntryMap, Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesOnId ) {
		SavePsmAnnotations savePsmAnnotations = new SavePsmAnnotations();
		savePsmAnnotations.searchProgramEntryMap = searchProgramEntryMap;
		savePsmAnnotations.filterableAnnotationTypesOnIdMasterCopy = filterableAnnotationTypesOnId;
		return savePsmAnnotations;
	}
	
	private Map<String, SearchProgramEntry> searchProgramEntryMap;
	private Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesOnIdMasterCopy;
	
	/**
	 * Returns list of inserted Filterable PsmAnnotationDTO
	 * 
	 * @param psm
	 * @param psmDTO
	 * @return
	 * @throws Exception
	 */
	public List<PsmAnnotationDTO> savePsmAnnotations( Psm psm, PsmDTO psmDTO ) throws Exception {
		List<PsmAnnotationDTO> psmAnnotationDTO_Filterable_List = 
				savePsmFilterablePsmAnnotations( psm, psmDTO );
		savePsmDescriptivePsmAnnotations( psm, psmDTO );
		return psmAnnotationDTO_Filterable_List;
	}
	
	/**
	 * @param psm
	 * @param psmDTO
	 * @throws Exception 
	 */
	private List<PsmAnnotationDTO> savePsmFilterablePsmAnnotations( Psm psm, PsmDTO psmDTO ) throws Exception {
	
		List<PsmAnnotationDTO> psmAnnotationDTO_Filterable_List = new ArrayList<>();
		//  Make local copy of filterableAnnotationTypesOnIdMasterCopy
		//    since remove entries from it.
		Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesOnId = new HashMap<>();
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry : filterableAnnotationTypesOnIdMasterCopy.entrySet() ) {
			filterableAnnotationTypesOnId.put( entry.getKey(), entry.getValue() );
		}
		//  Process PSM Filterable Annotation Entries
		FilterablePsmAnnotations filterablePsmAnnotations = psm.getFilterablePsmAnnotations();
		if ( filterablePsmAnnotations == null ) {
			if ( ! filterableAnnotationTypesOnIdMasterCopy.isEmpty() ) {
				String msg = "No PSM Filterable annotations on this PSM."
						+ "  Filterable annotations are required on all PSMs."
						+ "  Scan Number: " + psm.getScanNumber();
				log.error( msg );
				throw new ProxlImporterDataException( msg );
			} else {
				String msg = "No Filterable PSM annotations";
				log.warn( msg );
			}
		} else {
			List<FilterablePsmAnnotation> filterablePsmAnnotationList = filterablePsmAnnotations.getFilterablePsmAnnotation();
			if ( filterablePsmAnnotationList == null || filterablePsmAnnotationList.isEmpty() ) {
				if ( ! filterableAnnotationTypesOnIdMasterCopy.isEmpty() ) {
					String msg = "No PSM Filterable annotations on this PSM."
							+ "  Filterable annotations are required on all PSMs."
							+ "  Scan Number: " + psm.getScanNumber();
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				} else {
					String msg = "No Filterable PSM annotations";
					log.warn( msg );
				}				
			} else {
				//  Process list of filterable annotations on input list
				for ( FilterablePsmAnnotation filterablePsmAnnotation : filterablePsmAnnotationList ) {
					String searchProgram = filterablePsmAnnotation.getSearchProgram();
					String annotationName = filterablePsmAnnotation.getAnnotationName();
					BigDecimal value = filterablePsmAnnotation.getValue();
					int annotationTypeId = 
							getPsmAnnotationTypeId( 
									searchProgram, 
									annotationName, 
									FilterableDescriptiveAnnotationType.FILTERABLE );
					if ( filterableAnnotationTypesOnId.remove( annotationTypeId ) == null ) {
						//  Shouldn't get here
						String msg = "Internal Data mismatch error";
						log.error( msg );
						log.error( "filterableAnnotationTypesOnId.remove( annotationTypeId ) == null for annotationTypeId: " 
								+ annotationTypeId + ", annotationName: " + annotationName );
						List<String> filterablePsmAnnotationListNames = new ArrayList<>();
						for ( FilterablePsmAnnotation filterablePsmAnnotationTemp : filterablePsmAnnotationList ) {
							String name = filterablePsmAnnotationTemp.getAnnotationName();
							filterablePsmAnnotationListNames.add(name);
						}
						log.error( "filterableAnnotationTypesOnId.remove( annotationTypeId ) == null for filterablePsmAnnotationList names: " + StringUtils.join(filterablePsmAnnotationListNames, ",") );
						List<Integer> filterableAnnotationTypeIds = new ArrayList<>();
						for ( Map.Entry<Integer, AnnotationTypeDTO> entry : filterableAnnotationTypesOnId.entrySet() ) {
							int key = entry.getKey();
//							AnnotationTypeDTO valueTemp = entry.getValue();
							filterableAnnotationTypeIds.add( key );
						}
						log.error( "filterableAnnotationTypesOnId.remove( annotationTypeId ) == null for filterableAnnotationTypeIds type ids: " + StringUtils.join(filterableAnnotationTypeIds, ",") );
						throw new ProxlImporterInteralException(msg);
					}
					PsmAnnotationDTO psmAnnotationDTO = new PsmAnnotationDTO();
					psmAnnotationDTO.setPsmId( psmDTO.getId() );
					psmAnnotationDTO.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.FILTERABLE );
					psmAnnotationDTO.setAnnotationTypeId( annotationTypeId );
					psmAnnotationDTO.setValueDouble( value.doubleValue() );
					psmAnnotationDTO.setValueString( value.toString() );
					DB_Insert_PsmAnnotationDAO.getInstance().saveToDatabase(psmAnnotationDTO);
					psmAnnotationDTO_Filterable_List.add(psmAnnotationDTO);
				}
			}
		}
		if ( ! filterableAnnotationTypesOnId.isEmpty() ) {
			//  Filterable Annotations Types were not on the Filterable Annotations List
			String msg = "Not all Filterable Annotations Types were on the Filterable Annotations List for Psm. For Scan Number :" 
					+ psm.getScanNumber();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		return psmAnnotationDTO_Filterable_List;
	}
	
	/**
	 * @param psm
	 * @param psmDTO
	 * @throws Exception 
	 */
	private void savePsmDescriptivePsmAnnotations( Psm psm, PsmDTO psmDTO ) throws Exception {
	
		DescriptivePsmAnnotations descriptivePsmAnnotations = psm.getDescriptivePsmAnnotations();
		if ( descriptivePsmAnnotations == null ) {
//			String msg = "No Descriptive PSM annotations";
//			log.warn( msg );
		} else {
			List<DescriptivePsmAnnotation> descriptivePsmAnnotationList =
				descriptivePsmAnnotations.getDescriptivePsmAnnotation();
			if ( descriptivePsmAnnotationList == null || descriptivePsmAnnotationList.isEmpty() ) {
//				String msg = "No Descriptive PSM annotations";
//				log.warn( msg );
			} else {
				for ( DescriptivePsmAnnotation descriptivePsmAnnotation : descriptivePsmAnnotationList ) {
					String searchProgram = descriptivePsmAnnotation.getSearchProgram();
					String annotationName = descriptivePsmAnnotation.getAnnotationName();
					String value = descriptivePsmAnnotation.getValue();
					int annotationTypeId = 
							getPsmAnnotationTypeId( 
									searchProgram, 
									annotationName, 
									FilterableDescriptiveAnnotationType.DESCRIPTIVE );
					PsmAnnotationDTO psmAnnotationDTO = new PsmAnnotationDTO();
					psmAnnotationDTO.setPsmId( psmDTO.getId() );
					psmAnnotationDTO.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.DESCRIPTIVE );
					psmAnnotationDTO.setAnnotationTypeId( annotationTypeId );
					psmAnnotationDTO.setValueString( value.toString() );
					DB_Insert_PsmAnnotationDAO.getInstance().saveToDatabase(psmAnnotationDTO);
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
			FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType
			) throws ProxlImporterDataException {
		
		SearchProgramEntry searchProgramEntry =
				searchProgramEntryMap.get( searchProgram );
		if ( searchProgramEntry == null ) {
			String msg = "Processing filterablePsmAnnotations: "
					+ " search_program String |"
					+ searchProgram 
					+ "| on PSM not found under <search_programs> ."
					+ "  This is an error in the program that generated the Proxl XML file.";
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