package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.KojakFileDTO;

/**
 * Table kojak_file
 *
 */
public class KojakFileDAO {

	private static final Logger log = Logger.getLogger(KojakFileDAO.class);

	private KojakFileDAO() { }
	public static KojakFileDAO getInstance() { return new KojakFileDAO(); }
	
	


	/**
	 * @param filename
	 * @param sha1Sum
	 * @return
	 * @throws Exception
	 */
	public List<KojakFileDTO> getKojakFileDTOListByFilenameSha1Sum( String filename, String sha1Sum ) throws Exception {
		
		List<KojakFileDTO> resultList = null;
		
		
		Connection dbConnection = null;
		
		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			resultList =  getKojakFileDTOListByFilenameSha1Sum( filename, sha1Sum, dbConnection );
					
		} finally {
			
			// be sure database handles are closed
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
		return resultList;
	}

	/**
	 * @param filename
	 * @param sha1Sum
	 * @return
	 * @throws Exception
	 */
	public List<KojakFileDTO> getKojakFileDTOListByFilenameSha1Sum( String filename, String sha1Sum, Connection dbConnection ) throws Exception {
		
		
		List<KojakFileDTO> resultList = new ArrayList<KojakFileDTO>();
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		final String sql = "SELECT * FROM kojak_file WHERE filename = ? AND sha1sum = ?";

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setString( counter, filename );
			counter++;
			pstmt.setString( counter, sha1Sum );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				KojakFileDTO item = populateFromResultSet(rs);
				
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
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
		}
		
		
		return resultList;
	}
	

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private KojakFileDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		KojakFileDTO result = new KojakFileDTO();
		
		result.setId( rs.getInt( "id" ) );
		result.setFilename( rs.getString( "filename" ) );
//		result.setFilenameForPercolator( rs.getString( "filename_for_percolator" ) );
		result.setPath( rs.getString( "path" ) );
		result.setSha1sum( rs.getString( "sha1sum" ) );
		result.setKojakProgramVersion( rs.getString( "kojak_program_version" ) );
		return result;
	}
	
	

	private static String insertSQL = "INSERT INTO kojak_file " 
			+ " ( filename, path, sha1sum, kojak_program_version )"
			+ " VALUES ( ?, ?, ?, ? )";


//	private static String insertSQL = "INSERT INTO kojak_file " 
//			+ " ( filename, filename_for_percolator, path, sha1sum, kojak_program_version )"
//			+ " VALUES ( ?, ?, ?, ?, ? )";

//	CREATE TABLE scan_file (
//	  id int(10) unsigned NOT NULL AUTO_INCREMENT,
//	  filename varchar(255) NOT NULL,
//	  filename_for_percolator VARCHAR(255) NULL,
//	  path varchar(2000) DEFAULT NULL,
//	  sha1sum varchar(255) DEFAULT NULL,
//	  kojak_program_version VARCHAR(255) NOT NULL

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public int save( KojakFileDTO item, Connection connection ) throws Exception {

//		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {


//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmt.setString( counter, item.getFilename() );
//			counter++;
//			pstmt.setString( counter, item.getFilenameForPercolator());
			counter++;
			pstmt.setString( counter, item.getPath() );
			counter++;
			pstmt.setString( counter, item.getSha1sum() );
			counter++;
			pstmt.setString( counter, item.getKojakProgramVersion() );

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
