package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs.DropPeptideAndOrPSMForCutoffs;
import org.yeastrc.proxl.import_xml_to_db.drop_peptides_psms_for_cutoffs.DropPeptidePSMCutoffValues;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;

/**
 * Process through the PSMs generating a Map of Sets of Scan Filenames and Scan Numbers.
 *
 */
public class GetScanFilenamesAndScanNumbersToInsert {

	private static final Logger log = Logger.getLogger( GetScanFilenamesAndScanNumbersToInsert.class );
	
	
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
	public Map<String, Set<Integer>> getScanFilenamesAndScanNumbersToInsert( ProxlInput proxlInput, DropPeptidePSMCutoffValues dropPeptidePSMCutoffValues ) throws ProxlImporterDataException, ProxlImporterInteralException  {
		
		Map<String, Set<Integer>> mapOfScanFilenamesSetsOfScanNumbers = new HashMap<String, Set<Integer>>();
		
		
		Set<Integer> setOfScanNumbersArbitraryScanfile = null;
		
		

		ReportedPeptides reportedPeptides =
				proxlInput.getReportedPeptides();
		
		List<ReportedPeptide> reportedPeptideList =
				reportedPeptides.getReportedPeptide();
		
		if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {
		

			for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
				
				if ( DropPeptideAndOrPSMForCutoffs.getInstance()
						.dropPeptideForCmdLineCutoffs( reportedPeptide, dropPeptidePSMCutoffValues ) ) {
					
					continue;  // EARLY continue to next record
				}

				Psms psms =	reportedPeptide.getPsms();
				
				List<Psm> psmList = psms.getPsm();
				
				for ( Psm psm : psmList ) {
					
					if ( DropPeptideAndOrPSMForCutoffs.getInstance()
							.dropPSMForCmdLineCutoffs( psm, dropPeptidePSMCutoffValues ) ) {
						
						continue;  // EARLY continue to next record
					}
				
					
					String scanFileName = psm.getScanFileName();
					
					Set<Integer> setOfScanNumbers = null;
					
					if ( scanFileName == null ) {
						
						if ( setOfScanNumbersArbitraryScanfile == null ) {
							
							setOfScanNumbersArbitraryScanfile = new HashSet<>();
						}
					
						setOfScanNumbers = setOfScanNumbersArbitraryScanfile;
						
					} else { 
						
						setOfScanNumbers = mapOfScanFilenamesSetsOfScanNumbers.get( scanFileName );
					}
					
					if ( setOfScanNumbers == null ) {
						
						setOfScanNumbers = new HashSet<>();
						
						mapOfScanFilenamesSetsOfScanNumbers.put( scanFileName, setOfScanNumbers );
					}
					
					int scanNumber = psm.getScanNumber().intValue();
					
					setOfScanNumbers.add( scanNumber );
				}
				
			}
		}
		

		if ( ( ! mapOfScanFilenamesSetsOfScanNumbers.isEmpty() ) && setOfScanNumbersArbitraryScanfile != null ) {
			
			String msg = "Cannot have mix of PSMs with scan filenames and PSMs where scan filename is null";
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
		
		if ( setOfScanNumbersArbitraryScanfile != null ) {

			mapOfScanFilenamesSetsOfScanNumbers.put( ARBITRARY_FILENAME_FOR_NULL_FILENAME, setOfScanNumbersArbitraryScanfile );
		}
		
		return mapOfScanFilenamesSetsOfScanNumbers;
	}
}
