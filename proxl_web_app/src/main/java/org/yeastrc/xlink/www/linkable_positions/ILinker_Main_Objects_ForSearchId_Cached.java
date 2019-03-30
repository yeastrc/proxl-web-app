package org.yeastrc.xlink.www.linkable_positions;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.linker_data_processing_base.ILinkers_Main_ForSingleSearch;
import org.yeastrc.xlink.linker_data_processing_base.Linker_Main_LinkersForSingleSearch_Factory;
import org.yeastrc.xlink.linker_data_processing_base.linker_db_data_per_search.LinkersDBDataSingleSearchRoot;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.linker_db_data_single_search.GetLinkerDBDataForSingleSearch_Cached;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Cache Key:   Integer searchId;
 * Cache Value: ILinker_Main_Objects_ForSearchId_Cached_Response iLinker_Main_Objects_ForSearchId_Cached_Response // defined in this class
 */
public class ILinker_Main_Objects_ForSearchId_Cached implements CachedDataCommonIF {

	private static final Logger log = LoggerFactory.getLogger(  ILinker_Main_Objects_ForSearchId_Cached.class );

	private static final int CACHE_MAX_SIZE_FULL_SIZE = 2000;
	private static final int CACHE_MAX_SIZE_SMALL = 20;

	private static final int CACHE_TIMEOUT_FULL_SIZE = 100; // in days
	private static final int CACHE_TIMEOUT_SMALL = 20; // in days


	private static final AtomicLong cacheGetCount = new AtomicLong();
	private static final AtomicLong cacheDBRetrievalCount = new AtomicLong();
	
	private static volatile int prevDayOfYear = -1;

	private static boolean debugLogLevelEnabled = false;
	
	/**
	 * Static singleton instance
	 */
	private static ILinker_Main_Objects_ForSearchId_Cached _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized ILinker_Main_Objects_ForSearchId_Cached getInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new ILinker_Main_Objects_ForSearchId_Cached();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private ILinker_Main_Objects_ForSearchId_Cached() {
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
	 * RESPONSE Object
	 *
	 */
	public static class ILinker_Main_Objects_ForSearchId_Cached_Response {
		
		ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch;

		public ILinkers_Main_ForSingleSearch getiLinkers_Main_ForSingleSearch() {
			return iLinkers_Main_ForSingleSearch;
		}
	}

	/**
	 * @param searchId
	 * @return - retrieved from DB
	 * @throws Exception
	 */
	public ILinker_Main_Objects_ForSearchId_Cached_Response getSearchLinkers_ForSearchId_Response( Integer searchId ) throws Exception {

		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}
		
		LoadingCache<Integer, ILinker_Main_Objects_ForSearchId_Cached_Response> cache = cacheHolderInternal.getCache();
		
		if ( cache != null ) {
			ILinker_Main_Objects_ForSearchId_Cached_Response iLinker_Main_Objects_ForSearchId_Cached_Response = cache.get( searchId );
			return iLinker_Main_Objects_ForSearchId_Cached_Response; // EARLY return
		}
		
		ILinker_Main_Objects_ForSearchId_Cached_Response iLinker_Main_Objects_ForSearchId_Cached_Response = cacheHolderInternal.createResponseObject( searchId );
		return iLinker_Main_Objects_ForSearchId_Cached_Response;
	}


	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private ILinker_Main_Objects_ForSearchId_Cached parentObject;
		
		private CacheHolderInternal( ILinker_Main_Objects_ForSearchId_Cached parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private LoadingCache<Integer, ILinker_Main_Objects_ForSearchId_Cached_Response> dbRecordsDataCache = null;
		
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
		private synchronized LoadingCache<Integer, ILinker_Main_Objects_ForSearchId_Cached_Response> getCache(  ) throws Exception {
			if ( ! cacheDataInitialized ) { 
				CachedDataSizeOptions cachedDataSizeOptions = 
						CachedDataCentralConfigStorageAndProcessing.getInstance().getCurrentSizeConfigValue();
				
				if ( cachedDataSizeOptions == CachedDataSizeOptions.FEW ) {
					//  No Cache, just mark initialized, dbRecordsDataCache already set to null;
					cacheDataInitialized = true;
					return dbRecordsDataCache;  //  EARLY RETURN
				}
				
				int cacheTimeout = CACHE_TIMEOUT_FULL_SIZE;
				cacheMaxSize = parentObject.CACHE_MAX_SIZE_FULL_SIZE;
				if ( cachedDataSizeOptions == CachedDataSizeOptions.HALF ) {
					cacheMaxSize = cacheMaxSize / 2;
				} else if ( cachedDataSizeOptions == CachedDataSizeOptions.SMALL ) {
					cacheMaxSize = parentObject.CACHE_MAX_SIZE_SMALL;
					cacheTimeout = CACHE_TIMEOUT_SMALL;
				}
				
				dbRecordsDataCache = CacheBuilder.newBuilder()
						.expireAfterAccess( cacheTimeout, TimeUnit.DAYS )
						.maximumSize( cacheMaxSize )
						.build(
								new CacheLoader<Integer, ILinker_Main_Objects_ForSearchId_Cached_Response>() {
									public ILinker_Main_Objects_ForSearchId_Cached_Response load(Integer searchId) throws Exception {
										
										//   WARNING  cannot return null.  
										//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
										
										//  value is NOT in cache so get/create it and return it
										return createResponseObject(searchId);
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
		 * @param searchId
		 * @return
		 * @throws Exception
		 */
		private ILinker_Main_Objects_ForSearchId_Cached_Response createResponseObject( Integer searchId ) throws Exception {
			
			//   WARNING  cannot return null.  
			//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
			
			//  value is NOT in cache so get it and return it
			if ( debugLogLevelEnabled ) {
				cacheDBRetrievalCount.incrementAndGet();
			}
			
			LinkersDBDataSingleSearchRoot linkersDBDataSingleSearchRoot = 
					GetLinkerDBDataForSingleSearch_Cached.getInstance().getLinkersDBDataSingleSearchRoot_ForSearchId( searchId );
			
			ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch = Linker_Main_LinkersForSingleSearch_Factory.getILinker_Main( linkersDBDataSingleSearchRoot );
			
			ILinker_Main_Objects_ForSearchId_Cached_Response iLinker_Main_Objects_ForSearchId_Cached_Response = new ILinker_Main_Objects_ForSearchId_Cached_Response();
			iLinker_Main_Objects_ForSearchId_Cached_Response.iLinkers_Main_ForSingleSearch = iLinkers_Main_ForSingleSearch;
			return iLinker_Main_Objects_ForSearchId_Cached_Response;
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
