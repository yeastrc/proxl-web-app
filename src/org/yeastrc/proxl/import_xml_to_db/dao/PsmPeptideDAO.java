package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

public class PsmPeptideDAO {
	
	private static Logger log = Logger.getLogger(PsmPeptideDAO.class);

	private PsmPeptideDAO() { }
	public static PsmPeptideDAO getInstance() { return new PsmPeptideDAO(); }
	
	/**
	 * 
	 * @param psmId
	 * @param peptideId
	 * @throws Exception
	 */
	public void saveToDatabase( int psmId, int peptideId ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "INSERT IGNORE INTO psm_peptide( psm_id, peptide_id ) VALUES (?, ?);";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			pstmt.setInt( 2, peptideId );
			
			pstmt.executeUpdate();

		} catch ( Exception e ) {
			
			String msg = "Error: sql: " + sql;
			log.error(msg, e);
			throw e;
			
		} finally {
			
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
