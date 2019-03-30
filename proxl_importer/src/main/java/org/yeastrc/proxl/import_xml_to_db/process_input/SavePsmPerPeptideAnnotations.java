package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_PsmPerPeptideAnnotationDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmPerPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmPerPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmPerPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmPerPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm.PerPeptideAnnotations.PsmPeptide;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.PsmPerPeptideAnnotationDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;

/**
 * Save PSM Per Peptide filterable and descriptive annotations to the DB
 *
 */

public class SavePsmPerPeptideAnnotations {

	private static final Logger log = LoggerFactory.getLogger(  SavePsmPerPeptideAnnotations.class );
	/**
	 * private constructor
	 */
	private SavePsmPerPeptideAnnotations(){}
	
	/**
	 * @param searchProgramEntryMap
	 * @return
	 */
	public static SavePsmPerPeptideAnnotations getInstance( Map<String, SearchProgramEntry> searchProgramEntryMap, Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesOnId ) {
		SavePsmPerPeptideAnnotations savePsmPerPeptideAnnotations = new SavePsmPerPeptideAnnotations();
		savePsmPerPeptideAnnotations.searchProgramEntryMap = searchProgramEntryMap;
		savePsmPerPeptideAnnotations.filterableAnnotationTypesOnIdMasterCopy = filterableAnnotationTypesOnId;
		return savePsmPerPeptideAnnotations;
	}
	
	private Map<String, SearchProgramEntry> searchProgramEntryMap;
	private Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesOnIdMasterCopy;
	
	/**
	 * Returns list of inserted Filterable PsmPerPeptideAnnotationDTO
	 * 
	 * @param psmPeptide
	 * @param perPeptideData
	 * @return
	 * @throws Exception
	 */
	public List<PsmPerPeptideAnnotationDTO> savePsmPerPeptideAnnotations( PsmPeptide psmPeptide, PerPeptideData perPeptideData, PsmDTO psmDTO ) throws Exception {
		List<PsmPerPeptideAnnotationDTO> psmPerPeptideAnnotationDTO_Filterable_List = 
				saveFilterablePsmAnnotations( psmPeptide, perPeptideData, psmDTO );
		saveDescriptivePsmAnnotations( psmPeptide, perPeptideData, psmDTO );
		return psmPerPeptideAnnotationDTO_Filterable_List;
	}
	
	/**
	 * @param psmPeptide
	 * @param perPeptideData
	 * @throws Exception 
	 */
	private List<PsmPerPeptideAnnotationDTO> saveFilterablePsmAnnotations( PsmPeptide psmPeptide, PerPeptideData perPeptideData, PsmDTO psmDTO ) throws Exception {
	
		List<PsmPerPeptideAnnotationDTO> psmPerPeptideAnnotationDTO_Filterable_List = new ArrayList<>();
		//  Make local copy of filterableAnnotationTypesOnIdMasterCopy
		//    since remove entries from it.
		Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesOnId = new HashMap<>();
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry : filterableAnnotationTypesOnIdMasterCopy.entrySet() ) {
			filterableAnnotationTypesOnId.put( entry.getKey(), entry.getValue() );
		}

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();
		
		//  Process PSM Per Peptide Filterable Annotation Entries
		FilterablePsmPerPeptideAnnotations filterablePsmPerPeptideAnnotations = psmPeptide.getFilterablePsmPerPeptideAnnotations();
					
		if ( filterablePsmPerPeptideAnnotations == null ) {
			if ( ! filterableAnnotationTypesOnIdMasterCopy.isEmpty() ) {
				String msg = "No PSM Per Peptide Filterable annotations on this PSM Per Peptide."
						+ "  Filterable annotations are required on all PSM Per Peptide."
						+ "  Unique id: " + psmPeptide.getUniqueId();
				log.error( msg );
				throw new ProxlImporterDataException( msg );
			} else {
				String msg = "No Filterable PSM Per Peptide annotations";
				log.warn( msg );
			}
		} else {
			List<FilterablePsmPerPeptideAnnotation> filterablePsmPerPeptideAnnotationList = filterablePsmPerPeptideAnnotations.getFilterablePsmPerPeptideAnnotation();
			if ( filterablePsmPerPeptideAnnotationList == null || filterablePsmPerPeptideAnnotationList.isEmpty() ) {
				if ( ! filterableAnnotationTypesOnIdMasterCopy.isEmpty() ) {
					String msg = "No PSM Per Peptide Filterable annotations on this PSM Per Peptide."
							+ "  Filterable annotations are required on all PSM Per Peptide."
							+ "  Unique id: " + psmPeptide.getUniqueId();
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				} else {
					String msg = "No Filterable PSM Per Peptide annotations";
					log.warn( msg );
				}
			} else {
				//  Process list of filterable annotations on input list
				for ( FilterablePsmPerPeptideAnnotation filterablePsmPerPeptideAnnotation : filterablePsmPerPeptideAnnotationList ) {
					String searchProgram = filterablePsmPerPeptideAnnotation.getSearchProgram();
					String annotationName = filterablePsmPerPeptideAnnotation.getAnnotationName();
					BigDecimal value = filterablePsmPerPeptideAnnotation.getValue();
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
						List<String> filterableAnnotationListNames = new ArrayList<>();
						for ( FilterablePsmPerPeptideAnnotation filterablePsmPerPeptideAnnotationTemp : filterablePsmPerPeptideAnnotationList ) {
							String name = filterablePsmPerPeptideAnnotationTemp.getAnnotationName();
							filterableAnnotationListNames.add(name);
						}
						log.error( "filterableAnnotationTypesOnId.remove( annotationTypeId ) == null for filterablePsmAnnotationList names: " + StringUtils.join(filterableAnnotationListNames, ",") );
						List<Integer> filterableAnnotationTypeIds = new ArrayList<>();
						for ( Map.Entry<Integer, AnnotationTypeDTO> entry : filterableAnnotationTypesOnId.entrySet() ) {
							int key = entry.getKey();
//							AnnotationTypeDTO valueTemp = entry.getValue();
							filterableAnnotationTypeIds.add( key );
						}
						log.error( "filterableAnnotationTypesOnId.remove( annotationTypeId ) == null for filterableAnnotationTypeIds type ids: " + StringUtils.join(filterableAnnotationTypeIds, ",") );
						throw new ProxlImporterInteralException(msg);
					}
					// Create DTO and save to DB
					PsmPerPeptideAnnotationDTO psmPerPeptideAnnotationDTO = new PsmPerPeptideAnnotationDTO();
					psmPerPeptideAnnotationDTO.setPsmId( psmDTO.getId() );
					psmPerPeptideAnnotationDTO.setSrchRepPeptPeptideId( srchRepPeptPeptideDTO.getId() );
					psmPerPeptideAnnotationDTO.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.FILTERABLE );
					psmPerPeptideAnnotationDTO.setAnnotationTypeId( annotationTypeId );
					psmPerPeptideAnnotationDTO.setValueDouble( value.doubleValue() );
					psmPerPeptideAnnotationDTO.setValueString( value.toString() );
					DB_Insert_PsmPerPeptideAnnotationDAO.getInstance().saveToDatabase( psmPerPeptideAnnotationDTO );
					psmPerPeptideAnnotationDTO_Filterable_List.add(psmPerPeptideAnnotationDTO);
				}
			}
		}
		if ( ! filterableAnnotationTypesOnId.isEmpty() ) {
			//  Filterable Annotations Types were not on the Filterable Annotations List
			String msg = "Not all Filterable Annotations Types were on the Filterable Annotations List for Psm Per Peptide. For Scan Number :" 
					+ psmDTO.getScanNumber();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		return psmPerPeptideAnnotationDTO_Filterable_List;
	}
	
	
	/**
	 * @param psmPeptide
	 * @param perPeptideData
	 * @param psmDTO
	 * @throws Exception
	 */
	private void saveDescriptivePsmAnnotations( PsmPeptide psmPeptide, PerPeptideData perPeptideData, PsmDTO psmDTO ) throws Exception {

		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();

		DescriptivePsmPerPeptideAnnotations descriptivePsmPerPeptideAnnotations = psmPeptide.getDescriptivePsmPerPeptideAnnotations();
		if ( descriptivePsmPerPeptideAnnotations == null ) {
//			String msg = "No Descriptive PSM annotations";
//			log.warn( msg );
		} else {
			List<DescriptivePsmPerPeptideAnnotation> descriptivePsmPerPeptideAnnotationList =
				descriptivePsmPerPeptideAnnotations.getDescriptivePsmPerPeptideAnnotation();
			if ( descriptivePsmPerPeptideAnnotationList == null || descriptivePsmPerPeptideAnnotationList.isEmpty() ) {
//				String msg = "No Descriptive PSM annotations";
//				log.warn( msg );
			} else {
				for ( DescriptivePsmPerPeptideAnnotation descriptivePsmPerPeptideAnnotation : descriptivePsmPerPeptideAnnotationList ) {
					String searchProgram = descriptivePsmPerPeptideAnnotation.getSearchProgram();
					String annotationName = descriptivePsmPerPeptideAnnotation.getAnnotationName();
					String value = descriptivePsmPerPeptideAnnotation.getValue();
					int annotationTypeId = 
							getPsmAnnotationTypeId( 
									searchProgram, 
									annotationName, 
									FilterableDescriptiveAnnotationType.DESCRIPTIVE );
					PsmPerPeptideAnnotationDTO psmPerPeptideAnnotationDTO = new PsmPerPeptideAnnotationDTO();
					psmPerPeptideAnnotationDTO.setPsmId( psmDTO.getId() );
					psmPerPeptideAnnotationDTO.setSrchRepPeptPeptideId( srchRepPeptPeptideDTO.getId() );
					psmPerPeptideAnnotationDTO.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.DESCRIPTIVE );
					psmPerPeptideAnnotationDTO.setAnnotationTypeId( annotationTypeId );
					psmPerPeptideAnnotationDTO.setValueString( value.toString() );
					DB_Insert_PsmPerPeptideAnnotationDAO.getInstance().saveToDatabase( psmPerPeptideAnnotationDTO );
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
			String msg = "Processing psmPerPeptideAnnotations: "
					+ " search_program String |"
					+ searchProgram 
					+ "| on PSM Per Peptide Annotation not found under <search_programs> ."
					+ "  This is an error in the program that generated the Proxl XML file.";
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		Map<String, AnnotationTypeDTO> psmPerPeptideAnnotationTypeDTOMap =searchProgramEntry.getPsmPerPeptideAnnotationTypeDTOMap();
		AnnotationTypeDTO annotationTypeDTO = psmPerPeptideAnnotationTypeDTOMap.get( annotationName );
		
		if ( annotationTypeDTO == null ) {
			String msg = "Processing PsmPerPeptideAnnotations: "
					+ " annotation name String |"
					+ annotationName 
					+ "| on PSM Per Peptide not found under <..._psm_per_peptide_annotation_types> under <search_programs> for search program: " + searchProgram;
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		if ( filterableDescriptiveAnnotationType != annotationTypeDTO.getFilterableDescriptiveAnnotationType() ) {
			String msg = "Processing PsmPerPeptideAnnotations: "
					+ "filterableDescriptiveAnnotationType for annotation name not same between types under <search_programs>"
					+ " and data under PSM Per Peptide."
					+ " annotation name String |"
					+ annotationName 
					+ "|.";
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		int id = annotationTypeDTO.getId();
		return id;
	}
}