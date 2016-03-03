package org.yeastrc.xlink.www.default_page_view;

//import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;

/**
 * 
 *
 */
public class GetDefaultURLFromPageURL {

//	private static final Logger log = Logger.getLogger(GetDefaultURLFromPageURL.class);

	//  private constructor
	private GetDefaultURLFromPageURL() { }
	
	/**
	 * @return newly created instance
	 */
	public static GetDefaultURLFromPageURL getInstance() { 
		return new GetDefaultURLFromPageURL(); 
	}
	


	/**
	 * @param pageUrl
	 * @return
	 */
	public String getDefaultURLFromPageURL( String pageUrl ) {

		String contextPathJSVar = CurrentContext.getCurrentWebAppContext();
		
		String contextPathJSVarWithTrailingSlash = contextPathJSVar + "/"; // Add to correctly find context in URL

		int contextStart = pageUrl.indexOf( contextPathJSVarWithTrailingSlash );
		int pageUrlAfterContextStartPosition = contextStart + contextPathJSVarWithTrailingSlash.length();

		String firstCharAfterContext = pageUrl.substring( pageUrlAfterContextStartPosition, pageUrlAfterContextStartPosition + 1 );

		if ( "/".equals(firstCharAfterContext ) ) {

			pageUrlAfterContextStartPosition++;  //  increment to move past "/"
		}

		String pageUrlAfterContext = pageUrl.substring( pageUrlAfterContextStartPosition );

		String defaultPageViewURL = pageUrlAfterContext;

		return defaultPageViewURL;
	}





	/**
	 * @param pageUrl
	 * @return
	 */
	public String getPageNameFromStrutsActionInURL( String pageUrl ) {

		String defaultPageViewURL = getDefaultURLFromPageURL( pageUrl);

		String pageName = defaultPageViewURL; 

		int questionMarkSeparator = defaultPageViewURL.indexOf( "?" );

		if ( questionMarkSeparator >= 0 ) {
			pageName = defaultPageViewURL.substring( 0, questionMarkSeparator );
		}

		int semiColonSeparator = defaultPageViewURL.indexOf( ";" );  // before jsessionid

		if ( semiColonSeparator >= 0 && semiColonSeparator < questionMarkSeparator ) {
			pageName = defaultPageViewURL.substring( 0, semiColonSeparator );
		}

		return pageName;
	}
}
