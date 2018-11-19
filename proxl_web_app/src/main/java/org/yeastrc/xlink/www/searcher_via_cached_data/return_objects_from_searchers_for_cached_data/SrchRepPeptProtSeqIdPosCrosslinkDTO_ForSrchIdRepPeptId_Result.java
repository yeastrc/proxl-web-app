package org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data;

import java.util.List;

import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosCrosslinkDTO;

/**
 * result from SearchReportedPeptideProteinSequencePositionCrosslinkSearcher
 *
 */
public class SrchRepPeptProtSeqIdPosCrosslinkDTO_ForSrchIdRepPeptId_Result {

	List<SrchRepPeptProtSeqIdPosCrosslinkDTO> srchRepPeptProtSeqIdPosCrosslinkDTOList;

	public List<SrchRepPeptProtSeqIdPosCrosslinkDTO> getSrchRepPeptProtSeqIdPosCrosslinkDTOList() {
		return srchRepPeptProtSeqIdPosCrosslinkDTOList;
	}

	public void setSrchRepPeptProtSeqIdPosCrosslinkDTOList(
			List<SrchRepPeptProtSeqIdPosCrosslinkDTO> srchRepPeptProtSeqIdPosCrosslinkDTOList) {
		this.srchRepPeptProtSeqIdPosCrosslinkDTOList = srchRepPeptProtSeqIdPosCrosslinkDTOList;
	}
}
