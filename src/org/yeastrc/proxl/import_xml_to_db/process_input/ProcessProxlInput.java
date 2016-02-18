package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.process_scans.Process_MzML_MzXml_File;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabel;
import org.yeastrc.proxl_import.api.xml_dto.DecoyLabels;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.xlink.dao.SearchCommentDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dao.SearchLinkerDAO;
import org.yeastrc.xlink.dto.SearchCommentDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.SearchLinkerDTO;
import org.yeastrc.xlink.utils.IsDynamicModMassAMonolink;

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
			
			int nrseqDatabaseId
			
			) throws Exception {
		
		
		
		searchDTOInserted = null;


		try {
			
			SearchDTO searchDTO = new SearchDTO();
			
			searchDTO.setFastaFilename( proxlInput.getFastaFilename() );
			searchDTO.setProjectId( projectId );
			
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
			
			
			{
				//  Save Linker mapping for search

			    int linkerId = IsDynamicModMassAMonolink.getInstance().getLinkerDTO().getId();

			    SearchLinkerDTO searchLinkerDTO = new SearchLinkerDTO();
			    
			    searchLinkerDTO.setSearchId( searchDTO.getId() );
			    searchLinkerDTO.setLinkerId( linkerId );
			    
			    SearchLinkerDAO.getInstance().saveToDatabase( searchLinkerDTO );
			    
			    
			}
			
			

			ProcessStaticModifications.getInstance().processStaticModifications( proxlInput, searchDTO.getId() );
			
			ProcessConfigurationFiles.getInstance().processConfigurationFiles( proxlInput, searchDTO.getId() );

			//  TODO  Must load linkers in a Per Search way
//			proxlInput.getLinkers();
			
			//  Scan Numbers to Scan Ids Map per Scan Filename
			Map<String, Map<Integer,Integer>> mapOfScanFilenamesMapsOfScanNumbersToScanIds = new HashMap<>();
			
			if ( scanFileList != null && ( ! scanFileList.isEmpty() ) ) {

				//  Scan Numbers in the input XML that need to be read from the scan files and inserted into the DB
				Map<String, Set<Integer>> mapOfScanFilenamesSetsOfScanNumbers = 
						GetScanFilenamesAndScanNumbersToInsert.getInstance().getScanFilenamesAndScanNumbersToInsert( proxlInput );
				
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
			

			ProcessReportedPeptidesAndPSMs.getInstance().processReportedPeptides( 
					proxlInput, 
					nrseqDatabaseId, 
					proteinNameDecoyPrefixList, 
					searchDTO.getId(), 
					searchProgramEntryMap,
					mapOfScanFilenamesMapsOfScanNumbersToScanIds );
		
			

			
			
		} catch ( Exception e ) {
			
			
			throw e;
		}
		
		return searchDTOInserted;
	}
}
