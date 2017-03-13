package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.utils.ProteinAnnotationNameTruncationUtil;
import org.yeastrc.proxl_import.api.xml_dto.MatchedProteins;
import org.yeastrc.proxl_import.api.xml_dto.Protein;
import org.yeastrc.proxl_import.api.xml_dto.ProteinAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;

/**
 * 
 *
 */
public class ValidateMatchedProteinSection {
	
	private static final Logger log = Logger.getLogger( ValidateMatchedProteinSection.class );
	private ValidateMatchedProteinSection() { }
	public static ValidateMatchedProteinSection getInstance() {
		return new ValidateMatchedProteinSection();
	}
	
	/**
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	public void validateMatchedProteinSection( ProxlInput proxlInput ) throws ProxlImporterDataException {
		//  Validate that protein sequence only exists once.
		//  Validate that all protein annotation names are unique
		Set<String> proteinSequences = new HashSet<>();
		Set<String> proteinAnnotationNames = new HashSet<>();
		MatchedProteins matchedProteins = proxlInput.getMatchedProteins();
		if ( matchedProteins == null ) {
			return;  //  TODO  maybe throw exception
		}
		List<Protein> proteinList = matchedProteins.getProtein();
		if ( proteinList == null ) {
			return;  //  TODO  maybe throw exception
		}
		if ( proteinList.isEmpty() ) {
			return;  //  TODO  maybe throw exception
		}
		for ( Protein protein : proteinList ) {
			if ( ! proteinSequences.add( protein.getSequence() ) ) {
				String msg = "duplicate protein sequences under <matched_proteins> section, sequence: "
						+ protein.getSequence();
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}
			List<ProteinAnnotation> proteinAnnotationList = protein.getProteinAnnotation();
			if ( proteinAnnotationList == null ) {
				String msg = "Must have at least one <protein_annotation> under <protein> section, sequence: "
						+ protein.getSequence();
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}
			if ( proteinAnnotationList.isEmpty() ) {
				String msg = "Must have at least one <protein_annotation> under <protein> section, sequence: "
						+ protein.getSequence();
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}
			for ( ProteinAnnotation proteinAnnotation : proteinAnnotationList ) {
				String proteinAnnotationNameTruncated = ProteinAnnotationNameTruncationUtil.truncateProteinAnnotationName( proteinAnnotation.getName() );
				if ( ! proteinAnnotationNames.add( proteinAnnotationNameTruncated ) ) {
					String msg = "duplicate protein annotation name under <matched_proteins> section,"
							+ " annotation name: " + proteinAnnotation.getName()
							+ " which was truncated as needed to: " + proteinAnnotationNameTruncated
							+ ", sequence: " + protein.getSequence();
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}
			}
		}
	}
}
