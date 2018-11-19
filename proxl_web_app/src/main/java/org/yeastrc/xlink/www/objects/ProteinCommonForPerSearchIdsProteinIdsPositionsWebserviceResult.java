package org.yeastrc.xlink.www.objects;

import java.util.Map;
import java.util.TreeMap;

public class ProteinCommonForPerSearchIdsProteinIdsPositionsWebserviceResult {

	/**
	 * Key is project search id
	 */
	private Map<Integer, ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry> proteinsPerProjectSearchIdMap = new TreeMap<>();
	
	/**
	 * Add entry to map.  Returns previous entry if already in map
	 * 
	 * @param projectSearchId
	 * @param entry
	 */
	public ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry addEntryToProteinsPerProjectSearchIdMap( int projectSearchId, ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry entry ) {
		return proteinsPerProjectSearchIdMap.put( projectSearchId, entry );
	}
	
	/**
	 * Key is project search id
	 */
	public Map<Integer, ProteinCommonDataForPerSearchIdsProteinIdsPositionsResultEntry> getProteinsPerProjectSearchIdMap() {
		return proteinsPerProjectSearchIdMap;
	}

}
