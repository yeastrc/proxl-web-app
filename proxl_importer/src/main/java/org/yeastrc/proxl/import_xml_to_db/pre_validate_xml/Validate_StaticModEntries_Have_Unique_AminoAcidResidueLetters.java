package org.yeastrc.proxl.import_xml_to_db.pre_validate_xml;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.StaticModification;



/**
 * Validate that the <static_modification> has unique values for attribute amino_acid
 * 
 */
public class Validate_StaticModEntries_Have_Unique_AminoAcidResidueLetters {
	
	private static final Logger log = LoggerFactory.getLogger( Validate_StaticModEntries_Have_Unique_AminoAcidResidueLetters.class );
	
	private Validate_StaticModEntries_Have_Unique_AminoAcidResidueLetters() { }
	public static Validate_StaticModEntries_Have_Unique_AminoAcidResidueLetters getInstance() {
		return new Validate_StaticModEntries_Have_Unique_AminoAcidResidueLetters();
	}
	
	
	/**
	 * @param proxlInput
	 * @throws ProxlImporterDataException for data errors
	 */
	public void validate_StaticModEntries_Have_Unique_AminoAcidResidueLetters( 
			
			ProxlInput proxlInput
			
			) throws ProxlImporterDataException {
		
		Set<String> aminoAcidLetters_Set = new HashSet<>();
		
		if ( proxlInput.getStaticModifications() != null && proxlInput.getStaticModifications().getStaticModification() != null ) {
			
			for ( StaticModification staticModification : proxlInput.getStaticModifications().getStaticModification() ) {
				
				if ( ! aminoAcidLetters_Set.add( staticModification.getAminoAcid() ) ) {
					String msg = "<static_modification> has duplicate attribute amino_acid value '"
							+  staticModification.getAminoAcid()
							+ "'.";
					log.error( msg );
					throw new ProxlImporterDataException( msg );
				}
			}
		}
	}
			
}
