package org.yeastrc.proxl.import_xml_to_db.utils;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 *
 */
public class ProteinPositionUtils {


	/**
	 * Given the protein, peptide and the position of a link in a peptide, return the position(s) of that link
	 * in the protein
	 * @param protein
	 * @param peptide
	 * @param peptideposition (1 == beginning of sequence)
	 * @return
	 * @throws Exception
	 */
	public static List<Integer> getProteinPosition( String proteinSequence, String peptideSequence, int peptidePosition ) throws Exception {

		List<Integer> positions = new ArrayList<Integer>();
		
        // iterate over all matches of the peptide sequence in the protein sequence
        for (int i = -1; (i = proteinSequence.indexOf( peptideSequence, i + 1)) != -1; )
        	positions.add( i + peptidePosition );
		
		return positions;
	}
	
	/**
	 * Given the parameters return a list of lists, each with two elements, which are all the corresponding matches to the
	 * protein sequence by the peptide sequence, and where the two linked spots on the peptide match to the protein sequence
	 * @param protein
	 * @param peptide
	 * @param peptidePosition1
	 * @param peptidePosition2
	 * @return
	 * @throws Exception
	 */
	public static List<List<Integer>> getLooplinkProteinPosition( String proteinSequence, String peptideSequence, int peptidePosition1, int peptidePosition2 ) throws Exception {
		
		List<List<Integer>> positions = new ArrayList<List<Integer>>();

		
        // iterate over all matches of the peptide sequence in the protein sequence
        for (int i = -1; (i = proteinSequence.indexOf( peptideSequence, i + 1) ) != -1; ) {

        	List<Integer> l = new ArrayList<Integer>(2);
        	l.add( i + peptidePosition1 );
        	l.add( i + peptidePosition2 );
		
        	positions.add( l );
        }
        	
		return positions;
	}
	
	
}
