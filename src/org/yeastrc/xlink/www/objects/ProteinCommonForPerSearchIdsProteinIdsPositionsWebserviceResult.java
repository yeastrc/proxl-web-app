package org.yeastrc.xlink.www.objects;

import java.util.Map;
import java.util.TreeMap;

public class ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult {


	/**
	 * Key is search id
	 */
	private Map<Integer, ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry> proteinsPerSearchIdMap = new TreeMap<>();

	
	/**
	 * Add entry to map.  Returns previous entry if already in map
	 * 
	 * @param searchId
	 * @param entry
	 */
	public ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry addEntryToProteinsPerSearchIdMap( int searchId, ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry entry ) {
		
		return proteinsPerSearchIdMap.put( searchId, entry );
	}
	
	/**
	 * Key is search id
	 */
	public Map<Integer, ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry> getProteinsPerSearchIdMap() {
		return proteinsPerSearchIdMap;
	}

	

}
