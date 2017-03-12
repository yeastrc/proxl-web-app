package org.yeastrc.xlink.www.annotation_utils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
//import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.searcher.AnnotationTypeForSearchIdsSearcher;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Central place to get Annotation Type data
 * 
 * Singleton instance
 */
public class GetAnnotationTypeData implements CachedDataCommonIF {
	
//	private static final Logger log = Logger.getLogger(GetAnnotationTypeData.class);
	private static final int CACHE_MAX_SIZE = 500;
	private static final int CACHE_TIMEOUT = 20; // in days
	/**
	 * Static get singleton instance
	 * @return
	 */
	public static GetAnnotationTypeData getInstance() {
		return _instance; 
	}
	
	/* (non-Javadoc)
	 * @see org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF#clearCacheData()
	 * 
	 * Clear all entries from the cache
	 */
	@Override
	public void clearCacheData() throws Exception {
		annotationTypeDataCache.invalidateAll();
	}
	
	@Override
	public CacheCurrentSizeMaxSizeResult getCurrentCacheSizeAndMax() throws Exception {
		CacheCurrentSizeMaxSizeResult result = new CacheCurrentSizeMaxSizeResult();
		result.setCurrentSize( annotationTypeDataCache.size() );
		result.setMaxSize( CACHE_MAX_SIZE );
		return result;
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
		LocalCacheValue localCacheValue = annotationTypeDataCache.get( localCacheKey );
		if ( localCacheValue == null ) {
			throw new Exception("Should be getting annotation data via cache internal load");
		}
		Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeData = localCacheValue.annotationTypeData;
		return annotationTypeData;
	}
	/**
	 * Static singleton instance
	 */
	private static final GetAnnotationTypeData _instance = new GetAnnotationTypeData();
	
	/**
	 * constructor
	 */
	private GetAnnotationTypeData() {
		annotationTypeDataCache = CacheBuilder.newBuilder()
				.expireAfterAccess( CACHE_TIMEOUT, TimeUnit.DAYS )
			    .maximumSize( CACHE_MAX_SIZE )
			    .build(
			    		new CacheLoader<LocalCacheKey, LocalCacheValue>() {
			    			public LocalCacheValue load(LocalCacheKey localCacheKey) throws Exception {
			    				//   WARNING  cannot return null.  
			    				//   If would return null, throw ProxlWebappDataNotFoundException and catch at the .get(...)
			    				//  value is NOT in cache so get it and return it
			    				return loadFromDB(localCacheKey);
			    			}
			    		});
//			    .build(); // no CacheLoader
		//  Register this class with the centralized Cached Data Registry, to support centralized cache clearing
		CachedDataCentralRegistry.getInstance().register( this );
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
		LocalCacheKey localCacheKey;
		Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeData;
		long lastAccessTime;
	}
	
	/**
	 * cached annotation type data
	 */
	private LoadingCache<LocalCacheKey, LocalCacheValue> annotationTypeDataCache = null;
	
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
		localCacheValue.localCacheKey = localCacheKey;
		localCacheValue.annotationTypeData = annotationTypeData;
		localCacheValue.lastAccessTime = System.currentTimeMillis();
		return localCacheValue;
	}
}
