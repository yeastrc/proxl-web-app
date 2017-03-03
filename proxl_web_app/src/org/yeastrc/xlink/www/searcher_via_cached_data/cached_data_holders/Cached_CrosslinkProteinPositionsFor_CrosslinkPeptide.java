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
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.CrosslinkProteinPositionsFor_CrosslinkPeptide_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.CrosslinkProteinPositionsFor_CrosslinkPeptide_Result;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Cache Key:   CrosslinkProteinPositionsFor_CrosslinkPeptide_Request crosslinkProteinPositionsFor_CrosslinkPeptide_Request;
 * Cache Value: CrosslinkProteinPositionsFor_CrosslinkPeptide_Result crosslinkProteinPositionsFor_CrosslinkPeptide_Result;
 */
public class Cached_CrosslinkProteinPositionsFor_CrosslinkPeptide implements CachedDataCommonIF {

	private static final Logger log = Logger.getLogger( Cached_CrosslinkProteinPositionsFor_CrosslinkPeptide.class );

	private static final int CACHE_MAX_SIZE_FULL_SIZE = 40000;
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
	private static Cached_CrosslinkProteinPositionsFor_CrosslinkPeptide _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized Cached_CrosslinkProteinPositionsFor_CrosslinkPeptide getInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new Cached_CrosslinkProteinPositionsFor_CrosslinkPeptide();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private Cached_CrosslinkProteinPositionsFor_CrosslinkPeptide() {
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
	 * @param crosslinkProteinPositionsFor_CrosslinkPeptide_Request
	 * @return - retrieved from DB
	 * @throws Exception
	 */
	public CrosslinkProteinPositionsFor_CrosslinkPeptide_Result getCrosslinkProteinPositionsFor_CrosslinkPeptide_Result( 
			CrosslinkProteinPositionsFor_CrosslinkPeptide_Request crosslinkProteinPositionsFor_CrosslinkPeptide_Request ) throws Exception {

		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}
		
		LoadingCache<CrosslinkProteinPositionsFor_CrosslinkPeptide_Request, CrosslinkProteinPositionsFor_CrosslinkPeptide_Result> cache = cacheHolderInternal.getCache();
		
		if ( cache != null ) {
			CrosslinkProteinPositionsFor_CrosslinkPeptide_Result crosslinkProteinPositionsFor_CrosslinkPeptide_Result = cache.get( crosslinkProteinPositionsFor_CrosslinkPeptide_Request );
			return crosslinkProteinPositionsFor_CrosslinkPeptide_Result; // EARLY return
		}
		
		CrosslinkProteinPositionsFor_CrosslinkPeptide_Result crosslinkProteinPositionsFor_CrosslinkPeptide_Result = cacheHolderInternal.loadFromDB( crosslinkProteinPositionsFor_CrosslinkPeptide_Request );
		return crosslinkProteinPositionsFor_CrosslinkPeptide_Result;
	}


	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private Cached_CrosslinkProteinPositionsFor_CrosslinkPeptide parentObject;
		
		private CacheHolderInternal( Cached_CrosslinkProteinPositionsFor_CrosslinkPeptide parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private LoadingCache<CrosslinkProteinPositionsFor_CrosslinkPeptide_Request, CrosslinkProteinPositionsFor_CrosslinkPeptide_Result> dbRecordsDataCache = null;
		
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
		private synchronized LoadingCache<CrosslinkProteinPositionsFor_CrosslinkPeptide_Request, CrosslinkProteinPositionsFor_CrosslinkPeptide_Result> getCache(  ) throws Exception {
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
								new CacheLoader<CrosslinkProteinPositionsFor_CrosslinkPeptide_Request, CrosslinkProteinPositionsFor_CrosslinkPeptide_Result>() {
									public CrosslinkProteinPositionsFor_CrosslinkPeptide_Result load(CrosslinkProteinPositionsFor_CrosslinkPeptide_Request crosslinkProteinPositionsFor_CrosslinkPeptide_Request) throws Exception {
										//  value is NOT in cache so get it and return it
										return loadFromDB(crosslinkProteinPositionsFor_CrosslinkPeptide_Request);
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
		 * @param crosslinkProteinPositionsFor_CrosslinkPeptide_Request
		 * @return
		 * @throws Exception
		 */
		private CrosslinkProteinPositionsFor_CrosslinkPeptide_Result loadFromDB( CrosslinkProteinPositionsFor_CrosslinkPeptide_Request crosslinkProteinPositionsFor_CrosslinkPeptide_Request ) throws Exception {
			//  value is NOT in cache so get it and return it
			if ( debugLogLevelEnabled ) {
				cacheDBRetrievalCount.incrementAndGet();
			}
			List<CrosslinkProteinPositionsFor_CrosslinkPeptide_Result_Entry> proteinPositions = 
					SearchProteinSearcher.getInstance()
					.getCrosslinkProteinPositions( // int searchId, int reportedPeptideId, int peptideId, int position
							crosslinkProteinPositionsFor_CrosslinkPeptide_Request.getSearchId(),
							crosslinkProteinPositionsFor_CrosslinkPeptide_Request.getReportedPeptideId(),
							crosslinkProteinPositionsFor_CrosslinkPeptide_Request.getPeptideId(),
							crosslinkProteinPositionsFor_CrosslinkPeptide_Request.getPosition() );
			CrosslinkProteinPositionsFor_CrosslinkPeptide_Result crosslinkProteinPositionsFor_CrosslinkPeptide_Result =
					new CrosslinkProteinPositionsFor_CrosslinkPeptide_Result();
			crosslinkProteinPositionsFor_CrosslinkPeptide_Result.setEntryList( proteinPositions );
			return crosslinkProteinPositionsFor_CrosslinkPeptide_Result;
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
