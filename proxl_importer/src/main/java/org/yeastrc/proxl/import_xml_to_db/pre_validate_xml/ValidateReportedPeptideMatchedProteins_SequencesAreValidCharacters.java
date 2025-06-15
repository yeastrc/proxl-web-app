package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;


import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl_import.api.xml_dto.MatchedProteins;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Protein;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptides;

/**
 * Validate the Peptide and Matched Protein Sequences are valid Characters.
 * 
 * Duplicate check in XSD to remove from XSD due to performance issues.
 *
 */
public class ValidateReportedPeptideMatchedProteins_SequencesAreValidCharacters {

	private static final Logger log = LoggerFactory.getLogger( ValidateReportedPeptideMatchedProteins_SequencesAreValidCharacters.class );
	
	private ValidateReportedPeptideMatchedProteins_SequencesAreValidCharacters() { }
	public static ValidateReportedPeptideMatchedProteins_SequencesAreValidCharacters getInstance() {
		return new ValidateReportedPeptideMatchedProteins_SequencesAreValidCharacters();
	}
	
	 Pattern regexPatter_AtoZ = Pattern.compile("[A-Z]+");
	 
	/**
	 * Validate the Peptide and Matched Protein Sequences are valid Characters.
	 * 
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	public void validateReportedPeptideMatchedProteins_SequencesAreValidCharacters( ProxlInput proxlInput ) throws ProxlImporterDataException {
		
		reportedPeptides_ValidateSequences( proxlInput );
				
		matchedProteins_ValidateSequences( proxlInput );
	}

	/**
	 * 
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	private void reportedPeptides_ValidateSequences( ProxlInput proxlInput ) throws ProxlImporterDataException {
		
		ReportedPeptides reportedPeptides = proxlInput.getReportedPeptides();
		if ( reportedPeptides != null ) {
			List<ReportedPeptide> reportedPeptideList =
					reportedPeptides.getReportedPeptide();
			if ( reportedPeptideList != null && ( ! reportedPeptideList.isEmpty() ) ) {
				for ( ReportedPeptide reportedPeptide : reportedPeptideList ) {
					
					if ( reportedPeptide.getPeptides() == null ) {
						String msg = "<reported_peptide> element contains no <peptides> element. <reported_peptide reported_peptide_string= "
								+  reportedPeptide.getReportedPeptideString();
						log.error( msg );
						throw new ProxlImporterDataException(msg);
					}
										
					for ( Peptide peptide : reportedPeptide.getPeptides().getPeptide() ) {

						if ( ! isSequenceValid( peptide.getSequence() ) ) {
							String msg = "The the 'sequence' attribute on <peptide> element contains invalid characters.  Only valid characters are A-Z.  sequence: "
									+  peptide.getSequence();
							log.error( msg );
							throw new ProxlImporterDataException(msg);

						}
					}
				}
			}
		}
	}


	//////////////////////////////////
	
	/**
	 * @param proxlInput
	 * @throws ProxlImporterDataException
	 */
	private void matchedProteins_ValidateSequences( ProxlInput proxlInput ) throws ProxlImporterDataException {


		//  Validate that all matchedProteinForPeptide_IDs_InAllReportedPeptides are found in matched proteins
		
		MatchedProteins matchedProteins = proxlInput.getMatchedProteins();
		if ( matchedProteins == null ) {
			String msg = "<matched_proteins> not in file";
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		List<Protein> matchedProteinList = matchedProteins.getProtein();
		if ( matchedProteinList == null ) {
			String msg = "<matched_proteins> not in file";
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		if ( matchedProteinList.isEmpty() ) {
			String msg = "<matched_proteins> is empty";
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		
		for ( Protein matchedProtein : matchedProteinList ) {
			
			if ( ! isSequenceValid( matchedProtein.getSequence() ) ) {
				String msg = "The the 'sequence' attribute on <protein> element contains invalid characters.  Only valid characters are A-Z.  sequence: "
						+  matchedProtein.getSequence();
				log.error( msg );
				throw new ProxlImporterDataException(msg);
				
			}
		}
	}

	/**
	 * @param sequence
	 * @return
	 */
	private boolean isSequenceValid( String sequence ) {
		
		if ( regexPatter_AtoZ.matcher( sequence ).matches() ) {
			return true;
		}
		return false;
		
	}
	
	 
	 //  Quick Test
	 
//	 public static void main(String[] args) throws Exception {
//
//
//		 if ( ValidateReportedPeptideMatchedProteins_SequencesAreValidCharacters.getInstance().isSequenceValid( "A") ) {
//			 System.out.println( "A is valid");
//		 } else {
//			 System.out.println( "A is NOT valid");
//		 }
//
//		 if ( ValidateReportedPeptideMatchedProteins_SequencesAreValidCharacters.getInstance().isSequenceValid( "3") ) {
//			 System.out.println( "3 is valid");
//		 } else {
//			 System.out.println( "3 is NOT valid");
//		 }
//
//		 if ( ValidateReportedPeptideMatchedProteins_SequencesAreValidCharacters.getInstance().isSequenceValid( "") ) {
//			 System.out.println( "Empty String is valid");
//		 } else {
//			 System.out.println( "Empty String is NOT valid");
//		 }
//	 }
	
	
}
