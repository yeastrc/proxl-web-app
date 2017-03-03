package org.yeastrc.xlink.www.searcher_via_cached_data.config_size_etc_central_code;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.CacheSizeConfigStringsConstants;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappConfigException;

/**
 *
 *
 */
public class CachedDataSizeConfigEntryConvertStringToEnum {

	private static final Logger log = Logger.getLogger( CachedDataSizeConfigEntryConvertStringToEnum.class );
	
	public static CachedDataSizeConfigEntryConvertStringToEnum getInstance() {
		return new CachedDataSizeConfigEntryConvertStringToEnum();
	}
	
	// private constructor
	private CachedDataSizeConfigEntryConvertStringToEnum() { }

	/**
	 * @param cacheSizeConfigString
	 * @return
	 * @throws ProxlWebappConfigException
	 */
	public CachedDataSizeOptions convertStringToEnum_SizeConfigEntry( String cacheSizeConfigString ) throws ProxlWebappConfigException {
		
		if ( StringUtils.isEmpty( cacheSizeConfigString ) ) {
			// Default
			cacheSizeConfigString = CacheSizeConfigStringsConstants.CACHE_SIZE_VALUE_DEFAULT;
		} 
		
		CachedDataSizeOptions sizeConfigValue = null;
		if ( CacheSizeConfigStringsConstants.CACHE_SIZE_VALUE_FULL.equals( cacheSizeConfigString ) ) {
			sizeConfigValue = CachedDataSizeOptions.FULL;
		} else if ( CacheSizeConfigStringsConstants.CACHE_SIZE_VALUE_HALF.equals( cacheSizeConfigString ) ) {
			sizeConfigValue = CachedDataSizeOptions.HALF;
		} else if ( CacheSizeConfigStringsConstants.CACHE_SIZE_VALUE_SMALL.equals( cacheSizeConfigString ) ) {
			sizeConfigValue = CachedDataSizeOptions.SMALL;
		} else if ( CacheSizeConfigStringsConstants.CACHE_SIZE_VALUE_FEW.equals( cacheSizeConfigString ) ) {
			sizeConfigValue = CachedDataSizeOptions.FEW;
		} else {
			String msg = "Unknown value for config key '"
					+ ConfigSystemsKeysConstants.CACHE_SIZE_CONFIG_LABEL_KEY
					+ "', value: " + cacheSizeConfigString;
			log.error(msg);
			throw new ProxlWebappConfigException(msg);
		}

		return sizeConfigValue;
	}
}
