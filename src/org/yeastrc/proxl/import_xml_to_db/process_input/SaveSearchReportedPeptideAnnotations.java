package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl_import.api.xml_dto.DescriptiveReportedPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptiveReportedPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.FilterableReportedPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterableReportedPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.xlink.dao.SearchReportedPeptideAnnotationDAO;
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
	
	public void saveReportedPeptideAnnotations( ReportedPeptide reportedPeptide, int searchId, int reportedPeptideId, Map<String, SearchProgramEntry> searchProgramEntryMap ) throws Exception {
		
		saveReportedPeptideFilterableReportedPeptideAnnotations( reportedPeptide, searchId, reportedPeptideId, searchProgramEntryMap );
		
		saveReportedPeptideDescriptiveReportedPeptideAnnotations( reportedPeptide, searchId, reportedPeptideId, searchProgramEntryMap );
	}

	
	

	/**
	 * @param reportedPeptide
	 * @param searchId
	 * @param reportedPeptideId
	 * @param searchProgramEntryMap
	 * @throws Exception
	 */
	private void saveReportedPeptideFilterableReportedPeptideAnnotations( ReportedPeptide reportedPeptide, int searchId, int reportedPeptideId, Map<String, SearchProgramEntry> searchProgramEntryMap ) throws Exception {


		///  Build list of Filterable annotation type ids
		
		Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesOnId = new HashMap<>();
		
		for ( Map.Entry<String, SearchProgramEntry> searchProgramEntryMapEntry : searchProgramEntryMap.entrySet() ) {

			SearchProgramEntry searchProgramEntry = searchProgramEntryMapEntry.getValue();

			Map<String, AnnotationTypeDTO> reportedPeptideAnnotationTypeDTOMap =
					searchProgramEntry.getReportedPeptideAnnotationTypeDTOMap();
		
			for ( Map.Entry<String, AnnotationTypeDTO> reportedPeptideAnnotationTypeDTOMapEntry : reportedPeptideAnnotationTypeDTOMap.entrySet() ) {

				AnnotationTypeDTO reportedPeptideAnnotationTypeDTO = reportedPeptideAnnotationTypeDTOMapEntry.getValue();
		
				 if ( reportedPeptideAnnotationTypeDTO.getFilterableDescriptiveAnnotationType()
						 == FilterableDescriptiveAnnotationType.FILTERABLE ) {
				 
					 filterableAnnotationTypesOnId.put( reportedPeptideAnnotationTypeDTO.getId(), reportedPeptideAnnotationTypeDTO );
				 }
				
			}
		}
		
		


		ReportedPeptide.ReportedPeptideAnnotations reportedPeptideAnnotations =
				reportedPeptide.getReportedPeptideAnnotations();

		if ( reportedPeptideAnnotations == null ) {
			
			if ( ! filterableAnnotationTypesOnId.isEmpty() ) {
			
				String msg = "No Reported Peptide Filterable annotations on this reported peptide."
						+ "  Filterable annotations are required on all reported peptides."
						+ "  ReportedPeptideString: " + reportedPeptide.getReportedPeptideString();
				log.error( msg );
				throw new ProxlImporterDataException( msg );
			
			} else {

				String msg = "No Reported Peptide annotations."
						+ "  ReportedPeptideString: " + reportedPeptide.getReportedPeptideString();

				log.warn( msg );
			}
			
		} else {

			FilterableReportedPeptideAnnotations filterableReportedPeptideAnnotations =
					reportedPeptideAnnotations.getFilterableReportedPeptideAnnotations();


			if ( filterableReportedPeptideAnnotations == null ) {

				if ( ! filterableAnnotationTypesOnId.isEmpty() ) {
					

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

					if ( ! filterableAnnotationTypesOnId.isEmpty() ) {


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
						
						if ( filterableAnnotationTypesOnId.remove( annotationTypeId ) == null ) {

							//  Shouldn't get here
							
							String msg = "Internal Data mismatch error";
							
							log.error( msg );
							
							log.error( "filterableAnnotationTypesOnId.remove( annotationTypeId ) == null for annotationTypeId: " 
									+ annotationTypeId + ", annotationName: " + annotationName );

							List<String> filterablePsmAnnotationListNames = new ArrayList<>();
							
							for ( FilterableReportedPeptideAnnotation filterableReportedPeptideAnnotationTemp : filterableReportedPeptideAnnotationList ) {

								String name = filterableReportedPeptideAnnotationTemp.getAnnotationName();
								
								filterablePsmAnnotationListNames.add(name);
							}

							log.error( "filterableAnnotationTypesOnId.remove( annotationTypeId ) == null for filterablePsmAnnotationList names: " + StringUtils.join(filterablePsmAnnotationListNames, ",") );


							List<Integer> filterableAnnotationTypeIds = new ArrayList<>();
							
							for ( Map.Entry<Integer, AnnotationTypeDTO> entry : filterableAnnotationTypesOnId.entrySet() ) {
								
								int key = entry.getKey();
//								AnnotationTypeDTO valueTemp = entry.getValue();
								
								filterableAnnotationTypeIds.add( key );
							}

							log.error( "filterableAnnotationTypesOnId.remove( annotationTypeId ) == null for filterableAnnotationTypeIds type ids: " + StringUtils.join(filterableAnnotationTypeIds, ",") );

							
							throw new ProxlImporterInteralException(msg);
						}
						
						SearchReportedPeptideAnnotationDTO searchReportedPeptideAnnotationDTO = new SearchReportedPeptideAnnotationDTO();

						searchReportedPeptideAnnotationDTO.setSearchId( searchId );
						searchReportedPeptideAnnotationDTO.setReportedPeptideId( reportedPeptideId );
						searchReportedPeptideAnnotationDTO.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.FILTERABLE );
						searchReportedPeptideAnnotationDTO.setAnnotationTypeId( annotationTypeId );
						searchReportedPeptideAnnotationDTO.setValueDouble( value.doubleValue() );
						searchReportedPeptideAnnotationDTO.setValueString( value.toString() );

						SearchReportedPeptideAnnotationDAO.getInstance().saveToDatabase(searchReportedPeptideAnnotationDTO);
						
					}
				}
			}
		}
		
		if ( ! filterableAnnotationTypesOnId.isEmpty() ) {
			
			//  Filterable Annotations Types were not on the Filterable Annotations List
			
			String msg = "Not all Filterable Annotations Types were on the Filterable Annotations List "
					+ " for ReportedPeptide.  "
					+ " for reported peptide string :" 
					+ reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
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
			
			String msg = "No Reported Peptide annotations";
			log.warn( msg );
			
		} else {

			DescriptiveReportedPeptideAnnotations descriptiveReportedPeptideAnnotations =
					reportedPeptideAnnotations.getDescriptiveReportedPeptideAnnotations();

			if ( descriptiveReportedPeptideAnnotations == null ) {

				String msg = "No Descriptive Reported Peptide annotations";
				log.warn( msg );

			} else {


				List<DescriptiveReportedPeptideAnnotation> descriptiveReportedPeptideAnnotationList =
						descriptiveReportedPeptideAnnotations.getDescriptiveReportedPeptideAnnotation();


				if ( descriptiveReportedPeptideAnnotationList == null || descriptiveReportedPeptideAnnotationList.isEmpty() ) {

					String msg = "No Descriptive Reported Peptide annotations";
					log.warn( msg );

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


						SearchReportedPeptideAnnotationDAO.getInstance().saveToDatabase(searchReportedPeptideAnnotationDTO);
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
			
			String msg = "Processing filterablePsmAnnotations: "
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
			
			String msg = "Processing filterablePsmAnnotations: "
					+ " annotation name String |"
					+ annotationName 
					+ "| on Reported Peptide not found under <filterable_psm_annotation_types> under <search_programs> for search program: " + searchProgram;
			
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