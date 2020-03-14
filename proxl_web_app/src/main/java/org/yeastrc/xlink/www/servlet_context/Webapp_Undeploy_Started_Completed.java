package org.yeastrc.xlink.www.servlet_context;

/**
 * ServletContext::contextDestroyed(ServletContextEvent event) has been called
 *
 */
public class Webapp_Undeploy_Started_Completed {
	
	/**
	 * true when ServletContext::contextDestroyed(ServletContextEvent event) has been called
	 */
	private static volatile boolean webapp_Undeploy_Started = false;
	
	/**
	 * true when ServletContext::contextDestroyed(ServletContextEvent event) has been called and has completed
	 */
	private static volatile boolean webapp_Undeploy_Completed = false;

	/**
	 * @return true if ServletContext::contextDestroyed(ServletContextEvent event) has been called
	 */
	public static boolean isWebapp_Undeploy_Started() {
		return webapp_Undeploy_Started;
	}

	/**
	 * true when ServletContext::contextDestroyed(ServletContextEvent event) has been called
	 * @param webapp_Undeploy_Started
	 */
	public static void setWebapp_Undeploy_Started(boolean webapp_Undeploy_Started) {
		Webapp_Undeploy_Started_Completed.webapp_Undeploy_Started = webapp_Undeploy_Started;
	}


	/**
	 * @return true if ServletContext::contextDestroyed(ServletContextEvent event) has been called and has completed
	 */
	public static boolean isWebapp_Undeploy_Completed() {
		return webapp_Undeploy_Completed;
	}

	/**
	 * Set true when ServletContext::contextDestroyed(ServletContextEvent event) has been called and has completed
	 * @param webapp_Undeploy_Completed
	 */
	public static void setWebapp_Undeploy_Completed(boolean webapp_Undeploy_Completed) {
		Webapp_Undeploy_Started_Completed.webapp_Undeploy_Completed = webapp_Undeploy_Completed;
	}

}
