package org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.dao.ProjectDAO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataNotFoundException;
import org.yeastrc.xlink.www.objects.ProjectTblSubPartsForProjectLists;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Cache Key:   Integer projectId;
 * Cache Value: ProjectTblSubPartsForProjectLists projectTblSubPartsForProjectLists;
 * 
 * uses ProxlWebappDataNotFoundException internally since loadFromDB call to searcher may return null
 */
public class Cached_ProjectTblSubPartsForProjectLists implements CachedDataCommonIF {

	private static final Logger log = Logger.getLogger( Cached_ProjectTblSubPartsForProjectLists.class );

	private static final int CACHE_MAX_SIZE_FULL_SIZE = 200;
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
	private static Cached_ProjectTblSubPartsForProjectLists _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized Cached_ProjectTblSubPartsForProjectLists getInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new Cached_ProjectTblSubPartsForProjectLists();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private Cached_ProjectTblSubPartsForProjectLists() {
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
	 * Remove specific projectId from cache since data changed
	 * @param projectId
	 */
	public void invalidateProjectId( int projectId ) {
		cacheHolderInternal.invalidateKey( projectId );
	}

	/**
	 * @param projectId
	 * @return - retrieved from DB or null if not found
	 * @throws Exception
	 */
	public ProjectTblSubPartsForProjectLists getProjectTblSubPartsForProjectLists( 
			Integer projectId ) throws Exception {

		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}

		try {
			LoadingCache<Integer, ProjectTblSubPartsForProjectLists> cache = cacheHolderInternal.getCache();

			if ( cache != null ) {
				ProjectTblSubPartsForProjectLists projectTblSubPartsForProjectLists = cache.get( projectId );
				return projectTblSubPartsForProjectLists; // EARLY return
			}

			ProjectTblSubPartsForProjectLists projectTblSubPartsForProjectLists = cacheHolderInternal.loadFromDB( projectId );
			return projectTblSubPartsForProjectLists;
		} catch ( ProxlWebappDataNotFoundException e ) {
			//  DB query returned null so return null here
			return null;
		}
	}


	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private Cached_ProjectTblSubPartsForProjectLists parentObject;
		
		private CacheHolderInternal( Cached_ProjectTblSubPartsForProjectLists parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private LoadingCache<Integer, ProjectTblSubPartsForProjectLists> dbRecordsDataCache = null;
		
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
		private synchronized LoadingCache<Integer, ProjectTblSubPartsForProjectLists> getCache(  ) throws Exception {
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
						.expireAfterAccess( cacheTimeout, TimeUnit.DAYS ) // always in cache
						.maximumSize( cacheMaxSize )
						.build(
								new CacheLoader<Integer, ProjectTblSubPartsForProjectLists>() {
									public ProjectTblSubPartsForProjectLists load(Integer projectId) throws Exception {
										
										//   WARNING  cannot return null.  
										//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
										
										//  value is NOT in cache so get it and return it
										return loadFromDB(projectId);
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

		private synchronized void invalidateKey( Integer key ) {
			dbRecordsDataCache.invalidate( key );
		}

		/**
		 * @param projectId
		 * @return
		 * @throws Exception
		 */
		private ProjectTblSubPartsForProjectLists loadFromDB( Integer projectId ) throws Exception {
			
			//   WARNING  cannot return null.  
			//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
			
			//  value is NOT in cache so get it and return it
			if ( debugLogLevelEnabled ) {
				cacheDBRetrievalCount.incrementAndGet();
			}
			ProjectTblSubPartsForProjectLists projectTblSubPartsForProjectLists =
					ProjectDAO.getInstance().getProjectTblSubPartsForProjectListsForProjectId( projectId );
			if ( projectTblSubPartsForProjectLists == null ) {
				// Throw this exception since cannot return null to Cache
				throw new ProxlWebappDataNotFoundException();
			}
			return projectTblSubPartsForProjectLists;
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
