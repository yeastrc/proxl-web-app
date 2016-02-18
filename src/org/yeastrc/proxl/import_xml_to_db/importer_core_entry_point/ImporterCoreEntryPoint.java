package org.yeastrc.proxl.import_xml_to_db.importer_core_entry_point;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.FASTADatabaseLookup;
import org.yeastrc.proxl.import_xml_to_db.exceptions.PrintHelpOnlyException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.main.ImportPostProcessingPerSearch;
import org.yeastrc.proxl.import_xml_to_db.pre_validate_xml.ValidateAnnotationNamesUniqueWithinSearchProgramAndType;
import org.yeastrc.proxl.import_xml_to_db.pre_validate_xml.ValidateScanFilenamesInXMLAreOnCommandLine;
import org.yeastrc.proxl.import_xml_to_db.process_input.ProcessProxlInput;
import org.yeastrc.proxl.import_xml_to_db.project_importable_validation.IsImportingAllowForProject;
import org.yeastrc.proxl_import.api.xml_dto.Linker;
import org.yeastrc.proxl_import.api.xml_dto.Linkers;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.utils.IsDynamicModMassAMonolink;



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
	
	
	public void doImport( 
			
			int projectId,
			File mainXMLFileToImport,
			List<File> scanFileList 
			
			) throws Exception {
		

		ProxlInput proxlInputForImport = null;


		//  Unmarshall the main import file


		InputStream inputStream = null;

		try {

			inputStream = new FileInputStream( mainXMLFileToImport );

			JAXBContext jaxbContext = JAXBContext.newInstance( ProxlInput.class );

			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			//  TODO  Uncomment to validate against schema

			//				URL xmlSchemaURL = this.getClass().getResource( "/proxl-xml-v1.0.xsd" );
			//				
			//				SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
			//				Schema schema = sf.newSchema( xmlSchemaURL );
			//				
			//				unmarshaller.setSchema(schema);

			Object unmarshalledObject = null;

			try {

				unmarshalledObject = unmarshaller.unmarshal( inputStream );

			} catch ( Exception e ) {

				throw e;
			}


			if ( ! ( unmarshalledObject instanceof ProxlInput ) ) {

				String msg = "Object unmarshalled from " + mainXMLFileToImport.getAbsolutePath() 
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

		} finally {

			if ( inputStream != null ) {

				inputStream.close();
			}
		}

		doImportPassingDeserializedImportInputXML( projectId, proxlInputForImport, scanFileList );
		
	}
		

	/**
	 * @param projectId
	 * @param proxlInputForImport
	 * @param scanFileList
	 * @throws Exception
	 */
	public void doImportPassingDeserializedImportInputXML( 

			int projectId,
			ProxlInput proxlInputForImport,
			List<File> scanFileList 

			) throws Exception {

		

		try {

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
			ValidateAnnotationNamesUniqueWithinSearchProgramAndType.getInstance().validateAnnotationNamesUniqueWithinSearchProgramAndType( proxlInputForImport );

			
			

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


			//  Confirm Linkers are supported:

			Linkers proxlInputLinkers = proxlInputForImport.getLinkers();

			if ( proxlInputLinkers == null ) {

				String msg = "at least one linker is required";
				log.error( msg );

				throw new ProxlImporterDataException(msg);
			}

			//			List<Linker> proxlInputLinkerList = proxlInputLinkers.getLinker();
			//
			//			if ( proxlInputLinkerList == null || proxlInputLinkerList.isEmpty() ) {
			//					
			//
			//				String msg = "at least one linker is required";
			//				log.error( msg );
			//				
			//				throw new ProxlImporterDataException(msg);
			//			}


			Linker proxlInputLinker = proxlInputLinkers.getLinker();


			//  Put in a list to support more than one linker later

			List<Linker> proxlInputLinkerList = new ArrayList<>();

			proxlInputLinkerList.add( proxlInputLinker );



			//  TODO  !!!!!!!   Many places in the code need to be changed if this is removed.  Look for IsDynamicModMassAMonolink

			if ( proxlInputLinkerList.size() > 1 ) {


				String msg = "Only one linker is allowed at this time";
				log.error( msg );

				throw new ProxlImporterDataException(msg);

			}

			//  verify all linkers are supported in the code

			for ( Linker proxlInputLinkerItem : proxlInputLinkerList ) {

				String linkerAbbr = proxlInputLinkerItem.getName();

				try {

					GetLinkerFactory.getLinkerForAbbr( linkerAbbr );

					IsDynamicModMassAMonolink.init( linkerAbbr );

				} catch ( Exception e ) {


					throw e;
				}
			}


			//  Process proxl Input

			processProxlInput = ProcessProxlInput.getInstance();

			SearchDTO searchDTOInserted =

					processProxlInput.processProxlInput( projectId, proxlInputForImport, scanFileList, nrseqDatabaseId );

			

			System.out.println( "!!!!");

			System.out.println( "Insert done for core tables for search ID " + searchDTOInserted.getId() + ".");

			System.out.println( "!!!!");


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
