package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * Temp copy some of KojakImporterUtils so web app will run
 *
 */
public class ZZ_TempCopyKojakImporterUtilsPartial {

	
	/////////////////////////////////////////////////////////////////////////////////
	
	

//	/**
//	 * Get the type of link represented in the string as defined by these rules:
//	 * 
//	 * 	unlinked: will look like a single peptide, e.g. LAADTGKGGQR
//	 *  monolinked: peptide-Mono, e.g. EVYSLEKCYR-Mono
//	 *  looplinked: peptide-Loop, e.g AKIVQKSSGLNMENLANHEHLLSPVR-Loop
//	 *  dimer: FADQEGLTSSVGEYNENTIQQLLLPK+FADQEGLTSSVGEYNENTIQQLLLPK
//	 *  crosslinked: peptide--peptide, e.g. AKLCQLDPVLYEK--NMNAILFDELSKER
//	 * 
//	 * @param sequence
//	 * @return
//	 * @throws Exception 
//	 */
//	public static int getLinkType( String sequence ) throws Exception {
//		
////		if( sequence.contains( "-Mono" ) )
////			return XLinkUtils.TYPE_MONOLINK;
////		
////		if( sequence.contains( "-MONO" ) )
////			return XLinkUtils.TYPE_MONOLINK;
//
//		
//		if( sequence.contains( "--" ) )
//			return XLinkUtils.TYPE_CROSSLINK;
//		
//		if( sequence.contains( "-Loop" ) )
//			return XLinkUtils.TYPE_LOOPLINK;
//		
//		if( sequence.contains( "-LOOP" ) )
//			return XLinkUtils.TYPE_LOOPLINK;
//		
//		if( sequence.contains( "+" ) )
//			return XLinkUtils.TYPE_DIMER;
//
//		
//		return XLinkUtils.TYPE_UNLINKED;
//	}
//	
//
//	
//	/**
//	 * This returns sequences that are stripped of other info like dynamic modifications/variable modifications
//	 * 
//	 * 
//	 * Get the actual peptide sequences present in the Kojak peptide sequence
//	 * according to these rules:
//	 * 
//	 * 	unlinked: will look like a single peptide, e.g. LAADTGKGGQR
//	 *  monolinked: peptide-Mono, e.g. EVYSLEKCYR-Mono
//	 *  looplinked: peptide-Loop, e.g AKIVQKSSGLNMENLANHEHLLSPVR-Loop
//	 *  dimer: FADQEGLTSSVGEYNENTIQQLLLPK+FADQEGLTSSVGEYNENTIQQLLLPK
//	 *  crosslinked: peptide--peptide, e.g. AKLCQLDPVLYEK(5)--NMNAILFDELSKER(9)
//	 * 
//	 * @param seq
//	 * @return
//	 */
//	public static Map<String, List<Integer>> getSequencesFromKojakSequence( String seq ) throws Exception {
//		
//		Map<String, List<Integer>> peptideMap = new HashMap<String, List<Integer>>();
//		Pattern p = Pattern.compile( "^(\\w+)\\((\\d+)\\)$" );
//		Pattern p2 = Pattern.compile( "^(\\w+)\\((\\d+),(\\d+)\\)$" );
//
//		
//		// strip out all mods from sequence
//		String nakedPeptideSeq = getNakedPeptide( seq );
//		
//		int linkType = getLinkType( seq );
//		
//		switch(  linkType) {
//		
//			case XLinkUtils.TYPE_UNLINKED:
//				peptideMap.put( nakedPeptideSeq, null );
//				break;
//				
//			case XLinkUtils.TYPE_DIMER:
//				for( String s : nakedPeptideSeq.split( "\\+" ) )
//					peptideMap.put( s, null );
//				break;
//				
//			case XLinkUtils.TYPE_CROSSLINK:
//				for( String s : nakedPeptideSeq.split( "--" ) ) {
//
//					Matcher m = p.matcher( s );
//					if( !m.matches() )
//						throw new Exception( "Could not get position of crosslinked peptide (" + s + ") from " + seq );
//					
//					if( !peptideMap.containsKey( m.group( 1 ) ) )
//						peptideMap.put( m.group( 1 ), new ArrayList<Integer>() );
//					
//					 
//					peptideMap.get( m.group( 1 )).add( Integer.parseInt( m.group( 2 ) ) );
//				}
//				break;
//				
//			case XLinkUtils.TYPE_LOOPLINK:
//				String lseq = nakedPeptideSeq;
//				
//				if( seq.endsWith( "-Loop" ) )
//					lseq = lseq.replace( "-Loop", "" );
//				
//				else if( seq.endsWith( "-LOOP" ) )
//					lseq = lseq.replace( "-LOOP", "" );
//				
//				Matcher m = p2.matcher( lseq );
//				if( !m.matches() )
//					throw new Exception( "Could not get position of looplink peptide (" + lseq + ") from " + seq );
//				
//				
//				String sequence = m.group( 1 );
//				
//				String position1 = m.group( 2 );
//				String position2 = m.group( 3 );
//				
//				List<Integer> positionList = null;
//				
//				if( ! peptideMap.containsKey( sequence ) ) {
//					
//					positionList = new ArrayList<Integer>();
//					peptideMap.put( sequence, positionList );
//				} else {
//					
//					positionList = peptideMap.get( sequence );
//				}
//				
//				positionList.add( Integer.parseInt( position1 ) );
//				positionList.add( Integer.parseInt( position2 ) );
//				
//				break;
//				
//			default:
//				return null;  //  EARLY EXIT from Method
//		}
//		
//		// ensure the list of positions is sorted
//		for( String s : peptideMap.keySet() ) {
//			if( peptideMap.get( s ) != null && peptideMap.get( s ).size() > 1 ) {
//				Collections.sort( peptideMap.get( s ) );
//			}
//		}
//		
//		return peptideMap;
//	}
//
//
//	
//	/**
//	 * Attempts to get the "naked" peptide string of a peptide string that
//	 * has been marked with dynamic modification masses (e.g. "PEPT[21.23]IDE"
//	 * would return "PEPTIDE")
//	 * @param pseq
//	 * @return
//	 */
//	public static String getNakedPeptide( String pseq ) {
//		return pseq.replaceAll( "\\[[^\\[]+\\]",  "" );
//	}
	

}
