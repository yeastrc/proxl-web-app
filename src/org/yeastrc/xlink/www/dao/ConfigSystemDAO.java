package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.ConfigSystemDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;

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
	
	
	

	/**
	 * @param configList
	 * @throws Exception
	 */
	public void updateValueOnlyOnConfigKey( List<ConfigSystemDTO> configList  ) throws Exception {
		
		Connection conn = null;
		
		PreparedStatement queryPstmt = null;
		ResultSet queryRS = null;

		PreparedStatement updatePstmt = null;
		
		final String querySQL = "SELECT id FROM config_system WHERE config_key = ?";

		final String updateSQL = "UPDATE config_system SET config_value = ? WHERE config_key = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			conn.setAutoCommit(false);

			queryPstmt = conn.prepareStatement( querySQL );
			
			updatePstmt = conn.prepareStatement( updateSQL );
			
			for ( ConfigSystemDTO configItem : configList ) {

				queryPstmt.setString( 1, configItem.getConfigKey()  );

				queryRS = queryPstmt.executeQuery();
				
				if( ! queryRS.next() ) {

					String msg = "Config key '" + configItem.getConfigKey() 
							+ "' not found in database.";
					log.error( msg );

					throw new ProxlWebappDataException( msg );
				}

				updatePstmt.setString( 1, configItem.getConfigValue() );
				updatePstmt.setString( 2, configItem.getConfigKey() );

//				int updatedRecordCount = 
				updatePstmt.executeUpdate();
			}
			
			conn.commit();
			
		} catch ( Exception e ) {
			
			conn.rollback();
			
			String msg = "Failed to update config_system, updateSQL: " + updateSQL;
			
			log.error( msg, e );
			
			throw e;
			

		} finally {
			
			// be sure database handles are closed
			if( queryRS != null ) {
				try { queryRS.close(); } catch( Throwable t ) { ; }
				queryRS = null;
			}

			if( queryPstmt != null ) {
				try { queryPstmt.close(); } catch( Throwable t ) { ; }
				queryPstmt = null;
			}
			
			if( updatePstmt != null ) {
				try { updatePstmt.close(); } catch( Throwable t ) { ; }
				updatePstmt = null;
			}
			
			conn.setAutoCommit(true);
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
	}
	
	

}
