package org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl_import.api.xml_dto.AnnotationCutoffsOnImport;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.PsmAnnotationCutoffsOnImport;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptideAnnotationCutoffsOnImport;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotationCutoff;
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
public class DropPeptidePSMPopulateFromProxlXMLInput {

	private static final Logger log = Logger.getLogger(DropPeptidePSMPopulateFromProxlXMLInput.class);

	//  private constructor
	private DropPeptidePSMPopulateFromProxlXMLInput() { }
	
	public static DropPeptidePSMPopulateFromProxlXMLInput getInstance() { return new DropPeptidePSMPopulateFromProxlXMLInput(); }
	
	
	
	/**
	 * If dropPeptidePSMCutoffValues has current values from the command line:
	 *    Populate the searchProgram and filterDirection
	 *    
	 * If dropPeptidePSMCutoffValues does NOT have current values:
	 *    Populate cutoffs from proxlInput if there are any
	 * 
	 * @param dropPeptidePSMCutoffValues
	 * @param proxlInput
	 * @throws ProxlImporterDataException
	 */
	public void populateFromProxlXMLInput( DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues, ProxlInput proxlInput ) throws ProxlImporterDataException {


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

		if ( ( dropPeptidePSMCutoffValues.getDropPeptideCutoffValuesCommandLineList() != null 
				&& ( ! dropPeptidePSMCutoffValues.getDropPeptideCutoffValuesCommandLineList().isEmpty() ) ) 
				||  ( dropPeptidePSMCutoffValues.getDropPSMCutoffValuesCommandLineList() != null 
						&& ( ! dropPeptidePSMCutoffValues.getDropPSMCutoffValuesCommandLineList().isEmpty() ) ) ) {

			//  Current cutoff values exist so populate the searchProgram and filterDirection
			
			processCutoffsFromCommandLine( dropPeptidePSMCutoffValues, searchProgramList );

		} else {
			
			//  Populate cutoffs from proxlInput if there are any
			
			populate_dropPeptidePSMCutoffValues_FromProxlInput( dropPeptidePSMCutoffValues, searchProgramInfo, searchProgramList );
			
		}
	}
	
	
	private void populate_dropPeptidePSMCutoffValues_FromProxlInput(
			
			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues,
			SearchProgramInfo searchProgramInfo,
			List<SearchProgram> searchProgramList ) throws ProxlImporterDataException {
		
		
		AnnotationCutoffsOnImport annotationCutoffsOnImport = searchProgramInfo.getAnnotationCutoffsOnImport();
		
		if ( annotationCutoffsOnImport == null ) {
			
			//  No Cutoffs in the ProxlInput so exit
			
			return;  //  EARLY EXIT
		}
		
		ReportedPeptideAnnotationCutoffsOnImport reportedPeptideAnnotationCutoffsOnImport =
				annotationCutoffsOnImport.getReportedPeptideAnnotationCutoffsOnImport();
		
		PsmAnnotationCutoffsOnImport psmAnnotationCutoffsOnImport = 
				annotationCutoffsOnImport.getPsmAnnotationCutoffsOnImport();

		if ( reportedPeptideAnnotationCutoffsOnImport == null && psmAnnotationCutoffsOnImport == null ) {
			
			//  No Cutoffs in the ProxlInput so exit
			
			return;  //  EARLY EXIT
		}
		
		
		List<SearchAnnotationCutoff> reportedPeptideSearchAnnotationCutoffList = null;
		
		if ( reportedPeptideAnnotationCutoffsOnImport != null ) {
			reportedPeptideSearchAnnotationCutoffList =
				reportedPeptideAnnotationCutoffsOnImport.getSearchAnnotationCutoff();
		}

		List<SearchAnnotationCutoff> psmSearchAnnotationCutoffList = null;
		
		if ( psmAnnotationCutoffsOnImport != null ) {
			psmSearchAnnotationCutoffList =
					psmAnnotationCutoffsOnImport.getSearchAnnotationCutoff();
		}
		
		if ( ( reportedPeptideSearchAnnotationCutoffList == null || reportedPeptideSearchAnnotationCutoffList.isEmpty() )
				&& ( psmSearchAnnotationCutoffList == null || psmSearchAnnotationCutoffList.isEmpty() ) ) {
			
			//  No Cutoffs in the ProxlInput so exit
			
			return;  //  EARLY EXIT
		}
		
		//  Check for duplicates in cutoffs
		
		//  Throws ProxlImporterDataException if errors
		checkForDuplicatesInCutoffs( reportedPeptideSearchAnnotationCutoffList, "<reported_peptide_annotation_cutoffs_on_import>" );

		//  Throws ProxlImporterDataException if errors
		checkForDuplicatesInCutoffs( psmSearchAnnotationCutoffList, "<psm_annotation_cutoffs_on_import>" );
		
		
		
		//  Copy FilterablePeptideAnnotationType and FilterablePsmAnnotationType to maps for lookup
		
		Map<String,Map<String,FilterablePeptideAnnotationType>> peptideAnnotationTypeKeyedOnSearchPgmNameAnnotationName = new HashMap<>();
		Map<String,Map<String,FilterablePsmAnnotationType>> psmAnnotationTypeKeyedOnSearchPgmNameAnnotationName = new HashMap<>();
		
		

		for ( SearchProgram searchProgram : searchProgramList ) {

			ReportedPeptideAnnotationTypes reportedPeptideAnnotationTypes = searchProgram.getReportedPeptideAnnotationTypes();

			if ( reportedPeptideAnnotationTypes != null ) {

				FilterablePeptideAnnotationTypes filterablePeptideAnnotationTypes = 
						reportedPeptideAnnotationTypes.getFilterablePeptideAnnotationTypes();

				if ( filterablePeptideAnnotationTypes != null ) {

					List<FilterablePeptideAnnotationType> filterablePeptideAnnotationTypeList = 
							filterablePeptideAnnotationTypes.getFilterablePeptideAnnotationType();

					if ( filterablePeptideAnnotationTypeList != null && ( ! filterablePeptideAnnotationTypeList.isEmpty() ) ) {

						for ( FilterablePeptideAnnotationType filterablePeptideAnnotationType : filterablePeptideAnnotationTypeList ) {

							Map<String,FilterablePeptideAnnotationType> peptideAnnotationTypeKeyedOnAnnotationName =
									peptideAnnotationTypeKeyedOnSearchPgmNameAnnotationName.get( searchProgram.getName() );
							
							if ( peptideAnnotationTypeKeyedOnAnnotationName == null ) {
								
								peptideAnnotationTypeKeyedOnAnnotationName = new HashMap<>();
								peptideAnnotationTypeKeyedOnSearchPgmNameAnnotationName.put( searchProgram.getName(), peptideAnnotationTypeKeyedOnAnnotationName );
							}

							peptideAnnotationTypeKeyedOnAnnotationName.put( filterablePeptideAnnotationType.getName(), filterablePeptideAnnotationType );
						}
					}
				}
			}

			PsmAnnotationTypes psmAnnotationTypes = searchProgram.getPsmAnnotationTypes();

			if ( psmAnnotationTypes != null ) {

				FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = 
						psmAnnotationTypes.getFilterablePsmAnnotationTypes();

				if ( filterablePsmAnnotationTypes != null ) {

					List<FilterablePsmAnnotationType> filterablePsmAnnotationTypeList = 
							filterablePsmAnnotationTypes.getFilterablePsmAnnotationType();

					if ( filterablePsmAnnotationTypeList != null && ( ! filterablePsmAnnotationTypeList.isEmpty() ) ) {

						for ( FilterablePsmAnnotationType filterablePsmAnnotationType : filterablePsmAnnotationTypeList ) {

							Map<String,FilterablePsmAnnotationType> psmAnnotationTypeKeyedOnAnnotationName =
									psmAnnotationTypeKeyedOnSearchPgmNameAnnotationName.get( searchProgram.getName() );
							
							if ( psmAnnotationTypeKeyedOnAnnotationName == null ) {
								
								psmAnnotationTypeKeyedOnAnnotationName = new HashMap<>();
								psmAnnotationTypeKeyedOnSearchPgmNameAnnotationName.put( searchProgram.getName(), psmAnnotationTypeKeyedOnAnnotationName );
							}

							psmAnnotationTypeKeyedOnAnnotationName.put( filterablePsmAnnotationType.getName(), filterablePsmAnnotationType );
						}
					}
				}
			}
		}
		
		//  Create output Maps

		Map<String,Map<String,DropPeptidePSMCutoffValue>> dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName = new HashMap<>();

		Map<String,Map<String,DropPeptidePSMCutoffValue>> dropPsmCutoffValueKeyedOnSearchPgmNameAnnName = new HashMap<>();
		
		dropPeptidePSMCutoffValues.setDropPeptideCutoffValueKeyedOnSearchPgmNameAnnName( dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName );
		dropPeptidePSMCutoffValues.setDropPSMCutoffValueKeyedOnSearchPgmNameAnnName( dropPsmCutoffValueKeyedOnSearchPgmNameAnnName );
		
		//  Process cutoff Lists, populating output 
		
		if ( reportedPeptideSearchAnnotationCutoffList != null ) {

			for ( SearchAnnotationCutoff annCutoffItem : reportedPeptideSearchAnnotationCutoffList ) {

				Map<String,FilterablePeptideAnnotationType> peptideAnnotationTypeKeyedOnAnnotationName =
						peptideAnnotationTypeKeyedOnSearchPgmNameAnnotationName
						.get( annCutoffItem.getSearchProgram() );

				if ( peptideAnnotationTypeKeyedOnAnnotationName == null ) {

					String msg = "Error Processing Peptide Cutoffs.  There is No entry in <search_programs>"
							+ " for "
							+ " search program '" + annCutoffItem.getSearchProgram() + "'.";
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				}
				
				FilterablePeptideAnnotationType filterablePeptideAnnotationType =
						peptideAnnotationTypeKeyedOnAnnotationName.get( annCutoffItem.getAnnotationName() );

				if ( filterablePeptideAnnotationType == null ) {

					String msg = "Error Processing Peptide Cutoffs.  There is No entry in <search_programs>"
							+ " for "
							+ " peptide annotation name '" + annCutoffItem.getAnnotationName() + "',"
							+ " under search program '" + annCutoffItem.getSearchProgram() + "'.";
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				}
				
				FilterDirectionType proxlInternalFilterDirectionType = 
						getFilterDirectionTypeFromProxlInputFilterDirectionType( 
								filterablePeptideAnnotationType.getFilterDirection(), 
								annCutoffItem.getAnnotationName() );
				
				DropPeptidePSMCutoffValue dropPeptidePSMCutoffValue = new DropPeptidePSMCutoffValue();
				
				dropPeptidePSMCutoffValue.setSearchProgram( annCutoffItem.getSearchProgram() );
				dropPeptidePSMCutoffValue.setAnnotationName( annCutoffItem.getAnnotationName() );
				dropPeptidePSMCutoffValue.setAnnotationTypeFilterDirection( proxlInternalFilterDirectionType );
				dropPeptidePSMCutoffValue.setCutoffValue( annCutoffItem.getCutoffValue() );
				
				
				Map<String,DropPeptidePSMCutoffValue> dropPeptideCutoffValueKeyedOnAnnName =
						dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName.get( annCutoffItem.getSearchProgram() );
				
				if ( dropPeptideCutoffValueKeyedOnAnnName == null ) {
					
					dropPeptideCutoffValueKeyedOnAnnName = new HashMap<>();
					dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName.put( annCutoffItem.getSearchProgram(), dropPeptideCutoffValueKeyedOnAnnName );
				}
				
				Object prevMapValue =
						dropPeptideCutoffValueKeyedOnAnnName.put( annCutoffItem.getAnnotationName(), dropPeptidePSMCutoffValue );
				
				if ( prevMapValue != null ) {
					
					String msg = "Peptide Entry already processed for search program name: '" + annCutoffItem.getSearchProgram()
							+ "', annotation name: '" + annCutoffItem.getAnnotationName() + "'.";
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				}
			}
		}

		if ( psmSearchAnnotationCutoffList != null ) {

			for ( SearchAnnotationCutoff annCutoffItem : psmSearchAnnotationCutoffList ) {


				Map<String,FilterablePsmAnnotationType> psmAnnotationTypeKeyedOnAnnotationName =
						psmAnnotationTypeKeyedOnSearchPgmNameAnnotationName
						.get( annCutoffItem.getSearchProgram() );

				if ( psmAnnotationTypeKeyedOnAnnotationName == null ) {

					String msg = "Error Processing Psm Cutoffs.  There is No entry in <search_programs>"
							+ " for "
							+ " search program '" + annCutoffItem.getSearchProgram() + "'.";
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				}
				
				FilterablePsmAnnotationType filterablePsmAnnotationType =
						psmAnnotationTypeKeyedOnAnnotationName.get( annCutoffItem.getAnnotationName() );

				if ( filterablePsmAnnotationType == null ) {

					String msg = "Error Processing Psm Cutoffs.  There is No entry in <search_programs>"
							+ " for "
							+ " psm annotation name '" + annCutoffItem.getAnnotationName() + "',"
							+ " under search program '" + annCutoffItem.getSearchProgram() + "'.";
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				}
				
				FilterDirectionType proxlInternalFilterDirectionType = 
						getFilterDirectionTypeFromProxlInputFilterDirectionType( 
								filterablePsmAnnotationType.getFilterDirection(), 
								annCutoffItem.getAnnotationName() );
				
				DropPeptidePSMCutoffValue dropPeptidePSMCutoffValue = new DropPeptidePSMCutoffValue();
				
				dropPeptidePSMCutoffValue.setSearchProgram( annCutoffItem.getSearchProgram() );
				dropPeptidePSMCutoffValue.setAnnotationName( annCutoffItem.getAnnotationName() );
				dropPeptidePSMCutoffValue.setAnnotationTypeFilterDirection( proxlInternalFilterDirectionType );
				dropPeptidePSMCutoffValue.setCutoffValue( annCutoffItem.getCutoffValue() );
				

				Map<String,DropPeptidePSMCutoffValue> dropPsmCutoffValueKeyedOnAnnName =
						dropPsmCutoffValueKeyedOnSearchPgmNameAnnName.get( annCutoffItem.getSearchProgram() );
				
				if ( dropPsmCutoffValueKeyedOnAnnName == null ) {
					
					dropPsmCutoffValueKeyedOnAnnName = new HashMap<>();
					dropPsmCutoffValueKeyedOnSearchPgmNameAnnName.put( annCutoffItem.getSearchProgram(), dropPsmCutoffValueKeyedOnAnnName );
				}
				
				Object prevMapValue =
						dropPsmCutoffValueKeyedOnAnnName.put( annCutoffItem.getAnnotationName(), dropPeptidePSMCutoffValue );
				
				if ( prevMapValue != null ) {
					
					String msg = "Psm Entry already processed for search program name: '" + annCutoffItem.getSearchProgram()
							+ "', annotation name: '" + annCutoffItem.getAnnotationName() + "'.";
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				}
				
			}
			
		}
		
	}
	
	
	/**
	 * @param inputFilterDirectionType
	 * @param annotationName
	 * @return
	 * @throws ProxlImporterDataException 
	 */
	private FilterDirectionType getFilterDirectionTypeFromProxlInputFilterDirectionType(
			org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType inputFilterDirectionType,
			String annotationName
			) throws ProxlImporterDataException {
		
		FilterDirectionType proxlInternalFilterDirectionType = null;

		if ( inputFilterDirectionType 
				== org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType.ABOVE ) {
		
			proxlInternalFilterDirectionType = FilterDirectionType.ABOVE;
			
		} else if ( inputFilterDirectionType 
				== org.yeastrc.proxl_import.api.xml_dto.FilterDirectionType.BELOW ) {
		
			proxlInternalFilterDirectionType = FilterDirectionType.BELOW;
			
		} else {

			String msg = " Unknown value for Filter direction for annotation name '"
					+ annotationName
					+ "'.  Filter direction value: " + 
					inputFilterDirectionType ;
			log.error( msg );
			throw new ProxlImporterDataException(msg);
			
		}
		
		return proxlInternalFilterDirectionType;
	}

	/**
	 * @param searchAnnotationCutoffList
	 * @param reportedPeptide_PSM_ElementString
	 * @throws ProxlImporterDataException
	 */
	private void checkForDuplicatesInCutoffs(
			
			List<SearchAnnotationCutoff> searchAnnotationCutoffList,
			final String reportedPeptide_PSM_ElementString)
			throws ProxlImporterDataException {
		
		if ( searchAnnotationCutoffList == null || searchAnnotationCutoffList.isEmpty() ) {
			
			return;  // Skip since no values 
		}
		
		Map<String,Set<String>> annotationNamesKeyedOnSearchPgmName = new HashMap<>();
		
		for ( SearchAnnotationCutoff annCutoffItem : searchAnnotationCutoffList ) {
			
			Set<String> entriesForSearchPgmName = annotationNamesKeyedOnSearchPgmName.get( annCutoffItem.getSearchProgram() );
			
			if ( entriesForSearchPgmName == null ) {
				
				entriesForSearchPgmName = new HashSet<>();
				annotationNamesKeyedOnSearchPgmName.put( annCutoffItem.getSearchProgram(), entriesForSearchPgmName );
			}
			
			if ( ! entriesForSearchPgmName.add( annCutoffItem.getAnnotationName() ) ) {
				
				String msg = "There is more than one entry in the <annotation_cutoffs_on_import>"
						+ reportedPeptide_PSM_ElementString
						+ " for "
						+ " annotation name '" + annCutoffItem.getAnnotationName() + "',"
						+ " search program '" + annCutoffItem.getSearchProgram() + "'.";
				log.error( msg );
				throw new ProxlImporterDataException( msg );
			}
		}
	}
	
	
	

	/**
	 * @param dropPeptidePSMCutoffValues
	 * @param searchProgramList
	 * @throws ProxlImporterDataException
	 */
	private void processCutoffsFromCommandLine(
			
			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues,
			List<SearchProgram> searchProgramList)
			throws ProxlImporterDataException {
		
		
		//  cutoff values from command line exist so populate the searchProgram and filterDirection
		
		//   Transfer into output maps

		//  Create output Maps

		Map<String,Map<String,DropPeptidePSMCutoffValue>> dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName = new HashMap<>();

		Map<String,Map<String,DropPeptidePSMCutoffValue>> dropPsmCutoffValueKeyedOnSearchPgmNameAnnName = new HashMap<>();
		
		dropPeptidePSMCutoffValues.setDropPeptideCutoffValueKeyedOnSearchPgmNameAnnName( dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName );
		dropPeptidePSMCutoffValues.setDropPSMCutoffValueKeyedOnSearchPgmNameAnnName( dropPsmCutoffValueKeyedOnSearchPgmNameAnnName );
		
		
		
		

		List<DropPeptidePSMCutoffValue> dropPeptideCutoffValueList = dropPeptidePSMCutoffValues.getDropPeptideCutoffValuesCommandLineList();
		List<DropPeptidePSMCutoffValue> dropPsmCutoffValueList = dropPeptidePSMCutoffValues.getDropPSMCutoffValuesCommandLineList();

		

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

											String dataErrorMsg = "Reported Peptide Annotation '"
													+ filterablePeptideAnnotationType.getName() 
													+ "' found for more than one <search_program>"
													+ " so this annotation name cannot be used for import cutoffs.";
											
											log.error( dataErrorMsg );
											
											
											String msg = " Filter direction already assigned for annotation name '"
													+ filterablePeptideAnnotationType.getName() 
													+ "'.  Drop records currently not supported for annotation names that are the same"
													+ " in more than one search program.";
											log.error( msg );
											
											
											throw new ProxlImporterDataException( dataErrorMsg );
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
										
										//  Set search Program Name
										
										dropPeptideCutoffValue.setSearchProgram( searchProgram.getName() );
										

										Map<String,DropPeptidePSMCutoffValue> dropPeptideCutoffValueKeyedOnAnnName =
												dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName.get( dropPeptideCutoffValue.getSearchProgramName() );
										
										if ( dropPeptideCutoffValueKeyedOnAnnName == null ) {
											
											dropPeptideCutoffValueKeyedOnAnnName = new HashMap<>();
											dropPeptideCutoffValueKeyedOnSearchPgmNameAnnName.put( dropPeptideCutoffValue.getSearchProgramName(), dropPeptideCutoffValueKeyedOnAnnName );
										}
										
										Object prevMapValue =
												dropPeptideCutoffValueKeyedOnAnnName.put( dropPeptideCutoffValue.getAnnotationName(), dropPeptideCutoffValue );
										
										if ( prevMapValue != null ) {
											
											String msg = "Peptide Entry already processed for search program name: '" + dropPeptideCutoffValue.getSearchProgramName()
													+ "', annotation name: '" + dropPeptideCutoffValue.getAnnotationName() + "'.";
											log.error( msg );
											throw new ProxlImporterDataException( msg );
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

											String dataErrorMsg = "PSM Annotation '"
													+ filterablePsmAnnotationType.getName() 
													+ "' found for more than one <search_program>"
													+ " so this annotation name cannot be used for import cutoffs.";
											
											log.error( dataErrorMsg );
											
											String msg = " Filter direction already assigned for annotation name '"
													+ filterablePsmAnnotationType.getName() 
													+ "'.  Drop records currently not supported for annotation names that are the same"
													+ " in more than one search program.";
											log.error( msg );
											
											
											throw new ProxlImporterDataException(dataErrorMsg);
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
										
										//  Set search Program Name
										
										dropPsmCutoffValue.setSearchProgram( searchProgram.getName() );

										// put into output map

										Map<String,DropPeptidePSMCutoffValue> dropPsmCutoffValueKeyedOnAnnName =
												dropPsmCutoffValueKeyedOnSearchPgmNameAnnName.get( dropPsmCutoffValue.getSearchProgramName() );
										
										if ( dropPsmCutoffValueKeyedOnAnnName == null ) {
											
											dropPsmCutoffValueKeyedOnAnnName = new HashMap<>();
											dropPsmCutoffValueKeyedOnSearchPgmNameAnnName.put( dropPsmCutoffValue.getSearchProgramName(), dropPsmCutoffValueKeyedOnAnnName );
										}
										
										Object prevMapValue =
												dropPsmCutoffValueKeyedOnAnnName.put( dropPsmCutoffValue.getAnnotationName(), dropPsmCutoffValue );
										
										if ( prevMapValue != null ) {
											
											String msg = "Psm Entry already processed for search program name: '" + dropPsmCutoffValue.getSearchProgramName()
													+ "', annotation name: '" + dropPsmCutoffValue.getAnnotationName() + "'.";
											log.error( msg );
											throw new ProxlImporterDataException( msg );
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

					String msg = "Processing Cutoffs: No Reported Peptide Annotation Type record found for annotation name '"
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

					String msg = "Processing Cutoffs: No Psm Annotation Type record found for annotation name '"
							+ dropPsmCutoffValue.getAnnotationName()
							+ "'.";
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}

			}
		}
	}
}
