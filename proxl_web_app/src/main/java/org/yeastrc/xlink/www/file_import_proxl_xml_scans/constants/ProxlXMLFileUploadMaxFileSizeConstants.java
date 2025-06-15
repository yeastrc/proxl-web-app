package org.yeastrc.xlink.www.file_import_proxl_xml_scans.constants;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemTableGetValueCommon;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;

public class ProxlXMLFileUploadMaxFileSizeConstants {

	private static final Logger log = LoggerFactory.getLogger( ProxlXMLFileUploadMaxFileSizeConstants.class );

	private static final String GIGABYTE_LABEL_SUFFIX = "GB";
	
	private static final long ONE_GIGABYTE = ( 1 * ( (long) ( 1000 * 1000 * 1000 ) ) ); // 1GB - Disk definition, not memory base 2 definition
	
	//  TODO  FAKE VALUE
//	private static final long ONE_GIGABYTE = 100; // TODO  FAKE VALUE
	
	
	private static final int MAX_PROXL_XML_FILE_UPLOAD_SIZE_DEFAULT_IN_GB = 1; // 1GB max
	private static final int MAX_SCAN_FILE_UPLOAD_SIZE_DEFAULT_IN_GB = 10; // 10GB max

	
	public static final String MAX_PROXL_XML_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL = "PROXL_PROXL_XML_FILE_MAX_SIZE_IN_GB";
	public static final String MAX_FASTA_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL = "PROXL_FASTA_FILE_UPLOAD_MAX_SIZE_IN_GB";
	public static final String MAX_SCAN_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL = "PROXL_SCAN_FILE_MAX_SIZE_IN_GB";
	public static final String MAX_GENERIC_OTHER_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL = "PROXL_GENERIC_OTHER_FILE_MAX_SIZE_IN_GB";

	private static volatile String max_proxl_xml_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage;
	private static volatile String max_scan_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage;


	/**
	 * @throws Exception 
	 * 
	 */
	public static void getValuesAndLog() throws Exception {
		
		get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property_Or_ConfigTable__OrDefault__LogIfRequired_IfLog_NoRethrowException(true);
		
	
		get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property_Or_ConfigTable__OrDefault__LogIfRequired_IfLog_NoRethrowException(true);
	}

	
	//   PROXL_XML

	public static int get_PROXL_XML_MAX_FILE_UPLOAD_SIZE__DEFAULT_IN_GB() {
		
		return MAX_PROXL_XML_FILE_UPLOAD_SIZE_DEFAULT_IN_GB;
	}


	public static long get_PROXL_XML_MAX_FILE_UPLOAD_SIZE() {
		
		int result_InGB = get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property_Or_ConfigTable__OrDefault__LogIfRequired_IfLog_NoRethrowException(false);
		
		long result = result_InGB * ONE_GIGABYTE;
		
		return result;
	}

	public static String get_MAX_PROXL_XML_FILE_UPLOAD_SIZE_AS_STRING() {
		
		long result_Number = get_PROXL_XML_MAX_FILE_UPLOAD_SIZE();
		
		return Long.toString( result_Number );
	}
	
	public static String get_MAX_PROXL_XML_FILE_UPLOAD_SIZE_FORMATTED() {

		int result_InGB = get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property_Or_ConfigTable__OrDefault__LogIfRequired_IfLog_NoRethrowException(false);
		
		return result_InGB + GIGABYTE_LABEL_SUFFIX;
	}

	
	//  SCAN
	

	//  SCAN

	public static int get_SCAN_MAX_FILE_UPLOAD_SIZE__DEFAULT_IN_GB() {
		
		return MAX_SCAN_FILE_UPLOAD_SIZE_DEFAULT_IN_GB;
	}

	public static long get_SCAN_MAX_FILE_UPLOAD_SIZE() {
		
		int result_InGB = get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property_Or_ConfigTable__OrDefault__LogIfRequired_IfLog_NoRethrowException(false);
		
		long result = result_InGB * ONE_GIGABYTE;
		
		return result;
	}

	public static String get_MAX_SCAN_FILE_UPLOAD_SIZE_AS_STRING() {

		long result_Number = get_SCAN_MAX_FILE_UPLOAD_SIZE();
		
		return Long.toString( result_Number );
	}
	
	public static String get_MAX_SCAN_FILE_UPLOAD_SIZE_FORMATTED() {

		int result_InGB = get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property_Or_ConfigTable__OrDefault__LogIfRequired_IfLog_NoRethrowException(false);
		
		return result_InGB + GIGABYTE_LABEL_SUFFIX;
	}

	
	/**
	 * Max_ProxlXML_FileSize
	 * 
	 * @return
	 */
	private static int get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property_Or_ConfigTable__OrDefault__LogIfRequired_IfLog_NoRethrowException(
			boolean logResult
			) {

		Integer result = get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property__LogIfRequired_IfLog_NoRethrowException(logResult);

		if ( result == null ) {
			
			String value = null; 
			try {
				value = ConfigSystemTableGetValueCommon.getInstance()
						.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.PROXL_XML_FILE_MAX_FILE_SIZE_IN_GB_KEY );
			} catch (Exception e ) {
				String msg = "Exception getting Config table entry for key: " + ConfigSystemsKeysSharedConstants.PROXL_XML_FILE_MAX_FILE_SIZE_IN_GB_KEY;
				log.error( msg, e );
			}
			if ( StringUtils.isNotEmpty(value) ) {
				try {
					result = Integer.parseInt(value);

					if ( logResult ) {
						log.warn( "INFO::  Value from Config table entry for key: '" 
								+ ConfigSystemsKeysSharedConstants.PROXL_XML_FILE_MAX_FILE_SIZE_IN_GB_KEY
								+ "' is '"
								+ value
								+ "'." );
					}
					
				} catch (Throwable t) {
					String errorMessage = "get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property__OrDefault(): Value in Config Table key '" 
							+ ConfigSystemsKeysSharedConstants.PROXL_XML_FILE_MAX_FILE_SIZE_IN_GB_KEY
							+ "' is not parsable as integer.  value: " + value;
					if ( ! errorMessage.equals(max_proxl_xml_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage)) {
						max_proxl_xml_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage = errorMessage;
						log.error(errorMessage);
					}
				}
			}
		}

		if ( result == null ) {
			
			result = MAX_PROXL_XML_FILE_UPLOAD_SIZE_DEFAULT_IN_GB; // use default

			if ( logResult ) {
				log.warn( "INFO::  Proxl XML File Max Upload size in GB is set to default value of: " 
						+ result
						+ "." );
			}
		}
		
		return result.intValue();
	}


	/**
	 * Max_LimelightXML_FileSize
	 * 
	 * @return
	 */
	public static Integer get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property() {
		
		return get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property__LogIfRequired_IfLog_NoRethrowException(false);
	}
	
	/**
	 * Max_ProxlXML_FileSize
	 * 
	 * @return
	 */
	public static Integer get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property__LogIfRequired_IfLog_NoRethrowException(
			boolean logResult
			) {
		
		Integer result = null;
		
		{
			String value = System.getenv(MAX_PROXL_XML_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL);
			if ( StringUtils.isNotEmpty(value) ) {
				try {
					result = Integer.parseInt(value);
					
					if ( logResult ) {
						log.warn( "INFO::  Value from Environment Variable: '" 
								+ MAX_PROXL_XML_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL
								+ "' is '"
								+ value
								+ "'." );
					}
					
				} catch (Throwable t) {
					String errorMessage = "get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property__OrDefault(): Value in environment variable '" 
							+ MAX_PROXL_XML_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL
							+ "' is not parsable as integer.  value: " + value;
					if ( ! errorMessage.equals(max_proxl_xml_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage)) {
						max_proxl_xml_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage = errorMessage;
						log.error(errorMessage);
					}
				}
			}
		}
		if ( result == null ) {
			
			//  Not in Environment Variable so get from JVM -D Property

			Properties prop = System.getProperties();
			String value = prop.getProperty(MAX_PROXL_XML_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL);
			if ( StringUtils.isNotEmpty(value) ) {
				try {
					result = Integer.parseInt(value);

					if ( logResult ) {
						log.warn( "INFO::  Value from JVM -D property: '" 
								+ MAX_PROXL_XML_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL
								+ "' is '"
								+ value
								+ "'." );
					}
					
				} catch (Throwable t) {
					String errorMessage = "get_Max_ProxlXML_FileSize_From_Environment_Or_JVM_dashD_Property__OrDefault(): Value in JVM -D property '" 
							+ MAX_PROXL_XML_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL
							+ "' is not parsable as integer.  value: " + value;
					if ( ! errorMessage.equals(max_proxl_xml_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage)) {
						max_proxl_xml_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage = errorMessage;
						log.error(errorMessage);
					}
				}
			}
		}

		return result;
	}
	

	//////////////////////////
	
	///////   Max Scan FileSize
	

	/**
	 * Max_Scan_FileSize
	 * 
	 * @return
	 */
	private static int get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property_Or_ConfigTable__OrDefault__LogIfRequired_IfLog_NoRethrowException(
			boolean logResult
			) {
		
		Integer result = get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property__LogIfRequired_IfLog_NoRethrowException(logResult);

		if ( result == null ) {
			
			String value = null; 
			try {
				value = ConfigSystemTableGetValueCommon.getInstance()
						.getConfigValueForConfigKey( ConfigSystemsKeysSharedConstants.SCAN_FILE_MAX_FILE_SIZE_IN_GB_KEY );
			} catch (Exception e ) {
				String msg = "Exception getting Config table entry for key: " + ConfigSystemsKeysSharedConstants.SCAN_FILE_MAX_FILE_SIZE_IN_GB_KEY;
				log.error( msg, e );
			}
			if ( StringUtils.isNotEmpty(value) ) {
				try {
					result = Integer.parseInt(value);

					if ( logResult ) {
						log.warn( "INFO::  Value from Config table entry for key: '" 
								+ ConfigSystemsKeysSharedConstants.SCAN_FILE_MAX_FILE_SIZE_IN_GB_KEY
								+ "' is '"
								+ value
								+ "'." );
					}
					
				} catch (Throwable t) {
					String errorMessage = "get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property__OrDefault(): Value in Config Table key '" 
							+ ConfigSystemsKeysSharedConstants.SCAN_FILE_MAX_FILE_SIZE_IN_GB_KEY
							+ "' is not parsable as integer.  value: " + value;
					if ( ! errorMessage.equals(max_scan_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage)) {
						max_scan_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage = errorMessage;
						log.error(errorMessage);
					}
				}
			}
		}

		if ( result == null ) {
			
			result = MAX_SCAN_FILE_UPLOAD_SIZE_DEFAULT_IN_GB; // use default
		}
		
		return result.intValue();
	}
	

	/**
	 * Max_Scan_FileSize
	 * 
	 * @return
	 */
	public static Integer get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property() {
		
		return get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property__LogIfRequired_IfLog_NoRethrowException(false);
	}
	
	/**
	 * Max_Scan_FileSize
	 * 
	 * @return
	 */
	private static Integer get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property__LogIfRequired_IfLog_NoRethrowException(
			boolean logResult
			) {
		
		Integer result = null;
		
		{
			String value = System.getenv(MAX_SCAN_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL);
			if ( StringUtils.isNotEmpty(value) ) {
				try {
					result = Integer.parseInt(value);
					if ( logResult ) {
						log.warn( "INFO::  Value from Environment Variable: '" 
								+ MAX_SCAN_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL
								+ "' is '"
								+ value
								+ "'." );
					}
					
				} catch (Throwable t) {
					String errorMessage = "get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property__OrDefault(): Value in environment variable '" 
							+ MAX_SCAN_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL
							+ "' is not parsable as integer.  value: " + value;
					if ( ! errorMessage.equals(max_scan_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage)) {
						max_scan_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage = errorMessage;
						log.error(errorMessage);
					}
				}
			}
		}
		if ( result == null ) {
			
			//  Not in Environment Variable so get from JVM -D Property

			Properties prop = System.getProperties();
			String value = prop.getProperty(MAX_SCAN_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL);
			if ( StringUtils.isNotEmpty(value) ) {
				try {
					result = Integer.parseInt(value);
					if ( logResult ) {
						log.warn( "INFO::  Value from JVM -D property: '" 
								+ MAX_SCAN_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL
								+ "' is '"
								+ value
								+ "'." );
					}
				} catch (Throwable t) {
					String errorMessage = "get_Max_Scan_FileSize_From_Environment_Or_JVM_dashD_Property__OrDefault(): Value in JVM -D property '" 
							+ MAX_SCAN_FILE_UPLOAD_SIZE_IN_GB__ENV_LABEL
							+ "' is not parsable as integer.  value: " + value;
					if ( ! errorMessage.equals(max_scan_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage)) {
						max_scan_file_upload_size_default_in_gb_FromEnv_FailedToParse_ErrorMessage = errorMessage;
						log.error(errorMessage);
					}
				}
			}
		}

		return result;
	}

}
