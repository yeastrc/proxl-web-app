package org.yeastrc.xlink.www.no_data_validation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

/**
 * Singleton Class
 * 
 * If config is set for throw exception on no data, this returns true
 *
 */
public class ThrowExceptionOnNoDataConfig {
	
	private static final Logger log = LoggerFactory.getLogger(  ThrowExceptionOnNoDataConfig.class );

	public static String DB_CONFIG_FILENAME = "throw_exception_no_data.properties";

	private static String PROPERTY_NAME__THROW_EXCEPTION_NO_DATA = "throw_exception_no_data";
	
	private static String PROPERTY_VALUE__TRUE = "true";
	
	private static ThrowExceptionOnNoDataConfig _instance = new ThrowExceptionOnNoDataConfig();

	////
	

	private boolean initialized = false;
	
	
	//  Values from properties file
	
	private boolean throwExceptionNoData = false;
	
	
	/**
	 * private constructor
	 */
	private ThrowExceptionOnNoDataConfig() { }

	/**
	 * Static get singleton instance
	 * @return
	 */
	public static ThrowExceptionOnNoDataConfig getInstance() {
		return _instance; 
	}
	
	public void init() throws IOException {
		
		initialized = true; //  Set here since multiple exit points from method
		
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
			
			String throwExceptionNoDataString = configProps.getProperty( PROPERTY_NAME__THROW_EXCEPTION_NO_DATA );
			
			if ( PROPERTY_VALUE__TRUE.equals( throwExceptionNoDataString ) ) {
				throwExceptionNoData  = true;
				log.warn("In config file '" + DB_CONFIG_FILENAME + "', property '" + PROPERTY_NAME__THROW_EXCEPTION_NO_DATA 
						+ "' is set to true" );
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

	public boolean isThrowExceptionNoData() {
		if ( ! initialized ) {
			throw new IllegalStateException( "init() not called");
		}
		return this.throwExceptionNoData;
	}

	public void setThrowExceptionNoData(boolean throwExceptionNoData) {
		this.throwExceptionNoData = throwExceptionNoData;
	}
}
