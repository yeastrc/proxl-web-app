package org.yeastrc.proxl.import_xml_to_db.post_insert_search_processing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;import org.slf4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO;
import org.yeastrc.proxl.import_xml_to_db.db.ImportDBConnectionFactory;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchDTO_Importer;
import org.yeastrc.xlink.base_searcher.PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;

/**
 * Post Search Insert, update table unified_rp__search__rep_pept__generic_lookup
 *
 */
public class Update_unified_rp__search__rep_pept__generic_lookup_table {

	private static final Logger log = LoggerFactory.getLogger(  Update_unified_rp__search__rep_pept__generic_lookup_table.class );
	/**
	 * private constructor
	 */
	private Update_unified_rp__search__rep_pept__generic_lookup_table(){}
	public static Update_unified_rp__search__rep_pept__generic_lookup_table getInstance() {
		return new Update_unified_rp__search__rep_pept__generic_lookup_table();
	}

	/**
	 * Post Search Insert, update table unified_rp__search__rep_pept__generic_lookup
	 * 
	 * Populate unified_rp__search__rep_pept__generic_lookup.num_unique_psm_at_default_cutoff
	 * since cannot populate while inserting unified_rp__search__rep_pept__generic_lookup records
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @throws Exception 
	 */
	public void update_unified_rp__search__rep_pept__generic_lookup_table( 
			SearchDTO_Importer search,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception {

		if ( ! search.isHasScanData() ) {
			//  No Scan data so unable to compute unique psm count
			if ( log.isInfoEnabled() ) {
				log.info( "Skip populating num_unique_psm_at_default_cutoff since no scans loaded" );
			}
			return;  // EARLY EXIT
		}
		
		int searchId = search.getId();
		
		//  Commit all inserts executed to this point
		ImportDBConnectionFactory.getInstance().commitInsertControlCommitConnection();
		
		List<Integer> reportedPeptideIdList = getReportedPeptidesForRecordsWhereUniquePSMCountIsNULL( searchId );

		for ( Integer reportedPeptideId : reportedPeptideIdList ) {
			int num_unique_psm_at_default_cutoff = 
					PsmCountForUniquePSM_SearchIdReportedPeptideId_Searcher.getInstance()
					.getPsmCountForUniquePSM_SearchIdReportedPeptideId( reportedPeptideId, searchId, searcherCutoffValuesSearchLevel );

			UnifiedRepPep_Search_ReportedPeptide__Generic_Lookup__DAO.getInstance()
			.update_num_unique_psm_at_default_cutoff( searchId, reportedPeptideId, num_unique_psm_at_default_cutoff );
		}
		
	}
	
	private static final String getReportedPeptidesForRecordsWhereUniquePSMCountIsNULLSQL = 
			"SELECT reported_peptide_id FROM unified_rp__search__rep_pept__generic_lookup "
			+ "WHERE search_id = ? AND num_unique_psm_at_default_cutoff IS NULL";

	/**
	 * @param searchId
	 * @return
	 * @throws Exception 
	 */
	private List<Integer> getReportedPeptidesForRecordsWhereUniquePSMCountIsNULL( int searchId ) throws Exception {
		
		List<Integer> resultList = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = getReportedPeptidesForRecordsWhereUniquePSMCountIsNULLSQL;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchId );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				resultList.add( rs.getInt( "reported_peptide_id" ) );
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

		return resultList;
	}
	
	

}
