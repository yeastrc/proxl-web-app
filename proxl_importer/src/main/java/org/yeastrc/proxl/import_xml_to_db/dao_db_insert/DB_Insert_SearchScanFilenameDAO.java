package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchScanFilenameDTO;

/**
 * table search_scan_filename
 *
 */
public class DB_Insert_SearchScanFilenameDAO {

	private static final Logger log = Logger.getLogger(DB_Insert_SearchScanFilenameDAO.class);

	private DB_Insert_SearchScanFilenameDAO() { }
	public static DB_Insert_SearchScanFilenameDAO getInstance() { return new DB_Insert_SearchScanFilenameDAO(); }


	/**
	 * @param item
	 * @return
	 * @throws Throwable
	 */
	public void saveToDatabase(SearchScanFilenameDTO item ) throws Exception {

		Connection connection = null;

		try {


			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			saveToDatabase( item, connection );
			
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
	
	private static final String INSERT_SQL =
			
			"INSERT INTO search_scan_filename "
			+ "( search_id, filename, scan_file_id ) "
			+ "VALUES ( ?, ?, ? )";
	
	/**
	 * @param item
	 * @param conn
	 * @throws Exception
	 */
	public void saveToDatabase( SearchScanFilenameDTO item, Connection conn ) throws Exception {
		
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
			if ( item.getScanFileId() != null ) {
				pstmt.setInt( counter, item.getScanFileId() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert record..." );
			
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
	}
}
