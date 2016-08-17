package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFile;
import org.yeastrc.proxl_import.api.xml_dto.ConfigurationFiles;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.xlink.base.constants.ConfigFileMimeType;
import org.yeastrc.xlink.dao.SearchFileDAO;
import org.yeastrc.xlink.dto.SearchFileDTO;


/**
 * 
 *
 */
public class ProcessConfigurationFiles {
	
	private static final Logger log = Logger.getLogger( ProcessConfigurationFiles.class );
	
	/**
	 * private constructor
	 */
	private ProcessConfigurationFiles(){}
	
	public static ProcessConfigurationFiles getInstance() {
		
		return new ProcessConfigurationFiles();
	}

	
	
	/**
	 * @param proxlInput
	 * @param searchId
	 * @throws Exception 
	 */
	public void processConfigurationFiles( ProxlInput proxlInput, int searchId ) throws Exception {
		
		ConfigurationFiles configurationFiles =
				proxlInput.getConfigurationFiles();
		
		if ( configurationFiles != null ) {

			List<ConfigurationFile> configurationFileList =
					configurationFiles.getConfigurationFile();

			if ( configurationFileList != null && ( ! configurationFileList.isEmpty() ) ) {


				SearchFileDAO searchFileDAO = SearchFileDAO.getInstance();

				for ( ConfigurationFile configurationFile : configurationFileList ) {

					long fileSize = 0;
					
					if ( configurationFile.getFileContent() != null ) {
						
						fileSize = configurationFile.getFileContent().length;
					}

					SearchFileDTO searchFileDTO = new SearchFileDTO();


					searchFileDTO.setSearchId(  searchId );

					searchFileDTO.setMimeType( ConfigFileMimeType.CONFIG_FILE_MIME_TYPE );
					searchFileDTO.setFilename( configurationFile.getFileName() );

					searchFileDTO.setFileSize( fileSize );
					
					
					// TODO  Set Search program on configuration file
					//				configurationFile.getSearchProgram();
					//				searchFileDTO.set

					searchFileDAO.save( searchFileDTO );

					searchFileDAO.saveData( searchFileDTO.getId(), configurationFile.getFileContent() );
				}
			}
		}
	}
}