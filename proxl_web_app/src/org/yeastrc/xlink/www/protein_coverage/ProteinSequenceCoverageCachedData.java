package org.yeastrc.xlink.www.protein_coverage;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 *  Cache protein coverages
 * Cached key:  Internal class ProteinSequenceCoverageCacheKey
 * Cached value:  ProteinSequenceCoverage
 * 
 * No CacheLoader, Entries are added by the caller
 */
public class ProteinSequenceCoverageCachedData implements CachedDataCommonIF {

	private static final Logger log = Logger.getLogger( ProteinSequenceCoverageCachedData.class );

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
	private static ProteinSequenceCoverageCachedData _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized ProteinSequenceCoverageCachedData getInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new ProteinSequenceCoverageCachedData();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private ProteinSequenceCoverageCachedData() {
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
	 * @param proteinSequenceId
	 * @param searcherCutoffValuesSearchLevel
	 * @return - null if not in cache
	 * @throws Exception
	 */
	public ProteinSequenceCoverage getProteinSequenceCoverage( 	
			int proteinSequenceId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {

		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}
		
		Cache<ProteinSequenceCoverageCacheKey, ProteinSequenceCoverage> cache = cacheHolderInternal.getCache();
		
		if ( cache == null ) {
			return null; // EARLY return
		}
		
		ProteinSequenceCoverageCacheKey proteinSequenceCoverageCacheKey = new ProteinSequenceCoverageCacheKey();		
		proteinSequenceCoverageCacheKey.proteinSequenceId = proteinSequenceId;
		proteinSequenceCoverageCacheKey.searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel;

		ProteinSequenceCoverage proteinSequenceCoverage = cache.getIfPresent( proteinSequenceCoverageCacheKey );
		return proteinSequenceCoverage;
	}


	/**
	 * @param proteinSequenceId
	 * @param searcherCutoffValuesSearchLevel
	 * @return - null if not in cache
	 * @throws Exception
	 */
	public void addProteinSequenceCoverage( 	
			int proteinSequenceId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			ProteinSequenceCoverage proteinSequenceCoverage
			 ) throws Exception {

		Cache<ProteinSequenceCoverageCacheKey, ProteinSequenceCoverage> cache = cacheHolderInternal.getCache();
		
		if ( cache == null ) {
			return; // EARLY return
		}

		ProteinSequenceCoverageCacheKey proteinSequenceCoverageCacheKey = new ProteinSequenceCoverageCacheKey();		
		proteinSequenceCoverageCacheKey.proteinSequenceId = proteinSequenceId;
		proteinSequenceCoverageCacheKey.searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel;

		cache.put( proteinSequenceCoverageCacheKey, proteinSequenceCoverage );
	
	}

	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private ProteinSequenceCoverageCachedData parentObject;
		
		private CacheHolderInternal( ProteinSequenceCoverageCachedData parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private Cache<ProteinSequenceCoverageCacheKey, ProteinSequenceCoverage> dbRecordsDataCache = null;
		
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
		private synchronized Cache<ProteinSequenceCoverageCacheKey, ProteinSequenceCoverage> getCache(  ) throws Exception {
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
						.build(); // no CacheLoader
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
	

	/**
	 * 
	 * equals and hashCode required for Cache
	 */
	private static class ProteinSequenceCoverageCacheKey {
		
		int proteinSequenceId;
		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + proteinSequenceId;
			result = prime * result
					+ ((searcherCutoffValuesSearchLevel == null) ? 0 : searcherCutoffValuesSearchLevel.hashCode());
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
			ProteinSequenceCoverageCacheKey other = (ProteinSequenceCoverageCacheKey) obj;
			if (proteinSequenceId != other.proteinSequenceId)
				return false;
			if (searcherCutoffValuesSearchLevel == null) {
				if (other.searcherCutoffValuesSearchLevel != null)
					return false;
			} else if (!searcherCutoffValuesSearchLevel.equals(other.searcherCutoffValuesSearchLevel))
				return false;
			return true;
		}
		
	}

}
