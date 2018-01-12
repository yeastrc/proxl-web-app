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

/**
 * 
 *
 */
public class XLinkWebAppUtils {
	
	/**
	 * @param peptideProteinPositions
	 * @return
	 */
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
			int proteinSequenceVersionId1 = crosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
			int proteinSequenceVersionId2 = crosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
			int pos1 = crosslink.getProtein1Position();
			int pos2 = crosslink.getProtein2Position();
			// ensure proteinSequenceVersionId1 is always <= proteinSequenceVersionId2
			if( proteinSequenceVersionId1 > proteinSequenceVersionId2 ) {
				int tmp = proteinSequenceVersionId1;
				proteinSequenceVersionId1 = proteinSequenceVersionId2;
				proteinSequenceVersionId2 = tmp;
				// if we swap proteins, we better swap corresponding positions in those proteins
				tmp = pos1;
				pos1 = pos2;
				pos2 = tmp;
			}
			// ensure pos1 always <= pos2 for the same protein
			if( proteinSequenceVersionId1 == proteinSequenceVersionId2 && pos1 > pos2 ) {
				int tmp = pos1;
				pos1 = pos2;
				pos2 = tmp;
			}
			// ensure entry is in for this protein
			if( !udrMap.containsKey( proteinSequenceVersionId1 ) )
				udrMap.put( proteinSequenceVersionId1, new HashMap<Integer, Map<Integer, Set<Integer>>>() );
			// ensure entry is in for this protein's position
			if( !udrMap.get( proteinSequenceVersionId1 ).containsKey( pos1 ) )
				udrMap.get( proteinSequenceVersionId1 ).put( pos1, new HashMap<Integer, Set<Integer>>() );
			// ensure entry is in for the 2nd protein link to this position in the first protein
			if( !udrMap.get( proteinSequenceVersionId1 ).get( pos1 ).containsKey( proteinSequenceVersionId2 ) )
				udrMap.get( proteinSequenceVersionId1 ).get( pos1 ).put( proteinSequenceVersionId2, new HashSet<Integer>() );
			udrMap.get( proteinSequenceVersionId1 ).get( pos1 ).get( proteinSequenceVersionId2 ).add( pos2 );
		}
		for( IProteinLooplink looplink: looplinks ) {
			int proteinSequenceVersionId1 = looplink.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
			int proteinSequenceVersionId2 = proteinSequenceVersionId1;
			int pos1 = looplink.getProteinPosition1();
			int pos2 = looplink.getProteinPosition2();
			// ensure pos1 always <= pos2
			if( pos1 > pos2 ) {
				int tmp = pos1;
				pos1 = pos2;
				pos2 = tmp;
			}
			// ensure entry is in for this protein
			if( !udrMap.containsKey( proteinSequenceVersionId1 ) )
				udrMap.put( proteinSequenceVersionId1, new HashMap<Integer, Map<Integer, Set<Integer>>>() );
			// ensure entry is in for this protein's position
			if( !udrMap.get( proteinSequenceVersionId1 ).containsKey( pos1 ) )
				udrMap.get( proteinSequenceVersionId1 ).put( pos1, new HashMap<Integer, Set<Integer>>() );
			// ensure entry is in for the 2nd protein link to this position in the first protein
			if( !udrMap.get( proteinSequenceVersionId1 ).get( pos1 ).containsKey( proteinSequenceVersionId2 ) )
				udrMap.get( proteinSequenceVersionId1 ).get( pos1 ).put( proteinSequenceVersionId2, new HashSet<Integer>() );
			udrMap.get( proteinSequenceVersionId1 ).get( pos1 ).get( proteinSequenceVersionId2 ).add( pos2 );
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
		for( int proteinSequenceVersionId1 : udrMap.keySet() ) {
			for( int pos1 : udrMap.get( proteinSequenceVersionId1 ).keySet() ) {
				for( int proteinSequenceVersionId2 : udrMap.get( proteinSequenceVersionId1 ).get( pos1 ).keySet() ) {
					for( int pos2 : udrMap.get( proteinSequenceVersionId1 ).get( pos1 ).get( proteinSequenceVersionId2 ) ) {
						numDistinctLinks++;
					}
				}
			}
		}
		return numDistinctLinks;
	}
}
