package org.yeastrc.xlink.linker_data_processing_base;

import java.util.List;

/**
 * Package Private
 * 
 * All the Linkers for a Single Search
 *
 */
class Linkers_Main_ForSingleSearch implements ILinkers_Main_ForSingleSearch {

	/**
	 * Constructor - Page Private
	 * @param linker_MainList
	 */
	Linkers_Main_ForSingleSearch(List<ILinker_Main> linker_MainList) {
		super();
		if ( linker_MainList == null || linker_MainList.isEmpty() ) {
			throw new IllegalArgumentException( "Constructor: Invalid param: linker_MainList is null or empty" );
		}
		this.linker_MainList = linker_MainList;
	}

	/**
	 * Made unmodifiable in Linker_Main_LinkersForSingleSearch_Factory before setting
	 */
	private List<ILinker_Main> linker_MainList;
	
	private List<String> linkerAbbreviations;
	
	private String linkerAbbreviationsCommaDelim;
	
	private boolean allLinkersHave_LinkablePositions;

	///////////////
	
	//    Package Private Setters


	void setLinkerAbbreviations(List<String> linkerAbbreviations) {
		this.linkerAbbreviations = linkerAbbreviations;
	}
	void setLinkerAbbreviationsCommaDelim(String linkerAbbreviationsCommaDelim) {
		this.linkerAbbreviationsCommaDelim = linkerAbbreviationsCommaDelim;
	}
	void setAllLinkersHave_LinkablePositions(boolean allLinkersHave_LinkablePositions) {
		this.allLinkersHave_LinkablePositions = allLinkersHave_LinkablePositions;
	}
	
	/////////////////
	
	//  Public getters
	
	/**
	 * @return - Unmodifiable list of linkers
	 */
	@Override
	public List<ILinker_Main> getLinker_MainList() {
		return linker_MainList;
	}

	/**
	 * @return - Unmodifiable list of linker abbreviation strings
	 */
	@Override
	public List<String> getLinkerAbbreviations() {
		return linkerAbbreviations;
	}
	/**
	 * @return - linker abbreviations comma delimited
	 */
	@Override
	public String getLinkerAbbreviationsCommaDelim() {
		return linkerAbbreviationsCommaDelim;
	}

	
	/**
	 * @return true if all linkers have linkable positions
	 */
	@Override
	public boolean isAllLinkersHave_LinkablePositions() {
		return allLinkersHave_LinkablePositions;
	}

	
}
