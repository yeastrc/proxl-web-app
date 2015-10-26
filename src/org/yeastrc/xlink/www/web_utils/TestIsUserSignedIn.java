package org.yeastrc.xlink.www.web_utils;


import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.user_account.UserSessionObject;

/**
 * 
 *
 */
public class TestIsUserSignedIn {

	private static final Logger log = Logger.getLogger(TestIsUserSignedIn.class);
	
	
	private TestIsUserSignedIn() { }
	private static final TestIsUserSignedIn _INSTANCE = new TestIsUserSignedIn();
	public static TestIsUserSignedIn getInstance() { return _INSTANCE; }
	

	/**
	 * Is there an actual user signed on
	 * 
	 * @param userSessionObject
	 * @return
	 * @throws Exception
	 */
	public boolean testIsUserSignedIn( UserSessionObject userSessionObject )
					  throws Exception {
				
		if ( userSessionObject == null ) {

			//  No User session 

			return false;
		}

		if ( userSessionObject.getUserDBObject() == null ) {

			//  No User session 

			return false;
		}
		
		return true;
	}
}
