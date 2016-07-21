package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.ConfigSystemDTO;

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

	
	public List<ConfigSystemDTO> getAll() throws Exception {
		
		List<ConfigSystemDTO> results = new ArrayList<>();
		

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM config_system";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				ConfigSystemDTO item = new ConfigSystemDTO();
				
				item.setId( rs.getInt( "id" ) );
				item.setConfigKey( rs.getString( "config_key" ) );
				item.setConfigValue( rs.getString( "config_value" ) );
				item.setComment( rs.getString( "comment" ) );

				results.add( item );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to get all config_system, sql: " + sql;
			
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
		
		
		return results;
	}
	

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
	
	
	//  Insert, on duplicate update

	private static final String INSERT_UPDATE_SQL = 
			
			"INSERT INTO config_system (config_key, config_value, comment)"
			+ "VALUES (?, ?, ?)"
					
			+ " ON DUPLICATE KEY UPDATE config_value = ?";

	/**
	 * @param configList
	 * @throws Exception
	 */
	public void updateValueOnlyOnConfigKey( List<ConfigSystemDTO> configList  ) throws Exception {
		
		Connection conn = null;

		PreparedStatement pstmt = null;
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			conn.setAutoCommit(false);

			pstmt = conn.prepareStatement( INSERT_UPDATE_SQL );
			
			for ( ConfigSystemDTO configItem : configList ) {

				int counter = 0;
				
				counter++;
				pstmt.setString( counter, configItem.getConfigKey() );
				counter++;
				pstmt.setString( counter, configItem.getConfigValue() );
				counter++;
				pstmt.setString( counter, configItem.getComment() );
				counter++;
				pstmt.setString( counter, configItem.getConfigValue() );

//				int updatedRecordCount = 
				pstmt.executeUpdate();
			}
			
			conn.commit();
			
		} catch ( Exception e ) {
			
			conn.rollback();
			
			String msg = "Failed to insert or update config_system, SQL: " + INSERT_UPDATE_SQL;
			
			log.error( msg, e );
			
			throw e;
			

		} finally {
			
			// be sure database handles are closed
			
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			try {
				conn.setAutoCommit(true);
			 } catch( Throwable t ) { ; }
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
	}
	
	

}
