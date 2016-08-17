package org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram.ReportedPeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.SearchPrograms;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;

/**
 * 
 *
 */
public class DropPeptidePSMPopulateFilterDirection {

	private static final Logger log = Logger.getLogger(DropPeptidePSMPopulateFilterDirection.class);

	//  private constructor
	private DropPeptidePSMPopulateFilterDirection() { }
	
	public static DropPeptidePSMPopulateFilterDirection getInstance() { return new DropPeptidePSMPopulateFilterDirection(); }
	
	
	
	/**
	 * @param dropPeptidePSMCutoffValues
	 * @param proxlInput
	 * @throws ProxlImporterDataException
	 */
	public void populateFilterDirection( DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues, ProxlInput proxlInput ) throws ProxlImporterDataException {

		List<DropPeptidePSMCutoffValue> dropPeptideCutoffValueList = dropPeptidePSMCutoffValues.getDropPeptideCutoffValueList();
		List<DropPeptidePSMCutoffValue> dropPsmCutoffValueList = dropPeptidePSMCutoffValues.getDropPSMCutoffValueList();
		
		if ( ( dropPeptideCutoffValueList != null && ( ! dropPeptideCutoffValueList.isEmpty() ) ) 
				||  ( dropPsmCutoffValueList != null && ( ! dropPsmCutoffValueList.isEmpty() ) ) ) {

			SearchProgramInfo searchProgramInfo = proxlInput.getSearchProgramInfo();

			if ( searchProgramInfo == null ) {

				String msg = "proxlInput.getSearchProgramInfo() cannot be null";
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}

			SearchPrograms searchPrograms = searchProgramInfo.getSearchPrograms();

			if ( searchPrograms == null ) {

				String msg = "searchProgramInfo.getSearchPrograms() cannot be null";
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}

			List<SearchProgram> searchProgramList = searchPrograms.getSearchProgram();

			if ( searchProgramList == null || searchProgramList.isEmpty() ) {

				String msg = " searchPrograms.getSearchProgram() cannot be null or empty";
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}

			for ( SearchProgram searchProgram : searchProgramList ) {
				
				if ( dropPeptideCutoffValueList != null && ( ! dropPeptideCutoffValueList.isEmpty() ) ) {

					ReportedPeptideAnnotationTypes reportedPeptideAnnotationTypes = searchProgram.getReportedPeptideAnnotationTypes();

					if ( reportedPeptideAnnotationTypes != null ) {

						FilterablePeptideAnnotationTypes filterablePeptideAnnotationTypes = 
								reportedPeptideAnnotationTypes.getFilterablePeptideAnnotationTypes();

						if ( filterablePeptideAnnotationTypes != null ) {

							List<FilterablePeptideAnnotationType> filterablePeptideAnnotationTypeList = 
									filterablePeptideAnnotationTypes.getFilterablePeptideAnnotationType();

							if ( filterablePeptideAnnotationTypeList != null && ( ! filterablePeptideAnnotationTypeList.isEmpty() ) ) {

								for ( FilterablePeptideAnnotationType filterablePeptideAnnotationType : filterablePeptideAnnotationTypeList ) {

									for ( DropPeptidePSMCutoffValue dropPeptideCutoffValue : dropPeptideCutoffValueList ) {

										if ( dropPeptideCutoffValue.getAnnotationName().equals( filterablePeptideAnnotationType.getName() ) ) {

											if ( dropPeptideCutoffValue.getAnnotationTypeFilterDirection() != null ) {

												String msg = " Filter direction already assigned for annotation name '"
														+ filterablePeptideAnnotationType.getName() 
														+ "'.  Drop records currently not supported for annotation names that are the same"
														+ " in more than one search program.";
												log.error( msg );
												throw new ProxlImporterDataException(msg);
											}
											
											if ( filterablePeptideAnnotationType.getFilterDirection() 
													== org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType.ABOVE ) {
											
												dropPeptideCutoffValue.setAnnotationTypeFilterDirection( FilterDirectionType.ABOVE );
												
											} else if ( filterablePeptideAnnotationType.getFilterDirection() 
													== org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType.BELOW ) {
											
												dropPeptideCutoffValue.setAnnotationTypeFilterDirection( FilterDirectionType.BELOW );
												
											} else {

												String msg = " Unknown value for Filter direction for annotation name '"
														+ filterablePeptideAnnotationType.getName()
														+ "'.  Filter direction value: " + 
														filterablePeptideAnnotationType.getFilterDirection() ;
												log.error( msg );
												throw new ProxlImporterDataException(msg);
												
											}
										}
									}
								}
							}
						}
					}
				}
					

				if ( dropPsmCutoffValueList != null && ( ! dropPsmCutoffValueList.isEmpty() ) ) {

					PsmAnnotationTypes psmAnnotationTypes = searchProgram.getPsmAnnotationTypes();

					if ( psmAnnotationTypes != null ) {

						FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = 
								psmAnnotationTypes.getFilterablePsmAnnotationTypes();

						if ( filterablePsmAnnotationTypes != null ) {

							List<FilterablePsmAnnotationType> filterablePsmAnnotationTypeList = 
									filterablePsmAnnotationTypes.getFilterablePsmAnnotationType();

							if ( filterablePsmAnnotationTypeList != null && ( ! filterablePsmAnnotationTypeList.isEmpty() ) ) {

								for ( FilterablePsmAnnotationType filterablePsmAnnotationType : filterablePsmAnnotationTypeList ) {

									for ( DropPeptidePSMCutoffValue dropPsmCutoffValue : dropPsmCutoffValueList ) {

										if ( dropPsmCutoffValue.getAnnotationName().equals( filterablePsmAnnotationType.getName() ) ) {

											if ( dropPsmCutoffValue.getAnnotationTypeFilterDirection() != null ) {

												String msg = " Filter direction already assigned for annotation name '"
														+ filterablePsmAnnotationType.getName() 
														+ "'.  Drop records currently not supported for annotation names that are the same"
														+ " in more than one search program.";
												log.error( msg );
												throw new ProxlImporterDataException(msg);
											}

											if ( filterablePsmAnnotationType.getFilterDirection() 
													== org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType.ABOVE ) {

												dropPsmCutoffValue.setAnnotationTypeFilterDirection( FilterDirectionType.ABOVE );

											} else if ( filterablePsmAnnotationType.getFilterDirection() 
													== org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType.BELOW ) {

												dropPsmCutoffValue.setAnnotationTypeFilterDirection( FilterDirectionType.BELOW );

											} else {

												String msg = " Unknown value for Filter direction for annotation name '"
														+ filterablePsmAnnotationType.getName()
														+ "'.  Filter direction value: " + 
														filterablePsmAnnotationType.getFilterDirection() ;
												log.error( msg );
												throw new ProxlImporterDataException(msg);

											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		
		//  Make sure all drop values have a direction
		

		if ( dropPeptideCutoffValueList != null && ( ! dropPeptideCutoffValueList.isEmpty() ) ) {

			for ( DropPeptidePSMCutoffValue dropPeptideCutoffValue : dropPeptideCutoffValueList ) {

				if ( dropPeptideCutoffValue.getAnnotationTypeFilterDirection() == null ) {

					String msg = " No Reported Peptide Annotation Type record found for annotation name '"
							+ dropPeptideCutoffValue.getAnnotationName()
							+ "'.";
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}

			}
		}
		

		if ( dropPsmCutoffValueList != null && ( ! dropPsmCutoffValueList.isEmpty() ) ) {

			for ( DropPeptidePSMCutoffValue dropPsmCutoffValue : dropPsmCutoffValueList ) {

				if ( dropPsmCutoffValue.getAnnotationTypeFilterDirection() == null ) {

					String msg = " No Reported Psm Annotation Type record found for annotation name '"
							+ dropPsmCutoffValue.getAnnotationName()
							+ "'.";
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}

			}
		}
		
	}
}
