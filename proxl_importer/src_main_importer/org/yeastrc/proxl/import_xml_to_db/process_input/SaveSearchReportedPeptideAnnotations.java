package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchReportedPeptideAnnotationDAO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl_import.api.xml_dto.DescriptiveReportedPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptiveReportedPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterableReportedPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterableReportedPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;

/**
 * Save Reported Peptide level annotations 
 *
 */
public class SaveSearchReportedPeptideAnnotations {

	private static final Logger log = Logger.getLogger( SaveSearchReportedPeptideAnnotations.class );
	/**
	 * private constructor
	 */
	private SaveSearchReportedPeptideAnnotations(){}
	public static SaveSearchReportedPeptideAnnotations getInstance() {
		return new SaveSearchReportedPeptideAnnotations();
	}
	
	/**
	 * @param reportedPeptide
	 * @param searchId
	 * @param reportedPeptideId
	 * @param searchProgramEntryMap
	 * @param filterableReportedPeptideAnnotationTypesOnId
	 * @return
	 * @throws Exception
	 */
	public List<SearchReportedPeptideAnnotationDTO> saveReportedPeptideAnnotations( ReportedPeptide reportedPeptide, int searchId, int reportedPeptideId, Map<String, SearchProgramEntry> searchProgramEntryMap, Map<Integer, AnnotationTypeDTO> filterableReportedPeptideAnnotationTypesOnId ) throws Exception {
		List<SearchReportedPeptideAnnotationDTO> searchReportedPeptideFilterableAnnotationDTOList = 
				saveReportedPeptideFilterableReportedPeptideAnnotations( reportedPeptide, searchId, reportedPeptideId, searchProgramEntryMap, filterableReportedPeptideAnnotationTypesOnId );
		saveReportedPeptideDescriptiveReportedPeptideAnnotations( reportedPeptide, searchId, reportedPeptideId, searchProgramEntryMap );
		return searchReportedPeptideFilterableAnnotationDTOList;
	}
	
	/**
	 * @param reportedPeptide
	 * @param searchId
	 * @param reportedPeptideId
	 * @param searchProgramEntryMap
	 * @throws Exception
	 */
	private List<SearchReportedPeptideAnnotationDTO> saveReportedPeptideFilterableReportedPeptideAnnotations( 
			ReportedPeptide reportedPeptide, 
			int searchId, 
			int reportedPeptideId, 
			Map<String, SearchProgramEntry> searchProgramEntryMap,
			Map<Integer, AnnotationTypeDTO> filterableReportedPeptideAnnotationTypesOnIdParamMasterCopy ) throws Exception {
		
		List<SearchReportedPeptideAnnotationDTO> searchReportedPeptideFilterableAnnotationDTOList = new ArrayList<>();
		//  Make local copy of filterableAnnotationTypesOnIdMasterCopy
		//    since remove entries from it.
		Map<Integer, AnnotationTypeDTO> filterableReportedPeptideAnnotationTypesOnId = new HashMap<>();
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry : filterableReportedPeptideAnnotationTypesOnIdParamMasterCopy.entrySet() ) {
			filterableReportedPeptideAnnotationTypesOnId.put( entry.getKey(), entry.getValue() );
		}
		ReportedPeptide.ReportedPeptideAnnotations reportedPeptideAnnotations =
				reportedPeptide.getReportedPeptideAnnotations();
		if ( reportedPeptideAnnotations == null ) {
			if ( ! filterableReportedPeptideAnnotationTypesOnId.isEmpty() ) {
				String msg = "No Reported Peptide Filterable annotations on this reported peptide."
						+ "  Filterable annotations are required on all reported peptides."
						+ "  ReportedPeptideString: " + reportedPeptide.getReportedPeptideString();
				log.error( msg );
				throw new ProxlImporterDataException( msg );
			} else {
//				String msg = "No Reported Peptide annotations."
//						+ "  ReportedPeptideString: " + reportedPeptide.getReportedPeptideString();
//
//				log.warn( msg );
			}
		} else {
			FilterableReportedPeptideAnnotations filterableReportedPeptideAnnotations =
					reportedPeptideAnnotations.getFilterableReportedPeptideAnnotations();
			if ( filterableReportedPeptideAnnotations == null ) {
				if ( ! filterableReportedPeptideAnnotationTypesOnId.isEmpty() ) {
					String msg = "No Filterable Reported Peptide Filterable annotations on this reported peptide."
							+ "  Filterable annotations are required on all reported peptides."
							+ "  ReportedPeptideString: " + reportedPeptide.getReportedPeptideString();
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				} else {
					String msg = "No Filterable Reported Peptide annotations."
							+ "  ReportedPeptideString: " + reportedPeptide.getReportedPeptideString();
					log.warn( msg );
				}
			} else {
				List<FilterableReportedPeptideAnnotation> filterableReportedPeptideAnnotationList =
						filterableReportedPeptideAnnotations.getFilterableReportedPeptideAnnotation();
				if ( filterableReportedPeptideAnnotationList == null || filterableReportedPeptideAnnotationList.isEmpty() ) {
					if ( ! filterableReportedPeptideAnnotationTypesOnId.isEmpty() ) {
						String msg = "No Filterable Reported Peptide Filterable annotations on this reported peptide."
								+ "  Filterable annotations are required on all reported peptides."
								+ "  ReportedPeptideString: " + reportedPeptide.getReportedPeptideString();
						log.error( msg );
						throw new ProxlImporterDataException( msg );
					} else {
						String msg = "No Filterable Reported Peptide annotations."
								+ "  ReportedPeptideString: " + reportedPeptide.getReportedPeptideString();
						log.warn( msg );
					}
				} else {
					//  Process list of filterable annotations on input list
					for ( FilterableReportedPeptideAnnotation filterableReportedPeptideAnnotation : filterableReportedPeptideAnnotationList ) {
						String searchProgram = filterableReportedPeptideAnnotation.getSearchProgram();
						String annotationName = filterableReportedPeptideAnnotation.getAnnotationName();
						BigDecimal value = filterableReportedPeptideAnnotation.getValue();
						int annotationTypeId = 
								getReportedPeptideAnnotationTypeId( 
										searchProgram, 
										annotationName, 
										FilterableDescriptiveAnnotationType.FILTERABLE, 
										searchProgramEntryMap );
						if ( filterableReportedPeptideAnnotationTypesOnId.remove( annotationTypeId ) == null ) {
							//  Shouldn't get here
							String msg = "Internal Data mismatch error";
							log.error( msg );
							log.error( "filterableReportedPeptideAnnotationTypesOnId.remove( annotationTypeId ) == null for annotationTypeId: " 
									+ annotationTypeId + ", annotationName: " + annotationName );
							List<String> filterablePsmAnnotationListNames = new ArrayList<>();
							for ( FilterableReportedPeptideAnnotation filterableReportedPeptideAnnotationTemp : filterableReportedPeptideAnnotationList ) {
								String name = filterableReportedPeptideAnnotationTemp.getAnnotationName();
								filterablePsmAnnotationListNames.add(name);
							}
							log.error( "filterableReportedPeptideAnnotationTypesOnId.remove( annotationTypeId ) == null for filterablePsmAnnotationList names: " + StringUtils.join(filterablePsmAnnotationListNames, ",") );
							List<Integer> filterableAnnotationTypeIds = new ArrayList<>();
							for ( Map.Entry<Integer, AnnotationTypeDTO> entry : filterableReportedPeptideAnnotationTypesOnId.entrySet() ) {
								int key = entry.getKey();
//								AnnotationTypeDTO valueTemp = entry.getValue();
								filterableAnnotationTypeIds.add( key );
							}
							log.error( "filterableReportedPeptideAnnotationTypesOnId.remove( annotationTypeId ) == null for filterableAnnotationTypeIds type ids: " + StringUtils.join(filterableAnnotationTypeIds, ",") );
							throw new ProxlImporterInteralException(msg);
						}
						SearchReportedPeptideAnnotationDTO searchReportedPeptideAnnotationDTO = new SearchReportedPeptideAnnotationDTO();
						searchReportedPeptideAnnotationDTO.setSearchId( searchId );
						searchReportedPeptideAnnotationDTO.setReportedPeptideId( reportedPeptideId );
						searchReportedPeptideAnnotationDTO.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.FILTERABLE );
						searchReportedPeptideAnnotationDTO.setAnnotationTypeId( annotationTypeId );
						searchReportedPeptideAnnotationDTO.setValueDouble( value.doubleValue() );
						searchReportedPeptideAnnotationDTO.setValueString( value.toString() );
						DB_Insert_SearchReportedPeptideAnnotationDAO.getInstance().saveToDatabase(searchReportedPeptideAnnotationDTO);
						searchReportedPeptideFilterableAnnotationDTOList.add(searchReportedPeptideAnnotationDTO);
					}
				}
			}
		}
		if ( ! filterableReportedPeptideAnnotationTypesOnId.isEmpty() ) {
			//  Filterable Annotations Types were not on the Filterable Annotations List
			String msg = "Not all Filterable Annotations Types were on the Filterable Annotations List "
					+ " for ReportedPeptide.  "
					+ " for reported peptide string :" 
					+ reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		return searchReportedPeptideFilterableAnnotationDTOList;
	}
	
	/**
	 * @param reportedPeptide
	 * @param searchId
	 * @param reportedPeptideId
	 * @param searchProgramEntryMap
	 * @throws Exception
	 */
	private void saveReportedPeptideDescriptiveReportedPeptideAnnotations( ReportedPeptide reportedPeptide, int searchId, int reportedPeptideId, Map<String, SearchProgramEntry> searchProgramEntryMap ) throws Exception {
		
		ReportedPeptide.ReportedPeptideAnnotations reportedPeptideAnnotations =
				reportedPeptide.getReportedPeptideAnnotations();
		if ( reportedPeptideAnnotations == null ) {
//			String msg = "No Reported Peptide annotations";
//			log.warn( msg );
		} else {
			DescriptiveReportedPeptideAnnotations descriptiveReportedPeptideAnnotations =
					reportedPeptideAnnotations.getDescriptiveReportedPeptideAnnotations();
			if ( descriptiveReportedPeptideAnnotations == null ) {
//				String msg = "No Descriptive Reported Peptide annotations";
//				log.warn( msg );
			} else {
				List<DescriptiveReportedPeptideAnnotation> descriptiveReportedPeptideAnnotationList =
						descriptiveReportedPeptideAnnotations.getDescriptiveReportedPeptideAnnotation();
				if ( descriptiveReportedPeptideAnnotationList == null || descriptiveReportedPeptideAnnotationList.isEmpty() ) {
//					String msg = "No Descriptive Reported Peptide annotations";
//					log.warn( msg );
				} else {
					for ( DescriptiveReportedPeptideAnnotation descriptiveReportedPeptideAnnotation : descriptiveReportedPeptideAnnotationList ) {
						String searchProgram = descriptiveReportedPeptideAnnotation.getSearchProgram();
						String annotationName = descriptiveReportedPeptideAnnotation.getAnnotationName();
						String value = descriptiveReportedPeptideAnnotation.getValue();
						int descriptiveAnnotationTypeId = 
								getReportedPeptideAnnotationTypeId( 
										searchProgram, 
										annotationName, 
										FilterableDescriptiveAnnotationType.DESCRIPTIVE, 
										searchProgramEntryMap );
						SearchReportedPeptideAnnotationDTO searchReportedPeptideAnnotationDTO = new SearchReportedPeptideAnnotationDTO();
						searchReportedPeptideAnnotationDTO.setSearchId( searchId );
						searchReportedPeptideAnnotationDTO.setReportedPeptideId( reportedPeptideId );
						searchReportedPeptideAnnotationDTO.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.DESCRIPTIVE );
						searchReportedPeptideAnnotationDTO.setAnnotationTypeId( descriptiveAnnotationTypeId );
						searchReportedPeptideAnnotationDTO.setValueString( value );
						DB_Insert_SearchReportedPeptideAnnotationDAO.getInstance().saveToDatabase(searchReportedPeptideAnnotationDTO);
					}
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
	private int getReportedPeptideAnnotationTypeId( 
			String searchProgram, 
			String annotationName, 
			FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType,
			Map<String, SearchProgramEntry> searchProgramEntryMap ) throws ProxlImporterDataException {
		
		SearchProgramEntry searchProgramEntry =
				searchProgramEntryMap.get( searchProgram );
		if ( searchProgramEntry == null ) {
			String msg = "Processing filterableReportedPeptideAnnotations: "
					+ " search_program String |"
					+ searchProgram 
					+ "| on Reported Peptide not found under <search_programs> .";
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		Map<String, AnnotationTypeDTO> reportedPeptideAnnotationTypeDTOMap =
				searchProgramEntry.getReportedPeptideAnnotationTypeDTOMap();
		AnnotationTypeDTO reportedPeptideAnnotationTypeDTO = 
				reportedPeptideAnnotationTypeDTOMap.get( annotationName );
		if ( reportedPeptideAnnotationTypeDTO == null ) {
			String msg = "Processing filterableReportedPeptideAnnotations: "
					+ " annotation name String |"
					+ annotationName 
					+ "| on Reported Peptide not found under <filterable_peptide_annotation_types> under <search_programs> for search program: " + searchProgram;
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		if ( filterableDescriptiveAnnotationType != reportedPeptideAnnotationTypeDTO.getFilterableDescriptiveAnnotationType() ) {
			String msg = "Processing Reported PeptideAnnotations: "
					+ "filterableDescriptiveAnnotationType for annotation name not same between types under <search_programs>"
					+ " and data under Reported Peptide."
					+ " annotation name String |"
					+ annotationName 
					+ "|.";
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		int id = reportedPeptideAnnotationTypeDTO.getId();
		return id;
	}
}