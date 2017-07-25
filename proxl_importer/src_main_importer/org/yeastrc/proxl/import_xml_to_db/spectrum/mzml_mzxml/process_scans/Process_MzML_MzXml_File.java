package org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.process_scans;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingSingleFileDTO;
import org.yeastrc.xlink.dao.ScanRetentionTimeDAO;
import org.yeastrc.xlink.dto.ScanFileDTO;
import org.yeastrc.xlink.dto.ScanFileHeaderDTO;
import org.yeastrc.xlink.dto.ScanFileSourceDTO;
import org.yeastrc.xlink.dto.ScanRetentionTimeDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.ScanFileFileContainer;
import org.yeastrc.proxl.import_xml_to_db.file_import_proxl_xml_scans.dao.ProxlXMLFileImportTrackingSingleFile_Importer_DAO;
import org.yeastrc.proxl.import_xml_to_db.spectrum.db_update_with_transaction_services.AddNewScanFileAndHeadersIfNeededDBTransactionService;
import org.yeastrc.proxl.import_xml_to_db.spectrum.db_update_with_transaction_services.InsertNewScanAndPrescanIfNeededDBTransactionService;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.dto.MzML_MzXmlHeader;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.dto.MzML_MzXmlScan;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.reader.MzMl_MzXml_FileReader;
import org.yeastrc.proxl.import_xml_to_db.utils.RoundDecimalFieldsIfNecessary;
import org.yeastrc.proxl.import_xml_to_db.utils.SHA1SumCalculator;

/**
 * 
 *
 */
public class Process_MzML_MzXml_File {
	
	private static final Logger log = Logger.getLogger(Process_MzML_MzXml_File.class);
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
	public Map<Integer,Integer> processMzMLFileWithScanNumbersToLoad( ScanFileFileContainer scanFileFileContainer , int[] scanNumbersToLoad ) throws Exception {
		
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
					processAllScan( scanFileReader, scanNumbersToLoad, scanFileWithPath, scanFileDTO, newScanFileRecord );
		} catch ( ProxlImporterInteralException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Error Exception processing mzML or mzXml Scan file: " + scanFileWithPath.getAbsolutePath()
					+ ",  Throwing Data error since probably error in file format.";
			log.error( msg, e );
			String msgForException = "Error processing Scan file: " + scanFileWithPath.getAbsolutePath()
					+ ".  Please check the file to ensure it contains the correct contents for "
					+ "a scan file based on the suffix of the file ('mzML' or 'mzXML')";
			throw new ProxlImporterDataException( msgForException );
		} finally {
			if ( scanFileReader != null ) {
				scanFileReader.close();
			}
		}
		return mapOfScanNumbersToScanIds;
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
	private Map<Integer,Integer> processAllScan( MzMl_MzXml_FileReader scanFileReader, int[] scanNumbersToLoad, File scanFileWithPath, ScanFileDTO scanFileDTO, boolean newScanFileRecord ) throws Exception {
		
		Map<Integer,Integer> mapOfScanNumbersToScanIds = new HashMap<>();
		boolean[] insertedScanNumberAtIndex = null;
		if ( scanNumbersToLoad != null ) {
			Arrays.sort( scanNumbersToLoad ); // sort so can do binary search
			insertedScanNumberAtIndex = new boolean[ scanNumbersToLoad.length ];
		}
		int scanFileId = scanFileDTO.getId();
		boolean saveSpectrumData = true;
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
		
		//  Object for accumulating Scan info/statistics for saving in DB
		AccumScanFileStatistics accumScanFileStatistics = AccumScanFileStatistics.getInstance();
		
		try {
			MzML_MzXmlScan scanIn = null;
			MzML_MzXmlScan prevMS1ScanIn = null;
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
    			BigDecimal retentionTime = 
    					RoundDecimalFieldsIfNecessary.roundDecimalFieldsIfNecessary( scanIn.getRetentionTime() );
    			//  Save every scan to table scan_retention_time
    			ScanRetentionTimeDTO scanRetentionTimeDTO = new ScanRetentionTimeDTO();
    			scanRetentionTimeDTO.setScanFileId( scanFileId );
    			scanRetentionTimeDTO.setScanNumber( scanIn.getStartScanNum() );
    			scanRetentionTimeDTO.setScanLevel( scanIn.getMsLevel() );
    			scanRetentionTimeDTO.setPrecursorScanNumber( scanIn.getPrecursorScanNum() );
    			scanRetentionTimeDTO.setRetentionTime( retentionTime );
    			ScanRetentionTimeDAO.save( scanRetentionTimeDTO );
    			//  Sum up intensities
                if(scanIn.getMsLevel() == 1)  {
                	ms1_ScanCounter++;
                } else {
                	ms2_ScanCounter++;
                }
                accumScanFileStatistics.processScanForAccum( scanIn );
                if(scanIn.getMsLevel() == 1)  {
                }
                else {
                	//  Only save MS2 (also save associated MS1 scan which is the previous MS1 scan)
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
                				.insertNewScanAndPrescanIfNeededDBTransactionService( scanIn, prevMS1ScanIn, scanFileDTO, saveSpectrumData, mapOfScanNumbersToScanIds );
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
                    					log.info( "Number of ms2 scans (also ms1 scans being inserted) inserted so far: " 
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
//                		} else {
//                			int z = 0;
                		}
//            		} else {
//            			int z = 0;
            		}
                }
                if(scanIn.getMsLevel() == 1)  {
                	prevMS1ScanIn = scanIn;
                }
			}
//		} catch (IOException e) {
//			String msg = "Error IOException processing mzML or MzXml Scan file: " + scanFileWithPath.getAbsolutePath();
//			log.error( msg, e );
//			throw new ProxlImporterInteralException( msg, e );
		} catch ( Exception e ) {
			String msg = "Error Exception processing mzML or mzXml Scan file: " + scanFileWithPath.getAbsolutePath()
					+ ",  Throwing Data error since probably error in file format.";
			log.error( msg, e );
			String msgForException = "Error processing Scan file: " + scanFileWithPath.getAbsolutePath()
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
				String msg = "ERROR: For Scan File: " + scanFileReader.getFileName()
						+ ", the following scan numbers were in the main import XML file but not in the scan file: "
						+ scanNumbersNotInserted;
				log.error( msg );
				throw new ProxlImporterDataException(msg);
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
			log.info( "for all ms1 scans: Intensities Summed: "  
					+ numberFormatInsertedScansCounter.format( accumScanFileStatistics.getMs1_ScanIntensitiesSummed() ) );
			log.info( "for all ms2 scans: Intensities Summed: "  
					+ numberFormatInsertedScansCounter.format( accumScanFileStatistics.getMs2_ScanIntensitiesSummed() ) );
			log.info( "Number of ms2 scans inserted (also ms1 scans being inserted) : "  
					+ numberFormatInsertedScansCounter.format( insertedScansCounter ) );
		}
		
		SaveScanFileStatisticsToDB.getInstance().saveScanFileStatisticsToDB( accumScanFileStatistics, scanFileId, scanFileDTO.getFilename() );
		
		return mapOfScanNumbersToScanIds;
	}
}
