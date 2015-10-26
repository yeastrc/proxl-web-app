package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * DAO for config_system table
 *
 */
public class ConfigSystemDAO {
	
	private static final Logger log = Logger.getLogger(ConfigSystemDAO.class);

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
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
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
