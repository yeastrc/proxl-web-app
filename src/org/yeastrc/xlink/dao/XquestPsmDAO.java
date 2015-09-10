package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.XquestPsmDTO;

/**
 * Table xquest_psm
 *
 */
public class XquestPsmDAO {

	private static final Logger log = Logger.getLogger(XquestPsmDAO.class);



	private XquestPsmDAO() { }
	public static XquestPsmDAO getInstance() { return new XquestPsmDAO(); }
//
//	/**
//	 * @param id
//	 * @return
//	 * @throws Exception
//	 */
//	public XquestPsmDTO getXquestPsmDTOById( int id ) throws Exception {
//		
//		
//		XquestPsmDTO result = null;
//		
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//
//		String sql = "SELECT * FROM xquest_psm WHERE id = ?";
//		
//		
//		try {
//			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//

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
//	private XquestPsmDTO populateFromResultSet(ResultSet rs) throws SQLException {
//		
//		XquestPsmDTO result = new XquestPsmDTO();
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
	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public int save(XquestPsmDTO item ) throws Exception {

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
	
	private static String insertSQL = "INSERT INTO xquest_psm "
			+ "( psm_id, xquest_file_id, type, scan_number, xquest_id, fdr, charge, seq1, seq2, xlinkposition ) "
			+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";

//	CREATE TABLE xquest_psm (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  psm_id INT UNSIGNED NOT NULL,
//			  xquest_file_id INT UNSIGNED NOT NULL,
//			  type VARCHAR(200) NULL,
//			  scan_number VARCHAR(45) NULL,
//			  xquest_id VARCHAR(2000) NULL,
//			  fdr VARCHAR(200) NULL,
//			  charge VARCHAR(200) NULL,
//			  seq1 VARCHAR(2000) NULL,
//			  seq2 VARCHAR(2000) NULL,
//			  xlinkposition VARCHAR(200) NULL,

	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public int save(XquestPsmDTO item, Connection connection ) throws Exception {

//		Connection connection = null;

		PreparedStatement pstmt = null;

		ResultSet rsGenKeys = null;

		try {


//			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = connection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmt.setInt( counter, item.getPsmId() );
			counter++;
			pstmt.setInt( counter, item.getXquestFileId() );

			counter++;
			pstmt.setString( counter, item.getType() );

			counter++;
			pstmt.setString( counter, item.getScanNumber() );
			counter++;
			pstmt.setString( counter, item.getXquestId() );
			counter++;
			pstmt.setString( counter, item.getFdr() );
			counter++;
			pstmt.setString( counter, item.getCharge() );
			counter++;
			pstmt.setString( counter, item.getSeq1() );
			counter++;
			pstmt.setString( counter, item.getSeq2() );
			counter++;
			pstmt.setString( counter, item.getXlinkposition() );

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
