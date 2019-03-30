package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.TermsOfServiceTextVersionsDTO;

/**
 * DAO for terms_of_service_text_versions table
 *
 */
public class TermsOfServiceTextVersionsDAO {

	private static final Logger log = LoggerFactory.getLogger( TermsOfServiceTextVersionsDAO.class);
	//  private constructor
	private TermsOfServiceTextVersionsDAO() { }
	/**
	 * @return newly created instance
	 */
	public static TermsOfServiceTextVersionsDAO getInstance() { 
		return new TermsOfServiceTextVersionsDAO(); 
	}
	
	/**
	 * @param versionId
	 * @return null if not found
	 * @throws Exception
	 */
	public TermsOfServiceTextVersionsDTO getForVersionId( int versionId ) throws Exception {
		TermsOfServiceTextVersionsDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM terms_of_service_text_versions WHERE version_id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, versionId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = populateFromResultSet( rs );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select TermsOfServiceTextVersionsDTO, version_id: " + versionId + ", sql: " + sql;
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
	 * @param idString
	 * @return null if not found
	 * @throws Exception
	 */
	public TermsOfServiceTextVersionsDTO getForIdString( String idString ) throws Exception {
		TermsOfServiceTextVersionsDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM terms_of_service_text_versions WHERE id_string = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, idString );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = populateFromResultSet( rs );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select TermsOfServiceTextVersionsDTO, id_string: " + idString + ", sql: " + sql;
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
	 * @param idString
	 * @return null if not found
	 * @throws Exception
	 */
	public Integer getVersionIdForIdString( String idString ) throws Exception {
		Integer returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT version_id FROM terms_of_service_text_versions WHERE id_string = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, idString );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = rs.getInt( "version_id" );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select TermsOfServiceTextVersionsDTO, id_string: " + idString + ", sql: " + sql;
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
	 * Get record with largest version_id
	 * @return null if no records
	 * @throws Exception
	 */
	public TermsOfServiceTextVersionsDTO getLatest(  ) throws Exception {
		TermsOfServiceTextVersionsDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM terms_of_service_text_versions ORDER BY version_id DESC LIMIT 1";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = populateFromResultSet(rs);
			}
		} catch ( Exception e ) {
			String msg = "Failed to select TermsOfServiceTextVersionsDTO, sql: " + sql;
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
	 * Get version_id value with largest version_id
	 * @return null if no records
	 * @throws Exception
	 */
	public Integer getLatestVersionId(  ) throws Exception {
		Integer returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT version_id FROM terms_of_service_text_versions ORDER BY version_id DESC LIMIT 1";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = rs.getInt( "version_id" );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select TermsOfServiceTextVersionsDTO, sql: " + sql;
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
	public TermsOfServiceTextVersionsDTO populateFromResultSet(ResultSet rs) throws SQLException {
		TermsOfServiceTextVersionsDTO returnItem = new TermsOfServiceTextVersionsDTO();
		returnItem.setVersionId( rs.getInt( "version_id" ) );
		returnItem.setIdString( rs.getString( "id_string" ) );
		returnItem.setTermsOfServiceText( rs.getString( "terms_of_service_text" ) );
		returnItem.setCreatedAuthUserId( rs.getInt( "created_auth_user_id" ) );
		returnItem.setCreatedDateTime( rs.getDate( "created_date_time" ) );
		return returnItem;
	}
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( TermsOfServiceTextVersionsDTO item ) throws Exception {
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
	
	/**
	 * @param item
	 * @param dbConnection
	 * @throws Exception
	 */
	public void save( TermsOfServiceTextVersionsDTO item, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "INSERT INTO terms_of_service_text_versions "
				+ "( id_string, terms_of_service_text, created_auth_user_id, created_date_time ) " +
				"VALUES ( ?, ?, ?, NOW() )";
		try {
			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
			counter++;
			pstmt.setString( counter, item.getIdString() );
			counter++;
			pstmt.setString( counter, item.getTermsOfServiceText() );
			counter++;
			pstmt.setInt( counter, item.getCreatedAuthUserId() );
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setVersionId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert TermsOfServiceTextVersionsDTO, missing autoincrement value" );
		} catch ( Exception e ) {
			String msg = "Failed to insert TermsOfServiceTextVersionsDTO, sql: " + sql;
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
