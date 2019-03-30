package org.yeastrc.xlink.www.objects;

import java.util.Collections;
import java.util.List;
//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.form_page_objects.CutoffPageDisplaySearchLevel;
import org.yeastrc.xlink.www.searcher.SearchProgramDisplaySearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SearchLinkerAbbreviations_ForSearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SearchLinkerAbbreviations_ForSearchId_Response;
import org.yeastrc.xlink.www.web_utils.GetCutoffsAppliedOnImport;


/**
 * Used for the searchDetailsBlock.jsp and viewProject.jsp
 *
 */
public class SearchDTODetailsDisplayWrapper {
	
//	private static final Logger log = LoggerFactory.getLogger(  SearchDTODetailsDisplayWrapper.class );
	
	private SearchDTO searchDTO;
	private List<SearchProgramDisplay> searchProgramDisplayList; 
	private List<CutoffsAppliedOnImportWebDisplay> cutoffsAppliedOnImportList;
	
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
		
		Cached_SearchLinkerAbbreviations_ForSearchId cached_Linkers_ForSearchId = Cached_SearchLinkerAbbreviations_ForSearchId.getInstance();
		int searchId = searchDTO.getSearchId();
		SearchLinkerAbbreviations_ForSearchId_Response linkers_ForSearchId_Response =
				cached_Linkers_ForSearchId.getSearchLinkers_ForSearchId_Response( searchId );
		List<String>  linkerAbbreviationList = linkers_ForSearchId_Response.getLinkerAbbreviationsForSearchIdList();
		if ( linkerAbbreviationList == null || linkerAbbreviationList.isEmpty() ) {
			return "";
		}
		Collections.sort( linkerAbbreviationList );
		String linkersString = linkerAbbreviationList.get(0);
		if ( linkerAbbreviationList.size() > 1 ) {
			//  start loop at second index
			for ( int index = 1; index < linkerAbbreviationList.size(); index++ ) {
				linkersString += ", ";
				linkersString += linkerAbbreviationList.get( index );
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
		int searchId = getSearchDTO().getSearchId();
		searchProgramDisplayList = SearchProgramDisplaySearcher.getInstance().getSearchProgramDisplay( searchId );
		return searchProgramDisplayList;
	}
	
	public List<CutoffsAppliedOnImportWebDisplay> getCutoffsAppliedOnImportList() throws Exception {
		if ( cutoffsAppliedOnImportList != null ) {
			return cutoffsAppliedOnImportList;
		}
		if ( searchDTO == null ) {
			throw new IllegalStateException( "searchDTO == null");
		}
		cutoffsAppliedOnImportList = 
				GetCutoffsAppliedOnImport.getInstance().getCutoffsAppliedOnImportList( searchDTO.getSearchId() );

		return cutoffsAppliedOnImportList;
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
