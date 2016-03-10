package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;




public class SearchPsmSearcher {

	private static final Logger log = Logger.getLogger(SearchPsmSearcher.class);
	
	private SearchPsmSearcher() { }
	private static final SearchPsmSearcher _INSTANCE = new SearchPsmSearcher();
	public static SearchPsmSearcher getInstance() { return _INSTANCE; }
	
	

	/**
	 * Get the psm id for any psm that matches the search criteria
	 * 
	 * @param searchId
	 * @param reportedPeptideId
	 * @return null if none found
	 * @throws Exception
	 */
	public Integer getSinglePsmId( int searchId, int reportedPeptideId  ) throws Exception {
		
		Integer singlePsmId = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = 
				"SELECT id "
				+ " FROM psm  "
				
				+ " WHERE search_id = ? AND reported_peptide_id = ? LIMIT 1 ";

		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, reportedPeptideId );

			rs = pstmt.executeQuery();
			
			if( rs.next() )
				singlePsmId = rs.getInt( "id" );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getSinglePsmIdInternal( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		return singlePsmId;
	}
}
