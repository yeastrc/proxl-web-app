package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.apache.log4j.Logger;
import org.yeastrc.proxl_import.api.xml_dto.Modification;
import org.yeastrc.proxl_import.api.xml_dto.Modifications;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;

/**
 * 
 *
 */
public class GetPeptideMonolinkPositions {

//	private static final Logger log = Logger.getLogger(GetPeptideMonolinkPositions.class);
	
	//  private constructor
	private GetPeptideMonolinkPositions() {  }
	
	private static final GetPeptideMonolinkPositions _INSTANCE = new GetPeptideMonolinkPositions(); 
	
	public static GetPeptideMonolinkPositions getInstance() { return _INSTANCE; }
	
	/**
	 * @param peptide
	 * @return
	 */
	public Set<Integer> getPeptideMonolinkPositions( Peptide peptide ) {
		
		Set<Integer> peptideMonolinkPositions = new HashSet<>();
		
		Modifications modifications = peptide.getModifications();
		
		if ( modifications != null ) {
			
			List<Modification> modificationList = modifications.getModification();
			
			if ( modificationList != null ) {
				
				for ( Modification modification : modificationList ) {
					
					if ( modification.isIsMonolink() ) {
						
						peptideMonolinkPositions.add( modification.getPosition().intValue() );
					}
				}
			}
		}
		
		return peptideMonolinkPositions;
	}
}
