package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

public class PsmPeptideSearcher {

	private static final Logger log = Logger.getLogger(PsmPeptideSearcher.class);
	
	private PsmPeptideSearcher() { }
	private static final PsmPeptideSearcher _INSTANCE = new PsmPeptideSearcher();
	public static PsmPeptideSearcher getInstance() { return _INSTANCE; }
	


	public List<Integer> getPeptideIdsFromPsmId( int psmId ) throws Exception {
		
		List<Integer> results = new ArrayList<Integer>( );
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT peptide_id FROM psm_peptide WHERE psm_id = " + psmId;
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			rs = pstmt.executeQuery();

			while( rs.next() )
				results.add( rs.getInt( "peptide_id" ) );
			
		} catch ( Exception e ) {
			
			String msg = "getIdListFromStartAndEndIds(), sql: " + sql;
			
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
		
		return results;
	}
	

	
}
