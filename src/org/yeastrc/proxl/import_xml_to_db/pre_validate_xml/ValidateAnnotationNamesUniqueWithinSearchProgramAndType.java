package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.SearchPrograms;



public class ValidateAnnotationNamesUniqueWithinSearchProgramAndType {
	
	private static final Logger log = Logger.getLogger( ValidateAnnotationNamesUniqueWithinSearchProgramAndType.class );

	private ValidateAnnotationNamesUniqueWithinSearchProgramAndType() { }
	
	public static ValidateAnnotationNamesUniqueWithinSearchProgramAndType getInstance() {
		
		return new ValidateAnnotationNamesUniqueWithinSearchProgramAndType();
	}

	
	/**
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	public void validateAnnotationNamesUniqueWithinSearchProgramAndType( ProxlInput proxlInput ) throws ProxlImporterDataException {
		

		SearchProgramInfo searchProgramInfo = proxlInput.getSearchProgramInfo(); 
		

		
		SearchPrograms proxlInputSearchPrograms = searchProgramInfo.getSearchPrograms();
		
		List<SearchProgram> searchProgramList =
				proxlInputSearchPrograms.getSearchProgram();
		
		for ( SearchProgram searchProgram : searchProgramList ) {
						
			validateReportedPeptideAnnotationTypes( searchProgram );

			validatePsmAnnotationTypes( searchProgram );
		}
		
	}
	
	/**
	 * validate Reported Peptide Annotation Types
	 * 
	 * @param searchProgram
	 * @throws ProxlImporterDataException 
	 */
	private void validateReportedPeptideAnnotationTypes( SearchProgram searchProgram ) throws ProxlImporterDataException {
		
		Set<String> annotationNames = new HashSet<>();
		
		SearchProgram.ReportedPeptideAnnotationTypes reportedPeptideAnnotationTypes =
				searchProgram.getReportedPeptideAnnotationTypes();
		
		if ( reportedPeptideAnnotationTypes == null ) {
			
			String msg = "No Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);
			return;
		}

		//////	Filterable Peptide Annotations
		
		FilterablePeptideAnnotationTypes filterablePeptideAnnotationTypes =
				reportedPeptideAnnotationTypes.getFilterablePeptideAnnotationTypes();

		if ( filterablePeptideAnnotationTypes == null ) {
			
			String msg = "No Filterable Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);
			
		} else {

			List<FilterablePeptideAnnotationType> filterablePeptideAnnotationTypeList =
					filterablePeptideAnnotationTypes.getFilterablePeptideAnnotationType();

			if ( filterablePeptideAnnotationTypeList == null || filterablePeptideAnnotationTypeList.isEmpty() ) {

				String msg = "No Filterable Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
				log.warn(msg);

			} else {

				for ( FilterablePeptideAnnotationType filterablePeptideAnnotationType : filterablePeptideAnnotationTypeList ) {

					String annotationName = filterablePeptideAnnotationType.getName();

					if ( ! annotationNames.add( annotationName ) ) {

						String msg = "Annotation name '" + annotationName + "'"
								+ " occurs more than once for Reported Peptide annotation types for search program  "
								+ "'" + searchProgram.getName() + "'.";

						log.error( msg );

						throw new ProxlImporterDataException( msg );
					}
				}
			}
		}
		
		
		////////   Descriptive Peptide Annotations

		DescriptivePeptideAnnotationTypes descriptivePeptideAnnotationTypes =
				reportedPeptideAnnotationTypes.getDescriptivePeptideAnnotationTypes();

		if ( descriptivePeptideAnnotationTypes == null ) {
			
			String msg = "No Descriptive Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);

		} else {

			List<DescriptivePeptideAnnotationType> descriptivePeptideAnnotationTypeList =
					descriptivePeptideAnnotationTypes.getDescriptivePeptideAnnotationType();

			if ( descriptivePeptideAnnotationTypeList == null || descriptivePeptideAnnotationTypeList.isEmpty() ) {

				String msg = "No Descriptive Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
				log.warn(msg);

			} else {

				for ( DescriptivePeptideAnnotationType descriptivePeptideAnnotationType : descriptivePeptideAnnotationTypeList ) {

					String annotationName = descriptivePeptideAnnotationType.getName();

					if ( ! annotationNames.add( annotationName ) ) {

						String msg = "Annotation name '" + annotationName + "'"
								+ " occurs more than once for Reported Peptide annotation types for search program  "
								+ "'" + searchProgram.getName() + "'.";

						log.error( msg );

						throw new ProxlImporterDataException( msg );
					}
				}
			}
		}
	}
	


	/**
	 *  validate Psm Annotation Types
	 *  
	 * @param searchProgram
	 * @throws ProxlImporterDataException 
	 */
	private void validatePsmAnnotationTypes( SearchProgram searchProgram ) throws ProxlImporterDataException {
		
		Set<String> annotationNames = new HashSet<>();
		
		SearchProgram.PsmAnnotationTypes psmAnnotationTypes =
				searchProgram.getPsmAnnotationTypes();
		

		if ( psmAnnotationTypes == null ) {
			
			String msg = "No Psm Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);
			return;
		}


		//////	Filterable Psm Annotations
		
		
		FilterablePsmAnnotationTypes filterablePsmAnnotationTypes =
				psmAnnotationTypes.getFilterablePsmAnnotationTypes();

		if ( filterablePsmAnnotationTypes == null ) {
			
			String msg = "No Filterable Psm Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);
			
		} else {

			List<FilterablePsmAnnotationType> filterablePsmAnnotationTypeList =
					filterablePsmAnnotationTypes.getFilterablePsmAnnotationType();

			if ( filterablePsmAnnotationTypeList == null || filterablePsmAnnotationTypeList.isEmpty() ) {

				String msg = "No Filterable Psm Annotation Types for search program name: " + searchProgram.getName();
				log.warn(msg);

			} else {

				for ( FilterablePsmAnnotationType filterablePsmAnnotationType : filterablePsmAnnotationTypeList ) {

					String annotationName = filterablePsmAnnotationType.getName();

					if ( ! annotationNames.add( annotationName ) ) {

						String msg = "Annotation name '" + annotationName + "'"
								+ " occurs more than once for Psm annotation types for search program  "
								+ "'" + searchProgram.getName() + "'.";

						log.error( msg );

						throw new ProxlImporterDataException( msg );
					}
				}
			}
		}
		

		////////   Descriptive Psm Annotations

		DescriptivePsmAnnotationTypes descriptivePsmAnnotationTypes =
				psmAnnotationTypes.getDescriptivePsmAnnotationTypes();

		if ( descriptivePsmAnnotationTypes == null ) {
			
			String msg = "No Descriptive Psm Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);
			
		} else {

			List<DescriptivePsmAnnotationType> descriptivePsmAnnotationTypeList =
					descriptivePsmAnnotationTypes.getDescriptivePsmAnnotationType();

			if ( descriptivePsmAnnotationTypeList == null || descriptivePsmAnnotationTypeList.isEmpty() ) {

				String msg = "No Descriptive Psm Annotation Types for search program name: " + searchProgram.getName();
				log.warn(msg);

			} else {

				for ( DescriptivePsmAnnotationType descriptivePsmAnnotationType : descriptivePsmAnnotationTypeList ) {

					String annotationName = descriptivePsmAnnotationType.getName();

					if ( ! annotationNames.add( annotationName ) ) {

						String msg = "Annotation name '" + annotationName + "'"
								+ " occurs more than once for Psm annotation types for search program  "
								+ "'" + searchProgram.getName() + "'.";

						log.error( msg );

						throw new ProxlImporterDataException( msg );
					}
				}
			}
		}
	}

}
