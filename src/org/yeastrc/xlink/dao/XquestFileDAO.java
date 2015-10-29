package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.XquestFileDTO;

/**
 * Table xquest_file
 *
 */
public class XquestFileDAO {

	private static final Logger log = Logger.getLogger(XquestFileDAO.class);



	private XquestFileDAO() { }
	public static XquestFileDAO getInstance() { return new XquestFileDAO(); }
//
//	/**
//	 * @param id
//	 * @return
//	 * @throws Exception
//	 */
//	public XquestFileDTO getXquestFileDTOById( int id ) throws Exception {
//		
//		
//		XquestFileDTO result = null;
//		
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//
//		String sql = "SELECT * FROM xquest_file WHERE id = ?";
//		
//		
//		try {
//			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//
////			CREATE TABLE xquest_file (
////			  id int(10) unsigned NOT NULL AUTO_INCREMENT,
////			  filename varchar(255) NOT NULL,
////			  path varchar(2000) DEFAULT NULL,
////			  sha1sum varchar(255) DEFAULT NULL,
//
//			pstmt = conn.prepareStatement( sql );
//			pstmt.setInt( 1, id );
//			
//			rs = pstmt.executeQuery();
//			
//			if( rs.next() ) {
//				result = populateFromResultSet(rs);
//			}
//			
//			
//		} catch ( Exception e ) {
//			
//			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//		
//		return result;
//	}
//
//
//
//	/**
//	 * @param rs
//	 * @return
//	 * @throws SQLException
//	 */
//	private XquestFileDTO populateFromResultSet(ResultSet rs) throws SQLException {
//		
//		XquestFileDTO result = new XquestFileDTO();
//		
//		result.setId( rs.getInt( "id" ) );
//		result.setFilename( rs.getString( "filename" ) );
//		result.setPath( rs.getString( "path" ) );
//		result.setSha1sum( rs.getString( "sha1sum" ) );
//		return result;
//	}
//	
//
//
//	/**
//	 * @param filename
//	 * @param sha1Sum
//	 * @return
//	 * @throws Exception
//	 */
//	public List<XquestFileDTO> getXquestFileDTOListByFilenameSha1Sum( String filename, String sha1Sum ) throws Exception {
//		
//		
//		List<XquestFileDTO> resultList = new ArrayList<XquestFileDTO>();
//		
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//
//		String sql = "SELECT * FROM xquest_file WHERE filename = ? AND sha1sum = ?";
//		
//		
//		try {
//			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//
////			CREATE TABLE xquest_file (
////			  id int(10) unsigned NOT NULL AUTO_INCREMENT,
////			  filename varchar(255) NOT NULL,
////			  path varchar(2000) DEFAULT NULL,
////			  sha1sum varchar(255) DEFAULT NULL,
//
//			pstmt = conn.prepareStatement( sql );
//			
//			int counter = 0;
//			
//			counter++;
//			pstmt.setString( counter, filename );
//			counter++;
//			pstmt.setString( counter, sha1Sum );
//			
//			rs = pstmt.executeQuery();
//			
//			if( rs.next() ) {
//				XquestFileDTO item = populateFromResultSet(rs);
//				
//				resultList.add( item );
//			}
//			
//		} catch ( Exception e ) {
//			
//			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//		
//		return resultList;
//	}
//	
	

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public int save(XquestFileDTO item ) throws Exception {

		Connection connection = null;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
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
	
	private static String insertSQL = "INSERT INTO xquest_file ( search_id, filename, path, sha1sum )"
		+ " VALUES ( ?, ?, ?, ? )";

//	CREATE TABLE xquest_file (
//	  id int(10) unsigned NOT NULL AUTO_INCREMENT,
//	  search_id INT UNSIGNED NOT NULL,
//	  filename varchar(255) NOT NULL,
//	  path varchar(2000) DEFAULT NULL,
//	  sha1sum varchar(255) DEFAULT NULL,


	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public int save(XquestFileDTO item, Connection connection ) throws Exception {

//		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {


//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setString( counter, item.getFilename() );
			counter++;
			pstmt.setString( counter, item.getPath() );
			counter++;
			pstmt.setString( counter, item.getSha1sum() );

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
}
