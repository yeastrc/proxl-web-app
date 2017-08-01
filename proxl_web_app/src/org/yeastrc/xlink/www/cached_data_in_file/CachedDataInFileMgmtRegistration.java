package org.yeastrc.xlink.www.cached_data_in_file;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.qc_data.scan_ms1_all_scan_intensity_heatmap.main.MS1_All_IntensityHeatmapImageCachedResultImageManager;

/**
 * Registration to use CachedDataInFileMgmt.  Registration is required.
 * 
 * For Cached data that is written to a file under the Cached Data Directory.
 * 
 * Singleton
 */
public class CachedDataInFileMgmtRegistration {

	private static final Logger log = Logger.getLogger( CachedDataInFileMgmtRegistration.class);

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

	/**
	 * Array of classes to call "register()" on so they will call "register(...)" on this class.
	 */
	private final CachedDataInFileMgmtRegistrationIF[] classesToRegister = {
		MS1_All_IntensityHeatmapImageCachedResultImageManager.getSingletonInstance()
	};
	
	/**
	 * Called on web app shutdown
	 * @throws Exception 
	 */
	public void init() throws Exception {

		if ( classesToRegister == null && classesToRegister.length == 0 ) {
			log.warn( "No entries in classesToRegister so exiting" );
			return;  //  EARLY EXIT
		}
		
		log.warn( "Starting:  Calling 'register()' on entries in classesToRegister.  classesToRegister.length: " + classesToRegister.length );
		
		for ( CachedDataInFileMgmtRegistrationIF classToRegister : classesToRegister ) {
			classToRegister.register();
		}
		log.warn( "Finished:  Calling 'register()' on entries in classesToRegister.  classesToRegister.length: " + classesToRegister.length );
		

		//  TODO  Start thread to clean up all versions < version registered 
		//           and all unused prefixes { calls to oldUnusedPrefixesToRemove( String prefix ) }
		
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
	 * @param version
	 * @throws ProxlWebappInternalErrorException
	 */
	public void register( String prefix, int version ) throws ProxlWebappInternalErrorException {
		RegistrationEntry registrationEntry = registrationEntries.get( prefix );
		if ( registrationEntry != null ) {
			String msg = "prefix '" + prefix + "' is already registered.";
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
	 */
	public void oldUnusedPrefixesToRemove( String prefix ) {
		
	}
	
	/**
	 * Called on web app shutdown
	 */
	public void shutdownNow(){
		
		//  TODO  Kill cleanup thread if executing
	}
 }
