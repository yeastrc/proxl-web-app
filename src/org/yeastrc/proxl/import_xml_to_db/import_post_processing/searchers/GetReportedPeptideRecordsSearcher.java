package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchReportedPeptideDTO;

/**
 * 
 *
 */
public class GetReportedPeptideRecordsSearcher {
	
	private static final Logger log = Logger.getLogger(GetReportedPeptideRecordsSearcher.class);
	
	private GetReportedPeptideRecordsSearcher() { }
	private static final GetReportedPeptideRecordsSearcher _INSTANCE = new GetReportedPeptideRecordsSearcher();
	public static GetReportedPeptideRecordsSearcher getInstance() { return _INSTANCE; }
	
	
	
	/**
	 * @param searchId
	 * @param offset
	 * @param limit
	 * @param startId - optional - null if no value
	 * @param endId - optional - null if no value
	 * @return
	 * @throws Exception
	 */
	public List<SearchReportedPeptideDTO> getSearchReportedPeptideDTOListFromSearchIdReportedPeptideStartAndEndIds( 
			int searchId, int offset, int limit, Integer startId, Integer endId ) throws Exception {
		
		List<SearchReportedPeptideDTO> results = new ArrayList<SearchReportedPeptideDTO>( limit + 1 );
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM search_reported_peptide"

			+ " WHERE search_id = ? ";
		
		if ( startId != null ) {

			sql += " AND reported_peptide_id >= " + startId;
			
			if ( endId != null ) {
				sql += " AND reported_peptide_id <= " + endId;
			}
		}

		sql += " ORDER BY search_id, reported_peptide_id  LIMIT " + offset + ", " + limit;
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, searchId );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				
				SearchReportedPeptideDTO item = new SearchReportedPeptideDTO();
				
				item.setSearchId( rs.getInt( "search_id" ) );
				item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				
				results.add( item );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getSearchReportedPeptideDTOListFromSearchIdReportedPeptideStartAndEndIds(), sql: " + sql;
			
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
