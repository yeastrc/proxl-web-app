package org.yeastrc.xlink.www.web_utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.xlink.www.objects.IProteinCrosslink;
import org.yeastrc.xlink.www.objects.IProteinLooplink;
import org.yeastrc.xlink.www.objects.WebMergedProteinPosition;


public class XLinkWebAppUtils {

	
	public static String getPeptideProteinPositionsString( List<WebMergedProteinPosition> peptideProteinPositions  ) {
		
		if ( peptideProteinPositions != null && ( ! peptideProteinPositions.isEmpty() ) ) {
		
			return StringUtils.join( peptideProteinPositions, ", " );
		}
		
		return "";
	}
	
	

	/**
	 * Get all unique distinct constrains represented by the given lists of crosslinks and looplinks
	 * @param crosslinks
	 * @param looplinks
	 * @return a Map in the form of protein 1 ID => protein 1 position => protein 2 ID => set of protein 2 positions, which
	 *         gives, for a given pair of proteins and the position in the first protein, all positions in the 2nd protein
	 *         that are linked to that position.
	 */
	public static Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>> getUDRs( List<? extends IProteinCrosslink> crosslinks, List<? extends IProteinLooplink> looplinks ) {
				
		Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>> udrMap = new HashMap<Integer, Map<Integer, Map<Integer, Set<Integer>>>>();
		
		for( IProteinCrosslink crosslink: crosslinks ) {
			
			int nrseqId1 = crosslink.getProtein1().getNrProtein().getNrseqId();
			int nrseqId2 = crosslink.getProtein2().getNrProtein().getNrseqId();
			
			int pos1 = crosslink.getProtein1Position();
			int pos2 = crosslink.getProtein2Position();
			
			// ensure nrseqId1 is always <= nrseqId2
			if( nrseqId1 > nrseqId2 ) {
				int tmp = nrseqId1;
				nrseqId1 = nrseqId2;
				nrseqId2 = tmp;
				
				// if we swap proteins, we better swap corresponding positions in those proteins
				tmp = pos1;
				pos1 = pos2;
				pos2 = tmp;
			}
			
			// ensure pos1 always <= pos2 for the same protein
			if( nrseqId1 == nrseqId2 && pos1 > pos2 ) {
				int tmp = pos1;
				pos1 = pos2;
				pos2 = tmp;
			}

			// ensure entry is in for this protein
			if( !udrMap.containsKey( nrseqId1 ) )
				udrMap.put( nrseqId1, new HashMap<Integer, Map<Integer, Set<Integer>>>() );
			
			
			// ensure entry is in for this protein's position
			if( !udrMap.get( nrseqId1 ).containsKey( pos1 ) )
				udrMap.get( nrseqId1 ).put( pos1, new HashMap<Integer, Set<Integer>>() );
			
			// ensure entry is in for the 2nd protein link to this position in the first protein
			if( !udrMap.get( nrseqId1 ).get( pos1 ).containsKey( nrseqId2 ) )
				udrMap.get( nrseqId1 ).get( pos1 ).put( nrseqId2, new HashSet<Integer>() );
			
			udrMap.get( nrseqId1 ).get( pos1 ).get( nrseqId2 ).add( pos2 );
					
		}

		for( IProteinLooplink looplink: looplinks ) {
			
			int nrseqId1 = looplink.getProtein().getNrProtein().getNrseqId();
			int nrseqId2 = nrseqId1;
			
			int pos1 = looplink.getProteinPosition1();
			int pos2 = looplink.getProteinPosition2();
			
			// ensure pos1 always <= pos2
			if( pos1 > pos2 ) {
				int tmp = pos1;
				pos1 = pos2;
				pos2 = tmp;
			}

			// ensure entry is in for this protein
			if( !udrMap.containsKey( nrseqId1 ) )
				udrMap.put( nrseqId1, new HashMap<Integer, Map<Integer, Set<Integer>>>() );
			
			
			// ensure entry is in for this protein's position
			if( !udrMap.get( nrseqId1 ).containsKey( pos1 ) )
				udrMap.get( nrseqId1 ).put( pos1, new HashMap<Integer, Set<Integer>>() );
			
			// ensure entry is in for the 2nd protein link to this position in the first protein
			if( !udrMap.get( nrseqId1 ).get( pos1 ).containsKey( nrseqId2 ) )
				udrMap.get( nrseqId1 ).get( pos1 ).put( nrseqId2, new HashSet<Integer>() );
			
			udrMap.get( nrseqId1 ).get( pos1 ).get( nrseqId2 ).add( pos2 );
					
		}
		
		return udrMap;
	}
	
	/**
	 * Get the number of unique distance restraint represented by the given lists of crosslinks and looplinks
	 * @param crosslinks
	 * @param looplinks
	 * @return
	 */
	public static int getNumUDRs( List<? extends IProteinCrosslink> crosslinks, List<? extends IProteinLooplink> looplinks ) {
		Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>> udrMap = getUDRs( crosslinks, looplinks );
		int numDistinctLinks = 0;

		for( int nrseqId1 : udrMap.keySet() ) {
			for( int pos1 : udrMap.get( nrseqId1 ).keySet() ) {
				for( int nrseqId2 : udrMap.get( nrseqId1 ).get( pos1 ).keySet() ) {
					for( int pos2 : udrMap.get( nrseqId1 ).get( pos1 ).get( nrseqId2 ) ) {
						numDistinctLinks++;
					}
				}
			}
		}
		
		return numDistinctLinks;
	}
	
	
	
}
