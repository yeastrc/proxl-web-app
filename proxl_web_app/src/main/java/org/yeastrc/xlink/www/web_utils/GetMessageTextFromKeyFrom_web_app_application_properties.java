package org.yeastrc.xlink.www.web_utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.PropertyFileNames;
/**
 * 
 *
 */
public class GetMessageTextFromKeyFrom_web_app_application_properties {
	
	private static Logger log = Logger.getLogger(GetMessageTextFromKeyFrom_web_app_application_properties.class);
	
	private Properties configProps = null;
	private Map<String, String> retrievedProperties = new HashMap<String, String>();
	private static final GetMessageTextFromKeyFrom_web_app_application_properties instance = new GetMessageTextFromKeyFrom_web_app_application_properties();
	
	/**
	 * 
	 */
	private GetMessageTextFromKeyFrom_web_app_application_properties() {
		ClassLoader mainClassLoader = this.getClass().getClassLoader();
		String propertyFilename = PropertyFileNames.MAIN_PROPERTY_FILE;
		URL configPropFile = mainClassLoader.getResource( propertyFilename );
		if ( configPropFile == null ) {
			String msg = "Properties file '" + propertyFilename + "' not found ";
			log.error( msg );
			throw new RuntimeException( msg );
		} 
		log.info( "Properties file '" + propertyFilename + "' load path = " + configPropFile.getFile() );
		InputStream props = mainClassLoader.getResourceAsStream( propertyFilename );
		if ( props == null ) {
			String msg = "Properties file '" + propertyFilename + "' not found ";
			log.error( msg );
			throw new RuntimeException( msg );
		} 
		configProps = new Properties();
		try {
			configProps.load(props);
		} catch (IOException e) {
			String msg = "Failed to load Properties file '" + propertyFilename + "'";
			log.error( msg, e );
			throw new RuntimeException( msg, e );
		}
	}
	/**
	 * @return
	 */
	public static GetMessageTextFromKeyFrom_web_app_application_properties getInstance() { return instance; }
	/**
	 * @param key
	 * @return
	 */
	public String getMessageForKey( String key ) {
		if ( configProps == null ){
			String msg = "Properties file not loaded yet";
			log.error( msg );
			throw new IllegalStateException( msg );
		}
		String message = retrievedProperties.get(key);
		if ( message == null ) {
			message = configProps.getProperty( key );
			retrievedProperties.put(key, message);
		}
		return message;
	}
}
