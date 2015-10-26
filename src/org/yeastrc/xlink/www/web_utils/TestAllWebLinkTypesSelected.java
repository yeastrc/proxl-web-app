package org.yeastrc.xlink.www.web_utils;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;

/**
 * 
 * test all link types are selected.
 */
public class TestAllWebLinkTypesSelected {

	private static final Logger log = Logger.getLogger(TestAllWebLinkTypesSelected.class);

	private TestAllWebLinkTypesSelected() { }
	public static TestAllWebLinkTypesSelected getInstance() { return new TestAllWebLinkTypesSelected(); }
	
	
	/**
	 * @param linkTypes
	 * @return true if all link types are selected.
	 * @throws Exception 
	 */
	public boolean testAllWebLinkTypesSelected ( List<String> linkTypes ) throws Exception {

		boolean allWebLinkTypesSelected = false;
		
		boolean webLinkTypeSelected_CROSSLINK = false;
		boolean webLinkTypeSelected_LOOPLINK = false;
		boolean webLinkTypeSelected_MONOLINK = false;
		boolean webLinkTypeSelected_NO_LINK = false;

		for ( String linkType : linkTypes ) {
			

			if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) ) {

				webLinkTypeSelected_CROSSLINK = true;

			} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType ) ) {

				webLinkTypeSelected_CROSSLINK = true;

			} else if ( PeptideViewLinkTypesConstants.MONOLINK_PSM.equals( linkType ) ) {

				webLinkTypeSelected_CROSSLINK = true;

			} else if ( PeptideViewLinkTypesConstants.NO_LINK_PSM.equals( linkType ) ) {

				webLinkTypeSelected_CROSSLINK = true;

			} else {

				String msg = "linkType is invalid, linkType: " + linkType;

				log.error( linkType );

				throw new Exception( msg );
			}
		}

		
		if ( webLinkTypeSelected_CROSSLINK 
				&& webLinkTypeSelected_LOOPLINK 
				&& webLinkTypeSelected_MONOLINK 
				&& webLinkTypeSelected_NO_LINK ) {
			
			allWebLinkTypesSelected = true;
		}
		
		return allWebLinkTypesSelected;
	}
}
