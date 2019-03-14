package org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data;

import java.util.List;

import org.yeastrc.xlink.dto.SearchLinkerDTO;


public class SearchLinker_ForSearchId_Response {

	private List<SearchLinkerDTO> searchLinkerDTOList;
	private List<String> linkerAbbreviationsForSearchIdList;

	public List<String> getLinkerAbbreviationsForSearchIdList() {
		return linkerAbbreviationsForSearchIdList;
	}

	public void setLinkerAbbreviationsForSearchIdList(List<String> linkerAbbreviationsForSearchIdList) {
		this.linkerAbbreviationsForSearchIdList = linkerAbbreviationsForSearchIdList;
	}

	public List<SearchLinkerDTO> getSearchLinkerDTOList() {
		return searchLinkerDTOList;
	}

	public void setSearchLinkerDTOList(List<SearchLinkerDTO> searchLinkerDTOList) {
		this.searchLinkerDTOList = searchLinkerDTOList;
	}

}
