package org.yeastrc.proxl.import_xml_to_db.importer_core_entry_point;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.constants.Proxl_XSD_XML_Schema_Enabled_And_Filename_With_Path_Constant;
import org.yeastrc.proxl.import_xml_to_db.dao.ProjectSearchDAO_Importer;
import org.yeastrc.proxl.import_xml_to_db.dao.SearchDAO_Importer;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs.DropPeptidePSMCutoffValues;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs.DropPeptidePSMPopulateFromProxlXMLInput;
import org.yeastrc.proxl.import_xml_to_db.dto.ProjectSearchDTO_Importer;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchDTO_Importer;
import org.yeastrc.proxl.import_xml_to_db.exception.ProxlImporterProjectNotAllowImportException;
import org.yeastrc.proxl.import_xml_to_db.exception.ProxlImporterProxlXMLDeserializeFailException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.ProxlInputObjectContainer;
import org.yeastrc.proxl.import_xml_to_db.objects.ScanFileFileContainer;
import org.yeastrc.proxl.import_xml_to_db.pre_validate_xml.ValidateAnnotationTypeRecords;
import org.yeastrc.proxl.import_xml_to_db.pre_validate_xml.ValidateMatchedProteinSection;
import org.yeastrc.proxl.import_xml_to_db.pre_validate_xml.ValidateScanFilenamesInXMLAreOnCommandLine;
import org.yeastrc.proxl.import_xml_to_db.process_input.ProcessProxlInput;
import org.yeastrc.proxl.import_xml_to_db.project_importable_validation.IsImportingAllowForProject;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.xlink.enum_classes.SearchRecordStatus;

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
	
	private volatile boolean shutdownRequested = false;
	
	/**
	 * @param projectId
	 * @param searchNameOverrideValue
	 * @param importDirectoryOverrideValue
	 * @param mainXMLFileToImport
	 * @param proxlInputForImportParam
	 * @param scanFileFileContainerList
	 * @param dropPeptidePSMCutoffValues
	 * @param skipPopulatingPathOnSearchLineOptChosen
	 * @return
	 * @throws Exception
	 * @throws ProxlImporterProjectNotAllowImportException
	 * @throws ProxlImporterProxlXMLDeserializeFailException
	 */
	public int doImport( 
			int projectId,
			Integer userIdInsertingSearch,
			String searchNameOverrideValue,
			String importDirectoryOverrideValue,
			File mainXMLFileToImport,
			ProxlInput proxlInputForImportParam,
			List<ScanFileFileContainer> scanFileFileContainerList,
			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues,
			Boolean skipPopulatingPathOnSearchLineOptChosen,
			Boolean doNotUseCutoffInInputFile 
			) throws Exception, ProxlImporterProjectNotAllowImportException, ProxlImporterProxlXMLDeserializeFailException {
		
		ProxlInput proxlInputForImport = null;
		if ( proxlInputForImportParam != null ) {
			proxlInputForImport = proxlInputForImportParam;
		}
		String importDirectory = null; 
		if ( StringUtils.isNotEmpty( importDirectoryOverrideValue ) ) {
			importDirectory = importDirectoryOverrideValue;
		} else {
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
		}
		if ( proxlInputForImport == null ) {
			//  main import file not provided as an object so unmarshall the file
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
		}
		//  If a searchName is provided, override the one in the Proxl XML file
		if ( StringUtils.isNotEmpty( searchNameOverrideValue ) ) {
			proxlInputForImport.setName( searchNameOverrideValue );
		}
		ProxlInputObjectContainer proxlInputObjectContainer = new ProxlInputObjectContainer();
		proxlInputObjectContainer.setProxlInput( proxlInputForImport );
		proxlInputForImport = null; //  release this reference
		int insertedSearchId = doImportPassingDeserializedProxlImportInputXML( 
				projectId, 
				userIdInsertingSearch,
				proxlInputObjectContainer, 
				scanFileFileContainerList, 
				importDirectory, 
				dropPeptidePSMCutoffValues, 
				skipPopulatingPathOnSearchLineOptChosen,
				doNotUseCutoffInInputFile );
		return insertedSearchId;
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
			) throws Exception, ProxlImporterProxlXMLDeserializeFailException {
		
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
				System.out.println( "Exception in deserializing the primary input XML file" );
				System.err.println( "Exception in deserializing the primary input XML file" );
				e.printStackTrace( System.out );
				e.printStackTrace( System.err );
				throw new ProxlImporterProxlXMLDeserializeFailException( e.toString() , e ); 
			}
			if ( ! ( unmarshalledObject instanceof ProxlInput ) ) {
				String msg = "Object unmarshalled "
						+ " cannot be cast to ProxlInput.  unmarshalledObject.getClass().getCanonicalName(): " + unmarshalledObject.getClass().getCanonicalName();
				System.err.println( msg );
				System.out.println( msg );
				throw new Exception(msg);
			}
			proxlInputForImport = (ProxlInput) unmarshalledObject;
		} catch ( ProxlImporterProxlXMLDeserializeFailException e ) {
			throw e;
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
	 * @return insertedSearchId
	 * @throws ProxlImporterProjectNotAllowImportException
	 * @throws Exception
	 */
	public int doImportPassingDeserializedProxlImportInputXML( 
			int projectId,
			Integer userIdInsertingSearch,
			ProxlInputObjectContainer proxlInputObjectContainer,
			List<ScanFileFileContainer> scanFileFileContainerList,
			String importDirectory,
			DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues,
			Boolean skipPopulatingPathOnSearchLineOptChosen,
			Boolean doNotUseCutoffInInputFile
			) throws Exception, ProxlImporterProjectNotAllowImportException {
		
		ProxlInput proxlInputForImport = proxlInputObjectContainer.getProxlInput();
		if ( proxlInputForImport.getMatchedProteins() == null ) {
			String msg = "<matched_proteins> is not populated in Proxl XML File.";
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
		///////////
		if ( dropPeptidePSMCutoffValues == null ) {
			dropPeptidePSMCutoffValues = new DropPeptidePSMCutoffValues();
		}
		try {
			//  isImportingAllowForProject(...) throws ProxlImporterProjectNotAllowImportException 
			//                                  when project not allows import
			//  isImportingAllowForProject(...) prints it's own error message
			IsImportingAllowForProject.getInstance().isImportingAllowForProject( projectId );
		} catch ( ProxlImporterProjectNotAllowImportException e ) {
			throw e;
		} catch( Exception e ) {
			System.err.println( "Error getting project" );
			System.err.println( "Error: " + e.getMessage() );
			throw e;
		}
		ProcessProxlInput processProxlInput = null; 
		try {
			//   Throws ProxlImporterDataException if data error found
			ValidateAnnotationTypeRecords.getInstance().validateAnnotationTypeRecords( proxlInputForImport );
			//   Throws ProxlImporterDataException if data error found
			ValidateMatchedProteinSection.getInstance().validateMatchedProteinSection( proxlInputForImport );
			//   Throws ProxlImporterDataException if data error found
			ValidateScanFilenamesInXMLAreOnCommandLine.getInstance().validateScanFilenamesInXMLAreOnCommandLine( proxlInputForImport, scanFileFileContainerList );
			if ( doNotUseCutoffInInputFile ) {
				dropPeptidePSMCutoffValues = new DropPeptidePSMCutoffValues();
			} else {
				//   Throws ProxlImporterDataException if data error found
				DropPeptidePSMPopulateFromProxlXMLInput.getInstance().populateFromProxlXMLInput( dropPeptidePSMCutoffValues, proxlInputForImport );
			}
			//  Process proxl Input
			processProxlInput = ProcessProxlInput.getInstance();
			processProxlInput.processProxlInput( 
					projectId, 
					userIdInsertingSearch,
					proxlInputForImport, 
					scanFileFileContainerList,
					importDirectory, 
					dropPeptidePSMCutoffValues,
					skipPopulatingPathOnSearchLineOptChosen
					);
			SearchDTO_Importer searchDTOInserted = processProxlInput.getSearchDTOInserted();
			ProjectSearchDTO_Importer projectSearchDTOInserted = processProxlInput.getProjectSearchDTOInserted();
			//  Set proxlInputForImport to null to release memory needed later, but right now no other code to run
			proxlInputForImport = null;
			proxlInputObjectContainer.setProxlInput( null );
			ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
			try {
				SearchDAO_Importer.getInstance().updateStatus( searchDTOInserted.getId(), SearchRecordStatus.IMPORT_COMPLETE_VIEW );
			}  catch ( Exception e ) {
				String msg = "Failed to mark the Search as ImportComplete, search id: " + searchDTOInserted.getId() ;
				log.error( msg );
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.err.println( msg );
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				throw e;
		    }
			try {
				ProjectSearchDAO_Importer.getInstance().updateStatus( projectSearchDTOInserted.getId(),  SearchRecordStatus.IMPORT_COMPLETE_VIEW );
			}  catch ( Exception e ) {
				String msg = "Failed to mark the project_search as ImportComplete, search id: " + searchDTOInserted.getId() ;
				log.error( msg );
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.err.println( msg );
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				throw e;
		    }
			if ( log.isInfoEnabled() ) {
				System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.out.println( "!!!!");
			}
			System.out.println( "Insert of search ID " + searchDTOInserted.getId() + " is complete and successful.");
			if ( log.isInfoEnabled() ) {
				System.out.println( "!!!!");
				System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
			int insertedSearchId = searchDTOInserted.getId();
			return insertedSearchId;
			
		} catch ( Exception e ) {
			if ( ! shutdownRequested ) {
				System.out.println( "Exception in processing" );
				System.err.println( "Exception in processing" );
				e.printStackTrace( System.out );
				e.printStackTrace( System.err );
				if ( processProxlInput != null ) {
					//  processProxlInput was instantiated to process the input so get data from it
					SearchDTO_Importer search = processProxlInput.getSearchDTOInserted();
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
						try {
							//  First commit the last insert transaction if needed
							ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
						}  catch ( Exception eUpd ) {
							//  Just ignore any exception
					    }
						try {
							SearchDAO_Importer.getInstance().updateStatus( search.getId(), SearchRecordStatus.IMPORT_FAIL );
						}  catch ( Exception eUpd ) {
							String msgeUpd = "Failed to mark the Search as ImportFail, search id: " + search.getId() ;
							log.error( msgeUpd, eUpd );
							System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							System.err.println( msgeUpd );
							System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					    }
						ProjectSearchDTO_Importer projectSearchDTOInserted = processProxlInput.getProjectSearchDTOInserted();
						try {
							ProjectSearchDAO_Importer.getInstance().updateStatus( projectSearchDTOInserted.getId(),  SearchRecordStatus.IMPORT_FAIL );
						}  catch ( Exception e2 ) {
							String msg_failUpd = "Failed to mark the project_search as IMPORT_FAIL, search id: " + search.getId() ;
							log.error( msg_failUpd, e2 );
							System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							System.err.println( msg );
							System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							throw e;
					    }						
					}
				}
			}
			throw e;
		}
	}
	
	public boolean isShutdownRequested() {
		return shutdownRequested;
	}
	public void setShutdownRequested(boolean shutdownRequested) {
		this.shutdownRequested = shutdownRequested;
	}
	
}