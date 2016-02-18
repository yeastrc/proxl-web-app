package org.yeastrc.proxl.import_xml_to_db.import_post_processing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Populate search__dynamic_mod_mass_lookup for provided search id
 *
 */
public class SearchDynamicModMassDAO {

	private static final Logger log = Logger.getLogger(SearchDynamicModMassDAO.class);

	private SearchDynamicModMassDAO() { }
	public static SearchDynamicModMassDAO getInstance() { return new SearchDynamicModMassDAO(); }
	
	private static final String SQL =
			"INSERT IGNORE INTO search__dynamic_mod_mass_lookup ( search_id, dynamic_mod_mass, search_id_dynamic_mod_mass_count) " 
			 + " VALUES (?, ?, 0) ";
	
	/**
	 * insert search__dynamic_mod_mass_lookup
	 * @param searchId
	 * @param reportedPeptideId
	 * @throws Exception
	 */
	public void saveSearchDynamicModMass( int searchId, double dynamicModMass ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( SQL );
			
			pstmt.setInt( 1, searchId );
			pstmt.setDouble( 2, dynamicModMass );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + SQL, e );
			
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
		
		
	}

}
