package org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data;

import java.util.List;

import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;


public class Linker_CrosslinkMass_ForSearchId_Response {

	private List<LinkerPerSearchCrosslinkMassDTO> linkerPerSearchCrosslinkMassDTOList;

	public List<LinkerPerSearchCrosslinkMassDTO> getLinkerPerSearchCrosslinkMassDTOList() {
		return linkerPerSearchCrosslinkMassDTOList;
	}

	public void setLinkerPerSearchCrosslinkMassDTOList(
			List<LinkerPerSearchCrosslinkMassDTO> linkerPerSearchCrosslinkMassDTOList) {
		this.linkerPerSearchCrosslinkMassDTOList = linkerPerSearchCrosslinkMassDTOList;
	}


}
