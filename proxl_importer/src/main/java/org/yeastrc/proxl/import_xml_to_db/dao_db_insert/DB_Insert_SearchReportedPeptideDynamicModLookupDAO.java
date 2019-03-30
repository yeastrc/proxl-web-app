package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * table search__reported_peptide__dynamic_mod_lookup
 *
 */
public class DB_Insert_SearchReportedPeptideDynamicModLookupDAO {

	private static final Logger log = LoggerFactory.getLogger( DB_Insert_SearchReportedPeptideDynamicModLookupDAO.class);

	private DB_Insert_SearchReportedPeptideDynamicModLookupDAO() { }
	public static DB_Insert_SearchReportedPeptideDynamicModLookupDAO getInstance() { return new DB_Insert_SearchReportedPeptideDynamicModLookupDAO(); }


	private final String INSERT_SQL = 
			"INSERT IGNORE INTO search__reported_peptide__dynamic_mod_lookup"
			+ " ( search_id, reported_peptide_id, dynamic_mod_mass, link_type ) VALUES (?, ?, ?, ?)";
	
	public void saveToDatabaseIgnoreDuplicates( SearchReportedPeptideDynamicModLookupDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		final String sql = INSERT_SQL;

		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );

			String linkTypeString = XLinkUtils.getTypeString( item.getLinkType() );


			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, item.getSearchId() );
			pstmt.setInt( 2, item.getReportedPeptideId() );
			pstmt.setDouble( 3, item.getDynamicModMass() );
			pstmt.setString( 4, linkTypeString );
			pstmt.executeUpdate();
			
			
		} catch ( Exception e ) {
			
			String msg = "ERROR inserting search__reported_peptide__dynamic_mod_lookup. database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n object to insert: " + item
					+ "\nsql: " + sql;
			log.error( msg, e );
			
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
