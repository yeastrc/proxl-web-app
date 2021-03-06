package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psm.PerPeptideAnnotations.PsmPeptide;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;

/**
 * Process through the PSMs generating a Map of Sets of Scan Filenames and Scan Numbers.
 *
 */
public class GetScanFilenamesAndScanNumbersToInsert {

	private static final Logger log = LoggerFactory.getLogger(  GetScanFilenamesAndScanNumbersToInsert.class );
	
	public static final String ARBITRARY_FILENAME_FOR_NULL_FILENAME = "";
	
	/**
	 * private constructor
	 */
	private GetScanFilenamesAndScanNumbersToInsert(){}
	public static GetScanFilenamesAndScanNumbersToInsert getInstance() {
		return new GetScanFilenamesAndScanNumbersToInsert();
	}
	
	/**
	 * Process through the PSMs generating a Map of Sets of Scan Filenames and Scan Numbers.
	 * 
	 * @return 
	 * @throws ProxlImporterDataException 
	 * @throws ProxlImporterInteralException 
	 */
	public Map<String, Set<Integer>> getScanFilenamesAndScanNumbersToInsert( ProxlInput proxlInput ) throws ProxlImporterDataException, ProxlImporterInteralException  {
		
		Map<String, Set<Integer>> mapOfScanFilenamesSetsOfScanNumbers = new HashMap<String, Set<Integer>>();
		Set<Integer> setOfScanNumbersArbitraryScanfile = new HashSet<>();
		ReportedPeptides reportedPeptides =
				proxlInput.getReportedPeptides();
		List<ReportedPeptide> reportedPeptideList =
				reportedPeptides.getReportedPeptide();
		if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {
			for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
				Psms psms =	reportedPeptide.getPsms();
				List<Psm> psmList = psms.getPsm();
				for ( Psm psm : psmList ) {
					{	// Process <psm> level data
						BigInteger scanNumberBI = psm.getScanNumber();
						String scanFileName = psm.getScanFileName();
						
						addScanTo_mapOfScanFilenamesSetsOfScanNumbers( mapOfScanFilenamesSetsOfScanNumbers, setOfScanNumbersArbitraryScanfile, scanFileName, scanNumberBI );
					}

						// Process <psm_per_peptide> level data under <psm>
					if ( psm.getPerPeptideAnnotations() != null ) {
						List<PsmPeptide> psmPeptideList = psm.getPerPeptideAnnotations().getPsmPeptide();
						if ( psmPeptideList != null && ( ! psmPeptideList.isEmpty() ) ) {
							// Get scan numbers from <psm_peptide> elements
							for ( PsmPeptide psmPeptide : psmPeptideList ) {
								BigInteger scanNumberBI = psmPeptide.getScanNumber();
								String scanFileName = psmPeptide.getScanFileName();
								
								addScanTo_mapOfScanFilenamesSetsOfScanNumbers( mapOfScanFilenamesSetsOfScanNumbers, setOfScanNumbersArbitraryScanfile, scanFileName, scanNumberBI );
							}
						}
					}
				}
			}
		}
		if ( ( ! mapOfScanFilenamesSetsOfScanNumbers.isEmpty() ) && ( ! setOfScanNumbersArbitraryScanfile.isEmpty() ) ) {
			String msg = "Cannot have mix of PSMs with scan filenames and PSMs where scan filename is null";
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
		if ( ! setOfScanNumbersArbitraryScanfile.isEmpty() ) {
			mapOfScanFilenamesSetsOfScanNumbers.put( ARBITRARY_FILENAME_FOR_NULL_FILENAME, setOfScanNumbersArbitraryScanfile );
		}
		return mapOfScanFilenamesSetsOfScanNumbers;
	}
	
	
	/**
	 * @param mapOfScanFilenamesSetsOfScanNumbers
	 * @param setOfScanNumbersArbitraryScanfile
	 * @param scanFileName
	 * @param scanNumberBI
	 */
	private void addScanTo_mapOfScanFilenamesSetsOfScanNumbers(
			Map<String, Set<Integer>> mapOfScanFilenamesSetsOfScanNumbers,
			Set<Integer> setOfScanNumbersArbitraryScanfile, 
			String scanFileName, 
			BigInteger scanNumberBI ) {

		if ( scanNumberBI == null ) {
			// No Scan Number
			return; // Early Exit
		}
		
		Integer scanNumber = scanNumberBI.intValue();
		
		Set<Integer> setOfScanNumbers = null;
		if ( scanFileName == null ) {
			setOfScanNumbers = setOfScanNumbersArbitraryScanfile;
		} else { 
			setOfScanNumbers = mapOfScanFilenamesSetsOfScanNumbers.get( scanFileName );
		}
		if ( setOfScanNumbers == null ) {
			setOfScanNumbers = new HashSet<>();
			mapOfScanFilenamesSetsOfScanNumbers.put( scanFileName, setOfScanNumbers );
		}
		setOfScanNumbers.add( scanNumber );

	}
}
