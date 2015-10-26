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
 * Searches kojak_file table
 * 
 *
 */
public class KojakFileSearcher {

	private static final Log log = LogFactory.getLog(KojakFileSearcher.class);
	
	private KojakFileSearcher() { }
	private static final KojakFileSearcher _INSTANCE = new KojakFileSearcher();
	public static KojakFileSearcher getInstance() { return _INSTANCE; }
	

	/**
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getKojakFileIdListForFilename( String kojakFilename ) throws Exception {
		
		List<Integer> results = new ArrayList<Integer>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT id FROM kojak_file"

			+ " WHERE filename = ? "

			+ " ORDER BY id ";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setString( 1, kojakFilename );
			
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
