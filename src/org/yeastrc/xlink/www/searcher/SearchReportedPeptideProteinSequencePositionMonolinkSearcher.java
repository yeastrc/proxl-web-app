package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosMonolinkDTO;

public class SearchReportedPeptideProteinSequencePositionMonolinkSearcher {

	private static final Logger log = Logger.getLogger( SearchReportedPeptideProteinSequencePositionMonolinkSearcher.class );
	
	private SearchReportedPeptideProteinSequencePositionMonolinkSearcher() { }
	private static final SearchReportedPeptideProteinSequencePositionMonolinkSearcher _INSTANCE = new SearchReportedPeptideProteinSequencePositionMonolinkSearcher();
	public static SearchReportedPeptideProteinSequencePositionMonolinkSearcher getInstance() { return _INSTANCE; }
	
	
	private static final String getSrchRepPeptProtSeqIdPosMonolinkDTOList_SQL = 
			"SELECT * FROM srch_rep_pept__prot_seq_id_pos_monolink WHERE search_id = ? AND reported_peptide_id = ?";

	public List<SrchRepPeptProtSeqIdPosMonolinkDTO> getSrchRepPeptProtSeqIdPosMonolinkDTOList( int searchId, int reportedPeptideId ) throws Exception {
		
		List<SrchRepPeptProtSeqIdPosMonolinkDTO> results = new ArrayList<>();

		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = getSrchRepPeptProtSeqIdPosMonolinkDTOList_SQL;

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );

			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			paramCounter++;
			pstmt.setInt( paramCounter, reportedPeptideId );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
		
				SrchRepPeptProtSeqIdPosMonolinkDTO item = new SrchRepPeptProtSeqIdPosMonolinkDTO();
		
				item.setId( rs.getInt( "id" ) );
				item.setSearchId( rs.getInt( "search_id" ) );
				item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				item.setSearchReportedPeptidepeptideId( rs.getInt( "search_reported_peptide_peptide_id" ) );
				item.setProteinSequenceId( rs.getInt( "protein_sequence_id" ) );
				
				item.setProteinSequencePosition( rs.getInt( "protein_sequence_position" ) );

				results.add( item );
			}

		} catch ( Exception e ) {

			String msg = "Exception in getSrchRepPeptProtSeqIdPosMonolinkDTOList( SearchDTO search, ... ), sql: " + sql;
			
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
