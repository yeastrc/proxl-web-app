package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.config_system_table_common_access.IConfigSystemTableGetValue;

/**
 * DAO for config_system table
 *
 * For Importer and Run Importer
 */
public class ConfigSystemDAO implements IConfigSystemTableGetValue {
	
	private static final Logger log = LoggerFactory.getLogger( ConfigSystemDAO.class);

	//  private constructor
	private ConfigSystemDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static ConfigSystemDAO getInstance() { 
		return new ConfigSystemDAO(); 
	}
	
	
//	CREATE TABLE config_system (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  config_key VARCHAR(255) NOT NULL,
//			  config_value VARCHAR(4000) NULL,
//			  comment VARCHAR(4000) NULL,

	
	/**
	 * @param configKey
	 * @return null if not found
	 * @throws Exception
	 */
	public String getConfigValueForConfigKey( String configKey ) throws Exception {


		String configValue = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM config_system WHERE config_key = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, configKey );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				configValue = rs.getString( "config_value" );

			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select config_system, configKey: " + configKey + ", sql: " + sql;
			
			log.error( msg, e );
			
			throw e;
			

		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		return configValue;
	}
	

}
