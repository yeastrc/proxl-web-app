package org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.cache_results_in_memory;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;


/**
 * 
 *
 */
public class DataPagesMain_GetDataWebservices_CacheResults_InMemory implements CachedDataCommonIF {
	
	private static final Logger log = LoggerFactory.getLogger( DataPagesMain_GetDataWebservices_CacheResults_InMemory.class );
	
	/**
	 * Data Type of Cached Data
	 *
	 */
	public enum DataType {
		PEPTIDE_SINGLE_SEARCH, PEPTIDE_MULTIPLE_SEARCHES, 
		PROTEINS_ALL_SINGLE_SEARCH, PROTEINS_ALL_MULTIPLE_SEARCHES,
		PROTEINS_CROSSLINKS_SINGLE_SEARCH, PROTEINS_CROSSLINKS_MULTIPLE_SEARCHES,
		PROTEINS_LOOPLINKS_SINGLE_SEARCH, PROTEINS_LOOPLINKS_MULTIPLE_SEARCHES,
		PROTEIN_COVERAGE_SINGLE_SEARCH_MULTIPLE_SEARCHES
	}

	private static final int CACHE_MAX_SIZE_FULL_SIZE = 20;
	private static final int CACHE_MAX_SIZE_SMALL = 2;

	private static final int CACHE_TIMEOUT_FULL_SIZE = 100; // in days
	private static final int CACHE_TIMEOUT_SMALL = 20; // in days


	private static final AtomicLong cacheGetCount = new AtomicLong();
	private static final AtomicLong cacheDBRetrievalCount = new AtomicLong();
	
	private static volatile int prevDayOfYear = -1;

	private static boolean debugLogLevelEnabled = false;
	
	/**
	 * Static singleton instance
	 */
	private static DataPagesMain_GetDataWebservices_CacheResults_InMemory _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized DataPagesMain_GetDataWebservices_CacheResults_InMemory getInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new DataPagesMain_GetDataWebservices_CacheResults_InMemory();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private DataPagesMain_GetDataWebservices_CacheResults_InMemory() {
		if ( log.isDebugEnabled() ) {
			debugLogLevelEnabled = true;
			log.debug( "debug log level enabled" );
		}

		cacheHolderInternal = new CacheHolderInternal( this );

		//  Register this class with the centralized Cached Data Registry, to support centralized cache clearing
		CachedDataCentralRegistry.getInstance().register( this );
	}

	private CacheHolderInternal cacheHolderInternal;

	
	@Override
	public CacheCurrentSizeMaxSizeResult getCurrentCacheSizeAndMax() throws Exception {
		return cacheHolderInternal.getCurrentCacheSizeAndMax();
	}
	

	@Override
	public void clearCacheData() throws Exception {
		clearCache();
	}

	/**
	 * Recreate the cache using current config values, if they exist, or else defaults
	 * @throws Exception 
	 */
	public void clearCache() throws Exception {
		printPrevCacheHitCounts( true /* forcePrintNow */ );
		cacheHolderInternal.invalidate();
	}
	
	/**
	 * @param dataType
	 * @param requestJSONBytes
	 * @return - null if not in cache
	 * @throws Exception
	 */
	public byte[] getData( DataType dataType, byte[] requestJSONBytes ) throws Exception {

		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}
		
		Cache<LocalCacheKey, LocalCacheValue> cache = cacheHolderInternal.getCache();
		
		if ( cache != null ) {

			LocalCacheKey localCacheKey = new LocalCacheKey();
			localCacheKey.dataType = dataType;
			localCacheKey.requestJSONBytes = requestJSONBytes;
			
			LocalCacheValue localCacheValue = 
					cache.getIfPresent( localCacheKey );
			if ( localCacheValue == null ) {
				return null;
			}
			return localCacheValue.responseJSONBytes; // EARLY return
		}
		
		return null;
	}

	/**
	 * @param dataType
	 * @param requestJSONBytes
	 * @throws Exception
	 */
	public void putData( DataType dataType, byte[] requestJSONBytes, byte[] responseJSONBytes ) throws Exception {
		
		Cache<LocalCacheKey, LocalCacheValue> cache = cacheHolderInternal.getCache();
		
		if ( cache != null ) {

			LocalCacheKey localCacheKey = new LocalCacheKey();
			localCacheKey.dataType = dataType;
			localCacheKey.requestJSONBytes = requestJSONBytes;
			
			LocalCacheValue localCacheValue = new LocalCacheValue();
			localCacheValue.responseJSONBytes = responseJSONBytes;
			
			cache.put( localCacheKey, localCacheValue );
		}
	}


	/**
	 * classes for holding data in the cache
	 * 
	 * key to the cache
	 *
	 */
	private static class LocalCacheKey {
		
		DataType dataType;
		byte[] requestJSONBytes;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
			result = prime * result + Arrays.hashCode(requestJSONBytes);
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
			if (dataType != other.dataType)
				return false;
			if (!Arrays.equals(requestJSONBytes, other.requestJSONBytes))
				return false;
			return true;
		}
		
	}

	/**
	 * RESPONSE Object
	 *
	 */
	private static class LocalCacheValue {
		
		byte[] responseJSONBytes;
	}

	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private DataPagesMain_GetDataWebservices_CacheResults_InMemory parentObject;
		
		private CacheHolderInternal( DataPagesMain_GetDataWebservices_CacheResults_InMemory parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private Cache<LocalCacheKey, LocalCacheValue> dbRecordsDataCache = null;
		
		private int cacheMaxSize;

		/**
		 * @return
		 * @throws Exception
		 */
		public synchronized CacheCurrentSizeMaxSizeResult getCurrentCacheSizeAndMax() throws Exception {
			CacheCurrentSizeMaxSizeResult result = new CacheCurrentSizeMaxSizeResult();
			if ( dbRecordsDataCache != null ) {
				result.setCurrentSize( dbRecordsDataCache.size() );
				result.setMaxSize( cacheMaxSize );
			}
			return result;
		}
		
		/**
		 * @throws Exception
		 */
		private synchronized Cache<LocalCacheKey, LocalCacheValue> getCache(  ) throws Exception {
			if ( ! cacheDataInitialized ) { 
				CachedDataSizeOptions cachedDataSizeOptions = 
						CachedDataCentralConfigStorageAndProcessing.getInstance().getCurrentSizeConfigValue();
				
				if ( cachedDataSizeOptions == CachedDataSizeOptions.FEW ) {
					//  No Cache, just mark initialized, dbRecordsDataCache already set to null;
					cacheDataInitialized = true;
					return dbRecordsDataCache;  //  EARLY RETURN
				}
				
				int cacheTimeout = CACHE_TIMEOUT_FULL_SIZE;
				cacheMaxSize = DataPagesMain_GetDataWebservices_CacheResults_InMemory.CACHE_MAX_SIZE_FULL_SIZE;
				if ( cachedDataSizeOptions == CachedDataSizeOptions.HALF ) {
					cacheMaxSize = cacheMaxSize / 2;
				} else if ( cachedDataSizeOptions == CachedDataSizeOptions.SMALL ) {
					cacheMaxSize = DataPagesMain_GetDataWebservices_CacheResults_InMemory.CACHE_MAX_SIZE_SMALL;
					cacheTimeout = CACHE_TIMEOUT_SMALL;
				}
				
				dbRecordsDataCache = CacheBuilder.newBuilder()
						.expireAfterAccess( cacheTimeout, TimeUnit.DAYS )
						.maximumSize( cacheMaxSize )
						.build();
			//			    .build(); // no CacheLoader
				cacheDataInitialized = true;
			}
			return dbRecordsDataCache;
		}

		private synchronized void invalidate() {
			dbRecordsDataCache = null;
			cacheDataInitialized = false;
		}
		
 	}

	/**
	 * 
	 */
	private void printPrevCacheHitCounts( boolean forcePrintNow ) {
		
		Calendar now = Calendar.getInstance();
		
		int nowDayOfYear = now.get( Calendar.DAY_OF_YEAR );
		
		if ( prevDayOfYear != nowDayOfYear || forcePrintNow ) {

			if ( prevDayOfYear != -1 ) {
				if ( debugLogLevelEnabled ) {
					log.debug( "Cache total gets and db loads(misses) for previous day (or since last cache recreate): 'total gets': " + cacheGetCount.intValue() 
					+ ", misses: " + cacheDBRetrievalCount.intValue() );
				}
			}
			if ( forcePrintNow ) {
				if ( debugLogLevelEnabled ) {
					log.debug( "Cache total gets and db loads(misses) since last print: 'total gets': " + cacheGetCount.intValue() 
					+ ", misses: " + cacheDBRetrievalCount.intValue() );
				}
			}
			
			prevDayOfYear = nowDayOfYear;
			//  Reset cache hit and miss counters
			cacheGetCount.set(0);
			cacheDBRetrievalCount.set(0);
		}
		
	}


}
