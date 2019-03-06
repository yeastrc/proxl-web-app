package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl_import.api.xml_dto.Psm.PerPeptideAnnotations;
import org.yeastrc.proxl_import.api.xml_dto.Psm.PerPeptideAnnotations.PsmPeptide;
import org.yeastrc.proxl_import.api.xml_dto.Psms;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;

/**
 * 
 * For every psm_peptide, the value of "unique_id" must be on a "peptide" under the same "reported_peptide"
 *
 */
public class ValidatePsmPeptideRecordsUniqueIdOnPeptideRecord {

	private static final Logger log = Logger.getLogger( ValidatePsmPeptideRecordsUniqueIdOnPeptideRecord.class );
	private ValidatePsmPeptideRecordsUniqueIdOnPeptideRecord() { }
	public static ValidatePsmPeptideRecordsUniqueIdOnPeptideRecord getInstance() {
		return new ValidatePsmPeptideRecordsUniqueIdOnPeptideRecord();
	}

	/**
	 * For every psm_peptide, the value of "unique_id" must be on a "peptide" under the same "reported_peptide"
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	public void validatePsmPeptideRecordsUniqueIdOnPeptideRecord( ProxlInput proxlInput ) throws ProxlImporterDataException {
		
		ReportedPeptides reportedPeptides = proxlInput.getReportedPeptides();
		if ( reportedPeptides != null ) {
			List<ReportedPeptide> reportedPeptideList =
					reportedPeptides.getReportedPeptide();
			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {
				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
					validateSingleReportedPeptide( reportedPeptide );
				}
			}
		}
		
	}
	
	/**
	 * @param reportedPeptide
	 * @throws ProxlImporterDataException
	 */
	private void validateSingleReportedPeptide( ReportedPeptide reportedPeptide ) throws ProxlImporterDataException {
		
		Set<String> peptideUniqueIds = new HashSet<>();
		
		Peptides peptides = reportedPeptide.getPeptides();
		if ( peptides == null ) {
			String msg = "No Peptides under reported peptide id: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
		List<Peptide> peptideList = peptides.getPeptide();
		if ( peptideList == null || peptideList.isEmpty() ) {
			String msg = "No Peptides under reported peptide id: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}
		
		// Collect the unique_id on the peptides
		for ( Peptide peptide : peptideList ) {
			String uniqueId = peptide.getUniqueId();
			if ( StringUtils.isNotEmpty( uniqueId ) ) {
				if ( ! peptideUniqueIds.add( uniqueId ) ) {
					String msg = "Peptide unique_id value is duplicate under same reported peptide id. unique_id: " + uniqueId
							+ ", reported peptide id: " + reportedPeptide.getReportedPeptideString();
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				}
			}
		}
		
		//  Validate the unique_id on the psm_peptide
		Psms psms =	reportedPeptide.getPsms();
		if ( psms != null ) {
			List<Psm> psmList = psms.getPsm();
			if ( psmList != null ) {
				for ( Psm psm : psmList ) {
					PerPeptideAnnotations perPeptideAnnotations = psm.getPerPeptideAnnotations();
					if ( perPeptideAnnotations != null ) {
						List<PsmPeptide> psmPeptideList = perPeptideAnnotations.getPsmPeptide();
						for ( PsmPeptide psmPeptide : psmPeptideList ) {
							String psmPeptideUniqueId =	psmPeptide.getUniqueId();
							if ( StringUtils.isEmpty( psmPeptideUniqueId ) ) {
								String msg = "psm_peptide unique_id value is empty under reported peptide id: " + reportedPeptide.getReportedPeptideString();
								log.error( msg );
								throw new ProxlImporterDataException( msg );
							}
							if ( ! peptideUniqueIds.contains( psmPeptideUniqueId ) ) {
								String msg = "psm_peptide unique_id value is not on any peptide under same reported peptide id. unique_id: " + psmPeptideUniqueId
										+ ", reported peptide id: " + reportedPeptide.getReportedPeptideString();
								log.error( msg );
								throw new ProxlImporterDataException( msg );
							}
						}
					}
				}
			}
		}
	}
	
}
