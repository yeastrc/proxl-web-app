package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchScanFilenameDTO;

/**
 * table search_scan_filename
 *
 */
public class SearchScanFilenameDAO {

	private static final Logger log = Logger.getLogger(SearchScanFilenameDAO.class);
	private SearchScanFilenameDAO() { }
	public static SearchScanFilenameDAO getInstance() { return new SearchScanFilenameDAO(); }

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SearchScanFilenameDTO getSearchScanFilenameDTO( int id ) throws Exception {
		SearchScanFilenameDTO result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM search_scan_filename WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = populateFromResultSet( rs );
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
	public SearchScanFilenameDTO populateFromResultSet( ResultSet rs) throws SQLException {
		SearchScanFilenameDTO result = new SearchScanFilenameDTO();
		result.setId( rs.getInt( "id" ) );
		result.setSearchId( rs.getInt( "search_id" ) );
		result.setFilename( rs.getString( "filename" ) );
		
		int scanFileId = rs.getInt( "scan_file_id" );
		if ( ! rs.wasNull() ) {
			result.setScanFileId( scanFileId );
		}
		return result;
	}
}
