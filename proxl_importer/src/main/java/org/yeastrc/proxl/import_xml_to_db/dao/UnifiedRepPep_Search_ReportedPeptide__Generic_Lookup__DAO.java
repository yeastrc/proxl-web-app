package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * table unified_rp__search__rep_pept__generic_lookup
 *
 */
public class UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO {

	private static final Logger log = Logger.getLogger(UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO.class);
	private UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO() { }
	public static UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO getInstance() { return new UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO(); }
	

	/**
	 * Update the num_unique_psm_at_default_cutoff
	 * @param searchId
	 * @param reportedPeptideId
	 * @param num_unique_psm_at_default_cutoff
	 * @throws Exception
	 */
	public void update_num_unique_psm_at_default_cutoff( int searchId, int reportedPeptideId, int num_unique_psm_at_default_cutoff ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			update_num_unique_psm_at_default_cutoff( searchId, reportedPeptideId, num_unique_psm_at_default_cutoff, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	private static final String update_num_unique_psm_at_default_cutoffsql = 
			"UPDATE unified_rp__search__rep_pept__generic_lookup "
			+ " SET num_unique_psm_at_default_cutoff = ? WHERE search_id = ? AND reported_peptide_id = ?";

	/**
	 * Update the num_unique_psm_at_default_cutoff
	 * @param searchId
	 * @param reportedPeptideId
	 * @param num_unique_psm_at_default_cutoff
	 * @throws Exception
	 */	public void update_num_unique_psm_at_default_cutoff( int searchId, int reportedPeptideId, int num_unique_psm_at_default_cutoff, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = update_num_unique_psm_at_default_cutoffsql;
		try {
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, num_unique_psm_at_default_cutoff );
			pstmt.setInt( 2, searchId );
			pstmt.setInt( 3, reportedPeptideId );
			pstmt.executeUpdate();
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
		}
	}
}
