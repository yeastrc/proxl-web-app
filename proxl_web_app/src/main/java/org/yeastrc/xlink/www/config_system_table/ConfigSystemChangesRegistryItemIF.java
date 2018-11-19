package org.yeastrc.xlink.www.config_system_table;

/**
 * Called for changes to config_system table changes
 *
 */
public interface ConfigSystemChangesRegistryItemIF {

	// Called when update any or all the values
	public void allConfigKeysChanged() throws Exception;
	
	public void specificConfigKeyChanged( String configKey ) throws Exception;
}
