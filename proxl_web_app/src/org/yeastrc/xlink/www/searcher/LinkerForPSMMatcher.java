package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.dao.LinkerDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.www.dto.PsmDTO;

public class LinkerForPSMMatcher {

	private static final Log log = LogFactory.getLog(LinkerForPSMMatcher.class);

	private static final LinkerForPSMMatcher _INSTANCE = new LinkerForPSMMatcher();
	private LinkerForPSMMatcher() { }
	public static LinkerForPSMMatcher getInstance() { return _INSTANCE; }
	
	public LinkerDTO getLinkerForPSM( PsmDTO psm ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT linker_id FROM linker_per_search_crosslink_mass WHERE search_id = ? AND crosslink_mass_double = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, psm.getSearchId() );
			pstmt.setDouble( 2, psm.getLinkerMass().doubleValue() );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {
				return LinkerDAO.getInstance().getLinkerDTOForId( rs.getInt( 1 ) );
			} else {
				throw new Exception( "Could not find a linker for psm: " + psm.getId() );
			}

			
		} catch ( Exception e ) {
			String msg = "getSearchsForProjectId(), sql: " + sql;
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
		
	}
	
}
