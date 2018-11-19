package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ScanFileDTO;

/**
 * Table scan_file
 *
 */
public class ScanFileDAO {

	private static final Logger log = Logger.getLogger(ScanFileDAO.class);

	private ScanFileDAO() { }
	public static ScanFileDAO getInstance() { return new ScanFileDAO(); }


	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getScanFilenameById( int id ) throws Exception {
		
		String result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT filename FROM scan_file WHERE id = ?";
		
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				result = rs.getString( "filename" );
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
		return result;
	}
	

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public String getSpectralStorageAPIKeyById( int id ) throws Exception {
		
		String result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT spectral_storage_api_key FROM scan_file WHERE id = ?";
		
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				result = rs.getString( "spectral_storage_api_key" );
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
		return result;
	}
	
	
	/**
	 * @param filename
	 * @param sha1Sum
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getScanFileIdListByFilenameSha1Sum( String filename, String sha1Sum ) throws Exception {

		Connection connection = null;
		try {
			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			return getScanFileIdListByFilenameSha1Sum( filename, sha1Sum, connection );
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}
	}

	/**
	 * @param filename
	 * @param sha1Sum
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getScanFileIdListByFilenameSha1Sum( String filename, String sha1Sum, Connection connection ) throws Exception {
		
		List<Integer> resultList = new ArrayList<>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT id FROM scan_file WHERE filename = ? AND sha1sum = ?";
		
		try {
			pstmt = connection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setString( counter, filename );
			counter++;
			pstmt.setString( counter, sha1Sum );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				resultList.add( rs.getInt( "id" ) );
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
		}
		return resultList;
	}

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public int save(ScanFileDTO item ) throws Exception {
		Connection connection = null;
		try {
			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			return save( item, connection );
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}
	}
	
	private static String insertSQL = "INSERT INTO scan_file ( filename, path, sha1sum, file_size )"
		+ " VALUES ( ?, ?, ?, ? )";

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public int save(ScanFileDTO item, Connection connection ) throws Exception {

//		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {
//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = connection.prepareStatement( insertSQL, Statement.RETURN_GENERATED_KEYS );

			int counter = 0;

			counter++;
			pstmt.setString( counter, item.getFilename() );
			counter++;
			pstmt.setString( counter, item.getPath() );
			counter++;
			pstmt.setString( counter, item.getSha1sum() );
			counter++;
			pstmt.setLong( counter, item.getFileSize() );

			int rowsUpdated = pstmt.executeUpdate();

			if ( rowsUpdated == 0 ) {

			}
			rsGenKeys = pstmt.getGeneratedKeys();
			if ( rsGenKeys.next() ) {
				item.setId( rsGenKeys.getInt( 1 ) );
			}
		} catch (Exception sqlEx) {
			log.error("save:Exception '" + sqlEx.toString() + ".\nSQL = " + insertSQL , sqlEx);
			throw sqlEx;
		} finally {
			if (rsGenKeys != null) {
				try {
					rsGenKeys.close();
				} catch (Exception ex) {
					// ignore
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}

//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (Exception ex) {
//					// ignore
//				}
//			}
		}

		return item.getId();
	}
	
	/**
	 * update the spectral_storage_process_key_temp in the database
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateSpectralStorageProcessKeyTemp( String spectralStorageProcessKeyTemp, int id ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		final String sql = "UPDATE scan_file SET spectral_storage_process_key_temp = ? WHERE id = ?";

		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, spectralStorageProcessKeyTemp);
			pstmt.setInt( 2, id );
			
//			int updatedRowCount = 
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			throw e;
		} finally {
			// be sure database handles are closed
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
	 * update the spectral_storage_api_key in the database
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateSpectralStorageAPIKey( String spectralStorageAPIKey, int id ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		final String sql = "UPDATE scan_file SET spectral_storage_api_key = ? WHERE id = ?";

		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, spectralStorageAPIKey);
			pstmt.setInt( 2, id );
			
//			int updatedRowCount = 
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			throw e;
		} finally {
			// be sure database handles are closed
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
