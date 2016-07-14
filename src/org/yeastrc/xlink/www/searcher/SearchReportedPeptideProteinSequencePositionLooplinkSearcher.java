package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosLooplinkDTO;

public class SearchReportedPeptideProteinSequencePositionLooplinkSearcher {

	private static final Logger log = Logger.getLogger( SearchReportedPeptideProteinSequencePositionLooplinkSearcher.class );
	
	private SearchReportedPeptideProteinSequencePositionLooplinkSearcher() { }
	private static final SearchReportedPeptideProteinSequencePositionLooplinkSearcher _INSTANCE = new SearchReportedPeptideProteinSequencePositionLooplinkSearcher();
	public static SearchReportedPeptideProteinSequencePositionLooplinkSearcher getInstance() { return _INSTANCE; }
	
	
	private static final String getSrchRepPeptProtSeqIdPosLooplinkDTOList_SQL = 
			"SELECT * FROM srch_rep_pept__prot_seq_id_pos_looplink WHERE search_id = ? AND reported_peptide_id = ?";

	public List<SrchRepPeptProtSeqIdPosLooplinkDTO> getSrchRepPeptProtSeqIdPosLooplinkDTOList( int searchId, int reportedPeptideId ) throws Exception {
		
		List<SrchRepPeptProtSeqIdPosLooplinkDTO> results = new ArrayList<>();

		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = getSrchRepPeptProtSeqIdPosLooplinkDTOList_SQL;

		
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
		
				SrchRepPeptProtSeqIdPosLooplinkDTO item = new SrchRepPeptProtSeqIdPosLooplinkDTO();
		
				item.setId( rs.getInt( "id" ) );
				item.setSearchId( rs.getInt( "search_id" ) );
				item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				item.setSearchReportedPeptidepeptideId( rs.getInt( "search_reported_peptide_peptide_id" ) );
				item.setProteinSequenceId( rs.getInt( "protein_sequence_id" ) );
				
				item.setProteinSequencePosition_1( rs.getInt( "protein_sequence_position_1" ) );
				item.setProteinSequencePosition_2( rs.getInt( "protein_sequence_position_2" ) );

				results.add( item );
			}

		} catch ( Exception e ) {

			String msg = "Exception in getSrchRepPeptProtSeqIdPosLooplinkDTOList( SearchDTO search, ... ), sql: " + sql;
			
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
