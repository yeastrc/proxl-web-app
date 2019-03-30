package org.yeastrc.xlink.www.linker_db_data_single_search;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.LinkersDBDataSingleSearchRoot;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Accept int searchId
 * Return linkersDBDataSingleSearchRoot
 *
 */
public class GetLinkerDBDataForSingleSearch_Cached implements CachedDataCommonIF {

	private static final Logger log = LoggerFactory.getLogger( GetLinkerDBDataForSingleSearch_Cached.class);
	
	private static final int CACHE_MAX_SIZE_FULL_SIZE = 2000;
	private static final int CACHE_MAX_SIZE_SMALL_FEW = 500;

	private static final int CACHE_TIMEOUT_FULL_SIZE = 80; // in days
	private static final int CACHE_TIMEOUT_SMALL = 80; // in days


	private static final AtomicLong cacheGetCount = new AtomicLong();
	private static final AtomicLong cacheDBRetrievalCount = new AtomicLong();
	
	private static volatile int prevDayOfYear = -1;

	private static boolean debugLogLevelEnabled = false;
	
	/**
	 * Static singleton instance
	 */
	private static GetLinkerDBDataForSingleSearch_Cached _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized GetLinkerDBDataForSingleSearch_Cached getInstance() throws Exception {
		if ( _instance == null ) {
			_instance = new GetLinkerDBDataForSingleSearch_Cached();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private GetLinkerDBDataForSingleSearch_Cached() {
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
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public LinkersDBDataSingleSearchRoot getLinkersDBDataSingleSearchRoot_ForSearchId( int searchId ) throws Exception {
		
		LocalCacheKey localCacheKey = new LocalCacheKey();
		localCacheKey.searchId = searchId;

		LoadingCache<LocalCacheKey, LocalCacheValue> cache = cacheHolderInternal.getCache();
		
		LocalCacheValue localCacheValue = cache.get( localCacheKey );
		if ( localCacheValue == null ) {
			throw new ProxlWebappInternalErrorException("Should be getting LinkersDBDataSingleSearchRoot data via cache internal load");
		}
		LinkersDBDataSingleSearchRoot linkersDBDataSingleSearchRoot = localCacheValue.linkersDBDataSingleSearchRoot;
		return linkersDBDataSingleSearchRoot;
	}
	
	///////////////////////////////////////////
	
	/**
	 * classes for holding data in the cache
	 * 
	 * key to the cache
	 *
	 */
	private static class LocalCacheKey {
		int searchId;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + searchId;
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
			if (searchId != other.searchId)
				return false;
			return true;
		}
		
	}
	
	////////////////////////

	/**
	 * classes for holding data in the cache
	 * 
	 * value in the cache
	 */
	private static class LocalCacheValue {
		LinkersDBDataSingleSearchRoot linkersDBDataSingleSearchRoot;
	}


	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private GetLinkerDBDataForSingleSearch_Cached parentObject;
		
		private CacheHolderInternal( GetLinkerDBDataForSingleSearch_Cached parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private LoadingCache<LocalCacheKey, LocalCacheValue> dbRecordsDataCache = null;
		
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
		@SuppressWarnings("static-access")
		private synchronized LoadingCache<LocalCacheKey, LocalCacheValue> getCache(  ) throws Exception {
			if ( ! cacheDataInitialized ) { 
				CachedDataSizeOptions cachedDataSizeOptions = 
						CachedDataCentralConfigStorageAndProcessing.getInstance().getCurrentSizeConfigValue();
				
				//  Not applicable for this cache.  Always create a cache
//				if ( cachedDataSizeOptions == CachedDataSizeOptions.FEW ) {
//					//  No Cache, just mark initialized, dbRecordsDataCache already set to null;
//					cacheDataInitialized = true;
//					return dbRecordsDataCache;  //  EARLY RETURN
//				}
				
				int cacheTimeout = CACHE_TIMEOUT_FULL_SIZE;
				cacheMaxSize = parentObject.CACHE_MAX_SIZE_FULL_SIZE;
				if ( cachedDataSizeOptions == CachedDataSizeOptions.HALF ) {
					cacheMaxSize = cacheMaxSize / 2;
				} else if ( cachedDataSizeOptions == CachedDataSizeOptions.SMALL
						|| cachedDataSizeOptions == CachedDataSizeOptions.FEW ) {
					cacheMaxSize = parentObject.CACHE_MAX_SIZE_SMALL_FEW;
					cacheTimeout = CACHE_TIMEOUT_SMALL;
				}
				
				dbRecordsDataCache = CacheBuilder.newBuilder()
						.expireAfterAccess( cacheTimeout, TimeUnit.DAYS )
						.maximumSize( cacheMaxSize )
						.build(
								new CacheLoader<LocalCacheKey, LocalCacheValue>() {
									public LocalCacheValue load( LocalCacheKey LocalCacheKey ) throws Exception {
										
										//   WARNING  cannot return null.  
										//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
										
										//  value is NOT in cache so get it and return it
										return loadFromDB( LocalCacheKey );
									}
								});
			//			    .build(); // no CacheLoader
				cacheDataInitialized = true;
			}
			return dbRecordsDataCache;
		}

		private synchronized void invalidate() {
			dbRecordsDataCache = null;
			cacheDataInitialized = false;
		}

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
			//   WARNING  cannot return null.  
			//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
			//  value is NOT in cache so get it and return it
			LinkersDBDataSingleSearchRoot linkersDBDataSingleSearchRoot =
			GetLinkerDBDataForSingleSearch_Actual_PkgPrivate.getInstance().getLinkersDBDataSingleSearchRoot_ForSearchId( localCacheKey.searchId );
			LocalCacheValue localCacheValue = new LocalCacheValue();
			localCacheValue.linkersDBDataSingleSearchRoot = linkersDBDataSingleSearchRoot;
			return localCacheValue;
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
