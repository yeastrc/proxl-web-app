package org.yeastrc.xlink.www.constants;

/**
 * Values in the struts-config.xml in the action mapping in the attribute "parameter"
 * 
 * example: passing "crosslink" to the Struts Action class 
 * 
 *  <action  parameter="crosslink"
 *  
 *  mapping.getParameter(); to retrieve in Action class
 *
 */
public class Struts_Config_Parameter_Values_Constants {

	
	//  Struts config action mapping:     parameter="crosslink"
	
	public static final String STRUTS__PARAMETER__CROSSLINK = "crosslink";

	//  Struts config action mapping:     parameter="looplink"

	public static final String STRUTS__PARAMETER__LOOPLINK = "looplink";

	
	
	/////////   These are used in the Action  ViewMergedSearchCoverageReportAction

	//  Struts config action mapping:     parameter="mergedPage"
	
	public static final String STRUTS__PARAMETER__MERGED_PROTEIN_COVERAGE_PAGE = "mergedPage";

	//  Struts config action mapping:     parameter="notMergedPage"

	public static final String STRUTS__PARAMETER__NOT__MERGED_PROTEIN_COVERAGE_PAGE = "notMergedPage";

}
