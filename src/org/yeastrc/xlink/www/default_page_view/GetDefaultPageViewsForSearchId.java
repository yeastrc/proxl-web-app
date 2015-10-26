package org.yeastrc.xlink.www.default_page_view;

import org.apache.log4j.Logger;

public class GetDefaultPageViewsForSearchId {

	private static final Logger log = Logger.getLogger(GetDefaultPageViewsForSearchId.class);

	//  private constructor
	private GetDefaultPageViewsForSearchId() { }
	
	/**
	 * @return newly created instance
	 */
	public static GetDefaultPageViewsForSearchId getInstance() { 
		return new GetDefaultPageViewsForSearchId(); 
	}
	
}
