package org.yeastrc.xlink.www.searcher;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
		
		BigDecimal psmLinkerMass = psm.getLinkerMass();
		psmLinkerMass = psmLinkerMass.setScale( 3, RoundingMode.HALF_UP );
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT linker_id, crosslink_mass_double FROM linker_per_search_crosslink_mass WHERE search_id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, psm.getSearchId() );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				
				BigDecimal linkerMass = new BigDecimal( rs.getDouble( 2 ) );
				linkerMass = linkerMass.setScale( 3, RoundingMode.HALF_UP );
								
				if( psmLinkerMass.toString().equals( linkerMass.toString() ) ) {
					return LinkerDAO.getInstance().getLinkerDTOForId( rs.getInt( 1 ) );
				}				
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
		
		throw new Exception( "Could not find linker for psm: " + psm.getId() );
		
	}
	
}
