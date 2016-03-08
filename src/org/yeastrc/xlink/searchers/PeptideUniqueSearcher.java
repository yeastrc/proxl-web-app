package org.yeastrc.xlink.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

public class PeptideUniqueSearcher {

	private PeptideUniqueSearcher() { }
	private static final PeptideUniqueSearcher _INSTANCE = new PeptideUniqueSearcher();
	public static PeptideUniqueSearcher getInstance() { return _INSTANCE; }
	
	private static final Logger log = Logger.getLogger(PeptideUniqueSearcher.class);
			
	private static final String SQl =
			"SELECT peptide_id FROM nrseq_database_peptide_protein"
			+ " WHERE nrseq_database_id = ? AND peptide_id = ? AND is_unique = 'Y' LIMIT 1";
	
	
	/**
	 * @param peptideId
	 * @param databaseId
	 * @return
	 * @throws Exception
	 */
	public boolean isPeptideUniqueForDatabaseId( int peptideId, int databaseId ) throws Exception {
		
		
		boolean isUnique = false;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQl;
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
	
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, databaseId );
			pstmt.setInt( 2, peptideId );
			
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				isUnique = true;
			}

		} catch ( Exception e ) {

			log.error( "ERROR isPeptideUniqueForDatabaseId(...):  SQL: " + sql, e );

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
		
		return isUnique;
	}
	
}
