package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.PercolatorFileDTO;

/**
 * Table percolator_file
 *
 */
public class PercolatorFileDAO {

	private static final Logger log = Logger.getLogger(PercolatorFileDAO.class);

	private PercolatorFileDAO() { }
	public static PercolatorFileDAO getInstance() { return new PercolatorFileDAO(); }
	
	

	/**
	 * @param filename
	 * @param sha1Sum
	 * @return
	 * @throws Exception
	 */
	public List<PercolatorFileDTO> getPercolatorFileDTOListByFilenameSha1Sum( String filename, String sha1Sum ) throws Exception {
		
		
		List<PercolatorFileDTO> resultList = new ArrayList<PercolatorFileDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		final String sql = "SELECT * FROM percolator_file WHERE filename = ? AND sha1sum = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

		
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setString( counter, filename );
			counter++;
			pstmt.setString( counter, sha1Sum );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				PercolatorFileDTO item = populateFromResultSet(rs);
				
				resultList.add( item );
			}
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
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
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private PercolatorFileDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		PercolatorFileDTO result = new PercolatorFileDTO();
		
		result.setId( rs.getInt( "id" ) );
		result.setSearchId( rs.getInt( "search_id" ) );
		result.setFilename( rs.getString( "filename" ) );
		result.setPath( rs.getString( "path" ) );
		result.setSha1sum( rs.getString( "sha1sum" ) );
		return result;
	}
	
	
	


	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( PercolatorFileDTO item ) throws Exception {
		
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			save( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
	

	private static String insertSQL = "INSERT INTO percolator_file " 
			+ " (search_id, filename, path, sha1sum)"
			+ " VALUES ( ?, ?, ?, ? )";

	//CREATE TABLE percolator_file (
//	  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//	  search_id INT UNSIGNED NOT NULL,
//	  filename VARCHAR(255) NOT NULL,
//	  path VARCHAR(2000) NOT NULL,
//	  sha1sum VARCHAR(40) NOT NULL,

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public int save( PercolatorFileDTO item, Connection connection ) throws Exception {

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
			log.error("save:Exception '" + sqlEx.toString() + "."
					+ "\n PercolatorFileDTO item: " + item
					+ "\nSQL = " + insertSQL , sqlEx);
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
