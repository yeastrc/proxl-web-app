package org.yeastrc.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.auth.db.AuthLibraryDBConnectionFactory;
import org.yeastrc.auth.dto.AuthSharedObjectDTO;

/**
 * DAO for auth_shared_object table
 *
 */
public class AuthSharedObjectDAO {
	
	private static final Logger log = Logger.getLogger(AuthSharedObjectDAO.class);

	//  private constructor
	private AuthSharedObjectDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static AuthSharedObjectDAO getInstance() { 
		return new AuthSharedObjectDAO(); 
	}
	
	
	/**
	 * @param sharedObjectId
	 * @return null if not found
	 * @throws Exception
	 */
	public AuthSharedObjectDTO getAuthSharedObjectDTOForSharedObjectId( int sharedObjectId ) throws Exception {
		
		AuthSharedObjectDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "SELECT * " 
				+ " FROM auth_shared_object WHERE shared_object_id = ? ";
		//
		//CREATE TABLE IF NOT EXISTS auth_shared_object (
//				  shared_object_id INT UNSIGNED NOT NULL,
//				  public_access_code_enabled TINYINT(1) NOT NULL DEFAULT false,
//				  public_access_code VARCHAR(255) NULL,
//				  
		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, sharedObjectId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				returnItem = populateResultObject(rs);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select AuthSharedObjectDTO, sharedObjectId: " + sharedObjectId
					+ ", sql: " + sql;
			
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
		
		return returnItem;
	}


	/**
	 * @param publicAccessCode
	 * @return null if not found
	 * @throws Exception
	 */
	public AuthSharedObjectDTO getForPublicAccessCode( String publicAccessCode ) throws Exception {
		
		AuthSharedObjectDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "SELECT * " 
				+ " FROM auth_shared_object WHERE public_access_code = ?";
		//
		//CREATE TABLE IF NOT EXISTS auth_shared_object (
//				  shared_object_id INT UNSIGNED NOT NULL,
//				  user_id INT UNSIGNED NOT NULL,
//				  access_level SMALLINT UNSIGNED NOT NULL,
//				  public_access_code_enabled TINYINT(1) NOT NULL DEFAULT false,
//				  public_access_code VARCHAR(255) NULL,
//				  
		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, publicAccessCode );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				returnItem = populateResultObject(rs);
			}
			
		} catch ( Exception e ) {
			
			String msg = "getForPublicAccessCode(...): Failed to select AuthSharedObjectDTO, publicAccessCode: " + publicAccessCode
					+ ", sql: " + sql;
			
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
		
		return returnItem;
	}

	
	
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private AuthSharedObjectDTO populateResultObject(ResultSet rs)
			throws SQLException {
		
		
		AuthSharedObjectDTO returnItem;
		returnItem = new AuthSharedObjectDTO();

		returnItem.setSharedObjectId( rs.getInt( "shared_object_id" ) );
		returnItem.setPublicAccessCodeEnabled( rs.getBoolean( "public_access_code_enabled" ) );
		returnItem.setPublicAccessCode( rs.getString( "public_access_code" ) );
		return returnItem;
	}
	

	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( AuthSharedObjectDTO item ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = AuthLibraryDBConnectionFactory.getConnection(  );

			save( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
	
	
	/**
	 * @param item
	 * @param dbConnection
	 * @throws Exception
	 */
	public void save( AuthSharedObjectDTO item, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "INSERT INTO auth_shared_object ( public_access_code_enabled, public_access_code) " +
				"VALUES ( ?, ? )";
		//
		//CREATE TABLE IF NOT EXISTS auth_shared_object (
//				  shared_object_id INT UNSIGNED NOT NULL,
//				  public_access_code_enabled TINYINT(1) NOT NULL DEFAULT false,
//				  public_access_code VARCHAR(255) NULL,
//				  
		
		try {
			
			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setBoolean( counter, item.isPublicAccessCodeEnabled() );
			counter++;
			pstmt.setString( counter, item.getPublicAccessCode() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				item.setSharedObjectId( rs.getInt( 1 ) );
			} else {
				
				String msg = "Failed to insert AuthSharedObjectDTO, generated key not found.";
				
				log.error( msg );
				
				throw new Exception( msg );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert AuthSharedObjectDTO, sql: " + sql;
			
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
			
		}
		
	}
	
	

	/**
	 * Update public_access_code = ? , public_access_code_enabled = ?
	 * @param id
	 * @param publicAccessCode
	 * @param publicAccessCodeEnabled
	 * @throws Exception
	 */
	public void updatePublicAccessCodeAndEnabled( int id, String publicAccessCode, boolean publicAccessCodeEnabled ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE auth_shared_object SET public_access_code = ?, public_access_code_enabled = ? WHERE shared_object_id = ?";

		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );

//	CREATE TABLE IF NOT EXISTS `crosslinks`.`auth_shared_object` (
//			  `shared_object_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  `public_access_code_enabled` TINYINT(1) NOT NULL DEFAULT false,
//			  `public_access_code` VARCHAR(255) NULL,
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setString( counter, publicAccessCode );

			counter++;
			pstmt.setBoolean( counter, publicAccessCodeEnabled );
			
			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update public_access_code, public_access_code_enabled, sql: " + sql;
			
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
		
	}
	
	

	/**
	 * Update public_access_code = ? 
	 * @param id
	 * @param publicAccessCode
	 * @throws Exception
	 */
	public void updatePublicAccessCode( int id, String publicAccessCode ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE auth_shared_object SET public_access_code = ? WHERE shared_object_id = ?";

		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );

//	CREATE TABLE IF NOT EXISTS `crosslinks`.`auth_shared_object` (
//			  `shared_object_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  `public_access_code_enabled` TINYINT(1) NOT NULL DEFAULT false,
//			  `public_access_code` VARCHAR(255) NULL,
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setString( counter, publicAccessCode );

			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update public_access_code, public_access_code_enabled, sql: " + sql;
			
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
		
	}



	/**
	 * Update public_access_code_enabled = ?
	 * @param id
	 * @param publicAccessCodeEnabled
	 * @throws Exception
	 */
	public void updatePublicAccessCodeEnabled( int id, boolean publicAccessCodeEnabled ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE auth_shared_object SET public_access_code_enabled = ? WHERE shared_object_id = ?";

		
		try {
			
			conn = AuthLibraryDBConnectionFactory.getConnection(  );

//	CREATE TABLE IF NOT EXISTS `crosslinks`.`auth_shared_object` (
//			  `shared_object_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  `public_access_code_enabled` TINYINT(1) NOT NULL DEFAULT false,
//			  `public_access_code` VARCHAR(255) NULL,
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setBoolean( counter, publicAccessCodeEnabled );
			
			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update public_access_code_enabled, sql: " + sql;
			
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
		
	}


}
