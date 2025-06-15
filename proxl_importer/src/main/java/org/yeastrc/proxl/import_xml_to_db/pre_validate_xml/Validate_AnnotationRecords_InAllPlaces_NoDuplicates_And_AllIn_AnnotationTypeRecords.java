package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.proxl.import_xml_to_db.exception.ProxlImporterInternalException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptiveReportedPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterableReportedPeptideAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.SearchPrograms;

/**
 * Validate the Annotation <annotation > values are in the Annotation Types and no duplicates.
 * 
 * Sort of Duplicate check in XSD to remove from XSD due to performance issues.
 *
 */
public class Validate_AnnotationRecords_InAllPlaces_NoDuplicates_And_AllIn_AnnotationTypeRecords {

	private static final Logger log = LoggerFactory.getLogger( Validate_AnnotationRecords_InAllPlaces_NoDuplicates_And_AllIn_AnnotationTypeRecords.class );
	
	private Validate_AnnotationRecords_InAllPlaces_NoDuplicates_And_AllIn_AnnotationTypeRecords() { }
	public static Validate_AnnotationRecords_InAllPlaces_NoDuplicates_And_AllIn_AnnotationTypeRecords getInstance() {
		return new Validate_AnnotationRecords_InAllPlaces_NoDuplicates_And_AllIn_AnnotationTypeRecords();
	}
	
	/**
	 * Validate the Annotation <annotation > values are in the Annotation Types and no duplicates.
	 * 
	 * @param proxlInput
	 * @throws ProxlImporterInternalException 
	 * @throws Validate_AnnotationRecords_InAllPlaces_NoDuplicates_And_AllIn_AnnotationTypeRecords for data errors
	 */
	public void validate_AnnotationRecords_InAllPlaces_NoDuplicates_And_AllIn_AnnotationTypeRecords( ProxlInput proxlInput ) throws ProxlImporterDataException, ProxlImporterInternalException {
		
		Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Root root_SearchProgram_AndChildren = get_Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Root( proxlInput );
	
		ReportedPeptides reportedPeptides = proxlInput.getReportedPeptides();
		if ( reportedPeptides != null ) {
			List<ReportedPeptide> reportedPeptideList =
					reportedPeptides.getReportedPeptide();
			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {
								
				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
					
					process_Single_ReportedPeptide( reportedPeptide, root_SearchProgram_AndChildren );
				}
			}
		}
	}
	
	/**
	 * @param reportedPeptide
	 * @param root_SearchProgram_AndChildren
	 * @throws ProxlImporterDataException
	 */
	private void process_Single_ReportedPeptide( ReportedPeptide reportedPeptide, Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Root root_SearchProgram_AndChildren ) throws ProxlImporterDataException {
		
		process_Single_ReportedPeptide_Annotations( reportedPeptide, root_SearchProgram_AndChildren );

		if ( reportedPeptide.getPsms() != null && reportedPeptide.getPsms().getPsm() != null ) {
			for ( Psm psm : reportedPeptide.getPsms().getPsm() ) {

				process_Single_Psm_RootLevelAnnotations( psm, root_SearchProgram_AndChildren );
			}
		}
	}

	/**
	 * @param reportedPeptide
	 * @param root_SearchProgram_AndChildren
	 * @throws ProxlImporterDataException
	 */
	private void process_Single_ReportedPeptide_Annotations( ReportedPeptide reportedPeptide, Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Root root_SearchProgram_AndChildren ) throws ProxlImporterDataException {
		
		if ( reportedPeptide.getReportedPeptideAnnotations() != null ) {
			
			if ( reportedPeptide.getReportedPeptideAnnotations().getFilterableReportedPeptideAnnotations() != null 
					&& reportedPeptide.getReportedPeptideAnnotations().getFilterableReportedPeptideAnnotations().getFilterableReportedPeptideAnnotation() != null ) {
				
				Map<String, Set<String>> annotationNames_Set_Map_Key_SearchProgramName = new HashMap<>();
				
				for ( FilterableReportedPeptideAnnotation annotation : reportedPeptide.getReportedPeptideAnnotations().getFilterableReportedPeptideAnnotations().getFilterableReportedPeptideAnnotation() ) {
					
					Set<String> annotationNames_Set_For_SearchProgramName = annotationNames_Set_Map_Key_SearchProgramName.get( annotation.getSearchProgram() );
					if ( annotationNames_Set_For_SearchProgramName == null ) {
						annotationNames_Set_For_SearchProgramName = new HashSet<>();
						annotationNames_Set_Map_Key_SearchProgramName.put( annotation.getSearchProgram(), annotationNames_Set_For_SearchProgramName );
					}

					if ( ! annotationNames_Set_For_SearchProgramName.add( annotation.getAnnotationName() ) ) {
						String msg = "The 'search_program' / 'annotation_name' attribute pair on <filterable_reported_peptide_annotation> on <reported_peptide> element has duplicate which is not allowed. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName() 
								+ ",  reported_peptide_string: " +  reportedPeptide.getReportedPeptideString();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}

					Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Single_SearchProgram_AndChildren single_SearchProgram_AndChildren = 
							root_SearchProgram_AndChildren.searchProgram_AndChildren_Map.get( annotation.getSearchProgram() );
					
					if ( single_SearchProgram_AndChildren == null ) {
						String msg = "The 'search_program' attribute on <filterable_reported_peptide_annotation> on <reported_peptide> element is not found under <search_program_info>. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName() 
								+ ",  reported_peptide_string: " +  reportedPeptide.getReportedPeptideString();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}
					
					if ( ! single_SearchProgram_AndChildren.filterableReportedPeptideAnnotationType_Names.contains( annotation.getAnnotationName() ) ) {
						String msg = "The 'search_program' / 'annotation_name' attribute pair on <filterable_reported_peptide_annotation> on <reported_peptide> element is not found under <search_program_info>. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName() 
								+ ",  reported_peptide_string: " +  reportedPeptide.getReportedPeptideString();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}
				}
			}

			if ( reportedPeptide.getReportedPeptideAnnotations().getDescriptiveReportedPeptideAnnotations() != null 
					&& reportedPeptide.getReportedPeptideAnnotations().getDescriptiveReportedPeptideAnnotations().getDescriptiveReportedPeptideAnnotation() != null ) {
				
				Map<String, Set<String>> annotationNames_Set_Map_Key_SearchProgramName = new HashMap<>();
				
				for ( DescriptiveReportedPeptideAnnotation annotation : reportedPeptide.getReportedPeptideAnnotations().getDescriptiveReportedPeptideAnnotations().getDescriptiveReportedPeptideAnnotation() ) {
					
					Set<String> annotationNames_Set_For_SearchProgramName = annotationNames_Set_Map_Key_SearchProgramName.get( annotation.getSearchProgram() );
					if ( annotationNames_Set_For_SearchProgramName == null ) {
						annotationNames_Set_For_SearchProgramName = new HashSet<>();
						annotationNames_Set_Map_Key_SearchProgramName.put( annotation.getSearchProgram(), annotationNames_Set_For_SearchProgramName );
					}

					if ( ! annotationNames_Set_For_SearchProgramName.add( annotation.getAnnotationName() ) ) {
						String msg = "The 'search_program' / 'annotation_name' attribute pair on <descriptive_reported_peptide_annotation> on <reported_peptide> element has duplicate which is not allowed. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName() 
								+ ",  reported_peptide_string: " +  reportedPeptide.getReportedPeptideString();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}

					Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Single_SearchProgram_AndChildren single_SearchProgram_AndChildren = 
							root_SearchProgram_AndChildren.searchProgram_AndChildren_Map.get( annotation.getSearchProgram() );
					
					if ( single_SearchProgram_AndChildren == null ) {
						String msg = "The 'search_program' attribute on <descriptive_reported_peptide_annotation> on <reported_peptide> element is not found under <search_program_info>. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName() 
								+ ",  reported_peptide_string: " +  reportedPeptide.getReportedPeptideString();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}
					
					if ( ! single_SearchProgram_AndChildren.descriptiveReportedPeptideAnnotationType_Names.contains( annotation.getAnnotationName() ) ) {
						String msg = "The 'search_program' / 'annotation_name' attribute pair on <descriptive_reported_peptide_annotation> on <reported_peptide> element is not found under <search_program_info>. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName() 
								+ ",  reported_peptide_string: " +  reportedPeptide.getReportedPeptideString();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}
				}
			}

		}

	}

	/**
	 * @param psm
	 * @throws ProxlImporterDataException
	 */
	private void process_Single_Psm_RootLevelAnnotations( Psm psm, Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Root root_SearchProgram_AndChildren ) throws ProxlImporterDataException {

		
		if ( psm != null ) {
			
			if ( psm.getFilterablePsmAnnotations() != null 
					&& psm.getFilterablePsmAnnotations().getFilterablePsmAnnotation() != null ) {
				
				Map<String, Set<String>> annotationNames_Set_Map_Key_SearchProgramName = new HashMap<>();
				
				for ( FilterablePsmAnnotation annotation : psm.getFilterablePsmAnnotations().getFilterablePsmAnnotation() ) {
					
					Set<String> annotationNames_Set_For_SearchProgramName = annotationNames_Set_Map_Key_SearchProgramName.get( annotation.getSearchProgram() );
					if ( annotationNames_Set_For_SearchProgramName == null ) {
						annotationNames_Set_For_SearchProgramName = new HashSet<>();
						annotationNames_Set_Map_Key_SearchProgramName.put( annotation.getSearchProgram(), annotationNames_Set_For_SearchProgramName );
					}

					if ( ! annotationNames_Set_For_SearchProgramName.add( annotation.getAnnotationName() ) ) {
						String msg = "The 'search_program' / 'annotation_name' attribute pair on <filterable_psm_annotation> on <psm> element has duplicate which is not allowed. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName()
								+ ",  scan_number: " +  psm.getScanNumber()
								+ ",  scan_file_name: " +  psm.getScanFileName();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}

					Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Single_SearchProgram_AndChildren single_SearchProgram_AndChildren = 
							root_SearchProgram_AndChildren.searchProgram_AndChildren_Map.get( annotation.getSearchProgram() );
					
					if ( single_SearchProgram_AndChildren == null ) {
						String msg = "The 'search_program' attribute on <filterable_psm_annotation> on <psm> element is not found under <search_program_info>. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName() 
								+ ",  scan_number: " +  psm.getScanNumber()
								+ ",  scan_file_name: " +  psm.getScanFileName();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}
					
					if ( ! single_SearchProgram_AndChildren.filterablePsmAnnotationType_Names.contains( annotation.getAnnotationName() ) ) {
						String msg = "The 'search_program' / 'annotation_name' attribute pair on <filterable_psm_annotation> on <psm> element is not found under <search_program_info>. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName() 
								+ ",  scan_number: " +  psm.getScanNumber()
								+ ",  scan_file_name: " +  psm.getScanFileName();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}
				}
			}

			if ( psm.getDescriptivePsmAnnotations() != null 
					&& psm.getDescriptivePsmAnnotations().getDescriptivePsmAnnotation() != null ) {
				
				Map<String, Set<String>> annotationNames_Set_Map_Key_SearchProgramName = new HashMap<>();
				
				for ( DescriptivePsmAnnotation annotation : psm.getDescriptivePsmAnnotations().getDescriptivePsmAnnotation() ) {
					
					Set<String> annotationNames_Set_For_SearchProgramName = annotationNames_Set_Map_Key_SearchProgramName.get( annotation.getSearchProgram() );
					if ( annotationNames_Set_For_SearchProgramName == null ) {
						annotationNames_Set_For_SearchProgramName = new HashSet<>();
						annotationNames_Set_Map_Key_SearchProgramName.put( annotation.getSearchProgram(), annotationNames_Set_For_SearchProgramName );
					}

					if ( ! annotationNames_Set_For_SearchProgramName.add( annotation.getAnnotationName() ) ) {
						String msg = "The 'search_program' / 'annotation_name' attribute pair on <descriptive_psm_annotation> on <psm> element has duplicate which is not allowed. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName() 
								+ ",  scan_number: " +  psm.getScanNumber()
								+ ",  scan_file_name: " +  psm.getScanFileName();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}

					Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Single_SearchProgram_AndChildren single_SearchProgram_AndChildren = 
							root_SearchProgram_AndChildren.searchProgram_AndChildren_Map.get( annotation.getSearchProgram() );
					
					if ( single_SearchProgram_AndChildren == null ) {
						String msg = "The 'search_program' attribute on <descriptive_psm_annotation> on <psm> element is not found under <search_program_info>. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName() 
								+ ",  scan_number: " +  psm.getScanNumber()
								+ ",  scan_file_name: " +  psm.getScanFileName();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}
					
					if ( ! single_SearchProgram_AndChildren.descriptivePsmAnnotationType_Names.contains( annotation.getAnnotationName() ) ) {
						String msg = "The 'search_program' / 'annotation_name' attribute pair on <descriptive_psm_annotation> on <psm> element is not found under <search_program_info>. search_program: "
								+ annotation.getSearchProgram()
								+ ", annotation_name: "
								+ annotation.getAnnotationName() 
								+ ",  scan_number: " +  psm.getScanNumber()
								+ ",  scan_file_name: " +  psm.getScanFileName();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}
				}
			}

		}
	}
	
	///////////////////////
	
	//  Internal Classes and their population

	/**
	 * 
	 *
	 */
	private static class Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Root {
		
		Map<String, Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Single_SearchProgram_AndChildren> searchProgram_AndChildren_Map = new HashMap<>();
	}

	/**
	 * 
	 *
	 */
	private static class Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Single_SearchProgram_AndChildren {
		
		String programName;
		
		Set<String> filterableMatchedProteinAnnotationType_Names = new HashSet<>();
		Set<String> descriptiveMatchedProteinAnnotationType_Names = new HashSet<>();
		
		Set<String> filterableReportedPeptideAnnotationType_Names = new HashSet<>();
		Set<String> descriptiveReportedPeptideAnnotationType_Names = new HashSet<>();

		Set<String> filterablePsmAnnotationType_Names = new HashSet<>();
		Set<String> descriptivePsmAnnotationType_Names = new HashSet<>();
		
		Set<String> filterableModificationPositionAnnotationType_Names = new HashSet<>();
		Set<String> descriptiveModificationPositionAnnotationType_Names = new HashSet<>();
		
	}
	
	/**
	 * @param proxlInput
	 * @return
	 * @throws ProxlImporterInternalException 
	 */
	private Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Root get_Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Root( ProxlInput proxlInput ) throws ProxlImporterInternalException {
		
		Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Root root = new Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Root();
		
		SearchProgramInfo searchProgramInfo = proxlInput.getSearchProgramInfo(); 
		SearchPrograms proxlInputSearchPrograms = searchProgramInfo.getSearchPrograms();
		List<SearchProgram> searchProgramList = proxlInputSearchPrograms.getSearchProgram();
		
		
		
		for ( SearchProgram searchProgram : searchProgramList ) {
			
			Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Single_SearchProgram_AndChildren single_SearchProgram_AndChildren = new Internal_Holder_SearchProgram_AndChildren_ConvertedToMaps_Single_SearchProgram_AndChildren();
			single_SearchProgram_AndChildren.programName = searchProgram.getName();
			
			if ( root.searchProgram_AndChildren_Map.put( searchProgram.getName(), single_SearchProgram_AndChildren ) != null ) {
				throw new ProxlImporterInternalException( "Duplicate search program '" + searchProgram.getName() + "'");
			}
			
			if ( searchProgram.getReportedPeptideAnnotationTypes() != null ) {
				
				if ( searchProgram.getReportedPeptideAnnotationTypes().getFilterablePeptideAnnotationTypes() != null )  {
					for ( FilterablePeptideAnnotationType annotationType : searchProgram.getReportedPeptideAnnotationTypes().getFilterablePeptideAnnotationTypes().getFilterablePeptideAnnotationType() ) {						
						
						if ( ! single_SearchProgram_AndChildren.filterableReportedPeptideAnnotationType_Names.add( annotationType.getName() ) ) {
							// Duplicates are checked in class ValidateAnnotationTypeRecords so throw internal error here
							throw new ProxlImporterInternalException( "Duplicate FilterablePeptideAnnotationType Type Name '" + annotationType.getName() + "' under search program '" + searchProgram.getName() + "'");
						}
					}
				}
				if ( searchProgram.getReportedPeptideAnnotationTypes().getDescriptivePeptideAnnotationTypes() != null )  {
					for ( DescriptivePeptideAnnotationType annotationType : searchProgram.getReportedPeptideAnnotationTypes().getDescriptivePeptideAnnotationTypes().getDescriptivePeptideAnnotationType() ) {						
						
						if ( ! single_SearchProgram_AndChildren.descriptiveReportedPeptideAnnotationType_Names.add( annotationType.getName() ) ) {
							// Duplicates are checked in class ValidateAnnotationTypeRecords so throw internal error here
							throw new ProxlImporterInternalException( "Duplicate DescriptivePeptideAnnotationType Annotation Type Name '" + annotationType.getName() + "' under search program '" + searchProgram.getName() + "'");
						}
					}
				}
			}

			if ( searchProgram.getPsmAnnotationTypes() != null ) {
				
				if ( searchProgram.getPsmAnnotationTypes().getFilterablePsmAnnotationTypes() != null )  {
					for ( FilterablePsmAnnotationType annotationType : searchProgram.getPsmAnnotationTypes().getFilterablePsmAnnotationTypes().getFilterablePsmAnnotationType() ) {						
						
						if ( ! single_SearchProgram_AndChildren.filterablePsmAnnotationType_Names.add( annotationType.getName() ) ) {
							// Duplicates are checked in class ValidateAnnotationTypeRecords so throw internal error here
							throw new ProxlImporterInternalException( "Duplicate FilterablePsmAnnotationType Type Name '" + annotationType.getName() + "' under search program '" + searchProgram.getName() + "'");
						}
					}
				}
				if ( searchProgram.getPsmAnnotationTypes().getDescriptivePsmAnnotationTypes() != null )  {
					for ( DescriptivePsmAnnotationType annotationType : searchProgram.getPsmAnnotationTypes().getDescriptivePsmAnnotationTypes().getDescriptivePsmAnnotationType() ) {						
						
						if ( ! single_SearchProgram_AndChildren.descriptivePsmAnnotationType_Names.add( annotationType.getName() ) ) {
							// Duplicates are checked in class ValidateAnnotationTypeRecords so throw internal error here
							throw new ProxlImporterInternalException( "Duplicate DescriptivePsmAnnotationType Annotation Type Name '" + annotationType.getName() + "' under search program '" + searchProgram.getName() + "'");
						}
					}
				}
			}
			
			
		}
		
		return root;
	}
	
	
}

