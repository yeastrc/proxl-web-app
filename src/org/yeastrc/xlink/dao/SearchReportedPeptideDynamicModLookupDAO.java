package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * table search__reported_peptide__dynamic_mod_lookup
 *
 */
public class SearchReportedPeptideDynamicModLookupDAO {
	
	private static final Logger log = Logger.getLogger(SearchReportedPeptideDynamicModLookupDAO.class);

	private SearchReportedPeptideDynamicModLookupDAO() { }
	public static SearchReportedPeptideDynamicModLookupDAO getInstance() { return new SearchReportedPeptideDynamicModLookupDAO(); }
	
//	CREATE TABLE search__reported_peptide__dynamic_mod_lookup (
//			  search_id INT UNSIGNED NOT NULL,
//			  reported_peptide_id INT UNSIGNED NOT NULL,
//			  dynamic_mod_mass DOUBLE UNSIGNED NOT NULL,
//			  link_type ENUM('looplink','crosslink','unlinked','monolink','dimer') NOT NULL,
//	  		  best_psm_q_value DOUBLE NOT NULL,
	
	/**
	 *	insert duplicates are ignored
	 *
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabaseIgnoreDuplicates( SearchReportedPeptideDynamicModLookupDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "INSERT IGNORE INTO search__reported_peptide__dynamic_mod_lookup ( search_id, reported_peptide_id, dynamic_mod_mass, link_type, best_psm_q_value ) VALUES (?, ?, ?, ?, ?)";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			String linkTypeString = XLinkUtils.getTypeString( item.getLinkType() );


			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, item.getSearchId() );
			pstmt.setInt( 2, item.getReportedPeptideId() );
			pstmt.setDouble( 3, item.getDynamicModMass() );
			pstmt.setString( 4, linkTypeString );
			pstmt.setDouble( 5, item.getBestPsmQValue() );

			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert SearchReportedPeptideDynamicModDTO: " + item + ".  SQL: " + sql;
			
			log.error( msg );
			
			throw e;
					
		} finally {
			
			// be sure database handles are closed
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
	}
	
}
