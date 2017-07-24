package org.yeastrc.proxl.import_xml_to_db.program_create_ms1_sum_json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.dto.MzML_MzXmlHeader;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.dto.MzML_MzXmlScan;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.process_scans.AccumScanFileStatistics;
import org.yeastrc.proxl.import_xml_to_db.spectrum.mzml_mzxml.reader.MzMl_MzXml_FileReader;
import org.yeastrc.xlink.ms1_binned_summed_intensities.main.MS1_BinnedSummedIntensitiesProcessing;
import org.yeastrc.xlink.ms1_binned_summed_intensities.main.MS1_BinnedSummedIntensitiesProcessing.MS1_BinnedSummedIntensitiesProcessingResult;

/**
 * Entry point to read a scan file and create the ms1 summed intensity JSON file
 *
 * This is not part of main import processing.
 * 
 * This is a side option for Generating the JSON for the ms1 summed intensity that is normally stored in the DB
 */
public class ImporterCreate_ms1_sum_json {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		if ( args.length < 2 || "-h".equals( args[0] ) ) {
			printHelp();
			System.exit( 1 );
		}

		String scanFileWithPath = args[0];
		String outputFile =  args[1];
		
		System.out.println( "Input Scan file: " + scanFileWithPath );
		System.out.println( "Ouput ms1 JSON file: " + outputFile );
		
		File scanFileWithPathFile = new File( scanFileWithPath );
		if ( ! scanFileWithPathFile.exists() ) {
			System.err.println( "Scan File does not exist: " + scanFileWithPath );
			System.exit( 1 );
		}

		//  Object for accumulating Scan info/statistics for saving in DB
		AccumScanFileStatistics accumScanFileStatistics = AccumScanFileStatistics.getInstance();
	
		MzMl_MzXml_FileReader scanFileReader = null;
		try {
			scanFileReader = new MzMl_MzXml_FileReader();
			scanFileReader.open( scanFileWithPath /* , sha1Sum */ );
			MzML_MzXmlHeader mzXmlHeader = scanFileReader.getRunHeader();
			MzML_MzXmlScan scanIn = null;
			while ( ( scanIn = scanFileReader.getNextScan() ) != null ) {
                accumScanFileStatistics.processScanForAccum( scanIn );
			}

		} finally {
			scanFileReader.close();
		}
		
		NumberFormat numberFormatInsertedScansCounter = NumberFormat.getInstance();

		System.out.println( "Number of ms1 scans read: "  
				+ numberFormatInsertedScansCounter.format( accumScanFileStatistics.getMs1_ScanCounter()  ) );
		System.out.println( "Number of ms2 scans read: "  
				+ numberFormatInsertedScansCounter.format( accumScanFileStatistics.getMs2_ScanCounter() ) );

		System.out.println( "for all ms1 scans: Intensities Summed: "  
				+ numberFormatInsertedScansCounter.format( accumScanFileStatistics.getMs1_ScanIntensitiesSummed() ) );
		System.out.println( "for all ms2 scans: Intensities Summed: "  
				+ numberFormatInsertedScansCounter.format( accumScanFileStatistics.getMs2_ScanIntensitiesSummed() ) );

		Map<Long, Map<Long, MutableDouble>> ms1_IntensitiesMutableDoubleBinnedSummedMap =
				accumScanFileStatistics.getMs1_IntensitiesSummedMap();
			
		MS1_BinnedSummedIntensitiesProcessingResult ms1_BinnedSummedIntensitiesProcessingResult =
		MS1_BinnedSummedIntensitiesProcessing.getInstance().getBytesAndSummaryObjectFrom_Ms1_IntensitiesBinnedSummedMap( ms1_IntensitiesMutableDoubleBinnedSummedMap );
		
		byte[] intensitiesMapToJSONRootAsBytes =  ms1_BinnedSummedIntensitiesProcessingResult.getFullJSON();
		
		File outputFileFile = new File( outputFile );

		OutputStream os = new FileOutputStream( outputFileFile );
		os.write( intensitiesMapToJSONRootAsBytes );
		os.close();
	}

	/**
	 * 
	 */
	private static void printHelp() {
		
		String msg = "Only parameters allowed are <scan file with path> <output filename> ";
		System.out.println( msg );
		System.err.println( msg );
	}
}
