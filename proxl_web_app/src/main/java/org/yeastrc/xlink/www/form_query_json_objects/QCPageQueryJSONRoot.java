package org.yeastrc.xlink.www.form_query_json_objects;

import java.util.List;

/**
 * The Root object for the query for the QC Pages (merged and single search)
 * 
 * includes additions to data stored in URL for QC pages
 *
 */
public class QCPageQueryJSONRoot extends MergedPeptideQueryJSONRoot {

	private List<Integer> includeProteinSeqVIdsDecodedArray;
	
	public List<Integer> getIncludeProteinSeqVIdsDecodedArray() {
		return includeProteinSeqVIdsDecodedArray;
	}
	public void setIncludeProteinSeqVIdsDecodedArray(List<Integer> includeProteinSeqVIdsDecodedArray) {
		this.includeProteinSeqVIdsDecodedArray = includeProteinSeqVIdsDecodedArray;
	}
}
