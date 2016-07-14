package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosUnlinkedDimerDTO;

public class SearchReportedPeptideProteinSequencePositionUnlinkedDimerSearcher {

	private static final Logger log = Logger.getLogger( SearchReportedPeptideProteinSequencePositionUnlinkedDimerSearcher.class );
	
	private SearchReportedPeptideProteinSequencePositionUnlinkedDimerSearcher() { }
	private static final SearchReportedPeptideProteinSequencePositionUnlinkedDimerSearcher _INSTANCE = new SearchReportedPeptideProteinSequencePositionUnlinkedDimerSearcher();
	public static SearchReportedPeptideProteinSequencePositionUnlinkedDimerSearcher getInstance() { return _INSTANCE; }
	
	
	private static final String getSrchRepPeptProtSeqIdPosUnlinkedDimerDTOList_SQL = 
			"SELECT * FROM srch_rep_pept__prot_seq_id_unlinked_dimer WHERE search_id = ? AND reported_peptide_id = ?";

	public List<SrchRepPeptProtSeqIdPosUnlinkedDimerDTO> getSrchRepPeptProtSeqIdPosUnlinkedDimerDTOList( int searchId, int reportedPeptideId ) throws Exception {
		
		List<SrchRepPeptProtSeqIdPosUnlinkedDimerDTO> results = new ArrayList<>();

		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = getSrchRepPeptProtSeqIdPosUnlinkedDimerDTOList_SQL;

		
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
		
				SrchRepPeptProtSeqIdPosUnlinkedDimerDTO item = new SrchRepPeptProtSeqIdPosUnlinkedDimerDTO();
		
				item.setId( rs.getInt( "id" ) );
				item.setSearchId( rs.getInt( "search_id" ) );
				item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				item.setSearchReportedPeptidepeptideId( rs.getInt( "search_reported_peptide_peptide_id" ) );
				item.setProteinSequenceId( rs.getInt( "protein_sequence_id" ) );
				

				results.add( item );
			}

		} catch ( Exception e ) {

			String msg = "Exception in getSrchRepPeptProtSeqIdPosUnlinkedDimerDTOList( SearchDTO search, ... ), sql: " + sql;
			
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
