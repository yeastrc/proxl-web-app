package org.yeastrc.xlink.www.config_properties_file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappConfigException;

/**
 * Populate ProxlConfigFileValues from config file(s)
 *
 */
public class ProxlConfigFileReader {

	private static final Logger log = LoggerFactory.getLogger(  ProxlConfigFileReader.class );

	private static String CONFIG_DEFAULTS_FILENAME = "proxl_config_defaults.properties";
	private static String CONFIG_OVERRIDES_FILENAME = "proxl_config.properties";

	private static String PROPERTY_NAME__SPECTRAL_STORAGE_WEB_APP_URL = "spectral.storage.webapp.url.app.context";
	
	/**
	 * special for when re-computing values for table unified_rp__search__rep_pept__generic_lookup
	 * First set annotation_type_filterable.default_filter_value_at_database_load = null where annotation_type_filterable.default_filter_at_database_load = 1
	 *     so that those records will always cause the determination for is cutoffs == default cutoffs to be false.
	 */
	private static String PROPERTY_NAME__allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True = 
			"allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True";
	
	private static String PROPERTY_NAME__allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True_ValueTrue = "true";
	
	private static enum AllowNoPropertiesFile { YES, NO }
	
	/**
	 * @return new instance
	 */
	public static ProxlConfigFileReader getInstance() {
		return new ProxlConfigFileReader();
	}
	/**
	 * private constructor
	 */
	private ProxlConfigFileReader() {}
	
	/**
	 * @throws IOException
	 * @throws ProxlWebappConfigException
	 */
	public void populateProxlConfigFileValuesFromConfigFiles() throws IOException, ProxlWebappConfigException {
		
		ProxlConfigFileValues proxlConfigFileValues = ProxlConfigFileValues.getInstance();
		
		processPropertiesFilename( CONFIG_DEFAULTS_FILENAME, AllowNoPropertiesFile.NO, proxlConfigFileValues );
		processPropertiesFilename( CONFIG_OVERRIDES_FILENAME, AllowNoPropertiesFile.YES, proxlConfigFileValues );
		
//		if ( StringUtils.isEmpty( proxlConfigFileValues.getUserAccountServerURLandAppContext() ) ) {
//			String msg = "Property  '" + PROPERTY_NAME__USER_ACCOUNT_WEB_APP_URL + "' must have a value in "
//					+ "properties file: '" + CONFIG_DEFAULTS_FILENAME + "'.";
//			log.error( msg );
//			throw new ProxlWebappConfigException( msg );
//		}
//
//		if ( StringUtils.isEmpty( proxlConfigFileValues.getRequestingWebappIdentifier() ) ) {
//			String msg = "Property  '" + PROPERTY_NAME__PROXL_WEBAPP_IDENTIFIER_FOR_USER_ACCOUNT_WEB_APP + "' must have a value in "
//					+ "properties file: '" + CONFIG_DEFAULTS_FILENAME + "'.";
//			log.error( msg );
//			throw new ProxlWebappConfigException( msg );
//		}
		
		log.warn( "INFO: '" + PROPERTY_NAME__SPECTRAL_STORAGE_WEB_APP_URL + "' has value: " 
				+ proxlConfigFileValues.getSpectralStorageServerURLandAppContext() );
		
	}
	
	/**
	 * @param propertiesFilename
	 * @param proxlConfigFileValues
	 * @throws IOException
	 * @throws ProxlWebappConfigException 
	 */
	private void processPropertiesFilename( 
			String propertiesFilename, 
			AllowNoPropertiesFile allowNoPropertiesFile,
			ProxlConfigFileValues proxlConfigFileValues ) throws IOException, ProxlWebappConfigException {

		InputStream propertiesFileAsStream = null;
		try {
			//  Get config file from class path
			ClassLoader thisClassLoader = this.getClass().getClassLoader();
			URL configPropFile = thisClassLoader.getResource( propertiesFilename );
			if ( configPropFile == null ) {
				//  No properties file
				return;  //  EARLY EXIT
//				String msg = "Properties file '" + DB_CONFIG_FILENAME + "' not found in class path.";
//				log.error( msg );
//				throw new Exception( msg );
			} else {
				log.info( "Properties file '" + propertiesFilename + "' found, load path = " + configPropFile.getFile() );
			}
			propertiesFileAsStream = thisClassLoader.getResourceAsStream( propertiesFilename );
			if ( propertiesFileAsStream == null ) {
				//  No properties file
				if ( allowNoPropertiesFile == AllowNoPropertiesFile.YES ) {
					return;  //  EARLY EXIT
				}
				String msg = "Properties file '" + propertiesFilename + "' not found in class path.";
				log.error( msg );
				throw new ProxlWebappConfigException( msg );
			}
			Properties configProps = new Properties();
			configProps.load(propertiesFileAsStream);
			String propertyValue = null;

			propertyValue = configProps.getProperty( PROPERTY_NAME__SPECTRAL_STORAGE_WEB_APP_URL );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				proxlConfigFileValues.setSpectralStorageServerURLandAppContext( propertyValue );
			}
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True );
			if ( PROPERTY_NAME__allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True_ValueTrue.equals( propertyValue ) ) {
				proxlConfigFileValues.setAllowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True( true );
			}
			
		} catch ( RuntimeException e ) {
			log.error( "Error processing Properties file '" + propertiesFilename + "', exception: " + e.toString(), e );
			throw e;
		} finally {
			if ( propertiesFileAsStream != null ) {
				propertiesFileAsStream.close();
			}
		}
	}
}
