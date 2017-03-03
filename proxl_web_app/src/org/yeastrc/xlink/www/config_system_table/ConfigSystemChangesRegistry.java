package org.yeastrc.xlink.www.config_system_table;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * Registry for classes that need notification when then the config_system values change 
 *
 * Classes registering must implement ConfigSystemChangesRegistryItemIF
 */
public class ConfigSystemChangesRegistry {

	private static final Logger log = Logger.getLogger( ConfigSystemChangesRegistry.class );

	private static final ConfigSystemChangesRegistry instance = new ConfigSystemChangesRegistry();

	private final Map<String,ConfigSystemChangesRegistryItemIF> configSystemChangesItemMap = new ConcurrentHashMap<>();
	
	//  private constructor
	private ConfigSystemChangesRegistry() { }
	
	/**
	 * @return newly created instance
	 */
	public static ConfigSystemChangesRegistry getInstance() { 
		return instance;
	}
	
	/**
	 * @param configSystemChangesRegistryItem
	 */
	public void register( ConfigSystemChangesRegistryItemIF configSystemChangesRegistryItem ) {
		String className = configSystemChangesRegistryItem.getClass().getName();
		configSystemChangesItemMap.put( className, configSystemChangesRegistryItem );
//		if ( log.isInfoEnabled() ) {
//			Exception e = new Exception();
//			log.info( "Adding configSystemChangesRegistryItem: " + configSystemChangesRegistryItem, e );
//		}
	}
	
	
	/**
	 * @throws Exception
	 */
	public void allConfigKeysChanged() throws Exception {
//		if ( log.isInfoEnabled() ) {
//			log.info( "allConfigKeysChanged() called" );
//		}
//		log.warn( "INFO: allConfigKeysChanged() called" );
		
		try {
			for ( Map.Entry<String,ConfigSystemChangesRegistryItemIF> entry : configSystemChangesItemMap.entrySet() ) {
				ConfigSystemChangesRegistryItemIF item = entry.getValue();
				item.allConfigKeysChanged();
			}
		} catch (Exception e) {
			String msg = "Error allConfigKeysChanged(): ";
			log.error( msg, e );
			throw e;
		}
	}
	
	public void specificConfigKeyChanged( String configKey ) throws Exception {

		try {
			for ( Map.Entry<String,ConfigSystemChangesRegistryItemIF> entry : configSystemChangesItemMap.entrySet() ) {
				ConfigSystemChangesRegistryItemIF item = entry.getValue();
				item.specificConfigKeyChanged( configKey );
			}
		} catch (Exception e) {
			String msg = "Error specificConfigKeyChanged( configKey ): ";
			log.error( msg, e );
			throw e;
		}
	}


}
