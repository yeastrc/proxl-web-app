package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl_import.api.xml_dto.Modification;
import org.yeastrc.proxl_import.api.xml_dto.Modifications;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;

/**
 * 
 *
 */
public class GetPeptideMonolinkPositions {

	private static final Logger log = LoggerFactory.getLogger( GetPeptideMonolinkPositions.class);
	//  private constructor
	private GetPeptideMonolinkPositions() {  }
	private static final GetPeptideMonolinkPositions _INSTANCE = new GetPeptideMonolinkPositions(); 
	public static GetPeptideMonolinkPositions getInstance() { return _INSTANCE; }
	
	/**
	 * @param peptide
	 * @return
	 * @throws ProxlImporterInteralException 
	 */
	public Set<Integer> getPeptideMonolinkPositions( Peptide peptide ) throws ProxlImporterInteralException {
		Set<Integer> peptideMonolinkPositions = new HashSet<>();

		final String peptideSequenceString = peptide.getSequence();
		
		Modifications modifications = peptide.getModifications();
		
		final int DTO_POSITION_INITIAL_VALUE = -1;
		
		if ( modifications != null ) {
			List<Modification> modificationList = modifications.getModification();
			if ( modificationList != null ) {
				for ( Modification modification : modificationList ) {

					Boolean monolink = modification.isIsMonolink();

					if ( monolink != null && monolink ) {

						BigInteger position = modification.getPosition();
						Boolean is_N_Terminal = modification.isIsNTerminal();
						Boolean is_C_Terminal = modification.isIsCTerminal();
						
						int resultPosition = DTO_POSITION_INITIAL_VALUE;
						
						if ( position != null ) {
							resultPosition = position.intValue();
						}
						
						//   For Database, set position to first or last position of peptide if N or C terminus is set
						if ( is_N_Terminal != null && is_N_Terminal.booleanValue() ) {
							resultPosition = 1;
						}
						if ( is_C_Terminal != null && is_C_Terminal.booleanValue() ) {
							resultPosition = peptideSequenceString.length();
						}
						
						if ( resultPosition == DTO_POSITION_INITIAL_VALUE ) {
							String msg = "ERROR: getPerPeptideData(...). Failed to set modification position. resultPosition == DTO_POSITION_INITIAL_VALUE. peptideSequenceString: " + peptideSequenceString;
							log.error( msg );
							throw new ProxlImporterInteralException( msg );
						}

						peptideMonolinkPositions.add( resultPosition );
					}
				}
			}
		}
		return peptideMonolinkPositions;
	}
}
