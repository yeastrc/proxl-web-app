package org.yeastrc.xlink.www.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplaySearchLevel;
import org.yeastrc.xlink.www.searcher.SearchProgramDisplaySearcher;

/**
 * Used for the searchDetailsBlock.jsp and viewProject.jsp
 *
 */
public class SearchDTODetailsDisplayWrapper {

	private SearchDTO searchDTO;

	private List<SearchProgramDisplay> searchProgramDisplayList; 
	
	/**
	 * Not used on viewProject.jsp
	 */
	private CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel;
	


	/**
	 * @return
	 * @throws Exception
	 */
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
	

	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<SearchProgramDisplay> getSearchPrograms() throws Exception {
		
		if ( searchProgramDisplayList != null ) {
			
			return searchProgramDisplayList;
		}
		
		int searchId = getSearchDTO().getId();
		
		searchProgramDisplayList = SearchProgramDisplaySearcher.getInstance().getSearchProgramDisplay( searchId );
		
		return searchProgramDisplayList;
	}
	
	
	
	//  Getters & Setters
	
	public SearchDTO getSearchDTO() {
		return searchDTO;
	}

	public void setSearchDTO(SearchDTO searchDTO) {
		this.searchDTO = searchDTO;
	}
	

	public CutoffPageDisplaySearchLevel getCutoffPageDisplaySearchLevel() {
		return cutoffPageDisplaySearchLevel;
	}



	public void setCutoffPageDisplaySearchLevel(
			CutoffPageDisplaySearchLevel cutoffPageDisplaySearchLevel) {
		this.cutoffPageDisplaySearchLevel = cutoffPageDisplaySearchLevel;
	}


	
}
