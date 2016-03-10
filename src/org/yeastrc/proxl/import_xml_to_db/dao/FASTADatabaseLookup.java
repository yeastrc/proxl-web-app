package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;


public class FASTADatabaseLookup {
	
	private static Logger log = Logger.getLogger(FASTADatabaseLookup.class);

	private static final FASTADatabaseLookup _INSTANCE = new FASTADatabaseLookup();
	private FASTADatabaseLookup() { }
	
	public static FASTADatabaseLookup getInstance() { return _INSTANCE; }
	
	public int lookupDatabase( String FASTAFilename ) throws Exception {
		int dbid = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		String sql = "SELECT id FROM tblDatabase WHERE name = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.YRC_NRSEQ );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, FASTAFilename );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				dbid = rs.getInt( 1 );
			} else {
				throw new Exception( "Could not find a database ID for " + FASTAFilename );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Error: sql: " + sql;
			log.error(msg, e);
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
		
		return dbid;
	}
	
}
