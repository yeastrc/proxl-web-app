package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;

/**
 * table reported_peptide
 *
 */
public class ReportedPeptideDAO_Web {
	
	private static final Logger log = Logger.getLogger( ReportedPeptideDAO_Web.class );
	private ReportedPeptideDAO_Web() { }
	public static ReportedPeptideDAO_Web getInstance() { return new ReportedPeptideDAO_Web(); }
	
	/**
	 * @param id
	 * @return - null if not found
	 * @throws Exception
	 */
	public ReportedPeptideDTO getReportedPeptideDTO( int id ) throws Exception {
		ReportedPeptideDTO peptide = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT sequence FROM reported_peptide WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( !rs.next() )
				throw new ProxlBaseDataException( "Could not find reported_peptide for " + id );
			peptide = new ReportedPeptideDTO();
			peptide.setSequence( rs.getString( 1 ) );
			peptide.setId( id );
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
		return peptide;
	}
}
