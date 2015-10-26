package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.dto.SearchDTO;

/**
 * Used for the searchDetailsBlock.jsp
 *
 */
public class SearchDTODetailsDisplayWrapper {

	private SearchDTO searchDTO;

	
	public String getLinkersDisplayString() throws Exception {
		
		if ( searchDTO == null ) {
			
			throw new IllegalStateException( "searchDTO == null");
		}
		
		List<LinkerDTO> linkers = searchDTO.getLinkers();
		
		if ( linkers == null || linkers.isEmpty() ) {
			
			return "";
		}
		
		List<String> linkerAbbreviations = new ArrayList<>( linkers.size() );
		
		for ( LinkerDTO linker : linkers ) {
			
			linkerAbbreviations.add( linker.getAbbr() );
		}
		
		Collections.sort( linkerAbbreviations );
		
		String linkersString = linkerAbbreviations.get(0);
		
		if ( linkerAbbreviations.size() > 1 ) {

			//  start loop at second index
			for ( int index = 1; index < linkerAbbreviations.size(); index++ ) {

				linkersString += ", ";
				linkersString += linkerAbbreviations.get( index );
			}
		}
		
		return linkersString;
	}
	
	
	//  Getters & Setters
	
	public SearchDTO getSearchDTO() {
		return searchDTO;
	}

	public void setSearchDTO(SearchDTO searchDTO) {
		this.searchDTO = searchDTO;
	}
	
	
}
