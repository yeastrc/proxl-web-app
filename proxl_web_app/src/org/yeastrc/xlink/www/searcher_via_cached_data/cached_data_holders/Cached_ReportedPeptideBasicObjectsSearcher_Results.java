package org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.searcher.ReportedPeptideBasicObjectsSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.ReportedPeptideBasicObjectsSearcherRequestParameters;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.ReportedPeptideBasicObjectsSearcherResult;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * ReportedPeptideBasicObjectsSearcherRequestParameters
 * ReportedPeptideBasicObjectsSearcherResult
 */
public class Cached_ReportedPeptideBasicObjectsSearcher_Results implements CachedDataCommonIF {

	private static final Logger log = Logger.getLogger( Cached_ReportedPeptideBasicObjectsSearcher_Results.class );

	private static final int CACHE_MAX_SIZE_FULL_SIZE = 10000;
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
	private static Cached_ReportedPeptideBasicObjectsSearcher_Results _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized Cached_ReportedPeptideBasicObjectsSearcher_Results getInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new Cached_ReportedPeptideBasicObjectsSearcher_Results();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private Cached_ReportedPeptideBasicObjectsSearcher_Results() {
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
	 * @param reportedPeptideBasicObjectsSearcherRequestParameters
	 * @return - retrieved from DB if not in cache
	 * @throws Exception
	 */
	public ReportedPeptideBasicObjectsSearcherResult getReportedPeptideBasicObjectsSearcherResult( 
			ReportedPeptideBasicObjectsSearcherRequestParameters reportedPeptideBasicObjectsSearcherRequestParameters ) throws Exception {

		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}
		
		LoadingCache<ReportedPeptideBasicObjectsSearcherRequestParameters, ReportedPeptideBasicObjectsSearcherResult> cache = cacheHolderInternal.getCache();
		
		if ( cache != null ) {
			ReportedPeptideBasicObjectsSearcherResult reportedPeptideBasicObjectsSearcherResult = cache.get( reportedPeptideBasicObjectsSearcherRequestParameters );
			return reportedPeptideBasicObjectsSearcherResult; // EARLY return
		}
		
		ReportedPeptideBasicObjectsSearcherResult reportedPeptideBasicObjectsSearcherResult = cacheHolderInternal.loadFromDB( reportedPeptideBasicObjectsSearcherRequestParameters );
		return reportedPeptideBasicObjectsSearcherResult;
	}


	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private Cached_ReportedPeptideBasicObjectsSearcher_Results parentObject;
		
		private CacheHolderInternal( Cached_ReportedPeptideBasicObjectsSearcher_Results parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private LoadingCache<ReportedPeptideBasicObjectsSearcherRequestParameters, ReportedPeptideBasicObjectsSearcherResult> dbRecordsDataCache = null;
		
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
		private synchronized LoadingCache<ReportedPeptideBasicObjectsSearcherRequestParameters, ReportedPeptideBasicObjectsSearcherResult> getCache(  ) throws Exception {
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
								new CacheLoader<ReportedPeptideBasicObjectsSearcherRequestParameters, ReportedPeptideBasicObjectsSearcherResult>() {
									public ReportedPeptideBasicObjectsSearcherResult load(ReportedPeptideBasicObjectsSearcherRequestParameters reportedPeptideBasicObjectsSearcherRequestParameters) throws Exception {
										
										//   WARNING  cannot return null.  
										//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
										
										//  value is NOT in cache so get it and return it
										return loadFromDB(reportedPeptideBasicObjectsSearcherRequestParameters);
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
		 * @param reportedPeptideBasicObjectsSearcherRequestParameters
		 * @return
		 * @throws Exception
		 */
		private ReportedPeptideBasicObjectsSearcherResult loadFromDB( ReportedPeptideBasicObjectsSearcherRequestParameters reportedPeptideBasicObjectsSearcherRequestParameters ) throws Exception {
			
			//   WARNING  cannot return null.  
			//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
			
			//  value is NOT in cache so get it and return it
			if ( debugLogLevelEnabled ) {
				cacheDBRetrievalCount.incrementAndGet();
			}
			ReportedPeptideBasicObjectsSearcherResult reportedPeptideBasicObjectsSearcherResult =
					ReportedPeptideBasicObjectsSearcher.getInstance()
					.searchOnSearchIdPsmCutoffPeptideCutoff( reportedPeptideBasicObjectsSearcherRequestParameters );
			return reportedPeptideBasicObjectsSearcherResult;
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
