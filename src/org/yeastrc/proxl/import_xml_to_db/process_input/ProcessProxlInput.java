package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.SearchDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_LinkerPerSearchCrosslinkMassDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_LinkerPerSearchMonolinkMassDAO;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs.DropPeptidePSMCutoffValues;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cmd_line_cutoffs.DropPeptidePSM_InsertToDB;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.process_scans.Process_MzML_MzXml_File;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMass;
import org.yeastrc.proxl_import.api.xml_dto.CrosslinkMasses;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabel;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabels;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.MonolinkMass;
import org.yeastrc.proxl_import.api.xml_dto.MonolinkMasses;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.xlink.dao.LinkerDAO;
import org.yeastrc.xlink.dao.SearchCommentDAO;
import org.yeastrc.xlink.dao.SearchLinkerDAO;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchMonolinkMassDTO;
import org.yeastrc.xlink.dto.SearchCommentDTO;
import org.yeastrc.xlink.dto.SearchLinkerDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;

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
	
	private SearchDTO searchDTOInserted;
	
	
	public SearchDTO getSearchDTOInserted() {
		return searchDTOInserted;
	}

	/**
	 * @param projectId
	 * @param proxlInput
	 * @param scanFileList
	 * @param nrseqDatabaseId
	 * @return
	 * @throws Exception
	 */
	public SearchDTO processProxlInput( 
			
			int projectId,
			ProxlInput proxlInput,
			List<File> scanFileList,
			
			String importDirectory,
			
			int nrseqDatabaseId,
			

			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues
			
			
			) throws Exception {
		
		
		
		searchDTOInserted = null;


		try {
			
			SearchDTO searchDTO = new SearchDTO();
			
			searchDTO.setFastaFilename( proxlInput.getFastaFilename() );
			searchDTO.setProjectId( projectId );
			
			searchDTO.setPath( importDirectory );
			
			if ( StringUtils.isNotEmpty( proxlInput.getName() ) ) {
			
				searchDTO.setName( proxlInput.getName() );
			}
			
			if ( scanFileList == null || scanFileList.isEmpty() ) {

				searchDTO.setNoScanData( true );
			}
			
			SearchDAO.getInstance().saveToDatabase( searchDTO );
			
			searchDTOInserted = searchDTO;

			if ( StringUtils.isNotEmpty( proxlInput.getComment() ) ) {

				SearchCommentDTO searchCommentDTO = new SearchCommentDTO();
				
				searchCommentDTO.setSearchid( searchDTO.getId() );
				searchCommentDTO.setComment( proxlInput.getComment() );

				searchCommentDTO.setAuthUserId(null);
				
				SearchCommentDAO.getInstance().save( searchCommentDTO );
			}
			
			
			///   process Linkers
			
			processLinkersValidateAndSave(proxlInput, searchDTO);
			
			

			ProcessStaticModifications.getInstance().processStaticModifications( proxlInput, searchDTO.getId() );
			
			ProcessConfigurationFiles.getInstance().processConfigurationFiles( proxlInput, searchDTO.getId() );

			//  TODO  Must load linkers in a Per Search way
//			proxlInput.getLinkers();
			
			//  Scan Numbers to Scan Ids Map per Scan Filename
			Map<String, Map<Integer,Integer>> mapOfScanFilenamesMapsOfScanNumbersToScanIds = new HashMap<>();
			
			if ( scanFileList != null && ( ! scanFileList.isEmpty() ) ) {

				//  Scan Numbers in the input XML that need to be read from the scan files and inserted into the DB
				Map<String, Set<Integer>> mapOfScanFilenamesSetsOfScanNumbers = 
						GetScanFilenamesAndScanNumbersToInsert.getInstance()
						.getScanFilenamesAndScanNumbersToInsert( proxlInput, dropPeptidePSMCutoffValues );
				
				if ( scanFileList.size() == 1 ) {
					
					if ( mapOfScanFilenamesSetsOfScanNumbers.size() != 1 ) {
						
						
						
					}
					
					File scanFile = scanFileList.get( 0 );

					String scanFilenameString = scanFile.getName();

					
					Set<Integer> scanNumbersToLoadSet = mapOfScanFilenamesSetsOfScanNumbers.entrySet().iterator().next().getValue();
				
					int[] scanNumbersToLoadIntArray = new int[ scanNumbersToLoadSet.size() ];

					int index = 0;
					for ( Integer scanNumberToLoad : scanNumbersToLoadSet ) {

						scanNumbersToLoadIntArray[ index ] = scanNumberToLoad;
						index++;
					}

					Map<Integer,Integer> mapOfScanNumbersToScanIds =
							Process_MzML_MzXml_File.getInstance()
							.processMzMLFileWithScanNumbersToLoad( scanFile, scanNumbersToLoadIntArray );

					mapOfScanFilenamesMapsOfScanNumbersToScanIds.put( scanFilenameString, mapOfScanNumbersToScanIds );
					
					
				} else {
				

					for ( File scanFile : scanFileList ) {

						String scanFilenameString = scanFile.getName();

						Set<Integer> scanNumbersToLoadSet = mapOfScanFilenamesSetsOfScanNumbers.get( scanFilenameString );

						if ( scanNumbersToLoadSet == null ) {

							String msg = "Scan Numbers not found for Scan File: " + scanFilenameString;
							log.warn( msg );
							
						} else {

							int[] scanNumbersToLoadIntArray = new int[ scanNumbersToLoadSet.size() ];
							
							int index = 0;
							for ( Integer scanNumberToLoad : scanNumbersToLoadSet ) {
								
								scanNumbersToLoadIntArray[ index ] = scanNumberToLoad;
								index++;
							}
							
							Map<Integer,Integer> mapOfScanNumbersToScanIds =
									Process_MzML_MzXml_File.getInstance()
									.processMzMLFileWithScanNumbersToLoad( scanFile, scanNumbersToLoadIntArray );
							

							mapOfScanFilenamesMapsOfScanNumbersToScanIds.put( scanFilenameString, mapOfScanNumbersToScanIds );
						}
					}
				}
			}
			
			//  create String list of decoy prefixes
			
			
			List<String> proteinNameDecoyPrefixList = new ArrayList<>();
			
			
			DecoyLabels decoyLabels = proxlInput.getDecoyLabels();
			
			if ( decoyLabels != null ) {
				
				List<DecoyLabel> decoyLabelList = decoyLabels.getDecoyLabel();
				
				if ( decoyLabelList != null && ( ! decoyLabelList.isEmpty() ) ) {
					
					for ( DecoyLabel decoyLabel : decoyLabelList ) {
					
						String decoyLabelPrefixString = decoyLabel.getPrefix();
						
						proteinNameDecoyPrefixList.add( decoyLabelPrefixString );
					}
				}
			}
			
			

			Map<String, SearchProgramEntry> searchProgramEntryMap =
					ProcessSearchProgramEntries.getInstance()
					.processSearchProgramEntries( proxlInput, searchDTO.getId() );
			
			
			DropPeptidePSM_InsertToDB.getInstance().insertDBEntries( dropPeptidePSMCutoffValues, searchProgramEntryMap );

			ProcessReportedPeptidesAndPSMs.getInstance().processReportedPeptides( 
					proxlInput, 
					nrseqDatabaseId, 
					proteinNameDecoyPrefixList, 
					searchDTO, 

					dropPeptidePSMCutoffValues,
					
					searchProgramEntryMap,
					mapOfScanFilenamesMapsOfScanNumbersToScanIds );
		
			

			
			
		} catch ( Exception e ) {
			
			
			throw e;
		}
		
		return searchDTOInserted;
	}

	
	
	
	/**
	 * Validate that the linkers are supported and save linker data for the search
	 * 
	 * @param proxlInput
	 * @param searchDTO
	 * @throws ProxlImporterDataException
	 * @throws Exception
	 */
	private void processLinkersValidateAndSave( ProxlInput proxlInput, SearchDTO searchDTO ) throws ProxlImporterDataException, Exception {
		
		

		// Save Linker mapping for search



		Linkers proxlInputLinkers = proxlInput.getLinkers();

		if ( proxlInputLinkers == null ) {

			String msg = "at least one linker is required";
			log.error( msg );

			throw new ProxlImporterDataException(msg);
		}



		List<Linker> proxlInputLinkerList = proxlInputLinkers.getLinker();

		for ( Linker proxlInputLinkerItem : proxlInputLinkerList ) {

			String linkerAbbr = proxlInputLinkerItem.getName();

			try {
				
				//  throws exception if linker abbr not supported in the code
				GetLinkerFactory.getLinkerForAbbr( linkerAbbr );


				LinkerDTO linkerDTO = LinkerDAO.getInstance().getLinkerDTOForAbbr( linkerAbbr );

				if ( linkerDTO == null ) {

					String msg = "No 'linker' record found for 'abbr': " + linkerAbbr;
					throw new ProxlImporterDataException( msg );
				}


				SearchLinkerDTO searchLinkerDTO = new SearchLinkerDTO();

				searchLinkerDTO.setSearchId( searchDTO.getId() );
				searchLinkerDTO.setLinkerId( linkerDTO.getId() );

				SearchLinkerDAO.getInstance().saveToDatabase( searchLinkerDTO );
				
				saveMonolinkMasses( proxlInputLinkerItem, linkerDTO, searchDTO );
				saveCrosslinkMasses( proxlInputLinkerItem, linkerDTO, searchDTO );
				

			} catch ( Exception e ) {


				throw e;
			}
		}
	}
	
	/**
	 * @param proxlInputLinkerItem
	 * @param linkerDTO
	 * @param searchDTO
	 * @throws Exception
	 */
	private void saveMonolinkMasses( Linker proxlInputLinkerItem, LinkerDTO linkerDTO, SearchDTO searchDTO ) throws Exception {
		
		MonolinkMasses monolinkMasses = proxlInputLinkerItem.getMonolinkMasses();
		
		if ( monolinkMasses == null ) {
			
			return;  //  EARLY RETURN
		}
		
		List<MonolinkMass> monolinkMassList = monolinkMasses.getMonolinkMass();

		if ( monolinkMassList == null ) {
			
			return;  //  EARLY RETURN
		}
		
		for ( MonolinkMass monolinkMass : monolinkMassList ) {
			
			LinkerPerSearchMonolinkMassDTO linkerPerSearchMonolinkMassDTO = new LinkerPerSearchMonolinkMassDTO();
			
			linkerPerSearchMonolinkMassDTO.setLinkerId( linkerDTO.getId() );
			linkerPerSearchMonolinkMassDTO.setSearchId(  searchDTO.getId() );
			linkerPerSearchMonolinkMassDTO.setMonolinkMassDouble( monolinkMass.getMass().doubleValue() );
			linkerPerSearchMonolinkMassDTO.setMonolinkMassString( monolinkMass.getMass().toString() );
			
			DB_Insert_LinkerPerSearchMonolinkMassDAO.getInstance().save( linkerPerSearchMonolinkMassDTO );
		}
		
		
		
	}
	
	

	/**
	 * @param proxlInputLinkerItem
	 * @param linkerDTO
	 * @param searchDTO
	 * @throws Exception
	 */
	private void saveCrosslinkMasses( Linker proxlInputLinkerItem, LinkerDTO linkerDTO, SearchDTO searchDTO ) throws Exception {
		
		CrosslinkMasses crosslinkMasses = proxlInputLinkerItem.getCrosslinkMasses();
		
		if ( crosslinkMasses == null ) {
			
			return;  //  EARLY RETURN
		}
		
		List<CrosslinkMass> crosslinkMassList = crosslinkMasses.getCrosslinkMass();

		if ( crosslinkMassList == null ) {
			
			return;  //  EARLY RETURN
		}
		
		for ( CrosslinkMass crosslinkMass : crosslinkMassList ) {
			
			LinkerPerSearchCrosslinkMassDTO linkerPerSearchCrosslinkMassDTO = new LinkerPerSearchCrosslinkMassDTO();
			
			linkerPerSearchCrosslinkMassDTO.setLinkerId( linkerDTO.getId() );
			linkerPerSearchCrosslinkMassDTO.setSearchId(  searchDTO.getId() );
			linkerPerSearchCrosslinkMassDTO.setCrosslinkMassDouble( crosslinkMass.getMass().doubleValue() );
			linkerPerSearchCrosslinkMassDTO.setCrosslinkMassString( crosslinkMass.getMass().toString() );
			
			DB_Insert_LinkerPerSearchCrosslinkMassDAO.getInstance().save( linkerPerSearchCrosslinkMassDTO );
		}
		
		
		
	}

}
