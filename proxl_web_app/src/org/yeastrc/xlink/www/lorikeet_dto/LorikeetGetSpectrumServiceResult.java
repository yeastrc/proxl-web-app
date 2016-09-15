package org.yeastrc.xlink.www.lorikeet_dto;

import java.util.List;

/**
 * Response from LorikeetSpectrumService
 *
 */
public class LorikeetGetSpectrumServiceResult {

	private String status;
	
	private LorikeetRootData data;
	
	/**
	 * List of data for the peptides
	 */
	private List<LorikeetPerPeptideData> lorikeetPerPeptideDataList;
	
	
	public List<LorikeetPerPeptideData> getLorikeetPerPeptideDataList() {
		return lorikeetPerPeptideDataList;
	}
	public void setLorikeetPerPeptideDataList(
			List<LorikeetPerPeptideData> lorikeetPerPeptideDataList) {
		this.lorikeetPerPeptideDataList = lorikeetPerPeptideDataList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LorikeetRootData getData() {
		return data;
	}
	public void setData(LorikeetRootData data) {
		this.data = data;
	}
}
