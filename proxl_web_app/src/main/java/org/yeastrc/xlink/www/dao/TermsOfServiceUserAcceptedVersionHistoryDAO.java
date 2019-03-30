package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.TermsOfServiceUserAcceptedVersionHistoryDTO;

/**
 * DAO for terms_of_service_user_accepted_version_history table
 *
 */
public class TermsOfServiceUserAcceptedVersionHistoryDAO {

	private static final Logger log = LoggerFactory.getLogger( TermsOfServiceUserAcceptedVersionHistoryDAO.class);
	//  private constructor
	private TermsOfServiceUserAcceptedVersionHistoryDAO() { }
	/**
	 * @return newly created instance
	 */
	public static TermsOfServiceUserAcceptedVersionHistoryDAO getInstance() { 
		return new TermsOfServiceUserAcceptedVersionHistoryDAO(); 
	}
	
	private static final String SQL_getForAuthUserIdTermsOfServiceVersionId =
			"SELECT accepted__date_time FROM terms_of_service_user_accepted_version_history "
					+ "WHERE auth_user_id = ? AND terms_of_service_version_id = ?";
	/**
	 * @param authUserId
	 * @param termsOfServiceVersionId
	 * @return null if not found
	 * @throws Exception
	 */
	public TermsOfServiceUserAcceptedVersionHistoryDTO getForAuthUserIdTermsOfServiceVersionId( 
			int authUserId,
			int termsOfServiceVersionId ) throws Exception {
		TermsOfServiceUserAcceptedVersionHistoryDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SQL_getForAuthUserIdTermsOfServiceVersionId;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, authUserId );
			pstmt.setInt( 2, termsOfServiceVersionId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = new TermsOfServiceUserAcceptedVersionHistoryDTO();
				returnItem.setAuthUserId( authUserId );
				returnItem.setTermsOfServiceVersionId( termsOfServiceVersionId );
				returnItem.setAcceptedDateTime( rs.getDate( "accepted__date_time" ) );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select TermsOfServiceUserAcceptedVersionHistoryDTO, "
					+ "auth_user_id: " + authUserId
					+ ", terms_of_service_version_id: " + termsOfServiceVersionId + ", sql: " + sql;
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
	 * @param item
	 * @throws Exception
	 */
	public void save( TermsOfServiceUserAcceptedVersionHistoryDTO item ) throws Exception {
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
	
	// Not using "INSERT IGNORE" since that also ignores failed inserts for missing foreign keys
	private static final String INSERT_SQL = "INSERT INTO terms_of_service_user_accepted_version_history "
			+ " (auth_user_id, terms_of_service_version_id, accepted__date_time) "
			+ " VALUES ( ?, ?, NOW() ) "
			+ " ON DUPLICATE KEY UPDATE accepted__date_time = now() ";
	/**
	 * @param item
	 * @param dbConnection
	 * @throws Exception
	 */
	public void save( TermsOfServiceUserAcceptedVersionHistoryDTO item, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = INSERT_SQL;
		try {
			pstmt = dbConnection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getAuthUserId() );
			counter++;
			pstmt.setInt( counter, item.getTermsOfServiceVersionId() );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to insert TermsOfServiceUserAcceptedVersionHistoryDTO, sql: " + sql;
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
