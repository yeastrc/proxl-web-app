package org.yeastrc.xlink.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;

public class XLinkUtils {
	
//	private static final Logger log = Logger.getLogger(XLinkUtils.class);

	public static final int TYPE_UNLINKED  = 0;
	public static final int TYPE_MONOLINK  = 1;
	public static final int TYPE_LOOPLINK  = 2;
	public static final int TYPE_DIMER     = 3;
	public static final int TYPE_CROSSLINK = 4;
	
	public static final String[] typeStrings = {
		"unlinked",
		"monolink",
		"looplink",
		"dimer",
		"crosslink"
	};
	
	private static final List<String> typesStringsAsList = Arrays.asList( typeStrings );
	
	public static int getTypeNumber( String type ) {
		return typesStringsAsList.indexOf( type );
	}
	
	
	
	public static final String CROSS_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_CROSSLINK ) ;
	public static final String LOOP_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_LOOPLINK ) ;
	public static final String UNLINKED_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_UNLINKED ) ;
	public static final String DIMER_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_DIMER ) ;
	
	
	public static final String CROSS_TYPE_STRING_UPPERCASE  = CROSS_TYPE_STRING.toUpperCase();
	public static final String LOOP_TYPE_STRING_UPPERCASE  = LOOP_TYPE_STRING.toUpperCase();
	public static final String DIMER_TYPE_STRING_UPPERCASE  = DIMER_TYPE_STRING.toUpperCase();
	public static final String UNLINKED_TYPE_STRING_UPPERCASE  = UNLINKED_TYPE_STRING.toUpperCase();

	

	/**
	 * Get the string representation of a type of linked peptide
	 * @param type The type, as defined in XLinkUtils
	 * @return
	 */
	public static String getTypeString( int type ) {

		if( type < 0 || type >= typeStrings.length )
			return "unknown";
		
		return typeStrings[ type ];		
	}
	
	
	
	
	
	/**
	 * Given the protein, peptide and the position of a link in a peptide, return the position(s) of that link
	 * in the protein
	 * @param protein
	 * @param peptide
	 * @param peptideposition (1 == beginning of sequence)
	 * @return
	 * @throws Exception
	 */
	public static List<Integer> getProteinPosition( NRProteinDTO protein, PeptideDTO peptide, int peptidePosition ) throws Exception {
		List<Integer> positions = new ArrayList<Integer>();
		String proteinSequence = YRC_NRSEQUtils.getSequence( protein.getNrseqId() );
		
        // iterate over all matches of the peptide sequence in the protein sequence
        for (int i = -1; (i = proteinSequence.indexOf(peptide.getSequence(), i + 1)) != -1; )
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
	public static List<List<Integer>> getLooplinkProteinPosition( NRProteinDTO protein, PeptideDTO peptide, int peptidePosition1, int peptidePosition2 ) throws Exception {
		List<List<Integer>> positions = new ArrayList<List<Integer>>();
		String proteinSequence = YRC_NRSEQUtils.getSequence( protein.getNrseqId() );
		
        // iterate over all matches of the peptide sequence in the protein sequence
        for (int i = -1; (i = proteinSequence.indexOf(peptide.getSequence(), i + 1)) != -1; ) {

        	List<Integer> l = new ArrayList<Integer>(2);
        	l.add( i + peptidePosition1 );
        	l.add( i + peptidePosition2 );
		
        	positions.add( l );
        }
        	
		return positions;
	}
	
}
