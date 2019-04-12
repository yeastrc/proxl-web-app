package org.yeastrc.xlink.www.annotation_utils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.searcher.AnnotationTypeForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Central place to get Annotation Type data
 * 
 * Singleton instance
 * 
 * Cache Key:  
 * Cache Value: 
 */
public class GetAnnotationTypeData implements CachedDataCommonIF {
	
	private static final Logger log = LoggerFactory.getLogger( GetAnnotationTypeData.class);
	
	private static final int CACHE_MAX_SIZE_FULL_SIZE = 8000;
	private static final int CACHE_MAX_SIZE_SMALL_FEW = 500;

	private static final int CACHE_TIMEOUT_FULL_SIZE = 20; // in days
	private static final int CACHE_TIMEOUT_SMALL = 20; // in days


	private static final AtomicLong cacheGetCount = new AtomicLong();
	private static final AtomicLong cacheDBRetrievalCount = new AtomicLong();
	
	private static volatile int prevDayOfYear = -1;

	private static boolean debugLogLevelEnabled = false;
	
	/**
	 * Static singleton instance
	 */
	private static GetAnnotationTypeData _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized GetAnnotationTypeData getInstance() throws Exception {
		if ( _instance == null ) {
			_instance = new GetAnnotationTypeData();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private GetAnnotationTypeData() {
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
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer, AnnotationTypeDTO>> getAll_Psm_Filterable_ForSearchIds( Collection<Integer> searchIds ) throws Exception {
		return getAnnTypeForSearchIdsFiltDescPsmPeptide( searchIds, FilterableDescriptiveAnnotationType.FILTERABLE, PsmPeptideAnnotationType.PSM );
	}
	/**
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer, AnnotationTypeDTO>> getAll_Psm_Descriptive_ForSearchIds( Collection<Integer> searchIds ) throws Exception {
		return getAnnTypeForSearchIdsFiltDescPsmPeptide( searchIds, FilterableDescriptiveAnnotationType.DESCRIPTIVE, PsmPeptideAnnotationType.PSM );
	}
	/**
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer, AnnotationTypeDTO>> getAll_Peptide_Filterable_ForSearchIds( Collection<Integer> searchIds ) throws Exception {
		return getAnnTypeForSearchIdsFiltDescPsmPeptide( searchIds, FilterableDescriptiveAnnotationType.FILTERABLE, PsmPeptideAnnotationType.PEPTIDE );
	}
	/**
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer, AnnotationTypeDTO>> getAll_Peptide_Descriptive_ForSearchIds( Collection<Integer> searchIds ) throws Exception {
		return getAnnTypeForSearchIdsFiltDescPsmPeptide( searchIds, FilterableDescriptiveAnnotationType.DESCRIPTIVE, PsmPeptideAnnotationType.PEPTIDE );
	}
	/**
	 * @param searchIds
	 * @param filterableDescriptiveAnnotationType
	 * @param psmPeptideAnnotationType
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Map<Integer, AnnotationTypeDTO>> getAnnTypeForSearchIdsFiltDescPsmPeptide( 
			Collection<Integer> searchIds,
			FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType,
			PsmPeptideAnnotationType psmPeptideAnnotationType ) throws Exception {
		
		LocalCacheKey localCacheKey = new LocalCacheKey();
		localCacheKey.searchIds = searchIds;
		localCacheKey.filterableDescriptiveAnnotationType = filterableDescriptiveAnnotationType;
		localCacheKey.psmPeptideAnnotationType = psmPeptideAnnotationType;

		LoadingCache<LocalCacheKey, LocalCacheValue> cache = cacheHolderInternal.getCache();
		
		LocalCacheValue localCacheValue = cache.get( localCacheKey );
		if ( localCacheValue == null ) {
			throw new ProxlWebappInternalErrorException("Should be getting annotation data via cache internal load");
		}
		Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeData = localCacheValue.annotationTypeData;
		return annotationTypeData;
	}
	
	/**
	 * classes for holding data in the cache
	 * 
	 * key to the cache
	 *
	 */
	private static class LocalCacheKey {
		Collection<Integer> searchIds;
		FilterableDescriptiveAnnotationType filterableDescriptiveAnnotationType;
		PsmPeptideAnnotationType psmPeptideAnnotationType;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((filterableDescriptiveAnnotationType == null) ? 0
							: filterableDescriptiveAnnotationType.hashCode());
			result = prime
					* result
					+ ((psmPeptideAnnotationType == null) ? 0
							: psmPeptideAnnotationType.hashCode());
			result = prime * result
					+ ((searchIds == null) ? 0 : searchIds.hashCode());
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
			if (filterableDescriptiveAnnotationType != other.filterableDescriptiveAnnotationType)
				return false;
			if (psmPeptideAnnotationType != other.psmPeptideAnnotationType)
				return false;
			if (searchIds == null) {
				if (other.searchIds != null)
					return false;
			} else if (!searchIds.equals(other.searchIds))
				return false;
			return true;
		}
	}
	
	/**
	 * classes for holding data in the cache
	 * 
	 * value in the cache
	 */
	private static class LocalCacheValue {
		Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeData;
	}

	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private GetAnnotationTypeData parentObject;
		
		private CacheHolderInternal( GetAnnotationTypeData parentObject ) {
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
				cacheMaxSize = GetAnnotationTypeData.CACHE_MAX_SIZE_FULL_SIZE;
				if ( cachedDataSizeOptions == CachedDataSizeOptions.HALF ) {
					cacheMaxSize = cacheMaxSize / 2;
				} else if ( cachedDataSizeOptions == CachedDataSizeOptions.SMALL
						|| cachedDataSizeOptions == CachedDataSizeOptions.FEW ) {
					cacheMaxSize = GetAnnotationTypeData.CACHE_MAX_SIZE_SMALL_FEW;
					cacheTimeout = CACHE_TIMEOUT_SMALL;
				}
				
				dbRecordsDataCache = CacheBuilder.newBuilder()
						.expireAfterAccess( cacheTimeout, TimeUnit.DAYS )
						.maximumSize( cacheMaxSize )
						.build(
								new CacheLoader<LocalCacheKey, LocalCacheValue>() {
									@Override
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
			Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeData =
					AnnotationTypeForSearchIdsSearcher.getInstance()
					.getAllForSearchIds_FilterableDescriptive_PsmPeptide(
							localCacheKey.searchIds, 
							localCacheKey.filterableDescriptiveAnnotationType, 
							localCacheKey.psmPeptideAnnotationType );
			LocalCacheValue localCacheValue = new LocalCacheValue();
			localCacheValue.annotationTypeData = annotationTypeData;
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
