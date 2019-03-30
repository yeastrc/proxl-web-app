package org.yeastrc.proxl.import_xml_to_db_runner_pgm.config;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * Values from the importer runner config file
 *
 */
public class ImporterRunnerConfigData {

	private static final Logger log = LoggerFactory.getLogger(  ImporterRunnerConfigData.class );
	
	private static Integer waitTimeForNextCheckForImportToProcess_InSeconds;
	
	private static String javaExecutableWithPath;
	private static List<String> javaExecutableParameters;
	
	private static String importerJarWithPath;
	private static String importerDbConfigWithPath;
	
	private static String proxlWebAppBaseURL;
	
	private static String commandToRunOnSuccessfulImport;
	private static String commandToRunOnSuccessfulImportSyoutSyserrDir;
	
	private static boolean configured = false;

	
	public static Integer getWaitTimeForNextCheckForImportToProcess_InSeconds() {
		if ( ! configured ) {
			String msg = "ImporterRunnerConfigData not configured";
			log.error( msg );
			throw new IllegalStateException(msg);
		}
		return waitTimeForNextCheckForImportToProcess_InSeconds;
	}
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
	public static String getCommandToRunOnSuccessfulImport() {
		if ( ! configured ) {
			String msg = "ImporterRunnerConfigData not configured";
			log.error( msg );
			throw new IllegalStateException(msg);
		}
		return commandToRunOnSuccessfulImport;
	}
	public static String getCommandToRunOnSuccessfulImportSyoutSyserrDir() {
		if ( ! configured ) {
			String msg = "ImporterRunnerConfigData not configured";
			log.error( msg );
			throw new IllegalStateException(msg);
		}
		return commandToRunOnSuccessfulImportSyoutSyserrDir;
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
	public static List<String> getJavaExecutableParameters() {
		return javaExecutableParameters;
	}
	public static void setJavaExecutableParameters(List<String> javaExecutableParameters) {
		ImporterRunnerConfigData.javaExecutableParameters = javaExecutableParameters;
	}

	
	public static void setProxlWebAppBaseURL(String proxlWebAppBaseURL) {
		ImporterRunnerConfigData.proxlWebAppBaseURL = proxlWebAppBaseURL;
	}

	public static void setCommandToRunOnSuccessfulImport(String commandToRunOnSuccessfulImport) {
		ImporterRunnerConfigData.commandToRunOnSuccessfulImport = commandToRunOnSuccessfulImport;
	}
	public static void setCommandToRunOnSuccessfulImportSyoutSyserrDir(
			String commandToRunOnSuccessfulImportSyoutSyserrDir) {
		ImporterRunnerConfigData.commandToRunOnSuccessfulImportSyoutSyserrDir = commandToRunOnSuccessfulImportSyoutSyserrDir;
	}
	public static void setWaitTimeForNextCheckForImportToProcess_InSeconds(
			Integer waitTimeForNextCheckForImportToProcess_InSeconds) {
		ImporterRunnerConfigData.waitTimeForNextCheckForImportToProcess_InSeconds = waitTimeForNextCheckForImportToProcess_InSeconds;
	}

	
}
