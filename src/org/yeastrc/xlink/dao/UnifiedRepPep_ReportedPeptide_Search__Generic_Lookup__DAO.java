package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 * 
 * table unified_rp__rep_pept__search__generic_lookup
 *
 */
public class UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO {
	
	private static final Logger log = Logger.getLogger(UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO.class);

	private UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO() { }
	public static UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO getInstance() { return new UnifiedRepPep_ReportedPeptide_Search__Generic_Lookup__DAO(); }
	
	


	/**
	 * Query on the 2 foreign keys
	 * 
	 * @param reportedPeptideId
	 * @param searchId
	 * @return psm.id if found, null otherwise
	 * @throws Exception
	 */
	public Integer getUnifiedReportedPeptideIdForSearchIdAndReportedPeptideId( int reportedPeptideId,  int searchId ) throws Exception {
		
		Integer unifiedReportedPeptideId = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT unified_reported_peptide_id FROM unified_rp__rep_pept__search__generic_lookup WHERE reported_peptide_id = ? AND search_id = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, reportedPeptideId );
			pstmt.setInt( 2, searchId );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				unifiedReportedPeptideId = rs.getInt( "unified_reported_peptide_id" );
			}
			
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
		
		
		return unifiedReportedPeptideId;
	}
	
	
	
}
