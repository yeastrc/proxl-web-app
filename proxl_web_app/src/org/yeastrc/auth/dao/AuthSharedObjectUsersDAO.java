package org.yeastrc.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.yeastrc.auth.db.AuthLibraryDBConnectionFactory;
import org.yeastrc.auth.dto.AuthSharedObjectUsersDTO;

/**
 * DAO for auth_shared_object_users table
 */
public class AuthSharedObjectUsersDAO {
	
	private static final Logger log = Logger.getLogger(AuthSharedObjectUsersDAO.class);
	//  private constructor
	private AuthSharedObjectUsersDAO() { }
	/**
	 * @return newly created instance
	 */
	public static AuthSharedObjectUsersDAO getInstance() { 
		return new AuthSharedObjectUsersDAO(); 
	}
	
	/**
	 * @param sharedObjectId
	 * @param userId
	 * @return null if not found
	 * @throws Exception
	 */
	public AuthSharedObjectUsersDTO getAuthSharedObjectUsersDTOForSharedObjectIdAndUserId( int sharedObjectId, int userId ) throws Exception {
		AuthSharedObjectUsersDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM auth_shared_object_users WHERE shared_object_id = ? AND user_id = ? ";
		try {
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, sharedObjectId );
			pstmt.setInt( 2, userId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = populateResultObject(rs);
			}
		} catch ( Exception e ) {
			String msg = "Failed to select AuthSharedObjectUsersDTO, sharedObjectId: " + sharedObjectId
					+ ", userId: " + userId + ", sql: " + sql;
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
	private AuthSharedObjectUsersDTO populateResultObject(ResultSet rs)
			throws SQLException {
		AuthSharedObjectUsersDTO returnItem;
		returnItem = new AuthSharedObjectUsersDTO();
		returnItem.setSharedObjectId( rs.getInt( "shared_object_id" ) );
		returnItem.setUserId( rs.getInt( "user_id" ) );
		returnItem.setAccessLevel( rs.getInt( "access_level" ) );
		return returnItem;
	}
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( AuthSharedObjectUsersDTO item ) throws Exception {
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
	public void save( AuthSharedObjectUsersDTO item, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "INSERT INTO auth_shared_object_users ( shared_object_id, user_id, access_level ) " +
				"VALUES ( ?, ?, ? )";
		try {
			pstmt = dbConnection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getSharedObjectId() );
			counter++;
			pstmt.setInt( counter, item.getUserId() );
			counter++;
			pstmt.setInt( counter, item.getAccessLevel() );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to insert AuthSharedObjectUsersDTO, sql: " + sql;
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
	 * Update access_level = ?
	 * @param item
	 * @throws Exception
	 */
	public void updateUserAccessLevel( AuthSharedObjectUsersDTO item ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = AuthLibraryDBConnectionFactory.getConnection(  );
			updateUserAccessLevel( item, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * Update access_level = ?
	 * @param item
	 * @throws Exception
	 */
	public void updateUserAccessLevel( AuthSharedObjectUsersDTO item, Connection dbConnection  ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE auth_shared_object_users SET access_level = ? WHERE shared_object_id = ? AND user_id = ?";
		try {
			pstmt = dbConnection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getAccessLevel() );
			counter++;
			pstmt.setInt( counter, item.getSharedObjectId() );
			counter++;
			pstmt.setInt( counter, item.getUserId() );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update used_date, sql: " + sql;
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
	 * delete using primary key  shared_object_id = ? AND user_id = ?
	 * @param item
	 * @throws Exception
	 */
	public void delete( AuthSharedObjectUsersDTO item ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = AuthLibraryDBConnectionFactory.getConnection(  );
			delete( item, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * delete using primary key  shared_object_id = ? AND user_id = ?
	 * @param item
	 * @throws Exception
	 */
	public void delete( AuthSharedObjectUsersDTO item, Connection dbConnection  ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "DELETE FROM auth_shared_object_users WHERE shared_object_id = ? AND user_id = ?";
		try {
			pstmt = dbConnection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getSharedObjectId() );
			counter++;
			pstmt.setInt( counter, item.getUserId() );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to delete, sql: " + sql;
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
}
