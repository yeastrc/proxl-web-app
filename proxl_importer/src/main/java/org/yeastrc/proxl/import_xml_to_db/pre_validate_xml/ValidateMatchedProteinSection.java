package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.utils.ProteinAnnotationNameTruncationUtil;
import org.yeastrc.proxl_import.api.xml_dto.MatchedProteins;
import org.yeastrc.proxl_import.api.xml_dto.Protein;
import org.yeastrc.proxl_import.api.xml_dto.ProteinAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.Protein.ProteinIsotopeLabels;
import org.yeastrc.proxl_import.api.xml_dto.Protein.ProteinIsotopeLabels.ProteinIsotopeLabel;

/**
 * 
 *
 */
public class ValidateMatchedProteinSection {
	
	private static final Logger log = LoggerFactory.getLogger(  ValidateMatchedProteinSection.class );
	private ValidateMatchedProteinSection() { }
	public static ValidateMatchedProteinSection getInstance() {
		return new ValidateMatchedProteinSection();
	}
	
	
	/**
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	public void validateMatchedProteinSection( ProxlInput proxlInput ) throws ProxlImporterDataException {
		
		//  Validate that protein sequence only exists once with a given isotope label or without any isotope label.
		
		//  Validate that all protein annotation names are unique
		
		Set<ProteinData_ProtienSequenceWithIsotopeLabel> proteinSequenceisotopeLabelName_Set = new HashSet<>();
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
			
			String proteinIsotopeLabelString = null;

			ProteinIsotopeLabels proteinIsotopeLabels = protein.getProteinIsotopeLabels();
			if ( proteinIsotopeLabels != null ) {
				ProteinIsotopeLabel proteinIsotopeLabel = proteinIsotopeLabels.getProteinIsotopeLabel();
				if ( proteinIsotopeLabel != null ) {
					proteinIsotopeLabelString = proteinIsotopeLabel.getLabel();
				}
			}
			
			ProteinData_ProtienSequenceWithIsotopeLabel proteinData_ProtienSequenceWithIsotopeLabel = new ProteinData_ProtienSequenceWithIsotopeLabel();
			proteinData_ProtienSequenceWithIsotopeLabel.proteinSequence = protein.getSequence();
			proteinData_ProtienSequenceWithIsotopeLabel.isotopeLabelName = proteinIsotopeLabelString;
			
			if ( ! proteinSequenceisotopeLabelName_Set.add( proteinData_ProtienSequenceWithIsotopeLabel ) ) {
				String isotopeLabelNameForErrorMsg = ", no isotope label name.";
				if ( proteinIsotopeLabelString != null ) {
					isotopeLabelNameForErrorMsg = ", isotope label name: " + proteinIsotopeLabelString;
				}
				String msg = "duplicate protein sequence / isotope label name under <matched_proteins> section, sequence: "
						+ protein.getSequence()
						+ isotopeLabelNameForErrorMsg;
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
	

	/**
	 * Used for validating a protein is in the <matched_proteins> section only once 
	 * for a given protein sequence and isotope label name (or isotope label name is null if no isotope label name)
	 *
	 */
	private static class ProteinData_ProtienSequenceWithIsotopeLabel {
		
		private String proteinSequence;
		private String isotopeLabelName;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((isotopeLabelName == null) ? 0 : isotopeLabelName.hashCode());
			result = prime * result + ((proteinSequence == null) ? 0 : proteinSequence.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ProteinData_ProtienSequenceWithIsotopeLabel other = (ProteinData_ProtienSequenceWithIsotopeLabel) obj;
			if (isotopeLabelName == null) {
				if (other.isotopeLabelName != null)
					return false;
			} else if (!isotopeLabelName.equals(other.isotopeLabelName))
				return false;
			if (proteinSequence == null) {
				if (other.proteinSequence != null)
					return false;
			} else if (!proteinSequence.equals(other.proteinSequence))
				return false;
			return true;
		}
	}
}
