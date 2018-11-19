package org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataNotFoundException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.searcher.SearchLinkerSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.Linkers_ForSearchId_Response;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Cache Key:   Integer searchId;
 * Cache Value: Linkers_ForSearchId_Response linkers_ForSearchId_Response;
 */
public class Cached_Linkers_ForSearchId implements CachedDataCommonIF {

	private static final Logger log = Logger.getLogger( Cached_Linkers_ForSearchId.class );

	private static final int CACHE_MAX_SIZE_FULL_SIZE = 400;
	private static final int CACHE_MAX_SIZE_SMALL = 10;

	private static final int CACHE_TIMEOUT_FULL_SIZE = 20; // in days
	private static final int CACHE_TIMEOUT_SMALL = 1; // in days


	private static final AtomicLong cacheGetCount = new AtomicLong();
	private static final AtomicLong cacheDBRetrievalCount = new AtomicLong();
	
	private static volatile int prevDayOfYear = -1;

	private static boolean debugLogLevelEnabled = false;
	
	/**
	 * Static singleton instance
	 */
	private static Cached_Linkers_ForSearchId _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized Cached_Linkers_ForSearchId getInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new Cached_Linkers_ForSearchId();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private Cached_Linkers_ForSearchId() {
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
	 * @return - retrieved from DB
	 * @throws Exception
	 */
	public Linkers_ForSearchId_Response getLinkers_ForSearchId_Response( 
			Integer searchId ) throws Exception {

		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}
		
		LoadingCache<Integer, Linkers_ForSearchId_Response> cache = cacheHolderInternal.getCache();
		
		if ( cache != null ) {
			Linkers_ForSearchId_Response linkers_ForSearchId_Response = cache.get( searchId );
			return linkers_ForSearchId_Response; // EARLY return
		}
		
		Linkers_ForSearchId_Response linkers_ForSearchId_Response = cacheHolderInternal.loadFromDB( searchId );
		return linkers_ForSearchId_Response;
	}


	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private Cached_Linkers_ForSearchId parentObject;
		
		private CacheHolderInternal( Cached_Linkers_ForSearchId parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private LoadingCache<Integer, Linkers_ForSearchId_Response> dbRecordsDataCache = null;
		
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
		private synchronized LoadingCache<Integer, Linkers_ForSearchId_Response> getCache(  ) throws Exception {
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
								new CacheLoader<Integer, Linkers_ForSearchId_Response>() {
									public Linkers_ForSearchId_Response load(Integer searchId) throws Exception {
										
										//   WARNING  cannot return null.  
										//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
										
										//  value is NOT in cache so get it and return it
										return loadFromDB(searchId);
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
		private Linkers_ForSearchId_Response loadFromDB( Integer searchId ) throws Exception {
			
			//   WARNING  cannot return null.  
			//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
			
			//  value is NOT in cache so get it and return it
			if ( debugLogLevelEnabled ) {
				cacheDBRetrievalCount.incrementAndGet();
			}
			List<Integer> linkerIdList = 
					SearchLinkerSearcher.getInstance()
					.getLinkerIdsForSearch( searchId );
			List<LinkerDTO> linkersForSearchIdList = new ArrayList<LinkerDTO>( linkerIdList.size() );
			for ( Integer linkerId : linkerIdList ) {
				LinkerDTO linkerDTO = null;
				try {
					linkerDTO = Cached_Linker.getInstance().getLinkerDTO( linkerId );
					
				} catch ( ExecutionException e ) {
					//  caught from LoadingCache when loadFromDB throws ProxlWebappDataNotFoundException
					if ( e.getCause() instanceof ProxlWebappDataNotFoundException ) {
						//  DB query returned null so return null here
					} else {
						throw e;
					}
					
				} catch ( ProxlWebappDataNotFoundException e ) {
					// thrown when not in DB
				}
				if ( linkerDTO == null ) {
					String msg = "linker id not found in DB after linker id returned from SearchLinkerSearcher.  "
							+ "linker id: " + linkerId;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
				linkersForSearchIdList.add( linkerDTO );
			}
			Linkers_ForSearchId_Response linkers_ForSearchId_Response =
					new Linkers_ForSearchId_Response();
			linkers_ForSearchId_Response.setLinkersForSearchIdList( linkersForSearchIdList );
			return linkers_ForSearchId_Response;
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
