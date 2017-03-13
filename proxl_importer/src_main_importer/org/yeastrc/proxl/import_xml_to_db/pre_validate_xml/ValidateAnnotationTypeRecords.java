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

/**
 * 
 *
 */
public class ValidateAnnotationTypeRecords {
	
	private static final Logger log = Logger.getLogger( ValidateAnnotationTypeRecords.class );
	private ValidateAnnotationTypeRecords() { }
	public static ValidateAnnotationTypeRecords getInstance() {
		return new ValidateAnnotationTypeRecords();
	}
	
	/**
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	public void validateAnnotationTypeRecords( ProxlInput proxlInput ) throws ProxlImporterDataException {
		validateAnnotationNamesUniqueWithinSearchProgramAndType( proxlInput );
		validatePresenceOfPeptideAndPSMAnnotationTypes( proxlInput );
	}
	
	/**
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	private void validateAnnotationNamesUniqueWithinSearchProgramAndType( ProxlInput proxlInput ) throws ProxlImporterDataException {
		SearchProgramInfo searchProgramInfo = proxlInput.getSearchProgramInfo(); 
		SearchPrograms proxlInputSearchPrograms = searchProgramInfo.getSearchPrograms();
		List<SearchProgram> searchProgramList =
				proxlInputSearchPrograms.getSearchProgram();
		for ( SearchProgram searchProgram : searchProgramList ) {
			validateReportedPeptideAnnotationNamesUniqueWithinSearchProgramAndType( searchProgram );
			validatePsmAnnotationNamesUniqueWithinSearchProgramAndType( searchProgram );
		}
	}
	
	/**
	 * validate Reported Peptide Annotation Types
	 * 
	 * @param searchProgram
	 * @throws ProxlImporterDataException 
	 */
	private void validateReportedPeptideAnnotationNamesUniqueWithinSearchProgramAndType( SearchProgram searchProgram ) throws ProxlImporterDataException {
		Set<String> annotationNames = new HashSet<>();
		SearchProgram.ReportedPeptideAnnotationTypes reportedPeptideAnnotationTypes =
				searchProgram.getReportedPeptideAnnotationTypes();
		if ( reportedPeptideAnnotationTypes == null ) {
			if ( log.isInfoEnabled() ) {
				String msg = "No Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
				log.info(msg);
			}
			return;
		}
		//////	Filterable Peptide Annotations
		FilterablePeptideAnnotationTypes filterablePeptideAnnotationTypes =
				reportedPeptideAnnotationTypes.getFilterablePeptideAnnotationTypes();
		if ( filterablePeptideAnnotationTypes == null ) {
			if ( log.isInfoEnabled() ) {
				String msg = "No Filterable Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
				log.info(msg);
			}
		} else {
			List<FilterablePeptideAnnotationType> filterablePeptideAnnotationTypeList =
					filterablePeptideAnnotationTypes.getFilterablePeptideAnnotationType();
			if ( filterablePeptideAnnotationTypeList == null || filterablePeptideAnnotationTypeList.isEmpty() ) {
				if ( log.isInfoEnabled() ) {
					String msg = "No Filterable Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
					log.info(msg);
				}
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
			if ( log.isInfoEnabled() ) {
				String msg = "No Descriptive Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
				log.info(msg);
			}
		} else {
			List<DescriptivePeptideAnnotationType> descriptivePeptideAnnotationTypeList =
					descriptivePeptideAnnotationTypes.getDescriptivePeptideAnnotationType();
			if ( descriptivePeptideAnnotationTypeList == null || descriptivePeptideAnnotationTypeList.isEmpty() ) {
				if ( log.isInfoEnabled() ) {
					String msg = "No Descriptive Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
					log.info(msg);
				}
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
	private void validatePsmAnnotationNamesUniqueWithinSearchProgramAndType( SearchProgram searchProgram ) throws ProxlImporterDataException {
		Set<String> annotationNames = new HashSet<>();
		SearchProgram.PsmAnnotationTypes psmAnnotationTypes =
				searchProgram.getPsmAnnotationTypes();
		if ( psmAnnotationTypes == null ) {
			if ( log.isInfoEnabled() ) {
				String msg = "No Psm Annotation Types for search program name: " + searchProgram.getName();
				log.info(msg);
			}
			return;
		}
		//////	Filterable Psm Annotations
		FilterablePsmAnnotationTypes filterablePsmAnnotationTypes =
				psmAnnotationTypes.getFilterablePsmAnnotationTypes();
		if ( filterablePsmAnnotationTypes == null ) {
			if ( log.isInfoEnabled() ) {
				String msg = "No Filterable Psm Annotation Types for search program name: " + searchProgram.getName();
				log.info(msg);
			}
		} else {
			List<FilterablePsmAnnotationType> filterablePsmAnnotationTypeList =
					filterablePsmAnnotationTypes.getFilterablePsmAnnotationType();
			if ( filterablePsmAnnotationTypeList == null || filterablePsmAnnotationTypeList.isEmpty() ) {
				if ( log.isInfoEnabled() ) {
					String msg = "No Filterable Psm Annotation Types for search program name: " + searchProgram.getName();
					log.info(msg);
				}
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
			if ( log.isInfoEnabled() ) {
				String msg = "No Descriptive Psm Annotation Types for search program name: " + searchProgram.getName();
				log.info(msg);
			}
		} else {
			List<DescriptivePsmAnnotationType> descriptivePsmAnnotationTypeList =
					descriptivePsmAnnotationTypes.getDescriptivePsmAnnotationType();
			if ( descriptivePsmAnnotationTypeList == null || descriptivePsmAnnotationTypeList.isEmpty() ) {
				if ( log.isInfoEnabled() ) {
					String msg = "No Descriptive Psm Annotation Types for search program name: " + searchProgram.getName();
					log.info(msg);
				}
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
	
	////////////////////////////////
	/**
	 * Validate presence of Peptide and PSM Annotation Types
	 * 
	 * Throw ProxlImporterDataException if:
	 *   No PSM filterable annotation types.
	 *   At least one PSM filterable  annotation type but none of them are a default filter.
	 *   At least one Peptide filterable  annotation type but none of them are a default filter.
	 *   
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	private void validatePresenceOfPeptideAndPSMAnnotationTypes( ProxlInput proxlInput ) throws ProxlImporterDataException {
		boolean foundPeptideFilterableAnnotationType = false;
		boolean foundPeptideDefaultFilterableAnnotationType = false;
		boolean foundPsmFilterableAnnotationType = false;
		boolean foundPsmDefaultFilterableAnnotationType = false;
		SearchProgramInfo searchProgramInfo = proxlInput.getSearchProgramInfo(); 
		SearchPrograms proxlInputSearchPrograms = searchProgramInfo.getSearchPrograms();
		List<SearchProgram> searchProgramList =
				proxlInputSearchPrograms.getSearchProgram();
		///////////////////////
		//   Process Peptide Annotation Types
		for ( SearchProgram searchProgram : searchProgramList ) {
			SearchProgram.ReportedPeptideAnnotationTypes reportedPeptideAnnotationTypes =
					searchProgram.getReportedPeptideAnnotationTypes();
			if ( reportedPeptideAnnotationTypes == null ) {
				continue;  //  EARLY CONTINUE
			}
			//////	Filterable Peptide Annotations
			FilterablePeptideAnnotationTypes filterablePeptideAnnotationTypes =
					reportedPeptideAnnotationTypes.getFilterablePeptideAnnotationTypes();
			if ( filterablePeptideAnnotationTypes == null ) {
				continue;  //  EARLY CONTINUE
			}
			List<FilterablePeptideAnnotationType> filterablePeptideAnnotationTypeList =
					filterablePeptideAnnotationTypes.getFilterablePeptideAnnotationType();
			if ( filterablePeptideAnnotationTypeList == null || filterablePeptideAnnotationTypeList.isEmpty() ) {
				continue;  //  EARLY CONTINUE
			}
			for ( FilterablePeptideAnnotationType filterablePeptideAnnotationType : filterablePeptideAnnotationTypeList ) {
				foundPeptideFilterableAnnotationType = true;
				if ( filterablePeptideAnnotationType.isDefaultFilter() ) {
					foundPeptideDefaultFilterableAnnotationType = true;
					break;
				}
			}
		}
		///////////////////////
		//   Process PSM Annotation Types
		for ( SearchProgram searchProgram : searchProgramList ) {
			SearchProgram.PsmAnnotationTypes psmAnnotationTypes =
					searchProgram.getPsmAnnotationTypes();
			if ( psmAnnotationTypes == null ) {
				continue;  //  EARLY CONTINUE
			}
			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes =
					psmAnnotationTypes.getFilterablePsmAnnotationTypes();
			if ( filterablePsmAnnotationTypes == null ) {
				continue;  //  EARLY CONTINUE
			}
			List<FilterablePsmAnnotationType> filterablePsmAnnotationTypeList =
					filterablePsmAnnotationTypes.getFilterablePsmAnnotationType();
			if ( filterablePsmAnnotationTypeList == null || filterablePsmAnnotationTypeList.isEmpty() ) {
				continue;  //  EARLY CONTINUE
			}
			for ( FilterablePsmAnnotationType filterablePsmAnnotationType : filterablePsmAnnotationTypeList ) {
				foundPsmFilterableAnnotationType = true;
				if ( filterablePsmAnnotationType.isDefaultFilter() ) {
					foundPsmDefaultFilterableAnnotationType = true;
					break;
				}
			}
		}
		//  Not really needed since covered by XSD validation
		if ( ! foundPsmFilterableAnnotationType ) {
			String msg = "At least one PSM Filterable Annotation Type is required.";
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
		//  Not covered by XSD validation
		if ( ! foundPsmDefaultFilterableAnnotationType ) {
			String msg = "At least one PSM Filterable Annotation Type is required to be a default filter.";
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
		//  Not covered by XSD validation
		if ( foundPeptideFilterableAnnotationType && ( ! foundPeptideDefaultFilterableAnnotationType ) ) {
			String msg = "There is at least one Peptide Filterable Annotation Type "
					+ " but none of theem is a default filter.";
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
	}
}