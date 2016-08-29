package org.yeastrc.proxl.import_xml_to_db.peptide_protein_position;

import java.util.HashSet;
import java.util.Set;

import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_PeptideProteinPositionDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideProteinPositionDTO;

/**
 * Save PeptideProteinPositionDTO to db, but not more than once
 *
 */
public class PeptideProteinPositionDTO_SaveToDB_NoDups {


	private static Set<PeptideProteinPositionDTO> savedRecords = new HashSet<>( 1000000 );

	//  private constructor
	private PeptideProteinPositionDTO_SaveToDB_NoDups() { }
	
	public static PeptideProteinPositionDTO_SaveToDB_NoDups getInstance() { return new PeptideProteinPositionDTO_SaveToDB_NoDups(); }
	
	/**
	 * Save PeptideProteinPositionDTO to db, but not more than once based on PeptideProteinPositionDTO equals()
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void peptideProteinPositionDTO_SaveToDB_NoDups( PeptideProteinPositionDTO item ) throws Exception {
		
		if ( savedRecords.add(item) ) {
			
			DB_Insert_PeptideProteinPositionDAO.getInstance().save(item);
			
		}
	}
}
