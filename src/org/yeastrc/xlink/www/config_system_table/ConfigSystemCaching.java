package org.yeastrc.xlink.www.config_system_table;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.config_system_table_common_access.IConfigSystemTableGetValue;
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Caching for for config_system table
 *
 * Centralized caching so that when the values are updated via the web app the cache gets rebuilt
 * 
 * Singleton instance
 */
public class ConfigSystemCaching implements IConfigSystemTableGetValue {


	private static final Logger log = Logger.getLogger(ConfigSystemCaching.class);



	
	private static final int CACHE_MAX_SIZE = 20;

	private static final int CACHE_TIMEOUT = 200000000; // in seconds

	
	/**
	 * Static singleton instance
	 */
	private static final ConfigSystemCaching _instance = new ConfigSystemCaching();
	
	
	
	
	/**
	 * Static get singleton instance
	 * @return
	 */
	public static ConfigSystemCaching getInstance() {
		return _instance; 
	}
	
	
	/**
	 * Clear all entries from the cache
	 */
	public void clearCache() {
		
		configSystemDataCache.invalidateAll();
	}


	/**
	 * @param configKey
	 * @return null if not found
	 * @throws Exception
	 */
	public String getConfigValueForConfigKey( String configKey ) throws Exception {

		try {

			LocalCacheKey localCacheKey = new LocalCacheKey();

			localCacheKey.configKey = configKey;

			LocalCacheValue localCacheValue = configSystemDataCache.get( localCacheKey );

			String configValue = localCacheValue.configValue;

			return configValue;
			
		} catch ( Exception e ) {
			
			String msg = "Exception getting configSystem value for configKey: '" + configKey + "'.";
			log.error( msg, e );
			throw e;
		}
	}
	
	

	/**
	 * constructor
	 */
	private ConfigSystemCaching() {
		
		configSystemDataCache = CacheBuilder.newBuilder()
				
				.expireAfterAccess( CACHE_TIMEOUT, TimeUnit.SECONDS )
			    .maximumSize( CACHE_MAX_SIZE )
			    .build(
			    		new CacheLoader<LocalCacheKey, LocalCacheValue>() {

			    			public LocalCacheValue load(LocalCacheKey localCacheKey) throws Exception {

			    				//  value is NOT in cache so get it and return it

			    				return loadFromDB(localCacheKey);

			    			}

			    		});
			    
//			    .build(); // no CacheLoader
	}
	
	

	/**
	 * classes for holding data in the cache
	 * 
	 * key to the cache
	 *
	 */
	private static class LocalCacheKey {
		
		String configKey;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((configKey == null) ? 0 : configKey.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LocalCacheKey other = (LocalCacheKey) obj;
			if (configKey == null) {
				if (other.configKey != null)
					return false;
			} else if (!configKey.equals(other.configKey))
				return false;
			return true;
		}

	}
	
	

	/**
	 * classes for holding data in the cache
	 * 
	 * value in the cache
	 *
	 */
	private static class LocalCacheValue {
		
		String configValue;
	}
	
	
	
	
	
	/**
	 * cached data from config_system table
	 */
	private LoadingCache<LocalCacheKey, LocalCacheValue> configSystemDataCache = null;
	
	
	
	
	/**
	 * @param searchIds
	 * @param filterableDescriptiveAnnotationType
	 * @param psmPeptideAnnotationType
	 * @param localCacheKey
	 * @return
	 * @throws Exception
	 */
	private LocalCacheValue loadFromDB(   

			LocalCacheKey localCacheKey

			) throws Exception {
		

		//  value is NOT in cache so get it and return it
		
		String configValue =
				ConfigSystemDAO.getInstance().getConfigValueForConfigKey(  localCacheKey.configKey );
		
		LocalCacheValue localCacheValue = new LocalCacheValue();

		localCacheValue.configValue = configValue;
		
		return localCacheValue;
		
	}

	
}
