package org.yeastrc.proxl.import_xml_to_db.importer_core_entry_point;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.constants.Proxl_XSD_XML_Schema_Enabled_And_Filename_With_Path_Constant;
import org.yeastrc.proxl.import_xml_to_db.dao.FASTADatabaseLookup;
import org.yeastrc.proxl.import_xml_to_db.exceptions.PrintHelpOnlyException;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.main.ImportPostProcessingPerSearch;
import org.yeastrc.proxl.import_xml_to_db.objects.ProxlInputObjectContainer;
import org.yeastrc.proxl.import_xml_to_db.pre_validate_xml.ValidateAnnotationTypeRecords;
import org.yeastrc.proxl.import_xml_to_db.pre_validate_xml.ValidateScanFilenamesInXMLAreOnCommandLine;
import org.yeastrc.proxl.import_xml_to_db.process_input.ProcessProxlInput;
import org.yeastrc.proxl.import_xml_to_db.project_importable_validation.IsImportingAllowForProject;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.SearchDTO;



/**
 * This is the internal core entry point to running the importer.
 * 
 * 
 *
 */
public class ImporterCoreEntryPoint {

	private static final Logger log = Logger.getLogger( ImporterCoreEntryPoint.class );
	

	/**
	 * private constructor
	 */
	private ImporterCoreEntryPoint(){}
	
	public static ImporterCoreEntryPoint getInstance() {
		
		return new ImporterCoreEntryPoint();
	}
	
	
	/**
	 * @param projectId
	 * @param mainXMLFileToImport
	 * @param scanFileList
	 * @throws Exception
	 */
	public void doImport( 
			
			int projectId,
			File mainXMLFileToImport,
			List<File> scanFileList 
			
			) throws Exception {
		

		ProxlInput proxlInputForImport = null;

		String importDirectory = null; 
				
		try {
			
			File importFileCanonicalFile = mainXMLFileToImport.getCanonicalFile();
			
			if ( importFileCanonicalFile != null ) {

				File importFileParent = importFileCanonicalFile.getParentFile();

				if ( importFileParent != null ) {

					importDirectory = importFileParent.getCanonicalPath();

				} else {

					importDirectory = importFileCanonicalFile.getCanonicalPath();
				}
			} else {
				
				importDirectory = mainXMLFileToImport.getCanonicalPath();
			}
			
		} catch ( Exception e ) {
			
			String msg = "Error mainXMLFileToImport.getCanonicalPath() or importFileCanonicalFile.getParentFile() or importFileParent.getCanonicalPath()";
			log.error( msg, e );
			throw e;
		}
		

		//  Unmarshall the main import file


		InputStream inputStream = null;

		try {

			inputStream = new FileInputStream( mainXMLFileToImport );

			proxlInputForImport = deserializeProxlInputFromInputStream( inputStream );

		} catch ( Exception e ) {

			System.out.println( "Exception in deserializing the primary input XML file" );
			System.err.println( "Exception in deserializing the primary input XML file" );

			e.printStackTrace( System.out );
			e.printStackTrace( System.err );

			throw e;
			
		} finally {

			if ( inputStream != null ) {

				inputStream.close();
			}
		}
		

		ProxlInputObjectContainer proxlInputObjectContainer = new ProxlInputObjectContainer();
		
		proxlInputObjectContainer.setProxlInput( proxlInputForImport );
		
		proxlInputForImport = null; //  release this reference
		
		doImportPassingDeserializedProxlImportInputXML( projectId, proxlInputObjectContainer, scanFileList, importDirectory );
		
	}
	

	/**
	 * Utility method to get the ProxlInput from an input stream
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public ProxlInput deserializeProxlInputFromInputStream( 
	
			InputStream inputStream

			) throws Exception {


		//  Unmarshall the main import file


		ProxlInput proxlInputForImport = null;

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance( ProxlInput.class );

			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			
			if ( Proxl_XSD_XML_Schema_Enabled_And_Filename_With_Path_Constant.PROXL_XSD_XML_SCHEMA_VALIDATION_ENABLED ) {

				URL xmlSchemaURL = null;

				try {

					xmlSchemaURL = this.getClass().getResource( Proxl_XSD_XML_Schema_Enabled_And_Filename_With_Path_Constant.PROXL_XSD_XML_SCHEMA_FILENAME_WITH_PATH );

				} catch ( Exception e ) {

					String msg = "Exception Retrieving URL for Proxl XSD Schema file: " + Proxl_XSD_XML_Schema_Enabled_And_Filename_With_Path_Constant.PROXL_XSD_XML_SCHEMA_FILENAME_WITH_PATH;
					log.error( msg, e );
					throw e;
				}

				if ( xmlSchemaURL == null ) {

					String msg = "Error retrieving URL for Proxl XSD Schema file: " + Proxl_XSD_XML_Schema_Enabled_And_Filename_With_Path_Constant.PROXL_XSD_XML_SCHEMA_FILENAME_WITH_PATH;
					log.error( msg );
					throw new Exception( msg );
				}

				SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
				Schema schema = sf.newSchema( xmlSchemaURL );

				unmarshaller.setSchema(schema);

			}

			Object unmarshalledObject = null;

			try {

				unmarshalledObject = unmarshaller.unmarshal( inputStream );

			} catch ( Exception e ) {

				throw e;
			}


			if ( ! ( unmarshalledObject instanceof ProxlInput ) ) {

				String msg = "Object unmarshalled "
						+ " cannot be cast to ProxlInput.  unmarshalledObject.getClass().getCanonicalName(): " + unmarshalledObject.getClass().getCanonicalName();

				System.err.println( msg );
				System.out.println( msg );

				throw new Exception(msg);
			}

			proxlInputForImport = (ProxlInput) unmarshalledObject;

		} catch ( Exception e ) {

			System.out.println( "Exception in deserializing the primary input XML file" );
			System.err.println( "Exception in deserializing the primary input XML file" );

			e.printStackTrace( System.out );
			e.printStackTrace( System.err );

			throw e;

		}
		
		return proxlInputForImport;
	}
		

	/**
	 * 
	 * @param projectId
	 * @param proxlInputObjectContainer
	 * @param scanFileList
	 * @param importDirectory - displayed on website in the "Path:" field for logged in users
	 * @throws Exception
	 */
	public void doImportPassingDeserializedProxlImportInputXML( 

			int projectId,
			ProxlInputObjectContainer proxlInputObjectContainer,
			List<File> scanFileList,
			
			String importDirectory

			) throws Exception {

		
		ProxlInput proxlInputForImport = proxlInputObjectContainer.getProxlInput();

		try {

			//  isImportingAllowForProject(...) prints it's own error message
			
			boolean isImportingAllowForProject = IsImportingAllowForProject.getInstance().isImportingAllowForProject( projectId );
			
			if ( ! isImportingAllowForProject ) {
				
				throw new PrintHelpOnlyException();
			}
			
    		
		} catch( Exception e ) {
			System.err.println( "Error getting project" );
			System.err.println( "Error: " + e.getMessage() );
			
			throw e;
			
		}
		
		
		ProcessProxlInput processProxlInput = null; 
		
		try {


			//   Throws Exception if data error found
			ValidateAnnotationTypeRecords.getInstance().validateAnnotationTypeRecords( proxlInputForImport );

			
			

			//   Throws Exception if data error found
			ValidateScanFilenamesInXMLAreOnCommandLine.getInstance().validateScanFilenamesInXMLAreOnCommandLine( proxlInputForImport, scanFileList );

			
			
			String fastaFilename = proxlInputForImport.getFastaFilename();


			//  Confirm Fasta file is in the YRC_NRSEQ DB

			int nrseqDatabaseId = 0;


			try {
				//  throws Exception if fasta filename is not found
				nrseqDatabaseId = FASTADatabaseLookup.getInstance().lookupDatabase( fastaFilename );
			} catch( Exception e ) {
				System.err.println( "Could not find a parsed FASTA file named: " + fastaFilename );
				System.err.println( "Error: " + e.getMessage() );
				System.err.println( "Is the name correct? Has it been parsed?" );

				throw e;

				//System.exit( 0 );
			}

			//  Process proxl Input

			processProxlInput = ProcessProxlInput.getInstance();

			SearchDTO searchDTOInserted =

					processProxlInput.processProxlInput( projectId, proxlInputForImport, scanFileList, importDirectory, nrseqDatabaseId );

			

			System.out.println( "!!!!");

			System.out.println( "Insert done for core tables for search ID " + searchDTOInserted.getId() + ".");
			System.out.println( "Now: " + new Date() );

			System.out.println( "!!!!");
			

			//  Set proxlInputForImport to null to release memory needed by ImportPostProcessingPerSearch
			
			proxlInputForImport = null;
			proxlInputObjectContainer.setProxlInput( null );


			System.out.println( "!!!!");

			System.out.println( "Starting Insert of lookup tables for search ID " + searchDTOInserted.getId() );

			System.out.println( "!!!!");


			
			ImportPostProcessingPerSearch.importPostProcessingPerSearch( searchDTOInserted.getId() );

			

			try {
				
				SearchDAO.getInstance().updateInsertComplete( searchDTOInserted.getId(), true /* insertComplete */ );
			}  catch ( Exception e ) {
		    	

				String msg = "Failed to mark the Search as InsertComplete, search id: " + searchDTOInserted.getId() ;

				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.err.println( msg );
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

				throw e;
		    }
			
			
			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println( "!!!!");

			System.out.println( "Insert of search ID " + searchDTOInserted.getId() + " is complete and successful.");

			System.out.println( "!!!!");
			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			
		} catch ( Exception e ) {

			System.out.println( "Exception in processing" );
			System.err.println( "Exception in processing" );

			e.printStackTrace( System.out );
			e.printStackTrace( System.err );

			if ( processProxlInput != null ) {
				
				//  processProxlInput was instantiated to process the input so get data from it

				SearchDTO search = processProxlInput.getSearchDTOInserted();

				if ( search != null ) {

					String msg = "search record inserted, but import not complete, search.id: " + search.getId()
							+ ", search.path: " + search.getPath();

					log.error( msg );

					System.out.println( "----------------------------------------");
					System.out.println( "----");
					System.out.println( msg );
					System.out.println( "----");
					System.out.println( "----------------------------------------");

					System.err.println( "----------------------------------------");
					System.err.println( "----");
					System.err.println( msg );
					System.err.println( "----");
					System.err.println( "----------------------------------------");

				}

			}

			throw e;
		}

	}

}