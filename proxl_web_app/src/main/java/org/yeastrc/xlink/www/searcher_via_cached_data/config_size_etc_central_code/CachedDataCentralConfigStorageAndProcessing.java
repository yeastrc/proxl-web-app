package org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemChangesRegistry;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemChangesRegistryItemIF;
import org.yeastrc.xlink.www.constants.CacheSizeConfigStringsConstants;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;

/**
 * Centralized point for getting config data and storing it
 *
 */
public class CachedDataCentralConfigStorageAndProcessing implements ConfigSystemChangesRegistryItemIF {

	private static final Logger log = LoggerFactory.getLogger(  CachedDataCentralConfigStorageAndProcessing.class );

	private static CachedDataCentralConfigStorageAndProcessing instance = null;

	private static boolean instanceCreated = false;
	
	private volatile String currentSizeConfigValueString = null;
	private volatile CachedDataSizeOptions currentSizeConfigValue = null;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public static synchronized CachedDataCentralConfigStorageAndProcessing getInstance() throws Exception {
		if ( ! instanceCreated ) {
			instance = new CachedDataCentralConfigStorageAndProcessing();
			ConfigSystemChangesRegistry.getInstance().register( instance );
			instance.configGetAndSaveIfChanged();
			instanceCreated = true;
		}
		return instance;
	}

	/**
	 * private constructor
	 */
	private CachedDataCentralConfigStorageAndProcessing() { }

	/**
	 * @return
	 */
	public CachedDataSizeOptions getCurrentSizeConfigValue() {
		return currentSizeConfigValue;
	}

	@Override
	public void allConfigKeysChanged() throws Exception {
		getConfigAndNotify();
	}

	@Override
	public void specificConfigKeyChanged(String configKey) throws Exception {
		if (  ConfigSystemsKeysConstants.CACHE_SIZE_CONFIG_LABEL_KEY.equals( configKey ) ) {
			getConfigAndNotify();
		}
	}
	
	/**
	 * @throws Exception 
	 */
	private void getConfigAndNotify() throws Exception {
		if ( configGetAndSaveIfChanged() ) {
			//  cache size config changed
			//   so rebuild all cache with new size
			CachedDataCentralRegistry.getInstance().clearAllCacheData();
		}
	}
	
	/**
	 * @return true if config value changed
	 * @throws Exception 
	 */
	private boolean configGetAndSaveIfChanged() throws Exception {
	
		String newCacheSizeConfigString =
				ConfigSystemCaching.getInstance()
				.getConfigValueForConfigKey( ConfigSystemsKeysConstants.CACHE_SIZE_CONFIG_LABEL_KEY );
		if ( StringUtils.isEmpty( newCacheSizeConfigString ) ) {
			// Default
			newCacheSizeConfigString = CacheSizeConfigStringsConstants.CACHE_SIZE_VALUE_DEFAULT;
		} 
		
		CachedDataSizeOptions newSizeConfigValue = 
				CachedDataSizeConfigEntryConvertStringToEnum.getInstance()
				.convertStringToEnum_SizeConfigEntry( newCacheSizeConfigString );

		if ( ! newCacheSizeConfigString.equals( currentSizeConfigValueString ) ) {
			//  cache size config changed
			if ( log.isInfoEnabled() ) {
				log.info( "New value for Cache size in config_system table: " + newCacheSizeConfigString );
			}
			currentSizeConfigValue = newSizeConfigValue;
			currentSizeConfigValueString = newCacheSizeConfigString;
			return true;  // config changed
		}
		return false;  // config not changed
	}
	
	
}
