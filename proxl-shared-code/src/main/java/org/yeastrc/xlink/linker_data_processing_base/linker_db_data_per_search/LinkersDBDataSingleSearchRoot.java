package org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search;

import java.util.List;

/**
 * Saved Linkers for a search in the database
 *
 */
public class LinkersDBDataSingleSearchRoot {

	private List<LinkersDBDataSingleSearchSingleLinker> linkersDBDataSingleSearchPerLinkerList;

	public List<LinkersDBDataSingleSearchSingleLinker> getLinkersDBDataSingleSearchPerLinkerList() {
		return linkersDBDataSingleSearchPerLinkerList;
	}
	public void setLinkersDBDataSingleSearchPerLinkerList(
			List<LinkersDBDataSingleSearchSingleLinker> linkersDBDataSingleSearchPerLinkerList) {
		this.linkersDBDataSingleSearchPerLinkerList = linkersDBDataSingleSearchPerLinkerList;
	}
}
