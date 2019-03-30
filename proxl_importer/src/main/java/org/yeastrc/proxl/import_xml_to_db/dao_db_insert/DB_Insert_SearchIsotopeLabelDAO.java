package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * table search__isotope_label_lookup
 *
 */
public class DB_Insert_SearchIsotopeLabelDAO {

	private static final Logger log = LoggerFactory.getLogger( DB_Insert_SearchIsotopeLabelDAO.class);

	private DB_Insert_SearchIsotopeLabelDAO() { }
	public static DB_Insert_SearchIsotopeLabelDAO getInstance() { return new DB_Insert_SearchIsotopeLabelDAO(); }
	
	private static final String SQL =
			"INSERT IGNORE INTO search__isotope_label_lookup ( search_id, isotope_label_id ) " 
			 + " VALUES (?, ?) ";
	
	/**
	 * insert search__isotope_label_lookup
	 * @param searchId
	 * @param isotopeLabelId
	 * @throws Exception
	 */
	public void saveSearchIsotopeLabelId( int searchId, int isotopeLabelId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( SQL );
			
			pstmt.setInt( 1, searchId );
			pstmt.setInt( 2, isotopeLabelId );
			
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
