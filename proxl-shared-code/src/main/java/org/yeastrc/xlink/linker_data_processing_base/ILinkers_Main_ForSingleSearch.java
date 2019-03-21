package org.yeastrc.xlink.linker_data_processing_base;

import java.util.List;

/**
 * All the linkers for a single search
 *
 */
public interface ILinkers_Main_ForSingleSearch {

	/**
	 * @return - Unmodifiable list of linkers
	 */
	List<ILinker_Main> getLinker_MainList();
	
	/**
	 * @return - Unmodifiable list of linker abbreviation strings
	 */
	List<String> getLinkerAbbreviations();
	
	/**
	 * @return - linker abbreviations comma delimited
	 */
	String getLinkerAbbreviationsCommaDelim();
	
	/**
	 * @return true if all linkers have linkable positions
	 */
	boolean isAllLinkersHave_LinkablePositions();
}
