package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.ZzUserDataMirrorDTO;

/**
 * Table zz_user_data_mirror
 *
 */
public class ZzUserDataMirrorDAO {
	
	private static final Logger log = Logger.getLogger(ZzUserDataMirrorDAO.class);
	
	private ZzUserDataMirrorDAO() { }
	public static ZzUserDataMirrorDAO getInstance() { return new ZzUserDataMirrorDAO(); }
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ZzUserDataMirrorDTO item ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			save( item, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	private static final String INSERT_SQL = 
			"INSERT INTO zz_user_data_mirror ( auth_user_id, username, email, first_name, last_name, organization )"
			+ "VALUES ( ?, ?, ?, ?, ?, ? )"
					
			+ " ON DUPLICATE KEY UPDATE "
			+ " username = ?, email = ?, first_name = ?, last_name = ?, organization = ?";
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( ZzUserDataMirrorDTO item, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = INSERT_SQL;
		try {
			pstmt = dbConnection.prepareStatement( sql );
//			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
			//  Insert Values
			counter++;
			pstmt.setInt( counter, item.getAuthUserId() );
			counter++;
			pstmt.setString( counter, item.getUsername() );
			counter++;
			pstmt.setString( counter, item.getEmail() );
			counter++;
			pstmt.setString( counter, item.getFirstName() );
			counter++;
			pstmt.setString( counter, item.getLastName() );
			counter++;
			pstmt.setString( counter, item.getOrganization() );
			//  Update Values
			counter++;
			pstmt.setString( counter, item.getUsername() );
			counter++;
			pstmt.setString( counter, item.getEmail() );
			counter++;
			pstmt.setString( counter, item.getFirstName() );
			counter++;
			pstmt.setString( counter, item.getLastName() );
			counter++;
			pstmt.setString( counter, item.getOrganization() );
			
			pstmt.executeUpdate();
			
//			rs = pstmt.getGeneratedKeys();
//			if( rs.next() ) {
//				item.setId( rs.getInt( 1 ) );
//			} else {
//				String msg = "Failed to insert ZzUserDataMirrorDTO, generated key not found.";
//				log.error( msg );
//				throw new Exception( msg );
//			}
		} catch ( Exception e ) {
			String msg = "Failed to insert ZzUserDataMirrorDTO, sql: " + sql;
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
	 * Update the non-null item values associated with this auth_user_id
	 * @param item
	 * @throws Exception
	 */
	public void updateRecord( ZzUserDataMirrorDTO item ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		boolean firstField = true;
		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		sqlSB.append( "UPDATE zz_user_data_mirror SET " );
		
		if ( item.getUsername() != null ) {
			if ( firstField ) {
				firstField = false;
			} else {
				sqlSB.append( ", " );
			}
			sqlSB.append( " username = ? " );
		}

		if ( item.getEmail() != null ) {
			if ( firstField ) {
				firstField = false;
			} else {
				sqlSB.append( ", " );
			}
			sqlSB.append( " email = ? " );
		}

		if ( item.getFirstName() != null ) {
			if ( firstField ) {
				firstField = false;
			} else {
				sqlSB.append( ", " );
			}
			sqlSB.append( " first_name = ? " );
		}
		
		if ( item.getLastName() != null ) {
			if ( firstField ) {
				firstField = false;
			} else {
				sqlSB.append( ", " );
			}
			sqlSB.append( " last_name = ? " );
		}

		if ( item.getOrganization() != null ) {
			if ( firstField ) {
				firstField = false;
			} else {
				sqlSB.append( ", " );
			}
			sqlSB.append( " organization = ? " );
		}
		
		if ( firstField ) {
			String msg = "In updateRecord(...), At least one field must be not null. item.getAuthUserId(): " + item.getAuthUserId();
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		sqlSB.append( " WHERE auth_user_id = ? " );

		final String sql = sqlSB.toString();

		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			if ( item.getUsername() != null ) {
				counter++;
				pstmt.setString( counter, item.getUsername() );
			}
			if ( item.getEmail() != null ) {
				counter++;
				pstmt.setString( counter, item.getEmail() );
			}
			if ( item.getFirstName() != null ) {
				counter++;
				pstmt.setString( counter, item.getFirstName() );
			}
			if ( item.getLastName() != null ) {
				counter++;
				pstmt.setString( counter, item.getLastName() );
			}
			if ( item.getOrganization() != null ) {
				counter++;
				pstmt.setString( counter, item.getOrganization() );
			}
			
			counter++;
			pstmt.setInt( counter, item.getAuthUserId() );

			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			log.error( "In updateRecord(...) item.getAuthUserId(): " + item.getAuthUserId() + ", sql: " + sql, e );
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
