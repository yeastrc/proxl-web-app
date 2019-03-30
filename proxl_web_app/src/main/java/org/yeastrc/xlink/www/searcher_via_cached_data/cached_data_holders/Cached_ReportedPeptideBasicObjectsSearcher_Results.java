package org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.searcher.ReportedPeptideBasicObjectsSearcher;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.ReportedPeptideBasicObjectsSearcherRequestParameters;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.ReportedPeptideBasicObjectsSearcherResult;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * Calls to get List<ReportedPeptideBasicObjectsSearcherResultEntry> entryList:
 *	ReportedPeptideBasicObjectsSearcher.getInstance()
 *   .searchOnSearchIdPsmCutoffPeptideCutoff( reportedPeptideBasicObjectsSearcherRequestParameters );
 *
 * Request and Response classes
 * ReportedPeptideBasicObjectsSearcherRequestParameters
 * ReportedPeptideBasicObjectsSearcherResult
 * 
 * !!!!  Has 2 caches, one for default cutoffs and one for non-default cutoffs  !!!!
 */
public class Cached_ReportedPeptideBasicObjectsSearcher_Results implements CachedDataCommonIF {

	private static final Logger log = LoggerFactory.getLogger(  Cached_ReportedPeptideBasicObjectsSearcher_Results.class );

	private static final int CACHE_MAX_SIZE_FULL_SIZE_DEFAULT_CUTOFFS = 10000;
	private static final int CACHE_MAX_SIZE_FULL_SIZE__NOT__DEFAULT_CUTOFFS = 600;
	private static final int CACHE_MAX_SIZE_SMALL = 10;

	private static final int CACHE_TIMEOUT_FULL_SIZE_DEFAULT_CUTOFFS = 20; // in days
	private static final int CACHE_TIMEOUT_FULL_SIZE__NOT__DEFAULT_CUTOFFS = 1; // in days
	private static final int CACHE_TIMEOUT_SMALL = 1; // in days

	private static final AtomicLong cacheGetCount = new AtomicLong();
	private static final AtomicLong cacheDBRetrievalCount = new AtomicLong();
	private static volatile int prevDayOfYear = -1;
	private static boolean debugLogLevelEnabled = false;
	
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

	//   Primary instance Method
	/**
	 * Primary Instance Method to get data from the cache
	 * 
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
		int searchId = reportedPeptideBasicObjectsSearcherRequestParameters.getSearchId();
		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = reportedPeptideBasicObjectsSearcherRequestParameters.getSearcherCutoffValuesSearchLevel();
		//  Determine count at Default Cutoff
		DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult defaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult =
				DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.getInstance()
				.defaultCutoffsExactlyMatchAnnTypeDataToSearchData( searchId, searcherCutoffValuesSearchLevel );
		boolean defaultCutoffsExactlyMatchAnnTypeDataToSearchData =
				defaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult.isDefaultCutoffsExactlyMatchAnnTypeDataToSearchData();
		LoadingCache<ReportedPeptideBasicObjectsSearcherRequestParameters, ReportedPeptideBasicObjectsSearcherResult> cache = 
				cacheHolderInternal.getCache( defaultCutoffsExactlyMatchAnnTypeDataToSearchData );
		if ( cache != null ) {
			ReportedPeptideBasicObjectsSearcherResult reportedPeptideBasicObjectsSearcherResult = cache.get( reportedPeptideBasicObjectsSearcherRequestParameters );
			return reportedPeptideBasicObjectsSearcherResult; // EARLY return
		}
		ReportedPeptideBasicObjectsSearcherResult reportedPeptideBasicObjectsSearcherResult = cacheHolderInternal.loadFromDB( reportedPeptideBasicObjectsSearcherRequestParameters );
		return reportedPeptideBasicObjectsSearcherResult;
	}


	/**
	 * Static singleton instance
	 */
	private static Cached_ReportedPeptideBasicObjectsSearcher_Results _instance = null; //  Delay creating until first getInstance() call

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
	
	//  Class Properties
	
	private CacheHolderInternal cacheHolderInternal;

	//  Other Instance Methods
	
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
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private Cached_ReportedPeptideBasicObjectsSearcher_Results parentObject;
		
		private CacheHolderInternal( Cached_ReportedPeptideBasicObjectsSearcher_Results parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		 // cached data, left null if no caching
		private LoadingCache<ReportedPeptideBasicObjectsSearcherRequestParameters, ReportedPeptideBasicObjectsSearcherResult> 
		dbRecordsDataCacheDefaultCutoffs = null;
		private LoadingCache<ReportedPeptideBasicObjectsSearcherRequestParameters, ReportedPeptideBasicObjectsSearcherResult> 
		dbRecordsDataCache_NOT_DefaultCutoffs = null;
		
		private int cacheMaxSizeDefaultCutoffs;
		private int cacheMaxSize_NOT_DefaultCutoffs;

		/**
		 * @return
		 * @throws Exception
		 */
		public synchronized CacheCurrentSizeMaxSizeResult getCurrentCacheSizeAndMax() throws Exception {
			CacheCurrentSizeMaxSizeResult result = new CacheCurrentSizeMaxSizeResult();
			if ( dbRecordsDataCacheDefaultCutoffs != null ) {
				result.setCurrentSize( dbRecordsDataCacheDefaultCutoffs.size() );
				result.setMaxSize( cacheMaxSizeDefaultCutoffs );
			}
			return result;
		}
		
		/**
		 * @throws Exception
		 */
		@SuppressWarnings("static-access")
		private synchronized LoadingCache<ReportedPeptideBasicObjectsSearcherRequestParameters, ReportedPeptideBasicObjectsSearcherResult> 
		getCache( boolean defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) throws Exception {
			if ( ! cacheDataInitialized ) { 
				//  No caches so create caches
				CachedDataSizeOptions cachedDataSizeOptions = 
						CachedDataCentralConfigStorageAndProcessing.getInstance().getCurrentSizeConfigValue();
				
				if ( cachedDataSizeOptions == CachedDataSizeOptions.FEW ) {
					//  No Cache, mark initialized, set dbRecordsDataCache to null
					cacheDataInitialized = true;
					dbRecordsDataCacheDefaultCutoffs = null;
					dbRecordsDataCache_NOT_DefaultCutoffs = null;
					return null;  //  EARLY RETURN
				}

				{	//  Create cache for Default cutoffs
					int cacheTimeout = CACHE_TIMEOUT_FULL_SIZE_DEFAULT_CUTOFFS;
					cacheMaxSizeDefaultCutoffs = parentObject.CACHE_MAX_SIZE_FULL_SIZE_DEFAULT_CUTOFFS;
					if ( cachedDataSizeOptions == CachedDataSizeOptions.HALF ) {
						cacheMaxSizeDefaultCutoffs = cacheMaxSizeDefaultCutoffs / 2;
					} else if ( cachedDataSizeOptions == CachedDataSizeOptions.SMALL ) {
						cacheMaxSizeDefaultCutoffs = parentObject.CACHE_MAX_SIZE_SMALL;
						cacheTimeout = CACHE_TIMEOUT_SMALL;
					}

					dbRecordsDataCacheDefaultCutoffs = CacheBuilder.newBuilder()
							.expireAfterAccess( cacheTimeout, TimeUnit.DAYS )
							.maximumSize( cacheMaxSizeDefaultCutoffs )
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
				}
				{	//  Create cache for NOT Default cutoffs
					int cacheTimeout = CACHE_TIMEOUT_FULL_SIZE__NOT__DEFAULT_CUTOFFS;
					cacheMaxSize_NOT_DefaultCutoffs = parentObject.CACHE_MAX_SIZE_FULL_SIZE__NOT__DEFAULT_CUTOFFS;
					if ( cachedDataSizeOptions == CachedDataSizeOptions.HALF ) {
						cacheMaxSize_NOT_DefaultCutoffs = cacheMaxSize_NOT_DefaultCutoffs / 2;
					} else if ( cachedDataSizeOptions == CachedDataSizeOptions.SMALL ) {
						cacheMaxSize_NOT_DefaultCutoffs = parentObject.CACHE_MAX_SIZE_SMALL;
						cacheTimeout = CACHE_TIMEOUT_SMALL;
					}

					dbRecordsDataCache_NOT_DefaultCutoffs = CacheBuilder.newBuilder()
							.expireAfterAccess( cacheTimeout, TimeUnit.DAYS )
							.maximumSize( cacheMaxSize_NOT_DefaultCutoffs )
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
				}
				cacheDataInitialized = true;
			}
			if ( defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) {
				return dbRecordsDataCacheDefaultCutoffs;
			} else {
				return dbRecordsDataCache_NOT_DefaultCutoffs;
			}
		}
		

		private synchronized void invalidate() {
			dbRecordsDataCacheDefaultCutoffs = null;
			dbRecordsDataCache_NOT_DefaultCutoffs = null;
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
