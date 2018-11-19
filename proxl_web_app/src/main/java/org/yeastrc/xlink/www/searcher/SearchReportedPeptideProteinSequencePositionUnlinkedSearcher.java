package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.SrchRepPeptProtSeqIdPosUnlinkedDTO;
/**
 * 
 *  table srch_rep_pept__prot_seq_id_unlinked
 *  WHERE search_id = ? AND reported_peptide_id = ?
 */
public class SearchReportedPeptideProteinSequencePositionUnlinkedSearcher {

	private static final Logger log = Logger.getLogger( SearchReportedPeptideProteinSequencePositionUnlinkedSearcher.class );
	private SearchReportedPeptideProteinSequencePositionUnlinkedSearcher() { }
	private static final SearchReportedPeptideProteinSequencePositionUnlinkedSearcher _INSTANCE = new SearchReportedPeptideProteinSequencePositionUnlinkedSearcher();
	public static SearchReportedPeptideProteinSequencePositionUnlinkedSearcher getInstance() { return _INSTANCE; }
	
	private static final String getSrchRepPeptProtSeqIdPosUnlinkedDTOList_SQL = 
			"SELECT * FROM srch_rep_pept__prot_seq_id_unlinked WHERE search_id = ? AND reported_peptide_id = ?";
	/**
	 * @param searchId
	 * @param reportedPeptideId
	 * @return
	 * @throws Exception
	 */
	public List<SrchRepPeptProtSeqIdPosUnlinkedDTO> getSrchRepPeptProtSeqIdPosUnlinkedDTOList( int searchId, int reportedPeptideId ) throws Exception {
		List<SrchRepPeptProtSeqIdPosUnlinkedDTO> results = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = getSrchRepPeptProtSeqIdPosUnlinkedDTOList_SQL;
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
				SrchRepPeptProtSeqIdPosUnlinkedDTO item = new SrchRepPeptProtSeqIdPosUnlinkedDTO();
				item.setId( rs.getInt( "id" ) );
				item.setSearchId( rs.getInt( "search_id" ) );
				item.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				item.setSearchReportedPeptidepeptideId( rs.getInt( "search_reported_peptide_peptide_id" ) );
				item.setProteinSequenceVersionId( rs.getInt( "protein_sequence_version_id" ) );
				results.add( item );
			}
		} catch ( Exception e ) {
			String msg = "Exception in getSrchRepPeptProtSeqIdPosUnlinkedDTOList( SearchDTO search, ... ), sql: " + sql;
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
