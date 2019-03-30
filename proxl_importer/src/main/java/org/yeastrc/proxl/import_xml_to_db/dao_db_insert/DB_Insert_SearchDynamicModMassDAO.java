package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * table search__dynamic_mod_mass_lookup
 *
 */
public class DB_Insert_SearchDynamicModMassDAO {

	private static final Logger log = LoggerFactory.getLogger( DB_Insert_SearchDynamicModMassDAO.class);

	private DB_Insert_SearchDynamicModMassDAO() { }
	public static DB_Insert_SearchDynamicModMassDAO getInstance() { return new DB_Insert_SearchDynamicModMassDAO(); }
	
	private static final String SQL =
			"INSERT IGNORE INTO search__dynamic_mod_mass_lookup ( search_id, dynamic_mod_mass) " 
			 + " VALUES (?, ?) ";
	
	/**
	 * insert search__dynamic_mod_mass_lookup
	 * @param searchId
	 * @param dynamicModMass
	 * @throws Exception
	 */
	public void saveSearchDynamicModMass( int searchId, double dynamicModMass ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( SQL );
			
			pstmt.setInt( 1, searchId );
			pstmt.setDouble( 2, dynamicModMass );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + SQL, e );
			
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
