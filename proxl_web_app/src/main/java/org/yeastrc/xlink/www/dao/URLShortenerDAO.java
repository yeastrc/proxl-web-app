package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.URLShortenerDTO;

/**
 * DAO for url_shortener table
 *
 */
public class URLShortenerDAO {
	
	private static final Logger log = Logger.getLogger(URLShortenerDAO.class);
	public static enum LogDuplicateSQLException{ TRUE, FALSE }
	//  private constructor
	private URLShortenerDAO() { }
	/**
	 * @return newly created instance
	 */
	public static URLShortenerDAO getInstance() { 
		return new URLShortenerDAO(); 
	}
	
	/**
	 * @param url
	 * @return null if not found
	 * @throws Exception
	 */
	public URLShortenerDTO getForURL( String url ) throws Exception {
		URLShortenerDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT id, shortened_url_key FROM url_shortener WHERE url = ? ORDER BY ID ";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, url );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = new URLShortenerDTO();
				returnItem.setId( rs.getInt( "id" ) );
				returnItem.setShortenedUrlKey( rs.getString( "shortened_url_key" ) );
				returnItem.setUrl( url );
			}
		} catch ( Exception e ) {
			String msg = "getForURL(...), url: " + url + ", sql: " + sql;
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
	 * @param shortenedUrlKey
	 * @return null if not found
	 * @throws Exception
	 */
	public URLShortenerDTO getForShortenedURLKey( String shortenedUrlKey ) throws Exception {
		URLShortenerDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT id, url FROM url_shortener WHERE shortened_url_key = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, shortenedUrlKey );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = new URLShortenerDTO();
				returnItem.setId( rs.getInt( "id" ) );
				returnItem.setShortenedUrlKey( shortenedUrlKey );
				returnItem.setUrl( rs.getString( "url" ) );
			}
		} catch ( Exception e ) {
			String msg = "getForShortenedURLKey(...), shortenedUrlKey: " + shortenedUrlKey + ", sql: " + sql;
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
	public void save( URLShortenerDTO item, LogDuplicateSQLException logDuplicateSQLException ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			save( item, logDuplicateSQLException, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	private final String INSERT_SQL = "INSERT INTO url_shortener "
			+ " (shortened_url_key, auth_user_id, url, date_record_created ) "
			+ " VALUES ( ?, ?, ?, NOW() ) ";
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( URLShortenerDTO item, LogDuplicateSQLException logDuplicateSQLException, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = INSERT_SQL;
		try {
			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
			counter++;
			pstmt.setString( counter, item.getShortenedUrlKey() );
			counter++;
			if ( item.getAuthUserId() != null ) {
				pstmt.setInt( counter, item.getAuthUserId() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}
			counter++;
			pstmt.setString( counter, item.getUrl() );
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				String msg = "Failed to insert URLShortenerDTO, generated key not found.";
				log.error( msg );
				throw new Exception( msg );
			}
		} catch ( SQLException sqlException ) {
			String exceptionMessage = sqlException.getMessage();
			if ( exceptionMessage != null && exceptionMessage.startsWith( "Duplicate entry" ) ) {
				if ( logDuplicateSQLException == LogDuplicateSQLException.TRUE ) {
					String msg = "Failed to insert URLShortenerDTO, sql: " + sql;
					log.error( msg, sqlException );
				}
			} else {
				String msg = "Failed to insert URLShortenerDTO, sql: " + sql;
				log.error( msg, sqlException );
			}			
			throw sqlException;
		} catch ( Exception e ) {
			String msg = "Failed to insert URLShortenerDTO, sql: " + sql;
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
	 * @param id
	 * @param url
	 * @throws Exception
	 */
	public void delete( int id ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "DELETE FROM url_shortener WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, id );
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
	}
}
