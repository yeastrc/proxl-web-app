package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class GetHasMonolinksForPsmIdSearcher {
	
	private static final Logger log = Logger.getLogger(GetHasMonolinksForPsmIdSearcher.class);
	
	private GetHasMonolinksForPsmIdSearcher() { }
	private static final GetHasMonolinksForPsmIdSearcher _INSTANCE = new GetHasMonolinksForPsmIdSearcher();
	public static GetHasMonolinksForPsmIdSearcher getInstance() { return _INSTANCE; }
	
	

	/**
	 * @param psmId
	 * @return true if any monolink records found for psmId
	 * @throws Exception
	 */
	public boolean getHasMonolinksForPsmId( int psmId  ) throws Exception {
		
		boolean result = false;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT psm.id FROM psm "
			+ " INNER JOIN monolink ON psm.id = monolink.psm_id "

			+ " WHERE psm.id = ?  LIMIT 1";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {

				result = true;
			}
			
		} catch ( Exception e ) {
			
			String msg = "getHasMonolinksForPsmId(), sql: " + sql;
			
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
