package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.MonolinkDTO;

/**
 * Get data from monolink table
 * 
 *
 */
public class MonolinkSearcher {

	private static final Log log = LogFactory.getLog(MonolinkSearcher.class);
	
	private MonolinkSearcher() { }
	private static final MonolinkSearcher _INSTANCE = new MonolinkSearcher();
	public static MonolinkSearcher getInstance() { return _INSTANCE; }
	
	
	
	
	

	

	/**
	 * Get Monolink For PsmId ProteinId ProteinPosition
	 * 
	 * !!!   The psm and protein properties are not set in the returned objects !!!
	 * 
	 * @param psmId
	 * @param proteinId
	 * @param proteinPosition
	 * @return
	 * @throws Exception
	 */
	public List<MonolinkDTO> getForPsmIdProteinIdProteinPosition( int psmId, int proteinId, int proteinPosition ) throws Exception {
		
		
		List<MonolinkDTO> monolinkList = new ArrayList<MonolinkDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM monolink"

			+ " WHERE psm_id = ? AND  nrseq_id = ? AND protein_position = ? "

			+ " ORDER BY id ";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, psmId );
			
			paramCounter++;
			pstmt.setInt( paramCounter, proteinId );
			
			paramCounter++;
			pstmt.setInt( paramCounter, proteinPosition );
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {

				MonolinkDTO item = new MonolinkDTO();
				
				item.setId( rs.getInt( "id" ) );
				
				item.setPeptideId( rs.getInt( "peptide_id" ) );
				item.setPeptidePosition( rs.getInt( "peptide_position" ) );
				item.setProteinPosition( rs.getInt( "protein_position" ) );
				
				monolinkList.add( item );
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
		
		return monolinkList;
	}
	
	
}
