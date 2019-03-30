package org.yeastrc.proxl.import_xml_to_db.utils;

import java.util.regex.Pattern;

//import org.slf4j.LoggerFactory;import org.slf4j.Logger;

/**
 * Get the Petide or Protein sequence used for string matches between peptide and protein sequences
 * 
 * This is done since I and L have same mass and thus for Peptide identification from Mass Spec they are interchangable
 * 
 * Replacing I and L with J is sort of a standard and J is currently not a residue letter
 *
 */
public class PeptideProteinSequenceForProteinInference {

//	private static final Logger log = LoggerFactory.getLogger( PeptideProteinSequenceForProteinInference.class);
	
	private static final String REPLACE_I_L_SEARCH_REGEX = "[IL]";
	private static final String REPLACE_I_L_REPLACEMENT_STRING_J = "J";
	
	private static final PeptideProteinSequenceForProteinInference instance = new PeptideProteinSequenceForProteinInference();
	
	Pattern replaceIL_SearchPatter;
	
	//  private constructor
	private PeptideProteinSequenceForProteinInference() { 
		replaceIL_SearchPatter = Pattern.compile( REPLACE_I_L_SEARCH_REGEX );
	}
	public static PeptideProteinSequenceForProteinInference getSingletonInstance() { return instance; }

	/**
	 * @param initialPeptideOrProteinSequence
	 * @return
	 */
	public String convert_PeptideOrProtein_SequenceFor_I_L_Equivalence_ChangeTo_J ( String initialPeptideOrProteinSequence ) {
		
		String result =	replaceIL_SearchPatter.matcher(initialPeptideOrProteinSequence).replaceAll( REPLACE_I_L_REPLACEMENT_STRING_J );
		return result;
	}
}
