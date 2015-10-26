package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class PsmCountForSearchIdReportedPeptideIdSearcher {

	private PsmCountForSearchIdReportedPeptideIdSearcher() { }
	public static PsmCountForSearchIdReportedPeptideIdSearcher getInstance() { return new PsmCountForSearchIdReportedPeptideIdSearcher(); }

	private static final Logger log = Logger.getLogger(PsmCountForSearchIdReportedPeptideIdSearcher.class);
	

	
	private static final String SQL = 
			"SELECT COUNT(ID) AS count FROM psm WHERE search_id = ? "
					+ "  AND reported_peptide_id = ? "
					+ "  AND q_value <= ? "
					;

			
			
	
	/**
	 * @param reportedPeptideId
	 * @param searchId
	 * @param psmQValueCutoff
	 * @return
	 * @throws Exception
	 */
	public int getPsmCountForSearchIdReportedPeptideId( int reportedPeptideId, int searchId, double psmQValueCutoff ) throws Exception {
		
		int numPsms = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = SQL;
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			
			counter++;
			pstmt.setInt( counter, searchId );
		
			counter++;
			pstmt.setInt( counter, reportedPeptideId );
			
			counter++;
			pstmt.setDouble( counter, psmQValueCutoff );
			
			
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				numPsms = rs.getInt( "count" );
			}


		} catch ( Exception e ) {

			log.error( "ERROR:  SQL: " + sql, e );

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
		
		return numPsms;		
	}
	
}
