package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class TaxonomyNameSearcher {
	
	private static final Logger log = LoggerFactory.getLogger( TaxonomyNameSearcher.class);
	
	/**
	 * Get the name associated with the given NCBI taxonomy ID
	 * @return
	 */
	public static String getTaxonomyName( int id ) throws Exception {
		String name = "unknown";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT name FROM taxonomy WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() )
				name = rs.getString( 1 );
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
		return name;
	}
}
