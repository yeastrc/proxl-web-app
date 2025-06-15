package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;

/**
 * Validate the Reported Peptide Strings are All Unique
 * 
 * Duplicate check in XSD to remove from XSD due to performance issues.
 *
 */
public class Validate_ReportedPeptideStrings_AllUnique {

	private static final Logger log = LoggerFactory.getLogger( Validate_ReportedPeptideStrings_AllUnique.class );
	
	private Validate_ReportedPeptideStrings_AllUnique() { }
	public static Validate_ReportedPeptideStrings_AllUnique getInstance() {
		return new Validate_ReportedPeptideStrings_AllUnique();
	}
	
	/**
	 * Validate the Reported Peptide Strings are All Unique
	 * 
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	public void validate_ReportedPeptideStrings_AllUnique( ProxlInput proxlInput ) throws ProxlImporterDataException {
		
		ReportedPeptides reportedPeptides = proxlInput.getReportedPeptides();
		if ( reportedPeptides != null ) {
			List<ReportedPeptide> reportedPeptideList =
					reportedPeptides.getReportedPeptide();
			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {
				
				Set<String> reportedPeptideStringSet = new HashSet<>();
				
				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
					

					if ( ! reportedPeptideStringSet.add( reportedPeptide.getReportedPeptideString() ) ) {
						String msg = "The 'reported_peptide_string' attribute on <reported_peptide> element has duplicate values which are not allowed.  reported_peptide_string: "
								+  reportedPeptide.getReportedPeptideString();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
						
					}
				}
			}
		}
	}


}
