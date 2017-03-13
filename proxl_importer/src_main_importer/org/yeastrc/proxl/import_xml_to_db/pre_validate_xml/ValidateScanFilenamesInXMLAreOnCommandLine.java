package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.objects.ScanFileFileContainer;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingSingleFileDTO;

/**
 * 
 *
 */
public class ValidateScanFilenamesInXMLAreOnCommandLine {

	private static final Logger log = Logger.getLogger( ValidateScanFilenamesInXMLAreOnCommandLine.class );
	private ValidateScanFilenamesInXMLAreOnCommandLine() { }
	public static ValidateScanFilenamesInXMLAreOnCommandLine getInstance() {
		return new ValidateScanFilenamesInXMLAreOnCommandLine();
	}
	
	/**
	 * @param proxlInput
	 * @param scanFileList
	 * @throws ProxlImporterDataException for data errors
	 */
	public void validateScanFilenamesInXMLAreOnCommandLine( 
			ProxlInput proxlInput, List<ScanFileFileContainer> scanFileFileContainerList ) throws ProxlImporterDataException {
		
		if ( scanFileFileContainerList == null || scanFileFileContainerList.isEmpty() ) {
			//  Exit if no scan files provided, which means no scans were chosen
			return;  //  EARLY RETURN
		}
		Set<String> scanFilenamesSet = new HashSet<>();
		for ( ScanFileFileContainer scanFileFileContainer : scanFileFileContainerList ) {
			File scanFile = scanFileFileContainer.getScanFile();
			String scanFilename = scanFile.getName();
			if ( scanFileFileContainer.getScanFileDBRecord() != null ) {
				ProxlXMLFileImportTrackingSingleFileDTO scanFileDBRecord = scanFileFileContainer.getScanFileDBRecord();
				scanFilename = scanFileDBRecord.getFilenameInUpload();
			}
			scanFilenamesSet.add( scanFilename );
		}
		ReportedPeptides reportedPeptides = proxlInput.getReportedPeptides();
		if ( reportedPeptides != null ) {
			List<ReportedPeptide> reportedPeptideList =
					reportedPeptides.getReportedPeptide();
			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {
				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
					reportedPeptide.getPsms();
					Psms psms =	reportedPeptide.getPsms();
					if ( psms != null ) {
						List<Psm> psmList = psms.getPsm();
						if ( psmList != null ) {
							for ( Psm psm : psmList ) {
								String scanFileName = psm.getScanFileName();
								if ( StringUtils.isEmpty( scanFileName ) ) {
									if (  scanFileFileContainerList == null || scanFileFileContainerList.isEmpty() ) {
										//   Valid condition
									} else if ( scanFileFileContainerList != null && scanFileFileContainerList.size() != 1 ) {
										String msg = "ERROR: Scan Filename on PSM is empty when there is more than one scan file on the command line.";
										log.error(msg);
										throw new ProxlImporterDataException(msg);
									}
								} else {
									if ( ! scanFilenamesSet.contains( scanFileName ) ) {
										String msg = "Scan Filename on PSM is not in Scan File List on Command Line."
												+ "  Scan Filename on PSM: " + scanFileName;
										log.error(msg);
										throw new ProxlImporterDataException(msg);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}