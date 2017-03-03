package org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.LooplinkProteinPositionsFor_LooplinkPeptide_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.LooplinkProteinPositionsFor_LooplinkPeptide_Result;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * LooplinkProteinPositionsFor_LooplinkPeptide_Request 
 * LooplinkProteinPositionsFor_LooplinkPeptide_Result
 *
 */
public class Cached_LooplinkProteinPositionsFor_LooplinkPeptide implements CachedDataCommonIF {

	private static final Logger log = Logger.getLogger( Cached_LooplinkProteinPositionsFor_LooplinkPeptide.class );

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
	private static Cached_LooplinkProteinPositionsFor_LooplinkPeptide _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized Cached_LooplinkProteinPositionsFor_LooplinkPeptide getInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new Cached_LooplinkProteinPositionsFor_LooplinkPeptide();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private Cached_LooplinkProteinPositionsFor_LooplinkPeptide() {
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
	 * @param looplinkProteinPositionsFor_LooplinkPeptide_Request
	 * @return - retrieved from DB
	 * @throws Exception
	 */
	public LooplinkProteinPositionsFor_LooplinkPeptide_Result getLooplinkProteinPositionsFor_LooplinkPeptide_Result( 
			LooplinkProteinPositionsFor_LooplinkPeptide_Request looplinkProteinPositionsFor_LooplinkPeptide_Request ) throws Exception {

		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}

		LoadingCache<LooplinkProteinPositionsFor_LooplinkPeptide_Request, LooplinkProteinPositionsFor_LooplinkPeptide_Result> cache = cacheHolderInternal.getCache();
		
		if ( cache != null ) {
			LooplinkProteinPositionsFor_LooplinkPeptide_Result looplinkProteinPositionsFor_LooplinkPeptide_Result = cache.get( looplinkProteinPositionsFor_LooplinkPeptide_Request );
			return looplinkProteinPositionsFor_LooplinkPeptide_Result; // EARLY return
		}
		
		LooplinkProteinPositionsFor_LooplinkPeptide_Result looplinkProteinPositionsFor_LooplinkPeptide_Result = cacheHolderInternal.loadFromDB( looplinkProteinPositionsFor_LooplinkPeptide_Request );
		return looplinkProteinPositionsFor_LooplinkPeptide_Result;
	}


	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private Cached_LooplinkProteinPositionsFor_LooplinkPeptide parentObject;
		
		private CacheHolderInternal( Cached_LooplinkProteinPositionsFor_LooplinkPeptide parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private LoadingCache<LooplinkProteinPositionsFor_LooplinkPeptide_Request, LooplinkProteinPositionsFor_LooplinkPeptide_Result> dbRecordsDataCache = null;
		
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
		private synchronized LoadingCache<LooplinkProteinPositionsFor_LooplinkPeptide_Request, LooplinkProteinPositionsFor_LooplinkPeptide_Result> getCache(  ) throws Exception {
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
								new CacheLoader<LooplinkProteinPositionsFor_LooplinkPeptide_Request, LooplinkProteinPositionsFor_LooplinkPeptide_Result>() {
									public LooplinkProteinPositionsFor_LooplinkPeptide_Result load(LooplinkProteinPositionsFor_LooplinkPeptide_Request looplinkProteinPositionsFor_LooplinkPeptide_Request) throws Exception {
										
										//   WARNING  cannot return null.  
										//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
										
										//  value is NOT in cache so get it and return it
										return loadFromDB(looplinkProteinPositionsFor_LooplinkPeptide_Request);
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
		 * @param looplinkProteinPositionsFor_LooplinkPeptide_Request
		 * @return
		 * @throws Exception
		 */
		private LooplinkProteinPositionsFor_LooplinkPeptide_Result loadFromDB( LooplinkProteinPositionsFor_LooplinkPeptide_Request looplinkProteinPositionsFor_LooplinkPeptide_Request ) throws Exception {
			
			//   WARNING  cannot return null.  
			//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
			
			//  value is NOT in cache so get it and return it
			if ( debugLogLevelEnabled ) {
				cacheDBRetrievalCount.incrementAndGet();
			}
			List<LooplinkProteinPositionsFor_LooplinkPeptide_Result_Entry> proteinPositions = 
					SearchProteinSearcher.getInstance()
					.getLooplinkProteinPositions( // int searchId, int reportedPeptideId, int peptideId, int position1, int position2
							looplinkProteinPositionsFor_LooplinkPeptide_Request.getSearchId(),
							looplinkProteinPositionsFor_LooplinkPeptide_Request.getReportedPeptideId(),
							looplinkProteinPositionsFor_LooplinkPeptide_Request.getPeptideId(),
							looplinkProteinPositionsFor_LooplinkPeptide_Request.getPosition_1(),
							looplinkProteinPositionsFor_LooplinkPeptide_Request.getPosition_2() );
			LooplinkProteinPositionsFor_LooplinkPeptide_Result looplinkProteinPositionsFor_LooplinkPeptide_Result =
					new LooplinkProteinPositionsFor_LooplinkPeptide_Result();
			looplinkProteinPositionsFor_LooplinkPeptide_Result.setEntryList( proteinPositions );
			return looplinkProteinPositionsFor_LooplinkPeptide_Result;
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
