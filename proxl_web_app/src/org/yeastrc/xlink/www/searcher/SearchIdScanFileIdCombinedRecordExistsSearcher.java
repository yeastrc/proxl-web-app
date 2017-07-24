package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Is there at least one scan/psm record where scan.scan_file_id = ? and psm.search_id = ?
 */
public class SearchIdScanFileIdCombinedRecordExistsSearcher {
	
	private static final Log log = LogFactory.getLog(SearchIdScanFileIdCombinedRecordExistsSearcher.class);
	private SearchIdScanFileIdCombinedRecordExistsSearcher() { }
	private static final SearchIdScanFileIdCombinedRecordExistsSearcher _INSTANCE = new SearchIdScanFileIdCombinedRecordExistsSearcher();
	public static SearchIdScanFileIdCombinedRecordExistsSearcher getInstance() { return _INSTANCE; }
	
	private static final String SQL = 
			"SELECT psm.id "
					+ " FROM psm "
					+ " INNER JOIN scan ON psm.scan_id = scan.id   "
					+ " WHERE psm.search_id = ? AND scan.scan_file_id = ? "
					+ " LIMIT 1 ";
	/**
	 * @param searchId
	 * @param scanFileId
	 * @return true if at least one scan/psm record exists where scan.scan_file_id = ? and psm.search_id = ?
	 * @throws Exception
	 */
	public boolean recordExistsForSearchIdScanFileIdCombined( int searchId, int scanFileId ) throws Exception {
		boolean result = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, scanFileId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = true;
			}
		} catch ( Exception e ) {
			String msg = "recordExistsForSearchIdScanFileIdCombined(), sql: " + sql;
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
		return result;
	}
}
