package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.utils.XLinkUtils;

public class SearchReportedPeptideLinkTypeSearcher {
	
	private static final Logger log = LoggerFactory.getLogger( SearchReportedPeptideLinkTypeSearcher.class);
	private SearchReportedPeptideLinkTypeSearcher() { }
	private static final SearchReportedPeptideLinkTypeSearcher _INSTANCE = new SearchReportedPeptideLinkTypeSearcher();
	public static SearchReportedPeptideLinkTypeSearcher getInstance() { return _INSTANCE; }
	
	private static final String sql = 
			"SELECT link_type "
			+ " FROM search_reported_peptide  "
			+ " WHERE search_id = ? AND reported_peptide_id = ? ";
	/**
	 * Get the link type from the search_reported_peptide record
	 * 
	 * @param searchId
	 * @param reportedPeptideId
	 * @return null if none found
	 * @throws Exception
	 */
	public Integer getSearchReportedPeptideLinkTypeNumber( int searchId, int reportedPeptideId  ) throws Exception {
		Integer linkTypeNumber = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, reportedPeptideId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				String linkTypeString = rs.getString( "link_type" );
				linkTypeNumber = XLinkUtils.getTypeNumber( linkTypeString );
			}
		} catch ( Exception e ) {
			String msg = "Exception in getSearchReportedPeptideLinkTypeNumber( ... ): sql: " + sql;
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
		return linkTypeNumber;
	}
}
