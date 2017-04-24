package org.yeastrc.xlink.www.web_utils;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
/**
 * 
 * Get Link types for searchers.  
 * 
 * Return null if all link types are selected, otherwise returns link types for use in searchers
 */
public class GetLinkTypesForSearchers {

	private static final Logger log = Logger.getLogger(GetLinkTypesForSearchers.class);
	private GetLinkTypesForSearchers() { }
	public static GetLinkTypesForSearchers getInstance() { return new GetLinkTypesForSearchers(); }
	
	/**
	 * @param linkTypes
	 * @return null if all link types are selected, otherwise returns link types for use in searchers
	 * @throws Exception 
	 */
	public String[] getLinkTypesForSearchers ( String[] linkTypes ) throws Exception {
		String[] resultLinkTypes = linkTypes;
		boolean webLinkTypeSelected_CROSSLINK = false;
		boolean webLinkTypeSelected_LOOPLINK = false;
		boolean webLinkTypeSelected_UNLINKED = false;
		if ( linkTypes != null ) {
			for ( String linkType : linkTypes ) {
				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) ) {
					webLinkTypeSelected_CROSSLINK = true;
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType ) ) {
					webLinkTypeSelected_LOOPLINK = true;
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkType ) ) {
					webLinkTypeSelected_UNLINKED = true;
				} else {
					String msg = "linkType is invalid, linkType: " + linkType;
					log.error( linkType );
					throw new Exception( msg );
				}
			}
			if ( webLinkTypeSelected_CROSSLINK 
					&& webLinkTypeSelected_LOOPLINK 
					&& webLinkTypeSelected_UNLINKED ) {
				resultLinkTypes = null;
			}
		}
		return resultLinkTypes;
	}
}
