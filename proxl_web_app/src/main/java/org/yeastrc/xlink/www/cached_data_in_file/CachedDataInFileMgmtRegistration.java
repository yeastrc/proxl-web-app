package org.yeastrc.xlink.www.cached_data_in_file;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.main.PPM_Error_Histogram_For_PSMPeptideCutoffs_CachedResultManager;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.main.PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.main.PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates_merged.main.PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager;
import org.yeastrc.xlink.www.qc_data.scan_level_data.main.Scan_MS_1_IonCurrent_Histograms_CachedResultManager;
import org.yeastrc.xlink.www.qc_data.scan_ms1_all_scan_intensity_heatmap.main.MS1_All_IntensityHeatmapImageCachedResultImageManager;
import org.yeastrc.xlink.www.qc_data.summary_statistics.main.QC_SummaryCounts_CachedResultManager;
import org.yeastrc.xlink.www.qc_data.summary_statistics_merged.main.QC_SummaryCounts_Merged_CachedResultManager;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerCrosslinkService_Results_Main_CachedResultManager;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerCrosslinkService_Results_PsmCount_CachedResultManager;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerLooplinkService_Results_Main_CachedResultManager;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerLooplinkService_Results_PsmCount_CachedResultManager;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerMonolinkService_Results_Main_CachedResultManager;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerMonolinkService_Results_PsmCount_CachedResultManager;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerProteinDataService_Results_CachedResultManager;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerSequenceCoverageService_Results_CachedResultManager;

/**
 * Registration to use CachedDataInFileMgmt.  Registration is required.
 * 
 * For Cached data that is written to a file under the Cached Data Directory.
 * 
 * Singleton
 */
public class CachedDataInFileMgmtRegistration {

	private static final Logger log = LoggerFactory.getLogger(  CachedDataInFileMgmtRegistration.class);

	private static final CachedDataInFileMgmtRegistration instance = new CachedDataInFileMgmtRegistration();

	// private constructor
	private CachedDataInFileMgmtRegistration() {}

	/**
	 * @return Singleton instance
	 */
	public static CachedDataInFileMgmtRegistration getSingletonInstance() {
		return instance;
	}
	
	/**
	 * An entry in registrationEntries
	 *
	 */
	private static class RegistrationEntry {
		private String prefix;
		private int version;
	}
	
	private Map<String, RegistrationEntry> registrationEntries = new HashMap<>();

	private Map<String, RegistrationEntry> oldUnusedPrefixesToRemoveEntries = new HashMap<>();

    /**
     * Used to stop removal of cached directories
     */
    private volatile boolean shutdownNowReceived = false;
    
	/**
	 * Called on web app shutdown
	 */
	public void shutdownNow(){
		log.warn( "INFO: shutdownNow() called." );
		shutdownNowReceived = true;
		CachedDataInFileMgmt.getSingletonInstance().shutdownNow();
	}
	
	/**
	 * Array of classes to call "register()" on so they will call "register(...)" on this class.
	 */
	private final CachedDataInFileMgmtRegistrationIF[] classesToRegister = {
			
			// Image and Structure Page webservices
			ViewerProteinDataService_Results_CachedResultManager.getSingletonInstance(),
			ViewerCrosslinkService_Results_Main_CachedResultManager.getSingletonInstance(),
			ViewerCrosslinkService_Results_PsmCount_CachedResultManager.getSingletonInstance(),
			ViewerLooplinkService_Results_Main_CachedResultManager.getSingletonInstance(),
			ViewerLooplinkService_Results_PsmCount_CachedResultManager.getSingletonInstance(),
			ViewerMonolinkService_Results_Main_CachedResultManager.getSingletonInstance(),
			ViewerMonolinkService_Results_PsmCount_CachedResultManager.getSingletonInstance(),
			ViewerSequenceCoverageService_Results_CachedResultManager.getSingletonInstance(),
			
			//  QC Single Search
			MS1_All_IntensityHeatmapImageCachedResultImageManager.getSingletonInstance(),
			Scan_MS_1_IonCurrent_Histograms_CachedResultManager.getSingletonInstance(),

			QC_SummaryCounts_CachedResultManager.getSingletonInstance(),

			PPM_Error_Histogram_For_PSMPeptideCutoffs_CachedResultManager.getSingletonInstance(),
			PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager.getSingletonInstance(),
			PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_CachedResultManager.getSingletonInstance(),

			//  QC Merged

			QC_SummaryCounts_Merged_CachedResultManager.getSingletonInstance(),
			PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_CachedResultManager.getSingletonInstance()
	};

	/**
	 * !!!!!!!!  Update this to add prefixes and last version to list to clean up
	 * 
	 *  This is part of removal of unused cached data files
	 * 
	 * called from init()
	 * 
	 * @throws ProxlWebappInternalErrorException
	 */
	private void addToOldUnusedPrefixesToRemove() throws ProxlWebappInternalErrorException {

//		oldUnusedPrefixesToRemove( <prefix>, <last version used> );
		
	}
	
	/**
	 * Called on web app startup
	 * @throws Exception 
	 */
	public void init() throws Exception {

		if ( classesToRegister == null && classesToRegister.length == 0 ) {
			log.warn( "No entries in classesToRegister so exiting" );
			return;  //  EARLY EXIT
		}
		
		log.warn( "INFO: Starting:  Calling 'register()' on entries in classesToRegister.  classesToRegister.length: " + classesToRegister.length );
		
		for ( CachedDataInFileMgmtRegistrationIF classToRegister : classesToRegister ) {
			if ( shutdownNowReceived ) {
				return;  //  EARLY EXIT
			}
			classToRegister.register();
		}
		log.warn( "INFO: Finished:  Calling 'register()' on entries in classesToRegister.  classesToRegister.length: " + classesToRegister.length );
		
		if ( shutdownNowReceived ) {
			return;  //  EARLY EXIT
		}
		
		addToOldUnusedPrefixesToRemove();
		
		log.warn( "INFO: Starting thread to run CachedDataInFileMgmtCleanupOnWebAppStartRunnable to clean up unused cached files." );
		
		CachedDataInFileMgmtCleanupOnWebAppStartRunnable cachedDataInFileMgmtCleanupOnWebAppStartRunnable =
				CachedDataInFileMgmtCleanupOnWebAppStartRunnable.getNewInstance();
		
		Thread thread = new Thread( cachedDataInFileMgmtCleanupOnWebAppStartRunnable );
		thread.start();
	}
	
	/**
	 * @param prefix
	 * @param version
	 * @return
	 */
	public boolean isPrefixAndVersionAllowed( String prefix, int version ) {
		RegistrationEntry registrationEntry = registrationEntries.get( prefix );
		if ( registrationEntry == null ) {
			String msg = "prefix '" + prefix + "' is not registered.";
			log.error( msg );
			return false;
		}
		if ( registrationEntry.version != version ) {
			String msg = "version does not match: " + version + ", prefix '" + prefix + "'.";
			log.error( msg );
			return false;
		}
		return true;
	}
	
	/**
	 * @param prefix
	 * @param version - Must be > 0
	 * @throws ProxlWebappInternalErrorException
	 */
	public void register( String prefix, int version ) throws ProxlWebappInternalErrorException {
		RegistrationEntry registrationEntry = registrationEntries.get( prefix );
		if ( registrationEntry != null ) {
			String msg = "prefix '" + prefix + "' is already registered.";
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
		if ( version < 1 ) {
			String msg = "version cannot be < 1, is '" + version + "'.";
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
		registrationEntry = new RegistrationEntry();
		registrationEntry.prefix = prefix;
		registrationEntry.version = version;
		registrationEntries.put( prefix, registrationEntry );
	}
		
	/**
	 * Any old unused prefixes to remove
	 * @param prefix
	 * @param lastVersion - last version that was used
	 * @throws ProxlWebappInternalErrorException 
	 */
	public void oldUnusedPrefixesToRemove( String prefix, int lastVersion ) throws ProxlWebappInternalErrorException {
		RegistrationEntry registrationEntry = oldUnusedPrefixesToRemoveEntries.get( prefix );
		if ( registrationEntry != null ) {
			String msg = "prefix '" + prefix + "' is already registered.";
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
		if ( lastVersion < 1 ) {
			String msg = "lastVersion cannot be < 1, is '" + lastVersion + "'.";
			log.error( msg );
			throw new ProxlWebappInternalErrorException(msg);
		}
		registrationEntry = new RegistrationEntry();
		registrationEntry.prefix = prefix;
		registrationEntry.version = lastVersion;
		oldUnusedPrefixesToRemoveEntries.put( prefix, registrationEntry );
	}

	/**
	 * Package private
	 * Called from CachedDataInFileMgmtRegistration
	 * @throws Exception
	 */
	void cleanupUnusedCachedFilesDirectories() throws Exception {
		if ( shutdownNowReceived ) {
			return;  //  EARLY EXIT
		}
		log.warn( "registrationEntries.size: " + registrationEntries.size() );
		log.warn( "oldUnusedPrefixesToRemoveEntries.size: " + oldUnusedPrefixesToRemoveEntries.size() );
		final int startingVersion = 1;
		//  For current entries, delete all versions before current version
		for ( Map.Entry<String, RegistrationEntry> entry : registrationEntries.entrySet() ) {
			RegistrationEntry registrationEntry = entry.getValue();
			String namePrefix = registrationEntry.prefix;
			for ( int versionToDelete = startingVersion; versionToDelete < registrationEntry.version; versionToDelete++ ) {
				if ( shutdownNowReceived ) {
					return;  //  EARLY EXIT
				}
				CachedDataInFileMgmt.getSingletonInstance().removeCachedDataDirectory( namePrefix, versionToDelete );
			}
		}

		//  For Old prefixes to remove entries, delete all versions including current version
		for ( Map.Entry<String, RegistrationEntry> entry : oldUnusedPrefixesToRemoveEntries.entrySet() ) {
			RegistrationEntry registrationEntry = entry.getValue();
			String namePrefix = registrationEntry.prefix;
			for ( int versionToDelete = startingVersion; versionToDelete <= registrationEntry.version; versionToDelete++ ) {
				if ( shutdownNowReceived ) {
					return;  //  EARLY EXIT
				}
				CachedDataInFileMgmt.getSingletonInstance().removeCachedDataDirectory( namePrefix, versionToDelete );
			}
		}
		
	}
	
 }
