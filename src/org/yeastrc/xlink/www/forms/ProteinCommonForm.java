package org.yeastrc.xlink.www.forms;


/**
 * Common for MergedSearchViewProteinsForm and SearchViewProteinsForm
 *
 */
public class ProteinCommonForm  extends PeptideProteinCommonForm {

	private static final long serialVersionUID = 1L;

	
	//  TODO  TEMP TESTING
	
	/**
	 * ExcludeProteinList
	 */
	private String exPr;


	public String getExPr() {
		return exPr;
	}


	public void setExPr(String exPr) {
		this.exPr = exPr;
	}


}
