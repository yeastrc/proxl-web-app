package org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data;

import java.util.List;

import org.yeastrc.xlink.dto.LinkerDTO;

public class Linkers_ForSearchId_Response {

	private List<LinkerDTO> linkersForSearchIdList;

	public List<LinkerDTO> getLinkersForSearchIdList() {
		return linkersForSearchIdList;
	}

	public void setLinkersForSearchIdList(List<LinkerDTO> linkersForSearchIdList) {
		this.linkersForSearchIdList = linkersForSearchIdList;
	}

}
