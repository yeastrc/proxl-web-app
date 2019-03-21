package org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search;

import java.util.List;

import org.yeastrc.xlink.dto.LinkerPerSearchCleavedCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchCrosslinkMassDTO;
import org.yeastrc.xlink.dto.LinkerPerSearchMonolinkMassDTO;
import org.yeastrc.xlink.dto.SearchLinkerDTO;
import org.yeastrc.xlink.exceptions.ProxlBaseInternalErrorException;

public class LinkersDBDataSingleSearchSingleLinker {

	private SearchLinkerDTO searchLinkerDTO;
	
	private List<LinkerPerSearchMonolinkMassDTO> linkerPerSearchMonolinkMassDTOList;
	private List<LinkerPerSearchCrosslinkMassDTO> linkerPerSearchCrosslinkMassDTOList;
	private List<LinkerPerSearchCleavedCrosslinkMassDTO> linkerPerSearchCleavedCrosslinkMassDTOList;
	private List<SearchLinkerPerSideDefinitionObj> searchLinkerPerSideDefinitionObjList;
	
	private boolean linkerPerSearchMonolinkMassDTOListNotLoaded;
	
	public List<LinkerPerSearchMonolinkMassDTO> getLinkerPerSearchMonolinkMassDTOList() {
		if ( linkerPerSearchMonolinkMassDTOListNotLoaded ) {
			throw new ProxlBaseInternalErrorException( "getLinkerPerSearchMonolinkMassDTOList() called when linkerPerSearchMonolinkMassDTOListNotLoaded is true" );
		}
		return linkerPerSearchMonolinkMassDTOList;
	}
	public void setLinkerPerSearchMonolinkMassDTOList(
			List<LinkerPerSearchMonolinkMassDTO> linkerPerSearchMonolinkMassDTOList) {
		linkerPerSearchMonolinkMassDTOListNotLoaded = false;
		this.linkerPerSearchMonolinkMassDTOList = linkerPerSearchMonolinkMassDTOList;
	}
	
	public SearchLinkerDTO getSearchLinkerDTO() {
		return searchLinkerDTO;
	}
	public void setSearchLinkerDTO(SearchLinkerDTO searchLinkerDTO) {
		this.searchLinkerDTO = searchLinkerDTO;
	}
	public List<LinkerPerSearchCrosslinkMassDTO> getLinkerPerSearchCrosslinkMassDTOList() {
		return linkerPerSearchCrosslinkMassDTOList;
	}
	public void setLinkerPerSearchCrosslinkMassDTOList(
			List<LinkerPerSearchCrosslinkMassDTO> linkerPerSearchCrosslinkMassDTOList) {
		this.linkerPerSearchCrosslinkMassDTOList = linkerPerSearchCrosslinkMassDTOList;
	}
	public List<LinkerPerSearchCleavedCrosslinkMassDTO> getLinkerPerSearchCleavedCrosslinkMassDTOList() {
		return linkerPerSearchCleavedCrosslinkMassDTOList;
	}
	public void setLinkerPerSearchCleavedCrosslinkMassDTOList(
			List<LinkerPerSearchCleavedCrosslinkMassDTO> linkerPerSearchCleavedCrosslinkMassDTOList) {
		this.linkerPerSearchCleavedCrosslinkMassDTOList = linkerPerSearchCleavedCrosslinkMassDTOList;
	}
	public List<SearchLinkerPerSideDefinitionObj> getSearchLinkerPerSideDefinitionObjList() {
		return searchLinkerPerSideDefinitionObjList;
	}
	public void setSearchLinkerPerSideDefinitionObjList(
			List<SearchLinkerPerSideDefinitionObj> searchLinkerPerSideDefinitionObjList) {
		this.searchLinkerPerSideDefinitionObjList = searchLinkerPerSideDefinitionObjList;
	}
	public boolean isLinkerPerSearchMonolinkMassDTOListNotLoaded() {
		return linkerPerSearchMonolinkMassDTOListNotLoaded;
	}
	public void setLinkerPerSearchMonolinkMassDTOListNotLoaded(boolean linkerPerSearchMonolinkMassDTOListNotLoaded) {
		this.linkerPerSearchMonolinkMassDTOListNotLoaded = linkerPerSearchMonolinkMassDTOListNotLoaded;
	}
}
