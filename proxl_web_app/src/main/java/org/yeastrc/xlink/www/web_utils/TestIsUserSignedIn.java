package org.yeastrc.xlink.www.web_utils;

//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.user_session_management.UserSession;
/**
 * 
 *
 */
public class TestIsUserSignedIn {

//	private static final Logger log = LoggerFactory.getLogger( TestIsUserSignedIn.class);
	private TestIsUserSignedIn() { }
	private static final TestIsUserSignedIn _INSTANCE = new TestIsUserSignedIn();
	public static TestIsUserSignedIn getInstance() { return _INSTANCE; }
	
	/**
	 * Is there an actual user signed on
	 * 
	 * @param userSession
	 * @return
	 * @throws Exception
	 */
	public boolean testIsUserSignedIn( UserSession userSession )
					  throws Exception {
		if ( userSession == null ) {
			//  No User session 
			return false;
		}
		if ( ! userSession.isActualUser() ) {
			//  No User session 
			return false;
		}
		return true;
	}
}
