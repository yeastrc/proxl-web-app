package org.yeastrc.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.auth.db.AuthLibraryDBConnectionFactory;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;

/**
 * Table auth_user
 */
public class AuthUserDAO implements Runnable {

	private static final Logger log = LoggerFactory.getLogger( AuthUserDAO.class);
	private AuthUserDAO() { }
	public static AuthUserDAO getInstance() { return new AuthUserDAO(); }
	/**
	 * Separate since always return a new instance
	 * @return
	 */
	public static AuthUserDAO getNewInstance() { return new AuthUserDAO(); }
	
	/**
	 * Which method on this class to execute on this class with property authUserDTO_ToUseInRun
	 * when run() is called when this is used as Runnable 
	 *
	 */
	public enum MethodToExecuteAsRunnable { UPDATE_LAST_LOGIN }

	private AuthUserDTO authUserDTO_ToUseInRun;
	private MethodToExecuteAsRunnable methodToExecuteAsRunnable;
	
	/* 
	 * Called when AuthUserDAO object passed as Runnable
	 */
	@Override
	public void run() {
		try {
			if ( methodToExecuteAsRunnable == MethodToExecuteAsRunnable.UPDATE_LAST_LOGIN ) {
				updateLastLogin( authUserDTO_ToUseInRun );
			} else {
				throw new ProxlWebappInternalErrorException( "methodToExecuteAsRunnable is not a valid value: " + methodToExecuteAsRunnable );
			}
		} catch ( Throwable t ) {
			String msg = "Exception caught in run(): methodToExecuteAsRunnable: " + methodToExecuteAsRunnable
					+ ", authUserDTO_ToUseInRun: " + authUserDTO_ToUseInRun;
			log.error( msg, t );
			throw new RuntimeException( msg, t );
		}
	}

	/**
	 * @param id
	 * @return null if not found
	 * @throws Exception 
	 */
	public AuthUserDTO getForId( int id ) throws Exception {
		
		AuthUserDTO result = null;

		final String querySQL = "SELECT user_mgmt_user_id, user_access_level, enabled_app_specific FROM auth_user WHERE id = ?";
		
		try ( Connection dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			     PreparedStatement preparedStatement = dbConnection.prepareStatement( querySQL ) ) {
			
			preparedStatement.setInt( 1, id );
			
			try ( ResultSet rs = preparedStatement.executeQuery() ) {
				if ( rs.next() ) {
					result = new AuthUserDTO();
					result.setId( id );
					result.setUserMgmtUserId( rs.getInt( "user_mgmt_user_id" ) );
					int userAccessLevel = rs.getInt( "user_access_level" );
					if ( ! rs.wasNull() ) {
						result.setUserAccessLevel( userAccessLevel );
					}
					int enabledInt = rs.getInt( "enabled_app_specific" );
					if ( enabledInt == Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE ) {
						result.setEnabledAppSpecific( true );
					} else {
						result.setEnabledAppSpecific( false );
					}
				}
			}
		} catch ( RuntimeException e ) {
			String msg = "SQL: " + querySQL;
			log.error( msg, e );
			throw e;
		} catch ( SQLException e ) {
			String msg = "SQL: " + querySQL;
			log.error( msg, e );
			throw e;
		}
		
		return result;
	}
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Integer getIdForId( int id ) throws Exception {
		Integer returnedItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT id FROM auth_user WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnedItem = rs.getInt( "id" );
			}
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
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
		return returnedItem;
	}
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Integer getUserMgmtUserIdForId( int id ) throws Exception {
		Integer returnedItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT user_mgmt_user_id FROM auth_user WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnedItem = rs.getInt( "user_mgmt_user_id" );
			}
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
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
		return returnedItem;
	}
	
	/**
	 * @param userMgmtUserId
	 * @return
	 * @throws Exception
	 */
	public Integer getIdForUserMgmtUserId( int userMgmtUserId ) throws Exception {
		Integer returnedItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT id FROM auth_user WHERE user_mgmt_user_id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, userMgmtUserId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnedItem = rs.getInt( "id" );
			}
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
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
		return returnedItem;
	}
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Integer getUserAccessLevel( int id ) throws Exception {
		Integer returnedItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT user_access_level FROM auth_user WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnedItem = rs.getInt( "user_access_level" );
				if ( rs.wasNull() ) {
					returnedItem = null;
				}
			}
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
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
		return returnedItem;
	}
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Boolean getUserEnabledAppSpecific( int id ) throws Exception {
		Boolean returnedItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT enabled_app_specific FROM auth_user WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				int enabledInt = rs.getInt( "enabled_app_specific" );
				if ( enabledInt == Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE ) {
					returnedItem = true;
				} else {
					returnedItem = false;
				}
			}
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
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
		return returnedItem;
	}
	
	/**
	 * Change to save id passed in
	 * @param item
	 * @param dbConnection
	 * @throws Exception
	 */
	public void save( AuthUserDTO item, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "INSERT INTO auth_user (user_mgmt_user_id, user_access_level) " +
				"VALUES ( ?, ? )";
		try {
			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
//			pstmt = dbConnection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getUserMgmtUserId() );
			counter++;
			if ( item.getUserAccessLevel() != null ) {
				pstmt.setInt( counter, item.getUserAccessLevel() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				throw new Exception( "Failed to insert AuthUserDTO" );
			}
		} catch ( Exception e ) {
			String msg = "Failed to insert AuthUserDTO, sql: " + sql;
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
	 * Update user_access_level = ? 
	 * @param id
	 * @param user_access_level
	 * @throws Exception
	 */
	public void updateUserAccessLevel( int id, Integer user_access_level ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE auth_user SET user_access_level = ? WHERE id = ?";
		try {
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			if ( user_access_level == null ) {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			} else {
				pstmt.setInt( counter, user_access_level );
			}
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update user_access_level, sql: " + sql;
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
	 * Update enabled_app_specific = ? 
	 * @param id
	 * @param enabled_app_specific
	 * @throws Exception
	 */
	public void updateEnabledAppSpecific( int id, boolean enabled_app_specific ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE auth_user SET enabled_app_specific = ? WHERE id = ?";
		try {
			conn = AuthLibraryDBConnectionFactory.getConnection(  );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			int enabled_app_specificInt = Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE;
			if ( enabled_app_specific ) {
				enabled_app_specificInt = Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE;
			}
			pstmt.setInt( counter, enabled_app_specificInt );
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update enabled_app_specific, sql: " + sql;
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
	 * @param authUserId
	 * @throws Exception
	 */
	public void updateLastLogin( AuthUserDTO item ) throws Exception {
		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE auth_user SET last_login = NOW(), last_login_ip = ? WHERE id = ?";
		try {
			dbConnection = AuthLibraryDBConnectionFactory.getConnection(  );
			pstmt = dbConnection.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setString( paramCounter, item.getLastLoginIP() );
			paramCounter++;
			pstmt.setInt( paramCounter, item.getId() );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			log.error( "ERROR: userId: " + item.getId() + ", sql: " + sql, e );
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
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	public AuthUserDTO getAuthUserDTO_ToUseInRun() {
		return authUserDTO_ToUseInRun;
	}
	public void setAuthUserDTO_ToUseInRun(AuthUserDTO authUserDTO_ToUseInRun) {
		this.authUserDTO_ToUseInRun = authUserDTO_ToUseInRun;
	}
	public MethodToExecuteAsRunnable getMethodToExecuteAsRunnable() {
		return methodToExecuteAsRunnable;
	}
	public void setMethodToExecuteAsRunnable(MethodToExecuteAsRunnable methodToExecuteAsRunnable) {
		this.methodToExecuteAsRunnable = methodToExecuteAsRunnable;
	}
}
