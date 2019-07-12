package org.yeastrc.xlink.www.config_properties_file;

/**
 * Values from proxl_config_defaults.properties or overlaid by proxl_config.properties
 *
 * Singleton instance
 */
public class ProxlConfigFileValues {

	private static final ProxlConfigFileValues instance = new ProxlConfigFileValues();
	
	/**
	 * @return Singleton instance
	 */
	public static ProxlConfigFileValues getInstance() {
		return instance;
	}
	
	/**
	 * private constructor
	 */
	private ProxlConfigFileValues() {}
	
	//  Spectral Storage Server Connection Info
	private String spectralStorageServerURLandAppContext;
	
 	/**
	 * special for when re-computing values for table unified_rp__search__rep_pept__generic_lookup
	 * First set annotation_type_filterable.default_filter_value_at_database_load = null where annotation_type_filterable.default_filter_at_database_load = 1
	 *     so that those records will always cause the determination for is cutoffs == default cutoffs to be false.
	 */
	private boolean allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True;

	/**
	 * special for when re-computing values for table unified_rp__search__rep_pept__generic_lookup
	 * First set annotation_type_filterable.default_filter_value_at_database_load = null where annotation_type_filterable.default_filter_at_database_load = 1
	 *     so that those records will always cause the determination for is cutoffs == default cutoffs to be false.
	 * @return
	 */
	public boolean isAllowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True() {
		return allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True;
	}

	/**
	 * special for when re-computing values for table unified_rp__search__rep_pept__generic_lookup
	 * First set annotation_type_filterable.default_filter_value_at_database_load = null where annotation_type_filterable.default_filter_at_database_load = 1
	 *     so that those records will always cause the determination for is cutoffs == default cutoffs to be false.
	 * @param allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True
	 */
	public void setAllowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True(
			boolean allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True) {
		this.allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True = allowAnnTypeFilterDefaultFilterValueAtDatabaseLoad_Null_When_isDefaultFilter_True;
	}
	
	public String getSpectralStorageServerURLandAppContext() {
		return spectralStorageServerURLandAppContext;
	}

	public void setSpectralStorageServerURLandAppContext(String spectralStorageServerURLandAppContext) {
		this.spectralStorageServerURLandAppContext = spectralStorageServerURLandAppContext;
	}

}
