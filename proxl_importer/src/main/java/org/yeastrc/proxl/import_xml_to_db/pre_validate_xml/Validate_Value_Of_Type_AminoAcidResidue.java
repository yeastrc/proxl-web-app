package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;

/**
 * 
 * Validate values of type <xs:simpleType name="amino_acid_residue">
 *
 */
public class Validate_Value_Of_Type_AminoAcidResidue {

	private static final Logger log = LoggerFactory.getLogger( Validate_Value_Of_Type_AminoAcidResidue.class );

	/**
	 * Validate values of type <xs:simpleType name="amino_acid_residue">
	 * 
	 * @param aminoAcidResidue
	 * @param elementName - Element Name
	 * @throws ProxlImporterDataException
	 */
	public static void validate_Value_Of_Type_AminoAcidResidue( String aminoAcidResidue, String elementName, String errorMessageAddition ) throws ProxlImporterDataException {
		

		//		Move to Java validation
		

		//		<xs:length value="1"/>
		//		<xs:pattern value="([A-Z]+)"/>
		
		if ( errorMessageAddition == null ) {
			errorMessageAddition = "";
		}
		
		if ( aminoAcidResidue == null ) {
			String msg = "value for element (of type 'amino_acid_residue') " + elementName + " is null. " + errorMessageAddition;
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}

		
		if ( aminoAcidResidue.length() != 1 ) {
			String msg = "value for element (of type 'amino_acid_residue') " + elementName + " must have length of 1. length: " + aminoAcidResidue.length() + ", value: '" + aminoAcidResidue + "'." + errorMessageAddition;
			log.error( msg );
			throw new ProxlImporterDataException( msg );
		}

		 Pattern regexPatter_AtoZ = Pattern.compile("[A-Z]+");
		 
		 if ( ! regexPatter_AtoZ.matcher( aminoAcidResidue ).matches() ) {
				String msg = "value for element (of type 'amino_acid_residue') " + elementName + " must have a letter of [A-Z]. value: '" + aminoAcidResidue + "'." + errorMessageAddition;
				log.error( msg );
				throw new ProxlImporterDataException( msg );
		 }
		 
	}
	
}
