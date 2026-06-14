package org.yeastrc.xlink.www.spring_controllers;

/**
 * Spring MVC return-string equivalents of the Struts global-forwards
 * (see &lt;global-forwards&gt; in WEB-INF/struts-config.xml).
 *
 * <p>All targets are still-Struts .do URLs, reached via forward:/redirect: so the
 * DispatcherServlet hands them back to the Struts ActionServlet (the *.do extension
 * mapping). The redirect= flag here matches the struts-config:
 * <ul>
 *   <li>no_user_session, insufficient_access_privilege, invalidRequest*, home -> redirect="false" -> forward:</li>
 *   <li>account_disabled -> redirect="true" -> redirect:</li>
 * </ul>
 */
public class SpringMvcForwards {

	public static final String NO_USER_SESSION = "forward:/user_loginPage.do";
	public static final String LOGIN = "forward:/user_loginPage.do";
	public static final String INSUFFICIENT_ACCESS_PRIVILEGE = "forward:/user_insufficient_access_privilege.do";
	public static final String ACCOUNT_DISABLED = "redirect:/account_disabled.do";
	public static final String INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS = "forward:/invalidRequestSearchesAcrossProjects.do";
	public static final String INVALID_REQUEST_DATA = "forward:/invalidRequestData.do";
	public static final String HOME = "forward:/home.do";

	private SpringMvcForwards() {}
}
