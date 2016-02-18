package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class GetHasDynamicModificationsForPsmIdSearcher {
	
	private static final Logger log = Logger.getLogger(GetHasDynamicModificationsForPsmIdSearcher.class);
	
	private GetHasDynamicModificationsForPsmIdSearcher() { }
	private static final GetHasDynamicModificationsForPsmIdSearcher _INSTANCE = new GetHasDynamicModificationsForPsmIdSearcher();
	public static GetHasDynamicModificationsForPsmIdSearcher getInstance() { return _INSTANCE; }
	
	
	
	/**
	 * @param searchId
	 * @param reportedPeptideId
	 * @return true if any dynamic_mod records found for psmId 
	 * @throws Exception
	 */
	public boolean getHasDynamicModificationsForPsmId( int psmId  ) throws Exception {
		
		boolean result = false;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT psm.id FROM psm "
			+ " INNER JOIN matched_peptide ON psm.id = matched_peptide.psm_id "
			+ " INNER JOIN dynamic_mod ON matched_peptide.id = dynamic_mod.matched_peptide_id "

			+ " WHERE psm.id = ?  LIMIT 1";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {

				result = true;
			}
			
		} catch ( Exception e ) {
			
			String msg = "getHasDynamicModificationsForPsmId(), sql: " + sql;
			
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
