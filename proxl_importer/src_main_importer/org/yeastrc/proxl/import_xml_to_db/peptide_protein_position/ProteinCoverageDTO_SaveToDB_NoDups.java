package org.yeastrc.proxl.import_xml_to_db.peptide_protein_position;

import java.util.HashSet;
import java.util.Set;

import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_ProteinCoverageDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinCoverageDTO;

/**
 * Save ProteinCoverageDTO to db, but not more than once
 *
 */
public class ProteinCoverageDTO_SaveToDB_NoDups {


	private static Set<ProteinCoverageDTO> savedRecords = new HashSet<>( 1000000 );

	//  private constructor
	private ProteinCoverageDTO_SaveToDB_NoDups() { }
	
	public static ProteinCoverageDTO_SaveToDB_NoDups getInstance() { return new ProteinCoverageDTO_SaveToDB_NoDups(); }
	
	/**
	 * Save ProteinCoverageDTO to db, but not more than once based on ProteinCoverageDTO equals()
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void proteinCoverageDTO_SaveToDB_NoDups( ProteinCoverageDTO item ) throws Exception {
		
		if ( savedRecords.add(item) ) {
			
			DB_Insert_ProteinCoverageDAO.getInstance().save( item );
			
		}
	}
}
