package org.yeastrc.xlink.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchFileDTO;

/**
 * search_file table
 */
public class SearchFileDAO {
	
	private static final Logger log = LoggerFactory.getLogger( SearchFileDAO.class);

	private SearchFileDAO() { }
	public static SearchFileDAO getInstance() { return new SearchFileDAO(); }
	


//		CREATE TABLE search_file (
//		  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//		  search_id INT(10) UNSIGNED NOT NULL,
//		  filename VARCHAR(255) NOT NULL,
//		  path VARCHAR(2000) NULL DEFAULT NULL,
//		  filesize INT(11) NOT NULL,
//		  mime_type VARCHAR(500) NULL DEFAULT NULL,
//		  description VARCHAR(2500) NULL DEFAULT NULL,
//		  upload_date DATETIME NOT NULL,
//		  file_contents LONGBLOB NULL DEFAULT NULL,
//  		display_filename` VARCHAR(255)



	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public List<SearchFileDTO> getSearchFileDTOForSearchId( int searchId ) throws Exception {
		
		 List<SearchFileDTO>  resultList = new ArrayList<SearchFileDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		
		
		String sql = "SELECT id, search_id, display_filename, filename, path, filesize, mime_type, description, upload_date "
				+ " FROM search_file  WHERE search_id = ?";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1, searchId );
			
			rs = pstmt.executeQuery();
			
			while ( rs.next() ) {
				
				SearchFileDTO item = getFromResultSet( rs );
				resultList.add( item );
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

		return resultList;
	}
	


	/**
	 * @param id
	 * @return null if not found
	 * @throws Exception
	 */
	public SearchFileDTO getSearchFileDTOForId( int id ) throws Exception {
		
		 SearchFileDTO  result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		String sql = "SELECT id, search_id, display_filename, filename, path, filesize, mime_type, description, upload_date "
				+ " FROM search_file  WHERE id = ?";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				
				result = getFromResultSet( rs );
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
	 * @return null if not found
	 * @throws Exception
	 */
	public Integer getSearchIdForId( int id ) throws Exception {
		
		Integer  result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		String sql = "SELECT search_id "
				+ " FROM search_file  WHERE id = ?";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				
				result = rs.getInt( "search_id" );
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
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private SearchFileDTO getFromResultSet( ResultSet rs ) throws SQLException {
		
		SearchFileDTO item = new SearchFileDTO();

		item.setId( rs.getInt( "id" ) );
		item.setSearchId( rs.getInt( "search_id" ) );
		item.setDisplayFilename( rs.getString( "display_filename" ) );
		item.setFilename( rs.getString( "filename" ) );
		item.setPath( rs.getString( "path" ) );
		item.setFileSize( rs.getLong( "filesize" ) );
		item.setMimeType( rs.getString( "mime_type" ) );
		item.setDescription( rs.getString( "description" ) );
		item.setUploadDate( rs.getDate( "upload_date" ) );


		return item;
	}
	
	
	
	/**
	 * Get the file_contents for the id
	 * @param id
	 * @return null if record not found for id
	 * @throws Exception
	 */
	public byte[] getDataFileData( int id ) throws Exception {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		byte[] datafiledata = null;
		

		String sql = "SELECT file_contents FROM search_file WHERE id = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, id );
			rs = stmt.executeQuery();
			
			if ( rs.next() ) {
			
				datafiledata = rs.getBytes( "file_contents" );
			}
			
			rs.close(); rs = null;
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
		} catch ( Exception e ) {
			
			String msg = "ERROR: database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\nsql: " + sql;
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			if (rs != null) {
				try { rs.close(); rs = null; } catch (Exception e) { ; }
			}

			if (stmt != null) {
				try { stmt.close(); stmt = null; } catch (Exception e) { ; }
			}
			
			if (conn != null) {
				try { conn.close(); conn = null; } catch (Exception e) { ; }
			}			
		}
		
		return datafiledata;
	}
	
	
//	CREATE TABLE search_file (
//	  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//	  search_id INT(10) UNSIGNED NOT NULL,
//	  filename VARCHAR(255) NOT NULL,
//	  path VARCHAR(2000) NULL DEFAULT NULL,
//	  filesize INT(11) NOT NULL,
//	  mime_type VARCHAR(500) NULL DEFAULT NULL,
//	  description VARCHAR(2500) NULL DEFAULT NULL,
//	  upload_date DATETIME NOT NULL,
//	  file_contents LONGBLOB NULL DEFAULT NULL,
//  	display_filename` VARCHAR(255)

	
	

	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( SearchFileDTO item ) throws Exception {
		
		Connection conn = null;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			save( item, conn );

		} catch ( Exception e ) {
			
			String msg = "ERROR inserting item. Error getting database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n item: " + item;
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
	}
	
	

	private final String INSERT_SQL = 
			"INSERT INTO search_file ( search_id, filename, path, filesize, mime_type, description, upload_date ) "
			+ "VALUES ( ?, ?, ?, ?, ?, ?, NOW() )";
	
	
	
	/**
	 * @param item
	 * @param conn
	 * @throws Exception
	 */
	public void save( SearchFileDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = INSERT_SQL;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setString( counter, item.getFilename() );
			counter++;
			pstmt.setString( counter, item.getPath() );
			counter++;
			pstmt.setLong( counter, item.getFileSize() );
			counter++;
			pstmt.setString( counter, item.getMimeType() );
			counter++;
			pstmt.setString( counter, item.getDescription() );

//			counter++;
//			pstmt.setDate( counter, item.getUploadDate(). );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert item" );
			
		} catch ( Exception e ) {
			
			String msg = "ERROR inserting item. database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n item: " + item
					+ "\nsql: " + sql;
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
		
	}
	
	

	/**
	 * @param item
	 * @throws Exception
	 */
	public void saveData( int id, byte[] fileContents ) throws Exception {
		
		Connection conn = null;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			saveData( id, fileContents, conn );

		} catch ( Exception e ) {
			
			String msg = "ERROR saveData. Error getting database connection: '" + DBConnectionFactory.PROXL + "'";
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
	}

	/**
	 * Save the fileContents to the record in file_and_contents with the provided id
	 * @param id
	 * @param data
	 * @throws Exception
	 */
	public void saveData( int id, byte[] fileContents, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement stmt = null;

		String sql = "UPDATE search_file SET file_contents = ? WHERE id = ?";

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

		
			stmt = conn.prepareStatement( sql );
			stmt.setBytes( 1, fileContents );
			stmt.setInt( 2, id );

			stmt.executeUpdate();
			
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
		} catch ( Exception e ) {
			
			String msg = "ERROR saveData. database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\nsql: " + sql;
			log.error( msg, e );
			
			throw e;
			
		} finally {

			if (stmt != null) {
				try { stmt.close(); stmt = null; } catch (Exception e) { ; }
			}
			
//			if (conn != null) {
//				try { conn.close(); conn = null; } catch (Exception e) { ; }
//			}			
		}
		
	}


	/**
	 * Save the contents of the input stream "is" to the record in file_and_contents with the provided id
	 * @param id
	 * @param fileSize - size of data in stream "is"
	 * @param is
	 * @throws Exception
	 */
	public void saveData( int id, int fileSize, InputStream is, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement stmt = null;
		

		String sql = "UPDATE search_file SET file_contents = ? WHERE id = ?";
		
		
		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			stmt = conn.prepareStatement( sql );
			stmt.setBinaryStream( 1, is, fileSize);
			stmt.setInt( 2, id );

			stmt.execute();
			
			stmt.close(); stmt = null;
			conn.close(); conn = null;
			
		} catch ( Exception e ) {
			
			String msg = "ERROR inserting item. database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\nsql: " + sql;
			log.error( msg, e );
			
			throw e;
			
		} finally {

			if (stmt != null) {
				try { stmt.close(); stmt = null; } catch (Exception e) { ; }
			}
			
//			if (conn != null) {
//				try { conn.close(); conn = null; } catch (Exception e) { ; }
//			}			
		}
		
	}
	
	


	private static String updateDisplayFilenameSQL = "UPDATE search_file " 
			+ " SET display_filename = ? WHERE id = ?";

	/**
	 * @param displayFilename
	 * @param id
	 * @return number of rows updated
	 * @throws Exception
	 */
	public int updateDisplayFilename( String displayFilename, int id ) throws Exception {
		
		int rowsUpdated = 0;
		
		Connection connection = null;

		PreparedStatement pstmt = null;

		String sql = updateDisplayFilenameSQL;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = connection.prepareStatement( sql );

			int counter = 0;

			counter++;
			pstmt.setString( counter, displayFilename );

			counter++;
			pstmt.setInt( counter, id );

			rowsUpdated = pstmt.executeUpdate();

			if ( rowsUpdated == 0 ) {

			}


		} catch (Exception sqlEx) {
			log.error("updateDisplayFilename: Exception '" + sqlEx.toString() + ".\nSQL = " + sql , sqlEx);
			throw sqlEx;

		} finally {


			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}

		return rowsUpdated;

	}
	
	
	

	private static String updateFilesizeSQL = "UPDATE search_file " 
			+ " SET filesize = ? WHERE id = ?";

	/**
	 * @param filesize
	 * @param id
	 * @return number of rows updated
	 * @throws Exception
	 */
	public int updateFilesize( int filesize, int id ) throws Exception {
		
		int rowsUpdated = 0;
		
		Connection connection = null;

		PreparedStatement pstmt = null;

		String sql = updateFilesizeSQL;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = connection.prepareStatement( sql );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, filesize );

			counter++;
			pstmt.setInt( counter, id );

			rowsUpdated = pstmt.executeUpdate();

			if ( rowsUpdated == 0 ) {

			}


		} catch (Exception sqlEx) {
			log.error("updateFilesize: Exception '" + sqlEx.toString() + ".\nSQL = " + sql , sqlEx);
			throw sqlEx;

		} finally {


			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}

		return rowsUpdated;

	}
	
	

	private static String updateFilenameSQL = "UPDATE search_file " 
			+ " SET filename = ? WHERE id = ?";

	/**
	 * @param filename
	 * @param id
	 * @return number of rows updated
	 * @throws Exception
	 */
	public int updateFilename( String filename, int id ) throws Exception {
		
		int rowsUpdated = 0;
		
		Connection connection = null;

		PreparedStatement pstmt = null;

		String sql = updateFilenameSQL;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = connection.prepareStatement( sql );

			int counter = 0;

			counter++;
			pstmt.setString( counter, filename );

			counter++;
			pstmt.setInt( counter, id );

			rowsUpdated = pstmt.executeUpdate();

			if ( rowsUpdated == 0 ) {

			}


		} catch (Exception sqlEx) {
			log.error("updateFilesize: Exception '" + sqlEx.toString() + ".\nSQL = " + sql , sqlEx);
			throw sqlEx;

		} finally {


			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}

		return rowsUpdated;

	}
	
	
}


//		CREATE TABLE search_file (
//		id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//		search_id INT(10) UNSIGNED NOT NULL,
//		filename VARCHAR(255) NOT NULL,
//		path VARCHAR(2000) NULL DEFAULT NULL,
//		filesize INT(11) NOT NULL,
//		mime_type VARCHAR(500) NULL DEFAULT NULL,
//		description VARCHAR(2500) NULL DEFAULT NULL,
//		upload_date DATETIME NOT NULL,
//		file_contents LONGBLOB NULL DEFAULT NULL,
//      display_filename` VARCHAR(255)
