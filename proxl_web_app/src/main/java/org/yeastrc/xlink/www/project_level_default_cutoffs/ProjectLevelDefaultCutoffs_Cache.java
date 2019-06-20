package org.yeastrc.xlink.www.project_level_default_cutoffs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;
import org.yeastrc.xlink.www.cached_data_mgmt.CacheCurrentSizeMaxSizeResult;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCentralRegistry;
import org.yeastrc.xlink.www.cached_data_mgmt.CachedDataCommonIF;
import org.yeastrc.xlink.www.dto.ProjectLevelDefaultFltrAnnCutoffs_DTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.searcher.ProjectLevelDefaultFltrAnnCutoffs_For_SettingSearchDefaults_Searcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataCentralConfigStorageAndProcessing;
import org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code.CachedDataSizeOptions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * 
 * Singleton instance
 * 
 * Cache Key:  
 * Cache Value: 
 */
public class ProjectLevelDefaultCutoffs_Cache implements CachedDataCommonIF {

	private static final Logger log = LoggerFactory.getLogger( ProjectLevelDefaultCutoffs_Cache.class);
	
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
	private static ProjectLevelDefaultCutoffs_Cache _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized ProjectLevelDefaultCutoffs_Cache getSingletonInstance() throws Exception {
		if ( _instance == null ) {
			_instance = new ProjectLevelDefaultCutoffs_Cache();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private ProjectLevelDefaultCutoffs_Cache() {
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
	
	public static class ProjectLevelDefaultCutoffs_Cache_Result {
		
		private ProjectLevelDefaultCutoffs_Cache_Result_Per_Type reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type;
		private ProjectLevelDefaultCutoffs_Cache_Result_Per_Type psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type;
		
		public ProjectLevelDefaultCutoffs_Cache_Result_Per_Type getReportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type() {
			return reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type;
		}
		public ProjectLevelDefaultCutoffs_Cache_Result_Per_Type getPsm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type() {
			return psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type;
		}
	}

	public static class ProjectLevelDefaultCutoffs_Cache_Result_Per_Type{
		
		private List<ProjectLevelDefaultFltrAnnCutoffs_DTO> projectLevelDefaultFltrAnnCutoffs;
		private Map<String, ProjectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName> perSingleSearchProgramName_Key_SearchProgramName;
		
		public List<ProjectLevelDefaultFltrAnnCutoffs_DTO> getProjectLevelDefaultFltrAnnCutoffs() {
			return projectLevelDefaultFltrAnnCutoffs;
		}

		public ProjectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName getForSearchProgramName( String searchProgramName ) {
			return perSingleSearchProgramName_Key_SearchProgramName.get( searchProgramName );
		}
	}
	
	public static class ProjectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName {
		
		private Map<String, ProjectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName> perSingleAnnotationTypeName_Key_AnnotationTypeName;
		
		public ProjectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName getForAnnotationTypeName( String annotationTypeName ) {
			return perSingleAnnotationTypeName_Key_AnnotationTypeName.get( annotationTypeName );
		}
	}
	
	public static class ProjectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName {
		
		double projectLevelCutoff;

		public double getProjectLevelCutoff() {
			return projectLevelCutoff;
		}
	}

	/**
	 * Remove specific projectId from cache since data changed
	 * @param projectSearchId
	 */
	public void invalidateProjectId( int projectId ) {
		LocalCacheKey localCacheKey = new LocalCacheKey();
		localCacheKey.projectId = projectId;
		cacheHolderInternal.invalidateKey( localCacheKey );
	}
	
	/**
	 * @param searchIds
	 * @param filterableDescriptiveAnnotationType
	 * @param psmPeptideAnnotationType
	 * @return
	 * @throws Exception
	 */
	public ProjectLevelDefaultCutoffs_Cache_Result getDefaultCutoffs_ForProjectId( int projectId ) throws Exception {
		
		LocalCacheKey localCacheKey = new LocalCacheKey();
		localCacheKey.projectId = projectId;

		LoadingCache<LocalCacheKey, LocalCacheValue> cache = cacheHolderInternal.getCache();
		
		LocalCacheValue localCacheValue = cache.get( localCacheKey );
		if ( localCacheValue == null ) {
			throw new ProxlWebappInternalErrorException("Should be getting data via cache internal load");
		}

		ProjectLevelDefaultCutoffs_Cache_Result response = new ProjectLevelDefaultCutoffs_Cache_Result();
		response.reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type = localCacheValue.reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type;
		response.psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type = localCacheValue.psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type;
				
		return response;
	}
	
	/**
	 * classes for holding data in the cache
	 * 
	 * key to the cache
	 *
	 */
	private static class LocalCacheKey {
		
		int projectId;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + projectId;
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
			if (projectId != other.projectId)
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

		private ProjectLevelDefaultCutoffs_Cache_Result_Per_Type reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type;
		private ProjectLevelDefaultCutoffs_Cache_Result_Per_Type psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type;
	}

	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private ProjectLevelDefaultCutoffs_Cache parentObject;
		
		private CacheHolderInternal( ProjectLevelDefaultCutoffs_Cache parentObject ) {
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
				cacheMaxSize = ProjectLevelDefaultCutoffs_Cache.CACHE_MAX_SIZE_FULL_SIZE;
				if ( cachedDataSizeOptions == CachedDataSizeOptions.HALF ) {
					cacheMaxSize = cacheMaxSize / 2;
				} else if ( cachedDataSizeOptions == CachedDataSizeOptions.SMALL
						|| cachedDataSizeOptions == CachedDataSizeOptions.FEW ) {
					cacheMaxSize = ProjectLevelDefaultCutoffs_Cache.CACHE_MAX_SIZE_SMALL_FEW;
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

		private synchronized void invalidateKey( LocalCacheKey key ) {
			if ( dbRecordsDataCache != null ) {
				dbRecordsDataCache.invalidate( key );
			}
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
			
			List<ProjectLevelDefaultFltrAnnCutoffs_DTO> dbResultList =
					ProjectLevelDefaultFltrAnnCutoffs_For_SettingSearchDefaults_Searcher.getInstance().getAllForProjectId( localCacheKey.projectId );
			
			int peptideCounts = 0;
			int psmCounts = 0;
			
			for ( ProjectLevelDefaultFltrAnnCutoffs_DTO entry : dbResultList ) {
				if ( entry.getPsmPeptideAnnotationType() == PsmPeptideAnnotationType.PEPTIDE ) {
					peptideCounts++;
				} else if ( entry.getPsmPeptideAnnotationType() == PsmPeptideAnnotationType.PSM ) {
					psmCounts++;
				}
			}
			
			List<ProjectLevelDefaultFltrAnnCutoffs_DTO> reportedPeptide_ProjectLevelDefaultFltrAnnCutoffs = new ArrayList<>( peptideCounts );
			List<ProjectLevelDefaultFltrAnnCutoffs_DTO> psm_ProjectLevelDefaultFltrAnnCutoffs = new ArrayList<>( psmCounts );
			
			Map<String, ProjectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName> reportedPeptide_perSingleSearchName_Key_SearchName = new HashMap<>();
			Map<String, ProjectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName> psm_perSingleSearchName_Key_SearchName = new HashMap<>();

			for ( ProjectLevelDefaultFltrAnnCutoffs_DTO entry : dbResultList ) {
	
				if ( entry.getPsmPeptideAnnotationType() == PsmPeptideAnnotationType.PEPTIDE ) {
					loadFromDB_ProcessPerType( entry, reportedPeptide_ProjectLevelDefaultFltrAnnCutoffs, reportedPeptide_perSingleSearchName_Key_SearchName );
				} else if ( entry.getPsmPeptideAnnotationType() == PsmPeptideAnnotationType.PSM ) {
					loadFromDB_ProcessPerType( entry, psm_ProjectLevelDefaultFltrAnnCutoffs, psm_perSingleSearchName_Key_SearchName );
				}
			}
			
			ProjectLevelDefaultCutoffs_Cache_Result_Per_Type reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type = new ProjectLevelDefaultCutoffs_Cache_Result_Per_Type();
			reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type.projectLevelDefaultFltrAnnCutoffs = Collections.unmodifiableList( reportedPeptide_ProjectLevelDefaultFltrAnnCutoffs );;
			reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type.perSingleSearchProgramName_Key_SearchProgramName = reportedPeptide_perSingleSearchName_Key_SearchName;
			
			ProjectLevelDefaultCutoffs_Cache_Result_Per_Type psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type = new ProjectLevelDefaultCutoffs_Cache_Result_Per_Type();
			psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type.projectLevelDefaultFltrAnnCutoffs = Collections.unmodifiableList( psm_ProjectLevelDefaultFltrAnnCutoffs );
			psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type.perSingleSearchProgramName_Key_SearchProgramName = psm_perSingleSearchName_Key_SearchName;
			
			LocalCacheValue localCacheValue = new LocalCacheValue();
			localCacheValue.reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type = reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type;
			localCacheValue.psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type = psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type;

			return localCacheValue;
		}

		private void loadFromDB_ProcessPerType( 
				ProjectLevelDefaultFltrAnnCutoffs_DTO entry, 
				List<ProjectLevelDefaultFltrAnnCutoffs_DTO> projectLevelDefaultFltrAnnCutoffs,
				Map<String, ProjectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName> perSingleSearchName_Key_SearchName ) {

			{
				ProjectLevelDefaultFltrAnnCutoffs_DTO projectLevelDefaultFltrAnnCutoffs_DTO = new ProjectLevelDefaultFltrAnnCutoffs_DTO();
				projectLevelDefaultFltrAnnCutoffs_DTO.setProjectId( entry.getProjectId() );
				projectLevelDefaultFltrAnnCutoffs_DTO.setSearchProgramName( entry.getSearchProgramName() );
				projectLevelDefaultFltrAnnCutoffs_DTO.setAnnotationTypeName( entry.getAnnotationTypeName() );
				projectLevelDefaultFltrAnnCutoffs_DTO.setAnnotationCutoffValue( entry.getAnnotationCutoffValue() );

				projectLevelDefaultFltrAnnCutoffs.add( projectLevelDefaultFltrAnnCutoffs_DTO );
			}
			{
				String searchProgramName = entry.getSearchProgramName();
				String annotationTypeName = entry.getAnnotationTypeName();
				ProjectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName per_SearchProgramName = perSingleSearchName_Key_SearchName.get( searchProgramName );
				if ( per_SearchProgramName == null ) {
					per_SearchProgramName = new ProjectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName();
					perSingleSearchName_Key_SearchName.put( searchProgramName, per_SearchProgramName );
				}
				Map<String, ProjectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName> perSingleAnnotationTypeName_Key_AnnotationTypeName = per_SearchProgramName.perSingleAnnotationTypeName_Key_AnnotationTypeName;
				if ( perSingleAnnotationTypeName_Key_AnnotationTypeName == null ) {
					perSingleAnnotationTypeName_Key_AnnotationTypeName = new HashMap<>();
					per_SearchProgramName.perSingleAnnotationTypeName_Key_AnnotationTypeName = perSingleAnnotationTypeName_Key_AnnotationTypeName;
				}
				ProjectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName per_AnnotationTypeName = 
						perSingleAnnotationTypeName_Key_AnnotationTypeName.get( annotationTypeName );
				if ( per_AnnotationTypeName == null ) {
					per_AnnotationTypeName = new ProjectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName();
					perSingleAnnotationTypeName_Key_AnnotationTypeName.put( annotationTypeName, per_AnnotationTypeName );
				}
				per_AnnotationTypeName.projectLevelCutoff = entry.getAnnotationCutoffValue();
			}
		}
		
		
 	}
	
	
	/////////////////////////////////////////

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