package org.yeastrc.xlink.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;



import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Searches scan table
 * 
 *
 */
public class ScanIdFromScanFileIdScanNumberSearcher {

	private static final Logger log = Logger.getLogger(ScanIdFromScanFileIdScanNumberSearcher.class);
	
	private ScanIdFromScanFileIdScanNumberSearcher() { }
	private static final ScanIdFromScanFileIdScanNumberSearcher _INSTANCE = new ScanIdFromScanFileIdScanNumberSearcher();
	public static ScanIdFromScanFileIdScanNumberSearcher getInstance() { return _INSTANCE; }
	

	/**
	 * @param scan_file_id
	 * @param scan_number
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getScanIdFromScanFileIdScanNumber( int scan_file_id, int scan_number ) throws Exception {
		
		List<Integer> results = new ArrayList<Integer>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT id FROM scan"

			+ " WHERE scan_file_id = ? AND start_scan_number = ?"

			+ " ORDER BY id ";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, scan_file_id );
			pstmt.setInt( 2, scan_number );
			
			rs = pstmt.executeQuery();

			while( rs.next() )
				results.add( rs.getInt( 1 ) );
			
		} catch ( Exception e ) {
			
			String msg = "getSearchsForProjectId(), sql: " + sql;
			
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
		
		return results;
	}
	
	
	
	
	
	
	
}
