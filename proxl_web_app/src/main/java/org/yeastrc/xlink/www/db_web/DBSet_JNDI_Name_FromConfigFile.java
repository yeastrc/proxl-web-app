package org.yeastrc.xlink.www.db_web;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

/**
 * 
 *
 */
public class DBSet_JNDI_Name_FromConfigFile {
	
	private static final Logger log = LoggerFactory.getLogger( DBSet_JNDI_Name_FromConfigFile.class);
	
	public static String DB_CONFIG_FILENAME = "database_connection.properties";
	private static String PROPERTY_NAME__PROXL_JNDI_NAME = "proxl.jndi.name";
	
	//  private constructor
	private DBSet_JNDI_Name_FromConfigFile() { }
	/**
	 * @return newly created instance
	 */
	public static DBSet_JNDI_Name_FromConfigFile getInstance() { 
		return new DBSet_JNDI_Name_FromConfigFile(); 
	}
	
	/**
	 * @throws Exception
	 */
	public void dbSet_JNDI_Name_FromConfigFile() throws Exception {
		InputStream propertiesFileAsStream = null;
		try {
			//  Get config file from class path
			ClassLoader thisClassLoader = this.getClass().getClassLoader();
			URL configPropFile = thisClassLoader.getResource( DB_CONFIG_FILENAME );
			if ( configPropFile == null ) {
				//  No properties file
				return;  //  EARLY EXIT
//				String msg = "Properties file '" + DB_CONFIG_FILENAME + "' not found in class path.";
//				log.error( msg );
//				throw new Exception( msg );
			} else {
				log.info( "Properties file '" + DB_CONFIG_FILENAME + "' found, load path = " + configPropFile.getFile() );
			}
			propertiesFileAsStream = thisClassLoader.getResourceAsStream( DB_CONFIG_FILENAME );
			if ( propertiesFileAsStream == null ) {
				//  No properties file
				return;  //  EARLY EXIT
//				String msg = "Properties file '" + DB_CONFIG_FILENAME + "' not found in class path.";
//				log.error( msg );
//				throw new Exception( msg );
			}
			Properties configProps = new Properties();
			configProps.load(propertiesFileAsStream);
			String jndiName = configProps.getProperty( PROPERTY_NAME__PROXL_JNDI_NAME );
			if ( StringUtils.isNotEmpty( jndiName ) ) {
				DBConnectionFactoryWeb.setProxlJNDIName( jndiName );
			}
		} catch ( RuntimeException e ) {
			log.error( "Error processing Properties file '" + DB_CONFIG_FILENAME + "', exception: " + e.toString(), e );
			throw e;
		} finally {
			if ( propertiesFileAsStream != null ) {
				propertiesFileAsStream.close();
			}
		}
	}
}
