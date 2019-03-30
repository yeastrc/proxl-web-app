package org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.process_scans;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingSingleFileDTO;
import org.yeastrc.xlink.dto.ScanFileDTO;
import org.yeastrc.xlink.dto.ScanFileHeaderDTO;
import org.yeastrc.xlink.dto.ScanFileSourceDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterConfigException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterSpectralStorageServiceErrorException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterSpectralStorageServiceRetryExceededException;
import org.yeastrc.proxl.import_xml_to_db.objects.ScanFileFileContainer;
import org.yeastrc.proxl.import_xml_to_db.objects.ScanFilenameScanNumberScanIdScanFileId_Mapping;
import org.yeastrc.proxl.import_xml_to_db.file_import_proxl_xml_scans.dao.ProxlXMLFileImportTrackingSingleFile_Importer_DAO;
import org.yeastrc.proxl.import_xml_to_db.spectrum.db_update_with_transaction_services.AddNewScanFileAndHeadersIfNeededDBTransactionService;
import org.yeastrc.proxl.import_xml_to_db.spectrum.db_update_with_transaction_services.InsertNewScanAndPrescanIfNeededDBTransactionService;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.dto.MzML_MzXmlHeader;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.dto.MzML_MzXmlScan;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.reader.MzMl_MzXml_FileReader;
import org.yeastrc.proxl.import_xml_to_db.spectrum.spectral_storage_service_interface.ScanFileToSpectralStorageService_Processing;
import org.yeastrc.proxl.import_xml_to_db.utils.SHA1SumCalculator;

/**
 * Ensure all contents of throw new ProxlImporterDataException(...) 
 * does not contain the scan filename and path on the file system.
 *
 */
public class Process_MzML_MzXml_File {
	
	private static final Logger log = LoggerFactory.getLogger( Process_MzML_MzXml_File.class);
	/**
	 * private constructor
	 */
	private Process_MzML_MzXml_File(){}
	public static Process_MzML_MzXml_File getInstance( ) throws Exception {
		Process_MzML_MzXml_File instance = new Process_MzML_MzXml_File();
		return instance;
	}
	
	/**
	 * @param scanFileFileContainer
	 * @throws Exception
	 */
	public void processMzMLFile( ScanFileFileContainer scanFileFileContainer ) throws Exception {
		processMzMLFileWithScanNumbersToLoad( scanFileFileContainer, null );
	}
	
	/**
	 * @param scanFileFileContainer
	 * @param scanNumbersToLoad
	 * @throws Exception
	 */
	public ScanFilenameScanNumberScanIdScanFileId_Mapping processMzMLFileWithScanNumbersToLoad( ScanFileFileContainer scanFileFileContainer , int[] scanNumbersToLoad ) throws Exception {
		
		ScanFilenameScanNumberScanIdScanFileId_Mapping scanFilenameScanNumberScanIdScanFileId_Mapping = new ScanFilenameScanNumberScanIdScanFileId_Mapping();
		
		File scanFileWithPath = scanFileFileContainer.getScanFile();
		Map<Integer,Integer> mapOfScanNumbersToScanIds = null;
		if ( ! scanFileWithPath.exists() ) {
			String msg = "Input scan file does not exist: " + scanFileWithPath.getAbsolutePath();
			log.error( msg );
			throw new Exception( msg );
		}
		String scanFileName = scanFileWithPath.getName();
		if ( scanFileFileContainer.getScanFileDBRecord() != null ) {
			ProxlXMLFileImportTrackingSingleFileDTO scanFileDBRecord = scanFileFileContainer.getScanFileDBRecord();
			scanFileName = scanFileDBRecord.getFilenameInUpload();
		}
		String scanFilePath = scanFileWithPath.getCanonicalFile().getParentFile().getCanonicalPath();
		String SHA1Sum = SHA1SumCalculator.getInstance().getSHA1Sum( scanFileWithPath );
		long scanFileToImportFileSize = scanFileWithPath.length();

		String canonicalFilename_W_Path_OnSubmitMachine = null;
		String absoluteFilename_W_Path_OnSubmitMachine = null;
		
		scanFilenameScanNumberScanIdScanFileId_Mapping.setScanFilename( scanFileName );

		
		/**
		 * When running the Import from the Run Importer Process
		 */
		ProxlXMLFileImportTrackingSingleFileDTO scanFileDBRecord = scanFileFileContainer.getScanFileDBRecord();
		
		if ( scanFileDBRecord != null ) {
			canonicalFilename_W_Path_OnSubmitMachine = scanFileDBRecord.getCanonicalFilename_W_Path_OnSubmitMachine();
			absoluteFilename_W_Path_OnSubmitMachine = scanFileDBRecord.getAbsoluteFilename_W_Path_OnSubmitMachine();
			ProxlXMLFileImportTrackingSingleFile_Importer_DAO.getInstance()
			.updateFileSizeSHA1Sum( scanFileToImportFileSize, SHA1Sum, scanFileFileContainer.getScanFileDBRecord().getId() );
		}
		
		boolean newScanFileRecord = false;
		ScanFileDTO scanFileDTO = null;
		MzMl_MzXml_FileReader scanFileReader = null;
		try {
			scanFileReader = getMzMLFileReader( scanFileWithPath /* , SHA1Sum */ );
			MzML_MzXmlHeader mzXmlHeader = scanFileReader.getRunHeader();
			
			//  create scanFileDTO to search for it in the db
			scanFileDTO = new ScanFileDTO();
			scanFileDTO.setFilename(scanFileName);
			scanFileDTO.setPath( scanFilePath );
			scanFileDTO.setSha1sum( SHA1Sum );
			scanFileDTO.setFileSize( scanFileToImportFileSize );
			
			ScanFileSourceDTO scanFileSourceDTO = new ScanFileSourceDTO();
			scanFileSourceDTO.setPath( scanFilePath );
			scanFileSourceDTO.setCanonicalFilename_W_Path_OnSubmitMachine( canonicalFilename_W_Path_OnSubmitMachine );
			scanFileSourceDTO.setAbsoluteFilename_W_Path_OnSubmitMachine( absoluteFilename_W_Path_OnSubmitMachine );

			List<ScanFileHeaderDTO> scanFileHeaderDTOList = getHeaders( mzXmlHeader );
			try {
				//  Inserts the scan file and headers records if the scan file does not already exist in the DB, 
				//    otherwise updates scanFileDTO.id with the id from the database.
				
				//  newScanFileRecord is true if the scan file record was inserted, false otherwise.
				newScanFileRecord =
						AddNewScanFileAndHeadersIfNeededDBTransactionService.getInstance()
						.addNewScanFileAndHeadersDBTransactionService( scanFileDTO, scanFileSourceDTO, scanFileHeaderDTOList );
			} catch ( Exception t ) {
				String msg = "failed to query or save mzML or MzXml file or file header. scanFileWithPath = " + scanFileWithPath.getAbsolutePath();
				log.error( msg, t );
				throw new ProxlImporterInteralException( msg, t );
			}
			if ( newScanFileRecord ) {
				//  New Scan file so create and save the scanFileDTO and 
				String msg = "Importing MzMl or MzXml file for the first time:  " + scanFileWithPath.getAbsolutePath();
				if ( scanFileFileContainer.getScanFileDBRecord() != null ) {
					scanFileName = scanFileDBRecord.getFilenameInUpload();
					msg += ", scanFileName in upload request: " + scanFileName;
				}
				System.out.println( msg );
			} else {
			}

			mapOfScanNumbersToScanIds =
					processAllScan( scanFileReader, scanNumbersToLoad, scanFileWithPath, scanFileName, scanFileDTO, newScanFileRecord );

			//  Send scan file to Spectral Storage Service for storage, will update proxl.scan_file record with returned API key
			//         Send after import to Proxl DB to ensure it is valid first and scans match Proxl Input XML file.
			//           (Could do the Scan File to Proxl Input XML file matching validation before inserting into the database, then could send to Spectral Storage Service first)
			ScanFileToSpectralStorageService_Processing.getInstance().sendScanFileToSpectralStorageServiceUpdateScanFileSpectralStorageAPIKey( scanFileWithPath, scanFileDTO );
			
		} catch ( RuntimeException e ) {
			throw e;
		} catch ( ProxlImporterConfigException e ) {
			throw e;
		} catch ( ProxlImporterInteralException e ) {
			throw e;
		} catch ( ProxlImporterSpectralStorageServiceRetryExceededException e ) {
			String msg = "Error: ProxlImporterSpectralStorageServiceRetryExceededException processing mzML or mzXml Scan file: " + scanFileWithPath.getAbsolutePath()
				+ ",  Throwing ProxlImporterInteralException.";
			log.error( msg, e );
			throw new ProxlImporterInteralException( msg, e );
		} catch ( ProxlImporterSpectralStorageServiceErrorException e ) {
			String msg = "Error: ProxlImporterSpectralStorageServiceErrorException processing mzML or mzXml Scan file: " + scanFileWithPath.getAbsolutePath()
				+ ",  Throwing ProxlImporterInteralException.";
			log.error( msg, e );
			throw new ProxlImporterInteralException( msg, e );
		} catch ( Exception e ) {
			String msg = "Error Exception processing mzML or mzXml Scan file: " 
					+ scanFileWithPath.getAbsolutePath()
					+ ", scanFileName (possibly different if uploaded) (reported to user submitting upload): " + scanFileName
					+ ",  Throwing Data error since probably error in file format.";
			log.error( msg, e );
			String msgForException = "Error processing Scan file: " + scanFileName
					+ ".  Please check the file to ensure it contains the correct contents for a scan file based on the suffix of the file ('mzML' or 'mzXML')";
			throw new ProxlImporterDataException( msgForException );
		} finally {
			if ( scanFileReader != null ) {
				scanFileReader.close();
			}
		}
		
		scanFilenameScanNumberScanIdScanFileId_Mapping.setScanFileId( scanFileDTO.getId() );
		scanFilenameScanNumberScanIdScanFileId_Mapping.setMapOfScanNumbersToScanIds( mapOfScanNumbersToScanIds );
		return scanFilenameScanNumberScanIdScanFileId_Mapping;
	}
	
	/**
	 * @param scanFileWithPath
	 * @return
	 * @throws Exception
	 */
	private MzMl_MzXml_FileReader getMzMLFileReader( File scanFileWithPath /* , String sha1Sum */ ) throws Exception {
		if ( ! scanFileWithPath.exists() ) {
			throw new Exception( "Input mzMl or MzXml file not found: '" + scanFileWithPath.getAbsolutePath() + "'");
		}
		MzMl_MzXml_FileReader scanFileReader = new MzMl_MzXml_FileReader();
		scanFileReader.open( scanFileWithPath.getAbsolutePath() /* , sha1Sum */ );
		return scanFileReader;
	}
	
	/**
	 * @param mzXmlHeader
	 * @return
	 */
	private List<ScanFileHeaderDTO> getHeaders( MzML_MzXmlHeader mzXmlHeader ) {
		//  How MzXmlHeader is populated in File Reader:
//        dataConvType  = getDataConversionType(dpInfo.getCentroided());
//        run.setDataConversionType(dataConvType);
//        
//        List<SoftwareInfo> swList = dpInfo.getSoftwareUsed();
//        // TODO handle multiple software info.
//        if(swList.size() > 0) {
//            for(SoftwareInfo si: swList) {
//                if(si.type.equalsIgnoreCase("conversion")) {
//                    run.setConversionSW(swList.get(0).name);
//                    run.setConversionSWVersion(swList.get(0).version);
//                }
//            }
//        }
//        
//        MSInstrumentInfo msiInfo = info.getInstrumentInfo();
//        run.setInstrumentModel(msiInfo.getModel());
//        run.setInstrumentVendor(msiInfo.getManufacturer());
//      
//        run.setFileName(this.filename);
//        run.setSha1Sum(sha1Sum);
		
		List<ScanFileHeaderDTO> scanFileHeaderDTOList = new ArrayList<>();
		
		//  TODO  Need to copy out header items
		ScanFileHeaderDTO item = null;
		item = new ScanFileHeaderDTO();
		scanFileHeaderDTOList.add( item );
		item.setHeader( "CreationDate" );
		item.setValue( mzXmlHeader.getCreationDate() );
		item = new ScanFileHeaderDTO();
		scanFileHeaderDTOList.add( item );
		item.setHeader( "AcquisitionMethod" );
		item.setValue( mzXmlHeader.getAcquisitionMethod() );
		item = new ScanFileHeaderDTO();
		scanFileHeaderDTOList.add( item );
		item.setHeader( "Comment" );
		item.setValue( mzXmlHeader.getComment() );
		item = new ScanFileHeaderDTO();
		scanFileHeaderDTOList.add( item );
		item.setHeader( "ConversionSW" );
		item.setValue( mzXmlHeader.getConversionSW() );
		item = new ScanFileHeaderDTO();
		scanFileHeaderDTOList.add( item );
		item.setHeader( "ConversionSWOptions" );
		item.setValue( mzXmlHeader.getConversionSWOptions() );
		item = new ScanFileHeaderDTO();
		scanFileHeaderDTOList.add( item );
		item.setHeader( "ConversionSWVersion" );
		item.setValue( mzXmlHeader.getConversionSWVersion() );
		item = new ScanFileHeaderDTO();
		scanFileHeaderDTOList.add( item );
		item.setHeader( "CreationDate" );
		item.setValue( mzXmlHeader.getCreationDate() );
		item = new ScanFileHeaderDTO();
		scanFileHeaderDTOList.add( item );
		item.setHeader( "InstrumentModel" );
		item.setValue( mzXmlHeader.getInstrumentModel() );
		item = new ScanFileHeaderDTO();
		scanFileHeaderDTOList.add( item );
		item.setHeader( "InstrumentSN" );
		item.setValue( mzXmlHeader.getInstrumentSN() );
		item = new ScanFileHeaderDTO();
		scanFileHeaderDTOList.add( item );
		item.setHeader( "InstrumentVendor" );
		item.setValue( mzXmlHeader.getInstrumentVendor() );
		return scanFileHeaderDTOList;
	}
	
	/**
	 * @param scanFileReader
	 * @param scanNumbersToLoad
	 * @param scanFileWithPath
	 * @param scanFileDTO
	 * @param newScanFileRecord
	 * @return mapOfScanNumbersToScanIds
	 * @throws Exception
	 */
	private Map<Integer,Integer> processAllScan(
			MzMl_MzXml_FileReader scanFileReader, int[] scanNumbersToLoad, File scanFileWithPath, String scanFileName_ForErrorMessages, 
			ScanFileDTO scanFileDTO, boolean newScanFileRecord ) throws Exception {
		
		Map<Integer,Integer> mapOfScanNumbersToScanIds = new HashMap<>();
		boolean[] insertedScanNumberAtIndex = null;
		if ( scanNumbersToLoad != null ) {
			Arrays.sort( scanNumbersToLoad ); // sort so can do binary search
			insertedScanNumberAtIndex = new boolean[ scanNumbersToLoad.length ];
		}
		int insertedScansCounter = 0;
		 NumberFormat numberFormatInsertedScansCounter = NumberFormat.getInstance();
		int insertedScansBlockCounter = 0; // track number since last reported on number inserted.
		int scansReadBlockCounter = 0; // track number since last reported on number read.
		int scansForSysoutLineCounter = 0;
		if ( log.isDebugEnabled() ) {
			System.out.println( "Scan numbers loaded:" );
		}
		long scanCounter = 0;
		long ms1_ScanCounter = 0;
		long ms2_ScanCounter = 0;
		
		try {
			MzML_MzXmlScan scanIn = null;
//			MzML_MzXmlScan prevMS1ScanIn = null;
			while ( ( scanIn = scanFileReader.getNextScan() ) != null ) {
				scanCounter++;
				scansReadBlockCounter++;
    			if ( scansReadBlockCounter > 20000 ) {
    				if ( log.isInfoEnabled() ) {
    					log.info( "Number of scans (ms1, ms2, ?) read so far: " 
    							+ numberFormatInsertedScansCounter.format( scanCounter ) );
    				}
    				scansReadBlockCounter = 0;
    			}

    			//  Sum up intensities
                if(scanIn.getMsLevel() == 1)  {
                	ms1_ScanCounter++;
                } else {
                	ms2_ScanCounter++;
                }
                if(scanIn.getMsLevel() == 1)  {
                }
                else {
                	//  Only save MS2
                	//  Determine if load this scan
                	boolean scanNumberFoundInListToLoad = false;
                	if ( scanNumbersToLoad != null ) {
                		int scanNumberInScan = scanIn.getStartScanNum();
                		int scanNumberIndex = Arrays.binarySearch( scanNumbersToLoad, scanNumberInScan );
                		if ( scanNumberIndex >= 0 && scanNumberIndex < scanNumbersToLoad.length ) {
                			scanNumberFoundInListToLoad = true; // scanNumber found in array so load
                			insertedScanNumberAtIndex[ scanNumberIndex ] = true;
                		}
                	} else {
                		scanNumberFoundInListToLoad = true; // no scan array, so loading everything
                	}
                	if ( scanNumberFoundInListToLoad ) {
                		boolean insertedRecord = 
                				InsertNewScanAndPrescanIfNeededDBTransactionService.getInstance()
                				.insertNewScanAndPrescanIfNeededDBTransactionService( scanIn, scanFileDTO, mapOfScanNumbersToScanIds );
                		if ( insertedRecord ) { 
                			////////  Do some reporting
                			insertedScansCounter++;
                			insertedScansBlockCounter++;
                			scansForSysoutLineCounter++;
                			if ( scansForSysoutLineCounter > 100 ) {
                				scansForSysoutLineCounter = 0;
                    			if ( log.isDebugEnabled() ) {
                    				System.out.println( "" );
                    			}
                    			if ( insertedScansBlockCounter > 5000 ) {
                    				if ( log.isInfoEnabled() ) {
                    					log.info( "Number of ms2 scans inserted so far: " 
                    							+ numberFormatInsertedScansCounter.format( insertedScansCounter ) );
                    				}
                    				insertedScansBlockCounter = 0;
                    			}
                			}
                			if ( log.isDebugEnabled() ) {
                				if ( scansForSysoutLineCounter != 0 ) {
                					System.out.print( ", " );
                				}
                				System.out.print( Integer.toString( scanIn.getStartScanNum() ) );
                			}                			
                		}
            		}
                }
			}
//		} catch (IOException e) {
//			String msg = "Error IOException processing mzML or MzXml Scan file: " + scanFileWithPath.getAbsolutePath();
//			log.error( msg, e );
//			throw new ProxlImporterInteralException( msg, e );
		} catch ( Exception e ) {
			String msg = "Error Exception processing mzML or mzXml Scan file: " 
					+ scanFileWithPath.getAbsolutePath()
					+ ", scanFileName (possibly different if uploaded) (reported to user submitting upload): " + scanFileName_ForErrorMessages
					+ scanFileName_ForErrorMessages
					+ ",  Throwing Data error since probably error in file format.";
			log.error( msg, e );
			String msgForException = "Error processing Scan file: " + scanFileName_ForErrorMessages
					+ ".  Please check the file to ensure it contains the correct contents for "
					+ "a scan file based on the suffix of the file ('mzML' or 'mzXML')";
			throw new ProxlImporterDataException( msgForException );
		}
		if ( scanNumbersToLoad != null && insertedScanNumberAtIndex != null ) {
			//  Confirm that all scan numbers to load were inserted:
			StringBuilder scanNumbersNotInsertedSB = null;
			for ( int scanNumberIndex = 0; scanNumberIndex < scanNumbersToLoad.length; scanNumberIndex++ ) {
				if ( ! insertedScanNumberAtIndex[ scanNumberIndex ] ) {
					if ( scanNumbersNotInsertedSB == null ) {
						scanNumbersNotInsertedSB = new StringBuilder( 1000 );
					} else {
						scanNumbersNotInsertedSB.append( ", " );
					}
					int scanNumberNotInserted = scanNumbersToLoad[ scanNumberIndex ];
					scanNumbersNotInsertedSB.append( Integer.toString( scanNumberNotInserted ) );
				}
			}
			if ( scanNumbersNotInsertedSB != null ) {
				String scanNumbersNotInserted = scanNumbersNotInsertedSB.toString();
				String msg = "Error Exception processing mzML or mzXml Scan file: " 
						+ scanFileWithPath.getAbsolutePath()
						+ ", scanFileName (possibly different if uploaded) (reported to user submitting upload): " + scanFileName_ForErrorMessages
						+ scanFileName_ForErrorMessages
						+ ", the following scan numbers were in the main import XML file but not in the scan file: "
						+ scanNumbersNotInserted;
				log.error( msg );
				String msgForException = "Error processing Scan file: " + scanFileName_ForErrorMessages
						+ ", the following scan numbers were in the main import XML file but not in the scan file: "
						+ scanNumbersNotInserted;
				throw new ProxlImporterDataException(msgForException);
			}
		}
		if ( log.isInfoEnabled() ) {
			log.info( "Done processing the MzML or MzXml scan file: " + scanFileWithPath.getAbsolutePath() );
			log.info( "Number of scans (ms1, ms2, ?) read: " 
					+ numberFormatInsertedScansCounter.format( scanCounter ) );
			log.info( "Number of ms1 scans read: "  
					+ numberFormatInsertedScansCounter.format( ms1_ScanCounter ) );
			log.info( "Number of ms2 scans read: "  
					+ numberFormatInsertedScansCounter.format( ms2_ScanCounter ) );
			log.info( "Number of ms2 scans inserted: "  
					+ numberFormatInsertedScansCounter.format( insertedScansCounter ) );
		}
		
		return mapOfScanNumbersToScanIds;
	}
}
