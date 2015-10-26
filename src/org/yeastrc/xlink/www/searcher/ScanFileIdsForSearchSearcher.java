package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**

 *
 */
public class ScanFileIdsForSearchSearcher {

	private static final Log log = LogFactory.getLog(ScanFileIdsForSearchSearcher.class);
	
	private ScanFileIdsForSearchSearcher() { }
	private static final ScanFileIdsForSearchSearcher _INSTANCE = new ScanFileIdsForSearchSearcher();
	public static ScanFileIdsForSearchSearcher getInstance() { return _INSTANCE; }
	
	
	
	
	

	

	/**
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getScanFileIdsForSearchId( int searchId ) throws Exception {
		
		
		List<Integer> scanFileIds = new ArrayList<Integer>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT DISTINCT scan.scan_file_id FROM psm "

			+ " INNER JOIN scan ON psm.scan_id = scan.id   "

			+ " WHERE psm.search_id = ? "

			+ " ORDER BY scan.scan_file_id ";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, searchId );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				scanFileIds.add( rs.getInt( "scan_file_id" ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getScanFileIdsForSearchId(), sql: " + sql;
			
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
		
		
		
		return scanFileIds;
	}
	
	
	
	
}
