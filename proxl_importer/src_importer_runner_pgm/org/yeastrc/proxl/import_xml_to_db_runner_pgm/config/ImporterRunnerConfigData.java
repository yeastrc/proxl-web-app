package org.yeastrc.proxl.import_xml_to_db_runner_pgm.config;

import org.apache.log4j.Logger;

/**
 * Values from the importer runner config file
 *
 */
public class ImporterRunnerConfigData {

	private static Logger log = Logger.getLogger( ImporterRunnerConfigData.class );
	
	private static String javaExecutableWithPath;
	private static String importerJarWithPath;
	private static String importerDbConfigWithPath;
	
	private static String proxlWebAppBaseURL;
	
	private static boolean configured = false;
	
	public static String getImporterJarWithPath() {
		if ( ! configured ) {
			String msg = "ImporterRunnerConfigData not configured";
			log.error( msg );
			throw new IllegalStateException(msg);
		}
		return importerJarWithPath;
	}
	public static String getImporterDbConfigWithPath() {
		if ( ! configured ) {
			String msg = "ImporterRunnerConfigData not configured";
			log.error( msg );
			throw new IllegalStateException(msg);
		}
		return importerDbConfigWithPath;
	}
	public static String getProxlWebAppBaseURL() {
		if ( ! configured ) {
			String msg = "ImporterRunnerConfigData not configured";
			log.error( msg );
			throw new IllegalStateException(msg);
		}
		return proxlWebAppBaseURL;
	}
	
	public static void setImporterJarWithPath(String importerJarWithPath) {
		ImporterRunnerConfigData.importerJarWithPath = importerJarWithPath;
	}

	public static void setImporterDbConfigWithPath(String importerDbConfigWithPath) {
		ImporterRunnerConfigData.importerDbConfigWithPath = importerDbConfigWithPath;
	}
	public static boolean isConfigured() {
		return configured;
	}
	public static void setConfigured(boolean configured) {
		ImporterRunnerConfigData.configured = configured;
	}
	public static String getJavaExecutableWithPath() {
		return javaExecutableWithPath;
	}
	public static void setJavaExecutableWithPath(String javaExecutableWithPath) {
		ImporterRunnerConfigData.javaExecutableWithPath = javaExecutableWithPath;
	}
	
	public static void setProxlWebAppBaseURL(String proxlWebAppBaseURL) {
		ImporterRunnerConfigData.proxlWebAppBaseURL = proxlWebAppBaseURL;
	}

	
}
