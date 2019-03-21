package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.ProjectSearchDAO_Importer;
import org.yeastrc.proxl.import_xml_to_db.dao.SearchDAO_Importer;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_LinkerPerSearchCleavedCrosslinkMassDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_LinkerPerSearchCrosslinkMassDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_LinkerPerSearchMonolinkMassDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchLinkerDAO;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs.DropPeptidePSMCutoffValues;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs.DropPeptidePSM_InsertToDB;
import org.yeastrc.proxl.import_xml_to_db.dto.ProjectSearchDTO_Importer;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchDTO_Importer;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.ScanFileFileContainer;
import org.yeastrc.proxl.import_xml_to_db.objects.ScanFilenameScanNumberScanIdScanFileId_Mapping;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl.import_xml_to_db.post_insert_search_processing.PerformPostInsertSearchProcessing;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.process_scans.Process_MzML_MzXml_File;
import org.yeastrc.proxl.import_xml_to_db.spectrum.validate_input_scan_file.ValidateInputScanFile;
import org.yeastrc.proxl_import.api.xml_dto.CleavedCrosslinkMass;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMass;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMasses;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.MonolinkMass;
import org.yeastrc.proxl_import.api.xml_dto.MonolinkMasses;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;
import org.yeastrc.proxl_import.api.xml_dto.Peptide.PeptideIsotopeLabels;
import org.yeastrc.proxl_import.api.xml_dto.Peptide.PeptideIsotopeLabels.PeptideIsotopeLabel;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingSingleFileDTO;
import org.yeastrc.xlink.dao.SearchCommentDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchMonolinkMassDTO;
import org.yeastrc.xlink.dto.SearchCommentDTO;
import org.yeastrc.xlink.dto.SearchLinkerDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.LinkersDBDataSingleSearchRoot;

/**
 * 
 *
 */
public class ProcessProxlInput {
	
	private static final Logger log = Logger.getLogger( ProcessProxlInput.class );
	/**
	 * private constructor
	 */
	private ProcessProxlInput(){}
	public static ProcessProxlInput getInstance() {
		return new ProcessProxlInput();
	}
	private SearchDTO_Importer searchDTOInserted;
	private ProjectSearchDTO_Importer projectSearchDTOInserted;
	public SearchDTO_Importer getSearchDTOInserted() {
		return searchDTOInserted;
	}
	public ProjectSearchDTO_Importer getProjectSearchDTOInserted() {
		return projectSearchDTOInserted;
	}
	
	/**
	 * @param projectId
	 * @param proxlInput
	 * @param scanFileList
	 * @return
	 * @throws Exception
	 */
	public void processProxlInput( 
			int projectId,
			Integer userIdInsertingSearch,
			ProxlInput proxlInput,
			List<ScanFileFileContainer> scanFileFileContainerList,
			String importDirectory,
			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues,
			Boolean skipPopulatingPathOnSearchLineOptChosen
			) throws Exception {
		searchDTOInserted = null;
		projectSearchDTOInserted = null;
		try {
			SearchDTO_Importer searchDTO = new SearchDTO_Importer();
			searchDTO.setCreatedByUserId( userIdInsertingSearch );
			searchDTO.setFastaFilename( proxlInput.getFastaFilename() );
			if ( ( skipPopulatingPathOnSearchLineOptChosen == null ) 
					|| ( ! skipPopulatingPathOnSearchLineOptChosen ) ) {
				searchDTO.setPath( importDirectory );
			}
			if ( scanFileFileContainerList == null || scanFileFileContainerList.isEmpty() ) {
				searchDTO.setHasScanData( false );
			} else {
				searchDTO.setHasScanData( true );
			}
			searchDTO.setHasIsotopeLabel( peptideContainsIsotopeLabel( proxlInput ) );
			SearchDAO_Importer.getInstance().saveToDatabase( searchDTO );
			searchDTOInserted = searchDTO;
			
			ProjectSearchDTO_Importer projectSearchDTO = new ProjectSearchDTO_Importer();
			projectSearchDTO.setProjectId( projectId );
			projectSearchDTO.setSearchId( searchDTO.getId() );
			projectSearchDTO.setCreatedByUserId( userIdInsertingSearch );
			if ( StringUtils.isNotEmpty( proxlInput.getName() ) ) {
				projectSearchDTO.setSearchName( proxlInput.getName() );
			}
			ProjectSearchDAO_Importer.getInstance().saveToDatabase( projectSearchDTO );
			projectSearchDTOInserted = projectSearchDTO;
			if ( StringUtils.isNotEmpty( proxlInput.getComment() ) ) {
				SearchCommentDTO searchCommentDTO = new SearchCommentDTO();
				searchCommentDTO.setProjectSearchid( projectSearchDTOInserted.getId() );
				searchCommentDTO.setComment( proxlInput.getComment() );
				searchCommentDTO.setAuthUserId(null);
				SearchCommentDAO.getInstance().save( searchCommentDTO );
			}
			
			///   process Linkers
			LinkersDBDataSingleSearchRoot linkersDBDataSingleSearchRoot = SaveLinkersData.getInstance().saveLinkersData( proxlInput, searchDTO );
			
			ProcessStaticModifications.getInstance().processStaticModifications( proxlInput, searchDTO.getId() );
			ProcessConfigurationFiles.getInstance().processConfigurationFiles( proxlInput, searchDTO.getId(), projectSearchDTOInserted.getId() );

			//  Scan Numbers to Scan Ids Map per Scan Filename
			Map<String, ScanFilenameScanNumberScanIdScanFileId_Mapping> mapOfScanFilenamesMapsOfScanNumbersToScanIds = new HashMap<>();
			if ( scanFileFileContainerList != null && ( ! scanFileFileContainerList.isEmpty() ) ) {
				//  Scan Numbers in the input XML that need to be read from the scan files and inserted into the DB
				Map<String, Set<Integer>> mapOfScanFilenamesSetsOfScanNumbers = 
						GetScanFilenamesAndScanNumbersToInsert.getInstance()
						.getScanFilenamesAndScanNumbersToInsert( proxlInput, dropPeptidePSMCutoffValues );
				if ( scanFileFileContainerList.size() == 1 ) {
					if ( mapOfScanFilenamesSetsOfScanNumbers.size() != 1 ) {
					}
					ScanFileFileContainer scanFileFileContainer = scanFileFileContainerList.get( 0 );
					File scanFile = scanFileFileContainer.getScanFile();
					String scanFilenameString = scanFile.getName();
					if ( scanFileFileContainer.getScanFileDBRecord() != null ) {
						ProxlXMLFileImportTrackingSingleFileDTO scanFileDBRecord = scanFileFileContainer.getScanFileDBRecord();
						scanFilenameString = scanFileDBRecord.getFilenameInUpload();
					}
					Set<Integer> scanNumbersToLoadSet = mapOfScanFilenamesSetsOfScanNumbers.entrySet().iterator().next().getValue();
					int[] scanNumbersToLoadIntArray = new int[ scanNumbersToLoadSet.size() ];
					int index = 0;
					for ( Integer scanNumberToLoad : scanNumbersToLoadSet ) {
						scanNumbersToLoadIntArray[ index ] = scanNumberToLoad;
						index++;
					}
					
					//  Throws ProxlImporterDataException if validate fails
					ValidateInputScanFile.getInstance().validateScanFile( scanFile, scanFilenameString );
					
					ScanFilenameScanNumberScanIdScanFileId_Mapping scanFilenameScanNumberScanIdScanFileId_Mapping =
							Process_MzML_MzXml_File.getInstance()
							.processMzMLFileWithScanNumbersToLoad( scanFileFileContainer, scanNumbersToLoadIntArray );
					
					mapOfScanFilenamesMapsOfScanNumbersToScanIds.put( scanFilenameString, scanFilenameScanNumberScanIdScanFileId_Mapping );
					
				} else {
					
					// First validate all scan files

					for ( ScanFileFileContainer scanFileFileContainer : scanFileFileContainerList ) {
						File scanFile = scanFileFileContainer.getScanFile();
						String scanFilenameString = scanFile.getName();
						if ( scanFileFileContainer.getScanFileDBRecord() != null ) {
							ProxlXMLFileImportTrackingSingleFileDTO scanFileDBRecord = scanFileFileContainer.getScanFileDBRecord();
							scanFilenameString = scanFileDBRecord.getFilenameInUpload();
						}

						//  Throws ProxlImporterDataException if validate fails
						ValidateInputScanFile.getInstance().validateScanFile( scanFile, scanFilenameString );
					}
					
					boolean scanNumbersFoundForAnyScanFile = false;
					for ( ScanFileFileContainer scanFileFileContainer : scanFileFileContainerList ) {
						File scanFile = scanFileFileContainer.getScanFile();
						String scanFilenameString = scanFile.getName();
						if ( scanFileFileContainer.getScanFileDBRecord() != null ) {
							ProxlXMLFileImportTrackingSingleFileDTO scanFileDBRecord = scanFileFileContainer.getScanFileDBRecord();
							scanFilenameString = scanFileDBRecord.getFilenameInUpload();
						}
						Set<Integer> scanNumbersToLoadSet = mapOfScanFilenamesSetsOfScanNumbers.get( scanFilenameString );
						if ( scanNumbersToLoadSet == null ) {
							String msg = "Scan Numbers not found for Scan File: " + scanFilenameString;
							log.warn( msg );
						} else {
							scanNumbersFoundForAnyScanFile = true;
							int[] scanNumbersToLoadIntArray = new int[ scanNumbersToLoadSet.size() ];
							int index = 0;
							for ( Integer scanNumberToLoad : scanNumbersToLoadSet ) {
								scanNumbersToLoadIntArray[ index ] = scanNumberToLoad;
								index++;
							}
							ScanFilenameScanNumberScanIdScanFileId_Mapping scanFilenameScanNumberScanIdScanFileId_Mapping =
									Process_MzML_MzXml_File.getInstance()
									.processMzMLFileWithScanNumbersToLoad( scanFileFileContainer, scanNumbersToLoadIntArray );
							mapOfScanFilenamesMapsOfScanNumbersToScanIds.put( scanFilenameString, scanFilenameScanNumberScanIdScanFileId_Mapping );
						}
					}
					if ( ! scanNumbersFoundForAnyScanFile ) {
						String msg = "No Scan Numbers found for any scan file.";
						log.error( msg );
						throw new ProxlImporterInteralException( msg );
					}
				}
				
				log.warn( "INFO:  !!  Finished processing Scan Files.");
			}
			Map<String, SearchProgramEntry> searchProgramEntryMap =
					ProcessSearchProgramEntries.getInstance()
					.processSearchProgramEntries( proxlInput, searchDTO.getId() );
			
			DropPeptidePSM_InsertToDB.getInstance().insertDBEntries( dropPeptidePSMCutoffValues, searchProgramEntryMap );
			
			ReportedPeptideAndPsmFilterableAnnotationTypesOnId reportedPeptideAndPsmFilterableAnnotationTypesOnId = new ReportedPeptideAndPsmFilterableAnnotationTypesOnId();
			reportedPeptideAndPsmFilterableAnnotationTypesOnId.filterableReportedPeptideAnnotationTypesOnId = 
					createReportedPeptideFilterableAnnotationTypesOnId( searchProgramEntryMap );
			reportedPeptideAndPsmFilterableAnnotationTypesOnId.filterablePsmAnnotationTypesOnId = 
					createPsmFilterableAnnotationTypesOnId( searchProgramEntryMap );
			reportedPeptideAndPsmFilterableAnnotationTypesOnId.filterablePsmPerPeptideAnnotationTypesOnId =
					createPsmPerPeptideFilterableAnnotationTypesOnId( searchProgramEntryMap );
			
			if ( reportedPeptideAndPsmFilterableAnnotationTypesOnId.filterablePsmAnnotationTypesOnId == null ) {
				String msg = "filterablePsmAnnotationTypesOnId == null";
				log.error( msg );
				throw new ProxlImporterInteralException(msg);
			}
			if ( reportedPeptideAndPsmFilterableAnnotationTypesOnId.filterablePsmAnnotationTypesOnId.isEmpty() ) {
				String msg = "filterablePsmAnnotationTypesOnId.isEmpty() ";
				log.error( msg );
				throw new ProxlImporterInteralException(msg);
			}
			
			log.warn( "INFO:  !!  Starting to process Reported Peptides and PSMs");

			ProcessReportedPeptidesAndPSMs.getInstance().processReportedPeptides( 
					proxlInput, 
					searchDTO, 
					linkersDBDataSingleSearchRoot,
					dropPeptidePSMCutoffValues,
					searchProgramEntryMap,
					reportedPeptideAndPsmFilterableAnnotationTypesOnId,
					mapOfScanFilenamesMapsOfScanNumbersToScanIds );
			

			//  Commit all inserts executed to this point
			ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
			
			if ( log.isInfoEnabled() ) {
				log.info( "Primary insert of search complete.  Now performing Updates to the search" );
			}
			
			//  After primary insert search processing, perform other required updates to search
			PerformPostInsertSearchProcessing.getInstance()
			.performPostInsertSearchProcessing( searchDTOInserted, reportedPeptideAndPsmFilterableAnnotationTypesOnId );

			
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 
	 *
	 */
	public static class ReportedPeptideAndPsmFilterableAnnotationTypesOnId {
		
		private Map<Integer, AnnotationTypeDTO> filterableReportedPeptideAnnotationTypesOnId;
		private Map<Integer, AnnotationTypeDTO> filterablePsmAnnotationTypesOnId;
		private Map<Integer, AnnotationTypeDTO> filterablePsmPerPeptideAnnotationTypesOnId;
		
		public Map<Integer, AnnotationTypeDTO> getFilterableReportedPeptideAnnotationTypesOnId() {
			return filterableReportedPeptideAnnotationTypesOnId;
		}
		public void setFilterableReportedPeptideAnnotationTypesOnId(
				Map<Integer, AnnotationTypeDTO> filterableReportedPeptideAnnotationTypesOnId) {
			this.filterableReportedPeptideAnnotationTypesOnId = filterableReportedPeptideAnnotationTypesOnId;
		}
		public Map<Integer, AnnotationTypeDTO> getFilterablePsmAnnotationTypesOnId() {
			return filterablePsmAnnotationTypesOnId;
		}
		public void setFilterablePsmAnnotationTypesOnId(Map<Integer, AnnotationTypeDTO> filterablePsmAnnotationTypesOnId) {
			this.filterablePsmAnnotationTypesOnId = filterablePsmAnnotationTypesOnId;
		}
		public Map<Integer, AnnotationTypeDTO> getFilterablePsmPerPeptideAnnotationTypesOnId() {
			return filterablePsmPerPeptideAnnotationTypesOnId;
		}
		public void setFilterablePsmPerPeptideAnnotationTypesOnId(
				Map<Integer, AnnotationTypeDTO> filterablePsmPerPeptideAnnotationTypesOnId) {
			this.filterablePsmPerPeptideAnnotationTypesOnId = filterablePsmPerPeptideAnnotationTypesOnId;
		}
	}
	

	/**
	 * @param searchProgramEntryMap
	 * @return
	 */
	private Map<Integer, AnnotationTypeDTO> createReportedPeptideFilterableAnnotationTypesOnId( Map<String, SearchProgramEntry> searchProgramEntryMap ) {
	
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
		return filterableAnnotationTypesOnId;
	}
	
	/**
	 * @param searchProgramEntryMap
	 * @return
	 */
	private Map<Integer, AnnotationTypeDTO> createPsmFilterableAnnotationTypesOnId( Map<String, SearchProgramEntry> searchProgramEntryMap ) {
		
		///  Build list of Filterable annotation type ids
		Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesOnId = new HashMap<>();
		for ( Map.Entry<String, SearchProgramEntry> searchProgramEntryMapEntry : searchProgramEntryMap.entrySet() ) {
			SearchProgramEntry searchProgramEntry = searchProgramEntryMapEntry.getValue();
			Map<String, AnnotationTypeDTO> psmAnnotationTypeDTOMap =
					searchProgramEntry.getPsmAnnotationTypeDTOMap();
			for ( Map.Entry<String, AnnotationTypeDTO> psmAnnotationTypeDTOMapEntry : psmAnnotationTypeDTOMap.entrySet() ) {
				AnnotationTypeDTO psmAnnotationTypeDTO = psmAnnotationTypeDTOMapEntry.getValue();
				if ( psmAnnotationTypeDTO.getFilterableDescriptiveAnnotationType()
						== FilterableDescriptiveAnnotationType.FILTERABLE ) {
					filterableAnnotationTypesOnId.put( psmAnnotationTypeDTO.getId(), psmAnnotationTypeDTO );
				}
			}
		}
		return filterableAnnotationTypesOnId;
	}

	/**
	 * @param searchProgramEntryMap
	 * @return
	 */
	private Map<Integer, AnnotationTypeDTO> createPsmPerPeptideFilterableAnnotationTypesOnId( Map<String, SearchProgramEntry> searchProgramEntryMap ) {
		
		///  Build list of Filterable annotation type ids
		Map<Integer, AnnotationTypeDTO> filterableAnnotationTypesOnId = new HashMap<>();
		for ( Map.Entry<String, SearchProgramEntry> searchProgramEntryMapEntry : searchProgramEntryMap.entrySet() ) {
			SearchProgramEntry searchProgramEntry = searchProgramEntryMapEntry.getValue();
			Map<String, AnnotationTypeDTO> psmPerPeptideAnnotationTypeDTOMap =
					searchProgramEntry.getPsmPerPeptideAnnotationTypeDTOMap();
			for ( Map.Entry<String, AnnotationTypeDTO> psmPerPeptideAnnotationTypeDTOMapEntry : psmPerPeptideAnnotationTypeDTOMap.entrySet() ) {
				AnnotationTypeDTO psmPerPeptideAnnotationTypeDTO = psmPerPeptideAnnotationTypeDTOMapEntry.getValue();
				if ( psmPerPeptideAnnotationTypeDTO.getFilterableDescriptiveAnnotationType()
						== FilterableDescriptiveAnnotationType.FILTERABLE ) {
					filterableAnnotationTypesOnId.put( psmPerPeptideAnnotationTypeDTO.getId(), psmPerPeptideAnnotationTypeDTO );
				}
			}
		}
		return filterableAnnotationTypesOnId;
	}
	
	
	/**
	 * At least one "peptide" under a "reported_peptide" contains "peptide_isotope_label"
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	private boolean peptideContainsIsotopeLabel( ProxlInput proxlInput ) throws ProxlImporterDataException {
		
		ReportedPeptides reportedPeptides = proxlInput.getReportedPeptides();
		if ( reportedPeptides != null ) {
			List<ReportedPeptide> reportedPeptideList =
					reportedPeptides.getReportedPeptide();
			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {
				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
					if ( peptideContainsIsotopeLabel_ProcessSingleReportedPeptide( reportedPeptide ) ) {
						//  This reported peptide contains at least one peptide that contains "peptide_isotope_label"
						return true;  //  EARLY RETURN
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * @param reportedPeptide
	 * @throws ProxlImporterDataException
	 */
	private boolean peptideContainsIsotopeLabel_ProcessSingleReportedPeptide( ReportedPeptide reportedPeptide ) throws ProxlImporterDataException {
		
		Peptides peptides = reportedPeptide.getPeptides();
		if ( peptides == null ) {
			String msg = "No Peptides under reported peptide id: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
		List<Peptide> peptideList = peptides.getPeptide();
		if ( peptideList == null || peptideList.isEmpty() ) {
			String msg = "No Peptides under reported peptide id: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
		
		for ( Peptide peptide : peptideList ) {
			PeptideIsotopeLabels peptideIsotopeLabels = peptide.getPeptideIsotopeLabels();
			if ( peptideIsotopeLabels != null ) {
				PeptideIsotopeLabel peptideIsotopeLabel = peptideIsotopeLabels.getPeptideIsotopeLabel();
				if ( StringUtils.isNotEmpty( peptideIsotopeLabel.getLabel() ) ) {
					// Peptide contains a isotope label
					return true; // EARLY RETURN
				}
			}
		}
		
		return false;
	}
	
}
