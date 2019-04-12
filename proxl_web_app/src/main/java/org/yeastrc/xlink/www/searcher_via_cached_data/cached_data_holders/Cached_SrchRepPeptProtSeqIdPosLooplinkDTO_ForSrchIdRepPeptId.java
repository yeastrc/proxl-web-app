package org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosLooplinkDTO;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideProteinSequencePositionLooplinkSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams
 * SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result
 */
public class Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId implements CachedDataCommonIF {

	private static final Logger log = LoggerFactory.getLogger(  Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId.class );

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
	private static Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId getInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId() {
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
	 * @param srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams
	 * @return - retrieved from DB if not in cache
	 * @throws Exception
	 */
	public SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result getSrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result( 
			SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams ) throws Exception {

		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}
		
		LoadingCache<SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams, SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result> cache = cacheHolderInternal.getCache();
		
		if ( cache != null ) {
			SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result = 
					cache.get( srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams );
			return srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result; // EARLY return
		}
		
		SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result = cacheHolderInternal.loadFromDB( srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams );
		return srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result;
	}


	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId parentObject;
		
		private CacheHolderInternal( Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private LoadingCache<SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams, SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result> dbRecordsDataCache = null;
		
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
		private synchronized LoadingCache<SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams, SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result> getCache(  ) throws Exception {
			if ( ! cacheDataInitialized ) { 
				CachedDataSizeOptions cachedDataSizeOptions = 
						CachedDataCentralConfigStorageAndProcessing.getInstance().getCurrentSizeConfigValue();
				
				if ( cachedDataSizeOptions == CachedDataSizeOptions.FEW ) {
					//  No Cache, just mark initialized, dbRecordsDataCache already set to null;
					cacheDataInitialized = true;
					return dbRecordsDataCache;  //  EARLY RETURN
				}
				
				int cacheTimeout = CACHE_TIMEOUT_FULL_SIZE;
				cacheMaxSize = Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId.CACHE_MAX_SIZE_FULL_SIZE;
				if ( cachedDataSizeOptions == CachedDataSizeOptions.HALF ) {
					cacheMaxSize = cacheMaxSize / 2;
				} else if ( cachedDataSizeOptions == CachedDataSizeOptions.SMALL ) {
					cacheMaxSize = Cached_SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId.CACHE_MAX_SIZE_SMALL;
					cacheTimeout = CACHE_TIMEOUT_SMALL;
				}
				
				dbRecordsDataCache = CacheBuilder.newBuilder()
						.expireAfterAccess( cacheTimeout, TimeUnit.DAYS )
						.maximumSize( cacheMaxSize )
						.build(
								new CacheLoader<SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams, SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result>() {
									@Override
									public SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result load(SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams) throws Exception {
										
										//   WARNING  cannot return null.  
										//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
										
										//  value is NOT in cache so get it and return it
										return loadFromDB(srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams);
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
		 * @param srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams
		 * @return
		 * @throws Exception
		 */
		private SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result loadFromDB( SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams ) throws Exception {
			
			//   WARNING  cannot return null.  
			//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
			
			//  value is NOT in cache so get it and return it
			if ( debugLogLevelEnabled ) {
				cacheDBRetrievalCount.incrementAndGet();
			}
			List<SrchRepPeptProtSeqIdPosLooplinkDTO> srchRepPeptProtSeqIdPosLooplinkDTOList = 
					SearchReportedPeptideProteinSequencePositionLooplinkSearcher.getInstance()
					.getSrchRepPeptProtSeqIdPosLooplinkDTOList( 
							srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams.getSearchId(),
							srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_ReqParams.getReportedPeptideId() );
			SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result =
					new SrchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result();
			srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result.setSrchRepPeptProtSeqIdPosLooplinkDTOList( srchRepPeptProtSeqIdPosLooplinkDTOList );
			return srchRepPeptProtSeqIdPosLooplinkDTO_ForSrchIdRepPeptId_Result;
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
