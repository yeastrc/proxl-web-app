package org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data;

import java.util.List;

import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosLooplinkDTO;

/**
 * result from SearchReportedPeptideProteinSequencePositionLooplinkSearcher
 *
 */
public class SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result {

	List<SrchRepPeptProtSeqIdPosLooplinkDTO> srchRepPeptProtSeqIdPosLooplinkDTOList;

	public List<SrchRepPeptProtSeqIdPosLooplinkDTO> getSrchRepPeptProtSeqIdPosLooplinkDTOList() {
		return srchRepPeptProtSeqIdPosLooplinkDTOList;
	}

	public void setSrchRepPeptProtSeqIdPosLooplinkDTOList(
			List<SrchRepPeptProtSeqIdPosLooplinkDTO> srchRepPeptProtSeqIdPosLooplinkDTOList) {
		this.srchRepPeptProtSeqIdPosLooplinkDTOList = srchRepPeptProtSeqIdPosLooplinkDTOList;
	}
}
