package org.yeastrc.xlink.www.web_utils;

import java.util.Iterator;
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
		boolean webLinkTypeSelected_UNLINKED = false;
		
//		boolean webLinkTypeSelected_PREVIOUS_VALUE_MONOLINK_PSM = false;
//		boolean webLinkTypeSelected_PREVIOUS_VALUE_NO_LINK_PSM = false;

		
		Iterator<String> linkTypeIter = linkTypes.iterator();
		
		while ( linkTypeIter.hasNext() ) {

			String linkType = linkTypeIter.next();
			

			if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) ) {

				webLinkTypeSelected_CROSSLINK = true;

			} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType ) ) {

				webLinkTypeSelected_LOOPLINK = true;

			} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkType ) ) {

				webLinkTypeSelected_UNLINKED = true;


			//  Process OLD values:

			} else if ( PeptideViewLinkTypesConstants.PREVIOUS_VALUE_MONOLINK_PSM.equals( linkType ) ) {

//				webLinkTypeSelected_PREVIOUS_VALUE_MONOLINK_PSM = true;
				
				linkTypeIter.remove();  //  Remove since value is no longer valid

			} else if ( PeptideViewLinkTypesConstants.PREVIOUS_VALUE_NO_LINK_PSM.equals( linkType ) ) {

//				webLinkTypeSelected_PREVIOUS_VALUE_NO_LINK_PSM = true;

				linkTypeIter.remove();  //  Remove since value is no longer valid
				
				
				
			} else {

				String msg = "linkType is invalid, linkType: " + linkType;

				log.error( linkType );

				throw new Exception( msg );
			}
		}
		
		//  Removed this since would have to get these values back into the form object so that the 
		//   web page would properly reflect the values processed.
		
//		if ( webLinkTypeSelected_CROSSLINK 
//				&& webLinkTypeSelected_LOOPLINK
//				&& webLinkTypeSelected_PREVIOUS_VALUE_MONOLINK_PSM 
//				&& webLinkTypeSelected_PREVIOUS_VALUE_NO_LINK_PSM ) {
//			
//			//  Previously selected all values, so set equivalent current selections
//			
//			webLinkTypeSelected_UNLINKED = true;
//			
//			linkTypes.add( PeptideViewLinkTypesConstants.UNLINKED_PSM );
//		}
		
		
		

		
		if ( webLinkTypeSelected_CROSSLINK 
				&& webLinkTypeSelected_LOOPLINK 
				&& webLinkTypeSelected_UNLINKED ) {
			
			allWebLinkTypesSelected = true;
		}
		
		return allWebLinkTypesSelected;
	}
}
