package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
/**
 * 
 *
 */
public class Get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Searcher {
	
	private static final Logger log = Logger.getLogger(Get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Searcher.class);
	private Get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Searcher() { }
	private static final Get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Searcher _INSTANCE = new Get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Searcher();
	public static Get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_Searcher getInstance() { return _INSTANCE; }
	
	private static final String get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_SQL =
			"SELECT related_peptides_unique_for_search FROM unified_rp__search__rep_pept__generic_lookup "
			+ " WHERE search_id = ? AND reported_peptide_id = ?";
	/**
	 * @param searchId
	 * @param reportedPeptideId
	 * @return
	 * @throws Exception 
	 */
	public boolean get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId( int searchId, int reportedPeptideId ) throws Exception {
		boolean result = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId_SQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			paramCounter++;
			pstmt.setInt( paramCounter, reportedPeptideId );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				int related_peptides_unique_for_search = rs.getInt( "related_peptides_unique_for_search" );
				if ( related_peptides_unique_for_search == Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE ) {
					result = true;
				} else {
					result = false;
				}
			} else {
				String msg = "get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId(...)"
						+ "  No result record found."
						+ " searchId: " + searchId + ", reportedPeptideId:" + reportedPeptideId + ", sql: " + sql;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			if( rs.next() ) {
				String msg = "get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId(...)"
						+ "  Only one result record expected but got more than one result record."
						+ " searchId: " + searchId + ", reportedPeptideId:" + reportedPeptideId + ", sql: " + sql;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
		} catch ( Exception e ) {
			String msg = "Exception in get_related_peptides_unique_for_search_For_SearchId_ReportedPeptideId( SearchDTO search, ... ), sql: " + sql;
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
