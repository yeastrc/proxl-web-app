package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table psm_per_peptide_annotation_large_value
 *
 */
public class PsmPerPeptideAnnotationLargeValueDAO {
	
	private static final Logger log = Logger.getLogger(PsmPerPeptideAnnotationLargeValueDAO.class);

	private PsmPerPeptideAnnotationLargeValueDAO() { }
	public static PsmPerPeptideAnnotationLargeValueDAO getInstance() { return new PsmPerPeptideAnnotationLargeValueDAO(); }
	
	/**
	 * Get the given psm_per_peptide_annotation_large_value String from the database
	 * 
	 * @param psmAnnotationId
	 * @return
	 * @throws Exception
	 */
	public String getValueString( int psmPerPeptideAnnotationId ) throws Exception {
		
		String valueString = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT value_string FROM psm_per_peptide_annotation_large_value WHERE psm_per_peptide_annotation_id = ?";
		

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmPerPeptideAnnotationId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				valueString = rs.getString( "value_string" );
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
		
		
		return valueString;
	}
	
	

	
}
